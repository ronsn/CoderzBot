import java.util.concurrent.TimeUnit
import java.util.ArrayList;
import net.freenode.xenomorph.xenomat.CommandResponse

public class beer implements net.freenode.xenomorph.xenomat.botCommand {

    public CommandResponse onCommand(String channel, String sender, String[] params, long commandLastUsedAt) {
        ArrayList<String> responsetext = new ArrayList<String>();
        Boolean success = false;
        Integer timeBetweenBeers = 5
        if(commandLastUsedAt > -1 && (System.currentTimeMillis() - commandLastUsedAt) <= (timeBetweenBeers * 60 * 1000)){
            long millis = (timeBetweenBeers * 60 * 1000 - (System.currentTimeMillis() - commandLastUsedAt))
            String tRemaining = String.format("%d Minuten, %d Sekunden",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)))
            responsetext.add("Du hattest gerade schon ein Bier, " + sender + ". Frag in " + tRemaining + " wieder.")
        }
        else{
            responsetext.add("/me schiebt " + sender + " ein kühles Bier rüber.")
            success = true;
        }
        CommandResponse cmdr = new CommandResponse(responsetext,success)
    }
}