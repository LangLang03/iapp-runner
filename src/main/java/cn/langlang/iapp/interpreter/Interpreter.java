package cn.langlang.iapp.interpreter;

import cn.langlang.iapp.ast.*;
import cn.langlang.iapp.lexer.TokenType;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.ArrayList;
import java.util.List;

public class Interpreter implements IInterpreter {

    private final StatementVisitorImpl statementVisitor = new StatementVisitorImpl();
    private final ExpressionVisitorImpl expressionVisitor = new ExpressionVisitorImpl();
    
    private RuntimeContext currentContext;
    
    public Interpreter() {
        statementVisitor.setInterpreter(this);
        expressionVisitor.setInterpreter(this);
    }

    @Override
    public Object execute(Program program, RuntimeContext context) throws InterpreterException {
        this.currentContext = context;
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
        this.currentContext = context;
        return statement.accept(statementVisitor);
    }
    
    @Override
    public Object evaluateExpression(Expression expression, RuntimeContext context) throws InterpreterException {
        this.currentContext = context;
        return expression.accept(expressionVisitor);
    }
    
    RuntimeContext getCurrentContext() {
        return currentContext;
    }
    
    private static class StatementVisitorImpl implements Statement.StatementVisitor<Object> {
        private Interpreter interpreter;
        
        void setInterpreter(Interpreter interpreter) {
            this.interpreter = interpreter;
        }
        
        @Override
        public Object visitVariableDeclaration(VariableDeclarationStatement stmt) throws InterpreterException {
            Object value = null;
            if (stmt.getInitialValue() != null) {
                value = interpreter.evaluateExpression(stmt.getInitialValue(), interpreter.currentContext);
            }
            interpreter.currentContext.setVariable(stmt.getVariableName(), value, stmt.getScope());
            return null;
        }

        @Override
        public Object visitAssignment(AssignmentStatement stmt) throws InterpreterException {
            Object value = interpreter.evaluateExpression(stmt.getValue(), interpreter.currentContext);
            interpreter.currentContext.setVariable(stmt.getVariableName(), value, stmt.getScope());
            return null;
        }

        @Override
        public Object visitIf(IfStatement stmt) throws InterpreterException {
            Object conditionValue = interpreter.evaluateExpression(stmt.getCondition(), interpreter.currentContext);
            if (isTruthy(conditionValue)) {
                for (Statement s : stmt.getThenStatements()) {
                    interpreter.executeStatement(s, interpreter.currentContext);
                }
            } else {
                boolean executed = false;
                for (IfStatement.ElseIfClause clause : stmt.getElseIfClauses()) {
                    Object elseIfCondition = interpreter.evaluateExpression(clause.condition(), interpreter.currentContext);
                    if (isTruthy(elseIfCondition)) {
                        for (Statement s : clause.statements()) {
                            interpreter.executeStatement(s, interpreter.currentContext);
                        }
                        executed = true;
                        break;
                    }
                }
                if (!executed && !stmt.getElseStatements().isEmpty()) {
                    for (Statement s : stmt.getElseStatements()) {
                        interpreter.executeStatement(s, interpreter.currentContext);
                    }
                }
            }
            return null;
        }

        @Override
        public Object visitWhile(WhileStatement stmt) throws InterpreterException {
            RuntimeContext ctx = interpreter.currentContext;
            ctx.pushBreakContext(new RuntimeContext.BreakContext("while"));
            while (isTruthy(interpreter.evaluateExpression(stmt.getCondition(), ctx))) {
                if (ctx.isEndCodeRequested()) break;

                RuntimeContext.BreakContext breakCtx = ctx.getCurrentBreakContext();
                if (breakCtx != null && breakCtx.shouldBreak()) {
                    breakCtx.setShouldBreak(false);
                    break;
                }

                for (Statement s : stmt.getBody()) {
                    interpreter.executeStatement(s, ctx);
                }
            }
            ctx.popBreakContext();
            return null;
        }

