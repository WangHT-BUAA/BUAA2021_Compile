import java.util.ArrayList;

public class InitVal {
    //变量初值
    //InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
    private boolean success;
    private Exp exp;
    private ArrayList<InitVal> initVals = new ArrayList<>();

    public InitVal() {
        success = doGrammarAnalysis();
    }

    private boolean doGrammarAnalysis() {
        int currentPoint = Compiler.point;
        exp = new Exp();
        if (exp.isSuccess()) {
            return true;
        }
        Compiler.point = currentPoint;
        if (Compiler.getSymbol().equals("LBRACE")) {
            Compiler.point++;
            InitVal initVal = new InitVal();
            if (initVal.success) {
                initVals.add(initVal);
                while (Compiler.getSymbol().equals("COMMA")) {
                    Compiler.point++;
                    InitVal newInitVal = new InitVal();
                    if (!newInitVal.isSuccess()) {
                        Compiler.point = currentPoint;
                        return false;
                    }
                    initVals.add(newInitVal);
                }
            } else {
                Compiler.point = currentPoint;
            }
            if (Compiler.getSymbol().equals("RBRACE")) {
                Compiler.point++;
                return true;
            }
        }
        Compiler.point = currentPoint;
        return false;
    }

    public boolean isSuccess() {
        return success;
    }

    public String toPrint() {
        //InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
        StringBuffer ans = new StringBuffer();
        if (exp.isSuccess()) {
            ans.append(exp.toPrint());
        } else {
            ans.append("LBRACE {\n");
            if (initVals.size() != 0) {
                ans.append(initVals.get(0).toPrint());
                for (int i = 1; i < initVals.size(); i++) {
                    ans.append("COMMA ,\n");
                    ans.append(initVals.get(i).toPrint());
                }
            }
            ans.append("RBRACE }\n");
        }
        ans.append("<InitVal>\n");
        return ans.toString();
    }

    public ArrayList<MidCode> getMidCode() {
        ArrayList<MidCode> ans = new ArrayList<>();
        if (exp.isSuccess()) {
            return exp.getMidCode();
        } else {
            //多维

            return ans;
        }
    }
}
