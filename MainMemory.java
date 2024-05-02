public class MainMemory {
    private static Word[] memory;  //static array of 1024 words

    /** initializes memory if not already done */
    private static void initMemo() {
        if (memory == null) {
            memory = new Word[1024]; // Allocate memory array
            Word zeroWord = new Word(0); // Default Word initialized to 0
            for (int i = 0; i < memory.length; i++) {
                memory[i] = new Word(zeroWord);
            }
            }

        }


    /** reads a word from memory at an address */
    public static Word read(Word address) {
        initMemo();
        int index = (int) address.getUnsigned(); // gets index from address
        if (index >= 0 && index < memory.length) {
            return new Word(memory[index]);
        } else {
            throw new IllegalArgumentException("Out of Bounds!" + address.getUnsigned());
        }
    }

    /** writes word to memory at an address */
    public static void write(Word address, Word value) {
        initMemo();
        int index = (int) address.getUnsigned();
        if (index >= 0 && index < memory.length) {
            memory[index] = new Word(value);
        }
    }

    /** loads data into memory from an array of strings */
    public static void load(String[] data) {
        initMemo();
        int index = 0;
        for (String line : data) {
            if (index >= memory.length) {
                break;
            }
            Word word = new Word();
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                if (c == '0') {
                    word.setBit(i, new Bit(false));
                } else if (c == '1') {
                    word.setBit(i, new Bit(true));
                }
            }
            memory[index++] = word;
        }
    }
}