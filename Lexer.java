import java.util.HashMap;
import java.util.LinkedList;

public class Lexer {

    private StringHandler stringManager;
    private int linePosition;
    private int characterPosition;

    /**HashMap of <String, TokenType> , fully populated  */
    private static HashMap<String, Token.tokenType> knownWords = new HashMap<String, Token.tokenType>();


    public static void keyWords() {
        /**All the keyword tokenTypes being initialized with semantically corresponding types*/
        knownWords.put("math", Token.tokenType.MATH);
        knownWords.put("add", Token.tokenType.ADD);
        knownWords.put("subtract", Token.tokenType.SUBTRACT);
        knownWords.put("multiply", Token.tokenType.MULTIPLY);
        knownWords.put("and", Token.tokenType.AND);
        knownWords.put("or", Token.tokenType.OR);
        knownWords.put("not", Token.tokenType.NOT);
        knownWords.put("xor", Token.tokenType.XOR);
        knownWords.put("copy", Token.tokenType.COPY);
        knownWords.put("halt", Token.tokenType.HALT);
        knownWords.put("branch", Token.tokenType.BRANCH);
        knownWords.put("jump", Token.tokenType.JUMP);
        knownWords.put("call", Token.tokenType.CALL);
        knownWords.put("push", Token.tokenType.PUSH);
        knownWords.put("load", Token.tokenType.LOAD);
        knownWords.put("return", Token.tokenType.RETURN);
        knownWords.put("store", Token.tokenType.STORE);
        knownWords.put("peek", Token.tokenType.PEEK);
        knownWords.put("pop", Token.tokenType.POP);
        knownWords.put("interrupt", Token.tokenType.INTERRUPT);
        knownWords.put("equal", Token.tokenType.EQUAL);
        knownWords.put("unequal", Token.tokenType.UNEQUAL);
        knownWords.put("greater", Token.tokenType.GREATER);
        knownWords.put("less", Token.tokenType.LESS);
        knownWords.put("greaterOrEqual", Token.tokenType.GREATEROREQUAL);
        knownWords.put("lessOrEqual", Token.tokenType.LESSOREQUAL);
        knownWords.put("shift", Token.tokenType.SHIFT);
        knownWords.put("left", Token.tokenType.LEFT);
        knownWords.put("right", Token.tokenType.RIGHT);
    }


    /**constructor which creates a stringManager and takes string as a param*/
    /**also keeps track of what line we're at and char position*/
    public Lexer(String assemblyfile){
        stringManager = new StringHandler(assemblyfile);
        linePosition = 0;
        characterPosition = 0;
        keyWords();
    }


    /**the lex method which breaks data from StringManager into linkedList of tokens*/
    public LinkedList<Token> Lex() throws Exception{
        LinkedList<Token> tokens = new LinkedList<>();
        while(!stringManager.isDone()){
            char next = stringManager.Peek(0);
            if( next == '\t' || next == ' '){
                characterPosition++;
                stringManager.GetChar();
            }
            else if (next == '\n'){
                Token sep = new Token(Token.tokenType.NEWLINE, linePosition, characterPosition);
                tokens.add(sep);
                linePosition++;
                characterPosition = 0;
                stringManager.GetChar();
            }
            else if( next == '\r'){
                stringManager.GetChar();
            }
            else if (Character.isLetter(next) && (next != 'R' && !Character.isDigit(stringManager.Peek(1))) ){
                tokens.add(ProcessWord());
            }

            else if (next == 'R'){
                stringManager.GetChar();
                tokens.add(new Token(Token.tokenType.REGISTER, ProcessNumber().getvalue(), linePosition, characterPosition));
            
            }
            else if(Character.isDigit(next)){
                tokens.add(ProcessNumber());

            }
        }
        return tokens;
    }


    /**method which returns a NUMBER token and only accepts 0-9 and one . per token */
    private Token ProcessNumber() throws Exception{
        boolean foundPoint = false;
        String currentString = "";
        while (!stringManager.isDone() && (Character.isDigit(stringManager.Peek(0)) || stringManager.Peek(0) == '.')){
            char c = stringManager.GetChar();
            currentString += c;
            if (c == '.' && foundPoint)
                throw new Exception();
            else if (c == '.')
                foundPoint = true;
        }
        Token number = new Token(Token.tokenType.NUMBER, currentString, linePosition, characterPosition);
        characterPosition += currentString.length();
        return number;

    }


    /**method which returns a WORD token and only accepts letters, digits and _ */
    /**Now modified to check hashMap for known words and makes a token specific to the word. */
    private Token ProcessWord() {
        String currentString = "";
        while (!stringManager.isDone() && (Character.isLetterOrDigit(stringManager.Peek(0)))){
            char c = stringManager.GetChar();
            currentString += c;
        }
        Token word;
  
        word = new Token(knownWords.get(currentString.toLowerCase()), linePosition, characterPosition);

        /**this line keeps track of how deep i am into a line and update length of WORD */
        characterPosition += currentString.length();

        return word;

    }

}