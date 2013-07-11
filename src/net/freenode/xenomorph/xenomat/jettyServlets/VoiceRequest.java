/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.freenode.xenomorph.xenomat.jettyServlets;

import org.pircbotx.Channel;
import org.pircbotx.User;

public class VoiceRequest {

    private boolean _isdenied;
    private User _user;
    private Channel _channel;

    public VoiceRequest(Boolean isdenied, User user, Channel channel) {
        _isdenied = isdenied;
        _user = user;
        _channel = channel;
    }

    /**
     * @return the _isdenied
     */
    public boolean isIsdenied() {
        return _isdenied;
    }

    /**
     * @param isdenied the _isdenied to set
     */
    public void setIsdenied(boolean isdenied) {
        this._isdenied = isdenied;
    }

    /**
     * @return the _user
     */
    public User getUser() {
        return _user;
    }

    /**
     * @param user the _user to set
     */
    public void setUser(User user) {
        this._user = user;
    }

    /**
     * @return the _channel
     */
    public Channel getChannel() {
        return _channel;
    }

    /**
     * @param channel the _channel to set
     */
    public void setChannel(Channel channel) {
        this._channel = channel;
    }
}
