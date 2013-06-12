package net.freenode.xenomorph.xenomat;

import java.util.ArrayList;

public interface botCommand {

    String getCommandName();

    CommandResponse onCommand(String sender, String[] params, long commandLastUsedAt,ArrayList<String> knownUsers);
}
