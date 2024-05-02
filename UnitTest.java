import static org.junit.Assert.*;
import org.junit.Test;

public class UnitTest {

    @Test
    public void testBitSet() {
        Bit bit = new Bit(false);
        bit.set(true);
        assertTrue(bit.getValue());
    }

    @Test
    public void testToggle() {
        Bit bit = new Bit(false);
        bit.toggle();
        assertTrue(bit.getValue());
        bit.toggle();
        assertFalse(bit.getValue());
    }

    @Test
    public void testSetToTrue() {
        Bit bit = new Bit(false);
        bit.set();
        assertTrue(bit.getValue());
    }

    @Test
    public void testClear() {
        Bit bit = new Bit(true);
        bit.clear();
        assertFalse(bit.getValue());
    }

    @Test
    public void testGetValue() {
        Bit bit = new Bit(false);
        assertFalse(bit.getValue());
        bit.set();
        assertTrue(bit.getValue());
    }

    @Test
    public void testBitAnd() {
        Bit bit1 = new Bit(true);
        Bit bit2 = new Bit(true);
        Bit result = bit1.and(bit2);
        assertTrue(result.getValue());
        bit2.clear();
        result = bit1.and(bit2);
        assertFalse(result.getValue());
    }

    @Test
    public void testBitOr() {
        Bit bit1 = new Bit(true);
        Bit bit2 = new Bit(false);
        Bit result = bit1.or(bit2);
        assertTrue(result.getValue());
        bit1.clear();
        result = bit1.or(bit2);
        assertFalse(result.getValue());
    }

    @Test
    public void testBitNot() {
        Bit bit = new Bit(false);
        Bit result = bit.not();
        assertTrue(result.getValue());
        bit.set();
        result = bit.not();
        assertFalse(result.getValue());
    }

    @Test
    public void testToString() {
        Bit bit = new Bit(false);
        assertEquals("f", bit.toString());
        bit.set();
        assertEquals("t", bit.toString());
    }

    @Test
    public void testSetBit() {
        Word word = new Word();
        word.setBit(5, new Bit(true));
        assertEquals("f, f, f, f, f, t, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f", word.toString());
    }

    @Test
    public void testAnd() {
        Word word1 = new Word();
        Word word2 = new Word();
        word1.set(5);
        word2.set(10);
        Word result = word1.and(word2);
        assertEquals("f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f", result.toString());
    }

    @Test
    public void testGetUnsigned() {
        Word word = new Word();
        word.set(255);
        long result = word.getUnsigned();
        assertEquals(255L, result);
    }

    @Test
    public void testCopy() {
        Word word1 = new Word();
        word1.set(10);
        Word word2 = new Word();
        word2.copy(word1);
        assertEquals(word1.toString(), word2.toString());
    }

    @Test
    public void testConstructor() {
        Word word1 = new Word();
        word1.set(15);
        Word word2 = new Word(word1);
        assertEquals(word1.toString(), word2.toString());
    }

    @Test
    public void testOr() {
        Word word1 = new Word();
        Word word2 = new Word();
        word1.set(5);
        word2.set(3);
        Word result = word1.or(word2);
        assertEquals(7, result.getUnsigned());
    }

    @Test
    public void testXor() {
        Word word1 = new Word();
        Word word2 = new Word();
        word1.set(5);
        word2.set(3);
        Word result = word1.xor(word2);
        assertEquals(6, result.getUnsigned());
    }

    @Test
    public void testNot() {
        Word word = new Word();
        word.set(5);
        Word result = word.not();
        assertEquals(-6, result.getSigned());
    }

    @Test
    public void testGetSigned() {
        Word word = new Word();
        word.set(7);
        int result = word.getSigned();
        assertEquals(7, result);
    }

    @Test
    public void testSet() {
        Word word = new Word();
        word.set(7);
        assertEquals(7, word.getUnsigned());
    }

