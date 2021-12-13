import java.util.ArrayList;

public class ConstInitVal {
    //常量初值
    //ConstInitVal → ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
    private boolean success;
    private ConstExp constExp;
    private ArrayList<ConstInitVal> constInitVals = new ArrayList<>();
    private int type = 0;

    public ConstInitVal() {
        success = doGrammarAnalysis();
    }

    private boolean doGrammarAnalysis() {
        int currentPoint = Compiler.point;
        constExp = new ConstExp();
        if (constExp.isSuccess()) {
            type = 1;
            return true;
        }
        Compiler.point = currentPoint;
        if (!Compiler.getSymbol().equals("LBRACE")) {
            Compiler.point = currentPoint;
            return false;
        }
        type = 2;
        Compiler.point++;
        if (Compiler.getSymbol().equals("RBRACE")) {
            Compiler.point++;
            return true;
        }
        //说明中括号里有这一项，因此进行完整分析。上面的判断是{}的情况
        ConstInitVal constInitVal = new ConstInitVal();
        if (!constInitVal.isSuccess()) {
            Compiler.point = currentPoint;
            return false;
        }
        constInitVals.add(constInitVal);
        while (Compiler.getSymbol().equals("COMMA")) {
            Compiler.point++;
            ConstInitVal newConstInitVal = new ConstInitVal();
            if (!newConstInitVal.isSuccess()) {
                Compiler.point = currentPoint;
                return false;
            }
            constInitVals.add(newConstInitVal);
        }
        if (Compiler.getSymbol().equals("RBRACE")) {
            Compiler.point++;
            return true;
        }
        return false;
    }

    public boolean isSuccess() {
        return success;
    }

    public String toPrint() {
        //ConstInitVal → ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
        StringBuffer ans = new StringBuffer();
        if (type == 1) {
            ans.append(constExp.toPrint());
        } else if (type == 2) {
            ans.append("LBRACE {\n");
            if (constInitVals.size() != 0) {
                ans.append(constInitVals.get(0).toPrint());
                for (int i = 1; i < constInitVals.size(); i++) {
                    ans.append("COMMA ,\n");
                    ans.append(constInitVals.get(i).toPrint());
                }
            }
            ans.append("RBRACE }\n");
        }
        ans.append("<ConstInitVal>\n");
        return ans.toString();
    }

    public ArrayList<MidCode> getMidCode() {
        ArrayList<MidCode> ans = new ArrayList<>();
        if (type == 1) {
            ans.addAll(constExp.getMidCode());
        } else if (type == 2) {
            //todo
        }
        return ans;
    }
}
