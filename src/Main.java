import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Enter Go code (end input with an empty line):");
        StringBuilder sourceCode = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                sourceCode.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Tokenize the source code
        Lexer lexer = new Lexer(sourceCode.toString());
        List<Token> tokens = lexer.tokenize();

        // Parse the tokens into an AST
        Parser parser = new Parser(tokens);
        List<AST> ast = parser.parse();

        // Translate the AST into Java code
        Translator translator = new Translator();
        String javaCode = translator.translate(ast);
        System.out.println("Translated Java Code:");
        System.out.println(javaCode);

        // Execute the translated Java code
        Executor executor = new Executor();
        try {
            executor.execute(javaCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}