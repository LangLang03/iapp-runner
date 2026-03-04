package cn.langlang.iapp.lexer;

public interface ILexer {
    java.util.List<Token> tokenize(String source) throws LexerException;
}
