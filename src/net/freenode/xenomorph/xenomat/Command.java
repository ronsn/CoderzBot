package net.freenode.xenomorph.xenomat;

public class Command {

    private String _commandName;
    private Long lastUsedAt;

    /**
     * @return the _commandName
     */
    public String getCommandName() {
        return _commandName;
    }

    /**
     * @param commandName the _commandName to set
     */
    public void setCommandName(String commandName) {
        this._commandName = commandName;
    }

    /**
     * @return the lastUsedAt
     */
    public Long getLastUsedAt() {
        return lastUsedAt;
    }

    /**
     * @param lastUsedAt the lastUsedAt to set
     */
    public void setLastUsedAt(Long lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }
}
