package net.freenode.xenomorph.xenomat;

public interface botCommand {

    String getCommandName();

    CommandResponse onCommand(String sender, String[] params, long commandLastUsedAt);
}
