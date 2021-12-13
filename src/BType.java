public class BType {
    //基本类型
    //BType → 'int'
    private boolean success;
    private String type;

    public BType() {
        success = doGrammarAnalysis();
    }

    private boolean doGrammarAnalysis() {
        if (!Compiler.getSymbol().equals("INTTK")) {
            return false;
        }
        Compiler.point++;
        type = Compiler.getSymbol();
        return true;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getType() {
        if (type.equals("INTTK")) {
            return "INT";
        }
        return null;
    }

    public String toPrint() {
        //不输出<BType>
        //BType → 'int'
        return "INTTK int\n";
    }
}
