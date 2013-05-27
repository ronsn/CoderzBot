package net.freenode.xenomorph.xenomat;

import java.util.ArrayList;

public class CommandResponse {

    private ArrayList<String> _responseText;
    private Boolean _commandSuccesfull;

    public CommandResponse(ArrayList<String> responseText, Boolean commandSuccesfull) {
        _responseText = responseText;
        _commandSuccesfull = commandSuccesfull;

    }

    /**
     * @return the _responseText
     */
    public ArrayList<String> getResponseText() {
        return _responseText;
    }

    /**
     * @param responseText the _responseText to set
     */
    public void setResponseText(ArrayList<String> responseText) {
        this._responseText = responseText;
    }

    /**
     * @return the _commandSuccesfull
     */
    public Boolean getCommandSuccesfull() {
        return _commandSuccesfull;
    }

    /**
     * @param commandSuccesfull the _commandSuccesfull to set
     */
    public void setCommandSuccesfull(Boolean commandSuccesfull) {
        this._commandSuccesfull = commandSuccesfull;
    }
}
