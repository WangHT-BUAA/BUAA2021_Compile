import java.util.ArrayList;

public class FuncFParam {
    //函数形参
    //FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
    private boolean success;
    private BType bType;
    private ArrayList<ConstExp> constExps = new ArrayList<>();
    private Node ident;
    private int type = 0;
    private int dimension = 0;
    private SymbolItem symbolItem;

    public FuncFParam(int blockNum) {
        success = doGrammarAnalysis(blockNum);
    }

    private boolean doGrammarAnalysis(int blockNum) {
        int currentPoint = Compiler.point;
        bType = new BType();
        if (!bType.isSuccess()) {
            Compiler.point = currentPoint;
            return false;
        }
        if (Compiler.getSymbol().equals("IDENFR")) {
            ident = new Node(Compiler.getSymbol(), Compiler.getValue());
            Compiler.point++;
            if (Compiler.getSymbol().equals("LBRACK")) {
                dimension++;
                type = 1;
                Compiler.point++;
                if (!Compiler.getSymbol().equals("RBRACK")) {
                    Compiler.point--;
                    GrammarAnalysis.addError('k', Compiler.getLine());
                }
                Compiler.point++;
                while (Compiler.getSymbol().equals("LBRACK")) {
                    dimension++;
                    Compiler.point++;
                    ConstExp constExp = new ConstExp();
                    if (!constExp.isSuccess()) {
                        Compiler.point = currentPoint;
                        return false;
                    }
                    constExps.add(constExp);
                    type = 2;
                    if (!Compiler.getSymbol().equals("RBRACK")) {
                        Compiler.point--;
                        GrammarAnalysis.addError('k', Compiler.getLine());
                    }
                    Compiler.point++;
                }
            }
            if (Compiler.getErrorSymbolItemInBlock(ident.getSymbolName(), blockNum) != null) {
                GrammarAnalysis.addError('b', Compiler.getLine()); //重定义了
            }
            SymbolItem paramSymbolItem = new SymbolItem("var", ident.getSymbolName(), dimension, false);
            symbolItem = paramSymbolItem;
            Compiler.pushErrorItemStack(paramSymbolItem);
            return true;
        }
        Compiler.point = currentPoint;
        return false;
    }

    public SymbolItem getSymbolItem() {
        return symbolItem;
    }

    public boolean isSuccess() {
        return success;
    }

    public String toPrint() {
        //FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
        StringBuffer ans = new StringBuffer();
        ans.append(bType.toPrint());
        ans.append(ident.toPrint());
        if (type == 1) {
            ans.append("LBRACK [\n");
            ans.append("RBRACK ]\n");
        } else if (type == 2) {
            ans.append("LBRACK [\n");
            ans.append("RBRACK ]\n");
            for (ConstExp constExp : constExps) {
                ans.append("LBRACK [\n");
                ans.append(constExp.toPrint());
                ans.append("RBRACK ]\n");
            }
        }
        ans.append("<FuncFParam>\n");
        return ans.toString();
    }

    public ArrayList<MidCode> getMidCode() {
        ArrayList<MidCode> ans = new ArrayList<>();
        String type = bType.getType(); //must be int
        String initName = ident.getSymbolName();
        if (dimension == 0) {
            String name = Compiler.getNewTempOrGlobal();
            SymbolItem paraItem = new SymbolItem("var", initName, name, false);
            Compiler.pushItemStack(paraItem);
            MidCode paraMidCode = new MidCode(OpType.PARA, name);
            ans.add(paraMidCode);
        } else if (dimension == 1) {
            //一维数组
            String name = Compiler.getNewPtr();
            SymbolItem paraItem = new SymbolItem("arr", initName, name, false, 999);
            Compiler.pushItemStack(paraItem);
            ans.add(new MidCode(OpType.PARA, name));
        } else if (dimension == 2) {
            //二维数组
            String name = Compiler.getNewPtr();
            int index2 = constExps.get(0).getArrCount();
            SymbolItem paraItem = new SymbolItem("arr", initName, name, false, 999, index2);
            Compiler.pushItemStack(paraItem);
            ans.add(new MidCode(OpType.PARA, name));
        }
        return ans;
    }
}
