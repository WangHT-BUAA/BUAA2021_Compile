import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class GrammarAnalysis {

    private CompUnit compUnit = null;
    private boolean success;
    //public static StringBuffer error = new StringBuffer();
    public static ArrayList<String> error = new ArrayList<>();

    public boolean doGrammarAnalysis() {
        compUnit = new CompUnit();
        success = compUnit.isSuccess();
        //todo
        /*
        if (error.length() != 0) {
            System.out.println(error);
        } else {
            System.out.println(compUnit.toPrint());
        }
         */
        return success;
    }

    public String toPrint() {
        return compUnit.toPrint();
    }

    public static void addError(char type, int line) {
        error.add(line + " " + type);
    }

    public static String getError() {
        StringBuffer ans = new StringBuffer();
        for (String s : error) {
            ans.append(s).append("\n");
        }
        return ans.toString();
    }

    public static void popError(int size) {
        while (error.size() > size) {
            error.remove(error.size() - 1);
        }
    }


    public ArrayList<MidCode> getMidCode() {
        return compUnit.getMidCode();
    }
}
