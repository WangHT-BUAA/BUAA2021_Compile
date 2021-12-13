import java.util.ArrayList;

public class VarDef {
    //变量定义
    //VarDef → Ident { '[' ConstExp ']' } |  Ident { '[' ConstExp ']' } '=' InitVal
    private boolean success;
    private ArrayList<ConstExp> constExps = new ArrayList<>();
    private InitVal initVal;
    private Node ident;
    private String initName;
    private int type = 0;

    public VarDef(int blockNum) {
        success = doGrammarAnalysis(blockNum);
    }

    private boolean doGrammarAnalysis(int blockNum) {
        //todo
        int currentPoint = Compiler.point;
        if (!Compiler.getSymbol().equals("IDENFR")) {
            return false;
        }
        ident = new Node(Compiler.getSymbol(), Compiler.getValue());
        initName = Compiler.getValue();
        Compiler.point++;
        while (Compiler.getSymbol().equals("LBRACK")) {
            Compiler.point++;
            ConstExp constExp = new ConstExp();
            if (!constExp.isSuccess()) {
                Compiler.point = currentPoint;
                return false;
            }
            constExps.add(constExp);
            if (!Compiler.getSymbol().equals("RBRACK")) {
                Compiler.point--;
                GrammarAnalysis.addError('k', Compiler.getLine());
            }
            Compiler.point++;
        }
        if (Compiler.getSymbol().equals("ASSIGN")) {
            type = 1;
            Compiler.point++;
            initVal = new InitVal();
            if (!initVal.isSuccess()) {
                Compiler.point = currentPoint;
                return false;
            }
        }
        if (Compiler.getErrorSymbolItemInBlock(initName,blockNum) != null) {
            GrammarAnalysis.addError('b', Compiler.getLine()); //重定义了
        }
        SymbolItem varSymbolItem = new SymbolItem("var", initName, constExps.size(), false);
        Compiler.pushErrorItemStack(varSymbolItem);
        return true;
    }

    public boolean isSuccess() {
        return success;
    }

    public String toPrint() {
        //VarDef → Ident { '[' ConstExp ']' } |  Ident { '[' ConstExp ']' } '=' InitVal
        StringBuffer ans = new StringBuffer();
        ans.append(ident.toPrint());
        for (ConstExp constExp : constExps) {
            ans.append("LBRACK [\n");
            ans.append(constExp.toPrint());
            ans.append("RBRACK ]\n");
        }
        if (type == 1) {
            ans.append("ASSIGN =\n");
            ans.append(initVal.toPrint());
        }
        ans.append("<VarDef>\n");
        return ans.toString();
    }

    public ArrayList<MidCode> getMidCode() {
        ArrayList<MidCode> ans = new ArrayList<>();
        if (constExps.size() == 0) {
            //0维
            String name = Compiler.getNewTemp();
            SymbolItem item = new SymbolItem("var", initName, name, false);
            Compiler.pushItemStack(item);
            MidCode defMidCode = new MidCode(OpType.VAR, name, false);// 定义
            ans.add(defMidCode);
            if (type == 1) {
                ArrayList<MidCode> partInitVal = initVal.getMidCode();
                MidCode midCodeInitVal = partInitVal.get(partInitVal.size() - 1);
                partInitVal.remove(partInitVal.size() - 1);
                MidCode assignMidCode = new MidCode(OpType.ASSIGN, name, midCodeInitVal.getLeft());
                ans.addAll(partInitVal);
                ans.add(assignMidCode);
            }
            return ans;
        } else if (constExps.size() == 1) {
            //1维
        } else if (constExps.size() == 2) {
            //2维
        }
        return null;
    }
}
