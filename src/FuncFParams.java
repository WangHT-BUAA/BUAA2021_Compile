import java.util.ArrayList;

public class FuncFParams {
    //函数形参表
    //FuncFParams → FuncFParam { ',' FuncFParam }
    private boolean success;
    private ArrayList<FuncFParam> funcFParams = new ArrayList<>();

    public FuncFParams(int blockNum) {
        success = doGrammarAnalysis(blockNum);
    }

    private boolean doGrammarAnalysis(int blockNum) {
        int currentPoint = Compiler.point;
        FuncFParam funcFParam = new FuncFParam(blockNum);
        if (!funcFParam.isSuccess()) {
            Compiler.point = currentPoint;
            return false;
        }
        funcFParams.add(funcFParam);
        while (Compiler.getSymbol().equals("COMMA")) {
            Compiler.point++;
            FuncFParam newFuncFParam = new FuncFParam(blockNum);
            if (!newFuncFParam.isSuccess()) {
                Compiler.point = currentPoint;
                return false;
            }
            funcFParams.add(newFuncFParam);
        }
        return true;
    }

    public int getParamNum() {
        return funcFParams.size();
    }

    public ArrayList<SymbolItem> getParams() {
        ArrayList<SymbolItem> ans = new ArrayList<>();
        for (FuncFParam funcFParam : funcFParams) {
            ans.add(funcFParam.getSymbolItem());
        }
        return ans;
    }

    public boolean isSuccess() {
        return success;
    }

    public String toPrint() {
        //FuncFParams → FuncFParam { ',' FuncFParam }
        StringBuffer ans = new StringBuffer();
        ans.append(funcFParams.get(0).toPrint());
        for (int i = 1; i < funcFParams.size(); i++) {
            ans.append("COMMA ,\n");
            ans.append(funcFParams.get(i).toPrint());
        }
        ans.append("<FuncFParams>\n");
        return ans.toString();
    }

    public ArrayList<MidCode> getMidCode() {
        ArrayList<MidCode> ans = new ArrayList<>();
        for (FuncFParam funcFParam : funcFParams) {
            ans.addAll(funcFParam.getMidCode());
        }
        return ans;
    }
}
