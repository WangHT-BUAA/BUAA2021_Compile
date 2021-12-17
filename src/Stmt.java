import java.util.ArrayList;

public class Stmt {
    //语句
    /*
    Stmt → LVal '=' Exp ';' // 每种类型的语句都要覆盖
     | [Exp] ';' //有⽆Exp两种情况
     | Block
     | 'if' '( Cond ')' Stmt [ 'else' Stmt ] // 1.有else 2.⽆else
     | 'while' '(' Cond ')' Stmt
     | 'break' ';' | 'continue' ';'
     | 'return' [Exp] ';' // 1.有Exp 2.⽆Exp
     | LVal = 'getint''('')'';'
     | 'printf''('FormatString{,Exp}')'';'
     */
    private boolean success;
    private LVal lValEq;
    private Exp expEq;

    private Exp expAlone;

    private Block block;

    private Cond condIf;
    private Stmt stmtIf;
    private Stmt stmtElse;

    private Cond condWhile;
    private Stmt stmtWhile;

    //break/continue
    private Node node;

    private Exp expReturn;

    private LVal lValGetInt;

    private ArrayList<Exp> expsPrint = new ArrayList<>();
    private Node formatString;

    private int type = 0;
    private int flag = 0;

    public Stmt(int blockNum, boolean isInCircle, int noReturn) {
        success = doGrammarAnalysis(blockNum, isInCircle, noReturn);
    }

