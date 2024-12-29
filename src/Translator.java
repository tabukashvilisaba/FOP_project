import java.util.List;

class Translator {
    public String translate(List<AST> ast) {
        StringBuilder javaCode = new StringBuilder();
        javaCode.append("import java.util.Scanner;\n");
        javaCode.append("public class TranslatedCode {\n");
        javaCode.append("    public static void main(String[] args) {\n");
        javaCode.append("        Scanner scanner = new Scanner(System.in);\n");

        for (AST node : ast) {
            javaCode.append(translateNode(node));
        }

        javaCode.append("    }\n");
        javaCode.append("}\n");
        return javaCode.toString();
    }

    private String translateNode(AST node) {
        if (node instanceof VarDeclaration) {
            VarDeclaration varDecl = (VarDeclaration) node;
            StringBuilder result = new StringBuilder();
            for (String var : varDecl.variables) {
                result.append("        int ").append(var).append(";\n");
            }
            return result.toString();
        } else if (node instanceof Assignment) {
            Assignment assignment = (Assignment) node;
            return "        " + assignment.variable + " = " + translateExpr(assignment.value) + ";\n";
        } else if (node instanceof IfStatement) {
            IfStatement ifStmt = (IfStatement) node;
            StringBuilder result = new StringBuilder();
            result.append("        if (").append(translateExpr(ifStmt.condition)).append(") {\n");
            for (AST stmt : ifStmt.body) {
                result.append(translateNode(stmt));
            }
            result.append("        }\n");
            return result.toString();
        } else if (node instanceof ForLoop) {
            ForLoop forLoop = (ForLoop) node;
            StringBuilder result = new StringBuilder();
            result.append("        while (").append(translateExpr(forLoop.condition)).append(") {\n");
            for (AST stmt : forLoop.body) {
                result.append(translateNode(stmt));
            }
            result.append("        }\n");
            return result.toString();
        } else if (node instanceof FuncDeclaration) {
            FuncDeclaration funcDecl = (FuncDeclaration) node;
            StringBuilder result = new StringBuilder();
            result.append("    public static void ").append(funcDecl.name).append("() {\n");
            for (AST stmt : funcDecl.body) {
                result.append(translateNode(stmt));
            }
            result.append("    }\n");
            return result.toString();
        } else if (node instanceof Variable) {
            Variable variable = (Variable) node;
            return variable.name;
        } else {
            throw new RuntimeException("Unknown AST node: " + node);
        }
    }

    private String translateExpr(AST expr) {
        if (expr instanceof Num) {
            return ((Num) expr).value;
        } else if (expr instanceof BinaryOp) {
            BinaryOp binOp = (BinaryOp) expr;
            return translateExpr(binOp.left) + " " + binOp.op + " " + translateExpr(binOp.right);
        } else if (expr instanceof Variable) {
            return ((Variable) expr).name;
        } else {
            throw new RuntimeException("Unknown expression: " + expr);
        }
    }
}