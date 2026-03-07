package cn.langlang.iapp.parser;

import cn.langlang.iapp.ast.*;
import cn.langlang.iapp.lexer.Token;
import cn.langlang.iapp.lexer.TokenType;
import cn.langlang.iapp.runtime.FunctionRegistry;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.ParamType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Parser implements IParser {
    private static final int MAX_ITERATIONS = 100000;
    private int iterationCount = 0;
    
    private List<Token> tokens;
    private int current;
    private final Set<String> definedFunctions;
    private FunctionRegistry functionRegistry;
    
    public Parser() {
        this.tokens = null;
        this.current = 0;
        this.definedFunctions = new HashSet<>();
        this.functionRegistry = new FunctionRegistry();
    }
    
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.current = 0;
        this.definedFunctions = new HashSet<>();
        this.functionRegistry = new FunctionRegistry();
    }
    
    public void reset(List<Token> tokens) {
        this.tokens = tokens;
        this.current = 0;
        this.iterationCount = 0;
        this.definedFunctions.clear();
    }
    
    public void setFunctionRegistry(FunctionRegistry functionRegistry) {
        this.functionRegistry = functionRegistry;
    }
    
    @Override
    public Program parse(List<Token> tokens) throws ParserException {
        reset(tokens);
        return parse();
    }
    
    public Program parse() throws ParserException {
        Program program = new Program();
        
        while (!isAtEnd()) {
            iterationCount++;
            if (iterationCount > MAX_ITERATIONS) {
                throw new ParserException("解析器检测到可能的死循环", peek().getLine(), peek().getColumn());
            }
            
            int startPos = current;
            Statement statement = parseStatement();
            if (statement != null) {
                program.addStatement(statement);
            }
            
            if (current == startPos && !isAtEnd()) {
                Token token = peek();
                throw new ParserException("解析器卡在 token: " + token.getValue(), token.getLine(), token.getColumn());
            }
        }
        
        return program;
    }
    
    private Statement parseStatement() throws ParserException {
        if (isAtEnd()) return null;
        
        Token token = peek();
        
        switch (token.getType()) {
            case KEYWORD_S:
            case KEYWORD_SS:
            case KEYWORD_SSS:
                Token nextToken = peekNext();
                if (nextToken != null && nextToken.getType() == TokenType.LPAREN) {
                    return parseFunctionCallStatement(token);
                }
                if (nextToken != null && isMathFunctionOperator(nextToken.getType())) {
                    return parseMathFunctionCallStatement(token, nextToken);
                }
                if (nextToken != null && nextToken.getType() == TokenType.DOT) {
                    return parseScopedAssignmentStatement(token);
                }
                return parseVariableDeclaration();
            case KEYWORD_IF:
                return parseIfStatement();
            case KEYWORD_WHILE:
                return parseWhileStatement();
            case KEYWORD_FOR:
                return parseForStatement();
            case KEYWORD_BREAK:
                return parseBreakStatement();
            case KEYWORD_ENDCODE:
                return parseEndCodeStatement();
            case KEYWORD_FN:
                return parseFunctionDefinition();
            case KEYWORD_T:
                return parseThreadStatement();
            case KEYWORD_ELSE:
                return null;
            case KEYWORD_END:
                return null;
            case LBRACE:
                return parseBlockStatement();
            case NEWLINE:
                advance();
                return null;
            case IDENTIFIER:
                return parseIdentifierStatement();
            default:
                throw new ParserException("意外的 token: " + token.getValue(), token.getLine(), token.getColumn());
        }
    }

    private Token peekNext() {
        if (current + 1 < tokens.size()) {
            return tokens.get(current + 1);
        }
        return null;
    }

    private Statement parseFunctionCallStatement(Token funcToken) throws ParserException {
        advance();
        consume(TokenType.LPAREN, "Expected '(' after function name");
        List<Expression> arguments = new ArrayList<>();
        List<String> outputVariables = new ArrayList<>();
        TokenType resultScope = funcToken.getType();
        
        String functionName = funcToken.getValue();
        
        if (!check(TokenType.RPAREN)) {
            int paramIndex = 0;
            do {
                boolean isOutputParam = isOutputParameter(functionName, paramIndex);
                
                if (isOutputParam && check(TokenType.IDENTIFIER)) {
                    Token varToken = advance();
                    outputVariables.add(varToken.getValue());
                } else {
                    arguments.add(parseExpression());
                }
                paramIndex++;
            } while (match(TokenType.COMMA));
        }
        
        consume(TokenType.RPAREN, "Expected ')' after arguments");
        
        return new FunctionCallStatement(funcToken.getLine(), functionName, arguments, outputVariables, resultScope);
    }
    
    private boolean isOutputParameter(String functionName, int paramIndex) {
        if (functionRegistry == null) {
            return false;
        }
        
        IFunction function = functionRegistry.getFunction(functionName);
        if (function == null) {
            return false;
        }
        
        List<List<ParamType>> paramTypeLists = function.getParamTypeLists();
        if (paramTypeLists == null || paramTypeLists.isEmpty()) {
            return false;
        }
        
        for (List<ParamType> paramTypes : paramTypeLists) {
            if (paramTypes == null || paramTypes.isEmpty()) {
                continue;
            }
            
            if (paramIndex < paramTypes.size()) {
                if (paramTypes.get(paramIndex) == ParamType.OUTPUT) {
                    return true;
                }
            } else if (paramTypes.get(paramTypes.size() - 1) == ParamType.OUTPUT) {
                return true;
            }
        }
        
        return false;
    }
    
    private Statement parseVariableDeclaration() throws ParserException {
        Token scopeToken = advance();
        TokenType scope = scopeToken.getType();
        
        if (!check(TokenType.IDENTIFIER)) {
            throw new ParserException("需要变量名", scopeToken.getLine(), scopeToken.getColumn());
        }
        
        Token nameToken = advance();
        String variableName = nameToken.getValue();
        
        Expression initialValue = null;
        if (match(TokenType.EQUALS)) {
            initialValue = parseExpression();
        }
        
        return new VariableDeclarationStatement(scopeToken.getLine(), scope, variableName, initialValue);
    }
    
    private Statement parseScopedAssignmentStatement(Token scopeToken) throws ParserException {
        advance();
        consume(TokenType.DOT, "Expected '.' after scope prefix");
        
        TokenType scope = scopeToken.getType();
        
        if (!check(TokenType.IDENTIFIER)) {
            throw new ParserException("Expected variable name after " + scopeToken.getValue() + ".", scopeToken.getLine(), scopeToken.getColumn());
        }
        
        Token nameToken = advance();
        
        if (match(TokenType.EQUALS)) {
            Expression value = parseExpression();
            return new AssignmentStatement(nameToken.getLine(), nameToken.getValue(), value, scope);
        }
        
        if (match(TokenType.LBRACKET)) {
            Expression index = parseExpression();
            consume(TokenType.RBRACKET, "Expected ']' after index");
            consume(TokenType.EQUALS, "Expected '=' after array access");
            Expression value = parseExpression();
            return new AssignmentStatement(nameToken.getLine(), nameToken.getValue(), index, value, scope);
        }
        
        throw new ParserException("Expected '=' after variable name", nameToken.getLine(), nameToken.getColumn());
    }
    
    private Statement parseIfStatement() throws ParserException {
        Token ifToken = advance();
        
        consume(TokenType.LPAREN, "Expected '(' after 'f'");
        Expression condition = parseExpression();
        consume(TokenType.RPAREN, "Expected ')' after condition");
        
        skipNewlines();
        consume(TokenType.LBRACE, "Expected '{' after condition");
        List<Statement> thenStatements = parseBlock();
        
        IfStatement ifStatement = new IfStatement(ifToken.getLine(), condition, thenStatements);
        
        skipNewlines();
        while (match(TokenType.KEYWORD_ELSE)) {
            skipNewlines();
            if (match(TokenType.KEYWORD_IF)) {
                consume(TokenType.LPAREN, "Expected '(' after 'f'");
                Expression elseIfCondition = parseExpression();
                consume(TokenType.RPAREN, "Expected ')' after condition");
                skipNewlines();
                consume(TokenType.LBRACE, "Expected '{' after condition");
                List<Statement> elseIfStatements = parseBlock();
                ifStatement.addElseIfClause(new IfStatement.ElseIfClause(elseIfCondition, elseIfStatements));
                skipNewlines();
            } else {
                skipNewlines();
                consume(TokenType.LBRACE, "Expected '{' after 'else'");
                List<Statement> elseStatements = parseBlock();
                ifStatement.setElseStatements(elseStatements);
                break;
            }
        }
        
        return ifStatement;
    }
    
    private Statement parseWhileStatement() throws ParserException {
        Token whileToken = advance();
        
        consume(TokenType.LPAREN, "Expected '(' after 'w'");
        Expression condition = parseExpression();
        consume(TokenType.RPAREN, "Expected ')' after condition");
        
        skipNewlines();
        consume(TokenType.LBRACE, "Expected '{' after condition");
        List<Statement> body = parseBlock();
        
        return new WhileStatement(whileToken.getLine(), condition, body);
    }
    
    private Statement parseForStatement() throws ParserException {
        Token forToken = advance();
        
        consume(TokenType.LPAREN, "Expected '(' after 'for'");
        
        Statement initStatement = null;
        Expression first = null;
        boolean isCStyle = false;
        
        if (check(TokenType.KEYWORD_S) || check(TokenType.KEYWORD_SS) || check(TokenType.KEYWORD_SSS)) {
            initStatement = parseVariableDeclaration();
            isCStyle = true;
        } else {
            first = parseExpression();
        }
        
        if (match(TokenType.SEMICOLON)) {
            if (isCStyle) {
                Expression condition = parseExpression();
                consume(TokenType.SEMICOLON, "Expected ';' after condition in for loop");
                Statement updateStatement = parseForUpdateStatement();
                consume(TokenType.RPAREN, "Expected ')' after for parameters");
                skipNewlines();
                consume(TokenType.LBRACE, "Expected '{' after for");
                List<Statement> body = parseBlock();
                return new ForStatement(forToken.getLine(), initStatement, condition, updateStatement, body);
            } else {
                Expression end = parseExpression();
                Expression step = null;
                if (match(TokenType.SEMICOLON)) {
                    step = parseExpression();
                }
                consume(TokenType.RPAREN, "Expected ')' after for parameters");
                skipNewlines();
                consume(TokenType.LBRACE, "Expected '{' after for");
                List<Statement> body = parseBlock();
                return new ForStatement(forToken.getLine(), first, end, step, body);
            }
        } else {
            if (isCStyle) {
                throw new ParserException("C风格 for 循环需要条件和更新语句", forToken.getLine(), forToken.getColumn());
            }
            Token varToken = previous();
            if (varToken.getType() != TokenType.IDENTIFIER) {
                throw new ParserException("for-each 循环需要变量名", varToken.getLine(), varToken.getColumn());
            }
            consume(TokenType.RPAREN, "Expected ')' after for-each");
            skipNewlines();
            consume(TokenType.LBRACE, "Expected '{' after for");
            List<Statement> body = parseBlock();
            return new ForStatement(forToken.getLine(), varToken.getValue(), first, body);
        }
    }
    
    private Statement parseForUpdateStatement() throws ParserException {
        Token identifier = peek();
        
        if (identifier.getType() == TokenType.IDENTIFIER) {
            advance();
            
            if (match(TokenType.PLUS_PLUS)) {
                return new AssignmentStatement(identifier.getLine(), identifier.getValue(), 
                    new BinaryExpression(identifier.getLine(), 
                        new VariableExpression(identifier.getLine(), identifier.getValue(), TokenType.KEYWORD_S),
                        TokenType.PLUS,
                        new NumberLiteralExpression(identifier.getLine(), 1)),
                    TokenType.KEYWORD_S);
            }
            
            if (match(TokenType.MINUS_MINUS)) {
                return new AssignmentStatement(identifier.getLine(), identifier.getValue(), 
                    new BinaryExpression(identifier.getLine(), 
                        new VariableExpression(identifier.getLine(), identifier.getValue(), TokenType.KEYWORD_S),
                        TokenType.MINUS,
                        new NumberLiteralExpression(identifier.getLine(), 1)),
                    TokenType.KEYWORD_S);
            }
            
            if (match(TokenType.PLUS_EQUALS)) {
                Expression value = parseExpression();
                return new AssignmentStatement(identifier.getLine(), identifier.getValue(), 
                    new BinaryExpression(identifier.getLine(), 
                        new VariableExpression(identifier.getLine(), identifier.getValue(), TokenType.KEYWORD_S),
                        TokenType.PLUS,
                        value),
                    TokenType.KEYWORD_S);
            }
            
            if (match(TokenType.MINUS_EQUALS)) {
                Expression value = parseExpression();
                return new AssignmentStatement(identifier.getLine(), identifier.getValue(), 
                    new BinaryExpression(identifier.getLine(), 
                        new VariableExpression(identifier.getLine(), identifier.getValue(), TokenType.KEYWORD_S),
                        TokenType.MINUS,
                        value),
                    TokenType.KEYWORD_S);
            }
            
            if (match(TokenType.STAR_EQUALS)) {
                Expression value = parseExpression();
                return new AssignmentStatement(identifier.getLine(), identifier.getValue(), 
                    new BinaryExpression(identifier.getLine(), 
                        new VariableExpression(identifier.getLine(), identifier.getValue(), TokenType.KEYWORD_S),
                        TokenType.STAR,
                        value),
                    TokenType.KEYWORD_S);
            }
            
            if (match(TokenType.SLASH_EQUALS)) {
                Expression value = parseExpression();
                return new AssignmentStatement(identifier.getLine(), identifier.getValue(), 
                    new BinaryExpression(identifier.getLine(), 
                        new VariableExpression(identifier.getLine(), identifier.getValue(), TokenType.KEYWORD_S),
                        TokenType.SLASH,
                        value),
                    TokenType.KEYWORD_S);
            }
            
            if (match(TokenType.EQUALS)) {
                Expression value = parseExpression();
                return new AssignmentStatement(identifier.getLine(), identifier.getValue(), value, TokenType.KEYWORD_S);
            }
            
            throw new ParserException("for 循环需要更新表达式", identifier.getLine(), identifier.getColumn());
        }
        
        throw new ParserException("for 更新语句需要标识符", identifier.getLine(), identifier.getColumn());
    }
    
    private Statement parseBreakStatement() {
        Token breakToken = advance();
        return new BreakStatement(breakToken.getLine());
    }
    
    private Statement parseEndCodeStatement() {
        Token endCodeToken = advance();
        return new EndCodeStatement(endCodeToken.getLine());
    }
    
    private Statement parseFunctionDefinition() throws ParserException {
        Token fnToken = advance();
        
        String moduleName = null;
        String functionName;
        
        Token nameToken = consume(TokenType.IDENTIFIER, "Expected function name");
        functionName = nameToken.getValue();
        
        if (match(TokenType.DOT)) {
            moduleName = functionName;
            Token methodToken = consume(TokenType.IDENTIFIER, "Expected method name");
            functionName = methodToken.getValue();
        }
        
        String fullName = moduleName != null ? moduleName + "." + functionName : functionName;
        
        consume(TokenType.LPAREN, "Expected '(' after function name");
        List<Expression> callArguments = new ArrayList<>();
        List<String> parameters = new ArrayList<>();
        
        if (!check(TokenType.RPAREN)) {
            do {
                callArguments.add(parseExpression());
            } while (match(TokenType.COMMA));
        }
        consume(TokenType.RPAREN, "Expected ')' after arguments");
        
        if (definedFunctions.contains(fullName)) {
            return new FunctionCallStatement(fnToken.getLine(), fullName, callArguments, new ArrayList<>(), TokenType.KEYWORD_S);
        }
        
        for (Expression arg : callArguments) {
            if (arg instanceof VariableExpression) {
                parameters.add(((VariableExpression) arg).getName());
            } else {
                throw new ParserException("函数定义参数必须是标识符", fnToken.getLine(), fnToken.getColumn());
            }
        }
        
        skipNewlines();
        
        boolean hasEndFn = false;
        int savedPos = current;
        int depth = 1;
        while (!isAtEnd() && depth > 0) {
            if (check(TokenType.KEYWORD_FN)) {
                Token nextFn = peek();
                int tempPos = current + 1;
                if (tempPos < tokens.size()) {
                    Token next = tokens.get(tempPos);
                    if (next.getType() == TokenType.IDENTIFIER) {
                        tempPos++;
                        if (tempPos < tokens.size() && tokens.get(tempPos).getType() == TokenType.LPAREN) {
                            depth++;
                        }
                    }
                }
            } else if (check(TokenType.KEYWORD_END)) {
                int tempPos = current + 1;
                if (tempPos < tokens.size() && tokens.get(tempPos).getType() == TokenType.KEYWORD_FN) {
                    depth--;
                    if (depth == 0) {
                        hasEndFn = true;
                    }
                }
            }
            advance();
        }
        
        if (!hasEndFn) {
            throw new ParserException("函数定义必须以 'end fn' 结束", fnToken.getLine(), fnToken.getColumn());
        }
        
        current = savedPos;
        List<Statement> body = parseFunctionBody();
        
        definedFunctions.add(fullName);
        
        return new FunctionDefinitionStatement(fnToken.getLine(), moduleName, functionName, parameters, body);
    }
    
    private List<Statement> parseFunctionBody() throws ParserException {
        List<Statement> statements = new ArrayList<>();
        int blockIterations = 0;
        
        while (!isAtEnd()) {
            blockIterations++;
            if (blockIterations > MAX_ITERATIONS) {
                throw new ParserException("parseFunctionBody 检测到可能的死循环", peek().getLine(), peek().getColumn());
            }
            
            skipNewlines();
            
            if (check(TokenType.KEYWORD_END)) {
                advance();
                if (!check(TokenType.KEYWORD_FN)) {
                    Token token = peek();
                    throw new ParserException("'end' 后需要 'fn'", token.getLine(), token.getColumn());
                }
                advance();
                break;
            }
            
            int startPos = current;
            Statement statement = parseStatement();
            if (statement != null) {
                statements.add(statement);
            }
            
            if (current == startPos && !isAtEnd()) {
                Token token = peek();
                if (token.getType() == TokenType.KEYWORD_END) {
                    continue;
                }
                throw new ParserException("解析器在函数体中卡在 token: " + token.getValue(), token.getLine(), token.getColumn());
            }
        }
        
        return statements;
    }
    
    private Statement parseThreadStatement() throws ParserException {
        Token threadToken = advance();
        
        consume(TokenType.LPAREN, "Expected '(' after 't'");
        consume(TokenType.RPAREN, "Expected ')' after '('");
        
        skipNewlines();
        consume(TokenType.LBRACE, "Expected '{' after ')'");
        List<Statement> body = parseBlock();
        
        return new ThreadStatement(threadToken.getLine(), body);
    }
    
    private Statement parseBlockStatement() throws ParserException {
        Token braceToken = advance();
        List<Statement> statements = parseBlock();
        return new BlockStatement(braceToken.getLine(), statements);
    }
    
    private List<Statement> parseBlock() throws ParserException {
        List<Statement> statements = new ArrayList<>();
        int blockIterations = 0;
        
        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            blockIterations++;
            if (blockIterations > MAX_ITERATIONS) {
                throw new ParserException("parseBlock 检测到可能的死循环", peek().getLine(), peek().getColumn());
            }
            
            skipNewlines();
            if (check(TokenType.RBRACE)) break;
            if (check(TokenType.KEYWORD_ELSE) || check(TokenType.KEYWORD_END)) break;
            
            int startPos = current;
            Statement statement = parseStatement();
            if (statement != null) {
                statements.add(statement);
            }
            
            if (current == startPos && !isAtEnd()) {
                Token token = peek();
                throw new ParserException("解析器在块中卡在 token: " + token.getValue(), token.getLine(), token.getColumn());
            }
        }
        
        consume(TokenType.RBRACE, "Expected '}' after block");
        return statements;
    }
    
    private Statement parseIdentifierStatement() throws ParserException {
        Token identifier = advance();
        
        TokenType scope = null;
        String scopePrefix = null;
        
        if (identifier.getValue().equals("s") || identifier.getValue().equals("ss") || identifier.getValue().equals("sss")) {
            if (match(TokenType.DOT)) {
                scopePrefix = identifier.getValue();
                if (identifier.getValue().equals("s")) {
                    scope = TokenType.KEYWORD_S;
                } else if (identifier.getValue().equals("ss")) {
                    scope = TokenType.KEYWORD_SS;
                } else {
                    scope = TokenType.KEYWORD_SSS;
                }
                
                if (!check(TokenType.IDENTIFIER)) {
                    throw new ParserException("Expected variable name after " + scopePrefix + ".", identifier.getLine(), identifier.getColumn());
                }
                identifier = advance();
            }
        }
        
        if (match(TokenType.DOT)) {
            return parseMemberAccessOrCall(identifier, scope);
        }
        
        if (match(TokenType.EQUALS)) {
            Expression value = parseExpression();
            TokenType useScope = (scope != null) ? scope : TokenType.KEYWORD_S;
            return new AssignmentStatement(identifier.getLine(), identifier.getValue(), value, useScope);
        }
        
        if (match(TokenType.LPAREN)) {
            return parseFunctionCallWithIdentifier(identifier.getValue(), identifier.getLine());
        }
        
        if (match(TokenType.LBRACKET)) {
            Expression index = parseExpression();
            consume(TokenType.RBRACKET, "Expected ']' after index");
            consume(TokenType.EQUALS, "Expected '=' after array access");
            Expression value = parseExpression();
            TokenType useScope = (scope != null) ? scope : TokenType.KEYWORD_S;
            return new AssignmentStatement(identifier.getLine(), identifier.getValue(), index, value, useScope);
        }
        
        throw new ParserException("标识符后有意外的 token: " + identifier.getValue(), identifier.getLine(), identifier.getColumn());
    }
    
    private Statement parseMemberAccessOrCall(Token objectToken, TokenType scope) throws ParserException {
        Token memberToken = consume(TokenType.IDENTIFIER, "Expected member name");
        
        if (match(TokenType.LPAREN)) {
            return parseMethodCall(objectToken.getValue(), memberToken.getValue(), objectToken.getLine());
        }
        
        if (match(TokenType.EQUALS)) {
            Expression value = parseExpression();
            TokenType useScope = (scope != null) ? scope : TokenType.KEYWORD_S;
            return new AssignmentStatement(objectToken.getLine(), objectToken.getValue() + "." + memberToken.getValue(), value, useScope);
        }
        
        throw new ParserException("成员访问后需要 '(' 或 '='", memberToken.getLine(), memberToken.getColumn());
    }
    
    private Statement parseMethodCall(String objectName, String methodName, int line) throws ParserException {
        List<Expression> arguments = new ArrayList<>();
        
        if (!check(TokenType.RPAREN)) {
            do {
                arguments.add(parseExpression());
            } while (match(TokenType.COMMA));
        }
        
        consume(TokenType.RPAREN, "Expected ')' after arguments");
        
        String fullName = objectName + "." + methodName;
        return new FunctionCallStatement(line, fullName, arguments, new ArrayList<>(), TokenType.KEYWORD_S);
    }
    
    private Statement parseFunctionCallWithIdentifier(String functionName, int line) throws ParserException {
        List<Expression> arguments = new ArrayList<>();
        List<String> outputVariables = new ArrayList<>();
        TokenType resultScope = TokenType.KEYWORD_S;
        
        if (!check(TokenType.RPAREN)) {
            int paramIndex = 0;
            do {
                boolean isOutputParam = isOutputParameter(functionName, paramIndex);
                
                if (isOutputParam && check(TokenType.IDENTIFIER)) {
                    Token varToken = advance();
                    outputVariables.add(varToken.getValue());
                } else {
                    arguments.add(parseExpression());
                }
                paramIndex++;
            } while (match(TokenType.COMMA));
        }
        
        consume(TokenType.RPAREN, "Expected ')' after arguments");
        
        return new FunctionCallStatement(line, functionName, arguments, outputVariables, resultScope);
    }
    
    private Expression parseExpression() throws ParserException {
        return parseOrExpression();
    }
    
    private Expression parseOrExpression() throws ParserException {
        Expression left = parseAndExpression();
        
        while (match(TokenType.OR)) {
            Token operator = previous();
            Expression right = parseAndExpression();
            left = new BinaryExpression(operator.getLine(), left, operator.getType(), right);
        }
        
        return left;
    }
    
    private Expression parseAndExpression() throws ParserException {
        Expression left = parseEqualityExpression();
        
        while (match(TokenType.AND)) {
            Token operator = previous();
            Expression right = parseEqualityExpression();
            left = new BinaryExpression(operator.getLine(), left, operator.getType(), right);
        }
        
        return left;
    }
    
    private Expression parseEqualityExpression() throws ParserException {
        Expression left = parseComparisonExpression();
        
        while (match(TokenType.EQ) || match(TokenType.NE)) {
            Token operator = previous();
            Expression right = parseComparisonExpression();
            left = new BinaryExpression(operator.getLine(), left, operator.getType(), right);
        }
        
        return left;
    }
    
    private Expression parseComparisonExpression() throws ParserException {
        Expression left = parseStringMatchExpression();
        
        while (match(TokenType.LT) || match(TokenType.GT) || match(TokenType.LE) || match(TokenType.GE)) {
            Token operator = previous();
            Expression right = parseStringMatchExpression();
            left = new BinaryExpression(operator.getLine(), left, operator.getType(), right);
        }
        
        return left;
    }
    
    private Expression parseStringMatchExpression() throws ParserException {
        Expression left = parseAdditiveExpression();
        
        while (match(TokenType.STARTS_WITH) || match(TokenType.ENDS_WITH) || match(TokenType.CONTAINS)) {
            Token operator = previous();
            Expression right = parseAdditiveExpression();
            left = new BinaryExpression(operator.getLine(), left, operator.getType(), right);
        }
        
        return left;
    }
    
    private Expression parseAdditiveExpression() throws ParserException {
        Expression left = parseMultiplicativeExpression();
        
        while (match(TokenType.PLUS) || match(TokenType.MINUS)) {
            Token operator = previous();
            Expression right = parseMultiplicativeExpression();
            left = new BinaryExpression(operator.getLine(), left, operator.getType(), right);
        }
        
        return left;
    }
    
    private Expression parseMultiplicativeExpression() throws ParserException {
        Expression left = parseUnaryExpression();
        
        while (match(TokenType.STAR) || match(TokenType.SLASH) || match(TokenType.PERCENT)) {
            Token operator = previous();
            Expression right = parseUnaryExpression();
            left = new BinaryExpression(operator.getLine(), left, operator.getType(), right);
        }
        
        return left;
    }
    
    private Expression parseUnaryExpression() throws ParserException {
        if (match(TokenType.NOT) || match(TokenType.MINUS)) {
            Token operator = previous();
            Expression operand = parseUnaryExpression();
            return new UnaryExpression(operator.getLine(), operator.getType(), operand, true);
        }
        
        return parsePrimaryExpression();
    }
    
    private Expression parsePrimaryExpression() throws ParserException {
        if (match(TokenType.NUMBER)) {
            Token token = previous();
            String value = token.getValue();
            if (value.contains(".")) {
                return new NumberLiteralExpression(token.getLine(), Double.parseDouble(value));
            } else {
                return new NumberLiteralExpression(token.getLine(), Integer.parseInt(value));
            }
        }
        
        if (match(TokenType.STRING)) {
            Token token = previous();
            return new StringLiteralExpression(token.getLine(), token.getValue());
        }
        
        if (match(TokenType.KEYWORD_TRUE)) {
            Token token = previous();
            return new BooleanLiteralExpression(token.getLine(), true);
        }
        
        if (match(TokenType.KEYWORD_FALSE)) {
            Token token = previous();
            return new BooleanLiteralExpression(token.getLine(), false);
        }
        
        if (match(TokenType.KEYWORD_NULL)) {
            Token token = previous();
            return new NullLiteralExpression(token.getLine());
        }
        
        if (match(TokenType.LPAREN)) {
            Expression expression = parseExpression();
            consume(TokenType.RPAREN, "Expected ')' after expression");
            return expression;
        }
        
        if (match(TokenType.KEYWORD_S)) {
            Token token = previous();
            if (match(TokenType.DOT)) {
                if (match(TokenType.IDENTIFIER)) {
                    Token varToken = previous();
                    
                    if (match(TokenType.LPAREN)) {
                        return parseFunctionCallExpression(varToken.getValue(), varToken.getLine());
                    }
                    
                    if (match(TokenType.DOT)) {
                        return parseMemberAccessExpression(varToken.getValue(), varToken.getLine(), TokenType.KEYWORD_S);
                    }
                    
                    if (match(TokenType.LBRACKET)) {
                        Expression index = parseExpression();
                        consume(TokenType.RBRACKET, "Expected ']' after index");
                        return new ArrayAccessExpression(varToken.getLine(), new VariableExpression(varToken.getLine(), varToken.getValue(), TokenType.KEYWORD_S), index);
                    }
                    
                    return new VariableExpression(varToken.getLine(), varToken.getValue(), TokenType.KEYWORD_S);
                }
                throw new ParserException("Expected variable name after s.", token.getLine(), token.getColumn());
            }
            if (match(TokenType.LPAREN)) {
                return parseFunctionCallExpression(token.getValue(), token.getLine());
            }
            return new VariableExpression(token.getLine(), token.getValue(), TokenType.KEYWORD_S);
        }
        
        if (match(TokenType.KEYWORD_SS)) {
            Token token = previous();
            if (match(TokenType.DOT)) {
                if (match(TokenType.IDENTIFIER)) {
                    Token varToken = previous();
                    
                    if (match(TokenType.LPAREN)) {
                        return parseFunctionCallExpression(varToken.getValue(), varToken.getLine());
                    }
                    
                    if (match(TokenType.DOT)) {
                        return parseMemberAccessExpression(varToken.getValue(), varToken.getLine(), TokenType.KEYWORD_SS);
                    }
                    
                    if (match(TokenType.LBRACKET)) {
                        Expression index = parseExpression();
                        consume(TokenType.RBRACKET, "Expected ']' after index");
                        return new ArrayAccessExpression(varToken.getLine(), new VariableExpression(varToken.getLine(), varToken.getValue(), TokenType.KEYWORD_SS), index);
                    }
                    
                    return new VariableExpression(varToken.getLine(), varToken.getValue(), TokenType.KEYWORD_SS);
                }
                throw new ParserException("Expected variable name after ss.", token.getLine(), token.getColumn());
            }
            if (match(TokenType.LPAREN)) {
                return parseFunctionCallExpression(token.getValue(), token.getLine());
            }
            return new VariableExpression(token.getLine(), token.getValue(), TokenType.KEYWORD_SS);
        }
        
        if (match(TokenType.KEYWORD_SSS)) {
            Token token = previous();
            if (match(TokenType.DOT)) {
                if (match(TokenType.IDENTIFIER)) {
                    Token varToken = previous();
                    
                    if (match(TokenType.LPAREN)) {
                        return parseFunctionCallExpression(varToken.getValue(), varToken.getLine());
                    }
                    
                    if (match(TokenType.DOT)) {
                        return parseMemberAccessExpression(varToken.getValue(), varToken.getLine(), TokenType.KEYWORD_SSS);
                    }
                    
                    if (match(TokenType.LBRACKET)) {
                        Expression index = parseExpression();
                        consume(TokenType.RBRACKET, "Expected ']' after index");
                        return new ArrayAccessExpression(varToken.getLine(), new VariableExpression(varToken.getLine(), varToken.getValue(), TokenType.KEYWORD_SSS), index);
                    }
                    
                    return new VariableExpression(varToken.getLine(), varToken.getValue(), TokenType.KEYWORD_SSS);
                }
                throw new ParserException("Expected variable name after sss.", token.getLine(), token.getColumn());
            }
            if (match(TokenType.LPAREN)) {
                return parseFunctionCallExpression(token.getValue(), token.getLine());
            }
            return new VariableExpression(token.getLine(), token.getValue(), TokenType.KEYWORD_SSS);
        }
        
        if (match(TokenType.IDENTIFIER)) {
            Token token = previous();
            
            if (token.getValue().equals("s") || token.getValue().equals("ss") || token.getValue().equals("sss")) {
                if (match(TokenType.DOT)) {
                    TokenType scope;
                    if (token.getValue().equals("s")) {
                        scope = TokenType.KEYWORD_S;
                    } else if (token.getValue().equals("ss")) {
                        scope = TokenType.KEYWORD_SS;
                    } else {
                        scope = TokenType.KEYWORD_SSS;
                    }
                    
                    if (match(TokenType.IDENTIFIER)) {
                        Token varToken = previous();
                        
                        if (match(TokenType.LPAREN)) {
                            return parseFunctionCallExpression(varToken.getValue(), varToken.getLine());
                        }
                        
                        if (match(TokenType.DOT)) {
                            return parseMemberAccessExpression(varToken.getValue(), varToken.getLine(), scope);
                        }
                        
                        if (match(TokenType.LBRACKET)) {
                            Expression index = parseExpression();
                            consume(TokenType.RBRACKET, "Expected ']' after index");
                            return new ArrayAccessExpression(varToken.getLine(), new VariableExpression(varToken.getLine(), varToken.getValue(), scope), index);
                        }
                        
                        return new VariableExpression(varToken.getLine(), varToken.getValue(), scope);
                    }
                    
                    throw new ParserException("Expected variable name after " + token.getValue() + ".", token.getLine(), token.getColumn());
                }
            }
            
            if (match(TokenType.LPAREN)) {
                return parseFunctionCallExpression(token.getValue(), token.getLine());
            }
            
            if (match(TokenType.DOT)) {
                return parseMemberAccessExpression(token.getValue(), token.getLine(), TokenType.KEYWORD_S);
            }
            
            if (match(TokenType.LBRACKET)) {
                Expression index = parseExpression();
                consume(TokenType.RBRACKET, "Expected ']' after index");
                return new ArrayAccessExpression(token.getLine(), new VariableExpression(token.getLine(), token.getValue(), TokenType.KEYWORD_S), index);
            }
            
            return new VariableExpression(token.getLine(), token.getValue(), TokenType.KEYWORD_S);
        }
        
        throw new ParserException("需要表达式", peek().getLine(), peek().getColumn());
    }
    
    private Expression parseFunctionCallExpression(String functionName, int line) throws ParserException {
        List<Expression> arguments = new ArrayList<>();
        
        if (!check(TokenType.RPAREN)) {
            int paramIndex = 0;
            do {
                boolean isOutputParam = isOutputParameter(functionName, paramIndex);
                
                if (isOutputParam && check(TokenType.IDENTIFIER)) {
                    Token varToken = advance();
                    arguments.add(new VariableExpression(varToken.getLine(), varToken.getValue(), TokenType.KEYWORD_S));
                } else {
                    arguments.add(parseExpression());
                }
                paramIndex++;
            } while (match(TokenType.COMMA));
        }
        
        consume(TokenType.RPAREN, "Expected ')' after arguments");
        
        return new FunctionCallExpression(line, functionName, arguments);
    }
    
    private Expression parseMemberAccessExpression(String objectName, int line, TokenType scope) throws ParserException {
        Token memberToken = consume(TokenType.IDENTIFIER, "Expected member name");
        
        if (match(TokenType.LPAREN)) {
            return parseMethodCallExpression(objectName, memberToken.getValue(), line);
        }
        
        if (match(TokenType.DOT)) {
            Expression inner = new MemberAccessExpression(line, new VariableExpression(line, objectName, scope), memberToken.getValue());
            return parseChainedMemberAccess(inner, line);
        }
        
        return new MemberAccessExpression(line, new VariableExpression(line, objectName, scope), memberToken.getValue());
    }
    
    private Expression parseChainedMemberAccess(Expression object, int line) throws ParserException {
        Token memberToken = consume(TokenType.IDENTIFIER, "Expected member name");
        
        if (match(TokenType.LPAREN)) {
            return parseMethodCallExpressionWithObject(object, memberToken.getValue(), line);
        }
        
        if (match(TokenType.DOT)) {
            Expression inner = new MemberAccessExpression(line, object, memberToken.getValue());
            return parseChainedMemberAccess(inner, line);
        }
        
        return new MemberAccessExpression(line, object, memberToken.getValue());
    }
    
    private Expression parseMethodCallExpression(String objectName, String methodName, int line) throws ParserException {
        List<Expression> arguments = new ArrayList<>();
        
        if (!check(TokenType.RPAREN)) {
            do {
                arguments.add(parseExpression());
            } while (match(TokenType.COMMA));
        }
        
        consume(TokenType.RPAREN, "Expected ')' after arguments");
        
        return new FunctionCallExpression(line, objectName + "." + methodName, arguments);
    }
    
    private Expression parseMethodCallExpressionWithObject(Expression object, String methodName, int line) throws ParserException {
        List<Expression> arguments = new ArrayList<>();
        
        if (!check(TokenType.RPAREN)) {
            do {
                arguments.add(parseExpression());
            } while (match(TokenType.COMMA));
        }
        
        consume(TokenType.RPAREN, "Expected ')' after arguments");
        
        return new FunctionCallExpression(line, methodName, arguments);
    }
    
    private void skipNewlines() {
        while (match(TokenType.NEWLINE)) {
        }
    }
    
    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }
    
    private Token peek() {
        return tokens.get(current);
    }
    
    private Token previous() {
        return tokens.get(current - 1);
    }
    
    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }
    
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getType() == type;
    }
    
    private boolean match(TokenType type) {
        if (check(type)) {
            advance();
            return true;
        }
        return false;
    }
    
    private Token consume(TokenType type, String message) throws ParserException {
        if (check(type)) return advance();
        Token token = peek();
        throw new ParserException(message, token.getLine(), token.getColumn());
    }
    
    private boolean isMathFunctionOperator(TokenType type) {
        return type == TokenType.PLUS || type == TokenType.MINUS || 
               type == TokenType.STAR || type == TokenType.SLASH || type == TokenType.PERCENT;
    }
    
    private Statement parseMathFunctionCallStatement(Token scopeToken, Token operatorToken) throws ParserException {
        advance();
        advance();
        consume(TokenType.LPAREN, "Expected '(' after function name");
        
        List<Expression> arguments = new ArrayList<>();
        List<String> outputVariables = new ArrayList<>();
        TokenType resultScope = scopeToken.getType();
        
        if (!check(TokenType.RPAREN)) {
            int paramIndex = 0;
            do {
                boolean isOutputParam = isOutputParameter(scopeToken.getValue() + operatorToken.getValue(), paramIndex);
                
                if (isOutputParam && check(TokenType.IDENTIFIER)) {
                    Token varToken = advance();
                    outputVariables.add(varToken.getValue());
                } else {
                    arguments.add(parseExpression());
                }
                paramIndex++;
            } while (match(TokenType.COMMA));
        }
        
        consume(TokenType.RPAREN, "Expected ')' after arguments");
        
        String functionName = scopeToken.getValue() + operatorToken.getValue();
        return new FunctionCallStatement(scopeToken.getLine(), functionName, arguments, outputVariables, resultScope);
    }
}
