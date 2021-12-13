import java.util.ArrayList;

public class SymbolItem {
    private String type; //类型 var 或 func 或 arr
    private boolean isConst = false;
    private String returnType; //返回类型：如果是func的话可以是 int 或 void
    private String name; //变量名
    private String initName; //原始变量名
    private int dimension; //维度，可以是0
    private int count; // 如果是0维的话的数值
    private String index1; //数组的长度，是一个变量名
    private String index2;
    private ArrayList<String> params = new ArrayList<>(); //函数的参数
    private ArrayList<String> temps = new ArrayList<>(); //函数块里定义的temp
    private int paramNum;
    private ArrayList<SymbolItem> paramItems = new ArrayList<>(); //错误处理 函数的参数们

    public SymbolItem(String type, String initName, String name, boolean isConst) {
        //0维常量或变量
        this.type = type;
        this.initName = initName;
        this.name = name;
        this.isConst = isConst;
        this.dimension = 0;

    }

    public SymbolItem(String type, String initName, String name, boolean isConst, String index1) {
        //1维数组
        this.type = type;
        this.initName = initName;
        this.name = name;
        this.isConst = isConst;
        this.dimension = 1;
        this.index1 = index1;

    }

    public SymbolItem(String type, String initName, String name, boolean isConst, String index1, String index2) {
        //2维数组
        this.type = type;
        this.initName = initName;
        this.name = name;
        this.isConst = isConst;
        this.dimension = 2;
        this.index1 = index1;
        this.index2 = index2;
    }

    public SymbolItem(String type, String initName, String name, String returnType) {
        this.type = type;
        this.initName = initName;
        this.name = name;
        this.returnType = returnType;
    }

    public SymbolItem(String type, String initName, int dimension, boolean isConst) {
        //错误处理 变量或常量的
        this.type = type;
        this.initName = initName;
        this.isConst = isConst;
        this.dimension = dimension;
    }

    public SymbolItem(String type, String initName, String returnType, int paramNum, ArrayList<SymbolItem> params) {
        //错误处理 函数的
        this.type = type;
        this.initName = initName;
        this.returnType = returnType;
        this.paramNum = paramNum;
        this.paramItems = params;
    }

    public ArrayList<SymbolItem> getParamItems() {
        return paramItems;
    }

    public String getInitName() {
        return initName;
    }

    public String getName() {
        return name;
    }

    public void addParam(String paraName) {
        params.add(paraName);
    }

    public ArrayList<String> getParams() {
        return params;
    }

    public void addTemp(String tempName) {
        temps.add(tempName);
    }

    public ArrayList<String> getTemps() {
        return temps;
    }

    public int getParamNum() {
        return paramNum;
    }

    public boolean isConst() {
        return isConst;
    }

    public int getDimension() {
        return dimension;
    }

    public String getType() {
        return type;
    }

    public String getReturnType() {
        return returnType;
    }
}
