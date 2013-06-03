package net.freenode.xenomorph.xenomat;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public class XenoMatUser {

    private String _nick;
    private String _login;
    private String _host;
    private String _channelName;
    private int _warned;
    private int _grammarFloodLimitCount;
    private CheckSentence _grammarCheck;
    private long _pendingSince;
    private MessageEvent _msgEv;
    private PrivateMessageEvent _pMsgEv;
    private JoinEvent _jEv;

    public XenoMatUser(String nick, String login, String host, String channelName, long pendingSince, MessageEvent msgEv) {
        _nick = nick;
        _login = login;
        _host = host;
        _channelName = channelName;
        _pendingSince = pendingSince;
        _pMsgEv = null;
        _msgEv = msgEv;
        _jEv=null;
    }

    public XenoMatUser(String nick, String login, String host, long pendingSince, PrivateMessageEvent pMsgEv) {
        _nick = nick;
        _login = login;
        _host = host;
        _channelName = "";
        _pendingSince = pendingSince;
        _pMsgEv = pMsgEv;
        _msgEv = null;
        _jEv=null;
    }

    public XenoMatUser(String nick, String login, String host, String channelName, long pendingSince, JoinEvent jEv) {
        _nick = nick;
        _login = login;
        _host = host;
        _channelName = channelName;
        _pendingSince = pendingSince;
        _pMsgEv = null;
        _msgEv = null;
        _jEv=jEv;
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

    /**
     * @return the _msgEv
     */
    public MessageEvent getMsgEv() {
        return _msgEv;
    }

    /**
     * @return the _pMsgEv
     */
    public PrivateMessageEvent getpMsgEv() {
        return _pMsgEv;
    }

    /**
     * @return the _jEv
     */
    public JoinEvent getjEv() {
        return _jEv;
    }

    public User getUser(){
        if(_msgEv != null){
            return _msgEv.getUser();
        }
        else if(_pMsgEv != null){
            return _pMsgEv.getUser();
        }
        else{
            return _jEv.getUser();
        }
    }

    public PircBotX getBot(){
        if(_msgEv != null){
            return _msgEv.getBot();
        }
        else if(_pMsgEv != null){
            return _pMsgEv.getBot();
        }
        else{
            return _jEv.getBot();
        }
    }
}
