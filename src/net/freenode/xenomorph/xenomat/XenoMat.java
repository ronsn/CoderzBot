package net.freenode.xenomorph.xenomat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jibble.pircbot.PircBot;

public class XenoMat extends PircBot {

    /**
     * Timer grammarTimer Check every second if there are users which couldn't
     * answer their grammar question in the given time. If there are such users,
     * kickban them.
     */
    private Timer grammarTimer;
    /**
     * Timer beerTimer Check every second if there are users who are prohibited
     * to order beer and waited for longer than 5 Minutes. If so, delete them
     * from List so they can order beer again.
     */
    private Timer beerTimer;
    /**
     * Timer banTimer Check every second if there are Users who are banned and
     * if their bantime has depleted, unban them
     */
    private Timer banTimer;
    /**
     * Timer muteTimer Check every second if a channel has to be unmuted
     */
    private Timer muteTimer;
    // Needed to pull random sentences off the sentence list
    private Random randomGenerator;
    /**
     * Contains a List of users who didn't answer their question yet. It's a
     * ConcurrentHashMap because there are two threads using it: The timer as
     * well as the PircBot events.
     */
    private ConcurrentHashMap<String, User> pendingGrammarUsers;
    // Hold a list of channels where the bot was oped/deoped
    // TODO: Integrate into this.channels
    private ConcurrentHashMap<String, Boolean> opRights;
    // Similar to pendingGrammarUsers, but for active bans
    private ConcurrentHashMap<String, Ban> pendingBans;
    // Similar to pendingGrammarUsers, but for drinkers
    private ConcurrentHashMap<String, User> pendingBeerUsers;
    // Current channel modes
    private ConcurrentHashMap<String, Channel> channels;
    // Holds the grammar questions
    private ArrayList<CheckSentence> sentences;
    // Users from this list are never asked grammar questions
    private ArrayList<String> grammarWhitelist;
    // Some config-vars
    private String opPass;
    private Integer banTime;
    private Integer answerTime;
    private String sentenceFile = "sentences.txt";
    private String grammarWhitelistFile = "whitelist.txt";
    private boolean useGrammarFloodLimit;
    private Integer grammarFloodTime;
    private Integer grammarFloodLimit;

    public enum txtFileType {

        SENTENCES, WHITELIST
    }

