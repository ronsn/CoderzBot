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
import net.freenode.xenomorph.xenomat.Listeners.DisconnectListener;
import net.freenode.xenomorph.xenomat.Listeners.GrammarListener;
import net.freenode.xenomorph.xenomat.Listeners.PluginListener;
import net.freenode.xenomorph.xenomat.jettyHandlers.HelloWorldHandler;
import net.freenode.xenomorph.xenomat.jettyHandlers.ModuleActivationHandler;
import net.freenode.xenomorph.xenomat.jettyHandlers.QuitHandler;
import net.freenode.xenomorph.xenomat.jettyHandlers.SayHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.pircbotx.Configuration;
import org.pircbotx.MultiBotManager;
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
            String nickPass = properties.getProperty("NickPass", null);
            String opPass = properties.getProperty("OpPass", "");
            if (opPass.isEmpty()) {
                System.out.println("You must set an opPass in bot.properties!");
                System.exit(0);
            }
            String serverPass = properties.getProperty("ServerPass", null);
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
            boolean grammarCheckActive = false;
            if (properties.getProperty("GrammarCheckActive", "false").equals("true")) {
                grammarCheckActive = true;
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


            //Setup this bot
            GrammarListener gl = new GrammarListener(nick, answerTime, banTime, useGrammarFloodLimit, grammarFloodLimit, grammarFloodTime, grammarCheckActive);
            Configuration configuration = new XenoConf.XenoBuilder()
                    .addAutoJoinChannels(channels) // MUST be set first, since all methods that are not overwritten do return an instance of Builder, not XenoBuilder!!!
                    .setName(nick) //Set the nick of the bot. CHANGE IN YOUR CODE
                    .setAutoNickChange(true) //Automatically change nick when the current one is in use
                    .setCapEnabled(true) //Enable CAP features
                    .addListener(new DisconnectListener()) //This class is a listener, so add it to the bots known listeners
                    .addListener(new PluginListener()) //This class is a listener, so add it to the bots known listeners
                    .addListener(gl) //This class is a listener, so add it to the bots known listeners
                    .setServerHostname(server)
                    .setLogin(login)
                    .setNickservPassword(nickPass)
                    .setAutoNickChange(autoNickChange)
                    .setServerPassword(serverPass)
                    .setServerPort(port)
                    .setEncoding(Charset.forName(encoding))
                    .buildConfiguration();

            PircBotX bot = new PircBotX(configuration);


            MultiBotManager mbm = new MultiBotManager();
            mbm.addBot(bot);
            mbm.start();

            Server httpServer = new Server(8080);
            ContextHandler moduleActivationHandler = new ContextHandler("/moduleactivation");
            moduleActivationHandler.setHandler(new ModuleActivationHandler(bot, gl, opPass));
            ContextHandler sayHandler = new ContextHandler("/say");
            sayHandler.setHandler(new SayHandler(bot, opPass));
            ContextHandler helloHandler = new ContextHandler("/");
            helloHandler.setHandler(new HelloWorldHandler(bot));
            ContextHandler quitHandler = new ContextHandler("/quit");
            quitHandler.setHandler(new QuitHandler(bot, opPass));
            ContextHandlerCollection contexts = new ContextHandlerCollection();
            contexts.setHandlers(new Handler[]{moduleActivationHandler, helloHandler, quitHandler, sayHandler});
            httpServer.setHandler(contexts);
            httpServer.start();
            httpServer.join();

        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
