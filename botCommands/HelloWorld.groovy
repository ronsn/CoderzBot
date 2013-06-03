import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import net.freenode.xenomorph.xenomat.CommandResponse

public class HelloWorld implements net.freenode.xenomorph.xenomat.botCommand {

    public CommandResponse onCommand(String sender, String[] params, long commandLastUsedAt) {
    	ArrayList<String> responsetext = new ArrayList<String>()
        Boolean success = true
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyy HH:mm:ss")
        Calendar cal = Calendar.getInstance()
        responsetext.add("Hallo " + sender+"! Suchst Du Orientierung? Hier, bitteschön: "+dateFormat.format(cal.getTime()))
        CommandResponse cmdr = new CommandResponse(responsetext,success)
    }
}