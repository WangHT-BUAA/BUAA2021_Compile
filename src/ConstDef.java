import java.util.ArrayList;

public class ConstDef {
    //常数定义
    //ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal
    private boolean success;
    private ArrayList<ConstExp> constExps = new ArrayList<>();
    private ConstInitVal constInitVal;
    private Node node;

    public ConstDef(int blockNum) {
        this.success = doGrammarAnalysis(blockNum);
    }

    private boolean doGrammarAnalysis(int blockNum) {
        int currentPoint = Compiler.point;
        if (Compiler.symbols.get(Compiler.point).equals("IDENFR")) {
            node = new Node("IDENFR", Compiler.getValue());
            Compiler.point++;
            int flag = 0;
            while (true) {
                if (!Compiler.symbols.get(Compiler.point).equals("LBRACK")) {
                    flag = 1;
                    break;
                }
                Compiler.point++;
                ConstExp constExp = new ConstExp();
                if (!constExp.isSuccess()) {
                    flag = 2;
                    break;
                }
                constExps.add(constExp);
                if (!Compiler.symbols.get(Compiler.point).equals("RBRACK")) {
                    Compiler.point--;
                    GrammarAnalysis.addError('k', Compiler.getLine());
                }
                Compiler.point++;
            }
            if (flag != 1) {
                //进去但是不是从[出来的
                Compiler.point = currentPoint;
                return false;
            }
            if (!Compiler.symbols.get(Compiler.point).equals("ASSIGN")) {
                Compiler.point = currentPoint;
                return false;
            }
            Compiler.point++;
            constInitVal = new ConstInitVal();
            if (!constInitVal.isSuccess()) {
                Compiler.point = currentPoint;
                return false;
            }
            if (Compiler.getErrorSymbolItemInBlock(node.getSymbolName(), blockNum) != null) {
                GrammarAnalysis.addError('b', Compiler.getLine()); //名字重定义了
            }
            SymbolItem constSymbolItem = new SymbolItem("var", node.getSymbolName(), 0, true);
            Compiler.pushErrorItemStack(constSymbolItem);
            return true;
        }
        return false;
    }

    public boolean isSuccess() {
        return success;
    }

    public String toPrint() {
        //ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal
        StringBuffer ans = new StringBuffer();
        ans.append(node.toPrint());
        for (ConstExp constExp : constExps) {
            ans.append("LBRACK [\n");
            ans.append(constExp.toPrint());
            ans.append("RBRACK ]\n");
        }
        ans.append("ASSIGN =\n");
        ans.append(constInitVal.toPrint());
        ans.append("<ConstDef>\n");
        return ans.toString();
    }

    public ArrayList<MidCode> getMidCode(String lastFunc) {
        ArrayList<MidCode> ans = new ArrayList<>();
        String initName = node.getSymbolName();
        switch (constExps.size()) {
            case 0: {
                String newName;
                SymbolItem constItem;
                if (Decl.isGlobal) {
                    newName = Compiler.getNewGlobal();
                    constItem = new SymbolItem("int", initName, newName, true);
                    Compiler.pushGlobalItem(constItem);
                } else {
                    newName = Compiler.getNewTemp();
                    constItem = new SymbolItem("int", initName, newName, true);
                    Compiler.pushItemStack(constItem); //常量压入符号表
                }
                int value = constInitVal.getArrCounts().get(0);
                //ArrayList<MidCode> midCodesInit = constInitVal.getMidCode(lastFunc);
                constItem.setConstNum(value);
                MidCode defMidCode = new MidCode(OpType.CONST, newName, true); //中间代码先定义
                ans.add(defMidCode);
                MidCode assignMidCode = new MidCode(OpType.ASSIGN, newName, value);//再赋值 直接赋值一个常数
                ans.add(assignMidCode);
                break;
            }
            case 1: {
                String newName;
                SymbolItem constItem;
                int num = constExps.get(0).getArrCount();
                if (Decl.isGlobal) {
                    newName = Compiler.getNewGlobal();
                    constItem = new SymbolItem("arr", initName, newName, true, num);
                    Compiler.pushGlobalItem(constItem);
                } else {
                    newName = Compiler.getNewTemp();
                    constItem = new SymbolItem("arr", initName, newName, true, num);
                    Compiler.pushItemStack(constItem); //常量压入符号表
                }
                ArrayList<Integer> values = constInitVal.getArrCounts();
                constItem.setConstNums(values);
                for (int i = 0; i < values.size(); i++) {
                    int value = values.get(i);
                    ans.add(new MidCode(OpType.ASSIGN, newName, value));
                    if (i < values.size() - 1) {
                        if (Decl.isGlobal) {
                            newName = Compiler.getNewGlobal();
                        } else {
                            newName = Compiler.getNewTemp();
                        }
                    }
                }
                break;
            }
            case 2: {
                String newName;
                SymbolItem constItem;
                int num1 = constExps.get(0).getArrCount();
                int num2 = constExps.get(1).getArrCount();
                if (Decl.isGlobal) {
                    newName = Compiler.getNewGlobal();
                    constItem = new SymbolItem("arr", initName, newName, true, num1, num2);
                    Compiler.pushGlobalItem(constItem);
                } else {
                    newName = Compiler.getNewTemp();
                    constItem = new SymbolItem("arr", initName, newName, true, num1, num2);
                    Compiler.pushItemStack(constItem); //常量压入符号表
                }
                ArrayList<Integer> values = constInitVal.getArrCounts();
                constItem.setConstNums(values);
                for (int i = 0; i < values.size(); i++) {
                    int value = values.get(i);
                    ans.add(new MidCode(OpType.ASSIGN, newName, value));
                    if (i < values.size() - 1) {
                        if (Decl.isGlobal) {
                            newName = Compiler.getNewGlobal();
                        } else {
                            newName = Compiler.getNewTemp();
                        }
                    }
                }
                break;
            }
        }
        return ans;
    }
}
