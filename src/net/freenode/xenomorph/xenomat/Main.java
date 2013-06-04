package net.freenode.xenomorph.xenomat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.freenode.xenomorph.xenomat.Listeners.GrammarListener;
import net.freenode.xenomorph.xenomat.Listeners.GroovyListener;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;

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
            encoding = encoding.isEmpty() ? "UTF-8" : encoding;
            String server = properties.getProperty("Server");
            String nick = properties.getProperty("Nick");
            String login = properties.getProperty("Login", nick);
            String nickPass = properties.getProperty("NickPass", "");
            String opPass = properties.getProperty("OpPass", "");
            String serverPass = properties.getProperty("ServerPass", "");
            String _port = properties.getProperty("Port", "6667");
            Integer port = _port.isEmpty() ? 6667 : Integer.valueOf(_port);// Ternary operator ensures there is a default value set
            String channelList = properties.getProperty("ChannelList", "");
            String _banTime = properties.getProperty("BanTime", "10");
            Integer banTime = _banTime.isEmpty() ? 10 : Integer.valueOf(_banTime);
            String _answerTime = properties.getProperty("AnswerTime", "10");
            Integer answerTime = _answerTime.isEmpty() ? 10 : Integer.valueOf(_answerTime);
            String _grammarFloodLimit = properties.getProperty("GrammarFloodLimit", "6");
            Integer grammarFloodLimit = _grammarFloodLimit.isEmpty() ? 6 : Integer.valueOf(_grammarFloodLimit);
            String _grammarFloodTime = properties.getProperty("GrammarFloodTime", "10");
            Integer grammarFloodTime = _grammarFloodTime.isEmpty() ? 10 : Integer.valueOf(_grammarFloodTime);
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

            // Create new configuration
            PircBotX bot = new PircBotX();
            bot.useShutdownHook(true);
            bot.setAutoReconnect(true);
            //Add Listeners
            bot.getListenerManager().addListener(new GroovyListener());
            GrammarListener gl = new GrammarListener(nick, answerTime, banTime, useGrammarFloodLimit, grammarFloodLimit, grammarFloodTime);
            bot.getListenerManager().addListener(gl);

            //Setup
            bot.setName(nick);
            bot.setVerbose(verbose);
            bot.setEncoding(Charset.forName(encoding));
            bot.setLogin(login);
            bot.setAutoNickChange(autoNickChange);

            if (serverPass.isEmpty()) {
                bot.connect(server, port);
            } else {
                bot.connect(server, port, serverPass);
            }

            if (!nickPass.isEmpty()) {
                bot.identify(nickPass);
            }

            for (String channel : channels) {
                bot.joinChannel(channel);
                Channel c = bot.getChannel(channel);
                // When connecting to a bouncer with the bot already having OP,
                // no onOp event will be triggered. By setting Op to ourselfs
                // it is triggered manually if we already have op, so the bot
                // knows in which channels it has OP.
                bot.op(c, bot.getUserBot());
                // The GrammarListener needs to know the channels we are in.
                // If connecting to a bouncer no onJoin events will be triggered.
                // Therefore we need to feed the channels from here.
                gl.putChannel(channel, new XenoMatChannel(channel, false, c));
            }


        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
