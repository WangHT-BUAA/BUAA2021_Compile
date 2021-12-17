import java.util.ArrayList;

public class MainFuncDef {
    //主函数定义
    //MainFuncDef → 'int' 'main' '(' ')' Block
    private boolean success;
    private Block block;

    public MainFuncDef() {
        success = doGrammarAnalysis();
    }

    private boolean doGrammarAnalysis() {
        int currentPoint = Compiler.point;
        if (!Compiler.getSymbol().equals("INTTK")) {
            return false;
        }
        Compiler.point++;
        if (!Compiler.getSymbol().equals("MAINTK")) {
            Compiler.point = currentPoint;
            return false;
        }
        Compiler.point++;
        if (!Compiler.getSymbol().equals("LPARENT")) {
            Compiler.point = currentPoint;
            return false;
        }
        Compiler.point++;
        if (!Compiler.getSymbol().equals("RPARENT")) {
            Compiler.point--;
            GrammarAnalysis.addError('j', Compiler.getLine());
        }
        Compiler.point++;
        block = new Block(Compiler.errorSymbolTable.size(), false, 2);
        if (block.isSuccess()) {
            int type = block.getLastStmtType();
            if (type != 8) {
                Compiler.point--;
                GrammarAnalysis.addError('g', Compiler.getLine());
            }
            return true;
        }
        Compiler.point = currentPoint;
        return false;
    }

    public boolean isSuccess() {
        return success;
    }

    public String toPrint() {
        //MainFuncDef → 'int' 'main' '(' ')' Block
        StringBuffer ans = new StringBuffer();
        ans.append("INTTK int\n");
        ans.append("MAINTK main\n");
        ans.append("LPARENT (\n");
        ans.append("RPARENT )\n");
        ans.append(block.toPrint());
        ans.append("<MainFuncDef>\n");
        return ans.toString();
    }

    public ArrayList<MidCode> getMidCode() {
        ArrayList<MidCode> ans = new ArrayList<>();
        MidCode mainLabel = new MidCode(OpType.LABEL, "Main");
        ans.add(mainLabel);
        int index = Compiler.symbolTable.size();

        Compiler.clearTempNum();
        ans.addAll(block.getMidCode(null, "main"));
        SymbolItem mainItem = new SymbolItem("func", "main", "main", "int");
        mainItem.setBlockSize(Compiler.tempNumber);
        Compiler.pushGlobalItem(mainItem);
        ans.add(ans.size() - 1, new MidCode(OpType.LABEL, "MainEnd"));
        Compiler.popSymbolTable(index);//维护符号表，pop掉block里的变量直到恢复到入块之前的大小
        return ans;
    }
}
