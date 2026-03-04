# 修复 `sj`、`stobm`、`sutf8to` 函数问题

## 问题分析

### 问题1：`sj` 函数输出 `null`

**测试代码：**

```
sj("Hello World", "Hello ", "World", sj_result)
```

**预期输出：** `World`（提取 "Hello " 和 "World" 之间的内容）

**实际输出：** `null`

**原因分析：**
查看 [SjFunction.java](file:///c:/Users/Administrator/IdeaProjects/iAppPC/src/main/java/cn/langlang/iapp/functions/string/SjFunction.java)：

* `call()` 方法只是简单地拼接所有参数，没有实现"提取两个分隔符之间内容"的功能

**正确的** **`sj`** **函数语义（iapp.txt 第626-645行）：**

```
s a = "123456789"
s b = "34"
s c = "8"
sj(a, b, c, d)
//将提示：567

//从头部开始截取
sj(a, null, c, d)

//截取到尾部
sj(a, b, null, d)
```

***

### 问题2：`stobm` 函数 Base64 解码错误

**测试代码：**

```
s tobm_result = stobm("你好", "UTF-8")
```

**错误信息：** `Base64 conversion failed: Illegal base64 character 3f`

**原因分析：**
查看 [StobmFunction.java](file:///c:/Users/Administrator/IdeaProjects/iAppPC/src/main/java/cn/langlang/iapp/functions/string/StobmFunction.java)：

* 当前实现是 Base64 编码/解码

* **但 iApp 原版是 URL 编码！**

**正确的** **`stobm`** **函数语义（iapp.txt 第1462-1470行）：**

```
stobm("你", "utf-8", b)
//输出：%E4%BD%A0

//转换网址中的汉字
stobm("你", "utf-8", true, b)
```

***

### 问题3：`sutf8to` 函数参数不匹配

**测试代码：**

```
sutf8to(tobm_result, utf8_result)
```

**原因分析：**
查看 [Sutf8toFunction.java](file:///c:/Users/Administrator/IdeaProjects/iAppPC/src/main/java/cn/langlang/iapp/functions/string/Sutf8toFunction.java)：

* `getMaxParameters()` 返回 `1`，只接受一个参数

* 测试代码传递了两个参数，第二个应该是输出变量

**正确的** **`sutf8to`** **函数语义（iapp.txt 第1474-1480行）：**

```
sutf8to("%E4%BD%A0", b)
//输出：你

//网址中的汉字
sutf8to("%E4%BD%A0", "utf-8", true, b)
```

***

## 修复计划

### 步骤1：修复 `sj` 函数

修改 `call()` 方法实现正确的截取逻辑：

* 从第一个参数（源字符串）中提取第二个参数（前缀）和第三个参数（后缀）之间的内容

* 支持前缀或后缀为 `null` 的情况

### 步骤2：修复 `stobm` 函数

将 Base64 编码改为 URL 编码：

* 使用 `URLEncoder.encode()` 替代 `Base64.getEncoder()`

* 支持可选的第三个参数（是否网址模式）

### 步骤3：修复 `sutf8to` 函数

* 增加 `getMaxParameters()` 返回值以支持输出变量

* 使用 `URLDecoder.decode()` 进行解码

* 添加 `OUTPUT` 参数类型

***

## 实现细节

### SjFunction.java 修改

```java
@Override
public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
    if (arguments.size() < 3) {
        throw new FunctionException("sj function requires at least 3 arguments");
    }
    
    String source = arguments.get(0) != null ? arguments.get(0).toString() : "";
    String prefix = arguments.get(1) != null ? arguments.get(1).toString() : null;
    String suffix = arguments.get(2) != null ? arguments.get(2).toString() : null;
    
    int startIndex = 0;
    int endIndex = source.length();
    
    if (prefix != null) {
        int pos = source.indexOf(prefix);
        if (pos == -1) {
            return "";
        }
        startIndex = pos + prefix.length();
    }
    
    if (suffix != null) {
        int pos = source.indexOf(suffix, startIndex);
        if (pos == -1) {
            return "";
        }
        endIndex = pos;
    }
    
    return source.substring(startIndex, endIndex);
}
```

### StobmFunction.java 修改

```java
@Override
public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
    String str = arguments.get(0) != null ? arguments.get(0).toString() : "";
    String charsetName = "UTF-8";
    boolean forUrl = false;
    
    if (arguments.size() > 1 && arguments.get(1) != null) {
        charsetName = arguments.get(1).toString();
    }
    
    if (arguments.size() > 2) {
        forUrl = toBoolean(arguments.get(2));
    }
    
    try {
        String encoded = URLEncoder.encode(str, charsetName);
        if (forUrl) {
            return encoded;
        }
        return encoded;
    } catch (Exception e) {
        throw new FunctionException("URL encoding failed: " + e.getMessage(), e);
    }
}
```

### Sutf8toFunction.java 修改

```java
@Override
public int getMaxParameters() {
    return 4;  // 支持输出变量和更多参数
}

@Override
public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
    String str = arguments.get(0) != null ? arguments.get(0).toString() : "";
    String charsetName = "UTF-8";
    
    if (arguments.size() > 1 && arguments.get(1) != null) {
        // 判断是否是编码名称（字符串且不是布尔值）
        Object arg1 = arguments.get(1);
        if (arg1 instanceof String && !(arg1 instanceof Boolean)) {
            charsetName = arg1.toString();
        }
    }
    
    try {
        return URLDecoder.decode(str, charsetName);
    } catch (Exception e) {
        throw new FunctionException("URL decoding failed: " + e.getMessage(), e);
    }
}

@Override
public List<ParamType> getParamTypes() {
    return types(ParamType.STRING, ParamType.STRING, ParamType.BOOLEAN, ParamType.OUTPUT);
}
```

