# 裕语言解释器修改计划

## 问题分析

根据 `iapp.txt` 文档规范，当前解释器存在以下三个问题：

### 1. 三参数for循环不支持

**文档规范** (iapp.txt 第249-271行):

```
for(s a=1; a<10; a++)
{
    syso(a)
}
for(s a=1; a<100; a+=10)
{
    syso(a)
}
for(s a=10; a>0; a--)
{
    syso(a)
}
```

**当前问题**: Parser.java 的 `parseForStatement()` 只支持：

* `for(start; end; step)` - 范围循环

* `for(var; array)` - 数组迭代

不支持三参数形式：`for(初始化; 条件; 更新)`

### 2. 单参数输出变量语法问题

**文档规范** (iapp.txt 第571-574行):

```
s a = 12
s b = 13
s(a + b, c)
tw(c)
```

**当前问题**: `parseFunctionCallWithIdentifier()` 中检测输出变量的逻辑在单参数情况下可能失效，因为 `hasArguments` 初始为 false。

### 3. 函数定义解析支持，但调用不完整

**文档规范** (iapp.txt 第1585-1626行):

```
fn hanshu(a, b)
ss(a + b, c)
tw(c)
end fn

fn mokuai.hanshu(a)
tw(a)
end fn
```

**当前问题**:

* `FunctionDefinitionStatement` AST节点已存在

* `parseFunctionDefinition()` 能解析函数定义

* 但 `Interpreter.visitFunctionDefinition()` 返回 null，未实际注册函数

* 函数调用时无法找到用户定义的函数

***

## 修改方案

### 步骤1: 修改 ForStatement.java

添加新的 `ForType.C_STYLE` 枚举值和新的构造函数：

```java
public enum ForType {
    RANGE,           // for(1; 10) 或 for(1; 10; 2)
    ARRAY_ITERATION, // for(item; array)
    C_STYLE          // for(s a=1; a<10; a++)
}

// 新增构造函数
public ForStatement(int line, Statement init, Expression condition, 
                    Statement update, List<Statement> body)
```

### 步骤2: 修改 Parser.java - parseForStatement()

重新设计 for 循环解析逻辑：

1. 检测第一个参数是否是变量声明 (`s a=1`)
2. 如果第一个分号后是条件表达式，则是三参数for循环
3. 如果第一个分号后是数字/变量，则是范围循环
4. 如果没有分号，则是数组迭代

### 步骤3: 修改 Parser.java - 输出变量解析

修改 `parseFunctionCallWithIdentifier()` 和 `parseFunctionCallStatement()`:

* 确保单参数情况下正确识别输出变量

* 输出变量应该是最后一个参数且后面紧跟 `)`

### 步骤4: 修改 RuntimeContext.java

添加用户函数注册表：

```java
private final Map<String, FunctionDefinitionStatement> userFunctions;

public void registerUserFunction(String name, FunctionDefinitionStatement func);
public FunctionDefinitionStatement getUserFunction(String name);
```

### 步骤5: 修改 Interpreter.java

1. **visitFunctionDefinition()**: 注册用户定义函数
2. **visitFor()**: 支持三参数for循环执行
3. **visitFunctionCall()**: 先查找用户函数，再查找内置函数

***

## 实施步骤

1. 修改 `TokenType.java` - 无需修改
2. 修改 `ForStatement.java` - 添加C\_STYLE类型和新构造函数
3. 修改 `Parser.java` - 修改for循环解析和输出变量解析
4. 修改 `RuntimeContext.java` - 添加用户函数注册
5. 修改 `Interpreter.java` - 实现函数注册和三参数for执行
6. 测试验证

***

## 测试用例

### 三参数for循环测试

```
for(s a=1; a<10; a++)
{
    syso(a)
}
```

### 单参数输出变量测试

```
s a = 12
s b = 13
s(a + b, c)
tw(c)
```

### 函数定义和调用测试

```
fn add(a, b)
ss(a + b, c)
end fn

fn add(1, 2)
```

