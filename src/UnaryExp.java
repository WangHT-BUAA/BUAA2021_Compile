import java.util.ArrayList;

public class UnaryExp {
    //一元表达式
    //UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
    //注意先识别调用函数的Ident '(' [FuncRParams] ')'，再识别基本表达式PrimaryExp
    private boolean success;
    private PrimaryExp primaryExp;

    private Node ident = null;
    private FuncRParams funcRParams;

    private UnaryOp unaryOp;
    private UnaryExp unaryExp;

    private int type = 0;

    private SymbolItem func;

    public UnaryExp() {
        success = doGrammarAnalysis();
    }

    private boolean doGrammarAnalysis() {
        int currentPoint = Compiler.point;
        if (Compiler.getSymbol().equals("IDENFR")) {
            ident = new Node(Compiler.getSymbol(), Compiler.getValue());
            Compiler.point++;
            if (Compiler.getSymbol().equals("LPARENT")) {
                func = Compiler.getErrorFuncItem(ident.getSymbolName());
                if (func == null) {
                    GrammarAnalysis.addError('c', Compiler.getLine());
                    Compiler.point++;
                    funcRParams = new FuncRParams();
                } else {
                    Compiler.point++;
                    funcRParams = new FuncRParams();
                    int rNum = funcRParams.getParamNum();
                    if (rNum != func.getParamNum()) {
                        GrammarAnalysis.addError('d', Compiler.getLine());
                    }
                    ArrayList<SymbolItem> fParams = func.getParamItems();
                    ArrayList<SymbolItem> rParams = funcRParams.getRParams();
                    if (compareFRHasError(rParams, fParams)) {
                        GrammarAnalysis.addError('e', Compiler.getLine());
                    }
                }
                if (!Compiler.getSymbol().equals("RPARENT")) {
                    Compiler.point--;
                    GrammarAnalysis.addError('j', Compiler.getLine());
                }
                type = 2;
                Compiler.point++;
                return true;

            }
        }
        Compiler.point = currentPoint;
        primaryExp = new PrimaryExp();
        if (primaryExp.isSuccess()) {
            type = 1;
            return true;
        }
        Compiler.point = currentPoint;
        unaryOp = new UnaryOp();
        if (unaryOp.isSuccess()) {
            type = 3;
            unaryExp = new UnaryExp();
            if (unaryExp.isSuccess()) {
                return true;
            }
        }
        Compiler.point = currentPoint;
        return false;
    }

    private boolean compareFRHasError(ArrayList<SymbolItem> rParams, ArrayList<SymbolItem> fParams) {
        //true 说明有错
        if (rParams.size() != fParams.size()) {
            return false;
        }
        for (int i = 0; i < rParams.size(); i++) {
            SymbolItem r = rParams.get(i);
            SymbolItem f = fParams.get(i);
            if (r.getType().equals("error")) {
                return true;
            }
            if (r.getDimension() != f.getDimension()) {
                return true;
            }
        }
        return false;
    }

    public SymbolItem getSymbolType() {
        if (type == 1) {
            return primaryExp.getSymbolType();
        } else if (type == 2) {
            //调用函数 返回值只有 int 和 void
            String type = func.getReturnType();
            if (type.equals("VOIDTK")) {
                return new SymbolItem("error", "error", -1, false);
            } else {
                return new SymbolItem("var", func.getInitName(), 0, false);
            }
        } else if (type == 3) {
            return new SymbolItem("var", "op", 0, false);
        }
        return null;
    }

    public boolean isSuccess() {
        return success;
    }

    public String toPrint() {
        //UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
        StringBuffer ans = new StringBuffer();
        if (type == 1) {
            ans.append(primaryExp.toPrint());
        } else if (type == 2) {
            ans.append(ident.toPrint());
            ans.append("LPARENT (\n");
            if (funcRParams.isSuccess()) {
                ans.append(funcRParams.toPrint());
            }
            ans.append("RPARENT )\n");
        } else if (type == 3) {
            ans.append(unaryOp.toPrint());
            ans.append(unaryExp.toPrint());
        }
        ans.append("<UnaryExp>\n");
        return ans.toString();
    }

    public ArrayList<MidCode> getMidCode(String lastFunc) {
        ArrayList<MidCode> ans = new ArrayList<>();
        if (type == 1) {
            ans.addAll(primaryExp.getMidCode(lastFunc));
        } else if (type == 2) {

            String initName = ident.getSymbolName(); //函数名
            SymbolItem item = Compiler.getSymbolItem(initName);
            assert item != null;
            //push s
            ans.add(new MidCode(OpType.PUSH, "ra", lastFunc));
            ArrayList<MidCode> partFuncRParams = funcRParams.getMidCode(lastFunc);//各种需要赋值的没用midCode在最后几个
            int paramNum = item.getParams().size(); //把参数都push进去
            for (int i = 0; i < partFuncRParams.size() - paramNum; i++) {
                ans.add(partFuncRParams.get(i));
            }
            for (int i = partFuncRParams.size() - paramNum; i < partFuncRParams.size(); i++) {
                //todo
                MidCode toPush = partFuncRParams.get(i);
                int index = (i - (partFuncRParams.size() - paramNum) + 1) * 4;
                ans.add(new MidCode(OpType.PUSH, toPush.getLeft(), "" + index, lastFunc));
            }
            String name = item.getName();
            ans.add(new MidCode(OpType.CHANGE_SP, lastFunc, "-")); //更新sp -max
            MidCode callMidCode = new MidCode(OpType.CALL, name); //call
            ans.add(callMidCode);
            ans.add(new MidCode(OpType.CHANGE_SP, lastFunc, "+")); //更新sp +max
            ans.add(new MidCode(OpType.POP, "ra", lastFunc));
            String newName = Compiler.getNewTempOrGlobal();
            ans.add(new MidCode(OpType.ASSIGN, newName, "RET"));
            ans.add(new MidCode(newName));
        } else if (type == 3) {
            ArrayList<MidCode> part = unaryExp.getMidCode(lastFunc);
            MidCode midCodePart = part.get(part.size() - 1);
            part.remove(part.size() - 1);
            ans.addAll(part); //把去掉最后一项的midCode们加入ans
            String op = unaryOp.getOp();
            String newTemp = Compiler.getNewTempOrGlobal();
            if (op.equals("PLUS")) {
                MidCode singleMidCode = new MidCode(OpType.ADD_SINGLE, newTemp, midCodePart.getLeft());
                ans.add(singleMidCode);
                ans.add(new MidCode(newTemp));
            } else if (op.equals("MINU")) {
                MidCode singleMidCode = new MidCode(OpType.SUB_SINGLE, newTemp, midCodePart.getLeft());
                ans.add(singleMidCode);
                ans.add(new MidCode(newTemp));
            } else if (op.equals("NOT")) {
                MidCode singleMidCode = new MidCode(OpType.NOT_SINGLE, newTemp, midCodePart.getLeft());
                ans.add(singleMidCode);
                ans.add(new MidCode(newTemp));
            }
        }
        return ans;
    }

    public int getArrCount() {
        //UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
        if (type == 1) {
            return primaryExp.getArrCount();
        } else if (type == 2) {
            System.out.println("UnaryExp:getArrCountError!!!!");
            return 0;
        } else if (type == 3) {
            String op = unaryOp.getOp();
            if (op.equals("PLUS")) {
                return unaryExp.getArrCount();
            } else if (op.equals("MINU")) {
                return -1 * unaryExp.getArrCount();
            } else if (op.equals("NOT")) {
                if (unaryExp.getArrCount() == 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
        return 0;
    }
}
