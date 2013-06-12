package net.freenode.xenomorph.xenomat.jettyHandlers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.pircbotx.PircBotX;

public class GrammarLogHandler extends AbstractHandler {

    private PircBotX _bot;

    public GrammarLogHandler(PircBotX bot) {
        _bot = bot;
    }

    @Override
    public void handle(String string, Request baseRequest, HttpServletRequest hsr, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().println(HtmlHelper.getHeader(_bot.getNick() + " - Grammar log"));
        response.getWriter().println("<h1>Those users made it through the grammar module:</h1>");
        response.getWriter().println("<a href='/'>Get back home...</a>");
        response.getWriter().println("<ul>");
        response.getWriter().println("<li><i>...disabled atm, sorry...</i></li>");
//        for (GrammarTestPassed gtp : gl.getAll()) {
//            response.getWriter().println("<li>");
//            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyy HH:mm:ss");
//            Calendar cal = Calendar.getInstance();
//            cal.setTimeInMillis(gtp.getTimestamp());
//            response.getWriter().println(dateFormat.format(cal.getTime()) + ": <b>");
//            response.getWriter().println(gtp.getU().getNick() + "</b> with sentence <i>");
//            response.getWriter().println(gtp.getU().getCheckSentence().getCorrectSentence() + "</i>");
//            response.getWriter().println("</li>");
//        }
        response.getWriter().println(HtmlHelper.getFooter());
    }
}
