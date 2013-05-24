package net.freenode.xenomorph.xenomat;

public class Ban {

    private String _banEntry;
    private String _channel;
    private long _banDuration;
    private long _timeOfBan;

    public Ban(String banEntry, String channel, long banDuration, long timeOfBan) {
        _banEntry = banEntry;
        _banDuration = banDuration;
        _timeOfBan = timeOfBan;
        _channel = channel;
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
    public String getChannel() {
        return _channel;
    }

    /**
     * @param channel the _channel to set
     */
    public void setChannel(String channel) {
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
}
