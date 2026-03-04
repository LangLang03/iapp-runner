# iAppV3 解释器开发规范

## Why

iAppV3是一种专用于安卓开发的脚本语言（裕语言），不支持PC环境。本项目旨在使用Java 25开发一个iAppV3解释器，使其能够在PC环境中运行iAppV3脚本代码，同时移除不支持PC环境的函数，并通过BeanShell支持Java代码执行。

## What Changes

* 创建iAppV3脚本语言的词法分析器（Lexer）

* 创建iAppV3脚本语言的语法分析器（Parser）

* 创建iAppV3脚本语言的解释器引擎

* 实现变量管理系统（局部变量、界面变量、全局变量）

* 实现支持的函数库

* 移除不支持PC环境的函数

* 集成BeanShell用于执行Java代码

* 添加build.gradle中的BeanShell依赖配置

## Impact

* Affected specs: 新建解释器核心模块

* Affected code: src/main/java 目录下的所有新建代码

## ADDED Requirements

### Requirement: 词法分析器

系统应提供词法分析器，能够将iAppV3脚本源代码转换为Token序列。

#### Scenario: 成功解析变量声明

* **WHEN** 输入代码 `s a = 123`

* **THEN** 生成Token序列：\[S, IDENTIFIER(a), EQUALS, NUMBER(123)]

#### Scenario: 成功解析函数调用

* **WHEN** 输入代码 `tw("你好")`

* **THEN** 生成Token序列：\[TW, LPAREN, STRING(你好), RPAREN]

### Requirement: 语法分析器

系统应提供语法分析器，能够将Token序列转换为抽象语法树（AST）。

#### Scenario: 成功解析if语句

* **WHEN** 输入Token序列表示if判断语句

* **THEN** 生成IfStatement AST节点，包含条件表达式和代码块

#### Scenario: 成功解析while循环

* **WHEN** 输入Token序列表示while循环语句

* **THEN** 生成WhileStatement AST节点，包含条件和循环体

### Requirement: 解释器引擎

系统应提供解释器引擎，能够执行AST并产生运行结果。

#### Scenario: 成功执行变量赋值

* **WHEN** 执行代码 `s a = 123`

* **THEN** 变量a的值为123

#### Scenario: 成功执行算术运算

* **WHEN** 执行代码 `s a = 10 + 20`

* **THEN** 变量a的值为30

### Requirement: 变量管理

系统应支持三种变量作用域：局部变量(s)、界面变量(ss)、全局变量(sss)。

#### Scenario: 局部变量操作

* **WHEN** 在事件中声明局部变量 `s a = 1`

* **THEN** 该变量仅在该事件中可访问

#### Scenario: 全局变量操作

* **WHEN** 声明全局变量 `sss b = 2`

* **THEN** 该变量在整个应用中均可访问

### Requirement: 支持的函数

系统应实现以下类别的函数：

#### 字符串处理函数

* ss: 变量相加

* sr: 替换字符

* sj: 截取字符

* sl: 数据数组分割

* siof: 获取字符位置

* slof: 获取字符位置

* ssg: 截取字符

* slg: 获取字符长度

* strim: 去除头尾空格

* slower: 转换为小写

* supper: 转换为大写

#### 数学运算函数

* s+、s-、s\*、s/、s%: 运算方式

* s: 计算表达式

* s2: 计算表达式（保留2位小数）

* sn: 计算表达式（保留所有小数）

* sran: 生成范围随机数

#### 数组操作函数

* nsz: 创建数组

* sgsz: 访问数组元素

* sssz: 设置数组数据

* sgszl: 访问数组总行数

#### 文件操作函数（PC适配）

* fd: 删除文件

* fe: 文件是否存在

* fs: 文件大小

* fr: 读取文本

* fc: 复制文件

* fw: 写入文本

* fl: 文件列表

* ft: 转移文件

* fdir: 获取根目录路径

* fuz: 解压zip部分文件

* fuzs: 解压整个zip

* fj: 压缩文件或文件夹

#### 控制流函数

* f: 判断语句

* w: 循环

* for: for循环

* t: 新线程

* break: 跳出循环

* endcode: 结束执行

#### 输出函数

* syso: 打印输出

