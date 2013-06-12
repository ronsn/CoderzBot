/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.freenode.xenomorph.xenomat.jettyHandlers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

public class SayHandler extends AbstractHandler {

    private PircBotX _bot;
    private String _adminPass;

    public SayHandler(PircBotX bot, String adminPass) {
        _bot = bot;
        _adminPass = adminPass;
    }

    @Override
    public void handle(String string, Request baseRequest, HttpServletRequest hsr, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        String pass = "";
        String channel = "";
        String user = "";
        String targetType = "";
        response.getWriter().println(HtmlHelper.getHeader(_bot.getNick() + " - Administration"));
        if (hsr.getParameter("mode") != null && hsr.getParameter("mode").equals("say") && hsr.getParameter("msg") != null && hsr.getParameter("sayTargetType") != null) {
            targetType = hsr.getParameter("sayTargetType");
            if (hsr.getParameter("pass") != null) {
                if (hsr.getParameter("pass").equals(_adminPass)) {
                    pass = hsr.getParameter("pass");
                    if (targetType.equals("channel")) {
                        if (hsr.getParameter("channel") != null && !hsr.getParameter("channel").isEmpty() && !hsr.getParameter("msg").isEmpty()) {
                            _bot.sendIRC().message(hsr.getParameter("channel"), hsr.getParameter("msg"));
                            channel = hsr.getParameter("channel");
                            response.getWriter().println("<h1>" + "Send \"" + hsr.getParameter("msg") + "\" to " + channel + "</h1>");
                        } else {
                            response.getWriter().println("<h1>" + "Please fill in all form elements!" + "</h1>");
                        }
                    } else if (targetType.equals("user")) {
                        if (hsr.getParameter("user") != null && !hsr.getParameter("user").isEmpty() && !hsr.getParameter("msg").isEmpty()) {
                            _bot.sendIRC().message(hsr.getParameter("user"), hsr.getParameter("msg"));
                            user = hsr.getParameter("user");
                            response.getWriter().println("<h1>" + "Send \"" + hsr.getParameter("msg") + "\" to " + user + "</h1>");
                        } else {
                            response.getWriter().println("<h1>" + "Please fill in all form elements!" + "</h1>");
                        }
                    }
                } else {
                    response.getWriter().println("<h1>" + "Wrong password!" + "</h1>");
                    response.getWriter().println("<h2>" + "I've got:" + hsr.getParameter("pass") + "</h2>");
                }
            } else {
                response.getWriter().println("<h1>" + "Not enough parameters!" + "</h1>");
                response.getWriter().println("<p><a href='/say/'>" + "Go back..." + "</a></p>");
            }
            response.getWriter().println("<hr />");
        }
        response.getWriter().println("<form action='/say/' method='POST'>");
        response.getWriter().println("<input type='hidden' name='mode' value='say' />");
        response.getWriter().println("Password: <input type='password' name='pass' value='" + pass + "' /><br />");
        response.getWriter().println("Message: <input type='text' name='msg' /><br />");
        response.getWriter().println("<input type='radio' name='sayTargetType' value='channel'" + ((!targetType.isEmpty() && targetType.equals("channel")) ? " checked='checked'" : "") + " />Channel: <select name='channel' size='1'>");
        for (Channel chan : _bot.getUserBot().getChannels()) {
            response.getWriter().println("<option" + ((!channel.isEmpty() && channel.equals(chan.getName())) ? " selected='selected'" : "") + ">" + chan.getName() + "</option>");
        }
        response.getWriter().println("</select><br />");
        response.getWriter().println("<p>or</p>");
        response.getWriter().println("<input type='radio' name='sayTargetType' value='user'" + ((!targetType.isEmpty() && targetType.equals("user")) ? " checked='checked'" : "") + " />");
        response.getWriter().println("User: <input type='text' name='user' value='" + (!user.isEmpty() ? user.trim() : "") + "' /><br />");
        response.getWriter().println("<input type='submit' value='Say it!' />");
        response.getWriter().println("</form>");
        response.getWriter().println("<p><a href='/'>" + "Go back home..." + "</a></p>");
        response.getWriter().println(HtmlHelper.getFooter());
    }
}