    private boolean doGrammarAnalysis(int blockNum, boolean isInCircle, int noReturn) {
        int currentPoint = Compiler.point;
        int errorNum = GrammarAnalysis.error.size();
        //LVal '=' Exp ';'
        lValEq = new LVal();
        if (lValEq.isSuccess()) {
            if (Compiler.getSymbol().equals("ASSIGN")) {
                Compiler.point++;
                expEq = new Exp();
                if (expEq.isSuccess()) {
                    SymbolItem symbolItem = lValEq.getSymbolItem();
                    if (symbolItem.isConst()) {
                        GrammarAnalysis.addError('h', Compiler.getLine()); //const被赋值了
                    }
                    if (Compiler.getSymbol().equals("SEMICN")) {
                        Compiler.point++;
                        type = 1;
                        return true;
                    } else {
                        Compiler.point--;
                        GrammarAnalysis.addError('i', Compiler.getLine());
                        Compiler.point++;
                        return true;
                    }
                }
            }
        }
        GrammarAnalysis.popError(errorNum);
        Compiler.point = currentPoint;
        //Block
        block = new Block(Compiler.errorSymbolTable.size(), isInCircle, noReturn);
        if (block.isSuccess()) {
            type = 3;
            return true;
        }
        GrammarAnalysis.popError(errorNum);
        Compiler.point = currentPoint;
        //'if' '( Cond ')' Stmt [ 'else' Stmt ]
        if (Compiler.getSymbol().equals("IFTK")) {
            type = 4;
            Compiler.point++;
            if (Compiler.getSymbol().equals("LPARENT")) {
                Compiler.point++;
                condIf = new Cond();
                if (condIf.isSuccess()) {
                    if (!Compiler.getSymbol().equals("RPARENT")) {
                        Compiler.point--;
                        GrammarAnalysis.addError('j', Compiler.getLine());
                    }
                    Compiler.point++;
                    stmtIf = new Stmt(blockNum, isInCircle, noReturn);
                    if (stmtIf.isSuccess()) {
                        if (Compiler.getSymbol().equals("ELSETK")) {
                            flag = 1;
                            Compiler.point++;
                            stmtElse = new Stmt(blockNum, isInCircle, noReturn);
                            if (stmtElse.isSuccess()) {
                                return true;
                            }
                        } else {
                            return true;
                        }
                    }
                }
            }
        }
        GrammarAnalysis.popError(errorNum);
        Compiler.point = currentPoint;
        //'while' '(' Cond ')' Stmt
        if (Compiler.getSymbol().equals("WHILETK")) {
            Compiler.point++;
            if (Compiler.getSymbol().equals("LPARENT")) {
                Compiler.point++;
                condWhile = new Cond();
                if (condWhile.isSuccess()) {
                    if (!Compiler.getSymbol().equals("RPARENT")) {
                        Compiler.point--;
                        GrammarAnalysis.addError('j', Compiler.getLine());
                    }
                    Compiler.point++;
                    stmtWhile = new Stmt(blockNum, true, noReturn);
                    if (stmtWhile.isSuccess()) {
                        type = 5;
                        return true;
                    }
                }
            }
        }
        GrammarAnalysis.popError(errorNum);
        Compiler.point = currentPoint;
        //'break' ';' | 'continue' ';'
        if (Compiler.getSymbol().equals("BREAKTK") || Compiler.getSymbol().equals("CONTINUETK")) {
            node = new Node(Compiler.getSymbol(), Compiler.getValue());
            Compiler.point++;
            if (!isInCircle) { //不在循环内，报个错
                GrammarAnalysis.addError('m', Compiler.getLine());
            }
            if (Compiler.getSymbol().equals("SEMICN")) {
                type = 6;
                Compiler.point++;
                return true;
            } else {
                Compiler.point--;
                GrammarAnalysis.addError('i', Compiler.getLine());
                Compiler.point++;
                return true;
            }
        }
        GrammarAnalysis.popError(errorNum);
        Compiler.point = currentPoint;
        //'return' [Exp] ';'
        if (Compiler.getSymbol().equals("RETURNTK")) {
            Compiler.point++;
            expReturn = new Exp();
            if (expReturn.isSuccess()) {
                if (noReturn == 1) {
                    GrammarAnalysis.addError('f', Compiler.getLine());
                }
            }
            if (Compiler.getSymbol().equals("SEMICN")) {
                type = 7;
                Compiler.point++;
                return true;
            } else {
                Compiler.point--;
                GrammarAnalysis.addError('i', Compiler.getLine());
                Compiler.point++;
                return true;
            }
        }
        GrammarAnalysis.popError(errorNum);
        Compiler.point = currentPoint;
        //LVal = 'getint''('')'';'
        lValGetInt = new LVal();
        if (lValGetInt.isSuccess()) {
            if (Compiler.getSymbol().equals("ASSIGN")) {
                Compiler.point++;
                if (Compiler.getSymbol().equals("GETINTTK")) {
                    Compiler.point++;

                    SymbolItem symbolItem = lValGetInt.getSymbolItem();
                    if (symbolItem.isConst()) {
                        GrammarAnalysis.addError('h', Compiler.getLine()); //const被赋值了
                    }

                    if (Compiler.getSymbol().equals("LPARENT")) {
                        Compiler.point++;
                        if (!Compiler.getSymbol().equals("RPARENT")) {
                            Compiler.point--;
                            GrammarAnalysis.addError('j', Compiler.getLine());
                        }
                        Compiler.point++;
                        if (Compiler.getSymbol().equals("SEMICN")) {
                            type = 8;
                            Compiler.point++;
                            return true;
                        } else {
                            Compiler.point--;
                            GrammarAnalysis.addError('i', Compiler.getLine());
                            Compiler.point++;
                            return true;
                        }
                    }
                }
            }
        }
        GrammarAnalysis.popError(errorNum);
        Compiler.point = currentPoint;
        //'printf''('FormatString{,Exp}')'';'
        if (Compiler.getSymbol().equals("PRINTFTK")) {
            Compiler.point++;
            if (Compiler.getSymbol().equals("LPARENT")) {
                Compiler.point++;
                if (Compiler.getSymbol().equals("STRCON")) {
                    formatString = new Node(Compiler.getSymbol(), Compiler.getValue());
                    if (isFormatStringError(Compiler.getValue())) {
                        GrammarAnalysis.addError('a', Compiler.getLine());
                    }
                    Compiler.point++;
                    int numOfExp = 0;
                    while (Compiler.getSymbol().equals("COMMA")) {
                        Compiler.point++;
                        Exp newExp = new Exp();
                        if (!newExp.isSuccess()) {
                            Compiler.point = currentPoint;
                            return false;
                        }
                        expsPrint.add(newExp);
                        numOfExp++;
                    }
                    if (numOfExp != formatString.getNumOfPercent()) { //printf中格式字符与表达式个数不匹配
                        GrammarAnalysis.addError('l', Compiler.getLine());
                    }
                    if (!Compiler.getSymbol().equals("RPARENT")) {
                        Compiler.point--;
                        GrammarAnalysis.addError('j', Compiler.getLine());
                    }
                    Compiler.point++;
                    if (Compiler.getSymbol().equals("SEMICN")) {
                        type = 9;
                        Compiler.point++;
                        return true;
                    } else {
                        Compiler.point--;
                        GrammarAnalysis.addError('i', Compiler.getLine());
                        Compiler.point++;
                        return true;
                    }
                }
            }
        }
        GrammarAnalysis.popError(errorNum);
        Compiler.point = currentPoint;
        //[Exp] ';'
        expAlone = new Exp();
        if (!expAlone.isSuccess()) {
            Compiler.point = currentPoint;
        } else {
            if (Compiler.getSymbol().equals("SEMICN")) {
                Compiler.point++;
                type = 2;
                return true;
            } else {
                Compiler.point--;
                GrammarAnalysis.addError('i', Compiler.getLine());
                Compiler.point++;
                return true;
            }
        }
        if (Compiler.getSymbol().equals("SEMICN")) {
            Compiler.point++;
            type = 2;
            return true;
        }

        Compiler.point = currentPoint;
        return false;
    }