        @Override
        public Object visitFor(ForStatement stmt) throws InterpreterException {
            RuntimeContext ctx = interpreter.currentContext;
            ctx.pushBreakContext(new RuntimeContext.BreakContext("for"));

            if (stmt.getForType() == ForStatement.ForType.C_STYLE) {
                if (stmt.getInitStatement() != null) {
                    interpreter.executeStatement(stmt.getInitStatement(), ctx);
                }
                
                while (stmt.getCondition() == null || isTruthy(interpreter.evaluateExpression(stmt.getCondition(), ctx))) {
                    if (ctx.isEndCodeRequested()) break;

                    RuntimeContext.BreakContext breakCtx = ctx.getCurrentBreakContext();
                    if (breakCtx != null && breakCtx.shouldBreak()) {
                        breakCtx.setShouldBreak(false);
                        break;
                    }

                    for (Statement s : stmt.getBody()) {
                        interpreter.executeStatement(s, ctx);
                    }

                    if (stmt.getUpdateStatement() != null) {
                        interpreter.executeStatement(stmt.getUpdateStatement(), ctx);
                    }
                }
            } else if (stmt.getForType() == ForStatement.ForType.RANGE) {
                Object startValue = interpreter.evaluateExpression(stmt.getStart(), ctx);
                Object endValue = interpreter.evaluateExpression(stmt.getEnd(), ctx);

                if (endValue instanceof Object[] array) {
                    String varName = null;
                    if (stmt.getStart() instanceof VariableExpression varExpr) {
                        varName = varExpr.getName();
                    }
                    if (varName != null) {
                        for (Object item : array) {
                            if (ctx.isEndCodeRequested()) break;

                            RuntimeContext.BreakContext breakCtx = ctx.getCurrentBreakContext();
                            if (breakCtx != null && breakCtx.shouldBreak()) {
                                breakCtx.setShouldBreak(false);
                                break;
                            }

                            ctx.setVariable(varName, item, TokenType.KEYWORD_S);

                            for (Statement s : stmt.getBody()) {
                                interpreter.executeStatement(s, ctx);
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
                            if (ctx.isEndCodeRequested()) break;

                            RuntimeContext.BreakContext breakCtx = ctx.getCurrentBreakContext();
                            if (breakCtx != null && breakCtx.shouldBreak()) {
                                breakCtx.setShouldBreak(false);
                                break;
                            }

                            ctx.setVariable(varName, item, TokenType.KEYWORD_S);

                            for (Statement s : stmt.getBody()) {
                                interpreter.executeStatement(s, ctx);
                            }
                        }
                    }
                } else {
                    long start = toLong(startValue);
                    long end = toLong(endValue);
                    long step = 1;

                    if (stmt.getStep() != null) {
                        Object stepValue = interpreter.evaluateExpression(stmt.getStep(), ctx);
                        step = toLong(stepValue);
                    }

                    for (long i = start; i <= end; i += step) {
                        if (ctx.isEndCodeRequested()) break;

                        RuntimeContext.BreakContext breakCtx = ctx.getCurrentBreakContext();
                        if (breakCtx != null && breakCtx.shouldBreak()) {
                            breakCtx.setShouldBreak(false);
                            break;
                        }

                        for (Statement s : stmt.getBody()) {
                            interpreter.executeStatement(s, ctx);
                        }
                    }
                }
            } else if (stmt.getForType() == ForStatement.ForType.ARRAY_ITERATION) {
                Object arrayValue = interpreter.evaluateExpression(stmt.getEnd(), ctx);
                if (arrayValue instanceof Object[] array) {
                    for (Object item : array) {
                        if (ctx.isEndCodeRequested()) break;

                        RuntimeContext.BreakContext breakCtx = ctx.getCurrentBreakContext();
                        if (breakCtx != null && breakCtx.shouldBreak()) {
                            breakCtx.setShouldBreak(false);
                            break;
                        }

                        ctx.setVariable(stmt.getVariableName(), item, TokenType.KEYWORD_S);

                        for (Statement s : stmt.getBody()) {
                            interpreter.executeStatement(s, ctx);
                        }
                    }
                } else if (arrayValue instanceof List<?> list) {
                    for (Object item : list) {
                        if (ctx.isEndCodeRequested()) break;

                        RuntimeContext.BreakContext breakCtx = ctx.getCurrentBreakContext();
                        if (breakCtx != null && breakCtx.shouldBreak()) {
                            breakCtx.setShouldBreak(false);
                            break;
                        }

                        ctx.setVariable(stmt.getVariableName(), item, TokenType.KEYWORD_S);

                        for (Statement s : stmt.getBody()) {
                            interpreter.executeStatement(s, ctx);
                        }
                    }
                }
            }

            ctx.popBreakContext();
            return null;
        }

        @Override
        public Object visitFunctionCall(FunctionCallStatement stmt) throws InterpreterException {
            try {
                String functionName = stmt.getFunctionName();
                List<Object> args = new ArrayList<>();

                for (Expression arg : stmt.getArguments()) {
                    args.add(interpreter.evaluateExpression(arg, interpreter.currentContext));
                }

                Object result;
                RuntimeContext ctx = interpreter.currentContext;
                
                if (ctx.hasUserFunction(functionName)) {
                    FunctionDefinitionStatement funcDef = ctx.getUserFunction(functionName);
                    result = executeUserFunction(funcDef, args, ctx, stmt.getOutputVariables(), interpreter);
                } else {
                    IFunction function = ctx.getFunctionRegistry().getFunction(functionName);
                    if (function != null) {
                        result = function.call(ctx, args);
                    } else {
                        result = ctx.executeMjavaMethod("", functionName, args.toArray());
                    }
                }

                if (stmt.hasOutputVariables()) {
                    List<String> outputVars = stmt.getOutputVariables();
                    for (String varName : outputVars) {
                        ctx.setVariable(varName, result, stmt.getResultScope());
                    }
                }

                return result;
            } catch (Exception e) {
                throw new InterpreterException("函数调用错误: " + e.getMessage());
            }
        }

        @Override
        public Object visitBreak(BreakStatement stmt) {
            RuntimeContext.BreakContext breakCtx = interpreter.currentContext.getCurrentBreakContext();
            if (breakCtx != null) {
                breakCtx.setShouldBreak(true);
            }
            return null;
        }

        @Override
        public Object visitEndCode(EndCodeStatement stmt) {
            interpreter.currentContext.requestEndCode();
            return null;
        }

        @Override
        public Object visitFunctionDefinition(FunctionDefinitionStatement stmt) {
            String fullName = stmt.getFullName();
            interpreter.currentContext.registerUserFunction(fullName, stmt);
            return null;
        }

        @Override
        public Object visitThread(ThreadStatement stmt) {
            final RuntimeContext ctx = interpreter.currentContext;
            new Thread(() -> {
                try {
                    for (Statement s : stmt.getBody()) {
                        interpreter.executeStatement(s, ctx);
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
                interpreter.executeStatement(s, interpreter.currentContext);
            }
            return null;
        }
    }
    
    private static class ExpressionVisitorImpl implements Expression.ExpressionVisitor<Object> {
        private Interpreter interpreter;
        
        void setInterpreter(Interpreter interpreter) {
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
            return interpreter.currentContext.getVariable(expr.getName());
        }

        @Override
        public Object visitBinary(BinaryExpression expr) throws InterpreterException {
            Object left = interpreter.evaluateExpression(expr.getLeft(), interpreter.currentContext);
            Object right = interpreter.evaluateExpression(expr.getRight(), interpreter.currentContext);

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
            Object operand = interpreter.evaluateExpression(expr.getOperand(), interpreter.currentContext);

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
                    args.add(interpreter.evaluateExpression(arg, interpreter.currentContext));
                }

                IFunction function = interpreter.currentContext.getFunctionRegistry().getFunction(functionName);
                if (function != null) {
                    return function.call(interpreter.currentContext, args);
                } else {
                    return interpreter.currentContext.executeMjavaMethod("", functionName, args.toArray());
                }
            } catch (Exception e) {
                throw new InterpreterException("函数 '" + functionName + "' 调用错误 (参数数量: " + args.size() + "): " + e.getMessage(), e);
            }
        }

        @Override
        public Object visitArrayAccess(ArrayAccessExpression expr) throws InterpreterException {
            Object array = interpreter.evaluateExpression(expr.getArray(), interpreter.currentContext);
            Object index = interpreter.evaluateExpression(expr.getIndex(), interpreter.currentContext);

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
            Object obj = interpreter.evaluateExpression(expr.getObject(), interpreter.currentContext);
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
    
    private static Object executeUserFunction(FunctionDefinitionStatement funcDef, List<Object> args, RuntimeContext context, List<String> outputVariables, Interpreter interpreter) throws InterpreterException {
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
                interpreter.executeStatement(stmt, context);
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
