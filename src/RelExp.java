import java.util.ArrayList;

public class RelExp {
    //关系表达式
    //RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
    //RelExp → AddExp { ('<' | '>' | '<=' | '>=') AddExp }
    private boolean success;
    private ArrayList<AddExp> addExps = new ArrayList<>();
    private ArrayList<Node> nodes = new ArrayList<>();
    private ArrayList<String> characters = new ArrayList<>();

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
            characters.add(Compiler.getSymbol());
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

    public ArrayList<MidCode> getMidCode() {
        ArrayList<MidCode> ans = new ArrayList<>();
        ans.addAll(addExps.get(0).getMidCode());
        if (addExps.size() == 1) {
            return ans;
        }
        for (int i = 1; i < addExps.size(); i++) {
            ArrayList<MidCode> part = addExps.get(i).getMidCode();
            MidCode midCodeAns = ans.get(ans.size() - 1);
            MidCode midCodePart = part.get(part.size() - 1);
            ans.remove(ans.size() - 1);
            part.remove(part.size() - 1);
            ans.addAll(part);
            String character = characters.get(i - 1);
            String newTemp = Compiler.getNewTemp();
            if (character.equals("LSS")) {
                // <
                MidCode lssMidCode = new MidCode(OpType.LSS, newTemp, midCodeAns.getLeft(), midCodePart.getLeft());
                ans.add(lssMidCode);
                ans.add(new MidCode(newTemp));
            } else if (character.equals("LEQ")) {
                // <=
                MidCode leqMidCode = new MidCode(OpType.LEQ, newTemp, midCodeAns.getLeft(), midCodePart.getLeft());
                ans.add(leqMidCode);
                ans.add(new MidCode(newTemp));
            } else if (character.equals("GRE")) {
                // >
                MidCode greMidCode = new MidCode(OpType.GRE, newTemp, midCodeAns.getLeft(), midCodePart.getLeft());
                ans.add(greMidCode);
                ans.add(new MidCode(newTemp));
            } else if (character.equals("GEQ")) {
                // >=
                MidCode geqMidCode = new MidCode(OpType.GEQ, newTemp, midCodeAns.getLeft(), midCodePart.getLeft());
                ans.add(geqMidCode);
                ans.add(new MidCode(newTemp));
            }
        }
        return ans;
    }

}