    public XenoMat(String botNick, String oPass, Integer bTime, Integer aTime, boolean uGrammarFloodLimit, Integer gFloodTime, Integer gFloodLimit) {
        // Security check to prevent the public from being able to control the bot
        if (oPass == null || oPass.isEmpty() || oPass.equals(botNick)) {
            System.out.println("OpPass must be set and must not be the BotNick!");
            System.exit(0);
        }

        // set some variables, mostly config
        opPass = oPass;
        try {
            setName(botNick);
            answerTime = aTime;
            banTime = bTime;
            //initialize maps
            useGrammarFloodLimit = uGrammarFloodLimit;
            grammarFloodTime = gFloodTime;
            grammarFloodLimit = gFloodLimit;
            pendingGrammarUsers = new ConcurrentHashMap<>();
            pendingBeerUsers = new ConcurrentHashMap<>();
            pendingBans = new ConcurrentHashMap<>();
            opRights = new ConcurrentHashMap<>();
            channels = new ConcurrentHashMap<>();

            // fill the List of sentences
            sentences = fileToArrayList(sentenceFile, txtFileType.SENTENCES);

            // get the whiteList
            grammarWhitelist = fileToArrayList(grammarWhitelistFile, txtFileType.WHITELIST);

            // initialize Random generator
            randomGenerator = new Random();

            // fire up all timers.
            startTimers();

        } catch (Exception ex) {
            Logger.getLogger(XenoMat.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }

    private ArrayList fileToArrayList(String fileName, txtFileType type) {
        ArrayList returnList = new ArrayList<>();
        try {
            File f = new File(fileName);
            if (!f.exists()) {
                System.out.println("File " + f.getCanonicalPath() + " doesn't exist.");
                System.exit(0);
            }
            // not needed anymore
            f = null;
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            String stringRead = br.readLine();

            while (stringRead != null) {
                String line = stringRead.trim();
                if (!line.startsWith("#") && !line.isEmpty()) { // ignore comments and empty lines
                    if (type.equals(txtFileType.SENTENCES)) {
                        String[] sentenceParts = line.split("\\s*--\\s*");
                        if (sentenceParts.length != 2) {
                            throw new Exception("Error in " + f.getCanonicalPath() + " - Line " + line);
                        } else {

                            returnList.add(new CheckSentence(sentenceParts[0], sentenceParts[1]));
                        }
                    } else if (type.equals(txtFileType.WHITELIST)) {
                        returnList.add(line.trim());
                    }
                }
                // read the next line
                stringRead = br.readLine();
            }
            br.close();
        } catch (Exception ex) {
            Logger.getLogger(XenoMat.class.getName()).log(Level.SEVERE, null, ex);
        }
        return returnList;
    }

    @Override
    /**
     * This method is called whenever someone (possibly us) changes nick on any
     * of the channels that we are on.
     * <p>
     * It's used to update all Lists if someone does a nick change. This way, a
     * nick change won't get someone a new beer, prevent a kick etc.
     *
     * @param oldNick The old nick.
     * @param login The login of the user.
     * @param hostname The hostname of the user.
     * @param newNick The new nick.
     */
    public void onNickChange(String oldNick, String login, String hostname, String newNick) {
        String oldKey = oldNick + login + hostname;
        if (pendingGrammarUsers.containsKey(oldKey)) {
            User u = pendingGrammarUsers.get(oldKey);
            pendingGrammarUsers.remove(oldKey);
            String newKey = newNick + login + hostname;
            u.setNick(newNick);
            pendingGrammarUsers.put(newKey, u);
        }
        if (pendingBeerUsers.containsKey(oldKey)) {
            User u = pendingBeerUsers.get(oldKey);
            pendingBeerUsers.remove(oldKey);
            String newKey = newNick + login + hostname;
            u.setNick(newNick);
            pendingBeerUsers.put(newKey, u);
        }
    }

    @Override
    /**
     * This method is called whenever a private message is sent to the PircBot.
     * <p>
     * It's main purpose is to react to commands starting with "!". opPass is
     * used to ensure the user is some sort of bot-admin. TODO: Authentification
     * should be done using user accounts and a db.
     * <p>
     * If there's no !command we assume it's an answer to a grammar check.
     *
     * @param sender The nick of the person who sent the private message.
     * @param login The login of the person who sent the private message.
     * @param hostname The hostname of the person who sent the private message.
     * @param message The actual message.
     */
    public void onPrivateMessage(String sender, String login, String hostname, String message) {
        if (message.equals("!quit " + opPass)) {
            /**
             * !quit
             *
             * Leave the server and end program
             */
            quitServer("Shutting down " + getNick() + "...");
            System.exit(0);
        } else if (message.equals("!rehash " + opPass + " sentences")) {
            /**
             * !rehash opPass sentences
             *
             * Re-Read sentence.txt
             */
            sentences = null;
            sentences = fileToArrayList(sentenceFile, txtFileType.SENTENCES);
            sendMessage(sender, "Rehash complete. List contains " + sentenceFile.length() + " entries.");
        } else if (message.equals("!rehash " + opPass + " whitelist")) {
            /**
             * !rehash opPass whitelist
             *
             * Re-Read whitelist.txt
             */
            grammarWhitelist = null;
            grammarWhitelist = fileToArrayList(grammarWhitelistFile, txtFileType.WHITELIST);
            sendMessage(sender, "Rehash complete. List is now:");
            for (String entry : grammarWhitelist) {
                sendMessage(sender, entry);
            }
            sendMessage(sender, "(End of List)");
        } else if (message.startsWith("!join")) {
            /**
             * !join opPass [#channel,#channel,...]
             *
             * Join all given channels. Needs the Hashtag before channel names
             */
            String[] command = message.split("\\s");
            if (command.length < 3 || !command[1].equals(opPass)) {
                sendMessage(sender, "Wrong Syntax! Usage: !join BotPass #channel [#channel2 ...]");
            } else {
                for (int i = 0; i < command.length; i++) {
                    if (i > 1 && command[i].startsWith("#")) {
                        joinChannel(command[i]);
                    }
                }
            }
        } else if (message.startsWith("!part")) {
            /**
             * !part opPass [#channel,#channel,...]
             *
             * Leave all given channels. Needs the Hashtag before channel names
             */
            String[] command = message.split("\\s");
            if (command.length < 3 || !command[1].equals(opPass)) {
                sendMessage(sender, "Wrong Syntax! Usage: !part BotPass #channel [#channel2 ...]");
            } else {
                for (int i = 0; i < command.length; i++) {
                    if (i > 1 && command[i].startsWith("#")) {
                        partChannel(command[i]);
                    }
                }
            }
        } else {
            /**
             * Since there was no command given, it is probably someone
             * answering a grammar question.
             */
            String key = sender + login + hostname;
            User grammarUser = pendingGrammarUsers.get(key);
            // check if the user is known as pending grammar user
            if (grammarUser != null) {
                /**
                 * The trim() is used to simplify the process for the user.
                 * There is no need to do this, except lowering the task
                 * complexity of the grammar challenge.
                 */
                if (message.trim().equals(grammarUser.getCheckSentence().getCorrectSentence())) {
                    pendingGrammarUsers.remove(key);
                    sendMessage(sender, "Du bist authentifiziert, Danke.");
                    voice(grammarUser.getChannel(), grammarUser.getNick());
                } else {
                    long millis = (answerTime * 60 * 1000 - (System.currentTimeMillis() - grammarUser.getPendingSince()));
                    String tRemaining = String.format("%d Minuten, %d Sekunden",
                            TimeUnit.MILLISECONDS.toMinutes(millis),
                            TimeUnit.MILLISECONDS.toSeconds(millis)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                    sendMessage(sender, "Das war falsch. Du hast noch " + tRemaining + " Zeit für die korrekte Antwort.");
                    grammarUser.setWarned(grammarUser.getWarned() + 1);
                    pendingGrammarUsers.put(key, grammarUser);
                }
            }
        }
    }

    @Override
    /**
     * This method is called whenever a message is sent to a channel.
     * <p>
     * Some !commands can be issued by normal users inside a channel
     *
     * @param channel The channel to which the message was sent.
     * @param sender The nick of the person who sent the message.
     * @param login The login of the person who sent the message.
     * @param hostname The hostname of the person who sent the message.
     * @param message The actual message sent to the channel.
     */
    public void onMessage(String channel, String sender, String login, String hostname, String message) {

        if (!sender.equalsIgnoreCase("chanserv") && !sender.equalsIgnoreCase("nickserv") && !sender.equalsIgnoreCase(getNick())) { // ignore services & self
            String key = sender + login + hostname;

            // Check if it's a pending grammar user and if we should mute the channel
            if (useGrammarFloodLimit && pendingGrammarUsers.containsKey(key) && channels.containsKey(channel) && !channels.get(channel).getChannelMuted()) {
                if (pendingGrammarUsers.get(key).getGrammarFloodLimitCount() >= grammarFloodLimit) {
                    sendMessage(channel, "Der Channel wird ab jetzt für " + grammarFloodTime + " Minuten auf moderiert gesetzt, da " + sender + " im Channel schreibt, statt die Grammatikfrage zu beantworten.");
                    setMode(channel, "+m");
                    Channel chan = new Channel(channel, true);
                    channels.put(channel, chan);
                    pendingGrammarUsers.get(key).setGrammarFloodLimitCount(0);
                } else {
                    pendingGrammarUsers.get(key).setGrammarFloodLimitCount(pendingGrammarUsers.get(key).getGrammarFloodLimitCount() + 1);
                }
            }

            if (message.equalsIgnoreCase("!time")) {
                /**
                 * !time
                 *
                 * Sends the current time into channel. -> Time of the bot host,
                 * not the time of the user!
                 */
                String time = new java.util.Date().toString();
                sendMessage(channel, sender + ": The time is now " + time);
            } else if (message.equalsIgnoreCase("!beer")) {
                /**
                 * !beer
                 *
                 * Give a virtual beer to the sender. Except he already had one
                 * in the past 5 minutes.
                 */
                User beerUser = pendingBeerUsers.get(key);
                if (beerUser != null && beerUser.getWarned() == 0) {
                    long millis = (5 * 60 * 1000 - (System.currentTimeMillis() - beerUser.getPendingSince()));
                    String tRemaining = String.format("%d Minuten, %d Sekunden",
                            TimeUnit.MILLISECONDS.toMinutes(millis),
                            TimeUnit.MILLISECONDS.toSeconds(millis)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                    sendMessage(channel, "Du hattest in den letzten fünf Minuten schon ein Bier, " + sender + ". Frag in " + tRemaining + " wieder.");
                    beerUser.setWarned(beerUser.getWarned() + 1);
                    pendingBeerUsers.put(key, beerUser);
                } else if (beerUser == null) {
                    pendingBeerUsers.put(key, new User(sender, login, hostname, channel, System.currentTimeMillis()));
                    sendAction(channel, "schiebt " + sender + " ein kühles Bier rüber.");
                }
            }

        }
    }

    @Override
    /**
     * Called when a user (possibly us) gets operator status taken away.
     * <p>
     * This is a type of mode change and is also passed to the onMode method in
     * the PircBot class.
     * <p>
     * Used only to be self-aware of the bot op-status. The grammar questions
     * will only be issued if the bot is op.
     *
     * @since PircBot 0.9.5
     *
     * @param channel The channel in which the mode change took place.
     * @param sourceNick The nick of the user that performed the mode change.
     * @param sourceLogin The login of the user that performed the mode change.
     * @param sourceHostname The hostname of the user that performed the mode
     * change.
     * @param recipient The nick of the user that got 'deopped'.
     */
    public void onDeop(String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient) {
        if (recipient.equals(getNick())) {
            opRights.put(channel, Boolean.FALSE);
        }
    }

    @Override
    /**
     * Called when a user (possibly us) gets granted operator status for a
     * channel.
     * <p>
     * This is a type of mode change and is also passed to the onMode method in
     * the PircBot class.
     * <p>
     * Only used to be self-aware of the bot op-status. The grammar questions
     * will only be issued if the bot is op.
     *
     * @since PircBot 0.9.5
     *
     * @param channel The channel in which the mode change took place.
     * @param sourceNick The nick of the user that performed the mode change.
     * @param sourceLogin The login of the user that performed the mode change.
     * @param sourceHostname The hostname of the user that performed the mode
     * change.
     * @param recipient The nick of the user that got 'opped'.
     */
    public void onOp(String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient) {
        if (recipient.equals(getNick())) {
            opRights.put(channel, Boolean.TRUE);
        }
    }

    @Override
    /**
     * This method is called whenever someone (possibly us) joins a channel
     * which we are on.
     * <p>
     * It's purpose if to challenge all new users with a grammar question. TODO:
     * The whitelist is static atm. Also, ops shouldn't be questioned.
     *
     * @param channel The channel which somebody joined.
     * @param sender The nick of the user who joined the channel.
     * @param login The login of the user who joined the channel.
     * @param hostname The hostname of the user who joined the channel.
     */
    public void onJoin(String channel, String sender, String login, String hostname) {
        if (!sentences.isEmpty() && opRights.get(channel) != null && opRights.get(channel) == true && !sender.equalsIgnoreCase(getNick()) && !grammarWhitelist.contains(sender)) {
//        if (sender.equalsIgnoreCase("xenomorph")) {
            CheckSentence s = randomSentence();
            User u = new User(sender, login, hostname, channel, System.currentTimeMillis());
            u.setCheckSentence(s);
            pendingGrammarUsers.put(sender + login + hostname, u);
            sendMessage(sender, "Hallo. Um Spam zu vermeiden schreibe bitte den folgenden Satz ab, korrigiere dabei den enthaltenen Fehler.");
            sendMessage(sender, "Wenn Du nicht innerhalb von " + String.valueOf(answerTime) + " Minuten mit dem korrekten Satz antwortest, muss ich Dich leider kicken.");
            sendMessage(sender, "Ausserdem bekommst Du dann einen Bann von " + String.valueOf(banTime) + " Minuten.");
            sendMessage(sender, "Bitte antworte mit dem kompletten, korrigierten Satz.");
            sendMessage(sender, "Der zu korrigierende Satz lautet:");
            sendMessage(sender, s.getWrongSentence());
        } else if (grammarWhitelist.contains(sender) && opRights.get(channel) && !sender.equalsIgnoreCase(getNick())) {
            setMode(channel, "+v " + sender);
        } else if (sender.equalsIgnoreCase(getNick())) {
            // TODO: Assuming channel is not muted on join. Should be checked!
            channels.put(channel, new Channel(channel, false));
        }
    }

    /**
     * This method is getting a random sentence from the sentences, used to
     * challenge a user with a grammar question.
     *
     * @return random sentence from sentence list.
     */
    private CheckSentence randomSentence() {
        int index = randomGenerator.nextInt(sentences.size());
        return sentences.get(index);
    }

    /**
     * This method is called once when the bot starts. It sets and starts all
     * TimerTasks needed.
     */
    private void startTimers() {
        grammarTimer = new Timer();
        grammarTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                for (String key : pendingGrammarUsers.keySet()) {
                    if (System.currentTimeMillis() - pendingGrammarUsers.get(key).getPendingSince() >= answerTime * 60 * 1000) {
                        if (opRights.get(pendingGrammarUsers.get(key).getChannel()) == true) {
                            sendMessage(pendingGrammarUsers.get(key).getNick(), "<<Du hast den Anti-Spammer Test leider nicht bestanden.>>");
                            sendMessage(pendingGrammarUsers.get(key).getNick(), "Du wirst für " + banTime + " Minuten vom Channel " + pendingGrammarUsers.get(key).getChannel() + " ausgeschlossen.");
                            String banmask = "*!*@" + pendingGrammarUsers.get(key).getHost();
                            pendingBans.put(banmask, new Ban(banmask, pendingGrammarUsers.get(key).getChannel(), banTime * 60 * 1000, System.currentTimeMillis()));
                            ban(pendingGrammarUsers.get(key).getChannel(), banmask);
                            kick(pendingGrammarUsers.get(key).getChannel(), pendingGrammarUsers.get(key).getNick());
                        }
                        pendingGrammarUsers.remove(key);
                    }
                }
            }
        }, 0, 1000);
        beerTimer = new Timer();
        beerTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                for (String key : pendingBeerUsers.keySet()) {
                    if (System.currentTimeMillis() - pendingBeerUsers.get(key).getPendingSince() >= 5 * 60 * 1000) {
                        pendingBeerUsers.remove(key);
                    }
                }
            }
        }, 0, 1000);
        banTimer = new Timer();
        banTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                for (String key : pendingBans.keySet()) {
                    if (System.currentTimeMillis() - pendingBans.get(key).getTimeOfBan() >= pendingBans.get(key).getBanDuration()) {
                        if (opRights.get(pendingBans.get(key).getChannel()) == true) {
                            unBan(pendingBans.get(key).getChannel(), pendingBans.get(key).getBanEntry());
                            pendingBans.remove(key);
                        }
                    }
                }
            }
        }, 0, 1000);
        muteTimer = new Timer();
        muteTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                for (String key : channels.keySet()) {
                    if (channels.get(key).getMutedSince() > 0 && System.currentTimeMillis() - channels.get(key).getMutedSince() >= grammarFloodTime * 60 * 1000) {
                        if (opRights.containsKey(key) && opRights.get(key) == true) {
                            System.out.println("----------------DOOOOOOOOOOOOOO it.");
                            setMode(key, "-m");
                            channels.remove(key);
                        }
                    }
                }
            }
        }, 0, 1000);
    }
}
