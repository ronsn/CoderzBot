
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.freenode.xenomorph.xenomat.CommandResponse;
import net.freenode.xenomorph.xenomat.botCommand;

public class CommandBring implements botCommand {

    public String getCommandName() {
        return "bring";
    }

    ;

    @Override
    public CommandResponse onCommand(String sender, String[] params, long commandLastUsedAt, ArrayList<String> knownUsers) {
        Boolean success = false;
        ArrayList<String> rt = new ArrayList<>();
        HashMap<String, String> drinks = new HashMap<>();
        drinks.put("kaffee", "einen heißen Kaffee");
        drinks.put("tee", "einen frisch gebrühten Tee");
        drinks.put("cola", "ein großes Glas kalte Cola");
        drinks.put("limo", "ein Glas leckere Limonade");
        drinks.put("kakao", "eine Tasse heißen, schokoladigen Kakao");
        drinks.put("cappucino", "eine Tasse perfekten Cappucino");

        if(params.length == 1){
            // If no one is mentioned, give stuff to all channel users.
            params = new String[]{"",params[0]};
        }

        if (params.length != 2 || !drinks.containsKey(params[1].toLowerCase())) {
            rt.add("Ich konnte Deine Bestellung nicht verarbeiten, " + sender + ", benutze: !bring Zielperson Getränk");
            StringBuilder drinklist = new StringBuilder();
            for (Map.Entry<String, String> d : drinks.entrySet()) {
                drinklist.append(capitalize(d.getKey()) + " ");

            }

            rt.add("Wir haben folgende Getränke auf der Karte: " + drinklist.toString().trim());
        } else {
            if (knownUsers != null && !knownUsers.isEmpty() && knownUsers.contains(params[0])) {
                success = true;
                rt.add("/me serviert " + params[0] + " " + drinks.get(params[1].toLowerCase()) + ".");
            }else if(params[0].isEmpty()){
                success = true;
                rt.add("/me serviert allen " + drinks.get(params[1].toLowerCase()) + ".");
            }else{
                rt.add("Ich kenne \""+params[0]+"\" nicht, tut mir leid.");
            }
        }
        return new CommandResponse(rt, success);
    }

    private String capitalize(String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }
}
