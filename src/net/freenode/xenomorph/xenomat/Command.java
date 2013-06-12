package net.freenode.xenomorph.xenomat;

public class Command {

    private String _commandName;
    private Long lastUsedAt;
    private Object _saveData;

    public Command(String commandName, Object saveData){
        _saveData = saveData;
        _commandName = commandName;
        lastUsedAt = System.currentTimeMillis();
    }

    /**
     * @return the _commandName
     */
    public String getCommandName() {
        return _commandName;
    }
    /**
     * @return the lastUsedAt
     */
    public Long getLastUsedAt() {
        return lastUsedAt;
    }

    /**
     * @return the _saveData
     */
    public Object getSaveData() {
        return _saveData;
    }

    /**
     * @param saveData the _saveData to set
     */
    public void setSaveData(Object saveData) {
        this._saveData = saveData;
    }
}
