public class Token {

    /**the different token types in the form of enums */
    public enum tokenType {
        MATH, ADD, SUBTRACT, MULTIPLY, AND, OR, NOT, XOR, COPY, HALT, BRANCH, JUMP, CALL, PUSH, LOAD, RETURN, REGISTER,
        STORE, PEEK, POP, INTERRUPT, EQUAL, UNEQUAL, GREATER, LESS, GREATEROREQUAL, LESSOREQUAL, SHIFT, LEFT, RIGHT, NEWLINE, NUMBER
    }

    /**string which holds value of the token */
    private String value;

    private tokenType t;
    private int lineNumber;
    private int position;


    /**constructor for tokens that does not store value */
    public Token(tokenType type, int lineNum, int pos) {
        this.t = type;
        this.lineNumber = lineNum;
        this.position = pos;

    }

    /**constructor for tokens that store value */
    public Token(tokenType type, String val, int lineNum, int pos) {

        this.t = type;
        this.value = val;
        this.lineNumber = lineNum;
        this.position = pos;
    }

    /**setters and getters for all relavent token data */
    public String getvalue() {
        return this.value;
    }

    public void setvalue(String value) {
        this.value = value;
    }

    public tokenType gettype() {
        return this.t;
    }

    public void settype(tokenType type) {
        this.t = type;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    /**method to output the token type as well as the string/int */
    @Override
    public String toString() {
        return this.t + "(" + this.value + ") ";
    }
}