    public int getType() {
        if (expReturn == null) {
            return -1;
        }
        if (expReturn.isSuccess()) {
            return 8; //return 有返回值
        }
        return type;
    }

    private boolean isFormatStringError(String s) {
        for (int i = 1; i < s.length() - 1; i++) {
            char c = s.charAt(i);
            if (c == ' ' || c == '!' || (c >= '(' && c <= '~') || (c == '%' && s.charAt(i + 1) == 'd')) {
                continue;
            } else {
                return true; //出错了
            }
        }
        return false;
    }

    public boolean isSuccess() {
        return success;
    }

    public String toPrint() {
        /*
    Stmt → LVal '=' Exp ';' // 每种类型的语句都要覆盖
     | [Exp] ';' //有⽆Exp两种情况
     | Block
     | 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // 1.有else 2.⽆else
     | 'while' '(' Cond ')' Stmt
     | 'break' ';' | 'continue' ';'
     | 'return' [Exp] ';' // 1.有Exp 2.⽆Exp
     | LVal = 'getint''('')'';'
     | 'printf''('FormatString{,Exp}')'';'
     */
        StringBuffer ans = new StringBuffer();
        if (type == 1) {
            ans.append(lValEq.toPrint());
            ans.append("ASSIGN =\n");
            ans.append(expEq.toPrint());
            ans.append("SEMICN ;\n");
        } else if (type == 2) {
            if (expAlone.isSuccess()) {
                ans.append(expAlone.toPrint());
            }
            ans.append("SEMICN ;\n");
        } else if (type == 3) {
            ans.append(block.toPrint());
        } else if (type == 4) {
            ans.append("IFTK if\n");
            ans.append("LPARENT (\n");
            ans.append(condIf.toPrint());
            ans.append("RPARENT )\n");
            ans.append(stmtIf.toPrint());
            if (flag == 1) {
                ans.append("ELSETK else\n");
                ans.append(stmtElse.toPrint());
            }
        } else if (type == 5) {
            ans.append("WHILETK while\n");
            ans.append("LPARENT (\n");
            ans.append(condWhile.toPrint());
            ans.append("RPARENT )\n");
            ans.append(stmtWhile.toPrint());
        } else if (type == 6) {
            ans.append(node.toPrint());
            ans.append("SEMICN ;\n");
        } else if (type == 7) {
            ans.append("RETURNTK return\n");
            if (expReturn.isSuccess()) {
                ans.append(expReturn.toPrint());
            }
            ans.append("SEMICN ;\n");
        } else if (type == 8) {
            ans.append(lValGetInt.toPrint());
            ans.append("ASSIGN =\n");
            ans.append("GETINTTK getint\n");
            ans.append("LPARENT (\n");
            ans.append("RPARENT )\n");
            ans.append("SEMICN ;\n");
        } else if (type == 9) {
            ans.append("PRINTFTK printf\n");
            ans.append("LPARENT (\n");
            ans.append(formatString.toPrint());
            for (Exp exp : expsPrint) {
                ans.append("COMMA ,\n");
                ans.append(exp.toPrint());
            }
            ans.append("RPARENT )\n");
            ans.append("SEMICN ;\n");
        }
        ans.append("<Stmt>\n");
        return ans.toString();
    }

