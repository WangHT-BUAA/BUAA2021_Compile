public class FuncType {
    //函数类型
    //FuncType → 'void' | 'int'
    private boolean success;
    private Node node;
    private String funcType;

    public FuncType() {
        success = doGrammarAnalysis();
    }

    private boolean doGrammarAnalysis() {
        if (Compiler.getSymbol().equals("INTTK") ||
                Compiler.getSymbol().equals("VOIDTK")) {
            node = new Node(Compiler.getSymbol(), Compiler.getValue());
            funcType = Compiler.getSymbol();
            Compiler.point++;
            return true;
        }
        return false;

    }

    public boolean isSuccess() {
        return success;
    }

    public String getFuncType() {
        if (funcType.equals("INTTK")) {
            return "INT";
        } else {
            return "VOIDTK";
        }
    }

    public String toPrint() {
        StringBuffer ans = new StringBuffer();
        ans.append(node.toPrint());
        ans.append("<FuncType>\n");
        return ans.toString();
    }
}
