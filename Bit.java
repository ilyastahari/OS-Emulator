public class Bit {
    private boolean value;

    public void set(Boolean value) {
        this.value = value;
    } // sets the value of the bit

    public void toggle() {
        if (value) {
            this.value = false;
        } else {
            this.value = true;
        }
    }// changes the value from true to false or false to true

    public void set() {
        this.value = true;
    }// sets the bit to true

    public void clear() {
        this.value = false;
    }// sets the bit to false

    public Boolean getValue() {
        return this.value;
    }// returns the current value

    public Bit and(Bit other) {
        Bit newBit = new Bit(false);
        if (this.value) {
            if (other.value) {
                newBit.set(true);
            }
        }
        return newBit;
    } // performs and on two bits and returns a new bit set to the result

    public Bit or(Bit other) {
        Bit newBit = new Bit(false);
        if (this.value) {
            newBit.set(true);
        } else {
            if (other.value) {
                newBit.set(true);
            }
        }
        return newBit;
    }// performs or on two bits and returns a new bit set to the result

    public Bit xor(Bit other) {
        Bit newBit = new Bit(false);

        if (this.value) {
            if (other.value == false) {
                newBit.set(true);
            }
        } else {
            if (other.value) {
                newBit.set(true);
            }
        }
        return newBit;
    }// performs xor on two bits and returns a new bit set to the result

    public Bit not() {
        Bit newBit = new Bit(false);
        if (this.value) {
            newBit.set(false);
        } else {
            newBit.set(true);
        }
        return newBit;
    }
    // performs not on the existing bit, returning the result as a new bit

    public String toString() {
        return this.value ? "t" : "f";
    } // returns “t” or “f”

    public Bit(boolean value) {
        set(value);
    }
}