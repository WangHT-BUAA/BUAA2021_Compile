import java.util.ArrayList;

public class LOrExp {
    //逻辑或表达式
    //LOrExp → LAndExp | LOrExp '||' LAndExp
    //LOrExp → LAndExp { '||' LAndExp }
    private boolean success;
    private ArrayList<LAndExp> lAndExps = new ArrayList<>();
    private ArrayList<Node> nodes = new ArrayList<>();

    public LOrExp() {
        success = doGrammarAnalysis();
    }

    private boolean doGrammarAnalysis() {
        int currentPoint = Compiler.point;
        LAndExp lAndExp = new LAndExp();
        if (!lAndExp.isSuccess()) {
            Compiler.point = currentPoint;
            return false;
        }
        lAndExps.add(lAndExp);
        while (Compiler.getSymbol().equals("OR")) {
            nodes.add(new Node(Compiler.getSymbol(), Compiler.getValue()));
            Compiler.point++;
            LAndExp newLAndExp = new LAndExp();
            if (!newLAndExp.isSuccess()) {
                Compiler.point = currentPoint;
                return false;
            }
            lAndExps.add(newLAndExp);
        }
        return true;
    }

    public boolean isSuccess() {
        return success;
    }

    public String toPrint() {
        //LOrExp → LAndExp | LOrExp '||' LAndExp
        //LOrExp → LAndExp { '||' LAndExp }
        StringBuffer ans = new StringBuffer();
        ans.append(lAndExps.get(0).toPrint());
        if (nodes.size() != 0) {
            ans.append("<LOrExp>\n");
        }
        for (int i = 0; i < nodes.size(); i++) {
            ans.append(nodes.get(i).toPrint());
            ans.append(lAndExps.get(i + 1).toPrint());
            if (i < nodes.size() - 1) {
                ans.append("<LOrExp>\n");
            }
        }
        ans.append("<LOrExp>\n");
        return ans.toString();
    }
}
