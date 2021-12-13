public class UnaryOp {
    //单目运算符
    //UnaryOp → '+' | '−' | '!'
    private boolean success;
    private Node node;
    private String op;

    public UnaryOp() {
        success = doGrammarAnalysis();
    }

    private boolean doGrammarAnalysis() {
        if (Compiler.getSymbol().equals("PLUS") ||
                Compiler.getSymbol().equals("MINU") ||
                Compiler.getSymbol().equals("NOT")) {
            op = Compiler.getSymbol();
            node = new Node(Compiler.getSymbol(), Compiler.getValue());
            Compiler.point++;
            return true;
        }
        return false;
    }

    public boolean isSuccess() {
        return success;
    }

    public String toPrint() {
        //UnaryOp → '+' | '−' | '!'
        StringBuffer ans = new StringBuffer();
        ans.append(node.toPrint());
        ans.append("<UnaryOp>\n");
        return ans.toString();
    }

    public String getOp() {
        return op;
    }
}
