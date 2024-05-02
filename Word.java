import java.lang.reflect.Method;

public class Word {
    private Bit[] bitArray;

    public Bit getBit(int i) {
        return new Bit(this.bitArray[i].getValue());
    }// Get a new Bit that has the same value as bit i

    public void setBitArray(Bit[] bitArray) {
        this.bitArray = bitArray;
    }

    public Bit[] getBitArray() {
        return bitArray;
    }

    public void setBit(int i, Bit value) {
        this.bitArray[i].set(value.getValue());
    } // set bit i's value
    

    public Word and(Word other) {
        Word newWord = new Word();
        for (int i = 0; i < 32; i++) {
            newWord.setBit(i, this.getBit(i).and(other.getBit(i)));
        }
        return newWord;
    } // and two words, returning a new Word

    public Word or(Word other) {
        Word newWord = new Word();
        for (int i = 0; i < 32; i++) {
            newWord.setBit(i, this.getBit(i).or(other.getBit(i)));
        }
        return newWord;
    } // or two words, returning a new Word

    public Word xor(Word other) {
        Word newWord = new Word();
        for (int i = 0; i < 32; i++) {
            newWord.setBit(i, this.getBit(i).xor(other.getBit(i)));
        }
        return newWord;
    }// xor two words, returning a new Word

    public Word not() {
        Word newWord = new Word();
        for (int i = 0; i < 32; i++) {
            newWord.setBit(i, this.getBit(i).not());
        }
        return newWord;
    } // negate this word, creating a new Word

    public Word rightShift(int amount) {
        Word newWord = new Word();

        for (int i = 0; i < 32 - amount; i++) {
            newWord.setBit(i + amount, this.getBit(i));
        }
        for (int i = 32 - amount; i < 32; i++) {
            newWord.setBit(i, new Bit(false));
        }
        return newWord;
    } // right shift this word by amount bits, creating a new Word

    public Word leftShift(int amount) {
        Word newWord = new Word();

        for (int i = 0; i < 32 - amount; i++) {
            newWord.setBit(i, this.getBit(i + amount));
        }
        for (int i = 32 - amount; i < 32; i++) {
            newWord.setBit(i, new Bit(false));
        }

        return newWord;
    }// left shift this word by amount bits, creating a new Word

    public long getUnsigned() {
        long newUnsigned = 0;
        for (int i = 31; i >= 0; i--) {
            newUnsigned = (newUnsigned << 1) | (this.getBit(i).getValue() ? 1 : 0);
        }
        return newUnsigned;
    } // returns the value of this word as a long

    public int getSigned() {
        int newSigned = 0;
        for (int i = 31; i >= 0; i--) {
            newSigned = (newSigned << 1) | (this.getBit(i).getValue() ? 1 : 0);
        }
        return newSigned;
    } // returns the value of this word as an int

    public void copy(Word other) {
        for (int i = 0; i < 32; i++) {
            this.setBit(i, other.getBit(i));
        }
    } // copies the values of the bits from another Word into this one

    public void set(int value) {
        for (int i = 0; i < 32; i++) {
            boolean bitValue = ((value >>> i) & 1) == 1;
            this.bitArray[i].set(bitValue);
        }
    } // set the value of the bits of this Word (used for tests)

    public Word() {
        this.bitArray = new Bit[32];
        for (int i = 0; i < 32; i++) {
            this.bitArray[i] = new Bit(false);
        }
    }

    public Word(boolean value) {
        this.bitArray = new Bit[32];
        for(int i = 0; i<32; i++){
            this.bitArray[i] = new Bit(value);
        }
    }

    public Word(Word other) {
        this.bitArray = new Bit[32];
        for (int i = 0; i < 32; i++) {
            this.bitArray[i] = new Bit(other.getBit(i).getValue());
        }
    }

    public Word(int value) {
        this();
        set(value);
    }

    public String toString() {
        String newString = "";
        for (int i = 0; i < 32; i++) {
            newString += (this.getBit(i).getValue() ? 't' : 'f');
            if (i < 31) {
                newString += ", ";
            }
        }
        return newString;
    }// returns a comma separated string t’s and f’s

    public int length() {
        return bitArray.length;
    }

    
    public void increment() {
        Bit carry = new Bit(true);
        for (int i = 0; i < this.bitArray.length; i++) {
            Bit temp = this.bitArray[i];
            this.bitArray[i] = this.bitArray[i].xor(carry);
            carry = temp.and(carry);
        }
    }

    public void decrement() {
        Bit carry = new Bit(true);
        for (int i = 0; i < this.bitArray.length; i++) {
            Bit temp = this.bitArray[i].not();
            this.bitArray[i] = temp.xor(carry);
            carry = temp.and(carry).not();
        }
    }

    public Word(String[] instructions) {
    }

}