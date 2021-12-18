import java.util.ArrayList;
import java.util.HashMap;

public class LexAnalysis {
    private int point;
    private char current;
    private int maxLength;
    private int line;
    private int column;
    private String totalString = null;
    private ArrayList<String> before = new ArrayList<>();
    private ArrayList<String> after = new ArrayList<>();
    private ArrayList<Integer> lineNumbers = new ArrayList<>();

    private HashMap<String, String> symToString;
    private HashMap<String, String> keywordsToString;

    public LexAnalysis(String initString) {
        totalString = initString;
        point = 0;
        line = 1;
        column = 0;
        maxLength = initString.length();
        symToString = new HashMap<String, String>() {
            {
                put("!", "NOT");
                put("&&", "AND");
                put("||", "OR");
                put("+", "PLUS");
                put("-", "MINU");
                put("*", "MULT");
                put("/", "DIV");
                put("%", "MOD");
                put("<", "LSS");
                put("<=", "LEQ");
                put(">", "GRE");
                put(">=", "GEQ");
                put("==", "EQL");
                put("!=", "NEQ");
                put("=", "ASSIGN");
                put(";", "SEMICN");
                put(",", "COMMA");
                put("(", "LPARENT");
                put(")", "RPARENT");
                put("[", "LBRACK");
                put("]", "RBRACK");
                put("{", "LBRACE");
                put("}", "RBRACE");
            }
        };
        keywordsToString = new HashMap<String, String>() {
            {
                put("main", "MAINTK");
                put("const", "CONSTTK");
                put("int", "INTTK");
                put("break", "BREAKTK");
                put("continue", "CONTINUETK");
                put("if", "IFTK");
                put("else", "ELSETK");
                put("while", "WHILETK");
                put("getint", "GETINTTK");
                put("printf", "PRINTFTK");
                put("return", "RETURNTK");
                put("void", "VOIDTK");
            }
        };
    }

    private void getNextChar() throws NullPointerException {
        point++;
        if (point >= maxLength) {
            throw new NullPointerException();
        }
        current = totalString.charAt(point);
    }

    public void doLexAnalysis() throws NullPointerException {
        while (point < maxLength) {
            int status = getSymbol();
            if (status == 0) {
                throw new NullPointerException();
            }
        }
    }

    /*
    1 成功
    0 失败
    */
    private int getSymbol() throws NullPointerException {
        current = totalString.charAt(point);
        //略去symbol前的空白字符，有换行符则line++
        while (isWhiteCharacter(current)) {
            if (isEnterCharacter(current)) {
                line++;
            }
            getNextChar();
        }
        //如果第一个字符是数字
        if (isNumber(current)) {
            return analyseNumber();
        }
        //如果第一个字符是引号
        if (current == '"') {
            return analyseFormatString();
        }
        //如果第一个字符不是字母也不是下划线
        if (!isLetter(current) && current != '_') {
            return analyseMark();
        }
        //如果第一个字符是下划线
        if (current == '_') {
            return analyseIdent();
        }
        //如果第一个字符是字母
        if (isLetter(current)) {
            return analyseKeywordOrIdent();
        }
        return 0;
    }

