import java.util.ArrayList;

public class MulExp {
    //乘除模表达式
    //MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
    //MulExp → UnaryExp { ('*' | '/' | '%') UnaryExp }
    private boolean success;
    private ArrayList<UnaryExp> unaryExps = new ArrayList<>();
    private ArrayList<String> characters = new ArrayList<>();
    private ArrayList<Node> nodes = new ArrayList<>();

    public MulExp() {
        success = doGrammarAnalysis();
    }

    private boolean doGrammarAnalysis() {
        int currentPoint = Compiler.point;
        UnaryExp unaryExp = new UnaryExp();
        if (!unaryExp.isSuccess()) {
            Compiler.point = currentPoint;
            return false;
        }
        unaryExps.add(unaryExp);
        while (Compiler.getSymbol().equals("MULT") ||
                Compiler.getSymbol().equals("DIV") ||
                Compiler.getSymbol().equals("MOD")) {
            characters.add(Compiler.getSymbol());
            nodes.add(new Node(Compiler.getSymbol(), Compiler.getValue()));
            Compiler.point++;
            UnaryExp newUnaryExp = new UnaryExp();
            if (!newUnaryExp.isSuccess()) {
                Compiler.point = currentPoint;
                return false;
            }
            unaryExps.add(newUnaryExp);
        }
        return true;
    }

    public SymbolItem getSymbolType() {
        SymbolItem basic = unaryExps.get(0).getSymbolType();
        if (unaryExps.size() == 1) {
            return basic;
        }
        for (UnaryExp unaryExp : unaryExps) {
            SymbolItem part = unaryExp.getSymbolType();
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
        //MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
        //MulExp → UnaryExp { ('*' | '/' | '%') UnaryExp }
        StringBuffer ans = new StringBuffer();
        ans.append(unaryExps.get(0).toPrint());
        if (nodes.size() != 0) {
            ans.append("<MulExp>\n");
        }
        for (int i = 0; i < nodes.size(); i++) {
            ans.append(nodes.get(i).toPrint());
            ans.append(unaryExps.get(i + 1).toPrint());
            if (i < nodes.size() - 1) {
                ans.append("<MulExp>\n");
            }
        }
        ans.append("<MulExp>\n");
        return ans.toString();
    }

    public ArrayList<MidCode> getMidCode() {
        ArrayList<MidCode> ans = new ArrayList<>();
        ans.addAll(unaryExps.get(0).getMidCode());
        if (unaryExps.size() == 1) {
            return ans;
        }
        for (int i = 1; i < unaryExps.size(); i++) {
            ArrayList<MidCode> part = unaryExps.get(i).getMidCode();
            MidCode midCodeAns = ans.get(ans.size() - 1);
            MidCode midCodePart = part.get(part.size() - 1);
            ans.remove(ans.size() - 1);
            part.remove(part.size() - 1);
            ans.addAll(part);
            String character = characters.get(i - 1);
            String newTemp = Compiler.getNewTemp();
            if (character.equals("MULT")) {
                MidCode mulMidCode = new MidCode(OpType.MUL, newTemp, midCodeAns.getLeft(), midCodePart.getLeft());
                ans.add(mulMidCode);
                ans.add(new MidCode(newTemp));
            } else if (character.equals("DIV")) {
                MidCode divMidCode = new MidCode(OpType.DIV, newTemp, midCodeAns.getLeft(), midCodePart.getLeft());
                ans.add(divMidCode);
                ans.add(new MidCode(newTemp));
            } else if (character.equals("MOD")) {
                MidCode modMidCode = new MidCode(OpType.MOD, newTemp, midCodeAns.getLeft(), midCodePart.getLeft());
                ans.add(modMidCode);
                ans.add(new MidCode(newTemp));
            }
        }
        return ans;
    }
}
