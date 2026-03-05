package cn.langlang.iapp.lexer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Lexer implements ILexer {
    private static final Logger logger = LoggerFactory.getLogger(Lexer.class);
    private final String source;
    private int position;
    private int line;
    private int column;
    private final int length;
    
    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();
    
    static {
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
        this.source = source;
        this.position = 0;
        this.line = 1;
        this.column = 1;
        this.length = source.length();
    }
    
    @Override
    public List<Token> tokenize(String source) throws LexerException {
        Lexer lexer = new Lexer(source);
        return lexer.tokenizeInternal();
    }
    
    public List<Token> tokenizeInternal() throws LexerException {
        List<Token> tokens = new ArrayList<>();
        logger.debug("开始词法分析, 源码长度: {}", length);
        
        while (!isAtEnd()) {
            skipWhitespace();
            if (isAtEnd()) break;
            
            Token token = scanToken();
            if (token != null) {
                tokens.add(token);
                logger.trace("Token: {} '{}' at line:{}", token.getType(), token.getValue(), token.getLine());
            }
        }
        
        tokens.add(new Token(TokenType.EOF, "", line, column));
        logger.debug("词法分析完成, 共生成 {} 个tokens", tokens.size());
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
        StringBuilder value = new StringBuilder();
        
        while (!isAtEnd() && peek() != '"') {
            char c = advance();
            if (c == '\\') {
                if (isAtEnd()) {
                    throw new LexerException("字符串未闭合", startLine, startColumn);
                }
                char escaped = advance();
                switch (escaped) {
                    case 'n': value.append('\n'); break;
                    case 't': value.append('\t'); break;
                    case 'r': value.append('\r'); break;
                    case '\\': value.append('\\'); break;
                    case '"': value.append('"'); break;
                    case '\'': value.append('\''); break;
                    default: value.append('\\').append(escaped);
                }
            } else if (c == '\n') {
                line++;
                column = 1;
                value.append(c);
            } else {
                value.append(c);
            }
        }
        
        if (isAtEnd()) {
            throw new LexerException("字符串未闭合", startLine, startColumn);
        }
        
        advance();
        return new Token(TokenType.STRING, value.toString(), startLine, startColumn);
    }
    
    private Token scanNumber(int startLine, int startColumn) {
        StringBuilder value = new StringBuilder();
        value.append(source.charAt(position - 1));
        
        while (!isAtEnd() && isDigit(peek())) {
            value.append(advance());
        }
        
        if (!isAtEnd() && peek() == '.' && isDigit(peekNext())) {
            do {
                value.append(advance());
            } while (!isAtEnd() && isDigit(peek()));
        }
        
        return new Token(TokenType.NUMBER, value.toString(), startLine, startColumn);
    }
    
    private Token scanIdentifier(int startLine, int startColumn) {
        StringBuilder value = new StringBuilder();
        value.append(source.charAt(position - 1));
        
        while (!isAtEnd() && isAlphaNumeric(peek())) {
            value.append(advance());
        }
        
        String identifier = value.toString();
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
        char c = source.charAt(position++);
        column++;
        if (c == '\n') {
            line++;
            column = 1;
        }
        return c;
    }
    
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(position);
    }
    
    private char peekNext() {
        if (position + 1 >= length) return '\0';
        return source.charAt(position + 1);
    }
    
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(position) != expected) return false;
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
