import java.util.ArrayList;

public class EqExp {
    //相等性表达式
    //EqExp → RelExp | EqExp ('==' | '!=') RelExp
    //EqExp → RelExp { ('==' | '!=') RelExp }
    private boolean success;
    private ArrayList<RelExp> relExps = new ArrayList<>();
    private ArrayList<Node> nodes = new ArrayList<>();

    public EqExp() {
        success = doGrammarAnalysis();
    }

    private boolean doGrammarAnalysis() {
        int currentPoint = Compiler.point;
        RelExp relExp = new RelExp();
        if (!relExp.isSuccess()) {
            Compiler.point = currentPoint;
            return false;
        }
        relExps.add(relExp);
        while (Compiler.getSymbol().equals("EQL") ||
                Compiler.getSymbol().equals("NEQ")) {
            nodes.add(new Node(Compiler.getSymbol(), Compiler.getValue()));
            Compiler.point++;
            RelExp newRelExp = new RelExp();
            if (!newRelExp.isSuccess()) {
                Compiler.point = currentPoint;
                return false;
            }
            relExps.add(newRelExp);
        }
        return true;
    }

    public boolean isSuccess() {
        return success;
    }

    public String toPrint() {
        //EqExp → RelExp | EqExp ('==' | '!=') RelExp
        //EqExp → RelExp { ('==' | '!=') RelExp }
        StringBuffer ans = new StringBuffer();
        ans.append(relExps.get(0).toPrint());
        if (nodes.size() != 0) {
            ans.append("<EqExp>\n");
        }
        for (int i = 0; i < nodes.size(); i++) {
            ans.append(nodes.get(i).toPrint());
            ans.append(relExps.get(i + 1).toPrint());
            if (i < nodes.size() - 1) {
                ans.append("<EqExp>\n");
            }
        }
        ans.append("<EqExp>\n");
        return ans.toString();
    }
}
