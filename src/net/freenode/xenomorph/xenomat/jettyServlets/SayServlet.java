/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
import org.pircbotx.User;

public class SayServlet extends HttpServlet {

    private PircBotX _bot;
    VelocityContext _vContext;

    public SayServlet(PircBotX bot) {
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

        _vContext.put("title", _bot.getNick() + " - Administration");

        String channel = "";
        String user = "";
        String targetType = "";

        if (request.getParameter("mode") != null && request.getParameter("mode").equals("say") && request.getParameter("msg") != null && request.getParameter("sayTargetType") != null) {
            targetType = request.getParameter("sayTargetType");
            if (targetType.equals("channel")) {
                if (request.getParameter("channel") != null && !request.getParameter("channel").isEmpty() && !request.getParameter("msg").isEmpty()) {
                    _bot.sendIRC().message(request.getParameter("channel"), request.getParameter("msg"));
                    channel = request.getParameter("channel");
                    _vContext.put("msgSent", "Sent \"" + request.getParameter("msg") + "\" to " + channel);
                    _vContext.put("lastChannel", channel);
                } else {
                    _vContext.put("msgError", "Please fill in all form elements!");
                }
            } else if (targetType.equals("user")) {
                if (request.getParameter("user") != null && !request.getParameter("user").isEmpty() && !request.getParameter("msg").isEmpty()) {
                    _bot.sendIRC().message(request.getParameter("user"), request.getParameter("msg"));
                    user = request.getParameter("user");
                    _vContext.put("msgSent", "Sent \"" + request.getParameter("msg") + "\" to " + user);
                    _vContext.put("lastUser", user);
                } else {
                    _vContext.put("msgError", "Please fill in all form elements!");
                }
            }

        }
        if (!targetType.isEmpty() && targetType.equals("channel")) {
            _vContext.put("lastTarget", "channel");
        }
        if (!targetType.isEmpty() && targetType.equals("user")) {
            _vContext.put("lastTarget", "user");
        }
        ArrayList<String> channels = new ArrayList<>();
        for (Channel chan : _bot.getUserBot().getChannels()) {
            channels.add(chan.getName());
        }
        _vContext.put("channels", channels);
        ArrayList<String> users = new ArrayList<>();
        for (Channel c : _bot.getUserBot().getChannels()) {
            for (User u : c.getUsers()) {
                if (!users.contains(u.getNick())) {
                    users.add(u.getNick());
                }
            }
        }
        _vContext.put("users", users);
        Template template = null;
        template = Velocity.getTemplate("htmlTemplates/say.html");
        StringWriter sw = new StringWriter();
        template.merge(_vContext, sw);
        response.getWriter().print(sw.toString());
    }
}
