public class MidCode {
    private OpType opType = null;
    private String right1 = null;
    private String right2 = null;
    private String left = null; // 被赋值的
    private String returnType = null;
    private boolean isConst = false;
    private String offset = null;
    private int arrType = 0;
    private Integer value = null; //常数的值
    private boolean isArr = false;

    public MidCode(OpType opType, String left, String right1, String right2) {
        //加减乘除等
        this.opType = opType;
        this.left = left;
        this.right1 = right1;
        this.right2 = right2;
    }

    public MidCode(OpType opType, String left, String str) {

        this.opType = opType;
        this.left = left;
        if (opType == OpType.FUNC) {
            //函数定义的四元式
            this.returnType = str;
        } else if (opType == OpType.ASSIGN) {
            //赋值式子
            this.right1 = str;
        } else if (opType == OpType.ADD_SINGLE || opType == OpType.SUB_SINGLE || opType == OpType.NOT_SINGLE) {
            //单目运算符
            this.right1 = str;
        } else if (opType == OpType.IMM) {
            //加载立即数，right1存的是个数
            this.right1 = str;
        } else if (opType == OpType.PRINT) {
            //输出 可以是int，也可以是str，也可以是number
            //这存的就是left的类型
            this.right1 = str;
        } else if (opType == OpType.CHANGE_SP) {
            this.right1 = str;
        } else {
            this.right1 = str;
        }

    }

    public MidCode(OpType opType, String left) {
        //label标签 return语句 getint语句 函数调用
        this.opType = opType;
        this.left = left;
    }

    public MidCode(OpType opType, String left, int value) {
        //常数赋值
        this.opType = opType;
        this.left = left;
        this.value = value;
        this.right1 = null;
    }

    public MidCode(OpType opType, String left, boolean isConst) {
        //定义 不赋值
        this.opType = opType;
        this.left = left;
        this.isConst = isConst;
    }

    public MidCode(String left) {
        //中间代码ArrayList里最后的那个存数的
        //统一存到left里
        this.left = left;
    }

    public void setArr(boolean arr) {
        isArr = arr;
    }

    public boolean isArr() {
        return isArr;
    }

    public MidCode(String left, String right1) {
        //这也是个存数的
        //left是基地址的名字，right1是偏移
        this.left = left;
        this.right1 = right1;
    }

    public String getLeft() {
        return left;
    }

    public String getRight1() {
        return right1;
    }

    public String getRight2() {
        return right2;
    }

    public OpType getOpType() {
        return opType;
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        String ans = "";
        if (this.opType != null) {
            ans += opType.toString();
        } else {
            ans += "T";
        }
        if (this.left != null) {
            ans += " " + this.left;
        }
        if (this.right1 != null) {
            ans += " " + this.right1;
        }
        if (this.right2 != null) {
            ans += " " + this.right2;
        }
        if (this.value != null) {
            ans += " " + this.value;
        }
        return ans;
    }
}

enum OpType {
    ADD, //加减乘除模
    SUB,
    MUL,
    DIV,
    MOD,
    ADD_SINGLE, //单目运算符
    SUB_SINGLE,
    NOT_SINGLE,
    IMM, //立即数
    ASSIGN, //赋值
    FUNC, //函数定义
    FUNC_END, //函数结束
    PARA, //形参定义
    VAR, //变量定义
    CONST, //常量定义
    ARR, //数组定义
    LW, //赋值语句右侧有数组 从内存中取出来
    SW, //赋值语句左侧有数组 存入内存中
    PUSH, //压栈
    POP, //出栈
    CHANGE_SP, //调整sp的位置
    CALL, //函数调用
    GETINT,//读入一个整数
    PRINT,//输出
    RETURN, //返回
    LABEL, //跳转的标签

    LSS, // <
    LEQ, // <=
    GRE, // >
    GEQ, // >=
    EQL, // ==
    NEQ, // !=
    BEQ, // 通常用来和0比较 cond结果为0(假)就直接跳走了 beq temp1，$0,label
    BNE, // 同上
    JUMP, //直接跳到某个标签
}