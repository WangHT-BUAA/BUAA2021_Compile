public class Node {
    private String name;
    private String symbolName;

    public Node(String symbol, String value) {
        this.symbolName = value;
        name = symbol + " " + value + "\n";
    }

    public String toPrint() {
        return name;
    }

    public int getNumOfPercent() {
        String replace = name.replace("%", "xx");
        return replace.length() - name.length();
    }

    public String getSymbolName() {
        return symbolName;
    }
}
