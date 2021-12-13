import java.util.ArrayList;

public class RelExp {
    //关系表达式
    //RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
    //RelExp → AddExp { ('<' | '>' | '<=' | '>=') AddExp }
    private boolean success;
    private ArrayList<AddExp> addExps = new ArrayList<>();
    private ArrayList<Node> nodes = new ArrayList<>();

    public RelExp() {
        success = doGrammarAnalysis();
    }

    private boolean doGrammarAnalysis() {
        int currentPoint = Compiler.point;
        AddExp addExp = new AddExp();
        if (!addExp.isSuccess()) {
            Compiler.point = currentPoint;
            return false;
        }
        addExps.add(addExp);
        while (Compiler.getSymbol().equals("LSS") ||
                Compiler.getSymbol().equals("GRE") ||
                Compiler.getSymbol().equals("LEQ") ||
                Compiler.getSymbol().equals("GEQ")) {
            nodes.add(new Node(Compiler.getSymbol(), Compiler.getValue()));
            Compiler.point++;
            AddExp newAddExp = new AddExp();
            if (!newAddExp.isSuccess()) {
                Compiler.point = currentPoint;
                return false;
            }
            addExps.add(newAddExp);
        }
        return true;
    }

    public boolean isSuccess() {
        return success;
    }

    public String toPrint() {
        //RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
        //RelExp → AddExp { ('<' | '>' | '<=' | '>=') AddExp }
        StringBuffer ans = new StringBuffer();
        ans.append(addExps.get(0).toPrint());
        if (nodes.size() != 0) {
            ans.append("<RelExp>\n");
        }
        for (int i = 0; i < nodes.size(); i++) {
            ans.append(nodes.get(i).toPrint());
            ans.append(addExps.get(i + 1).toPrint());
            if (i < nodes.size() - 1) {
                ans.append("<RelExp>\n");
            }
        }
        ans.append("<RelExp>\n");
        return ans.toString();
    }
}
