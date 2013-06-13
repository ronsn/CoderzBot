package net.freenode.xenomorph.xenomat.Listeners;

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
import net.freenode.xenomorph.xenomat.Ban;
import net.freenode.xenomorph.xenomat.XenoMatChannel;
import net.freenode.xenomorph.xenomat.CheckSentence;
import net.freenode.xenomorph.xenomat.FileTypes.txtFileType;
import net.freenode.xenomorph.xenomat.LevenshteinDistance;
import net.freenode.xenomorph.xenomat.XenoMatUser;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

/**
 * The GrammarListener waits for new users to join a channel. Every time it
 * detects a new user it checks if he is on the whithelist. If not, the user
 * will be confronted with a sentence containing errors. The user then has to
 * reply with the corrected sentence. If he fails to do so within a configured
 * time, he will be banned for a configured time. If he answers correct, he will
 * be voiced.
 */
public class GrammarListener extends ListenerAdapter {

    /**
     * Timer grammarTimer Check every second if there are users which couldn't
     * answer their grammar question in the given time. If there are such users,
     * kickban them.
     */
    private Timer grammarTimer;
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
    private ConcurrentHashMap<String, XenoMatUser> pendingGrammarUsers;
    // Similar to pendingGrammarUsers, but for active bans
    private ConcurrentHashMap<String, Ban> pendingBans;
    // Similar to pendingGrammarUsers, but for drinkers
    // Holds the grammar questions
    private ArrayList<CheckSentence> sentences;
    // Users from this list are never asked grammar questions
    private ArrayList<String> grammarWhitelist;
    // Current channel modes
    private ConcurrentHashMap<String, XenoMatChannel> channels;
    // Some more config values, should be self explanatory
    private Integer _banTime;
    private Integer _answerTime;
    boolean _useGrammarFloodLimit;
    String sentenceFile = "sentences.txt";
    String grammarWhitelistFile = "whitelist.txt";
    String botNickFromConfig;
    Integer _grammarFloodLimit;
    Integer _grammarFloodTime;
    private boolean _grammarCheckActive;

    /**
     *
     * @param botNickFromCfg The nick the bot is configured to use
     * @param answerTime The time a user has to reply with the correct sentence
     * @param banTime The time a user will be banned if he fails the grammar
     * test
     * @param useGrammarFloodLimit If true, a user who hasn't answered the
     * grammar question may only write a configured amount of messages in the
     * channel before the channel gets moderated
     * @param grammarFloodLimit How many messages a user who hasn't answered the
     * grammar question is allowed to write before the channel is muted
     * @param grammarFloodTime The time a channel will be set moderated if a
     * user writes in the channel instead of answering his grammar question
     */
    public GrammarListener(String botNickFromCfg, Integer answerTime, Integer banTime, Boolean useGrammarFloodLimit, Integer grammarFloodLimit, Integer grammarFloodTime, boolean grammarCheckActive) {
        _grammarFloodTime = grammarFloodTime;
        _grammarFloodLimit = grammarFloodLimit;
        _useGrammarFloodLimit = useGrammarFloodLimit;
        _banTime = banTime;
        _answerTime = answerTime;
        pendingGrammarUsers = new ConcurrentHashMap<>();
        pendingBans = new ConcurrentHashMap<>();
        channels = new ConcurrentHashMap<>();
        sentences = fileToArrayList(sentenceFile, txtFileType.SENTENCES);
        grammarWhitelist = fileToArrayList(grammarWhitelistFile, txtFileType.WHITELIST);
        // initialize Random generator
        randomGenerator = new Random();
        botNickFromConfig = botNickFromCfg;
        _grammarCheckActive = grammarCheckActive;
        startTimers();
    }

