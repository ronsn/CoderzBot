
import java.util.ArrayList;
import net.freenode.xenomorph.xenomat.CommandResponse;
import net.freenode.xenomorph.xenomat.botCommand;

public class CommandMemo implements botCommand {

    @Override
    public String getCommandName() {
        return "memo";
    }

    ;

    @Override
    public CommandResponse onCommand(String sender, String[] params, long commandLastUsedAt, ArrayList<String> knownUsers, Object savedData) {
        ArrayList<String> rt = new ArrayList<>();
        Boolean success = false;
        Memo m = null;
        if (params.length > 0 && params[0] != null && params[0].equals("get")) {
            if (savedData != null && savedData instanceof Memo) {
                m = (Memo) savedData;
                if (m.getMemotext() != null && !m.getMemotext().isEmpty()) {
                    rt.add("Das Memo lautet: \"" + m.getMemotext() + "\"");
                } else {
                    rt.add("Es gab einen Fehler, ich konnte das Memo nicht finden.");
                }
            } else {
                rt.add("Ich habe kein Memo für Dich gespeichert, sorry.");
            }
        } else if (params.length > 0 && params[0] != null && params[0].equals("save") && params.length > 1) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < params.length; i++) {
                sb.append(params[i] + " ");
            }
            String memotext = sb.toString().trim();
            m = new Memo();
            m.setMemotext(memotext);
            success = true;
            rt.add("Memo für " + sender + " gespeichert.");
        } else {
            rt.add("Memos kann man im Moment nur für sich selbst speichern.");
            rt.add("Benutzung:");
            rt.add("\"!memo get\" zeigt das aktuell gespeicherte Memo.");
            rt.add("\"!memo save Irgendein Text...\" speichert ein neues Memo, vorhandene werden dabei überschrieben!.");
        }
        return new CommandResponse(rt, success, m);
    }

    private String capitalize(String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    protected class Memo {

        private String _memotext;

        /**
         * @return the _memotext
         */
        public String getMemotext() {
            return _memotext;
        }

        /**
         * @param memotext the _memotext to set
         */
        public void setMemotext(String memotext) {
            this._memotext = memotext;
        }
    }
}
