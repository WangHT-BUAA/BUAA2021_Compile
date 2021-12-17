import java.util.ArrayList;

public class LAndExp {
    //逻辑与表达式
    //LAndExp → EqExp | LAndExp '&&' EqExp
    //LAndExp → EqExp { '&&' EqExp }
    private boolean success;
    private ArrayList<EqExp> eqExps = new ArrayList<>();
    private ArrayList<Node> nodes = new ArrayList<>();

    public LAndExp() {
        success = doGrammarAnalysis();
    }

    private boolean doGrammarAnalysis() {
        int currentPoint = Compiler.point;
        EqExp eqExp = new EqExp();
        if (!eqExp.isSuccess()) {
            Compiler.point = currentPoint;
            return false;
        }
        eqExps.add(eqExp);
        while (Compiler.getSymbol().equals("AND")) {
            nodes.add(new Node(Compiler.getSymbol(), Compiler.getValue()));
            Compiler.point++;
            EqExp newEqExp = new EqExp();
            if (!newEqExp.isSuccess()) {
                Compiler.point = currentPoint;
                return false;
            }
            eqExps.add(newEqExp);
        }
        return true;
    }

    public boolean isSuccess() {
        return success;
    }

    public String toPrint() {
        //LAndExp → EqExp | LAndExp '&&' EqExp
        //LAndExp → EqExp { '&&' EqExp }
        StringBuffer ans = new StringBuffer();
        ans.append(eqExps.get(0).toPrint());
        if (nodes.size() != 0) {
            ans.append("<LAndExp>\n");
        }
        for (int i = 0; i < nodes.size(); i++) {
            ans.append(nodes.get(i).toPrint());
            ans.append(eqExps.get(i + 1).toPrint());
            if (i < nodes.size() - 1) {
                ans.append("<LAndExp>\n");
            }
        }
        ans.append("<LAndExp>\n");
        return ans.toString();
    }

    public ArrayList<MidCode> getMidCode(String lastFunc, String nextLabel, String beginLabel) {
        ArrayList<MidCode> ans = new ArrayList<>();
        for (int i = 0; i < eqExps.size(); i++) {
            ArrayList<MidCode> part = eqExps.get(i).getMidCode(lastFunc);
            MidCode midCodePart = part.get(part.size() - 1);
            part.remove(part.size() - 1);
            ans.addAll(part);
            ans.add(new MidCode(OpType.BEQ, midCodePart.getLeft(), "$0", nextLabel));
        }
        ans.add(new MidCode(OpType.JUMP, beginLabel));
        return ans;

    }
}
