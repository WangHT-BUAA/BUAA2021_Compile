import java.util.ArrayList;

public class ConstExp {
    //常量表达式
    //ConstExp → AddExp
    private boolean success;
    private AddExp addExp;

    public ConstExp() {
        success = doGrammarAnalysis();
    }

    private boolean doGrammarAnalysis() {
        addExp = new AddExp();
        return addExp.isSuccess();
    }

    public boolean isSuccess() {
        return success;
    }

    public String toPrint() {
        //ConstExp → AddExp
        StringBuffer ans = new StringBuffer();
        ans.append(addExp.toPrint());
        ans.append("<ConstExp>\n");
        return ans.toString();
    }
    //useless
    public ArrayList<MidCode> getMidCode(String lastFunc) {
        return addExp.getMidCode(lastFunc);
    }

    public int getArrCount() {
        return addExp.getArrCount();
    }
}
