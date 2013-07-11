package net.freenode.xenomorph.xenomat.jettyServlets;

import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.pircbotx.PircBotX;

public class VoiceRequestServlet extends HttpServlet {

    private PircBotX _bot;
    VelocityContext _vContext;
    private final ConcurrentHashMap<String,VoiceRequest> _voiceRequests;

    public VoiceRequestServlet(PircBotX bot, ConcurrentHashMap<String, VoiceRequest> voiceRequests) {
        _bot = bot;
        _vContext = new VelocityContext();
        _voiceRequests = voiceRequests;
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

        _vContext.put("title", _bot.getNick() + " - Administration");

//        ArrayList<String> requestList = new ArrayList<>();
//        for (Map.Entry<String, VoiceRequest> entry : _voiceRequests.entrySet()) {
//            requestList.add(entry.getKey() + " - " + entry.getValue());
//        }
        _vContext.put("requestList", _voiceRequests);
        Template template = null;
        template = Velocity.getTemplate("htmlTemplates/voiceRequests.html");
        StringWriter sw = new StringWriter();
        template.merge(_vContext, sw);
        response.getWriter().print(sw.toString());

    }
}
