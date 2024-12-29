import java.util.ArrayList;
import java.util.List;


class AST {
    // Abstract base class for AST nodes
}
class Variable extends AST {
    public String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("Variable(%s)", name);
    }
}

class VarDeclaration extends AST {
    public List<String> variables;
    public String type;

    public VarDeclaration(List<String> variables, String type) {
        this.variables = variables;
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("VarDeclaration(%s, %s)", variables, type);
    }
}

class Assignment extends AST {
    public String variable;
    public AST value;

    public Assignment(String variable, AST value) {
        this.variable = variable;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("Assignment(%s, %s)", variable, value);
    }
}

class BinaryOp extends AST {
    public AST left;
    public String op;
    public AST right;

    public BinaryOp(AST left, String op, AST right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }

    @Override
    public String toString() {
        return String.format("BinaryOp(%s, %s, %s)", left, op, right);
    }
}

class Num extends AST {
    public String value;

    public Num(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("Num(%s)", value);
    }
}

class IfStatement extends AST {
    public AST condition;
    public List<AST> body;

    public IfStatement(AST condition, List<AST> body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public String toString() {
        return String.format("IfStatement(%s, %s)", condition, body);
    }
}

class ForLoop extends AST {
    public AST condition;
    public List<AST> body;

    public ForLoop(AST condition, List<AST> body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public String toString() {
        return String.format("ForLoop(%s, %s)", condition, body);
    }
}

class FuncDeclaration extends AST {
    public String name;
    public List<AST> body;

    public FuncDeclaration(String name, List<AST> body) {
        this.name = name;
        this.body = body;
    }

    @Override
    public String toString() {
        return String.format("FuncDeclaration(%s, %s)", name, body);
    }
}

class Parser {
    private List<Token> tokens;
    private int pos = 0;
    private Token currentToken;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentToken = tokens.get(pos);
    }

    private void advance() {
        pos++;
        if (pos < tokens.size()) {
            currentToken = tokens.get(pos);
        } else {
            currentToken = null;
        }
    }

    private void eat(String tokenType) {
        if (currentToken != null && currentToken.type.equals(tokenType)) {
            advance();
        } else {
            throw new RuntimeException("Unexpected token: " + currentToken);
        }
    }

    private AST factor() {
        Token token = currentToken;
        if (token.type.equals("NUMBER")) {
            eat("NUMBER");
            return new Num(token.value);
        } else if (token.type.equals("IDENTIFIER")) {
            eat("IDENTIFIER");
            return new Variable(token.value); // Change to Variable to support identifiers
        } else {
            throw new RuntimeException("Unexpected token: " + token);
        }
    }

    private AST term() {
        AST node = factor();
        while (currentToken != null && (currentToken.type.equals("MULTIPLY") || currentToken.type.equals("DIVIDE"))) {
            Token token = currentToken;
            if (token.type.equals("MULTIPLY")) {
                eat("MULTIPLY");
            } else if (token.type.equals("DIVIDE")) {
                eat("DIVIDE");
            }
            node = new BinaryOp(node, token.value, factor());
        }
        return node;
    }

    private AST expr() {
        AST node = term();
        while (currentToken != null && (currentToken.type.equals("PLUS") || currentToken.type.equals("MINUS"))) {
            Token token = currentToken;
            if (token.type.equals("PLUS")) {
                eat("PLUS");
            } else if (token.type.equals("MINUS")) {
                eat("MINUS");
            }
            node = new BinaryOp(node, token.value, term());
        }
        return node;
    }

    private AST parseAssignment() {
        Token variable = currentToken;
        eat("IDENTIFIER");
        eat("ASSIGN");
        AST value = expr();
        return new Assignment(variable.value, value);
    }

    private AST parseVarDeclaration() {
        eat("VAR");
        List<String> variables = new ArrayList<>();
        while (currentToken.type.equals("IDENTIFIER")) {
            variables.add(currentToken.value);
            advance();
            if (currentToken != null && currentToken.type.equals("COMMA")) {
                eat("COMMA");
            }
        }
        // Expecting a type identifier (e.g., "int")
        String type = currentToken.value;
        eat("IDENTIFIER");
        return new VarDeclaration(variables, type);
    }

    private AST parseIfStatement() {
        eat("IF");
        eat("LPAREN");
        AST condition = expr();
        eat("RPAREN");
        eat("LBRACE");
        List<AST> body = new ArrayList<>();
        while (currentToken != null && !currentToken.type.equals("RBRACE")) {
            body.add(parseStatement());
        }
        eat("RBRACE");
        return new IfStatement(condition, body);
    }

    private AST parseForLoop() {
        eat("FOR");
        AST condition = expr();
        eat("LBRACE");
        List<AST> body = new ArrayList<>();
        while (currentToken != null && !currentToken.type.equals("RBRACE")) {
            body.add(parseStatement());
        }
        eat("RBRACE");
        return new ForLoop(condition, body);
    }

    private AST parseFuncDeclaration() {
        eat("FUNC");
        String name = currentToken.value;
        eat("IDENTIFIER");
        eat("LPAREN");
        eat("RPAREN");
        eat("LBRACE");
        List<AST> body = new ArrayList<>();
        while (currentToken != null && !currentToken.type.equals("RBRACE")) {
            body.add(parseStatement());
        }
        eat("RBRACE");
        return new FuncDeclaration(name, body);
    }

    private AST parseStatement() {
        if (currentToken.type.equals("VAR")) {
            return parseVarDeclaration();
        } else if (currentToken.type.equals("IDENTIFIER")) {
            return parseAssignment();
        } else if (currentToken.type.equals("IF")) {
            return parseIfStatement();
        } else if (currentToken.type.equals("FOR")) {
            return parseForLoop();
        } else if (currentToken.type.equals("FUNC")) {
            return parseFuncDeclaration();
        } else {
            throw new RuntimeException("Unexpected statement: " + currentToken);
        }
    }

    public List<AST> parse() {
        List<AST> statements = new ArrayList<>();
        while (currentToken != null) {
            statements.add(parseStatement());
        }
        return statements;
    }
}