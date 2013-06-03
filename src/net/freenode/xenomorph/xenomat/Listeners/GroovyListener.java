package net.freenode.xenomorph.xenomat.Listeners;

import groovy.lang.GroovyClassLoader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.freenode.xenomorph.xenomat.Command;
import net.freenode.xenomorph.xenomat.CommandResponse;
import net.freenode.xenomorph.xenomat.botCommand;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.control.CompilationFailedException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public class GroovyListener extends ListenerAdapter {

    private ConcurrentHashMap<String, HashMap<String, Command>> commandLastUsedAt;

    public GroovyListener() {
        commandLastUsedAt = new ConcurrentHashMap<>();
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
    public void onMessage(MessageEvent event) throws Exception {
        if (event.getMessage().trim().startsWith("!") && !event.getMessage().trim().startsWith("!bot") && !event.getMessage().trim().startsWith("!help")) {
            CommandResponse crp = handleCommand(event.getMessage(), event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask());
            for (String response : crp.getResponseText()) {
                if (response.startsWith("/me")) {
                    event.getBot().sendAction(event.getChannel(), StringUtils.stripStart(response, "/me").trim());
                } else {
                    if (crp.getCommandSuccesfull()) {
                        event.respond(response);
                    } else {
                        event.getBot().sendMessage(event.getUser(), response);
                    }
                }
            }
        }
    }

    @Override
    public void onPrivateMessage(PrivateMessageEvent event) throws Exception {
        // Built-in commands MUST start with !bot or !help, everything else is considered to be a groovy command.
        if (event.getMessage().trim().startsWith("!") && !event.getMessage().trim().startsWith("!bot") && !event.getMessage().trim().startsWith("!help")) {
            CommandResponse crp = handleCommand(event.getMessage(), event.getUser().getNick(), event.getUser().getLogin(), event.getUser().getHostmask());
            for (String response : crp.getResponseText()) {
                event.respond(response);
            }
        }
    }

    private CommandResponse handleCommand(String message, String nick, String login, String hostmask) {
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
        CommandResponse returnValue = runCommand(command, args, nick, login, hostmask);
        if (returnValue.getCommandSuccesfull() == true) {
            String key = nick + login + hostmask;
            if (commandLastUsedAt.get(key) != null) {
                HashMap<String, Command> cmd = commandLastUsedAt.get(key);
                if (cmd.get(command) != null) {
                    Command c = cmd.get(command);
                    c.setLastUsedAt(System.currentTimeMillis());
                    cmd.remove(command);
                    cmd.put(command, c);
                    commandLastUsedAt.remove(key);
                    commandLastUsedAt.put(key, cmd);
                } else {
                    Command c = new Command();
                    c.setCommandName(command);
                    c.setLastUsedAt(System.currentTimeMillis());
                    cmd.put(command, c);
                    commandLastUsedAt.remove(key);
                    commandLastUsedAt.put(key, cmd);
                }
            } else {
                Command c = new Command();
                c.setCommandName(command);
                c.setLastUsedAt(System.currentTimeMillis());
                HashMap<String, Command> cmd = new HashMap<>();
                cmd.put(command, c);
                commandLastUsedAt.put(key, cmd);
            }
        }
        return returnValue;
    }

    public CommandResponse runCommand(String command, String[] args, String nick, String login, String hostmask) {
        ArrayList<String> text = new ArrayList<String>();
        CommandResponse returnVal = new CommandResponse(text, false);
        String key = nick + login + hostmask;
        try {
            if (command.matches("[A-Za-z]+")) {
                File commandFile = new File("botCommands/" + command + ".groovy");
                if (commandFile.exists()) {
                    GroovyClassLoader gcl = new GroovyClassLoader();
                    Class clazz = gcl.parseClass(commandFile);
                    Object aScript = clazz.newInstance();

                    botCommand hw = (botCommand) aScript;
                    long cmdLastUsedAt = -1;
                    if (commandLastUsedAt.get(key) != null && commandLastUsedAt.get(key).get(command) != null && commandLastUsedAt.get(key).get(command).getLastUsedAt() != null) {
                        cmdLastUsedAt = commandLastUsedAt.get(key).get(command).getLastUsedAt();
                    }
                    returnVal = hw.onCommand(nick, args, cmdLastUsedAt);


                }
            }
        } catch (CompilationFailedException | IOException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(GroovyListener.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return returnVal;
    }
}
