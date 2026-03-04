# Tasks

- [x] Task 1: 项目基础结构搭建
  - [x] SubTask 1.1: 创建项目基础目录结构（lexer、parser、interpreter、runtime、functions等包）
  - [x] SubTask 1.2: 在build.gradle中添加BeanShell依赖配置
  - [x] SubTask 1.3: 创建核心接口定义（ILexer、IParser、IInterpreter、IFunction等）

- [x] Task 2: 词法分析器实现
  - [x] SubTask 2.1: 创建TokenType枚举，定义所有Token类型
  - [x] SubTask 2.2: 创建Token类，表示词法单元
  - [x] SubTask 2.3: 实现Lexer类，将源代码转换为Token序列
  - [x] SubTask 2.4: 处理字符串字面量解析（包括转义字符）
  - [x] SubTask 2.5: 处理数字字面量解析（整数和小数）
  - [x] SubTask 2.6: 处理注释（单行和多行注释）
  - [x] SubTask 2.7: 处理运算符和分隔符

- [x] Task 3: 语法分析器实现
  - [x] SubTask 3.1: 创建AST节点基类和各类节点（Program、Statement、Expression等）
  - [x] SubTask 3.2: 实现Parser类，将Token序列转换为AST
  - [x] SubTask 3.3: 解析变量声明语句
  - [x] SubTask 3.4: 解析赋值语句
  - [x] SubTask 3.5: 解析if判断语句
  - [x] SubTask 3.6: 解析while循环语句
  - [x] SubTask 3.7: 解析for循环语句
  - [x] SubTask 3.8: 解析函数调用语句
  - [x] SubTask 3.9: 解析表达式（算术、逻辑、比较）
  - [x] SubTask 3.10: 解析模块定义和函数定义
  - [x] SubTask 3.11: 解析break和endcode语句

- [x] Task 4: 变量管理系统实现
  - [x] SubTask 4.1: 创建Variable类，表示变量
  - [x] SubTask 4.2: 创建VariableScope枚举，定义变量作用域类型
  - [x] SubTask 4.3: 创建VariableManager类，管理局部变量、界面变量、全局变量
  - [x] SubTask 4.4: 实现变量查找、设置、删除功能
  - [x] SubTask 4.5: 实现作用域切换功能

- [x] Task 5: 解释器引擎实现
  - [x] SubTask 5.1: 创建Interpreter类，实现AST遍历执行
  - [x] SubTask 5.2: 实现表达式求值
  - [x] SubTask 5.3: 实现语句执行
  - [x] SubTask 5.4: 实现控制流处理（if、while、for、break）
  - [x] SubTask 5.5: 实现函数调用机制
  - [x] SubTask 5.6: 实现模块系统
  - [x] SubTask 5.7: 实现线程支持（t函数）

- [x] Task 6: 字符串处理函数实现
  - [x] SubTask 6.1: 实现ss函数（变量相加）
  - [x] SubTask 6.2: 实现sr函数（替换字符）
  - [x] SubTask 6.3: 实现sj函数（截取字符）
  - [x] SubTask 6.4: 实现sl函数（数据数组分割）
  - [ ] SubTask 6.5: 实现siof/slof函数（获取字符位置）
  - [x] SubTask 6.6: 实现ssg函数（截取字符）
  - [x] SubTask 6.7: 实现slg函数（获取字符长度）
  - [x] SubTask 6.8: 实现strim函数（去除头尾空格）
  - [x] SubTask 6.9: 实现slower/supper函数（大小写转换）

- [x] Task 7: 数学运算函数实现
  - [x] SubTask 7.1: 实现s+/s-/s*/s//s%函数（运算方式）
  - [x] SubTask 7.2: 实现s函数（计算表达式）
  - [x] SubTask 7.3: 实现s2函数（保留2位小数）
  - [x] SubTask 7.4: 实现sn函数（保留所有小数）
  - [x] SubTask 7.5: 实现sran函数（生成随机数）

- [x] Task 8: 数组操作函数实现
  - [x] SubTask 8.1: 实现nsz函数（创建数组）
  - [x] SubTask 8.2: 实现sgsz函数（访问数组元素）
  - [x] SubTask 8.3: 实现sssz函数（设置数组数据）
  - [x] SubTask 8.4: 实现sgszl函数（访问数组总行数）

- [x] Task 9: 文件操作函数实现（PC适配）
  - [x] SubTask 9.1: 实现fd函数（删除文件）
  - [x] SubTask 9.2: 实现fe函数（文件是否存在）
  - [x] SubTask 9.3: 实现fs函数（文件大小）
  - [x] SubTask 9.4: 实现fr函数（读取文本）
  - [x] SubTask 9.5: 实现fc函数（复制文件）
  - [x] SubTask 9.6: 实现fw函数（写入文本）
  - [x] SubTask 9.7: 实现fl函数（文件列表）
  - [x] SubTask 9.8: 实现ft函数（转移文件）
  - [x] SubTask 9.9: 实现fdir函数（获取根目录路径）
  - [ ] SubTask 9.10: 实现fuz/fuzs函数（解压zip）
  - [ ] SubTask 9.11: 实现fj函数（压缩文件）

- [ ] Task 10: 网络函数实现（PC适配）
  - [ ] SubTask 10.1: 实现hs函数（获取网页源码）
  - [ ] SubTask 10.2: 实现hd函数（下载文件）
  - [ ] SubTask 10.3: 实现huf函数（上传文件）

