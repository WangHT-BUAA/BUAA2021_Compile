import java.util.ArrayList;

public class VarDecl {
    //变量声明
    //VarDecl → BType VarDef { ',' VarDef } ';'
    private boolean success;
    private BType bType;
    private ArrayList<VarDef> varDefs = new ArrayList<>();

    public VarDecl(int blockNum) {
        success = doGrammarAnalysis(blockNum);
    }

    private boolean doGrammarAnalysis(int blockNum) {
        int currentPoint = Compiler.point;
        bType = new BType();
        if (!bType.isSuccess()) {
            return false;
        }
        VarDef varDef = new VarDef(blockNum);
        if (!varDef.isSuccess()) {
            Compiler.point = currentPoint;
            return false;
        }
        varDefs.add(varDef);
        while (true) {
            if (!Compiler.getSymbol().equals("COMMA")) {
                break;
            }
            Compiler.point++;
            VarDef newVarDef = new VarDef(blockNum);
            if (!newVarDef.isSuccess()) {
                Compiler.point = currentPoint;
                return false;
            }
            varDefs.add(newVarDef);
        }
        if (!Compiler.getSymbol().equals("SEMICN")) {
            //防止把 int func() 识别成变量没分号
            if (Compiler.symbols.get(Compiler.point).equals("LPARENT")) {
                Compiler.point = currentPoint;
                return false;
            }
            Compiler.point--;
            GrammarAnalysis.addError('i', Compiler.getLine());
            Compiler.point++;
            return true;
        }
        Compiler.point++;
        return true;
    }

    public boolean isSuccess() {
        return success;
    }

    public String toPrint() {
        //VarDecl → BType VarDef { ',' VarDef } ';'
        StringBuffer ans = new StringBuffer();
        ans.append(bType.toPrint());
        ans.append(varDefs.get(0).toPrint());
        for (int i = 1; i < varDefs.size(); i++) {
            ans.append("COMMA ,\n");
            ans.append(varDefs.get(i).toPrint());
        }
        ans.append("SEMICN ;\n");
        ans.append("<VarDecl>\n");
        return ans.toString();
    }

    public ArrayList<MidCode> getMidCode(String lastFunc) {
        ArrayList<MidCode> ans = new ArrayList<>();
        for (VarDef varDef : varDefs) {
            ans.addAll(varDef.getMidCode(lastFunc));
        }
        return ans;
    }
}
