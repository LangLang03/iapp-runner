# iApp Language Support for VSCode

为 iApp (裕语言) 提供 VSCode 语言支持。

## 功能特性

- **语法高亮** - 支持 `.iyu`, `.myu`, `.mjava`, `.iapp` 文件
- **LSP 集成** - 通过 Java LSP 提供智能补全、错误诊断、悬停提示等
- **代码补全** - 内置函数和用户自定义函数的智能补全
- **错误诊断** - 实时语法和语义错误检测
- **签名帮助** - 函数参数提示
- **文档符号** - 快速导航到变量和函数定义

## 安装

### 前置要求

1. **Java 17+** - 运行 LSP 服务器需要 Java 环境
2. **iAppLSP JAR** - 需要编译 iAppLSP 模块

### 从源码构建

1. 编译 iAppLSP JAR:
   ```bash
   cd /path/to/iapp-runner
   ./gradlew :iAppLSP:jar
   ```

2. 将 JAR 复制到扩展目录:
   ```bash
   mkdir -p vscode/jars
   cp iAppLSP/build/libs/iAppLSP-*.jar vscode/jars/
   ```

3. 编译 VSCode 扩展:
   ```bash
   cd vscode
   pnpm install
   pnpm run compile
   ```

4. 打包扩展:
   ```bash
   pnpm run package
   ```

5. 安装 `.vsix` 文件:
   - 在 VSCode 中按 `Ctrl+Shift+P`
   - 输入 `Extensions: Install from VSIX...`
   - 选择生成的 `.vsix` 文件

## 配置

在 VSCode 设置中配置以下选项：

| 设置 | 说明 | 默认值 |
|------|------|--------|
| `iapp.java.path` | Java 可执行文件路径 | `java` |
| `iapp.lsp.jarPath` | iAppLSP JAR 文件路径 (留空使用内置) | `""` |
| `iapp.lsp.enableYuWeb` | 启用 YuWeb 模块支持 | `false` |
| `iapp.lsp.debug` | 启用 LSP 调试模式 | `false` |

## 命令

| 命令 | 说明 |
|------|------|
| `iapp.restartServer` | 重启 iApp 语言服务器 |
| `iapp.showOutput` | 显示 iApp 输出面板 |

## 语法支持

### 关键字

- 变量声明: `s`, `ss`, `sss`
- 控制流: `f`, `else`, `w`, `for`, `break`, `endcode`
- 函数定义: `fn`, `end fn`
- 线程: `t`
- 布尔值: `true`, `false`, `null`

### 注释

- 单行注释: `// 这是注释`
- 块注释: `/. 这是块注释 ./`

### 变量作用域

- `s` - 局部变量
- `ss` - 界面变量
- `sss` - 全局变量

## 示例代码

```java
// 变量声明
s name = "Hello"
ss count = 100
sss globalVar = true

// 条件判断
f(name == "Hello")
{
    tw("条件成立")
}
else
{
    tw("条件不成立")
}

// 循环
for(1; 10)
{
    syso("循环中")
}

// 函数定义
fn myFunc(a, b)
    ss(a + b, result)
    syso(result)
end fn

// 调用函数
myFunc("Hello", "World")
```

## 开发

### 项目结构

```
vscode/
├── src/
│   └── extension.ts      # 扩展入口
├── syntaxes/
│   └── iapp.tmLanguage.json  # TextMate 语法
├── icons/                # 图标文件
├── language-configuration.json  # 语言配置
├── package.json          # 扩展配置
└── tsconfig.json         # TypeScript 配置
```

### 调试

1. 在 VSCode 中打开 `vscode` 目录
2. 按 `F5` 启动调试
3. 这将打开一个新的 VSCode 窗口，扩展已加载

## 许可证

GPL-3.0
