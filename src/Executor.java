
    import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;

    public class Executor {
        public void execute(String javaCode) throws IOException {
            String className = "TranslatedCode";
            String javaFileName = className + ".java";

            // Write the translated Java code to a file
            try (PrintWriter writer = new PrintWriter(new FileWriter(javaFileName))) {
                writer.print(javaCode);
            }

            // Compile the Java code
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            int result = compiler.run(null, null, null, javaFileName);
            if (result != 0) {
                throw new RuntimeException("Failed to compile Java code.");
            }

            // Run the compiled Java code
            Process process = Runtime.getRuntime().exec("java " + className);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

