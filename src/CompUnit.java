import java.util.ArrayList;

public class CompUnit {
    //编译单元
    //CompUnit → {Decl} {FuncDef} MainFuncDef
    private ArrayList<Decl> decls = new ArrayList<>();
    private ArrayList<FuncDef> funcDefs = new ArrayList<>();
    private MainFuncDef mainFuncDef;
    private boolean success;

    public CompUnit() {
        success = doGrammarAnalysis();
    }

    public boolean doGrammarAnalysis() {
        int currentPoint = Compiler.point;
        while (true) {
            currentPoint = Compiler.point;
            Decl decl = new Decl(0);
            if (!decl.isSuccess()) {
                Compiler.point = currentPoint;
                break;
            }
            decls.add(decl);
        }
        while (true) {
            currentPoint = Compiler.point;
            FuncDef funcDef = new FuncDef(Compiler.errorSymbolTable.size());
            if (!funcDef.isSuccess()) {
                Compiler.point = currentPoint;
                break;
            }
            funcDefs.add(funcDef);
        }
        //System.out.println("" + FuncDef.noReturn);
        mainFuncDef = new MainFuncDef();
        if (mainFuncDef.isSuccess()) {
            return true;
        }
        Compiler.point = currentPoint;
        return false;
    }

    public boolean isSuccess() {
        return success;
    }

    public String toPrint() {
        StringBuffer ans = new StringBuffer();
        for (Decl decl : decls) {
            ans.append(decl.toPrint());
        }
        for (FuncDef funcDef : funcDefs) {
            ans.append(funcDef.toPrint());
        }
        ans.append(mainFuncDef.toPrint());
        ans.append("<CompUnit>\n");
        return ans.toString();
    }

    public ArrayList<MidCode> getMidCode() {
        ArrayList<MidCode> ans = new ArrayList<>();
        ArrayList<MidCode> funcPart = new ArrayList<>();
        for (Decl decl : decls) {
            ans.addAll(decl.getMidCode());
        }
        for (FuncDef funcDef : funcDefs) {
            funcPart.addAll(funcDef.getMidCode());
        }
        ans.addAll(mainFuncDef.getMidCode());
        ans.addAll(funcPart);
        return ans;
    }
}
