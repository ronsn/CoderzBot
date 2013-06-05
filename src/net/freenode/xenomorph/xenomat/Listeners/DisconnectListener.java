package net.freenode.xenomorph.xenomat.Listeners;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.DisconnectEvent;

/**
 * If disconnected from server, a reconnect attempt is made every 3 minutes
 */
public class DisconnectListener extends ListenerAdapter {

    @Override
    public void onDisconnect(DisconnectEvent event) {
        while (!event.getBot().isConnected()) {
            Logger.getLogger(DisconnectListener.class.getName()).log(Level.WARNING, null, "Connection lost. Trying to reconnect...");
            try {
                event.getBot().reconnect();
                Thread.sleep(3 * 60 * 1000);
            } catch (IOException | IrcException | InterruptedException ex) {
                Logger.getLogger(DisconnectListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
