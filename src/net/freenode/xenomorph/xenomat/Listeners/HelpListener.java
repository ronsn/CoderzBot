package net.freenode.xenomorph.xenomat.Listeners;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public class HelpListener extends ListenerAdapter {

    private String _helpCmd = "!help";

    @Override
    public void onPrivateMessage(PrivateMessageEvent event) throws Exception {
        if (event.getMessage().trim().equals(_helpCmd)) {

        }
    }
}