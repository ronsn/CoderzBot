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

public class SayHandler extends AbstractHandler {

    private PircBotX _bot;

    public SayHandler(PircBotX bot) {
        _bot = bot;
    }

    @Override
    public void handle(String string, Request baseRequest, HttpServletRequest hsr, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        String pass = "";
        if (hsr.getParameter("mode") != null && hsr.getParameter("mode").equals("say") && hsr.getParameter("msg") != null && hsr.getParameter("channel") != null) {
            if (hsr.getParameter("pass") != null) {
                if (hsr.getParameter("pass").equals("Zejgel94")) {
                    _bot.sendMessage(_bot.getChannel(hsr.getParameter("channel")), hsr.getParameter("msg"));
                } else {
                    response.getWriter().println("<h1>" + "Wrong password!" + "</h1>");
                    response.getWriter().println("<h2>" + "I've got:" + hsr.getParameter("pass") + "</h2>");
                    response.getWriter().println("<p><a href=\"/\">" + "get back..." + "</a></p>");
                    pass = hsr.getParameter("pass");
                }
            } else {
                response.getWriter().println("<h1>" + "Password was null!" + "</h1>");
                response.getWriter().println("<p><a href=\"/\">" + "get back..." + "</a></p>");
            }
        }
        response.getWriter().println("<hr />");
        response.getWriter().println("<form action='/say/' method='POST'>");
        response.getWriter().println("<input type='hidden' name='mode' value='say' />");
        response.getWriter().println("Password: <input type='password' name='pass' value='" + pass + "' /><br />");
        response.getWriter().println("Message: <input type='text' name='msg' /><br />");
        response.getWriter().println("Channel: <select name='channel' size='1'>");
        for (Channel chan : _bot.getChannels()) {
            response.getWriter().println("<option>" + chan.getName() + "</option>");
        }
        response.getWriter().println("</select><br />");
        response.getWriter().println("<input type='submit' value='Say it!' />");
        response.getWriter().println("</form>");
    }
}
