import java.util.ArrayList;

public class FuncDef {
    //函数定义
    //FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
    private boolean success;
    private FuncType funcType;
    private FuncFParams funcFParams;
    private Block block;
    private Node ident;

    private int noReturn = 0; //0 代表不在函数block里；1 代表函数是void；2 代表函数是int

    public FuncDef(int blockNum) {
        success = doGrammarAnalysis(blockNum);
    }

    private boolean doGrammarAnalysis(int blockNum) {
        int currentPoint = Compiler.point;
        funcType = new FuncType();
        if (!funcType.isSuccess()) {
            Compiler.point = currentPoint;
            noReturn = 0;
            return false;
        }
        if (funcType.getFuncType().equals("INT")) {
            noReturn = 2;
        } else {
            noReturn = 1;
        }
        if (Compiler.getSymbol().equals("IDENFR")) {
            ident = new Node(Compiler.getSymbol(), Compiler.getValue());
            int line = Compiler.getLine();
            Compiler.point++;
            if (Compiler.getSymbol().equals("LPARENT")) {
                Compiler.point++;
                funcFParams = new FuncFParams(blockNum);  //形参
                if (!Compiler.getSymbol().equals("RPARENT")) {
                    Compiler.point--;
                    GrammarAnalysis.addError('j', Compiler.getLine());
                }
                Compiler.point++;
                if (Compiler.getErrorFuncItem(ident.getSymbolName()) != null) {
                    GrammarAnalysis.addError('b', line); //重定义了
                }
                SymbolItem funcSymbolItem = new SymbolItem("func", ident.getSymbolName(), funcType.getFuncType(), funcFParams.getParamNum(), funcFParams.getParams());
                Compiler.errorFunTable.push(funcSymbolItem);
                block = new Block(Compiler.errorSymbolTable.size() - funcFParams.getParamNum(), false, noReturn);
                if (block.isSuccess()) {
                    int type = block.getLastStmtType();
                    if (noReturn == 2 && type != 8) {
                        Compiler.point--;
                        GrammarAnalysis.addError('g', Compiler.getLine());
                        Compiler.point++;
                    }

                    //System.out.println(ident.getSymbolName() + " " + Compiler.errorBlockNum);

                    return true;
                }
            }
        }
        Compiler.point = currentPoint;
        return false;
    }

    public boolean isSuccess() {
        return success;
    }

    public String toPrint() {
        //FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
        StringBuffer ans = new StringBuffer();
        ans.append(funcType.toPrint());
        ans.append(ident.toPrint());
        ans.append("LPARENT" + " " + "(\n");
        if (funcFParams.isSuccess()) {
            ans.append(funcFParams.toPrint());
        }
        ans.append("RPARENT" + " " + ")\n");
        ans.append(block.toPrint());
        ans.append("<FuncDef>\n");
        return ans.toString();
    }

    public ArrayList<MidCode> getMidCode() {
        ArrayList<MidCode> ans = new ArrayList<>();
        String type = funcType.getFuncType();
        String initName = ident.getSymbolName();
        String name = Compiler.getNewFunc();
        SymbolItem funcItem = new SymbolItem("func", initName, name, type); //函数定义声明
        Compiler.pushItemStack(funcItem); //加入符号表
        int index = Compiler.symbolTable.size();
        MidCode funcMidCode = new MidCode(OpType.FUNC, name);
        ans.add(funcMidCode);
        ArrayList<MidCode> paraMidCode = funcFParams.getMidCode(); //所有的形参
        for (MidCode midCode : paraMidCode) {
            funcItem.addParam(midCode.getLeft()); //把形参加入到函数symbol的属性中
        }
        ans.addAll(paraMidCode);
        ans.addAll(block.getMidCode(null));
        for (int i = index + 1; i < Compiler.symbolTable.size(); i++) {
            funcItem.addTemp(Compiler.symbolTable.get(i).getName());
        }
        Compiler.popSymbolTable(index);//维护符号表，pop掉block里的变量直到恢复到入块之前的大小
        ans.add(new MidCode(OpType.FUNC_END, name));
        return ans;
    }
}
