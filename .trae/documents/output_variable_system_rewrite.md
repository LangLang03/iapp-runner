# 输出变量解析系统重写计划

## 当前实现分析

当前系统使用以下机制处理输出变量：

1. `VariableManager.declaredVariables` - Set 存储已声明变量名
2. `FunctionCallStatement.potentialOutputVariable` - 存储潜在输出变量
3. `Interpreter.visitFunctionCall()` - 运行时检查变量是否已声明来决定是参数还是输出变量

## 目标架构

### 1. 新增参数类型枚举

创建 `ParamType` 枚举，定义参数的数据类型：

```java
package cn.langlang.iapp.runtime;

public enum ParamType {
    STRING,    // 字符串类型
    INT,       // 整数类型
    LONG,      // 长整数类型
    DOUBLE,    // 双精度浮点数类型
    BOOLEAN,   // 布尔类型
    OBJECT,    // 对象类型（任意类型）
    ARRAY,     // 数组类型
    OUTPUT     // 输出变量类型（特殊类型，表示该参数是输出变量名）
}
```

**关键点：OUTPUT 是唯一的特殊类型，表示该参数位置是输出变量名。**

### 2. 修改 IFunction 接口

添加 `getParamTypes()` 方法：

```java
List<ParamType> getParamTypes();
```

### 3. 移除潜在输出变量机制

* 移除 `FunctionCallStatement.potentialOutputVariable` 字段

* 移除 `VariableManager.declaredVariables` 及相关方法

* 移除 `RuntimeContext.declareVariable()` 和 `isVariableDeclared()`

### 4. Parser 解析逻辑

根据函数声明的参数类型解析参数：

* 非 OUTPUT 类型 - 解析表达式，变量会被求值

* OUTPUT 类型 - 解析标识符，作为输出变量名（不解引用）

### 5. Interpreter 执行逻辑

* 对于非 OUTPUT 参数：求值表达式（变量会被解引用为其值）

* 对于 OUTPUT 参数：获取变量名，函数执行完成后将返回值赋给该变量

***

## 实施步骤

### 步骤1: 创建 ParamType 枚举

文件: `src/main/java/cn/langlang/iapp/runtime/ParamType.java`

### 步骤2: 修改 IFunction 接口

添加 `getParamTypes()` 方法。

### 步骤3: 更新所有 IFunction 实现

为每个函数实现 `getParamTypes()` 方法。

### 步骤4: 修改 FunctionCallStatement

移除 `potentialOutputVariable` 字段。

### 步骤5: 修改 Parser

* 移除 `potentialOutputVariable` 相关逻辑

* 在解析函数调用时，查询函数的参数类型

* 根据参数类型决定如何处理每个参数

### 步骤6: 修改 VariableManager

移除 `declaredVariables` 相关代码。

### 步骤7: 修改 RuntimeContext

移除 `declareVariable()` 和 `isVariableDeclared()` 方法。

### 步骤8: 修改 Interpreter

* 移除潜在输出变量检查逻辑

* 根据参数类型处理参数

* 函数执行完成后自动创建输出变量

### 步骤9: 测试验证

***

## 关键函数的参数类型定义

| 函数                                 | 参数类型                                   | 说明                |
| ---------------------------------- | -------------------------------------- | ----------------- |
| syso(expr)                         | \[OBJECT]                              | 输入任意值打印           |
| s/v, out                           | \[OBJECT, OUTPUT]                      | 输入值，输出到变量         |
| ss/v, out                          | \[OBJECT, OUTPUT]                      | 输入值，输出到变量         |
| sss/v, out                         | \[OBJECT, OUTPUT]                      | 输入值，输出到变量         |
| fdir(out)                          | \[OUTPUT]                              | 只有输出变量            |
| fdir(path, out)                    | \[STRING, OUTPUT]                      | 输入路径，输出结果         |
| cls(name, out)                     | \[STRING, OUTPUT]                      | 输入类名，输出类对象        |
| javags(out, obj, cls, field)       | \[OUTPUT, OBJECT, OBJECT, STRING]      | 输出、对象、类、字段名       |
| javax(out, obj, cls, method, ...)  | \[OUTPUT, OBJECT, OBJECT, STRING, ...] | 输出、对象、类、方法名、参数... |
| javanew(out, cls, ...)             | \[OUTPUT, OBJECT, ...]                 | 输出、类名/类对象、构造参数... |
| tw(msg)                            | \[STRING]                              | 输入消息显示            |
| call(out, module, method, args...) | \[OUTPUT, STRING, STRING, OBJECT, ...] | 输出、模块、方法、参数...    |
| sl(string)                         | \[STRING]                              | 输入字符串             |
| sl(string, out)                    | \[STRING, OUTPUT]                      | 输入字符串，输出长度        |
| sran(min, max)                     | \[INT, INT]                            | 输入最小最大值           |
| sran(min, max, out)                | \[INT, INT, OUTPUT]                    | 输入最小最大值，输出随机数     |

***

## 示例

```
s a = "AAA"
syso(a)           // OBJECT: a 被解引用为 "AAA"
s(a, b)           // OBJECT: a 被解引用为 "AAA", OUTPUT: b 接收 "AAA"
fdir(c)           // OUTPUT: c 接收当前目录
fdir(a, d)        // STRING: a 被解引用为 "AAA" (作为路径), OUTPUT: d 接收结果
```

***

## 变量解引用处理

当参数类型不是 OUTPUT 时：

* 如果传递的是字面量（"AAA"、123），直接使用该值

* 如果传递的是变量名（a），获取该变量的值（解引用）

当参数类型是 OUTPUT 时：

* 直接使用标识符作为变量名，不解引用

* 函数执行完成后，将返回值赋给该变量

