package cn.langlang.iapp.interpreter;

import cn.langlang.iapp.ast.*;
import cn.langlang.iapp.lexer.TokenType;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.ArrayList;
import java.util.List;

public class Interpreter implements IInterpreter {

    @Override
    public Object execute(Program program, RuntimeContext context) throws InterpreterException {
        Object result = null;
        for (Statement statement : program.getStatements()) {
            if (context.isEndCodeRequested()) {
                break;
            }
            result = executeStatement(statement, context);
        }
        return result;
    }
    
    @Override
    public Object executeStatement(Statement statement, RuntimeContext context) throws InterpreterException {
        return statement.accept(new Statement.StatementVisitor<>() {
            @Override
            public Object visitVariableDeclaration(VariableDeclarationStatement stmt) throws InterpreterException {
                Object value = null;
                if (stmt.getInitialValue() != null) {
                    value = evaluateExpression(stmt.getInitialValue(), context);
                }
                context.setVariable(stmt.getVariableName(), value, stmt.getScope());
                return null;
            }

            @Override
            public Object visitAssignment(AssignmentStatement stmt) throws InterpreterException {
                Object value = evaluateExpression(stmt.getValue(), context);
                context.setVariable(stmt.getVariableName(), value, stmt.getScope());
                return null;
            }

            @Override
            public Object visitIf(IfStatement stmt) throws InterpreterException {
                Object conditionValue = evaluateExpression(stmt.getCondition(), context);
                if (isTruthy(conditionValue)) {
                    for (Statement s : stmt.getThenStatements()) {
                        executeStatement(s, context);
                    }
                } else {
                    boolean executed = false;
                    for (IfStatement.ElseIfClause clause : stmt.getElseIfClauses()) {
                        Object elseIfCondition = evaluateExpression(clause.condition(), context);
                        if (isTruthy(elseIfCondition)) {
                            for (Statement s : clause.statements()) {
                                executeStatement(s, context);
                            }
                            executed = true;
                            break;
                        }
                    }
                    if (!executed && !stmt.getElseStatements().isEmpty()) {
                        for (Statement s : stmt.getElseStatements()) {
                            executeStatement(s, context);
                        }
                    }
                }
                return null;
            }

            @Override
            public Object visitWhile(WhileStatement stmt) throws InterpreterException {
                context.pushBreakContext(new RuntimeContext.BreakContext("while"));
                while (isTruthy(evaluateExpression(stmt.getCondition(), context))) {
                    if (context.isEndCodeRequested()) break;

                    RuntimeContext.BreakContext breakCtx = context.getCurrentBreakContext();
                    if (breakCtx != null && breakCtx.shouldBreak()) {
                        breakCtx.setShouldBreak(false);
                        break;
                    }

                    for (Statement s : stmt.getBody()) {
                        executeStatement(s, context);
                    }
                }
                context.popBreakContext();
                return null;
            }

            @Override
            public Object visitFor(ForStatement stmt) throws InterpreterException {
                context.pushBreakContext(new RuntimeContext.BreakContext("for"));

                if (stmt.getForType() == ForStatement.ForType.C_STYLE) {
                    if (stmt.getInitStatement() != null) {
                        executeStatement(stmt.getInitStatement(), context);
                    }
                    
                    while (stmt.getCondition() == null || isTruthy(evaluateExpression(stmt.getCondition(), context))) {
                        if (context.isEndCodeRequested()) break;

                        RuntimeContext.BreakContext breakCtx = context.getCurrentBreakContext();
                        if (breakCtx != null && breakCtx.shouldBreak()) {
                            breakCtx.setShouldBreak(false);
                            break;
                        }

                        for (Statement s : stmt.getBody()) {
                            executeStatement(s, context);
                        }

                        if (stmt.getUpdateStatement() != null) {
                            executeStatement(stmt.getUpdateStatement(), context);
                        }
                    }
                } else if (stmt.getForType() == ForStatement.ForType.RANGE) {
                    Object startValue = evaluateExpression(stmt.getStart(), context);
                    Object endValue = evaluateExpression(stmt.getEnd(), context);

                    if (endValue instanceof Object[] array) {
                        String varName = null;
                        if (stmt.getStart() instanceof VariableExpression varExpr) {
                            varName = varExpr.getName();
                        }
                        if (varName != null) {
                            for (Object item : array) {
                                if (context.isEndCodeRequested()) break;

                                RuntimeContext.BreakContext breakCtx = context.getCurrentBreakContext();
                                if (breakCtx != null && breakCtx.shouldBreak()) {
                                    breakCtx.setShouldBreak(false);
                                    break;
                                }

                                context.setVariable(varName, item, TokenType.KEYWORD_S);

                                for (Statement s : stmt.getBody()) {
                                    executeStatement(s, context);
                                }
                            }
                        }
                    } else if (endValue instanceof List<?> list) {
                        String varName = null;
                        if (stmt.getStart() instanceof VariableExpression varExpr) {
                            varName = varExpr.getName();
                        }
                        if (varName != null) {
                            for (Object item : list) {
                                if (context.isEndCodeRequested()) break;

                                RuntimeContext.BreakContext breakCtx = context.getCurrentBreakContext();
                                if (breakCtx != null && breakCtx.shouldBreak()) {
                                    breakCtx.setShouldBreak(false);
                                    break;
                                }

                                context.setVariable(varName, item, TokenType.KEYWORD_S);

                                for (Statement s : stmt.getBody()) {
                                    executeStatement(s, context);
                                }
                            }
                        }
                    } else {
                        long start = toLong(startValue);
                        long end = toLong(endValue);
                        long step = 1;

                        if (stmt.getStep() != null) {
                            Object stepValue = evaluateExpression(stmt.getStep(), context);
                            step = toLong(stepValue);
                        }

                        for (long i = start; i <= end; i += step) {
                            if (context.isEndCodeRequested()) break;

                            RuntimeContext.BreakContext breakCtx = context.getCurrentBreakContext();
                            if (breakCtx != null && breakCtx.shouldBreak()) {
                                breakCtx.setShouldBreak(false);
                                break;
                            }

                            for (Statement s : stmt.getBody()) {
                                executeStatement(s, context);
                            }
                        }
                    }
                } else if (stmt.getForType() == ForStatement.ForType.ARRAY_ITERATION) {
                    Object arrayValue = evaluateExpression(stmt.getEnd(), context);
                    if (arrayValue instanceof Object[] array) {
                        for (Object item : array) {
                            if (context.isEndCodeRequested()) break;

                            RuntimeContext.BreakContext breakCtx = context.getCurrentBreakContext();
                            if (breakCtx != null && breakCtx.shouldBreak()) {
                                breakCtx.setShouldBreak(false);
                                break;
                            }

                            context.setVariable(stmt.getVariableName(), item, TokenType.KEYWORD_S);

                            for (Statement s : stmt.getBody()) {
                                executeStatement(s, context);
                            }
                        }
                    } else if (arrayValue instanceof List<?> list) {
                        for (Object item : list) {
                            if (context.isEndCodeRequested()) break;

                            RuntimeContext.BreakContext breakCtx = context.getCurrentBreakContext();
                            if (breakCtx != null && breakCtx.shouldBreak()) {
                                breakCtx.setShouldBreak(false);
                                break;
                            }

                            context.setVariable(stmt.getVariableName(), item, TokenType.KEYWORD_S);

                            for (Statement s : stmt.getBody()) {
                                executeStatement(s, context);
                            }
                        }
                    }
                }

                context.popBreakContext();
                return null;
            }

            @Override
            public Object visitFunctionCall(FunctionCallStatement stmt) throws InterpreterException {
                try {
                    String functionName = stmt.getFunctionName();
                    List<Object> args = new ArrayList<>();

                    for (Expression arg : stmt.getArguments()) {
                        args.add(evaluateExpression(arg, context));
                    }

                    Object result;
                    
                    if (context.hasUserFunction(functionName)) {
                        FunctionDefinitionStatement funcDef = context.getUserFunction(functionName);
                        result = executeUserFunction(funcDef, args, context, stmt.getOutputVariables());
                    } else {
                        IFunction function = context.getFunctionRegistry().getFunction(functionName);
                        if (function != null) {
                            result = function.call(context, args);
                        } else {
                            result = context.executeMjavaMethod("", functionName, args.toArray());
                        }
                    }

                    if (stmt.hasOutputVariables()) {
                        List<String> outputVars = stmt.getOutputVariables();
                        for (String varName : outputVars) {
                            context.setVariable(varName, result, stmt.getResultScope());
                        }
                    }

                    return result;
                } catch (Exception e) {
                    throw new InterpreterException("函数调用错误: " + e.getMessage());
                }
            }

            @Override
            public Object visitBreak(BreakStatement stmt) {
                RuntimeContext.BreakContext breakCtx = context.getCurrentBreakContext();
                if (breakCtx != null) {
                    breakCtx.setShouldBreak(true);
                }
                return null;
            }

            @Override
            public Object visitEndCode(EndCodeStatement stmt) {
                context.requestEndCode();
                return null;
            }

            @Override
            public Object visitFunctionDefinition(FunctionDefinitionStatement stmt) {
                String fullName = stmt.getFullName();
                context.registerUserFunction(fullName, stmt);
                return null;
            }

            @Override
            public Object visitThread(ThreadStatement stmt) {
                new Thread(() -> {
                    try {
                        for (Statement s : stmt.getBody()) {
                            executeStatement(s, context);
                        }
                    } catch (InterpreterException e) {
                        e.printStackTrace();
                    }
                }).start();
                return null;
            }

            @Override
            public Object visitBlock(BlockStatement stmt) throws InterpreterException {
                for (Statement s : stmt.getStatements()) {
                    executeStatement(s, context);
                }
                return null;
            }
        });
    }
    
