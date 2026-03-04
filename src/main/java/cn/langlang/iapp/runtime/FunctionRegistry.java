package cn.langlang.iapp.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FunctionRegistry {
    private final Map<String, IFunction> functions;
    
    public FunctionRegistry() {
        this.functions = new HashMap<>();
    }
    
    public void registerFunction(IFunction function) {
        functions.put(function.getName().toLowerCase(), function);
    }
    
    public IFunction getFunction(String name) {
        return functions.get(name.toLowerCase());
    }
    
    public boolean hasFunction(String name) {
        return functions.containsKey(name.toLowerCase());
    }
    
    public Set<String> getFunctionNames() {
        return functions.keySet();
    }
    
    public void unregisterFunction(String name) {
        functions.remove(name.toLowerCase());
    }
    
    public void clear() {
        functions.clear();
    }
}
