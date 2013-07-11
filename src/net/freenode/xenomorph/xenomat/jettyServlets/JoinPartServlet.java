package net.freenode.xenomorph.xenomat.jettyServlets;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;

public class JoinPartServlet extends HttpServlet {

    private PircBotX _bot;
    private VelocityContext _vContext;

    public JoinPartServlet(PircBotX bot) {
        _bot = bot;
        _vContext = new VelocityContext();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request, response);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Cache-Control", "no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        if (!SessionHelper.isAdmin(request)) {
            Template template = null;
            template = Velocity.getTemplate("htmlTemplates/errorNotAuthenticated.html");
            StringWriter sw = new StringWriter();
            template.merge(_vContext, sw);
            response.getWriter().print(sw.toString());
            return;
        }

        if (request.getParameter("mode") != null && !request.getParameter("mode").isEmpty() && request.getParameter("mode").equals("join") && request.getParameter("joinChannels") != null && !request.getParameter("joinChannels").isEmpty()) {
            String[] channelsToJoin = request.getParameter("joinChannels").split(",");
            for (String channelToJoin : channelsToJoin) {
                _bot.sendIRC().joinChannel(channelToJoin);
            }
        } else if (request.getParameter("mode") != null && !request.getParameter("mode").isEmpty() && request.getParameter("mode").equals("part") && request.getParameter("partChannel") != null && !request.getParameter("partChannel").isEmpty()) {
            for (Channel c : _bot.getUserBot().getChannels()) {
                if (c.getName().equals(request.getParameter("partChannel"))) {
                    c.send().part();
                }
            }
        }

        _vContext.put("title", _bot.getNick() + " - Administration");
        ArrayList<String> channels = new ArrayList<>();
        for (Channel chan : _bot.getUserBot().getChannels()) {
            channels.add(chan.getName());
        }
        _vContext.put("channels", channels);
        Template template = null;
        template = Velocity.getTemplate("htmlTemplates/joinPart.html");
        StringWriter sw = new StringWriter();
        template.merge(_vContext, sw);
        response.getWriter().print(sw.toString());
    }
}
