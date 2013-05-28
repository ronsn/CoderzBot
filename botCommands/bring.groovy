import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import net.freenode.xenomorph.xenomat.CommandResponse

public class bring implements net.freenode.xenomorph.xenomat.botCommand {

    public CommandResponse onCommand(String channel, String sender, String[] params, long commandLastUsedAt) {
    	ArrayList<String> rt = new ArrayList<String>()
        Boolean success = false
        def drinks = ["Kaffee":"einen heißen Kaffee", "Tee":"einen frisch gebrühten Tee", "Wasser":"ein Glas kühles Wasser", "Cola":"ein großes Glas hausgemachte Cola", "Limonade":"ein Glas leckere Limonade", "Kakao":"eine Tasse heißen, schokoladigen Kakao", "Cappucino":"eine Tasse perfekten Cappucino"]
        if(params.length != 2 || !drinks.containsKey(params[1])){
            rt.add("Ich konnte Deine Bestellung nicht verarbeiten, "+sender+", benutze: !bring Zielperson Getränk")
            def drinklist = drinks.keySet() as String[];
            rt.add("Wir haben folgende Getränke auf der Karte: "+drinklist.join(" "))
        }
        else{
            success = true
            rt.add("/me serviert "+params[0]+" "+drinks.get(params[1])+".")
        }
        CommandResponse cmdr = new CommandResponse(rt,success)
    }
}