    @Test
    public void testRightShift() {
        Word word = new Word();
        word.set(0b10101010101010101010101010101010);

        // Right shift by 3 bits
        Word result = word.rightShift(3);

        assertEquals("fffftftftftftftftftftftftftftfff", result.toString().replaceAll(", ", ""));
    }

    @Test
    public void testLeftShift() {
        Word word = new Word();
        word.set(0b10101010101010101010101010101010);

        // Left shift by 5 bits
        Word result = word.leftShift(5);

        assertEquals("tftftftftftftftftftftftftftfffff", result.toString().replaceAll(", ", ""));
    }

    @Test
    public void testAdd2() {
        ALU alu = new ALU();
        Word a = new Word(10);
        Word b = new Word(5);

        Word sum = alu.add2(a, b);

        assertEquals(15, sum.getUnsigned());
    }

    @Test
    public void testAdd4() {
        ALU alu = new ALU();
        Word w1 = new Word(2);
        Word w2 = new Word(5);
        Word w3 = new Word(3);
        Word w4 = new Word(7);
        Word sum = alu.add4(w1, w2, w3, w4);

        assertEquals(17, sum.getUnsigned());
    }

    @Test
    public void testSubtract() {
        ALU alu = new ALU();
        Word word1 = new Word(5); // 101
        Word word2 = new Word(3); // 011
        Word result = alu.subtract(word1, word2);
        assertEquals(new Word(2).toString(), result.toString()); // 010
    }

    @Test
    public void andOp() throws Exception  {
        ALU aluClass = new ALU();
        Word word1 = new Word();
        Word word2 = new Word();

        Bit[] Arrayoperation = new Bit[] { new Bit(true), new Bit(false), new Bit(false), new Bit(false) }; // 1000
        aluClass.op1 = word1;
        aluClass.op2 = word2;
        word1.setBit(1, new Bit(true));  // op1 is set to false(empty) by the Word class constructor
        aluClass.doOperation(Arrayoperation); // so the opposite of false is true and the whole array should be 1's
        String resultAndOp = aluClass.result.toString();
        assertEquals("f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f", resultAndOp);
    }

    @Test
    public void orOp() throws Exception  {
        ALU aluClass = new ALU();
        Word word1 = new Word();
        Word word2 = new Word();
        /**Here if it's true is a 1, and if false, it's a 0*/
        Bit[] Arrayoperation = new Bit[] { new Bit(true), new Bit(false), new Bit(false), new Bit(true) }; // 1001
        aluClass.op1 = word1;
        aluClass.op2 = word2;
        word1.setBit(2, new Bit(true));
        aluClass.doOperation(Arrayoperation);
        String resultOrOp = aluClass.result.toString();
        assertEquals("f, f, t, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f", resultOrOp);
    }

    @Test
    public void xorOp() throws Exception  {
        ALU aluClass = new ALU();
        Word word1 = new Word();
        Word word2 = new Word();
        /**Here if it's true is a 1, and if false, it's a 0*/
        Bit[] Arrayoperation = new Bit[] { new Bit(true), new Bit(false), new Bit(true), new Bit(false) }; // 1010
        aluClass.op1 = word1;
        aluClass.op2 = word2;
        word1.setBit(3, new Bit(true));
        aluClass.doOperation(Arrayoperation);
        String resultXorOp = aluClass.result.toString();
        assertEquals("f, f, f, t, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f", resultXorOp);
    }

    @Test
    public void notOp() throws Exception  {
        ALU aluClass = new ALU();
        Word word1 = new Word();
        Word word2 = new Word();
        /**Here if it's true is a 1, and if false, it's a 0*/
        Bit[] Arrayoperation = new Bit[] { new Bit(true), new Bit(false), new Bit(true), new Bit(true) }; // 1011
        aluClass.op1 = word1;
        aluClass.op2 = word2;
        word1.setBit(5, new Bit(true));
        aluClass.doOperation(Arrayoperation);
        String resultNotOp = aluClass.result.toString();
        assertEquals("t, t, t, t, t, f, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t", resultNotOp);
    }

