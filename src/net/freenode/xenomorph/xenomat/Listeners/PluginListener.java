package net.freenode.xenomorph.xenomat.Listeners;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.freenode.xenomorph.xenomat.Command;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import net.freenode.xenomorph.xenomat.CommandResponse;
import net.freenode.xenomorph.xenomat.botCommand;
import net.freenode.xenomorph.xenomat.jettyServlets.VoiceRequest;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.JoinEvent;

public class PluginListener extends ListenerAdapter {

    private ConcurrentHashMap<String, botCommand> plugins;
    private ConcurrentHashMap<String, String> pluginsChecksum;
    private ConcurrentHashMap<String, HashMap<String, Command>> commandLastUsedAt;
    private ArrayList<String> _whiteList;
    private String _pApiKey;
    private String _pAppID;
    private final ConcurrentHashMap<String, VoiceRequest> _voiceRequests;

    public PluginListener(String ParseApplicationID, String ParseRESTAPIKey, ConcurrentHashMap<String, VoiceRequest> voiceRequests, ArrayList<String> whiteList) {
        plugins = new ConcurrentHashMap<>();
        pluginsChecksum = new ConcurrentHashMap<>();
        commandLastUsedAt = new ConcurrentHashMap<>();
        _whiteList = whiteList;

        _pAppID = ParseApplicationID;
        _pApiKey = ParseRESTAPIKey;
        _voiceRequests = voiceRequests;
    }

    @Override
    public void onNickChange(NickChangeEvent event) {

        String oldNick = event.getOldNick();
        String login = event.getUser().getLogin();
        String hostname = event.getUser().getHostmask();
        String newNick = event.getNewNick();

        String oldKey = oldNick + login + hostname;

        if (commandLastUsedAt.containsKey(oldKey)) {
            HashMap<String, Command> lUA = commandLastUsedAt.get(oldKey);
            commandLastUsedAt.remove(oldKey);
            String newKey = newNick + login + hostname;
            commandLastUsedAt.put(newKey, lUA);
        }
    }

    @Override
    public void onMessage(MessageEvent event) {
        if (event.getMessage().trim().startsWith("!") && !event.getMessage().trim().startsWith("!bot") && !event.getMessage().trim().startsWith("!help") && !event.getMessage().trim().startsWith("!voice")) {
            CommandResponse crp = handleCommand(event.getMessage(), event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(), event.getBot());
            for (String response : crp.getResponseText()) {
                if (response.startsWith("/me")) {
                    event.getChannel().send().action(StringUtils.stripStart(response, "/me").trim());
                } else {
                    if (crp.getCommandSuccesfull()) {
                        event.respond(response);
                    } else {
                        event.getUser().send().message(response);
                    }
                }
            }
        }
    }

    @Override
    public void onPrivateMessage(PrivateMessageEvent event) {
        // Built-in commands MUST start with !bot or !help, everything else is considered to be a groovy command.
        if (event.getMessage().trim().startsWith("!") && !event.getMessage().trim().startsWith("!bot") && !event.getMessage().trim().startsWith("!help") && !event.getMessage().trim().startsWith("!voice")) {
            CommandResponse crp = handleCommand(event.getMessage(), event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask(), event.getBot());
            for (String response : crp.getResponseText()) {
                event.respond(response);
            }
        } else if (event.getMessage().trim().startsWith("!voice")) {
        }
    }

    @Override
    public void onJoin(JoinEvent event) {
        synchronized (_whiteList) {
            if (event.getChannel().isModerated() && event.getChannel().isOp(event.getBot().getUserBot()) && !_whiteList.contains(event.getUser().getNick()) && !event.getUser().getNick().equals(event.getBot().getNick())) {
                event.getBot().sendIRC().message(event.getUser().getNick(), "Der Channel " + event.getChannel().getName() + " ist moderiert.");
                event.getBot().sendIRC().message(event.getUser().getNick(), "Um voice zu erhalten und schreiben zu können, wende Dich bitte per Query an einen der anderen User, die Op-Rechte haben.");
                event.getBot().sendIRC().message(event.getUser().getNick(), "Wir bemühen uns dann, Deine Anfrage so schnell wie möglich zu bearbeiten.");
                event.getBot().sendIRC().message(event.getUser().getNick(), "Wir arbeiten daran, diesen Prozess weiter zu beschleunigen und zu automatisieren.");
                event.getBot().sendIRC().message(event.getUser().getNick(), "Bis dahin: Danke für Deine Geduld.");
                String userHash = event.getUser().getNick() + "!" + event.getUser().getLogin() + "@" + event.getUser().getHostmask();
                if (!_voiceRequests.containsKey(userHash)) {
                    _voiceRequests.put(userHash, new VoiceRequest(false, event.getUser(), event.getChannel()));
                }
            } else if (_whiteList.contains(event.getUser().getNick()) && event.getChannel().isOp(event.getBot().getUserBot()) && !event.getUser().getNick().equals(event.getBot().getNick()) && event.getUser().isVerified()) {
                event.getChannel().send().voice(event.getUser());
            }
        }
    }

