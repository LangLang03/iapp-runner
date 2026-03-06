package cn.langlang.iapp.lexer;

import java.util.*;

public class Lexer implements ILexer {
    private String source;
    private char[] chars;
    private int position;
    private int line;
    private int column;
    private int length;
    
    private static final Map<String, TokenType> KEYWORDS;
    
    static {
        KEYWORDS = new HashMap<>(16);
        KEYWORDS.put("s", TokenType.KEYWORD_S);
        KEYWORDS.put("ss", TokenType.KEYWORD_SS);
        KEYWORDS.put("sss", TokenType.KEYWORD_SSS);
        KEYWORDS.put("f", TokenType.KEYWORD_IF);
        KEYWORDS.put("else", TokenType.KEYWORD_ELSE);
        KEYWORDS.put("w", TokenType.KEYWORD_WHILE);
        KEYWORDS.put("for", TokenType.KEYWORD_FOR);
        KEYWORDS.put("break", TokenType.KEYWORD_BREAK);
        KEYWORDS.put("endcode", TokenType.KEYWORD_ENDCODE);
        KEYWORDS.put("fn", TokenType.KEYWORD_FN);
        KEYWORDS.put("end", TokenType.KEYWORD_END);
        KEYWORDS.put("true", TokenType.KEYWORD_TRUE);
        KEYWORDS.put("false", TokenType.KEYWORD_FALSE);
        KEYWORDS.put("null", TokenType.KEYWORD_NULL);
        KEYWORDS.put("t", TokenType.KEYWORD_T);
    }
    
    public Lexer(String source) {
        reset(source);
    }
    
    public void reset(String source) {
        this.source = source;
        this.chars = source != null ? source.toCharArray() : new char[0];
        this.position = 0;
        this.line = 1;
        this.column = 1;
        this.length = chars.length;
    }
    
    @Override
    public List<Token> tokenize(String source) throws LexerException {
        reset(source);
        return tokenizeInternal();
    }
    
    public List<Token> tokenizeInternal() throws LexerException {
        List<Token> tokens = new ArrayList<>(64);
        
        while (!isAtEnd()) {
            skipWhitespace();
            if (isAtEnd()) break;
            
            Token token = scanToken();
            if (token != null) {
                tokens.add(token);
            }
        }
        
        tokens.add(new Token(TokenType.EOF, "", line, column));
        return tokens;
    }
    
    private Token scanToken() throws LexerException {
        skipWhitespace();
        if (isAtEnd()) return null;
        
        int startLine = line;
        int startColumn = column;
        char c = advance();
        
        switch (c) {
            case '(':
                return new Token(TokenType.LPAREN, "(", startLine, startColumn);
            case ')':
                return new Token(TokenType.RPAREN, ")", startLine, startColumn);
            case '{':
                return new Token(TokenType.LBRACE, "{", startLine, startColumn);
            case '}':
                return new Token(TokenType.RBRACE, "}", startLine, startColumn);
            case '[':
                return new Token(TokenType.LBRACKET, "[", startLine, startColumn);
            case ']':
                return new Token(TokenType.RBRACKET, "]", startLine, startColumn);
            case ',':
                return new Token(TokenType.COMMA, ",", startLine, startColumn);
            case ';':
                return new Token(TokenType.SEMICOLON, ";", startLine, startColumn);
            case ':':
                return new Token(TokenType.COLON, ":", startLine, startColumn);
            case '.':
                return new Token(TokenType.DOT, ".", startLine, startColumn);
            case '+':
                if (match('=')) {
                    return new Token(TokenType.PLUS_EQUALS, "+=", startLine, startColumn);
                }
                if (match('+')) {
                    return new Token(TokenType.PLUS_PLUS, "++", startLine, startColumn);
                }
                return new Token(TokenType.PLUS, "+", startLine, startColumn);
            case '-':
                if (match('=')) {
                    return new Token(TokenType.MINUS_EQUALS, "-=", startLine, startColumn);
                }
                if (match('-')) {
                    return new Token(TokenType.MINUS_MINUS, "--", startLine, startColumn);
                }
                return new Token(TokenType.MINUS, "-", startLine, startColumn);
            case '*':
                if (match('=')) {
                    return new Token(TokenType.STAR_EQUALS, "*=", startLine, startColumn);
                }
                if (match('?')) {
                    return new Token(TokenType.ENDS_WITH, "*?", startLine, startColumn);
                }
                return new Token(TokenType.STAR, "*", startLine, startColumn);
            case '/':
                if (match('=')) {
                    return new Token(TokenType.SLASH_EQUALS, "/=", startLine, startColumn);
                }
                if (match('/')) {
                    skipLineComment();
                    return null;
                }
                if (match('.')) {
                    skipBlockComment();
                    return null;
                }
                return new Token(TokenType.SLASH, "/", startLine, startColumn);
            case '%':
                return new Token(TokenType.PERCENT, "%", startLine, startColumn);
            case '=':
                if (match('=')) {
                    return new Token(TokenType.EQ, "==", startLine, startColumn);
                }
                return new Token(TokenType.EQUALS, "=", startLine, startColumn);
            case '!':
                if (match('=')) {
                    return new Token(TokenType.NE, "!=", startLine, startColumn);
                }
                return new Token(TokenType.NOT, "!", startLine, startColumn);
            case '<':
                if (match('=')) {
                    return new Token(TokenType.LE, "<=", startLine, startColumn);
                }
                return new Token(TokenType.LT, "<", startLine, startColumn);
            case '>':
                if (match('=')) {
                    return new Token(TokenType.GE, ">=", startLine, startColumn);
                }
                return new Token(TokenType.GT, ">", startLine, startColumn);
            case '&':
                if (match('&')) {
                    return new Token(TokenType.AND, "&&", startLine, startColumn);
                }
                return new Token(TokenType.UNKNOWN, "&", startLine, startColumn);
            case '|':
                if (match('|')) {
                    return new Token(TokenType.OR, "||", startLine, startColumn);
                }
                return new Token(TokenType.UNKNOWN, "|", startLine, startColumn);
            case '?':
                if (match('*')) {
                    return new Token(TokenType.STARTS_WITH, "?*", startLine, startColumn);
                }
                return new Token(TokenType.CONTAINS, "?", startLine, startColumn);
            case '"':
                return scanString(startLine, startColumn);
            case '\n':
            case '\r':
                return new Token(TokenType.NEWLINE, "\n", startLine, startColumn);
            default:
                if (isDigit(c)) {
                    return scanNumber(startLine, startColumn);
                }
                if (isAlpha(c)) {
                    return scanIdentifier(startLine, startColumn);
                }
                return new Token(TokenType.UNKNOWN, String.valueOf(c), startLine, startColumn);
        }
    }
    
