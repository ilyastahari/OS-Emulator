public class ALU {

    public Word op1;
    public Word op2;
    public Word result = new Word();

    /** this method adds two bits together*/
    public Word add2(Word a, Word b) {
        Word result = new Word();
        boolean carry = false;

        for (int i = 0; i < 32; i++) {
            boolean bit1 = a.getBit(i).getValue();
            boolean bit2 = b.getBit(i).getValue();

            boolean sum = (bit1 ^ bit2 ^ carry);
            carry = (bit1 && bit2) || (bit1 && carry) || (bit2 && carry);
            result.setBit(i, new Bit(sum));
        }

        return result;
    }

    public Word add4(Word a, Word b, Word c, Word d) {
        Word result = new Word();
        Word firstCarryIn = new Word();
        Word secondCarryIn = new Word();
        for (int i = 0; i < 32; i++) {
            Word firstBit = new Word(a.getBit(i).getValue());
            Word secondBit = new Word(b.getBit(i).getValue());
            Word thirdBit = new Word(c.getBit(i).getValue());
            Word fourthBit = new Word(d.getBit(i).getValue());
            Word firstSum = firstBit.xor(secondBit).xor(thirdBit).xor(fourthBit);
            Word secondSum = firstSum.xor(firstCarryIn);
            Word thirdSum = secondSum.xor(secondCarryIn);
            Word firstCarryOut = (firstBit.and(secondBit)).or(firstBit.and(thirdBit)).or(secondBit.and(thirdBit)).or(firstBit.and(fourthBit)).or(secondBit.and(fourthBit)).or(thirdBit.and(fourthBit));
            Word secondCarryOut = (firstSum.and(firstCarryIn)).or(firstSum.and(secondCarryIn)).or(firstCarryIn.and(secondCarryIn));
            result.setBit(i, thirdSum.getBit(0));
            firstCarryIn = firstCarryOut;
            secondCarryIn = secondCarryOut;
        }
        return result;
    }


    /** this method subtracts two words together*/
    public Word subtract(Word op1, Word op2) {

        Word minusOp2 = op2.not();

        Word minusOp2PlusOne = add2(minusOp2, new Word(1));

        return add2(op1, minusOp2PlusOne);
    }

    /** this method multiplies two words together*/
    public Word multiply(Word op1, Word op2) {
        //Generate Partial Products
        Word[] partialProducts = new Word[32];
        for (int i = 0; i < 32; i++) {
            if (op2.getBit(i).getValue()) {
                partialProducts[i] = op1.leftShift(i);
            } else {
                partialProducts[i] = new Word(); // Represents 0
            }
        }

        Word[] roundOneResults = new Word[8];
        for (int i = 0; i < 8; i++) {
            roundOneResults[i] = add4(partialProducts[4*i], partialProducts[4*i+1], partialProducts[4*i+2], partialProducts[4*i+3]);
        }

        Word[] roundTwoResults = new Word[2];
        for (int i = 0; i < 2; i++) {
            roundTwoResults[i] = add4(roundOneResults[4*i], roundOneResults[4*i+1], new Word(), new Word()); // Adding zeros for the last two parameters since we only have 2 Words to add
        }

        Word finalResult = add2(roundTwoResults[0], roundTwoResults[1]);

        return finalResult;
    }

    /** This method looks at the array of bits (there will be 4) and determines the operation
     * compares bits to determine operation*/
    public void doOperation(Bit[] operation) {
        if (operation[0].getValue() && !operation[1].getValue() && !operation[2].getValue() && !operation[3].getValue()) {
            result = op1.and(op2);
        } else if (operation[0].getValue() && !operation[1].getValue() && !operation[2].getValue() && operation[3].getValue()) {
            result = op1.or(op2);
        } else if (operation[0].getValue() && !operation[1].getValue() && operation[2].getValue() && !operation[3].getValue()) {
            result = op1.xor(op2);
            /**(not “op1”; ignores op2)*/
        } else if (operation[0].getValue() && !operation[1].getValue() && operation[2].getValue() && operation[3].getValue()) {
            result = op1.not();
            /**(“op1” is the value to shift, “op2” is the amount to shift; ignores all but the lowest 5 bits)*/
        } else if (operation[0].getValue() && !operation[1].getValue() && !operation[2].getValue() && !operation[3].getValue()) {
            Word sum = new Word();
            for (int i = 0; i < op2.length() - 27; i++) {
                sum.setBit(i, op2.getBit(i));
            }
            result.setBitArray(op1.leftShift(sum.getSigned()).getBitArray());
            /**(“op1” is the value to shift, “op2” is the amount to shift; ignores all but the lowest 5 bits)*/
        } else if (operation[0].getValue() && !operation[1].getValue() && !operation[2].getValue() && !operation[3].getValue()) {
            Word sum = new Word();
            for (int i = 0; i < op2.length() - 27; i++) {
                sum.setBit(i, op2.getBit(i));
            }
            result.setBitArray(op1.rightShift(sum.getSigned()).getBitArray());
        } else if (operation[0].getValue() && operation[1].getValue() && operation[2].getValue() && !operation[3].getValue()) {
            result.setBitArray(add2(op1, op2).getBitArray());
        } else if (operation[0].getValue() && operation[1].getValue() && operation[2].getValue() && operation[3].getValue()) {
            result.setBitArray(subtract(op1, op2).getBitArray());
        } else if (!operation[0].getValue() && operation[1].getValue() && operation[2].getValue() && operation[3].getValue()) {
            result.setBitArray(multiply(op1, op2).getBitArray());
        }
    }
}