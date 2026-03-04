package cn.langlang.iapp.lexer;

public enum TokenType {
    KEYWORD_S,
    KEYWORD_SS,
    KEYWORD_SSS,
    KEYWORD_IF,
    KEYWORD_ELSE,
    KEYWORD_WHILE,
    KEYWORD_FOR,
    KEYWORD_BREAK,
    KEYWORD_ENDCODE,
    KEYWORD_FN,
    KEYWORD_END,
    KEYWORD_TRUE,
    KEYWORD_FALSE,
    KEYWORD_NULL,
    KEYWORD_T,
    
    IDENTIFIER,
    NUMBER,
    STRING,
    
    PLUS,
    MINUS,
    STAR,
    SLASH,
    PERCENT,
    
    PLUS_PLUS,
    MINUS_MINUS,
    
    EQUALS,
    PLUS_EQUALS,
    MINUS_EQUALS,
    STAR_EQUALS,
    SLASH_EQUALS,
    
    EQ,
    NE,
    LT,
    GT,
    LE,
    GE,
    
    AND,
    OR,
    NOT,
    
    STARTS_WITH,
    ENDS_WITH,
    CONTAINS,
    
    LPAREN,
    RPAREN,
    LBRACE,
    RBRACE,
    LBRACKET,
    RBRACKET,
    
    COMMA,
    DOT,
    SEMICOLON,
    COLON,
    
    NEWLINE,
    EOF,
    UNKNOWN
}
