package cn.langlang.iapp.interpreter;

import cn.langlang.iapp.ast.*;
import cn.langlang.iapp.ast.Expression;
import cn.langlang.iapp.ast.Statement;
import cn.langlang.iapp.lexer.TokenType;
import cn.langlang.iapp.runtime.FunctionRegistry;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Interpreter implements IInterpreter {
    private static final Logger logger = LoggerFactory.getLogger(Interpreter.class);
    private final RuntimeContext context;
    
    public Interpreter(RuntimeContext context) {
        this.context = context;
        registerBuiltinFunctions();
    }
    
    private void registerBuiltinFunctions() {
        FunctionRegistry registry = context.getFunctionRegistry();
        
        registry.registerFunction(new cn.langlang.iapp.functions.output.SysoFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.output.TwFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.string.SsFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.string.SrFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.string.SjFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.string.SlFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.string.SsgFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.string.SlgFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.string.StrimFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.string.SlowerFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.string.SupperFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.string.SiofFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.string.SlofFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.string.StobmFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.string.Sutf8toFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.math.SAddFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.math.SSubFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.math.SMulFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.math.SDivFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.math.SModFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.math.SFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.math.S2Function());
        registry.registerFunction(new cn.langlang.iapp.functions.math.SnFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.math.SranFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.array.NszFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.array.SgszFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.array.SsszFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.array.SgszlFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.file.FdFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.file.FeFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.file.FsFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.file.FrFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.file.FwFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.file.FcFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.file.FlFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.file.FtFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.file.FdirFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.file.FuzFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.file.FuzsFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.file.FjFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.file.FoFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.file.FiFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.net.HsFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.net.HdFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.net.HdflFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.net.HufFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.net.HwFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.net.HwsFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.time.TimeFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.other.StopFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.java.JavaFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.java.JavaxFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.java.JavanewFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.java.JavagsFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.java.JavassFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.java.ClsFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.other.CallFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.list.AslistFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.list.SslistFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.list.GslistFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.list.GslistlFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.list.DslistFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.list.GslistszFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.list.GslistisFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.list.GslistiofFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.list.GslistlofFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.clipboard.SxbFunction());
        registry.registerFunction(new cn.langlang.iapp.functions.clipboard.ShbFunction());
    }
    
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
                context.declareVariable(stmt.getVariableName());
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
                            context.declareVariable(varName);
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
                            context.declareVariable(varName);
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
                            context.declareVariable(stmt.getVariableName());

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
                            context.declareVariable(stmt.getVariableName());

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
                    
                    String actualOutputVariable = stmt.getResultVariable();
                    
                    if (stmt.hasPotentialOutputVariable()) {
                        String potentialVar = stmt.getPotentialOutputVariable();
                        if (!context.isVariableDeclared(potentialVar)) {
                            actualOutputVariable = potentialVar;
                            logger.debug("潜在输出变量 {} 未声明，作为输出变量处理", potentialVar);
                        } else {
                            args.add(context.getVariable(potentialVar));
                            logger.debug("潜在输出变量 {} 已声明，作为参数处理", potentialVar);
                        }
                    }

                    for (Expression arg : stmt.getArguments()) {
                        args.add(evaluateExpression(arg, context));
                    }

                    Object result;
                    
                    if (context.hasUserFunction(functionName)) {
                        FunctionDefinitionStatement funcDef = context.getUserFunction(functionName);
                        result = executeUserFunction(funcDef, args, context);
                    } else {
                        IFunction function = context.getFunctionRegistry().getFunction(functionName);
                        if (function != null) {
                            result = function.call(context, args);
                        } else {
                            result = context.executeMjavaMethod("", functionName, args.toArray());
                        }
                    }

                    if (actualOutputVariable != null && !actualOutputVariable.isEmpty()) {
                        context.setVariable(actualOutputVariable, result, stmt.getResultScope());
                        context.declareVariable(actualOutputVariable);
                    }

                    return result;
                } catch (Exception e) {
                    throw new InterpreterException("Function call error: " + e.getMessage());
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
                logger.debug("注册用户函数: {}", fullName);
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
                            return ((Number) left).doubleValue() + ((Number) right).doubleValue();
                        }
                        return left + String.valueOf(right);
                    case MINUS:
                        return ((Number) left).doubleValue() - ((Number) right).doubleValue();
                    case STAR:
                        return ((Number) left).doubleValue() * ((Number) right).doubleValue();
                    case SLASH:
                        return ((Number) left).doubleValue() / ((Number) right).doubleValue();
                    case PERCENT:
                        return ((Number) left).doubleValue() % ((Number) right).doubleValue();
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
                    case MINUS -> -((Number) operand).doubleValue();
                    default -> operand;
                };
            }

            @Override
            public Object visitFunctionCall(FunctionCallExpression expr) throws InterpreterException {
                try {
                    String functionName = expr.getFunctionName();
                    List<Object> args = new ArrayList<>();

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
                    throw new InterpreterException("Function call error: " + e.getMessage());
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
        return switch (value) {
            case null -> false;
            case Boolean b -> b;
            case Number number -> number.doubleValue() != 0;
            case String s -> !s.isEmpty();
            default -> true;
        };
    }
    
    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
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
    
    private Object executeUserFunction(FunctionDefinitionStatement funcDef, List<Object> args, RuntimeContext context) throws InterpreterException {
        context.getVariableManager().pushScope();
        
        try {
            List<String> parameters = funcDef.getParameters();
            for (int i = 0; i < parameters.size(); i++) {
                String paramName = parameters.get(i);
                Object argValue = i < args.size() ? args.get(i) : null;
                context.setVariable(paramName, argValue, TokenType.KEYWORD_S);
                context.declareVariable(paramName);
            }
            
            Object result = null;
            for (Statement stmt : funcDef.getBody()) {
                if (context.isEndCodeRequested()) break;
                result = executeStatement(stmt, context);
            }
            
            return result;
        } finally {
            context.getVariableManager().popScope();
        }
    }
}