* tw: 提示（PC环境适配为控制台输出）

#### 网络函数（PC适配）

* hs: 获取网页源码

* hd: 下载文件

* huf: 上传文件

#### 数据类型转换函数

* stobm: 汉字转换编码字符

* sutf8to: UTF-8编码字符转换中文

* cast: 强制转换数据类型

#### 时间函数

* time: 当前时间

#### JSON处理函数

* json: json数据解析

#### 正则表达式函数

* se: 正则表达式操作

#### 数据库函数（PC适配）

* sqlite: 数据库操作

* sql: 数据表操作

* sqlsele: 查询数据操作

#### 列表操作函数

* aslist: 添加数据列表

* sslist: 数据列表设置数据

* gslist: 数据列表访问数据

* gslistl: 数据列表访问数据总数

* dslist: 数据列表删除指定数据

* gslistsz: 列表数据转化为数组

* gslistis: 列表数据检查是否存在指定数据

* gslistiof: 列表数据从头开始检查

* gslistlof: 列表数据从尾开始检查

#### Java交互函数

* java: 调用java代码方法

* javax: 调用java代码方法

* javanew: 创建Java对象

* javags: 获取Java变量

* javass: 设置Java变量

* javacb: 自定义回调

* cls: 获取完整接口类

* clssm: 获取类的所有接口

* loadjar: 加载jar库

* loadso: 加载动态库

#### 模块函数

* fn: 模块与函数定义

* call: 交互式语言调用

#### 其他函数

* stop: 暂停代码

* sxb: 写入剪切板

* shb: 获取剪切板

* otob: 转换为字节组

* btoo: 字节组还原

* sot: Socket网络通信

* sota: 单个Socket通信操作

### Requirement: 不支持的函数（需移除）

以下函数因依赖Android环境或PC环境不支持，将在解释器中移除或标记为不可用：

#### Android界面相关函数

* ug: 获取控件属性

* us: 设置控件属性

* uigo: 跳转界面

* utw: 弹出界面

* endutw: 关闭弹出界面

* end: 结束界面

* ends: 显示桌面

* ssj: 设置或修改控件事件代码

* ula: 列表操作内容

* uls: 列表显示内容

* ulag: 获取列表内容数据

* ulas: 更新列表内容数据

* nvw: 创建动态控件

* uall: 获取子控件

* urvw: 移除控件

* gvs: 获取控件对象

* addv: 加载界面

* uht: 滑动窗体控制

* utb: Toolbar工具栏设置

* tws: 弹窗提醒

* yul: 加载yul布局

#### Android多媒体函数

* bfm: 播放音频

* bfms: 音频控制

* bfv: 播放视频

* bfvs: 播放视频

* bfvss: 播放视频控制

* bly: 录制音频

* blp: 录制屏幕

#### Android系统功能函数

* usms: 发送短信

* ucall: 拨打电话

* uqr: 二维码扫描

* usg: 闪光灯操作

* uzd: 震动器操作

* usxq: 开启前置摄像头

* usxh: 开启后置摄像头

* usx: 摄像头操作

* ujp: 截取屏幕

* uapp: 打开App应用

* uapplist: 获取App列表

* uapplistgo: 获取正在运行的App列表

* uninapp: 卸载应用

* uycl: 隐藏状态栏/修改状态栏颜色

* ushsp: 设置横屏或竖屏

* endkeyboard: 强制隐藏虚拟键盘

* uxf: 显示悬浮窗

* ftz: 发送通知栏

* sdeg: 启动调试模式

#### Android设备信息函数

* swh: 获取屏幕分辨率

* sjxx: 获取设备信息

* simsi: 获取设备imsi

* simei: 获取设备imei

#### Android浏览器相关函数

* hw: 访问网页

* hws: 系统浏览器访问网页

* hsas: 开启浏览器控件交互

* has: 裕语言交互JavaScript语言

#### Android动画函数

* dha: 渐变透明度动画

* dhs: 渐变尺寸伸缩动画

* dht: 画面位置移动动画

* dhr: 画面旋转动画

* dhset: 动画集合

* dhas: 队列动画执行

* dhast: 队列动画集合

* dh: 动画控制

* dhon: 动画监听事件

* dhb: 动画背景

