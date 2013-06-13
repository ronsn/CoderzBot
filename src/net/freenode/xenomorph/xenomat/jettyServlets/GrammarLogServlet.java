package net.freenode.xenomorph.xenomat.jettyServlets;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.pircbotx.PircBotX;

public class GrammarLogServlet extends HttpServlet {

    private PircBotX _bot;

    public GrammarLogServlet(PircBotX bot) {
        _bot = bot;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request, response);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
