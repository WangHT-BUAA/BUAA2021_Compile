import java.util.ArrayList;

public class LVal {
    //左值表达式
    //LVal → Ident {'[' Exp ']'}
    private boolean success;
    private ArrayList<Exp> exps = new ArrayList<>();
    private Node ident;
    private String initName;

    private SymbolItem symbolItem;

    public LVal() {
        success = doGrammarAnalysis();
    }

    private boolean doGrammarAnalysis() {
        int currentPoint = Compiler.point;
        if (Compiler.getSymbol().equals("IDENFR")) {
            ident = new Node(Compiler.getSymbol(), Compiler.getValue());
            initName = Compiler.getValue();
            symbolItem = Compiler.getErrorSymbolItem(initName);
            if (symbolItem == null) {
                GrammarAnalysis.addError('c', Compiler.getLine());
                //System.out.println("here" + Compiler.point);
            }
            Compiler.point++;
            while (Compiler.getSymbol().equals("LBRACK")) {
                Compiler.point++;
                Exp exp = new Exp();
                if (!exp.isSuccess()) {
                    Compiler.point = currentPoint;
                    return false;
                }
                exps.add(exp);
                if (!Compiler.getSymbol().equals("RBRACK")) {
                    Compiler.point--;
                    GrammarAnalysis.addError('k', Compiler.getLine());
                }
                Compiler.point++;
            }
            return true;
        }
        Compiler.point = currentPoint;
        return false;
    }

    public SymbolItem getSymbolType() {
        if (symbolItem == null) {
            return new SymbolItem("error", "error", -1, false);
        }
        int dimension = symbolItem.getDimension();
        return new SymbolItem("var", symbolItem.getInitName(), dimension - exps.size(), symbolItem.isConst());
    }

    public SymbolItem getSymbolItem() {
        if (symbolItem == null) {
            return new SymbolItem("error", "error", -1, false);
        }
        return symbolItem;
    }

    public boolean isSuccess() {
        return success;
    }

    public String toPrint() {
        //LVal → Ident {'[' Exp ']'}
        StringBuffer ans = new StringBuffer();
        ans.append(ident.toPrint());
        for (Exp exp : exps) {
            ans.append("LBRACK [\n");
            ans.append(exp.toPrint());
            ans.append("RBRACK ]\n");
        }
        ans.append("<LVal>\n");
        return ans.toString();
    }

    public ArrayList<MidCode> getMidCode(String lastFunc) {
        ArrayList<MidCode> ans = new ArrayList<>();
        SymbolItem item = Compiler.getSymbolItem(this.initName);
        if (exps.size() == 0) {
            // 这就是个0维变量调用
            assert item != null;
            if (item.getIndex1() != null) {
                ans.add(new MidCode(item.getName() + "_arr"));
            } else {
                ans.add(new MidCode(item.getName()));
            }
            return ans;
        } else if (exps.size() == 1) {
            ans.addAll(exps.get(0).getMidCode(lastFunc));
            MidCode midCode = ans.get(ans.size() - 1);
            ans.remove(ans.size() - 1);
            String indexTemp = Compiler.getNewTempOrGlobal();
            ans.add(new MidCode(OpType.ASSIGN, indexTemp, midCode.getLeft()));
            if (item.getIndex2() != null) {
                String newTemp = Compiler.getNewTempOrGlobal();
                ans.add(new MidCode(OpType.ASSIGN, newTemp, item.getIndex2()));
                ans.add(new MidCode(OpType.MUL, indexTemp, indexTemp, newTemp));
                ans.add(new MidCode(item.getName() + "_" + indexTemp + "_arr"));
            } else {
                ans.add(new MidCode(item.getName() + "_" + indexTemp)); //没乘4
            }
        } else if (exps.size() == 2) {
            ans.addAll(exps.get(0).getMidCode(lastFunc));
            MidCode midCode1 = ans.get(ans.size() - 1);
            ans.remove(ans.size() - 1);
            ans.addAll(exps.get(1).getMidCode(lastFunc));
            MidCode midCode2 = ans.get(ans.size() - 1);
            ans.remove(ans.size() - 1);

            int index = item.getIndex2();
            String newTemp = Compiler.getNewTempOrGlobal();
            String temp = Compiler.getNewTempOrGlobal();
            String indexTemp = Compiler.getNewTempOrGlobal();
            ans.add(new MidCode(OpType.IMM, newTemp, "" + index));
            ans.add(new MidCode(OpType.MUL, temp, newTemp, midCode1.getLeft()));
            ans.add(new MidCode(OpType.ADD, indexTemp, temp, midCode2.getLeft()));
            ans.add(new MidCode(item.getName() + "_" + indexTemp));
        }


        return ans;
    }

    public int getArrCount() {
        SymbolItem item = Compiler.getConstItem(this.initName);
        if (exps.size() == 0) {
            //System.out.println(item.getInitName() + " " + item.getConstNum());
            return item.getConstNum();
        } else if (exps.size() == 1) {
            int num = exps.get(0).getArrCount();
            return item.getConstNumAt(num);
        } else if (exps.size() == 2) {
            int num1 = exps.get(0).getArrCount();
            int num2 = exps.get(1).getArrCount();
            return item.getConstNumAt(num1, num2);
        }
        return 0;
    }
}
