package cn.langlang.iapp.parser;

import cn.langlang.iapp.ast.Program;
import cn.langlang.iapp.lexer.Token;

import java.util.List;

public interface IParser {
    Program parse(List<Token> tokens) throws ParserException;
}
