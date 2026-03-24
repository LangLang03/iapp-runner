package cn.langlang.iapp.lsp.header;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HeaderParser {
    private static final Logger logger = LoggerFactory.getLogger(HeaderParser.class);
    
    private static final Pattern DOC_BLOCK_START = Pattern.compile("^\\s*/\\*\\*");
    private static final Pattern DOC_BLOCK_END = Pattern.compile("\\*/\\s*$");
    private static final Pattern FUNCTION_TAG = Pattern.compile("@function\\s+(\\w+)");
    private static final Pattern CATEGORY_TAG = Pattern.compile("@category\\s+(.+)");
    private static final Pattern PARAM_TAG = Pattern.compile("@param\\s+(\\w+)(?:\\s+(\\w+))?(?:\\s+-\\s+(.+))?");
    private static final Pattern RETURNS_TAG = Pattern.compile("@returns?\\s+(.+)");
    private static final Pattern DESCRIPTION_TAG = Pattern.compile("@description\\s+(.+)");
    private static final Pattern EXAMPLE_TAG = Pattern.compile("@example\\s*");
    private static final Pattern YUWEB_TAG = Pattern.compile("@yuweb\\s*");
    private static final Pattern FN_DECLARATION = Pattern.compile("^\\s*fn\\s+(\\w+)\\s*\\(([^)]*)\\)");
    
    private static final Pattern SNIPPET_TAG = Pattern.compile("@snippet\\s+(.+)");
    private static final Pattern PREFIX_TAG = Pattern.compile("@prefix\\s+(.+)");
    private static final Pattern BODY_TAG = Pattern.compile("@body\\s*");

    public HeaderFile parse(String filePath, Reader reader) throws IOException {
        HeaderFile headerFile = new HeaderFile(filePath);
        
        if (filePath != null && filePath.contains("yuweb")) {
            headerFile.setYuWeb(true);
        }
        
        String line;
        StringBuilder currentDocBlock = null;
        boolean inDocBlock = false;
        boolean inExample = false;
        boolean inSnippetBody = false;
        HeaderFile.SnippetInfo currentSnippet = null;
        
        try (BufferedReader br = new BufferedReader(reader)) {
            while ((line = br.readLine()) != null) {
                if (inSnippetBody) {
                    if (line.trim().equals("*/")) {
                        inSnippetBody = false;
                        if (currentSnippet != null) {
                            headerFile.addSnippet(currentSnippet);
                            currentSnippet = null;
                        }
                    } else if (currentSnippet != null) {
                        String currentBody = currentSnippet.getBody();
                        if (currentBody == null) {
                            currentSnippet.setBody(line);
                        } else {
                            currentSnippet.setBody(currentBody + "\n" + line);
                        }
                    }
                    continue;
                }
                
                if (!inDocBlock && DOC_BLOCK_START.matcher(line).find()) {
                    inDocBlock = true;
                    currentDocBlock = new StringBuilder();
                    int endIndex = line.indexOf("*/");
                    if (endIndex != -1) {
                        String content = line.substring(0, endIndex);
                        currentDocBlock.append(content).append("\n");
                        inDocBlock = false;
                    } else {
                        currentDocBlock.append(line).append("\n");
                    }
                    continue;
                }
                
                if (inDocBlock) {
                    currentDocBlock.append(line).append("\n");
                    
                    if (DOC_BLOCK_END.matcher(line).find()) {
                        inDocBlock = false;
                        inExample = false;
                    }
                    continue;
                }
                
                Matcher fnMatcher = FN_DECLARATION.matcher(line);
                if (fnMatcher.find() && currentDocBlock != null) {
                    String funcName = fnMatcher.group(1);
                    String params = fnMatcher.group(2);
                    
                    HeaderFunctionInfo funcInfo = parseDocBlock(currentDocBlock.toString(), funcName, params);
                    if (funcInfo != null) {
                        funcInfo.setYuWeb(headerFile.isYuWeb());
                        headerFile.addFunction(funcInfo);
                    }
                    
                    currentDocBlock = null;
                    continue;
                }
                
                if (currentDocBlock != null) {
                    HeaderFile.SnippetInfo snippet = parseSnippetBlock(currentDocBlock.toString());
                    if (snippet != null) {
                        if (snippet.getBody() != null) {
                            headerFile.addSnippet(snippet);
                        } else {
                            currentSnippet = snippet;
                            inSnippetBody = true;
                        }
                    }
                    currentDocBlock = null;
                }
            }
        }
        
        logger.debug("Parsed header file: {} with {} functions and {} snippets", 
            filePath, headerFile.getFunctionCount(), headerFile.getSnippetCount());
        
        return headerFile;
    }

    private HeaderFunctionInfo parseDocBlock(String docBlock, String funcName, String params) {
        HeaderFunctionInfo funcInfo = new HeaderFunctionInfo();
        funcInfo.setName(funcName);
        
        List<HeaderFunctionInfo.ParamInfo> paramList = parseParams(params);
        funcInfo.setParams(paramList);
        
        Matcher funcMatcher = FUNCTION_TAG.matcher(docBlock);
        if (funcMatcher.find()) {
            funcInfo.setName(funcMatcher.group(1));
        }
        
        Matcher categoryMatcher = CATEGORY_TAG.matcher(docBlock);
        if (categoryMatcher.find()) {
            funcInfo.setCategory(categoryMatcher.group(1).trim());
        }
        
        Matcher returnsMatcher = RETURNS_TAG.matcher(docBlock);
        if (returnsMatcher.find()) {
            funcInfo.setReturnType(returnsMatcher.group(1).trim());
        }
        
        Matcher descMatcher = DESCRIPTION_TAG.matcher(docBlock);
        if (descMatcher.find()) {
            funcInfo.setDescription(descMatcher.group(1).trim());
        }
        
        Matcher yuwebMatcher = YUWEB_TAG.matcher(docBlock);
        if (yuwebMatcher.find()) {
            funcInfo.setYuWeb(true);
        }
        
        List<HeaderFunctionInfo.ParamInfo> docParams = new ArrayList<>();
        String[] lines = docBlock.split("\n");
        boolean inExampleBlock = false;
        StringBuilder exampleBuilder = new StringBuilder();
        
        for (String line : lines) {
            String trimmedLine = line.trim();
            if (trimmedLine.startsWith("*")) {
                trimmedLine = trimmedLine.substring(1).trim();
            }
            
            if (EXAMPLE_TAG.matcher(trimmedLine).find()) {
                inExampleBlock = true;
                continue;
            }
            
            if (inExampleBlock) {
                if (trimmedLine.startsWith("@") && !trimmedLine.startsWith("@example")) {
                    inExampleBlock = false;
                } else if (trimmedLine.equals("*/")) {
                    inExampleBlock = false;
                } else {
                    if (exampleBuilder.length() > 0) {
                        exampleBuilder.append("\n");
                    }
                    exampleBuilder.append(trimmedLine);
                    continue;
                }
            }
            
            Matcher paramMatcher = PARAM_TAG.matcher(trimmedLine);
            if (paramMatcher.find()) {
                String paramName = paramMatcher.group(1);
                String paramType = paramMatcher.group(2);
                String paramDesc = paramMatcher.group(3);
                
                HeaderFunctionInfo.ParamInfo paramInfo = findParamByName(paramList, paramName);
                if (paramInfo == null) {
                    paramInfo = new HeaderFunctionInfo.ParamInfo(paramName);
                }
                
                if (paramType != null && !paramType.equals("-")) {
                    paramInfo.setType(paramType);
                }
                if (paramDesc != null) {
                    paramInfo.setDescription(paramDesc.trim());
                }
                
                docParams.add(paramInfo);
            }
        }
        
        if (!docParams.isEmpty()) {
            funcInfo.setParams(docParams);
        }
        
        if (exampleBuilder.length() > 0) {
            funcInfo.setExample(exampleBuilder.toString());
        }
        
        return funcInfo;
    }

    private List<HeaderFunctionInfo.ParamInfo> parseParams(String params) {
        List<HeaderFunctionInfo.ParamInfo> paramList = new ArrayList<>();
        
        if (params == null || params.trim().isEmpty()) {
            return paramList;
        }
        
        String[] paramArray = params.split(",");
        for (String param : paramArray) {
            String trimmedParam = param.trim();
            if (!trimmedParam.isEmpty()) {
                paramList.add(new HeaderFunctionInfo.ParamInfo(trimmedParam));
            }
        }
        
        return paramList;
    }

    private HeaderFunctionInfo.ParamInfo findParamByName(List<HeaderFunctionInfo.ParamInfo> params, String name) {
        if (params == null || name == null) {
            return null;
        }
        
        for (HeaderFunctionInfo.ParamInfo param : params) {
            if (name.equals(param.getName())) {
                return param;
            }
        }
        
        return null;
    }

    private HeaderFile.SnippetInfo parseSnippetBlock(String docBlock) {
        Matcher snippetMatcher = SNIPPET_TAG.matcher(docBlock);
        if (!snippetMatcher.find()) {
            return null;
        }
        
        HeaderFile.SnippetInfo snippet = new HeaderFile.SnippetInfo();
        snippet.setLabel(snippetMatcher.group(1).trim());
        
        Matcher prefixMatcher = PREFIX_TAG.matcher(docBlock);
        if (prefixMatcher.find()) {
            snippet.setPrefix(prefixMatcher.group(1).trim());
        }
        
        Matcher bodyMatcher = BODY_TAG.matcher(docBlock);
        if (bodyMatcher.find()) {
            int bodyStart = bodyMatcher.end();
            int bodyEnd = docBlock.indexOf("*/", bodyStart);
            if (bodyEnd != -1) {
                String body = docBlock.substring(bodyStart, bodyEnd).trim();
                StringBuilder cleanBody = new StringBuilder();
                for (String line : body.split("\n")) {
                    String trimmed = line.trim();
                    if (trimmed.startsWith("*")) {
                        trimmed = trimmed.substring(1).trim();
                    }
                    if (cleanBody.length() > 0) {
                        cleanBody.append("\n");
                    }
                    cleanBody.append(trimmed);
                }
                snippet.setBody(cleanBody.toString());
            }
        }
        
        return snippet;
    }
}
