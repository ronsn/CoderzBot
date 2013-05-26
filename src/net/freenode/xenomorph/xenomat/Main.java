package net.freenode.xenomorph.xenomat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jibble.pircbot.IrcException;

public class Main {

    private static String botConfigFile = "bot.properties";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            // Read the properties file. If it doesn't exist, quit with error
            File f = new File(botConfigFile);
            if (!f.exists()) {
                System.out.println("Config file " + f.getCanonicalPath() + " doesn't exist.");
                System.exit(0);
            }
            // not needed anymore
            f = null;

            Properties properties = new Properties();
            try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(botConfigFile))) {
                properties.load(stream);
            }

            // Connect all property values to variables
            // Some of those need default values
            boolean autoNickChange = false;
            if (properties.getProperty("AutoNickChange", "false").equals("true")) {
                autoNickChange = true;
            }
            boolean verbose = false;
            if (properties.getProperty("Verbose", "false").equals("true")) {
                verbose = true;
            }
            String encoding = properties.getProperty("Encoding", "UTF-8");
            String server = properties.getProperty("Server");
            String nick = properties.getProperty("Nick");
            String nickPass = properties.getProperty("NickPass", "");
            String opPass = properties.getProperty("OpPass", "");
            String port = properties.getProperty("Port", "");
            String channelList = properties.getProperty("ChannelList", "");
            Integer banTime = Integer.valueOf(properties.getProperty("BanTime", "10"));
            Integer answerTime = Integer.valueOf(properties.getProperty("AnswerTime", "10"));
            Integer grammarFloodLimit = Integer.valueOf(properties.getProperty("GrammarFloodLimit", "6"));
            Integer grammarFloodTime = Integer.valueOf(properties.getProperty("GrammarFloodTime", "10"));
            boolean useGrammarFloodLimit = false;
            if (properties.getProperty("UseGrammarFloodLimit", "false").equals("true")) {
                useGrammarFloodLimit = true;
            }
            boolean killGhost = false;
            if (properties.getProperty("KillGhost", "false").equals("true")) {
                killGhost = true;
            }
            // We won't need the properties until next start!
            properties = null;

            // Parse channel list into array.
            // If no channels are defined the bot must be contacted via query
            List<String> channels = new ArrayList<>();
            if (!channelList.isEmpty()) {
                channels = Arrays.asList(channelList.split("\\s*,\\s*"));
            }

            // Now start our bot up.
            XenoMat bot = new XenoMat(nick, opPass, banTime, answerTime, useGrammarFloodLimit, grammarFloodTime, grammarFloodLimit, nickPass, killGhost);

            bot.setAutoNickChange(autoNickChange);
            bot.setEncoding(encoding);

            // Enable debugging output.
            bot.setVerbose(verbose);

            // Connect to the IRC server.
            if (port.isEmpty()) {
                bot.connect(server);
            } else {
                bot.connect(server, Integer.valueOf(port));
            }

            if (!nickPass.isEmpty()) {
                // Auth to NickServ
                bot.identify(nickPass);
            }

            for (String channel : channels) {
                bot.joinChannel(channel);
            }


        } catch (IOException | NumberFormatException | IrcException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }


    }
}
