import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MipsGenerator {
    private ArrayList<MipsCode> mipsCodes = new ArrayList<>();

    public void genMips(ArrayList<MidCode> midCodes) {
        addDataSection();
        addTextSection(midCodes);
    }

    public ArrayList<MipsCode> getMipsCodes() {
        return mipsCodes;
    }

    private void addDataSection() {
        mipsCodes.add(new MipsCode(".data"));
        int tempSize = Compiler.globalNumber * 4;
        mipsCodes.add(new MipsCode("array: .space " + tempSize));
        for (String key : Compiler.printStr.keySet()) {
            String asciiz = Compiler.printStr.get(key) + ": .asciiz \"" + key + "\"";
            mipsCodes.add(new MipsCode(asciiz));
        }
    }

    private void addTextSection(ArrayList<MidCode> midCodes) {
        mipsCodes.add(new MipsCode(".text"));
        for (MidCode midCode : midCodes) {
            OpType midOp = midCode.getOpType();
            switch (midOp) {
                case ADD:
                    dealADDMidCode(midCode);
                    break;
                case SUB:
                    dealSUBMidCode(midCode);
                    break;
                case MUL:
                    dealMULMidCode(midCode);
                    break;
                case DIV:
                    dealDIVMidCode(midCode);
                    break;
                case MOD:
                    dealMODMidCode(midCode);
                    break;
                case ADD_SINGLE:
                    dealADDSINGLEMidCode(midCode);
                    break;
                case SUB_SINGLE:
                    dealSUBSINGLEMidCode(midCode);
                    break;
                case NOT_SINGLE:
                    dealNOTSINGLEMidCode(midCode);
                    break;
                case IMM:
                    dealIMMMidCode(midCode);
                    break;
                case ASSIGN:
                    dealASSIGNMidCode(midCode);
                    break;
                case FUNC:
                    dealFUNCMidCode(midCode);
                    break;
                case FUNC_END:
                    dealFUNCENDMidCode(midCode);
                    break;
                case PARA:
                    //形参定义 不用管
                    break;
                case VAR:
                    //变量定义 不用管
                    break;
                case CONST:
                    //常量定义 不用管
                    break;
                case ARR:
                    //todo 数组
                    break;
                case LW:

                    break;
                case SW:

                    break;
                case PUSH:
                    dealPUSHMidCode(midCode);
                    break;
                case POP:
                    dealPOPMidCode(midCode);
                    break;
                case CALL:
                    dealCALLMidCode(midCode);
                    break;
                case GETINT:
                    dealGETINTMidCode(midCode);
                    break;
                case PRINT:
                    dealPRINTMidCode(midCode);
                    break;
                case RETURN:
                    dealRETURNMidCode(midCode);
                    break;
                case LABEL:
                    dealLABELMidCode(midCode);
                    break;
                case LSS:
                    dealLSSMidCode(midCode);
                    break;
                case LEQ:
                    dealLEQMidCode(midCode);
                    break;
                case GRE:
                    dealGREMidCode(midCode);
                    break;
                case GEQ:
                    dealGEQMidCode(midCode);
                    break;
                case EQL:
                    dealEQLMidCode(midCode);
                    break;
                case NEQ:
                    dealNEQMidCode(midCode);
                    break;
                case BEQ:
                    dealBEQMidCode(midCode);
                    break;
                case BNE:
                    dealBNEMidCode(midCode);
                    break;
                case JUMP:
                    dealJUMPMidCode(midCode);
                    break;
                case CHANGE_SP:
                    dealCHANGESPMidCode(midCode);
                    break;
                default:
                    System.out.println("生成mips代码有误");
                    break;
            }
        }
    }

    /*
        String numLeft = getNumber(midCode.getLeft());
        String numRight1 = getNumber(midCode.getRight1());
        String numRight2 = getNumber(midCode.getRight2());
        mipsCodes.add(new MipsCode(MipsOp.li, "$t0", numRight1));
        mipsCodes.add(new MipsCode(MipsOp.lw, "$t1", "array($t0)"));
        mipsCodes.add(new MipsCode(MipsOp.li, "$t0", numRight2));
        mipsCodes.add(new MipsCode(MipsOp.lw, "$t2", "array($t0)"));
        mipsCodes.add(new MipsCode(MipsOp.add, "$s0", "$t1", "$t2"));
        mipsCodes.add(new MipsCode(MipsOp.li, "$t0", numLeft));
        mipsCodes.add(new MipsCode(MipsOp.sw, "$s0", "array($t0)"));
     */

    private void dealCHANGESPMidCode(MidCode midCode) {
        String funcName = midCode.getLeft();
        String type = midCode.getRight1();
        int maxSize = Compiler.getFuncMax(funcName) * 4 + 4;
        String toCount = "" + maxSize;
        if (type.equals("-")) {
            mipsCodes.add(new MipsCode(MipsOp.addi, "$sp", "$sp", "-" + toCount));
        } else if (type.equals("+")) {
            mipsCodes.add(new MipsCode(MipsOp.addi, "$sp", "$sp", "+" + toCount));
        }
    }

    private void dealNEQMidCode(MidCode midCode) {
        String left = midCode.getLeft();
        String right1 = midCode.getRight1();
        String right2 = midCode.getRight2();
        getVarToReg(right1, "$t1");
        getVarToReg(right2, "$t2");
        mipsCodes.add(new MipsCode(MipsOp.sne, "$s0", "$t1", "$t2"));
        putRegToVar("$s0", left);
    }

    private void dealEQLMidCode(MidCode midCode) {
        String left = midCode.getLeft();
        String right1 = midCode.getRight1();
        String right2 = midCode.getRight2();
        getVarToReg(right1, "$t1");
        getVarToReg(right2, "$t2");
        mipsCodes.add(new MipsCode(MipsOp.seq, "$s0", "$t1", "$t2"));
        putRegToVar("$s0", left);
    }

    private void dealGEQMidCode(MidCode midCode) {
        String left = midCode.getLeft();
        String right1 = midCode.getRight1();
        String right2 = midCode.getRight2();
        getVarToReg(right1, "$t1");
        getVarToReg(right2, "$t2");
        mipsCodes.add(new MipsCode(MipsOp.sge, "$s0", "$t1", "$t2"));
        putRegToVar("$s0", left);
    }

    private void dealGREMidCode(MidCode midCode) {
        String left = midCode.getLeft();
        String right1 = midCode.getRight1();
        String right2 = midCode.getRight2();
        getVarToReg(right1, "$t1");
        getVarToReg(right2, "$t2");
        mipsCodes.add(new MipsCode(MipsOp.sgt, "$s0", "$t1", "$t2"));
        putRegToVar("$s0", left);
    }

    private void dealLEQMidCode(MidCode midCode) {
        String left = midCode.getLeft();
        String right1 = midCode.getRight1();
        String right2 = midCode.getRight2();
        getVarToReg(right1, "$t1");
        getVarToReg(right2, "$t2");
        mipsCodes.add(new MipsCode(MipsOp.sle, "$s0", "$t1", "$t2"));
        putRegToVar("$s0", left);
    }

    private void dealLSSMidCode(MidCode midCode) {
        String left = midCode.getLeft();
        String right1 = midCode.getRight1();
        String right2 = midCode.getRight2();
        getVarToReg(right1, "$t1");
        getVarToReg(right2, "$t2");
        mipsCodes.add(new MipsCode(MipsOp.slt, "$s0", "$t1", "$t2"));
        putRegToVar("$s0", left);
    }

    public void dealBNEMidCode(MidCode midCode) {
        String left = midCode.getLeft();
        getVarToReg(left, "$t1");
        String right = midCode.getRight1();
        String label = midCode.getRight2();
        if (right.equals("$0")) {
            mipsCodes.add(new MipsCode(MipsOp.bne, "$t1", "$0", label));
        } else {
            //如果不是和0比较
        }
    }

    private void dealBEQMidCode(MidCode midCode) {
        String left = midCode.getLeft();
        getVarToReg(left, "$t1");
        String right = midCode.getRight1();
        String label = midCode.getRight2();
        if (right.equals("$0")) {
            mipsCodes.add(new MipsCode(MipsOp.beq, "$t1", "$0", label));
        } else {
            //如果不是和0比较
        }
    }

    private void dealJUMPMidCode(MidCode midCode) {
        String label = midCode.getLeft();
        mipsCodes.add(new MipsCode(MipsOp.j, label));
    }

    private void dealLABELMidCode(MidCode midCode) {
        if (midCode.getLeft().equals("MainEnd")) {
            mipsCodes.add(new MipsCode(MipsOp.li, "$v0", "10"));
            mipsCodes.add(new MipsCode("syscall"));
            return;
        }
        mipsCodes.add(new MipsCode(midCode.getLeft() + ":"));
    }

    private void dealRETURNMidCode(MidCode midCode) {
        String left = midCode.getLeft();
        if (left.equals("NULL")) {
            mipsCodes.add(new MipsCode(MipsOp.jr, "$ra"));
            return;
        }
        getVarToReg(left, "$v0");
        mipsCodes.add(new MipsCode(MipsOp.jr, "$ra"));
    }

    private void dealPRINTMidCode(MidCode midCode) {
        String left = midCode.getLeft();
        String type = midCode.getRight1();
        if (type.equals("int")) {
            getVarToReg(left, "$a0");
            mipsCodes.add(new MipsCode(MipsOp.li, "$v0", "1"));
            mipsCodes.add(new MipsCode("syscall"));
        } else if (type.equals("str")) {
            mipsCodes.add(new MipsCode(MipsOp.la, "$a0", left));
            mipsCodes.add(new MipsCode(MipsOp.li, "$v0", "4"));
            mipsCodes.add(new MipsCode("syscall"));
        }
    }

    private void dealGETINTMidCode(MidCode midCode) {
        String left = midCode.getLeft();
        mipsCodes.add(new MipsCode(MipsOp.li, "$v0", "5"));
        mipsCodes.add(new MipsCode("syscall"));
        putRegToVar("$v0", left);
    }

    private void dealCALLMidCode(MidCode midCode) {
        String left = midCode.getLeft();
        mipsCodes.add(new MipsCode(MipsOp.jal, left));
    }

    private void dealPOPMidCode(MidCode midCode) {
        String left = midCode.getLeft();
        if (left.equals("ra")) {
            String lastFunc = midCode.getRight1();
            int maxSize = Compiler.getFuncMax(lastFunc) * 4;
            String toCount = "" + maxSize;
            mipsCodes.add(new MipsCode(MipsOp.lw, "$ra", "-" + toCount + "($sp)"));
        } else {
            String number = getNumber(left);
            mipsCodes.add(new MipsCode(MipsOp.add, "$sp", "$sp", "4"));
            mipsCodes.add(new MipsCode(MipsOp.lw, "$s0", "0($sp)"));
            mipsCodes.add(new MipsCode(MipsOp.li, "$t0", number));
            mipsCodes.add(new MipsCode(MipsOp.sw, "$s0", "array($t0)"));
        }
    }

    private void dealPUSHMidCode(MidCode midCode) {
        String left = midCode.getLeft();
        if (left.equals("ra")) {
            String lastFunc = midCode.getRight1();
            int maxSize = Compiler.getFuncMax(lastFunc) * 4;
            String toCount = "" + maxSize;
            mipsCodes.add(new MipsCode(MipsOp.sw, "$ra", "-" + toCount + "($sp)"));
        } else {
            String index = midCode.getRight1();
            String lastFunc = midCode.getRight2();
            int maxSize = Compiler.getFuncMax(lastFunc) * 4 + Integer.parseInt(index);
            String toCount = "" + maxSize;
            getVarToReg(left, "$t0");
            mipsCodes.add(new MipsCode(MipsOp.sw, "$t0", "-" + toCount + "($sp)"));
        }
    }

    private void dealFUNCENDMidCode(MidCode midCode) {
        mipsCodes.add(new MipsCode(MipsOp.jr, "$ra"));
    }

    private void dealFUNCMidCode(MidCode midCode) {
        String label = midCode.getLeft();
        mipsCodes.add(new MipsCode(label + ":"));
    }

    private void dealASSIGNMidCode(MidCode midCode) {
        String left = midCode.getLeft();
        if (midCode.getRight1() == null) {
            int value = midCode.getValue();
            mipsCodes.add(new MipsCode(MipsOp.li, "$s0", "" + value));
            putRegToVar("$s0", left);
            return;
        }
        if (midCode.getRight1().equals("RET")) {
            putRegToVar("$v0", left);
            return;
        }
        String right1 = midCode.getRight1();
        getVarToReg(right1, "$s0");
        putRegToVar("$s0", left);
    }

    private void dealIMMMidCode(MidCode midCode) {
        String left = midCode.getLeft();
        String immNumber = midCode.getRight1();
        mipsCodes.add(new MipsCode(MipsOp.li, "$s0", immNumber));
        putRegToVar("$s0", left);
    }

    private void dealADDMidCode(MidCode midCode) {
        //ADD temp1 temp2 temp3
        //li $t0, numRight1
        //lw $t1, array($t0)
        //li $t0, numRight2
        //lw $t2, array($t0)
        //add $s0,$t1,$t2
        //li $t0, numLeft
        //sw $s0, array($t0)
        String left = midCode.getLeft();
        String right1 = midCode.getRight1();
        String right2 = midCode.getRight2();
        getVarToReg(right1, "$t1");
        getVarToReg(right2, "$t2");
        mipsCodes.add(new MipsCode(MipsOp.add, "$s0", "$t1", "$t2"));
        putRegToVar("$s0", left);
    }

    private void dealSUBMidCode(MidCode midCode) {
        String left = midCode.getLeft();
        String right1 = midCode.getRight1();
        String right2 = midCode.getRight2();
        getVarToReg(right1, "$t1");
        getVarToReg(right2, "$t2");
        mipsCodes.add(new MipsCode(MipsOp.sub, "$s0", "$t1", "$t2"));
        putRegToVar("$s0", left);
    }

    private void dealMULMidCode(MidCode midCode) {
        String left = midCode.getLeft();
        String right1 = midCode.getRight1();
        String right2 = midCode.getRight2();
        getVarToReg(right1, "$t1");
        getVarToReg(right2, "$t2");
        mipsCodes.add(new MipsCode(MipsOp.mul, "$s0", "$t1", "$t2"));
        putRegToVar("$s0", left);
    }

    private void dealDIVMidCode(MidCode midCode) {
        String left = midCode.getLeft();
        String right1 = midCode.getRight1();
        String right2 = midCode.getRight2();
        getVarToReg(right1, "$t1");
        getVarToReg(right2, "$t2");
        mipsCodes.add(new MipsCode(MipsOp.div, "$s0", "$t1", "$t2"));
        putRegToVar("$s0", left);
    }

    private void dealMODMidCode(MidCode midCode) {
        String left = midCode.getLeft();
        String right1 = midCode.getRight1();
        String right2 = midCode.getRight2();
        getVarToReg(right1, "$t1");
        getVarToReg(right2, "$t2");
        mipsCodes.add(new MipsCode(MipsOp.div, "$t1", "$t2"));
        mipsCodes.add(new MipsCode(MipsOp.mfhi, "$s0"));
        putRegToVar("$s0", left);
    }

    private void dealADDSINGLEMidCode(MidCode midCode) {
        String left = midCode.getLeft();
        String right1 = midCode.getRight1();
        getVarToReg(right1, "$t1");
        mipsCodes.add(new MipsCode(MipsOp.add, "$s0", "$0", "$t1"));
        putRegToVar("$s0", left);
    }

    private void dealSUBSINGLEMidCode(MidCode midCode) {
        String left = midCode.getLeft();
        String right1 = midCode.getRight1();
        getVarToReg(right1, "$t1");
        mipsCodes.add(new MipsCode(MipsOp.sub, "$s0", "$0", "$t1"));
        putRegToVar("$s0", left);
    }

    private void dealNOTSINGLEMidCode(MidCode midCode) {
        String left = midCode.getLeft();
        String right1 = midCode.getRight1();
        getVarToReg(right1, "$t1");
        mipsCodes.add(new MipsCode(MipsOp.sltiu, "$s0", "$t1", "1"));
        putRegToVar("$s0", left);
    }


    private String getNumber(String str) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        int num = Integer.parseInt(m.replaceAll("").trim()) * 4;
        return "" + num;
    }

    private void getVarToReg(String var, String reg) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(var);
        int num = Integer.parseInt(m.replaceAll("").trim()) * 4;
        if (var.charAt(0) == 'g') {
            mipsCodes.add(new MipsCode(MipsOp.lw, reg, num + "($gp)"));
        } else if (var.charAt(0) == 't') {
            mipsCodes.add(new MipsCode(MipsOp.lw, reg, "-" + num + "($sp)"));
        }
    }

    private void putRegToVar(String reg, String var) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(var);
        int num = Integer.parseInt(m.replaceAll("").trim()) * 4;
        if (var.charAt(0) == 'g') {
            mipsCodes.add(new MipsCode(MipsOp.sw, reg, num + "($gp)"));
        } else if (var.charAt(0) == 't') {
            mipsCodes.add(new MipsCode(MipsOp.sw, reg, "-" + num + "($sp)"));
        }
    }
}

