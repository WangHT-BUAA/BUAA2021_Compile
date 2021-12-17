import java.util.ArrayList;

public class ConstDecl {
    //常量声明
    //ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
    private boolean success;
    private BType bType;
    private ArrayList<ConstDef> constDefs = new ArrayList<>();

    public ConstDecl(int blockNum) {
        this.success = doGrammarAnalysis(blockNum);
    }

    private boolean doGrammarAnalysis(int blockNum) {
        int currentPoint = Compiler.point;
        if (Compiler.getSymbol().equals("CONSTTK")) {
            Compiler.point++;
            bType = new BType();
            if (bType.isSuccess()) {
                ConstDef constDef = new ConstDef(blockNum);
                if (!constDef.isSuccess()) {
                    Compiler.point = currentPoint;
                    return false;
                }
                constDefs.add(constDef);
                while (Compiler.getSymbol().equals("COMMA")) {
                    Compiler.point++;
                    ConstDef newConstDef = new ConstDef(blockNum);
                    if (!newConstDef.isSuccess()) {
                        Compiler.point = currentPoint;
                        return false;
                    }
                    constDefs.add(newConstDef);
                }
                if (!Compiler.getSymbol().equals("SEMICN")) {
                    //Compiler.point = currentPoint;
                    Compiler.point--;
                    GrammarAnalysis.addError('i', Compiler.getLine());
                    Compiler.point++;
                    return true;
                }
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
        //ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
        StringBuffer ans = new StringBuffer();
        ans.append("CONSTTK const\n");
        ans.append(bType.toPrint());
        ans.append(constDefs.get(0).toPrint());
        for (int i = 1; i < constDefs.size(); i++) {
            ans.append("COMMA ,\n");
            ans.append(constDefs.get(i).toPrint());
        }
        ans.append("SEMICN ;\n");
        ans.append("<ConstDecl>\n");
        return ans.toString();
    }

    public ArrayList<MidCode> getMidCode(String lastFunc) {
        ArrayList<MidCode> ans = new ArrayList<>();
        for (ConstDef constDef : constDefs) {
            ans.addAll(constDef.getMidCode(lastFunc));
        }
        return ans;
    }
}
