import java.util.ArrayList;

public class Number {
    //数值
    //Number → IntConst
    private boolean success;
    private String number;
    private Node intConst;

    public Number() {
        success = doGrammarAnalysis();
    }

    private boolean doGrammarAnalysis() {
        if (Compiler.getSymbol().equals("INTCON")) {
            intConst = new Node(Compiler.getSymbol(), Compiler.getValue());
            number = Compiler.getValue();
            Compiler.point++;
            return true;
        }
        return false;
    }

    public boolean isSuccess() {
        return success;
    }

    public String toPrint() {
        //Number → IntConst
        StringBuffer ans = new StringBuffer();
        ans.append(intConst.toPrint());
        ans.append("<Number>\n");
        return ans.toString();
    }

    public ArrayList<MidCode> getMidCode() {
        ArrayList<MidCode> ans = new ArrayList<>();
        String newTemp = Compiler.getNewTemp();
        MidCode immMidCode = new MidCode(OpType.IMM, newTemp, number);
        ans.add(immMidCode);
        ans.add(new MidCode(newTemp));
        return ans;
    }

    public int getArrCount() {
        return Integer.parseInt(number);
    }
}