#### Android图像处理函数

* sbp: 图像分割

* bfs: 保存图像

* tot: 获取控件图标

* tzz: 图像旋转

* tsf: 图像缩放

* tfz: 图像反转

* tcc: 获取图像变量尺寸

* nuibs: 背景选择器

* ngde: 背景调控器

#### Android其他函数

* sit: 目标的设置

* uit: 目标的执行

* git: 目标获取参数

* fo: 打开文件

* lan: 跳转界面动画

* ufnsui: 线程更新界面

* tts: 文本转换语音

* res: 安装包资源管理器

* zj: 组件控制

* zdp/zpd/zps/zsp: 单位转换

* rps: 请求权限

* hdfl: 文件下载器

* hdfla: 文件下载器增加项

* hdd: 配置下载管理器

* hdda: 下载管理器增加项

* hddgl: 获取下载管理器列表

* hddg: 获取下载项属性

* hdds: 设置下载项属性

* hdduigo: 跳转至下载管理器

* usjxm: 手机休眠

### Requirement: BeanShell集成与mjava模块加载
系统应集成BeanShell库作为Java代码执行引擎，并支持mjava模块文件的加载机制。

#### mjava模块加载机制
- 解释器启动时应扫描并加载所有.mjava文件（标准Java代码文件）
- 每个mjava文件对应一个模块，文件名作为模块名
- mjava文件中定义的Java方法可以被iAppV3代码通过java、javax、call等函数调用
- BeanShell作为执行引擎，负责解释执行mjava中的Java代码

#### mjava文件特点
- mjava文件内容为标准Java代码语法
- 方法定义使用Java标准语法（public/private、返回类型、参数类型等）
- 支持Java标准库调用
- 可以定义多个方法

#### Scenario: 加载mjava模块

* **WHEN** 解释器启动时发现module.mjava文件

* **THEN** 将该文件内容加载到BeanShell解释器中，模块名为"module"

#### Scenario: 调用mjava方法

* **WHEN** iAppV3代码调用 `call(a, "mjava", "module.methodName", param1, param2)`

* **THEN** 执行module.mjava中定义的methodName方法，并返回结果

#### Scenario: 通过java函数调用mjava

* **WHEN** iAppV3代码通过java函数调用mjava中定义的方法

* **THEN** 通过BeanShell解释器执行对应方法并返回结果

#### mjava文件格式示例

mjava文件是标准Java代码文件，定义方法供iAppV3调用：

```java
// module.mjava 文件内容示例（标准Java代码）
public String methodName(String param1, String param2) {
    // Java代码
    return param1 + param2;
}

public int add(int a, int b) {
    return a + b;
}

public void printHello() {
    System.out.println("Hello from mjava!");
}
```

#### iAppV3调用mjava示例

```
// 通过call函数调用mjava模块方法
call(result, "mjava", "module.methodName", "a", "b")

// 通过java函数调用mjava方法
java(result, null, "module.methodName", "String", "a", "String", "b")

// 调用带返回值的方法
call(sum, "mjava", "module.add", 10, 20)
```

### Requirement: 语法规范支持

系统应支持iAppV3的完整语法规范：

#### 变量声明

* `s a`: 声明局部变量

* `s a = 123`: 声明并赋值

* `ss a = "123"`: 声明界面变量

* `sss b = a`: 声明全局变量

#### 注释

* `//`: 单行注释

* `/. ./`: 多行注释

#### 运算符

* `==`: 是否对等

* `!=`: 是否不等于

* `>=`: 是否大于或等于

* `<=`: 是否小于或等于

* `>`: 是否大于

* `<`: 是否小于

* `?*`: 字符串开头是否相同

* `*?`: 字符串结尾是否相同

* `?`: 字符串是否被包含

* `||`: 或者

* `&&`: 并且

* `!`: 反意

#### 控制结构

* `f(条件) { } else f(条件) { } else { }`: 判断语句

* `w(条件) { }`: while循环

* `for(起始; 结束) { }`: for循环

* `for(变量; 数组) { }`: 遍历数组

#### 模块定义

* `fn 函数名(参数) ... end fn`: 函数定义

## MODIFIED Requirements

无

## REMOVED Requirements

无
