package cn.langlang.iapp.lsp.server;

import cn.langlang.iapp.lsp.core.LSContext;
import cn.langlang.iapp.lsp.core.analyzer.SemanticAnalyzer;
import cn.langlang.iapp.lsp.core.model.DiagnosticInfo;
import cn.langlang.iapp.lsp.core.model.FunctionInfo;
import cn.langlang.iapp.lsp.core.model.SymbolInfo;
import cn.langlang.iapp.lsp.core.model.VariableInfo;
import cn.langlang.iapp.lsp.core.provider.CompletionProvider;
import cn.langlang.iapp.lsp.core.provider.DiagnosticProvider;
import cn.langlang.iapp.lsp.core.provider.HoverProvider;
import cn.langlang.iapp.lsp.core.provider.SignatureProvider;
import cn.langlang.iapp.lsp.registry.FunctionCategory;
import cn.langlang.iapp.runtime.IFunction;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.TextDocumentService;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class IAppTextDocumentService implements TextDocumentService {
    private final LSContext context;
    private final Map<String, String> documentContents;
    private LanguageClient client;
    
    private final CompletionProvider completionProvider;
    private final HoverProvider hoverProvider;
    private final SignatureProvider signatureProvider;
    private final DiagnosticProvider diagnosticProvider;
    private final SemanticAnalyzer semanticAnalyzer;

    public IAppTextDocumentService(LSContext context) {
        this.context = context;
        this.documentContents = new HashMap<>();
        this.completionProvider = new CompletionProvider(context);
        this.hoverProvider = new HoverProvider(context);
        this.signatureProvider = new SignatureProvider(context);
        this.diagnosticProvider = new DiagnosticProvider(context);
        this.semanticAnalyzer = new SemanticAnalyzer(context);
    }

    public void connect(LanguageClient client) {
        this.client = client;
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        String text = params.getTextDocument().getText();
        documentContents.put(uri, text);
        publishDiagnostics(uri, text);
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        String text = params.getContentChanges().get(0).getText();
        documentContents.put(uri, text);
        publishDiagnostics(uri, text);
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        documentContents.remove(uri);
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        // No action needed
    }

    @Override
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams params) {
        String uri = params.getTextDocument().getUri();
        String text = documentContents.get(uri);
        
        if (text == null) {
            return CompletableFuture.completedFuture(Either.forLeft(Collections.emptyList()));
        }
        
        Position position = params.getPosition();
        String lineText = getLineText(text, position.getLine());
        int column = position.getCharacter();
        String prefix = getPrefix(lineText, column);
        
        List<CompletionItem> items = new ArrayList<>();
        
        String trimmedLine = lineText.trim();
        String beforeCursor = column > 0 ? lineText.substring(0, column) : "";
        String trimmedBefore = beforeCursor.trim();
        
        boolean isVariableDeclaration = trimmedBefore.matches("^(s|ss|sss)\\s*$");
        boolean isAfterVarDecl = trimmedBefore.matches("^(s|ss|sss)\\s+\\w+$");
        boolean isInExpression = isInExpressionContext(trimmedBefore);
        boolean isInFunctionCall = isInFunctionCallContext(trimmedBefore);
        boolean hasPrefix = prefix != null && !prefix.isEmpty();
        
        if (isVariableDeclaration) {
            // 在变量声明关键字后，不需要补全
        } else if (isAfterVarDecl) {
            // 在变量声明后，可能需要赋值表达式
            if (hasPrefix) {
                items.addAll(getVariableCompletionItems(uri, prefix, true));
            }
        } else if (isInFunctionCall || isInExpression) {
            // 在函数参数或表达式中，优先变量补全
            if (hasPrefix) {
                items.addAll(getVariableCompletionItems(uri, prefix, true));
                items.addAll(getFunctionCompletionItems(prefix, false));
            }
        } else {
            // 默认情况：代码片段优先，然后关键字，然后函数，最后变量
            // 只有有前缀时才提供补全
            if (hasPrefix) {
                items.addAll(getSnippetCompletionItems(prefix));
                items.addAll(getKeywordCompletionItems(prefix));
                items.addAll(getFunctionCompletionItems(prefix, false));
                items.addAll(getVariableCompletionItems(uri, prefix, false));
            }
        }
        
        CompletionList completionList = new CompletionList(items);
        return CompletableFuture.completedFuture(Either.forRight(completionList));
    }
    
    private boolean isInExpressionContext(String beforeCursor) {
        if (beforeCursor.isEmpty()) return false;
        
        // 在运算符后面
        if (beforeCursor.matches(".*[=+\\-*/<>!&|,]\\s*$")) {
            return true;
        }
        
        // 在控制语句后面 - 确保完整匹配关键字后跟括号
        // f(, w(, for(, t( 后面需要表达式
        if (beforeCursor.matches("^f\\s*\\(.*") || 
            beforeCursor.matches("^w\\s*\\(.*") ||
            beforeCursor.matches("^for\\s*\\(.*") ||
            beforeCursor.matches("^t\\s*\\(.*")) {
            return true;
        }
        
        return false;
    }
    
    private boolean isInFunctionCallContext(String beforeCursor) {
        int parenOpen = beforeCursor.lastIndexOf('(');
        if (parenOpen < 0) return false;
        
        int closeParen = beforeCursor.indexOf(')', parenOpen);
        return closeParen < 0;
    }

    @Override
    public CompletableFuture<CompletionItem> resolveCompletionItem(CompletionItem item) {
        return CompletableFuture.completedFuture(item);
    }

    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {
        String uri = params.getTextDocument().getUri();
        String text = documentContents.get(uri);
        
        if (text == null) {
            return CompletableFuture.completedFuture(null);
        }
        
        Position position = params.getPosition();
        String word = getWordAtPosition(text, position);
        
        if (word == null || word.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }
        
        String hoverText = hoverProvider.getHoverText(word);
        
        if (hoverText == null) {
            return CompletableFuture.completedFuture(null);
        }
        
        Hover hover = new Hover();
        MarkupContent content = new MarkupContent();
        content.setKind("markdown");
        content.setValue(hoverText);
        hover.setContents(content);
        
        return CompletableFuture.completedFuture(hover);
    }

    @Override
    public CompletableFuture<SignatureHelp> signatureHelp(SignatureHelpParams params) {
        String uri = params.getTextDocument().getUri();
        String text = documentContents.get(uri);
        
        if (text == null) {
            return CompletableFuture.completedFuture(new SignatureHelp());
        }
        
        Position position = params.getPosition();
        FunctionCallContext callContext = getFunctionCallContext(text, position);
        
        if (callContext == null || callContext.functionName == null) {
            return CompletableFuture.completedFuture(new SignatureHelp());
        }
        
        IFunction function = context.getFunction(callContext.functionName);
        if (function == null) {
            return CompletableFuture.completedFuture(new SignatureHelp());
        }
        
        SignatureHelp help = convertSignatureHelp(
            signatureProvider.getSignatureHelp(callContext.functionName, callContext.activeParameter)
        );
        
        return CompletableFuture.completedFuture(help);
    }

    @Override
    public CompletableFuture<List<Either<SymbolInformation, DocumentSymbol>>> documentSymbol(DocumentSymbolParams params) {
        String uri = params.getTextDocument().getUri();
        String text = documentContents.get(uri);
        
        if (text == null) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
        
        SemanticAnalyzer.AnalysisResult result = semanticAnalyzer.analyze(text);
        
        List<Either<SymbolInformation, DocumentSymbol>> symbols = new ArrayList<>();
        for (SymbolInfo symbolInfo : result.getSymbolTable().values()) {
            DocumentSymbol symbol = new DocumentSymbol();
            symbol.setName(symbolInfo.getName());
            symbol.setKind(convertSymbolKind(symbolInfo.getType()));
            symbol.setDetail(symbolInfo.getDetail());
            symbol.setRange(new Range(
                new Position(symbolInfo.getLine() - 1, symbolInfo.getColumn()),
                new Position(symbolInfo.getEndLine() - 1, symbolInfo.getEndColumn())
            ));
            symbol.setSelectionRange(symbol.getRange());
            symbols.add(Either.forRight(symbol));
        }
        
        return CompletableFuture.completedFuture(symbols);
    }

    @Override
    public CompletableFuture<List<? extends TextEdit>> formatting(DocumentFormattingParams params) {
        String uri = params.getTextDocument().getUri();
        String text = documentContents.get(uri);
        
        if (text == null) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
        
        String formatted = formatText(text);
        
        List<TextEdit> edits = new ArrayList<>();
        TextEdit edit = new TextEdit();
        edit.setRange(new Range(new Position(0, 0), new Position(Integer.MAX_VALUE, Integer.MAX_VALUE)));
        edit.setNewText(formatted);
        edits.add(edit);
        
        return CompletableFuture.completedFuture(edits);
    }

    @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition(DefinitionParams params) {
        String uri = params.getTextDocument().getUri();
        String text = documentContents.get(uri);
        
        if (text == null) {
            return CompletableFuture.completedFuture(Either.forLeft(Collections.emptyList()));
        }
        
        Position position = params.getPosition();
        String word = getWordAtPosition(text, position);
        
        if (word == null || word.isEmpty()) {
            return CompletableFuture.completedFuture(Either.forLeft(Collections.emptyList()));
        }
        
        SemanticAnalyzer.AnalysisResult result = semanticAnalyzer.analyze(text);
        VariableInfo variable = result.getVariableProvider().getVariable(word);
        
        if (variable != null && variable.getLine() > 0) {
            Location location = new Location();
            location.setUri(uri);
            location.setRange(new Range(
                new Position(variable.getLine() - 1, variable.getColumn()),
                new Position(variable.getLine() - 1, variable.getColumn() + word.length())
            ));
            return CompletableFuture.completedFuture(Either.forLeft(Collections.singletonList(location)));
        }
        
        SymbolInfo symbol = result.getSymbolTable().get(word);
        if (symbol != null && symbol.getLine() > 0) {
            Location location = new Location();
            location.setUri(uri);
            location.setRange(new Range(
                new Position(symbol.getLine() - 1, symbol.getColumn()),
                new Position(symbol.getLine() - 1, symbol.getColumn() + word.length())
            ));
            return CompletableFuture.completedFuture(Either.forLeft(Collections.singletonList(location)));
        }
        
        return CompletableFuture.completedFuture(Either.forLeft(Collections.emptyList()));
    }

    @Override
    public CompletableFuture<List<? extends Location>> references(ReferenceParams params) {
        String uri = params.getTextDocument().getUri();
        String text = documentContents.get(uri);
        
        if (text == null) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
        
        Position position = params.getPosition();
        String word = getWordAtPosition(text, position);
        
        if (word == null || word.isEmpty()) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
        
        List<Location> locations = new ArrayList<>();
        String[] lines = text.split("\n");
        
        for (int lineNum = 0; lineNum < lines.length; lineNum++) {
            String line = lines[lineNum];
            int col = 0;
            int idx;
            while ((idx = line.indexOf(word, col)) != -1) {
                boolean validStart = (idx == 0 || !Character.isLetterOrDigit(line.charAt(idx - 1)));
                boolean validEnd = (idx + word.length() >= line.length() || !Character.isLetterOrDigit(line.charAt(idx + word.length())));
                
                if (validStart && validEnd) {
                    Location location = new Location();
                    location.setUri(uri);
                    location.setRange(new Range(
                        new Position(lineNum, idx),
                        new Position(lineNum, idx + word.length())
                    ));
                    locations.add(location);
                }
                col = idx + 1;
            }
        }
        
        return CompletableFuture.completedFuture(locations);
    }

    private void publishDiagnostics(String uri, String text) {
        List<DiagnosticInfo> diagnostics = diagnosticProvider.getDiagnostics(text);
        
        List<Diagnostic> lspDiagnostics = diagnostics.stream()
            .map(this::convertDiagnostic)
            .collect(Collectors.toList());
        
        if (client != null) {
            client.publishDiagnostics(new PublishDiagnosticsParams(uri, lspDiagnostics));
        }
    }

    private Diagnostic convertDiagnostic(DiagnosticInfo info) {
        Diagnostic diagnostic = new Diagnostic();
        diagnostic.setRange(new Range(
            new Position(info.getLine() - 1, info.getColumn() - 1),
            new Position(info.getEndLine() - 1, info.getEndColumn() - 1)
        ));
        diagnostic.setSeverity(convertSeverity(info.getSeverity()));
        diagnostic.setMessage(info.getMessage());
        diagnostic.setSource(info.getSource() != null ? info.getSource() : "iAppLSP");
        if (info.getCode() != null) {
            diagnostic.setCode(info.getCode());
        }
        return diagnostic;
    }

    private DiagnosticSeverity convertSeverity(DiagnosticInfo.Severity severity) {
        switch (severity) {
            case ERROR:
                return DiagnosticSeverity.Error;
            case WARNING:
                return DiagnosticSeverity.Warning;
            case INFORMATION:
                return DiagnosticSeverity.Information;
            case HINT:
                return DiagnosticSeverity.Hint;
            default:
                return DiagnosticSeverity.Information;
        }
    }

    private SymbolKind convertSymbolKind(SymbolInfo.SymbolType type) {
        switch (type) {
            case FUNCTION:
                return SymbolKind.Function;
            case VARIABLE:
                return SymbolKind.Variable;
            case PARAMETER:
                return SymbolKind.TypeParameter;
            case USER_FUNCTION:
                return SymbolKind.Method;
            default:
                return SymbolKind.Variable;
        }
    }

    private SignatureHelp convertSignatureHelp(cn.langlang.iapp.lsp.core.provider.SignatureProvider.SignatureHelp help) {
        SignatureHelp lspHelp = new SignatureHelp();
        
        List<SignatureInformation> signatures = new ArrayList<>();
        for (cn.langlang.iapp.lsp.core.provider.SignatureProvider.SignatureInformation info : help.getSignatures()) {
            SignatureInformation sigInfo = new SignatureInformation();
            sigInfo.setLabel(info.getLabel());
            sigInfo.setDocumentation(info.getDocumentation());
            
            List<ParameterInformation> params = new ArrayList<>();
            if (info.getParameters() != null) {
                for (cn.langlang.iapp.lsp.core.provider.SignatureProvider.ParameterInformation param : info.getParameters()) {
                    ParameterInformation paramInfo = new ParameterInformation();
                    paramInfo.setLabel(param.getLabel());
                    paramInfo.setDocumentation(param.getDocumentation());
                    params.add(paramInfo);
                }
            }
            sigInfo.setParameters(params);
            signatures.add(sigInfo);
        }
        
        lspHelp.setSignatures(signatures);
        lspHelp.setActiveSignature(help.getActiveSignature());
        lspHelp.setActiveParameter(help.getActiveParameter());
        
        return lspHelp;
    }

    private List<CompletionItem> getFunctionCompletionItems(String prefix) {
        return getFunctionCompletionItems(prefix, false);
    }
    
    private List<CompletionItem> getFunctionCompletionItems(String prefix, boolean lowPriority) {
        List<CompletionItem> items = new ArrayList<>();
        List<cn.langlang.iapp.lsp.core.provider.CompletionProvider.CompletionItem> completions = 
            completionProvider.getFunctionCompletions(prefix);
        
        for (cn.langlang.iapp.lsp.core.provider.CompletionProvider.CompletionItem comp : completions) {
            CompletionItem item = new CompletionItem();
            item.setLabel(comp.getLabel());
            item.setKind(CompletionItemKind.Function);
            item.setDetail(comp.getDetail());
            
            MarkupContent doc = new MarkupContent();
            doc.setKind("markdown");
            doc.setValue(comp.getDocumentation());
            item.setDocumentation(doc);
            
            item.setInsertText(comp.getInsertText());
            item.setInsertTextFormat(InsertTextFormat.Snippet);
            item.setSortText(lowPriority ? "2" + comp.getLabel() : comp.getSortText());
            
            items.add(item);
        }
        
        return items;
    }

    private List<CompletionItem> getKeywordCompletionItems(String prefix) {
        List<CompletionItem> items = new ArrayList<>();
        List<cn.langlang.iapp.lsp.core.provider.CompletionProvider.CompletionItem> keywords = 
            completionProvider.getKeywordCompletions(prefix);
        
        for (cn.langlang.iapp.lsp.core.provider.CompletionProvider.CompletionItem kw : keywords) {
            CompletionItem item = new CompletionItem();
            item.setLabel(kw.getLabel());
            item.setKind(CompletionItemKind.Keyword);
            item.setDetail(kw.getDetail());
            item.setInsertText(kw.getInsertText());
            item.setSortText(kw.getSortText());
            items.add(item);
        }
        
        return items;
    }
    
    private List<CompletionItem> getSnippetCompletionItems(String prefix) {
        List<CompletionItem> items = new ArrayList<>();
        List<cn.langlang.iapp.lsp.core.provider.CompletionProvider.CompletionItem> snippets = 
            completionProvider.getSnippetCompletions(prefix);
        
        for (cn.langlang.iapp.lsp.core.provider.CompletionProvider.CompletionItem snippet : snippets) {
            CompletionItem item = new CompletionItem();
            item.setLabel(snippet.getLabel());
            item.setKind(CompletionItemKind.Snippet);
            item.setDetail(snippet.getDetail());
            
            MarkupContent doc = new MarkupContent();
            doc.setKind("markdown");
            doc.setValue("```iapp\n" + snippet.getInsertText() + "\n```");
            item.setDocumentation(doc);
            
            item.setInsertText(snippet.getInsertText());
            item.setInsertTextFormat(InsertTextFormat.Snippet);
            item.setSortText(snippet.getSortText());
            items.add(item);
        }
        
        return items;
    }

    private List<CompletionItem> getVariableCompletionItems(String uri, String prefix) {
        return getVariableCompletionItems(uri, prefix, false);
    }
    
    private List<CompletionItem> getVariableCompletionItems(String uri, String prefix, boolean highPriority) {
        List<CompletionItem> items = new ArrayList<>();
        String text = documentContents.get(uri);
        
        if (text != null) {
            SemanticAnalyzer.AnalysisResult result = semanticAnalyzer.analyze(text);
            List<VariableInfo> variables = result.getVariableProvider().getVariablesByPrefix(prefix);
            
            for (VariableInfo var : variables) {
                CompletionItem item = new CompletionItem();
                item.setLabel(var.getDisplayName());
                item.setKind(CompletionItemKind.Variable);
                item.setDetail(getScopeDisplayName(var.getScope()));
                item.setInsertText(var.getName());
                item.setSortText(highPriority ? "0" + var.getName() : "2" + var.getName());
                items.add(item);
            }
        }
        
        return items;
    }

    private String getScopeDisplayName(cn.langlang.iapp.lexer.TokenType scope) {
        switch (scope) {
            case KEYWORD_SS:
                return "界面变量 (ss)";
            case KEYWORD_SSS:
                return "全局变量 (sss)";
            default:
                return "局部变量 (s)";
        }
    }

    private String getLineText(String text, int line) {
        String[] lines = text.split("\n");
        if (line >= 0 && line < lines.length) {
            return lines[line];
        }
        return "";
    }

    private String getPrefix(String line, int column) {
        if (column > line.length()) {
            column = line.length();
        }
        String beforeCursor = line.substring(0, column);
        
        int start = beforeCursor.length() - 1;
        while (start >= 0 && Character.isLetterOrDigit(beforeCursor.charAt(start))) {
            start--;
        }
        
        String prefix = beforeCursor.substring(start + 1);
        
        return prefix;
    }
    
    private boolean shouldProvideCompletions(String prefix) {
        return prefix != null && !prefix.isEmpty();
    }

    private String getWordAtPosition(String text, Position position) {
        String line = getLineText(text, position.getLine());
        int column = position.getCharacter();
        
        if (column > line.length()) {
            column = line.length();
        }
        
        int start = column;
        while (start > 0 && Character.isLetterOrDigit(line.charAt(start - 1))) {
            start--;
        }
        
        int end = column;
        while (end < line.length() && Character.isLetterOrDigit(line.charAt(end))) {
            end++;
        }
        
        if (start < end) {
            return line.substring(start, end);
        }
        return "";
    }

    private FunctionCallContext getFunctionCallContext(String text, Position position) {
        String line = getLineText(text, position.getLine());
        int column = position.getCharacter();
        
        int parenOpen = line.lastIndexOf('(', column);
        if (parenOpen < 0) {
            return null;
        }
        
        int funcNameEnd = parenOpen;
        while (funcNameEnd > 0 && Character.isWhitespace(line.charAt(funcNameEnd - 1))) {
            funcNameEnd--;
        }
        
        int funcNameStart = funcNameEnd;
        while (funcNameStart > 0 && Character.isLetterOrDigit(line.charAt(funcNameStart - 1))) {
            funcNameStart--;
        }
        
        String funcName = line.substring(funcNameStart, funcNameEnd);
        
        int activeParam = 0;
        for (int i = parenOpen + 1; i < column && i < line.length(); i++) {
            if (line.charAt(i) == ',') {
                activeParam++;
            }
        }
        
        FunctionCallContext ctx = new FunctionCallContext();
        ctx.functionName = funcName;
        ctx.activeParameter = activeParam;
        return ctx;
    }

    private String formatText(String text) {
        StringBuilder result = new StringBuilder();
        String[] lines = text.split("\n");
        int indentLevel = 0;
        
        for (String line : lines) {
            String trimmed = line.trim();
            
            if (trimmed.startsWith("end") || trimmed.startsWith("else")) {
                indentLevel = Math.max(0, indentLevel - 1);
            }
            
            for (int i = 0; i < indentLevel; i++) {
                result.append("    ");
            }
            result.append(trimmed);
            result.append("\n");
            
            if (trimmed.endsWith("{") || trimmed.startsWith("f(") || 
                trimmed.startsWith("w(") || trimmed.startsWith("for(") ||
                trimmed.startsWith("fn ") || trimmed.startsWith("t(")) {
                indentLevel++;
            }
        }
        
        return result.toString().trim();
    }

    private static class FunctionCallContext {
        String functionName;
        int activeParameter;
    }
}
