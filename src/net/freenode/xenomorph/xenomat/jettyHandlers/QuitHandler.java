/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.freenode.xenomorph.xenomat.jettyHandlers;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.pircbotx.PircBotX;

public class QuitHandler extends AbstractHandler {

    private PircBotX _bot;
    private String _adminPass;

    public QuitHandler(PircBotX bot, String adminPass) {
        _bot = bot;
        _adminPass = adminPass;
    }

    @Override
    public void handle(String string, Request baseRequest, HttpServletRequest hsr, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        if (hsr.getParameter("pass") != null) {
            if (hsr.getParameter("pass").equals(_adminPass)) {
                System.exit(0);
            } else {
                response.getWriter().println("<h1>" + "Wrong password!" + "</h1>");
                response.getWriter().println("<h2>" + "I've got:" + hsr.getParameter("pass") + "</h2>");
                response.getWriter().println("<p><a href=\"/\">" + "get back..." + "</a></p>");
            }
        } else {
            response.getWriter().println("<h1>" + "Password was null!" + "</h1>");
            response.getWriter().println("<p><a href=\"/\">" + "get back..." + "</a></p>");
        }
    }
}
