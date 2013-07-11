/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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

public class QuitServlet extends HttpServlet {

    private PircBotX _bot;
    VelocityContext _vContext;

    public QuitServlet(PircBotX bot) {
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
        if (request.getParameter("shutdown") != null && request.getParameter("shutdown").equals("do")) {
            _bot.shutdown(true);
        } else {
            response.sendRedirect("/");
        }
    }
}
