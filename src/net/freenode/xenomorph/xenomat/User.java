package net.freenode.xenomorph.xenomat;

public class User {

    private String _nick;
    private String _login;
    private String _host;
    private String _channel;
    private int _warned;
    private int _grammarFloodLimitCount;
    private CheckSentence _grammarCheck;
    private long _pendingSince;

    /**
     *
     * @param nick NickName
     * @param login LoginName
     * @param host Host
     * @param channel Channel
     * @param pendingSince PendingSince
     */
    public User(String nick, String login, String host, String channel, long pendingSince) {
        _nick = nick;
        _login = login;
        _host = host;
        _channel = channel;
        _pendingSince = pendingSince;
    }

    /**
     * @return the _nick
     */
    public String getNick() {
        return _nick;
    }

    /**
     * @param nick the _nick to set
     */
    public void setNick(String nick) {
        this._nick = nick;
    }

    /**
     * @return the _login
     */
    public String getLogin() {
        return _login;
    }

    /**
     * @param login the _login to set
     */
    public void setLogin(String login) {
        this._login = login;
    }

    /**
     * @return the _host
     */
    public String getHost() {
        return _host;
    }

    /**
     * @param host the _host to set
     */
    public void setHost(String host) {
        this._host = host;
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
     * @return the _warned
     */
    public int getWarned() {
        return _warned;
    }

    /**
     * @param warned the _warned to set
     */
    public void setWarned(int warned) {
        this._warned = warned;
    }

    /**
     * @return the grammarFloodLimitCount
     */
    public int getGrammarFloodLimitCount() {
        return _grammarFloodLimitCount;
    }

    /**
     * @param grammarFloodLimitCount the grammarFloodLimitCount to set
     */
    public void setGrammarFloodLimitCount(int grammarFloodLimitCount) {
        this._grammarFloodLimitCount = grammarFloodLimitCount;
    }

    /**
     * @return the _data
     */
    public CheckSentence getCheckSentence() {
        return _grammarCheck;
    }

    /**
     * @param data the _data to set
     */
    public void setCheckSentence(CheckSentence sentence) {
        this._grammarCheck = sentence;
    }

    /**
     * @return the _pendingSince
     */
    public long getPendingSince() {
        return _pendingSince;
    }

    /**
     * @param pendingSince the _pendingSince to set
     */
    public void setPendingSince(long pendingSince) {
        this._pendingSince = pendingSince;
    }
}