    private Token scanString(int startLine, int startColumn) throws LexerException {
        int startPos = position;
        int capacity = 32;
        char[] buffer = new char[capacity];
        int bufferPos = 0;
        
        while (!isAtEnd() && peek() != '"') {
            char c = advance();
            if (c == '\\') {
                if (isAtEnd()) {
                    throw new LexerException("字符串未闭合", startLine, startColumn);
                }
                char escaped = advance();
                char result;
                switch (escaped) {
                    case 'n': result = '\n'; break;
                    case 't': result = '\t'; break;
                    case 'r': result = '\r'; break;
                    case '\\': result = '\\'; break;
                    case '"': result = '"'; break;
                    case '\'': result = '\''; break;
                    default:
                        if (bufferPos + 2 >= capacity) {
                            capacity = capacity * 2;
                            char[] newBuffer = new char[capacity];
                            System.arraycopy(buffer, 0, newBuffer, 0, bufferPos);
                            buffer = newBuffer;
                        }
                        buffer[bufferPos++] = '\\';
                        buffer[bufferPos++] = escaped;
                        continue;
                }
                if (bufferPos >= capacity) {
                    capacity = capacity * 2;
                    char[] newBuffer = new char[capacity];
                    System.arraycopy(buffer, 0, newBuffer, 0, bufferPos);
                    buffer = newBuffer;
                }
                buffer[bufferPos++] = result;
            } else if (c == '\n') {
                line++;
                column = 1;
                if (bufferPos >= capacity) {
                    capacity = capacity * 2;
                    char[] newBuffer = new char[capacity];
                    System.arraycopy(buffer, 0, newBuffer, 0, bufferPos);
                    buffer = newBuffer;
                }
                buffer[bufferPos++] = c;
            } else {
                if (bufferPos >= capacity) {
                    capacity = capacity * 2;
                    char[] newBuffer = new char[capacity];
                    System.arraycopy(buffer, 0, newBuffer, 0, bufferPos);
                    buffer = newBuffer;
                }
                buffer[bufferPos++] = c;
            }
        }
        
        if (isAtEnd()) {
            throw new LexerException("字符串未闭合", startLine, startColumn);
        }
        
        advance();
        return new Token(TokenType.STRING, new String(buffer, 0, bufferPos), startLine, startColumn);
    }
    
    private Token scanNumber(int startLine, int startColumn) {
        int startPos = position - 1;
        
        while (!isAtEnd() && isDigit(peek())) {
            advance();
        }
        
        if (!isAtEnd() && peek() == '.' && isDigit(peekNext())) {
            advance();
            while (!isAtEnd() && isDigit(peek())) {
                advance();
            }
        }
        
        return new Token(TokenType.NUMBER, new String(chars, startPos, position - startPos), startLine, startColumn);
    }
    
    private Token scanIdentifier(int startLine, int startColumn) {
        int startPos = position - 1;
        
        while (!isAtEnd() && isAlphaNumeric(peek())) {
            advance();
        }
        
        String identifier = new String(chars, startPos, position - startPos);
        TokenType type = KEYWORDS.get(identifier);
        
        if (type == null) {
            type = TokenType.IDENTIFIER;
        }
        
        return new Token(type, identifier, startLine, startColumn);
    }
    
    private void skipWhitespace() {
        while (!isAtEnd()) {
            char c = peek();
            if (c == ' ' || c == '\t') {
                advance();
            } else {
                break;
            }
        }
    }
    
    private void skipLineComment() {
        while (!isAtEnd() && peek() != '\n') {
            advance();
        }
    }
    
    private void skipBlockComment() {
        while (!isAtEnd()) {
            if (peek() == '.' && peekNext() == '/') {
                advance();
                advance();
                break;
            }
            if (peek() == '\n') {
                line++;
                column = 1;
            }
            advance();
        }
    }
    
    private boolean isAtEnd() {
        return position >= length;
    }
    
    private char advance() {
        char c = chars[position++];
        column++;
        if (c == '\n') {
            line++;
            column = 1;
        }
        return c;
    }
    
    private char peek() {
        if (isAtEnd()) return '\0';
        return chars[position];
    }
    
    private char peekNext() {
        if (position + 1 >= length) return '\0';
        return chars[position + 1];
    }
    
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (chars[position] != expected) return false;
        position++;
        column++;
        return true;
    }
    
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
    
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }
    
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
}
