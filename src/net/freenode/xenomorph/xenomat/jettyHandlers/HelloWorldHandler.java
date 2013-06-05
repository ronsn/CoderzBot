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
import org.pircbotx.PircBotX;

public class HelloWorldHandler extends AbstractHandler {

    private PircBotX _bot;

    public HelloWorldHandler(PircBotX bot) {
        _bot = bot;
    }

    @Override
    public void handle(String string, Request baseRequest, HttpServletRequest hsr, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().println("<h1>" + "Hello!" + "</h1>");
        response.getWriter().println("<h2>" + "You are about to administer the bot: " + _bot.getNick() + "</h2>");
        response.getWriter().println("<h2>" + "Want to shut down the server?" + "</h2>");
        response.getWriter().println("<form action='/quit/' method='POST'>");
        response.getWriter().println("Password: <input type='text' name='pass' />");
        response.getWriter().println("<input type='submit' value='Shutdown' />");
        response.getWriter().println("</form>");
        response.getWriter().println("<h2>Or maybe do you want to...</h2>");
        response.getWriter().println("<ul>");
        response.getWriter().println("<li><a href='/say/'>Let the bot say something...</a></li>");
        response.getWriter().println("</ul>");
    }
}
