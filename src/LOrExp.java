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

    public ArrayList<MidCode> getMidCode() {
        ArrayList<MidCode> ans = new ArrayList<>();
        ans.addAll(lAndExps.get(0).getMidCode());
        if (lAndExps.size() == 1) {
            return ans;
        }
        for (int i = 1; i < lAndExps.size(); i++) {
            ArrayList<MidCode> part = lAndExps.get(i).getMidCode();
            MidCode midCodeAns = ans.get(ans.size() - 1);
            MidCode midCodePart = part.get(part.size() - 1);
            ans.remove(ans.size() - 1);
            part.remove(part.size() - 1);
            ans.addAll(part);
            String newTemp = Compiler.getNewTemp();
            MidCode orMidCode = new MidCode(OpType.OR, newTemp, midCodeAns.getLeft(), midCodePart.getLeft());
            ans.add(orMidCode);
            ans.add(new MidCode(newTemp));
        }
        return ans;
    }
}
