import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Compiler {
    private static String initString = null;
    private static LexAnalysis analysis;
    private static GrammarAnalysis grammarAnalysis;
    private static MipsGenerator mipsGenerator = new MipsGenerator();

    public static ArrayList<String> values;
    public static ArrayList<String> symbols;
    public static ArrayList<Integer> lineNumbers;
    public static int point = 0;

    public static Stack<SymbolItem> errorSymbolTable = new Stack<>();
    public static Stack<SymbolItem> errorFunTable = new Stack<>();

    public static int tempNumber = 0;
    public static int globalNumber = 0;
    public static int arrNumber = 0;
    public static int funcNumber = 0;
    public static int strNumber = 0;
    public static int ifNumber = 0;
    public static int whileNumber = 0;
    public static int labelNumber = 0;


    public static Stack<SymbolItem> symbolTable = new Stack<>();
    public static Stack<SymbolItem> globalSymbolTable = new Stack<>();
    public static HashMap<String, String> printStr = new HashMap<>(); // "\n" : str3


    public static void main(String[] args) throws IOException {
        initString = readFileContent("testfile.txt");
        analysis = new LexAnalysis(initString);
        try {
            analysis.doLexAnalysis();
        } catch (NullPointerException e) {
            //System.out.println("LexAnalysisError");
        }

        //语法分析
        values = analysis.getBefore();
        symbols = analysis.getAfter();
        lineNumbers = analysis.getLineNumbers();
        grammarAnalysis = new GrammarAnalysis();
        grammarAnalysis.doGrammarAnalysis();
        writeFileContent(); //写output.txt或者error.txt
        if (GrammarAnalysis.getError().length() == 0) {
            ArrayList<MidCode> midCodes = grammarAnalysis.getMidCode();
            writeMidCodeFile(midCodes); //写midcode.txt

            mipsGenerator.genMips(midCodes);
            ArrayList<MipsCode> mipsCodes = mipsGenerator.getMipsCodes();
            writeMipsFile(mipsCodes);
        } else {
            System.out.println("have errors");
        }


    }

    public static void writeMipsFile(ArrayList<MipsCode> mipsCodes) throws IOException {
        BufferedWriter bwMips = new BufferedWriter(new FileWriter("mips.txt"));
        for (MipsCode mipsCode : mipsCodes) {
            bwMips.write(mipsCode.toString() + "\n");
        }
        bwMips.close();
    }

    public static void writeMidCodeFile(ArrayList<MidCode> midCodes) throws IOException {
        //终端输出midcode
        for (String key : printStr.keySet()) {
            System.out.println(printStr.get(key) + " : " + key);
        }
        for (MidCode midCode : midCodes) {
            System.out.println(midCode.toString());
        }

        BufferedWriter bwMidCode = new BufferedWriter(new FileWriter("midcode.txt"));
        for (String key : printStr.keySet()) {
            bwMidCode.write(printStr.get(key) + " : " + key + "\n");
        }
        for (MidCode midCode : midCodes) {
            bwMidCode.write(midCode.toString() + "\n");
        }
        bwMidCode.close();
    }

    public static void writeFileContent() throws IOException {
        BufferedWriter bwOutput = new BufferedWriter(new FileWriter("output.txt"));
        BufferedWriter bwError = new BufferedWriter(new FileWriter("error.txt"));
        if (GrammarAnalysis.getError().length() != 0) {
            bwError.write(GrammarAnalysis.getError());
        } else {
            bwOutput.write(grammarAnalysis.toPrint());
        }
        bwError.close();
        bwOutput.close();
    }

    private static String readFileContent(String filename) {
        File file = new File(filename);
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                buffer.append(tempStr);
                buffer.append("\n");
            }
            reader.close();
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    public static String getValue() {
        return values.get(point);
    }

    public static String getSymbol() {
        try {
            return symbols.get(point);
        } catch (Exception e) {
            System.out.println("end");
        }
        return "error";
    }

    public static int getLine() {
        return lineNumbers.get(point);
    }

    public static void clearTempNum() {
        tempNumber = 0;
    }

    public static String getNewTemp() {
        String ans = "temp" + tempNumber;
        tempNumber++;
        return ans;
    }


    public static String getNewFunc() {
        String ans = "func" + funcNumber;
        funcNumber++;
        return ans;
    }

    public static String getNewStr() {
        String ans = "str" + strNumber;
        strNumber++;
        return ans;
    }

    public static String getNewWhile() {
        String ans = "while" + whileNumber;
        whileNumber++;
        return ans;
    }

    public static String getNewIf() {
        String ans = "if" + ifNumber;
        ifNumber++;
        return ans;
    }

    public static String getNewLabel() {
        String ans = "label" + labelNumber;
        labelNumber++;
        return ans;
    }

    public static String getNewGlobal() {
        String ans = "global" + globalNumber;
        globalNumber++;
        return ans;
    }

    public static void addTempNum(int num) {
        tempNumber += num;
    }

    public static void addGlobalNum(int num) {
        globalNumber += num;
    }

    public static void pushItemStack(SymbolItem symbolItem) {
        symbolTable.push(symbolItem);
    }

    public static void pushGlobalItem(SymbolItem symbolItem) {
        globalSymbolTable.push(symbolItem);
    }

    public static SymbolItem getSymbolItem(String initName) {
        for (int i = symbolTable.size() - 1; i >= 0; i--) {
            SymbolItem part = symbolTable.get(i);
            if (part.getInitName().equals(initName)) {
                return part;
            }
        }
        for (int i = globalSymbolTable.size() - 1; i >= 0; i--) {
            SymbolItem part = globalSymbolTable.get(i);
            if (part.getInitName().equals(initName)) {
                return part;
            }
        }
        return null;
    }

    public static SymbolItem getConstItem(String initName) {
        for (int i = symbolTable.size() - 1; i >= 0; i--) {
            SymbolItem part = symbolTable.get(i);
            if (part.getInitName().equals(initName) && part.isConst()) {
                return part;
            }
        }
        for (int i = globalSymbolTable.size() - 1; i >= 0; i--) {
            SymbolItem part = globalSymbolTable.get(i);
            if (part.getInitName().equals(initName) && part.isConst()) {
                return part;
            }
        }
        return null;
    }

    public static void popSymbolTable(int index) {
        while (symbolTable.size() > index) {
            symbolTable.pop();
        }
    }

    public static int getFuncMax(String funcName) {
        for (int i = globalSymbolTable.size() - 1; i >= 0; i--) {
            SymbolItem part = globalSymbolTable.get(i);
            if (part.getName().equals(funcName)) {
                return part.getBlockSize();
            }
        }
        return 0;
    }

    // todo errors
    public static void pushErrorItemStack(SymbolItem symbolItem) {
        errorSymbolTable.push(symbolItem);
    }


    public static SymbolItem getErrorFuncItem(String name) {
        for (SymbolItem part : errorFunTable) {
            if (part.getInitName().equals(name)) {
                return part;
            }
        }
        return null;
    }

    public static SymbolItem getErrorSymbolItem(String name) {
        for (int i = errorSymbolTable.size() - 1; i >= 0; i--) {
            SymbolItem part = errorSymbolTable.get(i);
            if (part.getInitName().equals(name)) {
                return part;
            }
        }
        return null;
    }

    public static SymbolItem getErrorSymbolItemInBlock(String name, int blockNum) {
        for (int i = errorSymbolTable.size() - 1; i >= blockNum; i--) {
            SymbolItem part = errorSymbolTable.get(i);
            if (part.getInitName().equals(name)) {
                return part;
            }
        }
        return null;
    }


    public static void popErrorSymbolTable(int num) {
        while (errorSymbolTable.size() > num) {
            errorSymbolTable.pop();
        }
    }


}
