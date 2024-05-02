import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;



public class main {

    /**this takes the command line parameter of filename */
    public static void main(String[] args) throws Exception {
        String filename = "example.txt";
        String assemblyfile = getAllBytes(filename);
        Lexer awkReader = new Lexer(assemblyfile);
        LinkedList<Token> tokens = awkReader.Lex();
        Parser parse = new Parser(tokens);
        parse.parseProgram();

    }

    /**method which calls GetAllBytes and passes result to lexer */
    /**prints out resultant tokens */
    private static String getAllBytes(String filename) throws IOException {
        Path myPath = Paths.get(filename);
        return new String(Files.readAllBytes(myPath));
    }
}