    @Override
    public void onJoin(JoinEvent event) {
        if (_grammarCheckActive && !sentences.isEmpty() && event.getChannel().isOp(event.getBot().getUserBot()) && !event.getUser().getNick().equals(event.getBot().getNick()) && !grammarWhitelist.contains(event.getUser().getNick())) {
            CheckSentence s = randomSentence();
            XenoMatUser u = new XenoMatUser(event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(), event.getChannel().getName(), System.currentTimeMillis(), event);
            u.setCheckSentence(s);
            pendingGrammarUsers.put(event.getUser().getNick() + event.getUser().getLogin() + event.getUser().getHostmask(), u);
            Integer errorCount = LevenshteinDistance.computeDistance(s.getCorrectSentence(), s.getWrongSentence());
            if (errorCount == 1) {
                event.getBot().sendIRC().message(u.getUser().getNick(), "Hallo. Um Spam zu vermeiden schreibe bitte den folgenden Satz ab, korrigiere dabei den enthaltenen Fehler.");
            } else {
                event.getBot().sendIRC().message(u.getUser().getNick(), "Hallo. Um Spam zu vermeiden schreibe bitte den folgenden Satz ab, korrigiere dabei die " + String.valueOf(errorCount) + " enthaltenen Fehler.");
            }
            event.getBot().sendIRC().message(u.getUser().getNick(), "Wenn Du nicht innerhalb von " + String.valueOf(_answerTime) + " Minuten mit dem korrekten Satz antwortest, muss ich Dich leider kicken.");
            event.getBot().sendIRC().message(u.getUser().getNick(), "Ausserdem bekommst Du dann einen Bann von " + String.valueOf(_banTime) + " Minuten.");
            event.getBot().sendIRC().message(u.getUser().getNick(), "Bitte antworte mit dem kompletten, korrigierten Satz.");
            event.getBot().sendIRC().message(u.getUser().getNick(), "Der zu korrigierende Satz lautet:");
            event.getBot().sendIRC().message(u.getUser().getNick(), s.getWrongSentence());
        } else if (grammarWhitelist.contains(event.getUser().getNick()) && event.getChannel().isOp(event.getBot().getUserBot()) && !event.getUser().getNick().equals(event.getBot().getNick())) {
            event.getChannel().send().voice(event.getUser());
        } else if (event.getBot().getNick().equals(event.getUser().getNick())) {
            channels.put(event.getChannel().getName(), new XenoMatChannel(event.getChannel().getName(), false, event.getChannel()));
        }
    }

    public void onMessage(MessageEvent event) throws Exception {
        String key = event.getUser().getNick() + event.getUser().getLogin() + event.getUser().getHostmask();
        if (_useGrammarFloodLimit && pendingGrammarUsers.containsKey(key)) {
            if (channels.containsKey(event.getChannel().getName())) {
                if (!channels.get(event.getChannel().getName()).getChannelMuted()) {
                    if (pendingGrammarUsers.get(key).getGrammarFloodLimitCount() >= _grammarFloodLimit) {
                        event.getBot().sendIRC().message(event.getChannel().getName(), "Der Channel wird ab jetzt für " + _grammarFloodTime + " Minuten auf moderiert gesetzt, da " + event.getUser().getNick() + " im Channel schreibt, statt die Grammatikfrage zu beantworten.");
                        event.getChannel().send().setModerated(event.getChannel());
                        XenoMatChannel chan = new XenoMatChannel(event.getChannel().getName(), true, event.getChannel());
                        channels.put(event.getChannel().getName(), chan);
                        pendingGrammarUsers.get(key).setGrammarFloodLimitCount(0);
                    } else {
                        pendingGrammarUsers.get(key).setGrammarFloodLimitCount(pendingGrammarUsers.get(key).getGrammarFloodLimitCount() + 1);

                    }
                }
            }
        }
    }

