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
        int tempSize = Compiler.tempNumber * 4;
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
                case LW:

                case SW:

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
                default:
                    System.out.println("生成mips代码有误");
                    break;
            }
        }
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
        String numLeft = getNumber(midCode.getLeft());
        mipsCodes.add(new MipsCode(MipsOp.li, "$t0", numLeft));
        mipsCodes.add(new MipsCode(MipsOp.lw, "$v0", "array($t0)"));
        mipsCodes.add(new MipsCode(MipsOp.jr, "$ra"));
    }

    private void dealPRINTMidCode(MidCode midCode) {
        String str = midCode.getLeft();
        String type = midCode.getRight1();
        if (type.equals("int")) {
            String number = getNumber(str);
            mipsCodes.add(new MipsCode(MipsOp.li, "$t0", number));
            mipsCodes.add(new MipsCode(MipsOp.lw, "$a0", "array($t0)"));
            mipsCodes.add(new MipsCode(MipsOp.li, "$v0", "1"));
            mipsCodes.add(new MipsCode("syscall"));
        } else if (type.equals("str")) {
            mipsCodes.add(new MipsCode(MipsOp.la, "$a0", str));
            mipsCodes.add(new MipsCode(MipsOp.li, "$v0", "4"));
            mipsCodes.add(new MipsCode("syscall"));
        }
    }

    private void dealGETINTMidCode(MidCode midCode) {
        String numLeft = getNumber(midCode.getLeft());
        mipsCodes.add(new MipsCode(MipsOp.li, "$v0", "5"));
        mipsCodes.add(new MipsCode("syscall"));
        mipsCodes.add(new MipsCode(MipsOp.li, "$t0", numLeft));
        mipsCodes.add(new MipsCode(MipsOp.sw, "$v0", "array($t0)"));
    }

    private void dealCALLMidCode(MidCode midCode) {
        String left = midCode.getLeft();
        mipsCodes.add(new MipsCode(MipsOp.jal, left));
    }

    private void dealPOPMidCode(MidCode midCode) {
        String left = midCode.getLeft();
        if (left.equals("ra")) {
            mipsCodes.add(new MipsCode(MipsOp.add, "$sp", "$sp", "4"));
            mipsCodes.add(new MipsCode(MipsOp.lw, "$ra", "0($sp)"));
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
            mipsCodes.add(new MipsCode(MipsOp.sw, "$ra", "0($sp)"));
            mipsCodes.add(new MipsCode(MipsOp.add, "$sp", "$sp", "-4"));
        } else {
            String number = getNumber(left);
            mipsCodes.add(new MipsCode(MipsOp.li, "$t0", number));
            mipsCodes.add(new MipsCode(MipsOp.lw, "$s0", "array($t0)"));
            mipsCodes.add(new MipsCode(MipsOp.sw, "$s0", "0($sp)"));
            mipsCodes.add(new MipsCode(MipsOp.add, "$sp", "$sp", "-4"));
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
        String numLeft = getNumber(midCode.getLeft());
        if (midCode.getRight1().equals("RET")) {
            mipsCodes.add(new MipsCode(MipsOp.li, "$t0", numLeft));
            mipsCodes.add(new MipsCode(MipsOp.sw, "$v0", "array($t0)"));
            return;
        }
        String numRight1 = getNumber(midCode.getRight1());
        mipsCodes.add(new MipsCode(MipsOp.li, "$t0", numRight1));
        mipsCodes.add(new MipsCode(MipsOp.lw, "$s0", "array($t0)"));
        mipsCodes.add(new MipsCode(MipsOp.li, "$t0", numLeft));
        mipsCodes.add(new MipsCode(MipsOp.sw, "$s0", "array($t0)"));
    }

    private void dealIMMMidCode(MidCode midCode) {
        String numLeft = getNumber(midCode.getLeft());
        String immNumber = midCode.getRight1();
        mipsCodes.add(new MipsCode(MipsOp.li, "$s0", immNumber));
        mipsCodes.add(new MipsCode(MipsOp.li, "$t0", numLeft));
        mipsCodes.add(new MipsCode(MipsOp.sw, "$s0", "array($t0)"));
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
    }

    private void dealSUBMidCode(MidCode midCode) {
        String numLeft = getNumber(midCode.getLeft());
        String numRight1 = getNumber(midCode.getRight1());
        String numRight2 = getNumber(midCode.getRight2());
        mipsCodes.add(new MipsCode(MipsOp.li, "$t0", numRight1));
        mipsCodes.add(new MipsCode(MipsOp.lw, "$t1", "array($t0)"));
        mipsCodes.add(new MipsCode(MipsOp.li, "$t0", numRight2));
        mipsCodes.add(new MipsCode(MipsOp.lw, "$t2", "array($t0)"));
        mipsCodes.add(new MipsCode(MipsOp.sub, "$s0", "$t1", "$t2"));
        mipsCodes.add(new MipsCode(MipsOp.li, "$t0", numLeft));
        mipsCodes.add(new MipsCode(MipsOp.sw, "$s0", "array($t0)"));
    }

    private void dealMULMidCode(MidCode midCode) {
        String numLeft = getNumber(midCode.getLeft());
        String numRight1 = getNumber(midCode.getRight1());
        String numRight2 = getNumber(midCode.getRight2());
        mipsCodes.add(new MipsCode(MipsOp.li, "$t0", numRight1));
        mipsCodes.add(new MipsCode(MipsOp.lw, "$t1", "array($t0)"));
        mipsCodes.add(new MipsCode(MipsOp.li, "$t0", numRight2));
        mipsCodes.add(new MipsCode(MipsOp.lw, "$t2", "array($t0)"));
        mipsCodes.add(new MipsCode(MipsOp.mul, "$s0", "$t1", "$t2"));
        mipsCodes.add(new MipsCode(MipsOp.li, "$t0", numLeft));
        mipsCodes.add(new MipsCode(MipsOp.sw, "$s0", "array($t0)"));
    }

    private void dealDIVMidCode(MidCode midCode) {
        String numLeft = getNumber(midCode.getLeft());
        String numRight1 = getNumber(midCode.getRight1());
        String numRight2 = getNumber(midCode.getRight2());
        mipsCodes.add(new MipsCode(MipsOp.li, "$t0", numRight1));
        mipsCodes.add(new MipsCode(MipsOp.lw, "$t1", "array($t0)"));
        mipsCodes.add(new MipsCode(MipsOp.li, "$t0", numRight2));
        mipsCodes.add(new MipsCode(MipsOp.lw, "$t2", "array($t0)"));
        mipsCodes.add(new MipsCode(MipsOp.div, "$s0", "$t1", "$t2"));
        mipsCodes.add(new MipsCode(MipsOp.li, "$t0", numLeft));
        mipsCodes.add(new MipsCode(MipsOp.sw, "$s0", "array($t0)"));
    }

    private void dealMODMidCode(MidCode midCode) {
        String numLeft = getNumber(midCode.getLeft());
        String numRight1 = getNumber(midCode.getRight1());
        String numRight2 = getNumber(midCode.getRight2());
        mipsCodes.add(new MipsCode(MipsOp.li, "$t0", numRight1));
        mipsCodes.add(new MipsCode(MipsOp.lw, "$t1", "array($t0)"));
        mipsCodes.add(new MipsCode(MipsOp.li, "$t0", numRight2));
        mipsCodes.add(new MipsCode(MipsOp.lw, "$t2", "array($t0)"));
        mipsCodes.add(new MipsCode(MipsOp.div, "$t1", "$t2"));
        mipsCodes.add(new MipsCode(MipsOp.mfhi, "$s0"));
        mipsCodes.add(new MipsCode(MipsOp.li, "$t0", numLeft));
        mipsCodes.add(new MipsCode(MipsOp.sw, "$s0", "array($t0)"));
    }

    private void dealADDSINGLEMidCode(MidCode midCode) {
        String numLeft = getNumber(midCode.getLeft());
        String numRight1 = getNumber(midCode.getRight1());
        mipsCodes.add(new MipsCode(MipsOp.li, "$t0", numRight1));
        mipsCodes.add(new MipsCode(MipsOp.lw, "$t1", "array($t0)"));
        mipsCodes.add(new MipsCode(MipsOp.add, "$s0", "$0", "$t1"));
        mipsCodes.add(new MipsCode(MipsOp.li, "$t0", numLeft));
        mipsCodes.add(new MipsCode(MipsOp.sw, "$s0", "array($t0)"));
    }

    private void dealSUBSINGLEMidCode(MidCode midCode) {
        String numLeft = getNumber(midCode.getLeft());
        String numRight1 = getNumber(midCode.getRight1());
        mipsCodes.add(new MipsCode(MipsOp.li, "$t0", numRight1));
        mipsCodes.add(new MipsCode(MipsOp.lw, "$t1", "array($t0)"));
        mipsCodes.add(new MipsCode(MipsOp.sub, "$s0", "$0", "$t1"));
        mipsCodes.add(new MipsCode(MipsOp.li, "$t0", numLeft));
        mipsCodes.add(new MipsCode(MipsOp.sw, "$s0", "array($t0)"));
    }

    private void dealNOTSINGLEMidCode(MidCode midCode) {
        String numLeft = getNumber(midCode.getLeft());
        String numRight1 = getNumber(midCode.getRight1());
        //todo NOT
    }


    private String getNumber(String str) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        int num = Integer.parseInt(m.replaceAll("").trim()) * 4;
        return "" + num;
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
    jal
}