class MipsCode {
    private MipsOp mipsOp = null;
    private String right1 = null;
    private String right2 = null;
    private String left = null;

    private String information = null;

    public MipsCode(MipsOp op, String left, String right1, String right2) {
        //仨数都有的
        this.mipsOp = op;
        this.left = left;
        this.right1 = right1;
        this.right2 = right2;
    }

    public MipsCode(MipsOp op, String left, String right1) {
        //只有俩数的
        this.mipsOp = op;
        this.left = left;
        this.right1 = right1;
    }

    public MipsCode(MipsOp op, String left) {
        this.mipsOp = op;
        this.left = left;
    }

    public MipsCode(MipsOp op) {
        this.mipsOp = op;
    }

    public MipsCode(String information) {
        this.information = information;
    }

    public String toString() {
        if (information != null) {
            return information;
        }
        String ans = "";
        ans += mipsOp.toString();
        if (left != null) {
            ans += " " + left;
        }
        if (right1 != null) {
            ans += " , " + right1;
        }
        if (right2 != null) {
            ans += " , " + right2;
        }
        return ans;
    }
}

enum MipsOp {
    add,
    addi,
    sub,
    mul, // 无溢出的
    div,
    lw,
    sw,
    li,
    la,
    mfhi,
    jr,
    jal,
    j,
    beq,
    bne,
    slt, // <
    sle, // <=
    sgt, // >
    sge, // >=
    seq, // ==
    sne, // !=
    sltiu,
    xori, //抑或立即数

}