    @Override
    public void onPrivateMessage(PrivateMessageEvent event) {
        /**
         * Since there was no command given, it is probably someone answering a
         * grammar question.
         */
        String key = event.getUser().getNick() + event.getUser().getLogin() + event.getUser().getHostmask();
        XenoMatUser grammarUser = pendingGrammarUsers.get(key);
        // check if the user is known as pending grammar user
        if (grammarUser != null) {
            /**
             * The trim() is used to simplify the process for the user. There is
             * no need to do this, except lowering the task complexity of the
             * grammar challenge.
             */
            int dist = LevenshteinDistance.computeDistance(event.getMessage().trim(), grammarUser.getCheckSentence().getCorrectSentence());
            if (dist == 0) {
                pendingGrammarUsers.remove(key);
                event.respond("Du bist authentifiziert, Danke.");
                event.getBot().getUserChannelDao().getChannel(grammarUser.getjEv().getChannel().getName()).send().voice(event.getUser());
            } else {
                long millis = (_answerTime * 60 * 1000 - (System.currentTimeMillis() - grammarUser.getPendingSince()));
                String tRemaining = String.format("%d Minuten, %d Sekunden",
                        TimeUnit.MILLISECONDS.toMinutes(millis),
                        TimeUnit.MILLISECONDS.toSeconds(millis)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                event.respond("Das war falsch. Du hast noch " + tRemaining + " Zeit für die korrekte Antwort. (Fehlerquote: " + String.valueOf(dist) + ")");
                grammarUser.setWarned(grammarUser.getWarned() + 1);
                pendingGrammarUsers.put(key, grammarUser);
            }
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
                String line = new String(stringRead.getBytes(), "UTF-8").trim();
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
            Logger.getLogger(GrammarListener.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return returnList;
    }

    private void startTimers() {
        grammarTimer = new Timer();
        grammarTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (String key : pendingGrammarUsers.keySet()) {
                    if (System.currentTimeMillis() - pendingGrammarUsers.get(key).getPendingSince() >= _answerTime * 60 * 1000) {
                        // Since the grammar questions are only issued when a user joined a channel, here must be an event of this Type -> .getjEv()
                        // If not, throw that stuff away
                        if (pendingGrammarUsers.get(key).getjEv() != null) {
                            JoinEvent e = pendingGrammarUsers.get(key).getjEv();
                            if (isGrammarCheckActive() && e.getChannel().isOp(e.getBot().getUserBot())) {
                                User u = pendingGrammarUsers.get(key).getUser();
                                u.getBot().sendIRC().message(u.getNick(), "<<Du hast den Anti-Spammer Test leider nicht bestanden.>>");
                                u.getBot().sendIRC().message(u.getNick(), "Du wirst für " + _banTime + " Minuten vom Channel " + e.getChannel().getName() + " ausgeschlossen.");
                                String banmask = "*!*@" + u.getHostmask();
                                pendingGrammarUsers.get(key).getBot().getUserChannelDao().getChannel(e.getChannel().getName()).send().ban(banmask);
                                pendingBans.put(banmask, new Ban(banmask, e.getChannel(), _banTime * 60 * 1000, System.currentTimeMillis(), pendingGrammarUsers.get(key).getUser()));
                                pendingGrammarUsers.get(key).getBot().getUserChannelDao().getChannel(e.getChannel().getName()).send().kick(u, "Du wirst für " + _banTime + " Minuten verbannt, weil du den Test nicht bestanden hast.");
                            }
                        }
                        pendingGrammarUsers.remove(key);
                    }
                }
            }
        }, 0, 1000);
        banTimer = new Timer();
        banTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (String key : pendingBans.keySet()) {
                    if (System.currentTimeMillis() - pendingBans.get(key).getTimeOfBan() >= pendingBans.get(key).getBanDuration()) {

                        if (pendingBans.get(key).getChannel().isOp(pendingBans.get(key).getUser().getBot().getUserBot())) {
                            pendingBans.get(key).getUser().getBot().getUserChannelDao().getChannel(pendingBans.get(key).getChannel().getName()).send().unBan(pendingBans.get(key).getChannel(), pendingBans.get(key).getBanEntry());
                            pendingBans.remove(key);
                        }
                    }
                }
            }
        }, 0, 1000);
        muteTimer = new Timer();
        muteTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (String key : channels.keySet()) {
                    if (channels.get(key).getMutedSince() > 0 && System.currentTimeMillis() - channels.get(key).getMutedSince() >= _grammarFloodTime * 60 * 1000) {
                        if (channels.get(key).getChannel().isOp(channels.get(key).getChannel().getBot().getUserBot())) {
                            channels.get(key).getChannel().getBot().getUserChannelDao().getChannel(channels.get(key).getChannel().getName()).send().removeModerated(channels.get(key).getChannel());
                            channels.remove(key);
                            XenoMatChannel chan = new XenoMatChannel(key, false, channels.get(key).getChannel());
                            channels.put(key, chan);
                        }
                    }
                }
            }
        }, 0, 1000);
    }

    public void putChannel(String channel, XenoMatChannel xenoMatChannel) {
        channels.put(channel, xenoMatChannel);
    }

    /**
     * @return the _grammarCheckActive
     */
    public boolean isGrammarCheckActive() {
        return _grammarCheckActive;
    }

    /**
     * @param grammarCheckActive the _grammarCheckActive to set
     */
    public void setGrammarCheckActive(boolean grammarCheckActive) {
        this._grammarCheckActive = grammarCheckActive;
    }
}
