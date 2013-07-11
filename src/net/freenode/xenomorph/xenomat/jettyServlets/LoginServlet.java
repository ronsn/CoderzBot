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

public class LoginServlet extends HttpServlet {

    private PircBotX _bot;
    private VelocityContext _vContext;
    private String _botPass;

    public LoginServlet(PircBotX bot, String botPass) {
        _bot = bot;
        _vContext = new VelocityContext();
        _botPass = botPass;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request, response);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionHelper.removeAdmin(request);
        response.setHeader("Cache-Control", "no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        if (request.getParameter("pass") != null && request.getParameter("pass").equals(_botPass)) {
            SessionHelper.setAdmin(request);
            _vContext.put("loginSuccessfull", true);
        }else{
            _vContext.put("loginSuccessfull", false);
        }

        _vContext.put("title", _bot.getNick() + " - Administration");
        _vContext.put("botName", _bot.getNick());
        Template template = null;
        template = Velocity.getTemplate("htmlTemplates/login.html");
        StringWriter sw = new StringWriter();
        template.merge(_vContext, sw);
        response.getWriter().print(sw.toString());
    }
}
