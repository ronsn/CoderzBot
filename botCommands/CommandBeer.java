
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import net.freenode.xenomorph.xenomat.CommandResponse;
import net.freenode.xenomorph.xenomat.botCommand;

public class CommandBeer implements botCommand {

    @Override
    public String getCommandName() {
        return "beer";
    }

    ;

    @Override
    public CommandResponse onCommand(String sender, String[] params, long commandLastUsedAt, ArrayList<String> knownUsers) {
        ArrayList<String> responsetext = new ArrayList<String>();
        Boolean success = false;
        Integer timeBetweenBeers = 5;
        if (commandLastUsedAt > -1 && (System.currentTimeMillis() - commandLastUsedAt) <= (timeBetweenBeers * 60 * 1000)) {
            long millis = (timeBetweenBeers * 60 * 1000 - (System.currentTimeMillis() - commandLastUsedAt));
            String tRemaining = String.format("%d Minuten, %d Sekunden",
                    TimeUnit.MILLISECONDS.toMinutes(millis),
                    TimeUnit.MILLISECONDS.toSeconds(millis)
                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            responsetext.add("Du hattest gerade schon ein Bier, " + sender + ". Frag in " + tRemaining + " wieder.");
        } else {
            responsetext.add("/me schiebt " + sender + " ein kühles Bier rüber.");
            success = true;
        }
        return new CommandResponse(responsetext, success);
    }

    private String capitalize(String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }
}
