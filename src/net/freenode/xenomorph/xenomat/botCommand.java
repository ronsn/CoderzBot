package net.freenode.xenomorph.xenomat;

public interface botCommand {

    CommandResponse onCommand(String channel, String sender, String[] params, long commandLastUsedAt);

}
