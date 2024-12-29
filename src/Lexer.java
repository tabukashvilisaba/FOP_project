import java.util.ArrayList;
import java.util.List;

class Token {
    public String type;
    public String value;

    public Token(String type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("Token(%s, %s)", type, value);
    }
}

class Lexer {
    private String input;
    private int pos = 0;
    private char currentChar;

    public Lexer(String input) {
        this.input = input;
        if (!input.isEmpty()) {
            this.currentChar = input.charAt(pos);
        } else {
            this.currentChar = '\0'; // Indicate end of input
        }
    }

    private void advance() {
        pos++;
        if (pos < input.length()) {
            currentChar = input.charAt(pos);
        } else {
            currentChar = '\0';
        }
    }

    private void skipWhitespace() {
        while (currentChar != '\0' && Character.isWhitespace(currentChar)) {
            advance();
        }
    }

    private Token identifier() {
        StringBuilder result = new StringBuilder();
        while (currentChar != '\0' && (Character.isLetterOrDigit(currentChar) || currentChar == '_')) {
            result.append(currentChar);
            advance();
        }
        String value = result.toString();
        switch (value) {
            case "var":
            case "if":
            case "for":
            case "func":
            case "fmt":
            case "strconv":
                return new Token(value.toUpperCase(), value);
            default:
                return new Token("IDENTIFIER", value);
        }
    }

    private Token number() {
        StringBuilder result = new StringBuilder();
        while (currentChar != '\0' && Character.isDigit(currentChar)) {
            result.append(currentChar);
            advance();
        }
        return new Token("NUMBER", result.toString());
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (currentChar != '\0') {
            if (Character.isWhitespace(currentChar)) {
                skipWhitespace();
            } else if (Character.isLetter(currentChar)) {
                tokens.add(identifier());
            } else if (Character.isDigit(currentChar)) {
                tokens.add(number());
            } else if (currentChar == '+') {
                tokens.add(new Token("PLUS", "+"));
                advance();
            } else if (currentChar == '-') {
                tokens.add(new Token("MINUS", "-"));
                advance();
            } else if (currentChar == '*') {
                tokens.add(new Token("MULTIPLY", "*"));
                advance();
            } else if (currentChar == '/') {
                tokens.add(new Token("DIVIDE", "/"));
                advance();
            } else if (currentChar == '=') {
                tokens.add(new Token("ASSIGN", "="));
                advance();
            } else if (currentChar == '<') {
                tokens.add(new Token("LT", "<"));
                advance();
            } else if (currentChar == '>') {
                tokens.add(new Token("GT", ">"));
                advance();
            } else if (currentChar == '(') {
                tokens.add(new Token("LPAREN", "("));
                advance();
            } else if (currentChar == ')') {
                tokens.add(new Token("RPAREN", ")"));
                advance();
            } else if (currentChar == '{') {
                tokens.add(new Token("LBRACE", "{"));
                advance();
            } else if (currentChar == '}') {
                tokens.add(new Token("RBRACE", "}"));
                advance();
            } else if (currentChar == ',') {
                tokens.add(new Token("COMMA", ","));
                advance();
            } else if (currentChar == ';') {
                tokens.add(new Token("SEMICOLON", ";"));
                advance();
            } else if (currentChar == '&') {
                advance();
                if (currentChar == '&') {
                    tokens.add(new Token("AND", "&&"));
                    advance();
                } else {
                    tokens.add(new Token("AMPERSAND", "&"));
                }
            } else {
                throw new RuntimeException("Unknown character: " + currentChar);
            }
        }
        return tokens;
    }
}