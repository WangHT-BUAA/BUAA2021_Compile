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

    public ArrayList<MidCode> getMidCode() {
        ArrayList<MidCode> ans = new ArrayList<>();
        ans.addAll(eqExps.get(0).getMidCode());
        if (eqExps.size() == 1) {
            return ans;
        }
        for (int i = 1; i < eqExps.size(); i++) {
            ArrayList<MidCode> part = eqExps.get(i).getMidCode();
            MidCode midCodeAns = ans.get(ans.size() - 1);
            MidCode midCodePart = part.get(part.size() - 1);
            ans.remove(ans.size() - 1);
            part.remove(part.size() - 1);
            ans.addAll(part);
            String newTemp = Compiler.getNewTemp();
            MidCode andMidCode = new MidCode(OpType.AND, newTemp, midCodeAns.getLeft(), midCodePart.getLeft());
            ans.add(andMidCode);
            ans.add(new MidCode(newTemp));
        }
        return ans;
    }
}