    @Override
    public Object evaluateExpression(Expression expression, RuntimeContext context) throws InterpreterException {
        return expression.accept(new Expression.ExpressionVisitor<>() {
            @Override
            public Object visitNumberLiteral(NumberLiteralExpression expr) {
                return expr.getValue();
            }

            @Override
            public Object visitStringLiteral(StringLiteralExpression expr) {
                return expr.getValue();
            }

            @Override
            public Object visitBooleanLiteral(BooleanLiteralExpression expr) {
                return expr.getValue();
            }

            @Override
            public Object visitNullLiteral(NullLiteralExpression expr) {
                return null;
            }

            @Override
            public Object visitVariable(VariableExpression expr) {
                return context.getVariable(expr.getName());
            }

            @Override
            public Object visitBinary(BinaryExpression expr) throws InterpreterException {
                Object left = evaluateExpression(expr.getLeft(), context);
                Object right = evaluateExpression(expr.getRight(), context);

                switch (expr.getOperator()) {
                    case PLUS:
                        if (left instanceof String || right instanceof String) {
                            return left + String.valueOf(right);
                        }
                        if (left instanceof Number && right instanceof Number) {
                            return addNumbers((Number) left, (Number) right);
                        }
                        return left + String.valueOf(right);
                    case MINUS:
                        return subtractNumbers((Number) left, (Number) right);
                    case STAR:
                        return multiplyNumbers((Number) left, (Number) right);
                    case SLASH:
                        return divideNumbers((Number) left, (Number) right);
                    case PERCENT:
                        return moduloNumbers((Number) left, (Number) right);
                    case EQ:
                        return isEqual(left, right);
                    case NE:
                        return !isEqual(left, right);
                    case LT:
                        return compare(left, right) < 0;
                    case GT:
                        return compare(left, right) > 0;
                    case LE:
                        return compare(left, right) <= 0;
                    case GE:
                        return compare(left, right) >= 0;
                    case AND:
                        return isTruthy(left) && isTruthy(right);
                    case OR:
                        return isTruthy(left) || isTruthy(right);
                    case STARTS_WITH:
                        return String.valueOf(left).startsWith(String.valueOf(right));
                    case ENDS_WITH:
                        return String.valueOf(left).endsWith(String.valueOf(right));
                    case CONTAINS:
                        return String.valueOf(left).contains(String.valueOf(right));
                    default:
                        return null;
                }
            }

            @Override
            public Object visitUnary(UnaryExpression expr) throws InterpreterException {
                Object operand = evaluateExpression(expr.getOperand(), context);

                return switch (expr.getOperator()) {
                    case NOT -> !isTruthy(operand);
                    case MINUS -> negateNumber((Number) operand);
                    default -> operand;
                };
            }

            @Override
            public Object visitFunctionCall(FunctionCallExpression expr) throws InterpreterException {
                String functionName = expr.getFunctionName();
                List<Object> args = new ArrayList<>();
                
                try {
                    for (Expression arg : expr.getArguments()) {
                        args.add(evaluateExpression(arg, context));
                    }

                    IFunction function = context.getFunctionRegistry().getFunction(functionName);
                    if (function != null) {
                        return function.call(context, args);
                    } else {
                        return context.executeMjavaMethod("", functionName, args.toArray());
                    }
                } catch (Exception e) {
                    throw new InterpreterException("函数 '" + functionName + "' 调用错误 (参数数量: " + args.size() + "): " + e.getMessage(), e);
                }
            }

            @Override
            public Object visitArrayAccess(ArrayAccessExpression expr) throws InterpreterException {
                Object array = evaluateExpression(expr.getArray(), context);
                Object index = evaluateExpression(expr.getIndex(), context);

                if (array instanceof Object[]) {
                    int idx = toInt(index);
                    return ((Object[]) array)[idx];
                } else if (array instanceof List) {
                    int idx = toInt(index);
                    return ((List<?>) array).get(idx);
                }
                return null;
            }

            @Override
            public Object visitMemberAccess(MemberAccessExpression expr) throws InterpreterException {
                Object obj = evaluateExpression(expr.getObject(), context);
                String member = expr.getMemberName();

                try {
                    java.lang.reflect.Field field = obj.getClass().getField(member);
                    return field.get(obj);
                } catch (Exception e) {
                    return null;
                }
            }
        });
    }
    
