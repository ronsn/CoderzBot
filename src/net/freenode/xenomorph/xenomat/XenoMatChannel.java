package net.freenode.xenomorph.xenomat;

import org.pircbotx.Channel;

public class XenoMatChannel {

    private String _channelName;
    private boolean _channelMuted;
    private long _mutedSince;
    private Channel _channel;

    public XenoMatChannel(String name, boolean muted, Channel channel) {
        _channelName = name;
        _channelMuted = muted;
        _channel = channel;
        if (_channelMuted == true) {
            _mutedSince = System.currentTimeMillis();
        } else {
            _mutedSince = 0;
        }
    }

    /**
     * @return the _channelName
     */
    public String getChannelName() {
        return _channelName;
    }

    /**
     * @param channelName the _channelName to set
     */
    public void setChannelName(String channelName) {
        this._channelName = channelName;
    }

    /**
     * @return the _channelMuted
     */
    public boolean getChannelMuted() {
        return _channelMuted;
    }

    /**
     * @param channelMuted the _channelMuted to set
     */
    public void setChannelMuted(boolean channelMuted) {
        this._channelMuted = channelMuted;
        if (_channelMuted == true) {
            _mutedSince = System.currentTimeMillis();
        } else {
            _mutedSince = 0;
        }
    }

    /**
     * @return the _mutedSince
     */
    public long getMutedSince() {
        return _mutedSince;
    }

    /**
     * @return the _channel
     */
    public Channel getChannel() {
        return _channel;
    }
}
