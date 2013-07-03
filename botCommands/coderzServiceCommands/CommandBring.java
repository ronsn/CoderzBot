package coderzServiceCommands;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import net.freenode.xenomorph.xenomat.CommandResponse;
import net.freenode.xenomorph.xenomat.botCommand;

public class CommandBring implements botCommand {

    @Override
    public String getCommandName() {
        return "bring";
    }

    @Override
    public CommandResponse onCommand(String sender, String[] params, long commandLastUsedAt, ArrayList<String> knownUsers, Object savedData) {
        Boolean success = false;
        ArrayList<String> rt = new ArrayList<>();
        HashMap<String, String> menu = new HashMap<>();
        try (Scanner scanner = new Scanner(Paths.get("botCommands/conf/CommandBringItems.txt"), "UTF-8")) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] item = line.split(";");
                if (item.length == 2) {
                    menu.put(item[0].trim(), item[1].trim());
                }
            }
        } catch (IOException ex) {
        }

        if (menu.isEmpty()) {
            rt.add("Leider ist der Lieferservice aus technischen Gründen heute geschlossen.");
            return new CommandResponse(rt, false, null);
        }

        if (params.length == 1) {
            // If no one is mentioned, give stuff to all channel users.
            params = new String[]{"", params[0]};
        }

        if (params.length != 2 || !menu.containsKey(params[1].toLowerCase())) {
            rt.add("Ich konnte Deine Bestellung nicht verarbeiten, " + sender + ", benutze: !bring Zielperson Sache");
            StringBuilder menulist = new StringBuilder();
            for (Map.Entry<String, String> d : menu.entrySet()) {
                menulist.append(capitalize(d.getKey()) + ", ");

            }
            String menuListString = menulist.toString().trim();
            rt.add("Wir haben folgende Dinge auf der Karte: " + menuListString.substring(0, menuListString.length() - 1) + ".");
        } else {
            if (knownUsers != null && !knownUsers.isEmpty() && knownUsers.contains(params[0])) {
                success = true;
                if (!sender.equals(params[0])) {
                    rt.add("/me serviert " + params[0] + " " + menu.get(params[1].toLowerCase()) + ". Mit freundlichen Grüßen von " + sender + "!");
                } else {
                    rt.add("/me serviert " + params[0] + " " + menu.get(params[1].toLowerCase()) + ".");
                }
            } else if (params[0].isEmpty()) {
                success = true;
                rt.add("/me serviert allen " + menu.get(params[1].toLowerCase()) + ".");
            } else {
                rt.add("Ich kenne \"" + params[0] + "\" nicht, tut mir leid.");
            }
        }
        return new CommandResponse(rt, success, null);
    }

    private String capitalize(String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }
}
