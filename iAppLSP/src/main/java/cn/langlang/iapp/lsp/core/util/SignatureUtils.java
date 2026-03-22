package cn.langlang.iapp.lsp.core.util;

import cn.langlang.iapp.runtime.ParamType;

import java.util.ArrayList;
import java.util.List;

public final class SignatureUtils {

    private SignatureUtils() {
    }

    public static String buildSignature(String name, List<ParamType> paramTypes) {
        if (name == null) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder(name);
        sb.append("(");
        
        if (paramTypes != null && !paramTypes.isEmpty()) {
            List<String> params = new ArrayList<>();
            int inputIndex = 0;
            int outputIndex = 0;
            
            for (ParamType type : paramTypes) {
                if (type == ParamType.OUTPUT) {
                    params.add("out" + (outputIndex > 0 ? outputIndex : ""));
                    outputIndex++;
                } else {
                    params.add(type.name().toLowerCase() + (inputIndex > 0 ? inputIndex : ""));
                    inputIndex++;
                }
            }
            sb.append(String.join(", ", params));
        }
        
        sb.append(")");
        return sb.toString();
    }

    public static String buildInsertText(String name, List<ParamType> paramTypes, int minParams) {
        if (name == null) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder(name);
        sb.append("(");
        
        if (paramTypes != null && !paramTypes.isEmpty()) {
            List<String> params = new ArrayList<>();
            int inputIndex = 0;
            int outputIndex = 0;
            
            for (ParamType type : paramTypes) {
                if (type == ParamType.OUTPUT) {
                    params.add("out" + (outputIndex > 0 ? outputIndex : ""));
                    outputIndex++;
                } else {
                    inputIndex++;
                    params.add("${" + inputIndex + ":arg" + inputIndex + "}");
                }
            }
            sb.append(String.join(", ", params));
        }
        
        sb.append(")$0");
        return sb.toString();
    }

    public static String buildParameterLabel(ParamType type, int inputIndex, int outputIndex) {
        if (type == ParamType.OUTPUT) {
            return "out" + (outputIndex > 0 ? outputIndex : "");
        } else {
            return type.name().toLowerCase() + (inputIndex > 0 ? inputIndex : "");
        }
    }

    public static String getParamTypeDescription(ParamType type) {
        if (type == null) {
            return "未知类型";
        }
        
        switch (type) {
            case STRING:
                return "字符串类型";
            case INT:
                return "整数类型";
            case LONG:
                return "长整数类型";
            case DOUBLE:
                return "浮点数类型";
            case BOOLEAN:
                return "布尔类型";
            case OBJECT:
                return "任意对象类型";
            case ARRAY:
                return "数组类型";
            case OUTPUT:
                return "输出参数（用于接收返回值）";
            default:
                return type.name();
        }
    }

    public static boolean hasOutputParameter(List<ParamType> paramTypes) {
        if (paramTypes == null || paramTypes.isEmpty()) {
            return false;
        }
        
        for (ParamType type : paramTypes) {
            if (type == ParamType.OUTPUT) {
                return true;
            }
        }
        return false;
    }

    public static int countInputParameters(List<ParamType> paramTypes) {
        if (paramTypes == null || paramTypes.isEmpty()) {
            return 0;
        }
        
        int count = 0;
        for (ParamType type : paramTypes) {
            if (type != ParamType.OUTPUT) {
                count++;
            }
        }
        return count;
    }

    public static int countOutputParameters(List<ParamType> paramTypes) {
        if (paramTypes == null || paramTypes.isEmpty()) {
            return 0;
        }
        
        int count = 0;
        for (ParamType type : paramTypes) {
            if (type == ParamType.OUTPUT) {
                count++;
            }
        }
        return count;
    }
}
