import java.util.ArrayList;

public class AddExp {
    //加减表达式
    //AddExp → MulExp | AddExp ('+' | '−') MulExp
    //AddExp → MulExp { ('+' | '−') MulExp }
    private boolean success;
    private ArrayList<MulExp> mulExps = new ArrayList<>();
    private ArrayList<String> characters = new ArrayList<>(); //记录所有的加减号 PLUS MINU
    private ArrayList<Node> nodes = new ArrayList<>();

    public AddExp() {
        success = doGrammarAnalysis();
    }

    private boolean doGrammarAnalysis() {
        int currentPoint = Compiler.point;
        MulExp mulExp = new MulExp();
        if (!mulExp.isSuccess()) {
            Compiler.point = currentPoint;
            return false;
        }
        mulExps.add(mulExp);
        while (Compiler.getSymbol().equals("PLUS") ||
                Compiler.getSymbol().equals("MINU")) {
            characters.add(Compiler.getSymbol());
            nodes.add(new Node(Compiler.getSymbol(), Compiler.getValue()));
            Compiler.point++;
            MulExp newMulExp = new MulExp();
            if (!newMulExp.isSuccess()) {
                Compiler.point = currentPoint;
                return false;
            }
            mulExps.add(newMulExp);
        }
        return true;
    }

    public SymbolItem getSymbolType() {
        SymbolItem basic = mulExps.get(0).getSymbolType();
        if (mulExps.size() == 1) {
            return basic;
        }
        for (MulExp mulExp : mulExps) {
            SymbolItem part = mulExp.getSymbolType();
            if (part.getType().equals("error")) {
                return new SymbolItem("error", "error", -1, true);
            }
            if (part.getDimension() != basic.getDimension()) {
                return new SymbolItem("error", "error", -1, true);
            }
        }
        return basic;
    }

    public boolean isSuccess() {
        return success;
    }

    public String toPrint() {
        //AddExp → MulExp | AddExp ('+' | '−') MulExp
        //AddExp → MulExp { ('+' | '−') MulExp }
        StringBuffer ans = new StringBuffer();
        ans.append(mulExps.get(0).toPrint());
        if (nodes.size() != 0) {
            ans.append("<AddExp>\n");
        }
        for (int i = 0; i < nodes.size(); i++) {
            ans.append(nodes.get(i).toPrint());
            ans.append(mulExps.get(i + 1).toPrint());
            if (i < nodes.size() - 1) {
                ans.append("<AddExp>\n");
            }
        }
        ans.append("<AddExp>\n");
        return ans.toString();
    }

    public ArrayList<MidCode> getMidCode(String lastFunc) {
        ArrayList<MidCode> ans = new ArrayList<>();
        ans.addAll(mulExps.get(0).getMidCode(lastFunc));
        if (mulExps.size() == 1) {
            return ans;
        }
        for (int i = 1; i < mulExps.size(); i++) {
            ArrayList<MidCode> part = mulExps.get(i).getMidCode(lastFunc);
            MidCode midCodeAns = ans.get(ans.size() - 1);
            MidCode midCodePart = part.get(part.size() - 1);
            ans.remove(ans.size() - 1);
            part.remove(part.size() - 1);
            ans.addAll(part);
            String character = characters.get(i - 1);
            String newTemp = Compiler.getNewTemp();
            if (character.equals("PLUS")) {
                MidCode plusMidCode = new MidCode(OpType.ADD, newTemp, midCodeAns.getLeft(), midCodePart.getLeft());
                ans.add(plusMidCode);
                ans.add(new MidCode(newTemp)); // 在最后添上要操作的存数的（无意义但很有用）
            } else if (character.equals("MINU")) {
                MidCode subMidCode = new MidCode(OpType.SUB, newTemp, midCodeAns.getLeft(), midCodePart.getLeft());
                ans.add(subMidCode);
                ans.add(new MidCode(newTemp));
            }
        }
        return ans;
    }

    public int getArrCount() {
        int ans = mulExps.get(0).getArrCount();
        for (int i = 1; i < mulExps.size(); i++) {
            String character = characters.get(i - 1);
            if (character.equals("PLUS")) {
                ans += mulExps.get(i).getArrCount();
            } else if (character.equals("MINU")) {
                ans -= mulExps.get(i).getArrCount();
            }
        }
        return ans;
    }
}