- [ ] Task 11: 数据类型转换函数实现
  - [ ] SubTask 11.1: 实现stobm函数（汉字转换编码字符）
  - [ ] SubTask 11.2: 实现sutf8to函数（UTF-8编码转换中文）
  - [ ] SubTask 11.3: 实现cast函数（强制转换数据类型）

- [x] Task 12: 时间函数实现
  - [x] SubTask 12.1: 实现time函数（当前时间）

- [ ] Task 13: JSON处理函数实现
  - [ ] SubTask 13.1: 实现json函数（json数据解析）

- [ ] Task 14: 正则表达式函数实现
  - [ ] SubTask 14.1: 实现se函数（正则表达式操作）

- [ ] Task 15: 数据库函数实现（PC适配）
  - [ ] SubTask 15.1: 实现sqlite函数（数据库操作）
  - [ ] SubTask 15.2: 实现sql函数（数据表操作）
  - [ ] SubTask 15.3: 实现sqlsele函数（查询数据操作）

- [ ] Task 16: 列表操作函数实现
  - [ ] SubTask 16.1: 实现aslist函数（添加数据列表）
  - [ ] SubTask 16.2: 实现sslist函数（设置列表数据）
  - [ ] SubTask 16.3: 实现gslist函数（访问列表数据）
  - [ ] SubTask 16.4: 实现gslistl函数（访问列表总数）
  - [ ] SubTask 16.5: 实现dslist函数（删除列表数据）
  - [ ] SubTask 16.6: 实现gslistsz/gslistis/gslistiof/gslistlof函数

- [x] Task 17: BeanShell集成与mjava模块加载
  - [x] SubTask 17.1: 集成BeanShell库到项目
  - [x] SubTask 17.2: 创建MjavaModuleLoader类，负责扫描和加载.mjava文件（标准Java代码）
  - [x] SubTask 17.3: 实现mjava文件解析，提取Java方法定义
  - [x] SubTask 17.4: 实现mjava方法通过BeanShell解释器执行
  - [x] SubTask 17.5: 实现java函数（调用Java代码方法，支持调用mjava方法）
  - [x] SubTask 17.6: 实现javax函数（带类对象的Java方法调用）
  - [x] SubTask 17.7: 实现javanew函数（创建Java对象）
  - [x] SubTask 17.8: 实现javags函数（获取Java变量）
  - [x] SubTask 17.9: 实现javass函数（设置Java变量）
  - [ ] SubTask 17.10: 实现javacb函数（自定义回调）
  - [x] SubTask 17.11: 实现cls函数（获取完整接口类）
  - [ ] SubTask 17.12: 实现clssm函数（获取类的所有接口）
  - [ ] SubTask 17.13: 实现loadjar函数（加载jar库）
  - [x] SubTask 17.14: 实现call函数对mjava模块的调用支持

- [x] Task 18: 输出函数实现
  - [x] SubTask 18.1: 实现syso函数（打印输出）
  - [x] SubTask 18.2: 实现tw函数（提示，PC适配为控制台输出）

- [x] Task 19: 其他函数实现
  - [x] SubTask 19.1: 实现stop函数（暂停代码）
  - [ ] SubTask 19.2: 实现sxb/shb函数（剪切板操作）
  - [ ] SubTask 19.3: 实现otob/btoo函数（字节组转换）
  - [ ] SubTask 19.4: 实现sot/sota函数（Socket网络通信）
  - [x] SubTask 19.5: 实现模块函数fn和call

- [x] Task 20: 函数注册和管理
  - [x] SubTask 20.1: 创建FunctionRegistry类，管理所有函数
  - [x] SubTask 20.2: 创建函数注解或元数据，记录函数信息
  - [x] SubTask 20.3: 实现函数查找和调用机制

- [x] Task 21: 错误处理和调试
  - [x] SubTask 21.1: 创建自定义异常类（LexerException、ParserException、RuntimeException）
  - [x] SubTask 21.2: 实现错误信息定位（行号、列号）
  - [x] SubTask 21.3: 实现运行时错误处理

- [x] Task 22: 主入口和测试
  - [x] SubTask 22.1: 创建Main类，提供命令行入口
  - [ ] SubTask 22.2: 创建测试脚本验证解释器功能
  - [ ] SubTask 22.3: 编写单元测试

# Task Dependencies
- [Task 2] depends on [Task 1]
- [Task 3] depends on [Task 2]
- [Task 5] depends on [Task 3, Task 4]
- [Task 6] depends on [Task 5]
- [Task 7] depends on [Task 5]
- [Task 8] depends on [Task 5]
- [Task 9] depends on [Task 5]
- [Task 10] depends on [Task 5]
- [Task 11] depends on [Task 5]
- [Task 12] depends on [Task 5]
- [Task 13] depends on [Task 5]
- [Task 14] depends on [Task 5]
- [Task 15] depends on [Task 5]
- [Task 16] depends on [Task 5]
- [Task 17] depends on [Task 5, Task 1.2]
- [Task 18] depends on [Task 5]
- [Task 19] depends on [Task 5]
- [Task 20] depends on [Task 6, Task 7, Task 8, Task 9, Task 10, Task 11, Task 12, Task 13, Task 14, Task 15, Task 16, Task 17, Task 18, Task 19]
- [Task 21] depends on [Task 2, Task 3, Task 5]
- [Task 22] depends on [Task 20, Task 21]
