package cn.langlang.iapp.interpreter;

import cn.langlang.iapp.ast.*;
import cn.langlang.iapp.lexer.TokenType;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Interpreter implements IInterpreter {

    private static volatile ExecutorService threadPool;
    private static final AtomicInteger threadCounter = new AtomicInteger(0);
    private static final Object poolLock = new Object();
    
    public Interpreter() {
        ensureThreadPoolInitialized();
    }
    
    private static void ensureThreadPoolInitialized() {
        if (threadPool == null) {
            synchronized (poolLock) {
                if (threadPool == null) {
                    threadPool = Executors.newCachedThreadPool(new ThreadFactory() {
                        @Override
                        public Thread newThread(Runnable r) {
                            Thread t = new Thread(r, "iapp-thread-" + threadCounter.incrementAndGet());
                            t.setDaemon(true);
                            return t;
                        }
                    });
                }
            }
        }
    }
    
    public static void shutdown() {
        synchronized (poolLock) {
            if (threadPool != null && !threadPool.isShutdown()) {
                threadPool.shutdown();
                try {
                    if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                        threadPool.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    threadPool.shutdownNow();
                    Thread.currentThread().interrupt();
                }
                threadPool = null;
            }
        }
    }
    
    public static boolean isShutdown() {
        synchronized (poolLock) {
            return threadPool == null || threadPool.isShutdown();
        }
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
        return statement.accept(new StatementVisitorImpl(context, this));
    }
    
    @Override
    public Object evaluateExpression(Expression expression, RuntimeContext context) throws InterpreterException {
        return expression.accept(new ExpressionVisitorImpl(context, this));
    }
    
    ExecutorService getThreadPool() {
        ensureThreadPoolInitialized();
        return threadPool;
    }
    
    Object executeStatementInternal(Statement statement, RuntimeContext context) throws InterpreterException {
        return statement.accept(new StatementVisitorImpl(context, this));
    }
    
    Object evaluateExpressionInternal(Expression expression, RuntimeContext context) throws InterpreterException {
        return expression.accept(new ExpressionVisitorImpl(context, this));
    }
    
    private static class StatementVisitorImpl implements Statement.StatementVisitor<Object> {
        private final RuntimeContext context;
        private final Interpreter interpreter;
        
        StatementVisitorImpl(RuntimeContext context, Interpreter interpreter) {
            this.context = context;
            this.interpreter = interpreter;
        }
        
        @Override
        public Object visitVariableDeclaration(VariableDeclarationStatement stmt) throws InterpreterException {
            Object value = null;
            if (stmt.getInitialValue() != null) {
                value = interpreter.evaluateExpressionInternal(stmt.getInitialValue(), context);
            }
            context.setVariable(stmt.getVariableName(), value, stmt.getScope());
            return null;
        }

        @Override
        public Object visitAssignment(AssignmentStatement stmt) throws InterpreterException {
            Object value = interpreter.evaluateExpressionInternal(stmt.getValue(), context);
            context.setVariable(stmt.getVariableName(), value, stmt.getScope());
            return null;
        }

        @Override
        public Object visitIf(IfStatement stmt) throws InterpreterException {
            Object conditionValue = interpreter.evaluateExpressionInternal(stmt.getCondition(), context);
            if (isTruthy(conditionValue)) {
                for (Statement s : stmt.getThenStatements()) {
                    interpreter.executeStatementInternal(s, context);
                }
            } else {
                boolean executed = false;
                for (IfStatement.ElseIfClause clause : stmt.getElseIfClauses()) {
                    Object elseIfCondition = interpreter.evaluateExpressionInternal(clause.condition(), context);
                    if (isTruthy(elseIfCondition)) {
                        for (Statement s : clause.statements()) {
                            interpreter.executeStatementInternal(s, context);
                        }
                        executed = true;
                        break;
                    }
                }
                if (!executed && !stmt.getElseStatements().isEmpty()) {
                    for (Statement s : stmt.getElseStatements()) {
                        interpreter.executeStatementInternal(s, context);
                    }
                }
            }
            return null;
        }

        @Override
        public Object visitWhile(WhileStatement stmt) throws InterpreterException {
            context.pushBreakContext(new RuntimeContext.BreakContext("while"));
            try {
                while (isTruthy(interpreter.evaluateExpressionInternal(stmt.getCondition(), context))) {
                    if (context.isEndCodeRequested()) break;

                    RuntimeContext.BreakContext breakCtx = context.getCurrentBreakContext();
                    if (breakCtx != null && breakCtx.shouldBreak()) {
                        breakCtx.setShouldBreak(false);
                        break;
                    }

                    for (Statement s : stmt.getBody()) {
                        interpreter.executeStatementInternal(s, context);
                    }
                }
            } finally {
                context.popBreakContext();
            }
            return null;
        }

        @Override
        public Object visitFor(ForStatement stmt) throws InterpreterException {
            context.pushBreakContext(new RuntimeContext.BreakContext("for"));

            try {
                if (stmt.getForType() == ForStatement.ForType.C_STYLE) {
                    executeCStyleFor(stmt);
                } else if (stmt.getForType() == ForStatement.ForType.RANGE) {
                    executeRangeFor(stmt);
                } else if (stmt.getForType() == ForStatement.ForType.ARRAY_ITERATION) {
                    executeArrayIterationFor(stmt);
                }
            } finally {
                context.popBreakContext();
            }
            return null;
        }
        
        private void executeCStyleFor(ForStatement stmt) throws InterpreterException {
            if (stmt.getInitStatement() != null) {
                interpreter.executeStatementInternal(stmt.getInitStatement(), context);
            }
            
            while (stmt.getCondition() == null || isTruthy(interpreter.evaluateExpressionInternal(stmt.getCondition(), context))) {
                if (context.isEndCodeRequested()) break;

                RuntimeContext.BreakContext breakCtx = context.getCurrentBreakContext();
                if (breakCtx != null && breakCtx.shouldBreak()) {
                    breakCtx.setShouldBreak(false);
                    break;
                }

                for (Statement s : stmt.getBody()) {
                    interpreter.executeStatementInternal(s, context);
                }

                if (stmt.getUpdateStatement() != null) {
                    interpreter.executeStatementInternal(stmt.getUpdateStatement(), context);
                }
            }
        }
        
        private void executeRangeFor(ForStatement stmt) throws InterpreterException {
            Object startValue = interpreter.evaluateExpressionInternal(stmt.getStart(), context);
            Object endValue = interpreter.evaluateExpressionInternal(stmt.getEnd(), context);

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
                            interpreter.executeStatementInternal(s, context);
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
                            interpreter.executeStatementInternal(s, context);
                        }
                    }
                }
            } else {
                long start = toLong(startValue);
                long end = toLong(endValue);
                long step = 1;

                if (stmt.getStep() != null) {
                    Object stepValue = interpreter.evaluateExpressionInternal(stmt.getStep(), context);
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
                        interpreter.executeStatementInternal(s, context);
                    }
                }
            }
        }
        
        private void executeArrayIterationFor(ForStatement stmt) throws InterpreterException {
            Object arrayValue = interpreter.evaluateExpressionInternal(stmt.getEnd(), context);
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
                        interpreter.executeStatementInternal(s, context);
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
                        interpreter.executeStatementInternal(s, context);
                    }
                }
            }
        }

        @Override
        public Object visitFunctionCall(FunctionCallStatement stmt) throws InterpreterException {
            try {
                String functionName = stmt.getFunctionName();
                List<Object> args = new ArrayList<>();

                for (Expression arg : stmt.getArguments()) {
                    args.add(interpreter.evaluateExpressionInternal(arg, context));
                }

                Object result;
                
                if (context.hasUserFunction(functionName)) {
                    FunctionDefinitionStatement funcDef = context.getUserFunction(functionName);
                    result = executeUserFunction(funcDef, args, stmt.getOutputVariables());
                } else {
                    IFunction function = context.getFunction(functionName);
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
            } catch (InterpreterException e) {
                throw e;
            } catch (Exception e) {
                throw new InterpreterException("函数调用错误: " + e.getMessage(), e);
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
            ExecutorService pool = interpreter.getThreadPool();
            if (pool != null && !pool.isShutdown()) {
                final RuntimeContext ctx = context;
                final Interpreter interp = interpreter;
                pool.submit(() -> {
                    try {
                        for (Statement s : stmt.getBody()) {
                            interp.executeStatementInternal(s, ctx);
                        }
                    } catch (InterpreterException e) {
                        System.err.println("Thread execution error: " + e.getMessage());
                    }
                });
            }
            return null;
        }

        @Override
        public Object visitBlock(BlockStatement stmt) throws InterpreterException {
            for (Statement s : stmt.getStatements()) {
                interpreter.executeStatementInternal(s, context);
            }
            return null;
        }
        
        private Object executeUserFunction(FunctionDefinitionStatement funcDef, List<Object> args, List<String> outputVariables) throws InterpreterException {
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
                    interpreter.executeStatementInternal(stmt, context);
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
    
    private static class ExpressionVisitorImpl implements Expression.ExpressionVisitor<Object> {
        private final RuntimeContext context;
        private final Interpreter interpreter;
        
        ExpressionVisitorImpl(RuntimeContext context, Interpreter interpreter) {
            this.context = context;
            this.interpreter = interpreter;
        }
        
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
            TokenType scope = expr.getScope();
            String name = expr.getName();
            
            if (scope == TokenType.KEYWORD_SSS) {
                return context.getVariableManager().getGlobalVariables().get(name);
            } else if (scope == TokenType.KEYWORD_SS) {
                return context.getVariableManager().getInterfaceVariables().get(name);
            } else {
                return context.getVariable(name);
            }
        }

        @Override
        public Object visitBinary(BinaryExpression expr) throws InterpreterException {
            Object left = interpreter.evaluateExpressionInternal(expr.getLeft(), context);
            Object right = interpreter.evaluateExpressionInternal(expr.getRight(), context);

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
                    requireNumbers(left, right, "减法");
                    return subtractNumbers((Number) left, (Number) right);
                case STAR:
                    requireNumbers(left, right, "乘法");
                    return multiplyNumbers((Number) left, (Number) right);
                case SLASH:
                    requireNumbers(left, right, "除法");
                    return divideNumbers((Number) left, (Number) right);
                case PERCENT:
                    requireNumbers(left, right, "取模");
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

        private void requireNumbers(Object left, Object right, String operation) throws InterpreterException {
            if (!(left instanceof Number)) {
                throw new InterpreterException(operation + "运算错误: 左操作数不是数字类型 (" + 
                    (left == null ? "null" : left.getClass().getSimpleName()) + ")");
            }
            if (!(right instanceof Number)) {
                throw new InterpreterException(operation + "运算错误: 右操作数不是数字类型 (" + 
                    (right == null ? "null" : right.getClass().getSimpleName()) + ")");
            }
        }

        @Override
        public Object visitUnary(UnaryExpression expr) throws InterpreterException {
            Object operand = interpreter.evaluateExpressionInternal(expr.getOperand(), context);

            return switch (expr.getOperator()) {
                case NOT -> !isTruthy(operand);
                case MINUS -> {
                    if (!(operand instanceof Number)) {
                        throw new InterpreterException("负号运算错误: 操作数不是数字类型 (" + 
                            (operand == null ? "null" : operand.getClass().getSimpleName()) + ")");
                    }
                    yield negateNumber((Number) operand);
                }
                default -> operand;
            };
        }

        @Override
        public Object visitFunctionCall(FunctionCallExpression expr) throws InterpreterException {
            String functionName = expr.getFunctionName();
            List<Object> args = new ArrayList<>();
            
            try {
                for (Expression arg : expr.getArguments()) {
                    args.add(interpreter.evaluateExpressionInternal(arg, context));
                }

                IFunction function = context.getFunction(functionName);
                if (function != null) {
                    return function.call(context, args);
                } else {
                    return context.executeMjavaMethod("", functionName, args.toArray());
                }
            } catch (InterpreterException e) {
                throw e;
            } catch (Exception e) {
                throw new InterpreterException("函数 '" + functionName + "' 调用错误 (参数数量: " + args.size() + "): " + e.getMessage(), e);
            }
        }

        @Override
        public Object visitArrayAccess(ArrayAccessExpression expr) throws InterpreterException {
            Object array = interpreter.evaluateExpressionInternal(expr.getArray(), context);
            Object index = interpreter.evaluateExpressionInternal(expr.getIndex(), context);

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
            Object obj = interpreter.evaluateExpressionInternal(expr.getObject(), context);
            String member = expr.getMemberName();

            try {
                java.lang.reflect.Field field = obj.getClass().getField(member);
                return field.get(obj);
            } catch (Exception e) {
                return null;
            }
        }
    }
    
    private static boolean isTruthy(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof Number) return ((Number) value).doubleValue() != 0;
        if (value instanceof String) return !((String) value).isEmpty();
        return true;
    }
    
    private static boolean isEqual(Object a, Object b) {
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
    
    private static int compare(Object a, Object b) {
        if (a instanceof Number && b instanceof Number) {
            return Double.compare(((Number) a).doubleValue(), ((Number) b).doubleValue());
        }
        if (a instanceof String && b instanceof String) {
            return ((String) a).compareTo((String) b);
        }
        return 0;
    }
    
    private static long toLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }
    
    private static int toInt(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }
    
    private static boolean isInteger(Number n) {
        if (n instanceof Integer || n instanceof Long) {
            return true;
        }
        if (n instanceof Double || n instanceof Float) {
            double d = n.doubleValue();
            return d == Math.floor(d) && !Double.isInfinite(d);
        }
        return false;
    }
    
    private static Number addNumbers(Number a, Number b) {
        if (isInteger(a) && isInteger(b)) {
            return a.longValue() + b.longValue();
        }
        return a.doubleValue() + b.doubleValue();
    }
    
    private static Number subtractNumbers(Number a, Number b) {
        if (isInteger(a) && isInteger(b)) {
            return a.longValue() - b.longValue();
        }
        return a.doubleValue() - b.doubleValue();
    }
    
    private static Number multiplyNumbers(Number a, Number b) {
        if (isInteger(a) && isInteger(b)) {
            return a.longValue() * b.longValue();
        }
        return a.doubleValue() * b.doubleValue();
    }
    
    private static Number divideNumbers(Number a, Number b) {
        if (isInteger(a) && isInteger(b)) {
            long la = a.longValue();
            long lb = b.longValue();
            if (lb != 0 && la % lb == 0) {
                return la / lb;
            }
        }
        return a.doubleValue() / b.doubleValue();
    }
    
    private static Number moduloNumbers(Number a, Number b) {
        if (isInteger(a) && isInteger(b)) {
            return a.longValue() % b.longValue();
        }
        return a.doubleValue() % b.doubleValue();
    }
    
    private static Number negateNumber(Number n) {
        if (isInteger(n)) {
            return -n.longValue();
        }
        return -n.doubleValue();
    }
}
