import java.util.ArrayList;
import java.util.Stack;

public class Block {
    //语句块
    //Block → '{' { BlockItem } '}'
    private boolean success;
    private ArrayList<BlockItem> blockItems = new ArrayList<>();
    private static Stack<Integer> indexStack = new Stack<>();

    public Block(int blockNum, boolean isInCircle, int noReturn) {
        success = doGrammarAnalysis(blockNum, isInCircle, noReturn);
    }

    private boolean doGrammarAnalysis(int blockNum, boolean isInCircle, int noReturn) {
        int currentPoint = Compiler.point;
        if (!Compiler.getSymbol().equals("LBRACE")) {
            return false;
        }
        Compiler.point++;
        if (Compiler.getSymbol().equals("RBRACE")) {
            Compiler.point++;
            Compiler.popErrorSymbolTable(blockNum);
            return true;
        }
        BlockItem blockItem = new BlockItem(blockNum, isInCircle, noReturn);
        if (!blockItem.isSuccess()) {
            Compiler.point = currentPoint;
            return false;
        }
        blockItems.add(blockItem);
        while (true) {
            currentPoint = Compiler.point;
            BlockItem newBlockItem = new BlockItem(blockNum, isInCircle, noReturn);
            if (!newBlockItem.isSuccess()) {
                Compiler.point = currentPoint;
                break;
            }
            blockItems.add(newBlockItem);
        }
        if (Compiler.getSymbol().equals("RBRACE")) {
            Compiler.point++;
            Compiler.popErrorSymbolTable(blockNum);
            return true;
        }
        Compiler.point = currentPoint;
        return false;
    }

    public int getLastStmtType() {
        if (blockItems.size() == 0) {
            return -1;
        }
        return blockItems.get(blockItems.size() - 1).getStmtType();
    }

    public boolean isSuccess() {
        return success;
    }

    public String toPrint() {
        //Block → '{' { BlockItem } '}'
        StringBuffer ans = new StringBuffer();
        ans.append("LBRACE {\n");
        for (BlockItem blockItem : blockItems) {
            ans.append(blockItem.toPrint());
        }
        ans.append("RBRACE }\n");
        ans.append("<Block>\n");
        return ans.toString();
    }

    public ArrayList<MidCode> getMidCode(String whileLabel, String lastFunc) {
        ArrayList<MidCode> ans = new ArrayList<>();

        for (BlockItem blockItem : blockItems) {
            ans.addAll(blockItem.getMidCode(whileLabel, lastFunc));
        }

        return ans;
    }
}
