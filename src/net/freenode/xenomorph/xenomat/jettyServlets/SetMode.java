/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.freenode.xenomorph.xenomat.jettyServlets;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

public class SetMode extends HttpServlet {

    private PircBotX _bot;
    VelocityContext _vContext;

    public SetMode(PircBotX bot) {
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
        if (!SessionHelper.isAdmin(request)) {
            Template template = null;
            template = Velocity.getTemplate("htmlTemplates/errorNotAuthenticated.html");
            StringWriter sw = new StringWriter();
            template.merge(_vContext, sw);
            response.getWriter().print(sw.toString());
            return;
        }

        _vContext.put("title", _bot.getNick() + " - Administration");

        ArrayList<String> channelsUsers = new ArrayList<>();
        for (Channel c : _bot.getUserBot().getChannels()) {
            if (c.isOp(_bot.getUserBot())) {
                for (User u : c.getUsers()) {
                    if (!u.equals(_bot.getUserBot())) {
                        channelsUsers.add(c.getName() + " - " + u.getNick());
                    }
                }
            }
        }
        _vContext.put("channelsUsers", channelsUsers);

        if (request.getParameter("channelUser") != null && !request.getParameter("channelUser").isEmpty()) {
            String[] channelUser = request.getParameter("channelUser").split("\\s-\\s");
            String targetChannelString = channelUser[0];
            String targetUserString = channelUser[1];
            Channel targetChannel = _bot.getUserChannelDao().getChannel(targetChannelString);
            if (targetChannel != null) {
                User targetUser = _bot.getUserChannelDao().getUser(targetUserString);
                if (targetChannel.isOp(_bot.getUserBot())) {
                    if (request.getParameter("usermodeOP") != null && request.getParameter("usermodeOP").equals("yes")) {
                        if (!targetChannel.isOp(targetUser)) {
                            targetChannel.send().op(targetUser);
                        }
                    } else {
                        if (targetChannel.isOp(targetUser)) {
                            targetChannel.send().deOp(targetUser);
                        }
                    }
                    if (request.getParameter("usermodeVOICE") != null && request.getParameter("usermodeVOICE").equals("yes")) {
                        if (!targetChannel.hasVoice(targetUser)) {
                            targetChannel.send().voice(targetUser);
                        }
                    } else {
                        if (targetChannel.hasVoice(targetUser)) {
                            targetChannel.send().deVoice(targetUser);
                        }
                    }
                    _vContext.put("notice", "Done.");
                } else {
                    _vContext.put("notice", "Sorry, can't fullfill your request - I'm not admin on that channel.");
                }
            }else{
                _vContext.put("notice", "Sorry, can't fullfill your request - There was an error regarding the channel.");
            }
        }

        Template template = null;
        template = Velocity.getTemplate("htmlTemplates/setMode.html");
        StringWriter sw = new StringWriter();
        template.merge(_vContext, sw);
        response.getWriter().print(sw.toString());
    }
}
