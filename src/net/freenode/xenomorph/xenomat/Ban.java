package net.freenode.xenomorph.xenomat;

import org.pircbotx.Channel;
import org.pircbotx.User;

public class Ban {

    private String _banEntry;
    private Channel _channel;
    private long _banDuration;
    private long _timeOfBan;
    private User _u;

    public Ban(String banEntry, Channel channel, long banDuration, long timeOfBan, User u) {
        _banEntry = banEntry;
        _banDuration = banDuration;
        _timeOfBan = timeOfBan;
        _channel = channel;
        _u = u;
    }

    /**
     * @return the _banEntry
     */
    public String getBanEntry() {
        return _banEntry;
    }

    /**
     * @param banEntry the _banEntry to set
     */
    public void setBanEntry(String banEntry) {
        this._banEntry = banEntry;
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

    /**
     * @return the _banDuration
     */
    public long getBanDuration() {
        return _banDuration;
    }

    /**
     * @param banDuration the _banDuration to set
     */
    public void setBanDuration(long banDuration) {
        this._banDuration = banDuration;
    }

    /**
     * @return the _timeOfBan
     */
    public long getTimeOfBan() {
        return _timeOfBan;
    }

    /**
     * @param timeOfBan the _timeOfBan to set
     */
    public void setTimeOfBan(long timeOfBan) {
        this._timeOfBan = timeOfBan;
    }

    public User getUser() {
        return _u;
    }
}
