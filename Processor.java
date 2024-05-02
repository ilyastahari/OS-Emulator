public class Processor {
    private Word PC;
    private Word SP;
    private Word currentInstruction; // holds current instruction being executed
    private Bit halted; // indicates whether processor is halted or not
    private Word rs1 = new Word(); // Words to store extracted values
    private Word rs2 = new Word(); // Words to store extracted values;
    private Word rd = new Word(); // Words to store extracted values;
    private Word function = new Word(); // Words to store extracted values;
    private Word immediate = new Word(); // Words to store extracted values;
    private static int currentClockCycle = 0; // the clock cycle counter
    private InstructionCache instructionCache;
    private L2Cache l2Cache;

    public Word[] getRegisters() {
        return registers;
    }

    Word[] registers = new Word[32];

    private ALU alu = new ALU();

    private Word result;

    private int rdIndex;

    public Word getRd() {
        return rd;
    }

    public Word getResult() {
        return result;
    }

    public void setResult(Word result) {
        this.result = result;
    }

    public void setRd(Word rd) {
        this.rd = rd;
    }

    /** executes instructions until halted */
    public void run() throws Exception {
        while (!halted.getValue()) { // continues execution until processor is halted
            rd = null;
            fetch();
            decode();
            execute();
            store();
        }
        System.out.println(currentClockCycle);
    }
    /** fetches next instruction from memory */
    private void fetch() {
//       currentInstruction = MainMemory.read(PC);
//        currentClockCycle += 300;
//        currentInstruction = instructionCache.read(PC);
        currentInstruction = l2Cache.read(PC);
        PC.increment();
    }

    private void decode() {
        Word formatMask = new Word(3);
        Word formatBits = currentInstruction.and(formatMask);
        if (!formatBits.getBit(0).getValue() && !formatBits.getBit(1).getValue()) { // No Register (0R)
            immediate = currentInstruction.leftShift(5);
        }
        else if (formatBits.getBit(0).getValue() && !formatBits.getBit(1).getValue()) { // Dest Only (1R)
            Word rdMask = new Word(0b1111100000);
            rd = currentInstruction.and(rdMask).leftShift(5);
            Word functionMask = new Word(0b11110000000000);
            function = currentInstruction.and(functionMask).leftShift(10);
            Word immediateMask = new Word(0b11111111111111111100000000000000);
            immediate = currentInstruction.and(immediateMask).leftShift(14);
        }
        else if (formatBits.getBit(0).getValue() && formatBits.getBit(1).getValue()) { // 2 Register (2R)
            Word rdMask = new Word(0b1111100000);
            rd = currentInstruction.and(rdMask).leftShift(5);
            Word functionMask = new Word(0b11110000000000);
            function = currentInstruction.and(functionMask).leftShift(10);
            Word rs1Mask = new Word(0b1111100000000000000);
            rs1 = currentInstruction.and(rs1Mask).leftShift(14);
            Word immediateMask = new Word(0b11111111111110000000000000000000);
            immediate = currentInstruction.and(immediateMask).leftShift(19);
        }
        else if (!formatBits.getBit(0).getValue() && formatBits.getBit(1).getValue()) { // 3 Register (3R)
            Word rdMask = new Word(0b1111100000);
            rd = currentInstruction.and(rdMask).leftShift(5);
            Word functionMask = new Word(0b11110000000000);
            function = currentInstruction.and(functionMask).leftShift(10);
            Word rs1Mask = new Word(0b1111100000000000000);
            rs1 = currentInstruction.and(rs1Mask).leftShift(14);
            Word rs2Mask =  new Word(0b111110000000000000000000);
            rs2 = currentInstruction.and(rs2Mask).leftShift(19);
            Word immediateMask = new Word(0b11111111000000000000000000000000);
            immediate = currentInstruction.and(immediateMask).leftShift(24);
        }
    }

    public void execute() throws Exception {
        result = new Word();
       // rdIndex = (int) rd.getUnsigned();

        Word formatMask = new Word(3);
        Word opCode = currentInstruction.and(formatMask);
//        Instruction();
//        BOP(rs1, rd, function);
        if(!opCode.getBit(2).getValue() && !opCode.getBit(3).getValue() && !opCode.getBit(4).getValue()){
            if (!opCode.getBit(0).getValue() && !opCode.getBit(1).getValue()){
                halted.set(true);
            }
            else if (opCode.getBit(0).getValue() && !opCode.getBit(1).getValue()) {
                registers[(int) rd.getUnsigned()] = immediate;
                result = immediate;
            } else if (opCode.getBit(0).getValue() && opCode.getBit(1).getValue()) {
                alu.op1 = registers[rdIndex];
                alu.op2 = registers[(int) rs1.getUnsigned()];
                Bit[] functionArray = new Bit[4];
                for (int i = 0; i < 4; i++) {
                    functionArray[i] = function.getBit(i);
                }
                alu.doOperation(functionArray);
                if (!functionArray[0].getValue() && functionArray[1].getValue() && functionArray[2].getValue() && functionArray[3].getValue()) {
                    currentClockCycle += 10;
                } else {
                    currentClockCycle += 2;
                }
                result = alu.result;
            } else if (!opCode.getBit(0).getValue() && opCode.getBit(1).getValue()) {
                alu.op1 = registers[(int) rs1.getUnsigned()];
                alu.op2 = registers[(int) rs2.getUnsigned()];
                Bit[] functionArray = new Bit[4];
                for (int i = 0; i < 4; i++) {
                    functionArray[i] = function.getBit(i);
                }
                alu.doOperation(functionArray);
                if (!functionArray[0].getValue() && functionArray[1].getValue() && functionArray[2].getValue() && functionArray[3].getValue()) {
                    currentClockCycle += 10;
                } else {
                    currentClockCycle += 2;
                }
                result = alu.result;
            }
        }
    }

    private void store() {
        if (rd != null) {
            if (rd.getBit(0).getValue().equals(false) && rd.getBit(1).getValue().equals(false) && rd.getBit(2).getValue().equals(false) && rd.getBit(3).getValue().equals(false) && rd.getBit(4).getValue().equals(false)) {
                registers[0] = result;
            }//0
            if (rd.getBit(0).getValue().equals(true) && rd.getBit(1).getValue().equals(false) && rd.getBit(2).getValue().equals(false) && rd.getBit(3).getValue().equals(false) && rd.getBit(4).getValue().equals(false)) {
                registers[1] = result;
            }//1
            if (rd.getBit(0).getValue().equals(false) && rd.getBit(1).getValue().equals(true) && rd.getBit(2).getValue().equals(false) && rd.getBit(3).getValue().equals(false) && rd.getBit(4).getValue().equals(false)) {
                registers[2] = result;
            }//2
            if (rd.getBit(0).getValue().equals(true) && rd.getBit(1).getValue().equals(true) && rd.getBit(2).getValue().equals(false) && rd.getBit(3).getValue().equals(false) && rd.getBit(4).getValue().equals(false)) {
                registers[3] = result;
            }//3
            if (rd.getBit(0).getValue().equals(false) && rd.getBit(1).getValue().equals(false) && rd.getBit(2).getValue().equals(true) && rd.getBit(3).getValue().equals(false) && rd.getBit(4).getValue().equals(false)) {
                registers[4] = result;
            }//4
            if (rd.getBit(0).getValue().equals(true) && rd.getBit(1).getValue().equals(false) && rd.getBit(2).getValue().equals(true) && rd.getBit(3).getValue().equals(false) && rd.getBit(4).getValue().equals(false)) {
                registers[5] = result;
            }//5
            if (rd.getBit(0).getValue().equals(false) && rd.getBit(1).getValue().equals(true) && rd.getBit(2).getValue().equals(true) && rd.getBit(3).getValue().equals(false) && rd.getBit(4).getValue().equals(false)) {
                registers[6] = result;
            }//6
            if (rd.getBit(0).getValue().equals(true) && rd.getBit(1).getValue().equals(true) && rd.getBit(2).getValue().equals(true) && rd.getBit(3).getValue().equals(false) && rd.getBit(4).getValue().equals(false)) {
                registers[7] = result;
            }//7
            if (rd.getBit(0).getValue().equals(false) && rd.getBit(1).getValue().equals(false) && rd.getBit(2).getValue().equals(false) && rd.getBit(3).getValue().equals(true) && rd.getBit(4).getValue().equals(false)) {
                registers[8] = result;
            }//8
            if (rd.getBit(0).getValue().equals(true) && rd.getBit(1).getValue().equals(false) && rd.getBit(2).getValue().equals(false) && rd.getBit(3).getValue().equals(true) && rd.getBit(4).getValue().equals(false)) {
                registers[9] = result;
            }//9
            if (rd.getBit(0).getValue().equals(false) && rd.getBit(1).getValue().equals(true) && rd.getBit(2).getValue().equals(false) && rd.getBit(3).getValue().equals(true) && rd.getBit(4).getValue().equals(false)) {
                registers[10] = result;
            }//10
            if (rd.getBit(0).getValue().equals(true) && rd.getBit(1).getValue().equals(true) && rd.getBit(2).getValue().equals(false) && rd.getBit(3).getValue().equals(true) && rd.getBit(4).getValue().equals(false)) {
                registers[11] = result;
            }//11
            if (rd.getBit(0).getValue().equals(false) && rd.getBit(1).getValue().equals(false) && rd.getBit(2).getValue().equals(true) && rd.getBit(3).getValue().equals(true) && rd.getBit(4).getValue().equals(false)) {
                registers[12] = result;
            }//12
            if (rd.getBit(0).getValue().equals(true) && rd.getBit(1).getValue().equals(false) && rd.getBit(2).getValue().equals(true) && rd.getBit(3).getValue().equals(true) && rd.getBit(4).getValue().equals(false)) {
                registers[13] = result;
            }//13
            if (rd.getBit(0).getValue().equals(false) && rd.getBit(1).getValue().equals(true) && rd.getBit(2).getValue().equals(true) && rd.getBit(3).getValue().equals(true) && rd.getBit(4).getValue().equals(false)) {
                registers[14] = result;
            }//14
            if (rd.getBit(0).getValue().equals(true) && rd.getBit(1).getValue().equals(true) && rd.getBit(2).getValue().equals(true) && rd.getBit(3).getValue().equals(true) && rd.getBit(4).getValue().equals(false)) {
                registers[15] = result;
            }//15
            if (rd.getBit(0).getValue().equals(false) && rd.getBit(1).getValue().equals(false) && rd.getBit(2).getValue().equals(false) && rd.getBit(3).getValue().equals(false) && rd.getBit(4).getValue().equals(true)) {
                registers[16] = result;
            }//16
            if (rd.getBit(0).getValue().equals(true) && rd.getBit(1).getValue().equals(false) && rd.getBit(2).getValue().equals(false) && rd.getBit(3).getValue().equals(false) && rd.getBit(4).getValue().equals(true)) {
                registers[17] = result;
            }//17
            if (rd.getBit(0).getValue().equals(false) && rd.getBit(1).getValue().equals(true) && rd.getBit(2).getValue().equals(false) && rd.getBit(3).getValue().equals(false) && rd.getBit(4).getValue().equals(true)) {
                registers[18] = result;
            }//18
            if (rd.getBit(0).getValue().equals(true) && rd.getBit(1).getValue().equals(true) && rd.getBit(2).getValue().equals(false) && rd.getBit(3).getValue().equals(false) && rd.getBit(4).getValue().equals(true)) {
                registers[19] = result;
            }//19
            if (rd.getBit(0).getValue().equals(false) && rd.getBit(1).getValue().equals(false) && rd.getBit(2).getValue().equals(true) && rd.getBit(3).getValue().equals(false) && rd.getBit(4).getValue().equals(true)) {
                registers[20] = result;
            }//20
            if (rd.getBit(0).getValue().equals(true) && rd.getBit(1).getValue().equals(false) && rd.getBit(2).getValue().equals(true) && rd.getBit(3).getValue().equals(false) && rd.getBit(4).getValue().equals(true)) {
                registers[21] = result;
            }//21
            if (rd.getBit(0).getValue().equals(false) && rd.getBit(1).getValue().equals(true) && rd.getBit(2).getValue().equals(true) && rd.getBit(3).getValue().equals(false) && rd.getBit(4).getValue().equals(true)) {
                registers[22] = result;
            }//22
            if (rd.getBit(0).getValue().equals(true) && rd.getBit(1).getValue().equals(true) && rd.getBit(2).getValue().equals(true) && rd.getBit(3).getValue().equals(false) && rd.getBit(4).getValue().equals(true)) {
                registers[23] = result;
            }//23
            if (rd.getBit(0).getValue().equals(false) && rd.getBit(1).getValue().equals(false) && rd.getBit(2).getValue().equals(false) && rd.getBit(3).getValue().equals(true) && rd.getBit(4).getValue().equals(true)) {
                registers[24] = result;
            }//24
            if (rd.getBit(0).getValue().equals(true) && rd.getBit(1).getValue().equals(false) && rd.getBit(2).getValue().equals(false) && rd.getBit(3).getValue().equals(true) && rd.getBit(4).getValue().equals(true)) {
                registers[25] = result;
            }//25
            if (rd.getBit(0).getValue().equals(false) && rd.getBit(1).getValue().equals(true) && rd.getBit(2).getValue().equals(false) && rd.getBit(3).getValue().equals(true) && rd.getBit(4).getValue().equals(true)) {
                registers[26] = result;
            }//26
            if (rd.getBit(0).getValue().equals(true) && rd.getBit(1).getValue().equals(true) && rd.getBit(2).getValue().equals(false) && rd.getBit(3).getValue().equals(true) && rd.getBit(4).getValue().equals(true)) {
                registers[27] = result;
            }//27
            if (rd.getBit(0).getValue().equals(false) && rd.getBit(1).getValue().equals(false) && rd.getBit(2).getValue().equals(true) && rd.getBit(3).getValue().equals(true) && rd.getBit(4).getValue().equals(true)) {
                registers[28] = result;
            }//28
            if (rd.getBit(0).getValue().equals(true) && rd.getBit(1).getValue().equals(false) && rd.getBit(2).getValue().equals(true) && rd.getBit(3).getValue().equals(true) && rd.getBit(4).getValue().equals(true)) {
                registers[29] = result;
            }//29
            if (rd.getBit(0).getValue().equals(false) && rd.getBit(1).getValue().equals(true) && rd.getBit(2).getValue().equals(true) && rd.getBit(3).getValue().equals(true) && rd.getBit(4).getValue().equals(true)) {
                registers[30] = result;
            }//30
            if (rd.getBit(0).getValue().equals(true) && rd.getBit(1).getValue().equals(true) && rd.getBit(2).getValue().equals(true) && rd.getBit(3).getValue().equals(true) && rd.getBit(4).getValue().equals(true)) {
                registers[31] = result;
            }//31
            registers[0] = new Word(0);
        }
    }

    public boolean BOP(Word rs1, Word rd, Word function) {

        boolean conditionMet = false;

        //0000
        if (function.getBit(0).getValue().equals(false) && function.getBit(1).getValue().equals(false)
                && function.getBit(2).getValue().equals(false) && function.getBit(3).getValue().equals(false)) {

            conditionMet = alu.subtract(rs1, rd).getUnsigned() == 0;
        }

        //0001
        if (function.getBit(0).getValue().equals(false) && function.getBit(1).getValue().equals(false)
                && function.getBit(2).getValue().equals(false) && function.getBit(3).getValue().equals(true)) {

            conditionMet = alu.subtract(rs1, rd).getUnsigned() != 0;
        }

        //0010
        if (function.getBit(0).getValue().equals(false) && function.getBit(1).getValue().equals(false)
                && function.getBit(2).getValue().equals(true) && function.getBit(3).getValue().equals(false)) {

            conditionMet = alu.subtract(rs1, rd).getBit(31).getValue();
        }

        //0011
        if (function.getBit(0).getValue().equals(false) && function.getBit(1).getValue().equals(false)
                && function.getBit(2).getValue().equals(true) && function.getBit(3).getValue().equals(true)) {

            conditionMet = !alu.subtract(rs1, rd).getBit(31).getValue();
        }

        //0100
        if (function.getBit(0).getValue().equals(false) && function.getBit(1).getValue().equals(true)
                && function.getBit(2).getValue().equals(false) && function.getBit(3).getValue().equals(false)) {

            Word result = alu.subtract(rs1, rd);

            conditionMet = !result.getBit(31).getValue() && result.getUnsigned() != 0;
        }

        //0101
        if (function.getBit(0).getValue().equals(false) && function.getBit(1).getValue().equals(true)
                && function.getBit(2).getValue().equals(false) && function.getBit(3).getValue().equals(true)) {

            Word result = alu.subtract(rs1, rd);

            conditionMet = result.getBit(31).getValue() || result.getUnsigned() == 0;
        }

        return conditionMet;

    }

    public void Instruction() throws Exception {

        Word formatMask = new Word(3);
        Word opCode = currentInstruction.and(formatMask);

        //Branch 001
        if (opCode.getBit(0).getValue().equals(false) && opCode.getBit(1).getValue().equals(false)
                && opCode.getBit(2).getValue().equals(true)) {

            boolean shouldBranch = false;

            //00
            if (opCode.getBit(3).getValue().equals(false) && opCode.getBit(4).getValue().equals(false)) {

                PC = immediate;
                return;

            }
            //01
            if (opCode.getBit(3).getValue().equals(false) && opCode.getBit(4).getValue().equals(true)) {

                shouldBranch = true;
            }
            //10
            if (opCode.getBit(3).getValue().equals(true) && !opCode.getBit(4).getValue().equals(false)) {
                shouldBranch = BOP(rs1, rs2, function);
            }
            //11
            if (opCode.getBit(3).getValue().equals(true) && opCode.getBit(4).getValue().equals(true)) {
                shouldBranch = BOP(rs1, rd, function);
            }
            if (shouldBranch) {

                PC = alu.add2(PC, immediate);
            }
        }

        //Call 010
        if (opCode.getBit(0).getValue().equals(false) && opCode.getBit(1).getValue().equals(true)
                && opCode.getBit(2).getValue().equals(false)) {

            SP.decrement();
            MainMemory.write(SP, PC);

            //00
            if (opCode.getBit(3).getValue().equals(false) && opCode.getBit(4).getValue().equals(false)) {
                PC = immediate;

            }
            //01
            if (opCode.getBit(3).getValue().equals(false) && opCode.getBit(4).getValue().equals(true)) {
                PC = registers[(int) rd.getUnsigned()];

            }
            //10
            if (opCode.getBit(3).getValue().equals(true) && opCode.getBit(4).getValue().equals(false)) {
                if (BOP(rs1, rs2, function)) {
                    PC = registers[(int) rd.getUnsigned()];
                }
                //11
                if (opCode.getBit(3).getValue().equals(true) && opCode.getBit(4).getValue().equals(true)) {
                    if (BOP(rs1, rd, function)) {
                        PC = registers[(int) rd.getUnsigned()];
                    }
                }
            }

            //Push 011
            if (opCode.getBit(0).getValue().equals(false) && opCode.getBit(1).getValue().equals(true)
                    && opCode.getBit(2).getValue().equals(true)) {


                // 00
                if (opCode.getBit(3).getValue().equals(false) && opCode.getBit(4).getValue().equals(false)) {

                    //unused
                }
                // 01
                if (opCode.getBit(3).getValue().equals(false) && opCode.getBit(4).getValue().equals(true)) {

                    MainMemory.write(SP, registers[(int) rd.getUnsigned()]);
                    SP.decrement(); // Move stack pointer
                }
                // 10
                if (opCode.getBit(3).getValue().equals(true) && opCode.getBit(4).getValue().equals(false)) {

                    Bit[] functionArray = new Bit[4];
                    for (int i = 0; i < 4; i++) {
                        functionArray[i] = function.getBit(i);
                    }
                    alu.op1 = registers[(int) rs1.getUnsigned()];
                    alu.op2 = registers[(int) rs2.getUnsigned()];
                    alu.doOperation(functionArray);

                    MainMemory.write(SP, alu.result); // Push the result onto the stack
                    SP.decrement(); // Move stack pointer
                }
                // 11
                if (opCode.getBit(3).getValue().equals(true) && opCode.getBit(4).getValue().equals(true)) {
                    Bit[] functionArray = new Bit[4];
                    for (int i = 0; i < 4; i++) {
                        functionArray[i] = function.getBit(i);
                    }
                    alu.op1 = registers[(int) rd.getUnsigned()];
                    alu.op2 = registers[(int) rs1.getUnsigned()];
                    alu.doOperation(functionArray);

                    MainMemory.write(SP, alu.result); // Push the result onto the stack
                    SP.decrement(); // Move stack pointer
                }
            }

            // Load 100
            if (opCode.getBit(0).getValue().equals(true) && opCode.getBit(1).getValue().equals(false)
                    && opCode.getBit(2).getValue().equals(false)) {

                // 00
                if (opCode.getBit(3).getValue().equals(false) && opCode.getBit(4).getValue().equals(false)) {

                    MainMemory.read(SP);
                    SP.increment();

                }
                // 01
                if (opCode.getBit(3).getValue().equals(false) && opCode.getBit(4).getValue().equals(true)) {

                    rd = MainMemory.read(alu.add2(rd, immediate));

                }
                // 10
                if (opCode.getBit(3).getValue().equals(true) && opCode.getBit(4).getValue().equals(false)) {

                    rd = MainMemory.read(alu.add2(rs1, rs2));

                }
                // 11
                if (opCode.getBit(3).getValue().equals(true) && opCode.getBit(4).getValue().equals(true)) {

                    rd = MainMemory.read(alu.add2(rs1, immediate));

                }
            }

            // Store 101
            if (opCode.getBit(0).getValue().equals(true) && opCode.getBit(1).getValue().equals(false)
                    && opCode.getBit(2).getValue().equals(true)) {

                // 00
                if (opCode.getBit(3).getValue().equals(false) && opCode.getBit(4).getValue().equals(false)) {

                    //unused

                }
                // 01
                if (opCode.getBit(3).getValue().equals(false) && opCode.getBit(4).getValue().equals(true)) {

                    MainMemory.write(rd, immediate);

                }
                // 10
                if (opCode.getBit(3).getValue().equals(true) && opCode.getBit(4).getValue().equals(false)) {

                    MainMemory.write(alu.add2(rd, rs1), rs2);

                }
                // 11
                if (opCode.getBit(3).getValue().equals(true) && opCode.getBit(4).getValue().equals(true)) {

                    MainMemory.write(alu.add2(rd, immediate), rs1);

                }
            }

            // 	Pop/interrupt 110
            if (opCode.getBit(0).getValue().equals(true) && opCode.getBit(1).getValue().equals(true)
                    && opCode.getBit(2).getValue().equals(false)) {

                // 00
                if (opCode.getBit(3).getValue().equals(false) && opCode.getBit(4).getValue().equals(false)) {

                    //unused

                }
                // 01
                if (opCode.getBit(3).getValue().equals(false) && opCode.getBit(4).getValue().equals(true)) {

                    rd = MainMemory.read(SP);
                    SP.increment();

                }
                // 10
                if (opCode.getBit(3).getValue().equals(true) && opCode.getBit(4).getValue().equals(false)) {

                    rd = MainMemory.read(alu.subtract(SP, alu.add2(rs1, rs2)));

                }
                // 11
                if (opCode.getBit(3).getValue().equals(true) && opCode.getBit(4).getValue().equals(true)) {

                    rd = MainMemory.read(alu.subtract(SP, alu.add2(rs1, immediate)));

                }
            }
        }
    }

    /** constructor to initialize processor */
    public Processor() {
        PC = new Word();
        PC.set(0);
        SP = new Word();
        SP.set(0);
        currentInstruction = new Word();
        halted = new Bit(false);
        l2Cache = new L2Cache();
        instructionCache = new InstructionCache();
    }

    public static void incrementClockCycles(int cycles) {
        currentClockCycle += cycles;
    }
}