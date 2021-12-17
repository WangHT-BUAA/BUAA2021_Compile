import java.util.ArrayList;

public class VarDef {
    //变量定义
    //VarDef → Ident { '[' ConstExp ']' } |  Ident { '[' ConstExp ']' } '=' InitVal
    private boolean success;
    private ArrayList<ConstExp> constExps = new ArrayList<>();
    private InitVal initVal;
    private Node ident;
    private String initName;
    private int type = 0;

    public VarDef(int blockNum) {
        success = doGrammarAnalysis(blockNum);
    }

    private boolean doGrammarAnalysis(int blockNum) {
        //todo
        int currentPoint = Compiler.point;
        if (!Compiler.getSymbol().equals("IDENFR")) {
            return false;
        }
        ident = new Node(Compiler.getSymbol(), Compiler.getValue());
        initName = Compiler.getValue();
        Compiler.point++;
        while (Compiler.getSymbol().equals("LBRACK")) {
            Compiler.point++;
            ConstExp constExp = new ConstExp();
            if (!constExp.isSuccess()) {
                Compiler.point = currentPoint;
                return false;
            }
            constExps.add(constExp);
            if (!Compiler.getSymbol().equals("RBRACK")) {
                Compiler.point--;
                GrammarAnalysis.addError('k', Compiler.getLine());
            }
            Compiler.point++;
        }
        if (Compiler.getSymbol().equals("ASSIGN")) {
            type = 1;
            Compiler.point++;
            initVal = new InitVal();
            if (!initVal.isSuccess()) {
                Compiler.point = currentPoint;
                return false;
            }
        }
        if (Compiler.getErrorSymbolItemInBlock(initName, blockNum) != null) {
            GrammarAnalysis.addError('b', Compiler.getLine()); //重定义了
        }
        SymbolItem varSymbolItem = new SymbolItem("var", initName, constExps.size(), false);
        Compiler.pushErrorItemStack(varSymbolItem);
        return true;
    }

    public boolean isSuccess() {
        return success;
    }

    public String toPrint() {
        //VarDef → Ident { '[' ConstExp ']' } |  Ident { '[' ConstExp ']' } '=' InitVal
        StringBuffer ans = new StringBuffer();
        ans.append(ident.toPrint());
        for (ConstExp constExp : constExps) {
            ans.append("LBRACK [\n");
            ans.append(constExp.toPrint());
            ans.append("RBRACK ]\n");
        }
        if (type == 1) {
            ans.append("ASSIGN =\n");
            ans.append(initVal.toPrint());
        }
        ans.append("<VarDef>\n");
        return ans.toString();
    }

    public ArrayList<MidCode> getMidCode(String lastFunc) {
        ArrayList<MidCode> ans = new ArrayList<>();
        if (constExps.size() == 0) {
            //0维
            String name;
            SymbolItem item;
            if (Decl.isGlobal) {
                name = Compiler.getNewGlobal();
                item = new SymbolItem("var", initName, name, false);
                Compiler.pushGlobalItem(item);
            } else {
                name = Compiler.getNewTemp();
                item = new SymbolItem("var", initName, name, false);
                Compiler.pushItemStack(item);
            }
            MidCode defMidCode = new MidCode(OpType.VAR, name, false);// 定义
            ans.add(defMidCode);
            if (type == 1) {
                ArrayList<MidCode> partInitVal = initVal.getMidCode(lastFunc);
                MidCode midCodeInitVal = partInitVal.get(partInitVal.size() - 1);
                partInitVal.remove(partInitVal.size() - 1);
                MidCode assignMidCode = new MidCode(OpType.ASSIGN, name, midCodeInitVal.getLeft());
                ans.addAll(partInitVal);
                ans.add(assignMidCode);
            }
            return ans;
        } else if (constExps.size() == 1) {
            //1维
            int num = constExps.get(0).getArrCount();
            String name;
            SymbolItem item;
            if (Decl.isGlobal) {
                name = Compiler.getNewGlobal();
                item = new SymbolItem("arr", initName, name, false, num);
                Compiler.pushGlobalItem(item);
            } else {
                name = Compiler.getNewTemp();
                item = new SymbolItem("arr", initName, name, false, num);
                Compiler.pushItemStack(item);
            }
            if (type == 1) { // 初始化了，需要挨个赋值
                ArrayList<MidCode> midCodesInit = initVal.getMidCode(lastFunc);
                int index = 0;
                for (MidCode midCode : midCodesInit) {
                    if (midCode.getOpType() == null) {
                        //System.out.println(midCode.getLeft());
                        ans.add(new MidCode(OpType.ASSIGN, name, midCode.getLeft()));
                        if (Decl.isGlobal) {
                            name = Compiler.getNewGlobal();
                        } else {
                            name = Compiler.getNewTemp();
                        }
                        index++;
                    } else {
                        ans.add(midCode);
                    }
                }
                //最后多加了一个 需要减掉
                if (Decl.isGlobal) {
                    Compiler.addGlobalNum(-1);
                } else {
                    Compiler.addTempNum(-1);
                }
                if (index != num) {
                    System.out.println(index + " " + num);
                    System.out.println("ERROR!!! VarDef 1维里初始化个数与数组大小不符！");
                }
            } else { //未初始化，那就只把空间留出来吧
                if (Decl.isGlobal) {
                    Compiler.addGlobalNum(num - 1);
                } else {
                    Compiler.addTempNum(num - 1);
                }
            }
            return ans;
        } else if (constExps.size() == 2) {
            //2维
            int num1 = constExps.get(0).getArrCount();
            int num2 = constExps.get(1).getArrCount();
            String name;
            SymbolItem item;
            if (Decl.isGlobal) {
                name = Compiler.getNewGlobal();
                item = new SymbolItem("arr", initName, name, false, num1, num2);
                Compiler.pushGlobalItem(item);
            } else {
                name = Compiler.getNewTemp();
                item = new SymbolItem("arr", initName, name, false, num1, num2);
                Compiler.pushItemStack(item);
            }
            if (type == 1) { // 初始化了，需要挨个赋值
                ArrayList<MidCode> midCodesInit = initVal.getMidCode(lastFunc);
                int index = 0;
                for (MidCode midCode : midCodesInit) {
                    if (midCode.getOpType() == null) {
                        ans.add(new MidCode(OpType.ASSIGN, name, midCode.getLeft()));
                        if (Decl.isGlobal) {
                            name = Compiler.getNewGlobal();
                        } else {
                            name = Compiler.getNewTemp();
                        }
                        index++;
                    } else {
                        ans.add(midCode);
                    }
                }
                //最后多加了一个 需要减掉
                if (Decl.isGlobal) {
                    Compiler.addGlobalNum(-1);
                } else {
                    Compiler.addTempNum(-1);
                }
                if (index != num1 * num2) {
                    System.out.println("ERROR!!! VarDef 二维里初始化个数与数组大小不符！");
                }
            } else { //未初始化，那就只把空间留出来吧
                if (Decl.isGlobal) {
                    Compiler.addGlobalNum(num1 * num2 - 1);
                } else {
                    Compiler.addTempNum(num1 * num2 - 1);
                }
            }
            return ans;
        }
        return null;
    }
}
