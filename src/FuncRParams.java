import java.util.ArrayList;

public class FuncRParams {
    //函数实参表
    //FuncRParams → Exp { ',' Exp }
    private boolean success;
    private ArrayList<Exp> exps = new ArrayList<>();

    public FuncRParams() {
        success = doGrammarAnalysis();
    }

    private boolean doGrammarAnalysis() {
        int currentPoint = Compiler.point;
        Exp exp = new Exp();
        if (!exp.isSuccess()) {
            Compiler.point = currentPoint;
            return false;
        }
        exps.add(exp);
        while (Compiler.getSymbol().equals("COMMA")) {
            Compiler.point++;
            Exp newExp = new Exp();
            if (!newExp.isSuccess()) {
                Compiler.point = currentPoint;
                return false;
            }
            exps.add(newExp);
        }
        return true;
    }

    public ArrayList<SymbolItem> getRParams() {
        ArrayList<SymbolItem> ans = new ArrayList<>();
        for (Exp exp : exps) {
            ans.add(exp.getSymbolType());
        }
        return ans;
    }

    public int getParamNum() {
        return exps.size();
    }

    public boolean isSuccess() {
        return success;
    }

    public String toPrint() {
        //FuncRParams → Exp { ',' Exp }
        StringBuffer ans = new StringBuffer();
        ans.append(exps.get(0).toPrint());
        for (int i = 1; i < exps.size(); i++) {
            ans.append("COMMA ,\n");
            ans.append(exps.get(i).toPrint());
        }
        ans.append("<FuncRParams>\n");
        return ans.toString();
    }

    public ArrayList<MidCode> getMidCode() {
        ArrayList<MidCode> ans = new ArrayList<>();
        ArrayList<MidCode> toParaMidCode = new ArrayList<>();
        for (Exp exp : exps) {
            ArrayList<MidCode> part = exp.getMidCode();
            MidCode midCode = part.get(part.size() - 1);
            part.remove(part.size() - 1);
            ans.addAll(part);
            toParaMidCode.add(midCode);
        }
        ans.addAll(toParaMidCode);
        return ans;
    }
}
