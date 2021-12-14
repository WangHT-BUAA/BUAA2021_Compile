import java.util.ArrayList;

public class Cond {
    //条件表达式
    //Cond → LOrExp
    private boolean success;
    private LOrExp lOrExp;

    public Cond() {
        success = doGrammarAnalysis();
    }

    private boolean doGrammarAnalysis() {
        lOrExp = new LOrExp();
        return lOrExp.isSuccess();
    }

    public boolean isSuccess() {
        return success;
    }

    public String toPrint() {
        StringBuffer ans = new StringBuffer();
        ans.append(lOrExp.toPrint());
        ans.append("<Cond>\n");
        return ans.toString();
    }

    public ArrayList<MidCode> getMidCode() {
        return lOrExp.getMidCode();
    }
}
