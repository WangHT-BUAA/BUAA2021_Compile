import java.util.ArrayList;

public class PrimaryExp {
    //基本表达式
    //PrimaryExp → '(' Exp ')' | LVal | Number
    private boolean success;
    private Exp exp;
    private LVal lVal;
    private Number number;
    int type = 0;

    public PrimaryExp() {
        success = doGrammarAnalysis();
    }

    private boolean doGrammarAnalysis() {
        int currentPoint = Compiler.point;
        if (Compiler.getSymbol().equals("LPARENT")) {
            Compiler.point++;
            exp = new Exp();
            if (exp.isSuccess()) {
                if (Compiler.getSymbol().equals("RPARENT")) {
                    type = 1;
                    Compiler.point++;
                    return true;
                } else {
                    type = 1;
                    Compiler.point--;
                    GrammarAnalysis.addError('j', Compiler.getLine());
                    Compiler.point++;
                    return true;
                }
            }
        }
        Compiler.point = currentPoint;
        lVal = new LVal();
        if (lVal.isSuccess()) {
            type = 2;
            return true;
        }
        Compiler.point = currentPoint;
        number = new Number();
        type = 3;
        return number.isSuccess();
    }

    public SymbolItem getSymbolType() {
        if (type == 3) {
            //数字
            return new SymbolItem("var", "num", 0, false);
        } else if (type == 2) {
            //左值表达式
            return lVal.getSymbolType();
        } else if (type == 1) {
            //(exp)
            return exp.getSymbolType();
        }
        return null;
    }

    public boolean isSuccess() {
        return success;
    }

    public String toPrint() {
        //PrimaryExp → '(' Exp ')' | LVal | Number
        StringBuffer ans = new StringBuffer();
        if (type == 1) {
            ans.append("LPARENT (\n");
            ans.append(exp.toPrint());
            ans.append("RPARENT )\n");
        } else if (type == 2) {
            ans.append(lVal.toPrint());
        } else if (type == 3) {
            ans.append(number.toPrint());
        }
        ans.append("<PrimaryExp>\n");
        return ans.toString();
    }

    public ArrayList<MidCode> getMidCode() {
        ArrayList<MidCode> ans = new ArrayList<>();
        if (type == 1) {
            ans.addAll(exp.getMidCode());
        } else if (type == 2) {
            ans.addAll(lVal.getMidCode());
        } else if (type == 3) {
            ans.addAll(number.getMidCode());
        }
        return ans;
    }
}
