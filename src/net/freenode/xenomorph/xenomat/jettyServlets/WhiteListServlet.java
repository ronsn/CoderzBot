/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.freenode.xenomorph.xenomat.jettyServlets;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.pircbotx.PircBotX;

public class WhiteListServlet extends HttpServlet {

    private PircBotX _bot;
    VelocityContext _vContext;
    private final ArrayList<String> _whiteList;

    public WhiteListServlet(PircBotX bot, ArrayList<String> whiteList) {
        _bot = bot;
        _vContext = new VelocityContext();
        _whiteList = whiteList;
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

        if (request.getParameter("whitelist") != null && !request.getParameter("whitelist").isEmpty()) {
            String[] whiteListTemp = request.getParameter("whitelist").split("\n");
            synchronized (_whiteList) {
                _whiteList.clear();
                for (String entry : whiteListTemp) {
                    _whiteList.add(entry);
                }
                File file = new java.io.File("whitelist.txt");
                if (file.exists()) {
                    file.delete();
                }
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("whitelist.txt"))) {
                    writer.write(request.getParameter("whitelist"));
                }
            }
        }

        _vContext.put("title", _bot.getNick() + " - Administration");
        StringBuilder text = new StringBuilder();
        synchronized (_whiteList) {
            for (String nick : _whiteList) {
                text.append(nick).append('\n');
            }
        }
        _vContext.put("whitelist", text);
        Template template = null;
        template = Velocity.getTemplate("htmlTemplates/whiteList.html");
        StringWriter sw = new StringWriter();
        template.merge(_vContext, sw);
        response.getWriter().print(sw.toString());

    }
}