    private boolean isTruthy(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof Number) return ((Number) value).doubleValue() != 0;
        if (value instanceof String) return !((String) value).isEmpty();
        return true;
    }
    
    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        
        if (a instanceof Number && b instanceof Number) {
            return Double.compare(((Number) a).doubleValue(), ((Number) b).doubleValue()) == 0;
        }
        
        if (a instanceof Number && b instanceof String) {
            try {
                double aVal = ((Number) a).doubleValue();
                double bVal = Double.parseDouble((String) b);
                return Double.compare(aVal, bVal) == 0;
            } catch (NumberFormatException e) {
                return a.toString().equals(b);
            }
        }
        
        if (a instanceof String && b instanceof Number) {
            try {
                double aVal = Double.parseDouble((String) a);
                double bVal = ((Number) b).doubleValue();
                return Double.compare(aVal, bVal) == 0;
            } catch (NumberFormatException e) {
                return a.equals(b.toString());
            }
        }
        
        return a.equals(b);
    }
    
    private int compare(Object a, Object b) {
        if (a instanceof Number && b instanceof Number) {
            return Double.compare(((Number) a).doubleValue(), ((Number) b).doubleValue());
        }
        if (a instanceof String && b instanceof String) {
            return ((String) a).compareTo((String) b);
        }
        return 0;
    }
    
    private long toLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }
    
    private int toInt(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }
    
    private boolean isInteger(Number n) {
        if (n instanceof Integer || n instanceof Long) {
            return true;
        }
        if (n instanceof Double || n instanceof Float) {
            double d = n.doubleValue();
            return d == Math.floor(d) && !Double.isInfinite(d);
        }
        return false;
    }
    
    private Number addNumbers(Number a, Number b) {
        if (isInteger(a) && isInteger(b)) {
            return a.longValue() + b.longValue();
        }
        return a.doubleValue() + b.doubleValue();
    }
    
    private Number subtractNumbers(Number a, Number b) {
        if (isInteger(a) && isInteger(b)) {
            return a.longValue() - b.longValue();
        }
        return a.doubleValue() - b.doubleValue();
    }
    
    private Number multiplyNumbers(Number a, Number b) {
        if (isInteger(a) && isInteger(b)) {
            return a.longValue() * b.longValue();
        }
        return a.doubleValue() * b.doubleValue();
    }
    
    private Number divideNumbers(Number a, Number b) {
        if (isInteger(a) && isInteger(b)) {
            long la = a.longValue();
            long lb = b.longValue();
            if (lb != 0 && la % lb == 0) {
                return la / lb;
            }
        }
        return a.doubleValue() / b.doubleValue();
    }
    
    private Number moduloNumbers(Number a, Number b) {
        if (isInteger(a) && isInteger(b)) {
            return a.longValue() % b.longValue();
        }
        return a.doubleValue() % b.doubleValue();
    }
    
    private Number negateNumber(Number n) {
        if (isInteger(n)) {
            return -n.longValue();
        }
        return -n.doubleValue();
    }
    
    private Object executeUserFunction(FunctionDefinitionStatement funcDef, List<Object> args, RuntimeContext context, List<String> outputVariables) throws InterpreterException {
        context.getVariableManager().pushScope();
        
        try {
            List<String> parameters = funcDef.getParameters();
            for (int i = 0; i < parameters.size(); i++) {
                String paramName = parameters.get(i);
                Object argValue = i < args.size() ? args.get(i) : null;
                context.setVariable(paramName, argValue, TokenType.KEYWORD_S);
            }
            
            for (Statement stmt : funcDef.getBody()) {
                if (context.isEndCodeRequested()) break;
                executeStatement(stmt, context);
            }
            
            Object result = null;
            if (outputVariables != null && !outputVariables.isEmpty()) {
                result = context.getVariable(outputVariables.get(0));
            }
            
            return result;
        } finally {
            context.getVariableManager().popScope();
        }
    }
}
