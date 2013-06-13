package net.freenode.xenomorph.xenomat.jettyServlets;

import java.io.IOException;
import java.io.StringWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.pircbotx.PircBotX;

public class IndexServlet extends HttpServlet {

    private PircBotX _bot;
    VelocityContext _vContext;

    public IndexServlet(PircBotX bot) {
        _bot = bot;
        _vContext = new VelocityContext();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        if (!SessionHelper.isAdmin(request)) {
            Template template = null;
            template = Velocity.getTemplate("htmlTemplates/errorNotAuthenticated.html");
            StringWriter sw = new StringWriter();
            template.merge(_vContext, sw);
            response.getWriter().print(sw.toString());
            return;
        }

        _vContext.put("title", _bot.getNick() + " - Administration");
        _vContext.put("botName", _bot.getNick());
        Template template = null;
        template = Velocity.getTemplate("htmlTemplates/index.html");
        StringWriter sw = new StringWriter();
        template.merge(_vContext, sw);
        response.getWriter().print(sw.toString());

    }
}