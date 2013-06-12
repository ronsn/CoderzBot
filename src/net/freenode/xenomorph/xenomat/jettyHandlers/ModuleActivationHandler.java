/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.freenode.xenomorph.xenomat.jettyHandlers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.freenode.xenomorph.xenomat.Listeners.GrammarListener;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.pircbotx.PircBotX;

public class ModuleActivationHandler extends AbstractHandler {

    private GrammarListener _gl;
    private PircBotX _bot;
    private final String _adminPass;

    public ModuleActivationHandler(PircBotX bot, GrammarListener gl, String adminPass) {
        _gl = gl;
        _bot = bot;
        _adminPass = adminPass;
    }

    @Override
    public void handle(String string, Request baseRequest, HttpServletRequest hsr, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().println(HtmlHelper.getHeader(_bot.getNick() + " - Switch modules on and off"));
        if (hsr.getParameter("mode") != null && hsr.getParameter("mode").equals("toggleGrammar") && hsr.getParameter("pass") != null && hsr.getParameter("pass").equals(_adminPass)) {
            if (_gl.isGrammarCheckActive()) {
                _gl.setGrammarCheckActive(false);
            } else {
                _gl.setGrammarCheckActive(true);
            }
        }
        response.getWriter().println("<h1>The grammar check module is: " + (_gl.isGrammarCheckActive() ? "<span style='color:green'>Active</span>" : "<span style='color:red'>Inactive</span>!"));
        response.getWriter().println("<form action='/moduleactivation/' method='POST'><input type='hidden' name='mode' value='toggleGrammar' /><input type='submit' value='Change with admin pass: ' /><input type='password' name='pass' value='" + ((hsr.getParameter("pass") != null && hsr.getParameter("pass").equals(_adminPass)) ? _adminPass : "") + "' /></form>");
        response.getWriter().println("</h1>");
        response.getWriter().println("<p><a href='/'>" + "Go back home..." + "</a></p>");
        response.getWriter().println(HtmlHelper.getFooter());
    }
}