    private int analyseKeywordOrIdent() throws NullPointerException {
        StringBuffer tempString = new StringBuffer();
        //m : main
        if (current == 'm') {
            tempString.append(current);
            getNextChar();
            if (current == 'a') {
                tempString.append(current);
                getNextChar();
                if (current == 'i') {
                    tempString.append(current);
                    getNextChar();
                    if (current == 'n') {
                        tempString.append(current);
                        getNextChar();
                        if (!isIdentifier(current)) {
                            before.add(tempString.toString());
                            after.add(keywordsToString.get(tempString.toString()));
                            lineNumbers.add(line);
                            return 1;
                        }
                    }
                }
            }
        }
        //c : const, continue
        else if (current == 'c') {
            tempString.append(current);
            getNextChar();
            if (current == 'o') {
                tempString.append(current);
                getNextChar();
                if (current == 'n') {
                    tempString.append(current);
                    getNextChar();
                    if (current == 's') {
                        tempString.append(current);
                        getNextChar();
                        if (current == 't') {
                            tempString.append(current);
                            getNextChar();
                            if (!isIdentifier(current)) {
                                before.add(tempString.toString());
                                after.add(keywordsToString.get(tempString.toString()));
                                lineNumbers.add(line);
                                return 1;
                            }
                        }
                    } else if (current == 't') {
                        tempString.append(current);
                        getNextChar();
                        if (current == 'i') {
                            tempString.append(current);
                            getNextChar();
                            if (current == 'n') {
                                tempString.append(current);
                                getNextChar();
                                if (current == 'u') {
                                    tempString.append(current);
                                    getNextChar();
                                    if (current == 'e') {
                                        tempString.append(current);
                                        getNextChar();
                                        if (!isIdentifier(current)) {
                                            before.add(tempString.toString());
                                            after.add(keywordsToString.get(tempString.toString()));
                                            lineNumbers.add(line);
                                            return 1;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //i : int, if
        else if (current == 'i') {
            tempString.append(current);
            getNextChar();
            if (current == 'f') {
                tempString.append(current);
                getNextChar();
                if (!isIdentifier(current)) {
                    before.add(tempString.toString());
                    after.add(keywordsToString.get(tempString.toString()));
                    lineNumbers.add(line);
                    return 1;
                }
            } else if (current == 'n') {
                tempString.append(current);
                getNextChar();
                if (current == 't') {
                    tempString.append(current);
                    getNextChar();
                    if (!isIdentifier(current)) {
                        before.add(tempString.toString());
                        after.add(keywordsToString.get(tempString.toString()));
                        lineNumbers.add(line);
                        return 1;
                    }
                }
            }
        }
        //b : break
        else if (current == 'b') {
            tempString.append(current);
            getNextChar();
            if (current == 'r') {
                tempString.append(current);
                getNextChar();
                if (current == 'e') {
                    tempString.append(current);
                    getNextChar();
                    if (current == 'a') {
                        tempString.append(current);
                        getNextChar();
                        if (current == 'k') {
                            tempString.append(current);
                            getNextChar();
                            if (!isIdentifier(current)) {
                                before.add(tempString.toString());
                                after.add(keywordsToString.get(tempString.toString()));
                                lineNumbers.add(line);
                                return 1;
                            }
                        }
                    }
                }
            }
        }
        //e : else
        else if (current == 'e') {
            tempString.append(current);
            getNextChar();
            if (current == 'l') {
                tempString.append(current);
                getNextChar();
                if (current == 's') {
                    tempString.append(current);
                    getNextChar();
                    if (current == 'e') {
                        tempString.append(current);
                        getNextChar();
                        if (!isIdentifier(current)) {
                            before.add(tempString.toString());
                            after.add(keywordsToString.get(tempString.toString()));
                            lineNumbers.add(line);
                            return 1;
                        }
                    }
                }
            }
        }
        //w : while
        else if (current == 'w') {
            tempString.append(current);
            getNextChar();
            if (current == 'h') {
                tempString.append(current);
                getNextChar();
                if (current == 'i') {
                    tempString.append(current);
                    getNextChar();
                    if (current == 'l') {
                        tempString.append(current);
                        getNextChar();
                        if (current == 'e') {
                            tempString.append(current);
                            getNextChar();
                            if (!isIdentifier(current)) {
                                before.add(tempString.toString());
                                after.add(keywordsToString.get(tempString.toString()));
                                lineNumbers.add(line);
                                return 1;
                            }
                        }
                    }
                }
            }
        }
        //g : getint
        else if (current == 'g') {
            tempString.append(current);
            getNextChar();
            if (current == 'e') {
                tempString.append(current);
                getNextChar();
                if (current == 't') {
                    tempString.append(current);
                    getNextChar();
                    if (current == 'i') {
                        tempString.append(current);
                        getNextChar();
                        if (current == 'n') {
                            tempString.append(current);
                            getNextChar();
                            if (current == 't') {
                                tempString.append(current);
                                getNextChar();
                                if (!isIdentifier(current)) {
                                    before.add(tempString.toString());
                                    after.add(keywordsToString.get(tempString.toString()));
                                    lineNumbers.add(line);
                                    return 1;
                                }
                            }
                        }
                    }
                }
            }
        }
        // p :printf
        else if (current == 'p') {
            tempString.append(current);
            getNextChar();
            if (current == 'r') {
                tempString.append(current);
                getNextChar();
                if (current == 'i') {
                    tempString.append(current);
                    getNextChar();
                    if (current == 'n') {
                        tempString.append(current);
                        getNextChar();
                        if (current == 't') {
                            tempString.append(current);
                            getNextChar();
                            if (current == 'f') {
                                tempString.append(current);
                                getNextChar();
                                if (!isIdentifier(current)) {
                                    before.add(tempString.toString());
                                    after.add(keywordsToString.get(tempString.toString()));
                                    lineNumbers.add(line);
                                    return 1;
                                }
                            }
                        }
                    }
                }
            }
        }
        //r : return
        else if (current == 'r') {
            tempString.append(current);
            getNextChar();
            if (current == 'e') {
                tempString.append(current);
                getNextChar();
                if (current == 't') {
                    tempString.append(current);
                    getNextChar();
                    if (current == 'u') {
                        tempString.append(current);
                        getNextChar();
                        if (current == 'r') {
                            tempString.append(current);
                            getNextChar();
                            if (current == 'n') {
                                tempString.append(current);
                                getNextChar();
                                if (!isIdentifier(current)) {
                                    before.add(tempString.toString());
                                    after.add(keywordsToString.get(tempString.toString()));
                                    lineNumbers.add(line);
                                    return 1;
                                }
                            }
                        }
                    }
                }
            }
        }
        //v : void
        else if (current == 'v') {
            tempString.append(current);
            getNextChar();
            if (current == 'o') {
                tempString.append(current);
                getNextChar();
                if (current == 'i') {
                    tempString.append(current);
                    getNextChar();
                    if (current == 'd') {
                        tempString.append(current);
                        getNextChar();
                        if (!isIdentifier(current)) {
                            before.add(tempString.toString());
                            after.add(keywordsToString.get(tempString.toString()));
                            lineNumbers.add(line);
                            return 1;
                        }
                    }
                }
            }
        }
        if (!isIdentifier(current)) {
            before.add(tempString.toString());
            after.add("IDENFR");
            lineNumbers.add(line);
            return 1;
        }
        tempString.append(current);
        getNextChar();
        appendLeftIdent(tempString);
        before.add(tempString.toString());
        after.add("IDENFR");
        lineNumbers.add(line);
        return 1;
    }

    private void appendLeftIdent(StringBuffer tempString) throws NullPointerException {
        while (isIdentifier(current)) {
            tempString.append(current);
            getNextChar();
        }
    }

    private int analyseIdent() throws NullPointerException {
        StringBuffer ident = new StringBuffer();
        ident.append(current);
        getNextChar();
        //下划线的下一个字符是数字，不符合ident
        //todo
        /*
        if (isNumber(current)) {
            return 0;
        }

         */
        while (isIdentifier(current)) {
            ident.append(current);
            getNextChar();
        }
        before.add(ident.toString());
        after.add("IDENFR");
        lineNumbers.add(line);
        return 1;
    }

    private int analyseMark() throws NullPointerException {
        StringBuffer mark = new StringBuffer();
        mark.append(current);
        //如果是注释
        if (current == '/' && totalString.charAt(point + 1) == '*') {
            getNextChar();
            getNextChar();
            while (current != '*' || totalString.charAt(point + 1) != '/') {
                if (current == '\n') {
                    line++;
                }
                getNextChar();
            }
            getNextChar();
            getNextChar();
            return 1;
        }
        if (current == '/' && totalString.charAt(point + 1) == '/') {
            getNextChar();
            getNextChar();
            while (current != '\n') {
                getNextChar();
            }
            getNextChar();
            line++;
            return 1;
        }
        //一定是单个字符的操作符
        if (current != '&' && current != '|' && current != '<' && current != '>' && current != '=' && current != '!') {
            if (symToString.containsKey(mark.toString())) {
                before.add(mark.toString());
                after.add(symToString.get(mark.toString()));
                lineNumbers.add(line);
                getNextChar();
                return 1;
            } else {
                return 0;
            }
        }
        if (current == '&') {
            getNextChar();
            if (current == '&') {
                mark.append(current);
                before.add(mark.toString());
                after.add(symToString.get(mark.toString()));
                lineNumbers.add(line);
                getNextChar();
            } else {
                return 0;
            }
            return 1;
        }
        if (current == '|') {
            getNextChar();
            if (current == '|') {
                mark.append(current);
                before.add(mark.toString());
                after.add(symToString.get(mark.toString()));
                lineNumbers.add(line);
                getNextChar();
            } else {
                return 0;
            }
            return 1;
        }
        if (current == '<') {
            return analyseNextMark(mark);
        }
        if (current == '>') {
            return analyseNextMark(mark);
        }
        if (current == '=') {
            return analyseNextMark(mark);
        }
        if (current == '!') {
            return analyseNextMark(mark);
        }
        return 0;
    }

    private int analyseNextMark(StringBuffer mark) throws NullPointerException {
        getNextChar();
        if (current == '=') {
            mark.append(current);
            before.add(mark.toString());
            after.add(symToString.get(mark.toString()));
            lineNumbers.add(line);
            point++;
            current = totalString.charAt(point);
        } else {
            before.add(mark.toString());
            after.add(symToString.get(mark.toString()));
            lineNumbers.add(line);
        }
        return 1;
    }

    private int analyseFormatString() throws NullPointerException {
        StringBuffer formatString = new StringBuffer();
        formatString.append('"');
        getNextChar();
        while (current != '"' || (current == '"' && totalString.charAt(point - 1) == '\\')) {
            formatString.append(current);
            getNextChar();
        }
        if (current != '"') {
            return 0;
        }
        formatString.append(current);
        getNextChar();
        before.add(formatString.toString());
        after.add("STRCON");
        lineNumbers.add(line);
        return 1;
    }

    private int analyseNumber() throws NullPointerException {
        StringBuffer number = new StringBuffer();
        while (isNumber(current)) {
            number.append(current);
            getNextChar();
        }
        if (isLetter(current)) {
            System.out.println("ERROR!");
            return 0;
        }
        before.add(number.toString());
        after.add("INTCON");
        lineNumbers.add(line);
        return 1;
    }

    private boolean isNumber(char c) {
        return Character.isDigit(c);
    }

    private boolean isLetter(char c) {
        return Character.isLetter(c);
    }

    private boolean isWhiteCharacter(char c) {
        return Character.isWhitespace(c);
    }

    private boolean isEnterCharacter(char c) {
        return c == '\n';
    }

    private boolean isIdentifier(char c) {
        return Character.isLetter(c) || Character.isDigit(c) || c == '_';
    }

    public ArrayList<String> getBefore() {
        return before;
    }

    public ArrayList<String> getAfter() {
        return after;
    }

    public ArrayList<Integer> getLineNumbers() {
        return lineNumbers;
    }
}
