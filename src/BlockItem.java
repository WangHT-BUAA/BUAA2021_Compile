import java.util.ArrayList;

public class BlockItem {
    //语句块项
    //BlockItem → Decl | Stmt
    private boolean success;
    private Decl decl;
    private Stmt stmt;
    private int type = 0;

    public BlockItem(int blockNum, boolean isInCircle, int noReturn) {
        success = doGrammarAnalysis(blockNum, isInCircle, noReturn);
    }

    private boolean doGrammarAnalysis(int blockNum, boolean isInCircle, int noReturn) {
        int currentPoint = Compiler.point;
        decl = new Decl(blockNum);
        if (decl.isSuccess()) {
            type = 1;
            return true;
        }
        Compiler.point = currentPoint;
        stmt = new Stmt(blockNum, isInCircle, noReturn);
        if (stmt.isSuccess()) {
            type = 2;
            return true;
        }
        Compiler.point = currentPoint;
        return false;
    }

    public int getStmtType() {
        if (stmt == null) {
            return -1;
        }
        if (stmt.isSuccess()) {
            return stmt.getType(); // 7 是return 8有返回值
        } else {
            return -1;
        }

    }

    public boolean isSuccess() {
        return success;
    }

    public String toPrint() {
        //不输出<BlockItem>
        //BlockItem → Decl | Stmt
        StringBuffer ans = new StringBuffer();
        if (type == 1) {
            ans.append(decl.toPrint());
        } else if (type == 2) {
            ans.append(stmt.toPrint());
        }
        return ans.toString();
    }

    public ArrayList<MidCode> getMidCode(String whileLabel) {
        ArrayList<MidCode> ans = new ArrayList<>();
        if (type == 1) {
            ans.addAll(decl.getMidCode());
        } else if (type == 2) {
            ans.addAll(stmt.getMidCode(whileLabel));
        }
        return ans;
    }
}