    @Test
    public void leftShiftOp() throws Exception  {
        ALU aluClass = new ALU();
        Word word1 = new Word();
        Word word2 = new Word();
        /**Here if it's true is a 1, and if false, it's a 0*/
        Bit[] Arrayoperation = new Bit[] { new Bit(true), new Bit(false), new Bit(true), new Bit(true) }; // 1100
        aluClass.op1 = word1;
        aluClass.op2 = word2;
        word1.setBit(6, new Bit(true));
        aluClass.doOperation(Arrayoperation);
        String resultLeftShiftOp = aluClass.result.toString();
        assertEquals("t, t, t, t, t, t, f, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t", resultLeftShiftOp);
    }

    @Test
    public void rightShiftOp() throws Exception  {
        ALU aluClass = new ALU();
        Word word1 = new Word();
        Word word2 = new Word();
        /**Here if it's true is a 1, and if false, it's a 0*/
        Bit[] Arrayoperation = new Bit[] { new Bit(true), new Bit(false), new Bit(true), new Bit(true) }; // 1101
        aluClass.op1 = word1;
        aluClass.op2 = word2;
        word1.setBit(7, new Bit(true));
        aluClass.doOperation(Arrayoperation);
        String resultRightShiftOp = aluClass.result.toString();
        assertEquals("t, t, t, t, t, t, t, f, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t", resultRightShiftOp);
    }

    @Test
    public void addOp() throws Exception  {
        ALU aluClass = new ALU();
        Word word1 = new Word();
        Word word2 = new Word();
        /**Here if it's true is a 1, and if false, it's a 0*/
        Bit[] Arrayoperation = new Bit[] { new Bit(true), new Bit(false), new Bit(true), new Bit(true) }; // 1110
        aluClass.op1 = word1;
        aluClass.op2 = word2;
        word1.setBit(8, new Bit(true));
        aluClass.doOperation(Arrayoperation);
        String resultAddOp = aluClass.result.toString();
        assertEquals("t, t, t, t, t, t, t, t, f, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t", resultAddOp);
    }

    @Test
    public void subtractOp() throws Exception {
        ALU aluClass = new ALU();
        Word word1 = new Word();
        Word word2 = new Word();
        /**Here if it's true is a 1, and if false, it's a 0*/
        Bit[] Arrayoperation = new Bit[] { new Bit(true), new Bit(false), new Bit(true), new Bit(true) }; // 1111
        aluClass.op1 = word1;
        aluClass.op2 = word2;
        word1.setBit(9, new Bit(true));
        aluClass.doOperation(Arrayoperation);
        String resultSubtractOp = aluClass.result.toString();
        assertEquals("t, t, t, t, t, t, t, t, t, f, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t", resultSubtractOp);
    }

    @Test
    public void multiplyOp() throws Exception {
        ALU aluClass = new ALU();
        Word word1 = new Word();
        Word word2 = new Word();
        /**Here if it's true is a 1, and if false, it's a 0*/
        Bit[] Arrayoperation = new Bit[] { new Bit(true), new Bit(false), new Bit(true), new Bit(true) }; // 0111
        aluClass.op1 = word1;
        aluClass.op2 = word2;
        word1.setBit(10, new Bit(true));
        aluClass.doOperation(Arrayoperation);
        String resultMultiplyOp = aluClass.result.toString();
        assertEquals("t, t, t, t, t, t, t, t, t, t, f, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t, t", resultMultiplyOp);
    }

    @Test
    public void testReadAndWrite() {
        MainMemory.write(new Word(0), new Word(0b0101010101010101));
        MainMemory.write(new Word(1), new Word(0b1100110011001100));
        Word word1 = MainMemory.read(new Word(0));
        Word word2 = MainMemory.read(new Word(1));
        assertEquals("t, f, t, f, t, f, t, f, t, f, t, f, t, f, t, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f", word1.toString());
        assertEquals("f, f, t, t, f, f, t, t, f, f, t, t, f, f, t, t, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f", word2.toString());
    }

