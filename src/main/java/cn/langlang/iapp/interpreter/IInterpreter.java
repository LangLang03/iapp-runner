package cn.langlang.iapp.interpreter;

import cn.langlang.iapp.ast.Program;
import cn.langlang.iapp.runtime.RuntimeContext;

public interface IInterpreter {
    Object execute(Program program, RuntimeContext context) throws InterpreterException;
    
    Object executeStatement(cn.langlang.iapp.ast.Statement statement, RuntimeContext context) throws InterpreterException;
    
    Object evaluateExpression(cn.langlang.iapp.ast.Expression expression, RuntimeContext context) throws InterpreterException;
}
