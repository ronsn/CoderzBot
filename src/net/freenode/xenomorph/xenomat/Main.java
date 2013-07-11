package net.freenode.xenomorph.xenomat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.freenode.xenomorph.xenomat.Listeners.PluginListener;
import net.freenode.xenomorph.xenomat.jettyServlets.IndexServlet;
import net.freenode.xenomorph.xenomat.jettyServlets.JoinPartServlet;
import net.freenode.xenomorph.xenomat.jettyServlets.LoginServlet;
import net.freenode.xenomorph.xenomat.jettyServlets.QuitServlet;
import net.freenode.xenomorph.xenomat.jettyServlets.SayServlet;
import net.freenode.xenomorph.xenomat.jettyServlets.SetMode;
import net.freenode.xenomorph.xenomat.jettyServlets.VoiceRequest;
import net.freenode.xenomorph.xenomat.jettyServlets.VoiceRequestServlet;
import net.freenode.xenomorph.xenomat.jettyServlets.WhiteListServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.pircbotx.Configuration;
import org.pircbotx.MultiBotManager;
import org.pircbotx.PircBotX;

public class Main {

    private static String botConfigFile = "bot.properties";
    private static ConcurrentHashMap<String, VoiceRequest> voiceRequests = new ConcurrentHashMap<>();
    private static ArrayList<String> _whiteList;

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
            if (opPass.isEmpty()) {
                System.out.println("You must set an opPass in bot.properties!");
                System.exit(0);
            }
            String serverPass = properties.getProperty("ServerPass", null);
            String _port = properties.getProperty("Port", "6667");
            Integer port = _port.isEmpty() ? 6667 : Integer.valueOf(_port);// Ternary operator ensures there is a default value set
            String channelList = properties.getProperty("ChannelList", "");
            String ParseApplicationID = properties.getProperty("ParseApplicationID", "");
            String ParseRESTAPIKey = properties.getProperty("ParseRESTAPIKey", "");
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

            //Get WhiteList
            _whiteList = fileToArrayList("whitelist.txt", FileTypes.txtFileType.WHITELIST);

            //Setup this bot
            Configuration configuration = new XenoConf.XenoBuilder()
                    .addAutoJoinChannels(channels) // MUST be set first, since all methods that are not overwritten do return an instance of Builder, not XenoBuilder!!!
                    .setName(nick) //Set the nick of the bot. CHANGE IN YOUR CODE
                    .setAutoNickChange(true) //Automatically change nick when the current one is in use
                    .setCapEnabled(true) //Enable CAP features
                    .addListener(new PluginListener(ParseApplicationID, ParseRESTAPIKey, voiceRequests, _whiteList)) //This class is a listener, so add it to the bots known listeners
                    //                    .addListener(gl) //This class is a listener, so add it to the bots known listeners
                    .setServerHostname(server)
                    .setLogin(login)
                    .setNickservPassword(nickPass)
                    .setAutoNickChange(autoNickChange)
                    .setServerPassword(serverPass)
                    .setServerPort(port)
                    .setEncoding(Charset.forName(encoding))
                    .buildConfiguration();
            PircBotX bot = new PircBotX(configuration);
            bot.setAutoReconnect(true);

            MultiBotManager mbm = new MultiBotManager();
            mbm.addBot(bot);
            mbm.start();

            Server httpServer = new Server(8080);

            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            httpServer.setHandler(context);

            context.addServlet(new ServletHolder(new IndexServlet(bot)), "/*");
            context.addServlet(new ServletHolder(new LoginServlet(bot, opPass)), "/login/*");
            context.addServlet(new ServletHolder(new SayServlet(bot)), "/say/*");
            context.addServlet(new ServletHolder(new QuitServlet(bot)), "/quit/*");
            context.addServlet(new ServletHolder(new JoinPartServlet(bot)), "/joinpart/*");
            context.addServlet(new ServletHolder(new SetMode(bot)), "/setmode/*");
            context.addServlet(new ServletHolder(new VoiceRequestServlet(bot, voiceRequests)), "/voicerequests/*");
            context.addServlet(new ServletHolder(new WhiteListServlet(bot, _whiteList)), "/whitelist/*");
            httpServer.start();
            httpServer.join();

        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static ArrayList<String> fileToArrayList(String fileName, FileTypes.txtFileType type) {
        ArrayList<String> returnList = new ArrayList<>();
        try {
            File f = new File(fileName);
            if (!f.exists()) {
                System.out.println("File " + f.getCanonicalPath() + " doesn't exist.");
                return new ArrayList<>();
            }
            // not needed anymore
            f = null;
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            String stringRead = br.readLine();

            while (stringRead != null) {
                String line = new String(stringRead.getBytes(), "UTF-8").trim();
                if (!line.startsWith("#") && !line.isEmpty()) { // ignore comments and empty lines
                    if (type.equals(FileTypes.txtFileType.WHITELIST)) {
                        returnList.add(line.trim());
                    }
                }
                // read the next line
                stringRead = br.readLine();
            }
            br.close();


        } catch (Exception ex) {
            Logger.getLogger(Main.class
                    .getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
        return returnList;
    }
}
