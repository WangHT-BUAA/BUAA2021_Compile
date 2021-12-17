import java.util.ArrayList;

public class Exp {
    //表达式
    //Exp → AddExp
    private boolean success;
    private AddExp addExp;

    public Exp() {
        success = doGrammarAnalysis();
    }

    private boolean doGrammarAnalysis() {
        int currentPoint = Compiler.point;
        addExp = new AddExp();
        if (addExp.isSuccess()) {
            return true;
        }
        Compiler.point = currentPoint;
        return false;
    }

    public SymbolItem getSymbolType() {
        return addExp.getSymbolType();
    }

    public boolean isSuccess() {
        return success;
    }

    public String toPrint() {
        StringBuffer ans = new StringBuffer();
        ans.append(addExp.toPrint());
        ans.append("<Exp>\n");
        return ans.toString();
    }

    public ArrayList<MidCode> getMidCode(String lastFunc) {
        return addExp.getMidCode(lastFunc);
    }

    public int getArrCount() {
        return addExp.getArrCount();
    }
}
