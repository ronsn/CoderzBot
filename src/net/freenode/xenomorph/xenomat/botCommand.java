package net.freenode.xenomorph.xenomat;

public interface botCommand {

    CommandResponse onCommand(String sender, String[] params, long commandLastUsedAt);

}
