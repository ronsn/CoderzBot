package coderzServiceCommands;


import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import net.freenode.xenomorph.xenomat.CommandResponse;
import net.freenode.xenomorph.xenomat.botCommand;

public class CommandAsk implements botCommand {

    private ArrayList<String> answers;

    public CommandAsk() {
        answers = new ArrayList<>();
        answers.add("Wie es aussieht ist die Antwort ja.");
        answers.add("Ich kann es sehen: Alles wendet sich zum Guten.");
        answers.add("Aber natürlich!");
        answers.add("Es gibt keinen Zweifel: Ja.");
        answers.add("Ja!");
        answers.add("Manche irren sich, ich nicht. Die Antwort lautet: Auf jeden Fall!");
        answers.add("Na klar.");
        answers.add("Du wirst sehen: Ja!");
        answers.add("Wieso nicht? Ja, klar!");
        answers.add("Dass Du das fragen musst... Die Antwort lautet natürlich ja!");

        answers.add("Ich bin mir nicht sicher. Es ist alles verschwommen.");
        answers.add("Meine Glaskugel hat einen Kratzer. Das muss ich erst wegpolieren, sorry.");
        answers.add("Ja! Oder... Moment... Nein! Oder Ja? Oder Nein!? Ich kann es grad nicht sehen.");
        answers.add("Frag später nochmal. Irgendwann.");
        answers.add("Du äh, jetzt ehrlich, so spontan hab ich auch keine Ahnung.");

        answers.add("Vergiss es, Nein.");
        answers.add("Nein.");
        answers.add("Auf keinen Fall.");
        answers.add("Niemals.");
        answers.add("Wie kommst Du denn auf die Frage? Nein!");
    }

    @Override
    public String getCommandName() {
        return "ask";
    }

    ;

    @Override
    public CommandResponse onCommand(String sender, String[] params, long commandLastUsedAt, ArrayList<String> knownUsers, Object savedData) {
        ArrayList<String> responsetext = new ArrayList<>();
        Boolean success = false;
        Integer timeBetweenBeers = 2;
        if (commandLastUsedAt > -1 && (System.currentTimeMillis() - commandLastUsedAt) <= (timeBetweenBeers * 60 * 1000)) {
            long millis = (timeBetweenBeers * 60 * 1000 - (System.currentTimeMillis() - commandLastUsedAt));
            String tRemaining = String.format("%d Minuten, %d Sekunden",
                    TimeUnit.MILLISECONDS.toMinutes(millis),
                    TimeUnit.MILLISECONDS.toSeconds(millis)
                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            responsetext.add("Meine Visionen zu Deiner Person ordnen sich gerade noch, " + sender + ". Vielleicht kann ich in " + tRemaining + " wieder etwas sehen.");
        } else {
            Random randomGenerator = new Random();
            int index = randomGenerator.nextInt(answers.size());
            responsetext.add(answers.get(index));
            success = true;
        }
        return new CommandResponse(responsetext, success, null);
    }

    private String capitalize(String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }
}
