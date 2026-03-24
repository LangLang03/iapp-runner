package cn.langlang.iapp.lsp.core.provider;

import cn.langlang.iapp.lsp.core.LSContext;
import cn.langlang.iapp.lsp.core.util.SignatureUtils;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.ParamType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SignatureProvider {
    private final LSContext context;
    private final FunctionProvider functionProvider;

    public SignatureProvider(LSContext context) {
        this.context = context;
        this.functionProvider = new FunctionProvider(context);
    }

    public SignatureProvider(LSContext context, FunctionProvider functionProvider) {
        this.context = context;
        this.functionProvider = functionProvider != null ? functionProvider : new FunctionProvider(context);
    }

    public SignatureHelp getSignatureHelp(String functionName, int activeParameter) {
        if (functionName == null || functionName.isEmpty()) {
            return null;
        }
        
        IFunction function = context.getFunction(functionName);
        if (function == null) {
            return null;
        }
        
        return createSignatureHelp(function, activeParameter);
    }

    private SignatureHelp createSignatureHelp(IFunction function, int activeParameter) {
        SignatureHelp help = new SignatureHelp();
        
        List<SignatureInformation> signatures = new ArrayList<>();
        List<List<ParamType>> paramTypeLists = function.getParamTypeLists();
        
        if (paramTypeLists != null && !paramTypeLists.isEmpty()) {
            for (List<ParamType> types : paramTypeLists) {
                signatures.add(createSignatureInformation(function.getName(), types));
            }
        } else {
            signatures.add(createSignatureInformation(function.getName(), function.getParamTypes()));
        }
        
        help.setSignatures(signatures);
        help.setActiveSignature(0);
        help.setActiveParameter(activeParameter);
        
        return help;
    }

    private SignatureInformation createSignatureInformation(String name, List<ParamType> types) {
        SignatureInformation info = new SignatureInformation();
        
        String label = SignatureUtils.buildSignature(name, types);
        info.setLabel(label);
        
        IFunction function = context.getFunction(name);
        if (function != null) {
            info.setDocumentation(functionProvider.generateDocumentation(function));
        }
        
        List<ParameterInformation> parameters = new ArrayList<>();
        if (types != null && !types.isEmpty()) {
            int inputIndex = 0;
            int outputIndex = 0;
            
            for (ParamType type : types) {
                String paramName = SignatureUtils.buildParameterLabel(type, inputIndex, outputIndex);
                
                if (type == ParamType.OUTPUT) {
                    outputIndex++;
                } else {
                    inputIndex++;
                }
                
                ParameterInformation paramInfo = new ParameterInformation();
                paramInfo.setLabel(paramName);
                paramInfo.setDocumentation(SignatureUtils.getParamTypeDescription(type));
                parameters.add(paramInfo);
            }
        }
        
        info.setParameters(parameters);
        
        return info;
    }

    public FunctionProvider getFunctionProvider() {
        return functionProvider;
    }

    public static class SignatureHelp {
        private List<SignatureInformation> signatures;
        private int activeSignature;
        private int activeParameter;

        public List<SignatureInformation> getSignatures() {
            return signatures;
        }

        public void setSignatures(List<SignatureInformation> signatures) {
            this.signatures = signatures;
        }

        public int getActiveSignature() {
            return activeSignature;
        }

        public void setActiveSignature(int activeSignature) {
            this.activeSignature = activeSignature;
        }

        public int getActiveParameter() {
            return activeParameter;
        }

        public void setActiveParameter(int activeParameter) {
            this.activeParameter = activeParameter;
        }
    }

    public static class SignatureInformation {
        private String label;
        private String documentation;
        private List<ParameterInformation> parameters;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getDocumentation() {
            return documentation;
        }

        public void setDocumentation(String documentation) {
            this.documentation = documentation;
        }

        public List<ParameterInformation> getParameters() {
            return parameters;
        }

        public void setParameters(List<ParameterInformation> parameters) {
            this.parameters = parameters;
        }
    }

    public static class ParameterInformation {
        private String label;
        private String documentation;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getDocumentation() {
            return documentation;
        }

        public void setDocumentation(String documentation) {
            this.documentation = documentation;
        }
    }
}
