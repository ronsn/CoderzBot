import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar

public class HelloWorld implements net.freenode.xenomorph.xenomat.botCommand {

    public String onCommand(String channel, String sender, String[] params, long commandLastUsedAt) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
		Calendar cal = Calendar.getInstance()
        return "Hello World! er... Hello " + sender+"! "+dateFormat.format(cal.getTime())
    }
}