    public ArrayList<MidCode> getMidCode(String whileLabel, String lastFunc) {
        ArrayList<MidCode> ans = new ArrayList<>();
        if (type == 1) {
            ArrayList<MidCode> partLVal = lValEq.getMidCode(lastFunc);
            ArrayList<MidCode> partExp = expEq.getMidCode(lastFunc);
            MidCode midCodeLVal = partLVal.get(partLVal.size() - 1);
            MidCode midCodeExp = partExp.get(partExp.size() - 1);
            partExp.remove(partExp.size() - 1);
            partLVal.remove(partLVal.size() - 1);
            MidCode assignMidCode = new MidCode(OpType.ASSIGN, midCodeLVal.getLeft(), midCodeExp.getLeft());
            ans.addAll(partExp);
            ans.addAll(partLVal);
            ans.add(assignMidCode);
            return ans;
        } else if (type == 2) {
            if (!expAlone.isSuccess()) {
                return ans;
            }
            ArrayList<MidCode> partExp = expAlone.getMidCode(lastFunc);
            if (partExp.get(partExp.size() - 1).getOpType() == null) {
                partExp.remove(partExp.size() - 1);
            }
            ans.addAll(partExp);
            return ans;
        } else if (type == 3) {
            int index = Compiler.symbolTable.size();
            ArrayList<MidCode> midCodes = block.getMidCode(whileLabel, lastFunc);
            Compiler.popSymbolTable(index);//维护符号表，pop掉block里的变量直到恢复到入块之前的大小
            return midCodes;
        } else if (type == 4) {
            //'if' '( Cond ')' Stmt [ 'else' Stmt ]
            String startName = Compiler.getNewIf(); //if1
            String endName = startName + "_end";
            String beginLabel = startName + "_begin";
            MidCode startLabel = new MidCode(OpType.LABEL, startName);
            ans.add(startLabel);
            ArrayList<MidCode> condMidCodes = condIf.getMidCode(lastFunc, endName, beginLabel);
            //MidCode midCodeCond = condMidCodes.get(condMidCodes.size() - 1);
            //condMidCodes.remove(condMidCodes.size() - 1);
            //MidCode beqMidCode = new MidCode(OpType.BEQ, midCodeCond.getLeft(), "$0", endName);
            ans.addAll(condMidCodes);
            //ans.add(beqMidCode);
            ans.add(new MidCode(OpType.LABEL, beginLabel));
            ans.addAll(stmtIf.getMidCode(whileLabel, lastFunc));
            if (flag == 0) {
                ans.add(new MidCode(OpType.LABEL, endName));
            } else {
                String elseName = startName + "_else_end";
                ans.add(new MidCode(OpType.JUMP, elseName));
                ans.add(new MidCode(OpType.LABEL, endName));
                ans.addAll(stmtElse.getMidCode(whileLabel, lastFunc));
                ans.add(new MidCode(OpType.LABEL, elseName));
            }
            return ans;
        } else if (type == 5) {
            //todo while
            //'while' '(' Cond ')' Stmt
            String startName = Compiler.getNewWhile();
            String endName = startName + "_end";
            String beginLabel = startName + "_begin";
            ans.add(new MidCode(OpType.LABEL, startName));
            ArrayList<MidCode> condMidCodes = condWhile.getMidCode(lastFunc, endName, beginLabel);
            //MidCode midCodeCond = condMidCodes.get(condMidCodes.size() - 1);
            //condMidCodes.remove(condMidCodes.size() - 1);
            ans.addAll(condMidCodes);
            //ans.add(new MidCode(OpType.BEQ, midCodeCond.getLeft(), "$0", endName));
            ans.add(new MidCode(OpType.LABEL, beginLabel));
            ans.addAll(stmtWhile.getMidCode(startName, lastFunc));
            ans.add(new MidCode(OpType.JUMP, startName));
            ans.add(new MidCode(OpType.LABEL, endName));
            return ans;
        } else if (type == 6) {
            //todo break continue
            if (whileLabel == null) {
                System.out.println("error in stmt : break & continue");
            }
            if (node.getSymbolName().equals("break")) {
                ans.add(new MidCode(OpType.JUMP, whileLabel + "_end"));
            } else {
                ans.add(new MidCode(OpType.JUMP, whileLabel));
            }
            return ans;
        } else if (type == 7) {
            if (expReturn.isSuccess()) {
                ArrayList<MidCode> partExp = expReturn.getMidCode(lastFunc);
                MidCode midCodeExp = partExp.get(partExp.size() - 1);
                partExp.remove(partExp.size() - 1);
                MidCode returnMidCode = new MidCode(OpType.RETURN, midCodeExp.getLeft());
                ans.addAll(partExp);
                ans.add(returnMidCode);
                return ans;
            } else {
                ans.add(new MidCode(OpType.RETURN, "NULL"));
                return ans;
            }
        } else if (type == 8) {
            //getint
            ArrayList<MidCode> partLVal = lValGetInt.getMidCode(lastFunc);
            MidCode midCodeLVal = partLVal.get(partLVal.size() - 1);
            partLVal.remove(partLVal.size() - 1);
            ans.addAll(partLVal);
            MidCode getIntMidCode = new MidCode(OpType.GETINT, midCodeLVal.getLeft());
            ans.add(getIntMidCode);
            return ans;
        } else if (type == 9) {
            //print
            String toPrint = formatString.getSymbolName();
            String str = toPrint.replaceAll("%d", "\\$%d\\$");
            str = str.substring(1, str.length() - 1); //删去开头结尾的 "
            String[] arr = str.split("\\$");
            int i = 0;
            for (String s : arr) {
                if (s.equals("%d")) {
                    ArrayList<MidCode> part = expsPrint.get(i++).getMidCode(lastFunc);
                    MidCode midCode = part.get(part.size() - 1);
                    part.remove(part.size() - 1);
                    ans.addAll(part);
                    ans.add(new MidCode(OpType.PRINT, midCode.getLeft(), "int"));
                } else {
                    if (Compiler.printStr.containsKey(s)) {
                        ans.add(new MidCode(OpType.PRINT, Compiler.printStr.get(s), "str"));
                    } else {
                        String name = Compiler.getNewStr();
                        Compiler.printStr.put(s, name);
                        ans.add(new MidCode(OpType.PRINT, name, "str"));
                    }
                }
            }
            return ans;
        }
        return null;
    }
}