    protected ArrayList<String> getAllUsers(PircBotX bot) {
        ArrayList<String> returnVal = new ArrayList<>();
        for (Channel c : bot.getUserBot().getChannels()) {
            for (User u : c.getUsers()) {
                if (!returnVal.contains(u.getNick())) {
                    returnVal.add(u.getNick());
                }
            }
        }
        return returnVal;
    }

    private CommandResponse handleCommand(String message, String nick, String login, String hostmask, PircBotX bot) {
        String[] cmdString = message.trim().split("\\s");
        // The command is always the first one, without !
        String command = StringUtils.stripStart(cmdString[0], "!");
        // First, assume there are nor arguments
        String[] args = new String[]{};
        // If however there are arguments...
        if (cmdString.length > 1) {
            // ...create a new string array and fill it with these arguments
            args = new String[cmdString.length - 1];
            for (int i = 0; i < cmdString.length; i++) {
                if (i > 0) {
                    args[i - 1] = cmdString[i];
                }
            }
        }
        CommandResponse returnValue = runCommand(command, args, nick, login, hostmask, getAllUsers(bot));
        if (returnValue.getCommandSuccesfull() == true) {
            String key = nick + login + hostmask;
            if (commandLastUsedAt.get(key) != null) {
                HashMap<String, Command> cmd = commandLastUsedAt.get(key);
                if (cmd.get(command) != null) {
                    Command c = cmd.get(command);
                    c.setSaveData(returnValue.getSaveData());
                    cmd.remove(command);
                    cmd.put(command, c);
                    commandLastUsedAt.remove(key);
                    commandLastUsedAt.put(key, cmd);
                } else {
                    Command c = new Command(command, returnValue.getSaveData());
                    cmd.put(command, c);
                    commandLastUsedAt.remove(key);
                    commandLastUsedAt.put(key, cmd);
                }
            } else {
                Command c = new Command(command, returnValue.getSaveData());
                HashMap<String, Command> cmd = new HashMap<>();
                cmd.put(command, c);
                commandLastUsedAt.put(key, cmd);
            }
        }
        return returnValue;
    }

    public static void dumpClasspath(ClassLoader loader) {
        System.out.println("Classloader " + loader + ":");

        if (loader instanceof URLClassLoader) {
            URLClassLoader ucl = (URLClassLoader) loader;
            System.out.println("\t" + Arrays.toString(ucl.getURLs()));
        } else {
            System.out.println("\t(cannot display components as not a URLClassLoader)");
        }

        if (loader.getParent() != null) {
            dumpClasspath(loader.getParent());
        }
    }

    private String fileChecksum(File f) {
        try {
            FileInputStream fis = new FileInputStream(f);
            return org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
        } catch (IOException ex) {
            Logger.getLogger(PluginListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public CommandResponse runCommand(String command, String[] args, String nick, String login, String hostmask, ArrayList<String> knownUsers) {
        ArrayList<String> text = new ArrayList<>();
        CommandResponse returnVal;
        returnVal = new CommandResponse(text, false, null);
        String key = nick + login + hostmask;
        if (command.matches("[A-Za-z]+")) {
            String classesDirString = StringUtils.replace(System.getProperty("user.dir"), "\\", "/") + "/botCommands";
            File classFile = new File(classesDirString + "/coderzServiceCommands/Command" + StringUtils.capitalize(command) + ".class");
            if (classFile.exists()) {
                if (!plugins.containsKey(command) || pluginsChecksum.get(command) == null || !fileChecksum(classFile).equals(pluginsChecksum.get(command))) {
                    try {
                        File classesDir = new File(classesDirString);
                        ClassLoader parentLoader = PluginListener.class.getClassLoader();
                        URLClassLoader loader = new URLClassLoader(new URL[]{classesDir.toURI().toURL()}, parentLoader);
                        Class cls = loader.loadClass("coderzServiceCommands.Command" + StringUtils.capitalize(command));
                        botCommand bc = (botCommand) cls.newInstance();
                        pluginsChecksum.put(command, fileChecksum(classFile));
                        plugins.put(command, bc);
                    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | MalformedURLException ex) {
                        if (plugins.containsKey(command)) {
                            plugins.remove(command);
                        }
                        if (pluginsChecksum.containsKey(command)) {
                            pluginsChecksum.remove(command);
                        }
                        //Logger.getLogger(PluginListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                // Still not a known command?
                if (!plugins.containsKey(command)) {
                    return returnVal;
                } else if (plugins.get(command) == null) {
                    plugins.remove(command);
                    return returnVal;
                }
            }
        }
        botCommand hw = plugins.get(command);
        if (hw != null) {
            long cmdLastUsedAt = -1;
            Object savedData = null;
            if (commandLastUsedAt.get(key) != null && commandLastUsedAt.get(key).get(command) != null && commandLastUsedAt.get(key).get(command).getLastUsedAt() != null) {
                cmdLastUsedAt = commandLastUsedAt.get(key).get(command).getLastUsedAt();
                savedData = commandLastUsedAt.get(key).get(command).getSaveData();
            }
            returnVal = hw.onCommand(nick, args, cmdLastUsedAt, knownUsers, savedData);
        }
        return returnVal;
    }
}