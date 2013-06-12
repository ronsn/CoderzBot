/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.freenode.xenomorph.xenomat.jettyHandlers;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        response.getWriter().println(HtmlHelper.getHeader( _bot.getNick() + " - Administration"));
        if (hsr.getParameter("pass") != null) {
            if (hsr.getParameter("pass").equals(_adminPass)) {
                _bot.shutdown(true);
                try {
                    this.stop();
                } catch (Exception ex) {
                    Logger.getLogger(QuitHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
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
        response.getWriter().println(HtmlHelper.getFooter());
    }
}