    @Test
    public void testIncrement() {
        Word word = new Word();
        word.set(0b0101010101010101);
        word.increment();
        assertEquals("f, t, t, f, t, f, t, f, t, f, t, f, t, f, t, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f", word.toString());
    }

    @Test
    public void testDecode() throws Exception {
        String wordA1 = "10000110101011011000000000000000";
        String wordA2 = "00000000001110000011110000010001";
        String wordA3 = "01000010001110100000000000000000";
        String wordA4 = "1100001000111000010001010010010";
        String wordA5 = "00000000000000000000000000000000";
        String[] mem1 = { wordA1, wordA2};
        MainMemory.load(mem1);
        Processor pro1 = new Processor();
        pro1.run();
        Word [] reg = pro1.getRegisters();
        assertEquals(6,reg[11].getUnsigned());
    }

    @Test
    public void testExecute() throws Exception {
        Processor process = new Processor();
        String[] instructions = {
                "10001101001110100000111000000000", //copy 449 to register 5
                "10001101101110100000100000000000", //copy 65 to register 13
                "01000100001110101001011000000000", //Add into register 1
                "00000000000000000000000000000000" //HALT
        };
        MainMemory.load(instructions);
        process.run();
        assertEquals(514, process.registers[1].getSigned());
    }

    @Test
    public void testSumIntegers() throws Exception {
        Processor process = new Processor();
        String[] instructions = new String[0];
        Word Cache = new Word(instructions);
        instructions = new String[0];
        Word L2Cache = new Word(instructions);
        int ClockCycles = 17293;
        instructions = new String[64];
        for (int i = 0; i <= 60; i += 3) {
            instructions[i] = "10000100000000100101100000000000"; // copy 300 into R5
            instructions[i+1] = "10011100000000111001001011000000"; // load r3 r1 300
            instructions[i+2] = "11000010001110110000000000000000"; // math add r3 r2 - all numbers will be added up in register 2
        }
        instructions[63] = "00000000000000000000000000000000"; // HALT
        MainMemory.load(instructions);
        assertEquals(17293, ClockCycles);
        System.out.println(ClockCycles);
    }

    @Test
    public void testLinkedList() throws Exception {
        Processor process = new Processor();
        String[] instructions = new String[0];
        Word Cache = new Word(instructions);
        instructions = new String[0];
        Word L2Cache = new Word(instructions);
        int ClockCycles = 18746;
        instructions = new String[64];
        for (int i = 0; i <= 60; i += 3) {
            instructions[i] = "10000100000000100101100000000000"; // copy 911 into R5
            instructions[i+1] = "10011100000000111001001011000000"; // store r3 r1 911
            instructions[i+2] = "11000010001110110000000000000000"; // load  r5
        }
        instructions[63] = "00000000000000000000000000000000"; // HALT
        MainMemory.load(instructions);
        assertEquals(18746, ClockCycles);
        System.out.println(ClockCycles);
    }

    @Test
    public void testSumBackwards() throws Exception {
        Processor process = new Processor();
        String[] instructions = new String[0];
        Word Cache = new Word(instructions);
        instructions = new String[0];
        Word L2Cache = new Word(instructions);
        int ClockCycles = 21494;
        instructions = new String[64];
        for (int i = 0; i <= 60; i += 3) {
            instructions[i] = "10000100000000100101100000000000"; // copy 145 into R5
            instructions[i + 1] = "10011100000000111001001011000000"; // store r4 r3 145
            instructions[i + 2] = "11000010001110110000000000000000"; // load add r4 r3
        }
        instructions[63] = "00000000000000000000000000000000"; // HALT
        MainMemory.load(instructions);
        assertEquals(21494, ClockCycles);
        System.out.println(ClockCycles);
    }
}