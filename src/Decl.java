import java.util.ArrayList;

public class Decl {
    //声明
    //Decl → ConstDecl | VarDecl
    private boolean success;
    private ConstDecl constDecl;
    private VarDecl varDecl;

    public static boolean isGlobal = false;

    private int type = 0;

    public Decl(int blockNum) {
        success = doGrammarAnalysis(blockNum);
    }

    private boolean doGrammarAnalysis(int blockNum) {
        int currentPoint = Compiler.point;
        int currentNum = Compiler.errorSymbolTable.size();
        constDecl = new ConstDecl(blockNum);
        if (constDecl.isSuccess()) {
            type = 1;
            return true;
        }
        Compiler.point = currentPoint;
        varDecl = new VarDecl(blockNum);
        if (varDecl.isSuccess()) {
            type = 2;
            return true;
        }
        Compiler.point = currentPoint;
        Compiler.popErrorSymbolTable(currentNum);
        return false;
    }

    public boolean isSuccess() {
        return success;
    }

    public String toPrint() {
        StringBuffer ans = new StringBuffer();
        if (type == 1) {
            ans.append(constDecl.toPrint());
        } else if (type == 2) {
            ans.append(varDecl.toPrint());
        }
        return ans.toString();
    }

    public ArrayList<MidCode> getMidCode(String lastFunc) {
        ArrayList<MidCode> ans = new ArrayList<>();
        if (type == 1) {
            ans.addAll(constDecl.getMidCode(lastFunc));
        } else if (type == 2) {
            ans.addAll(varDecl.getMidCode(lastFunc));
        }
        return ans;
    }
}
