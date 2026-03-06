package cn.langlang.yuweb.functions.database;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.DatabaseManager;

import java.util.List;

public class DbFunction extends AbstractFunction {
    private DatabaseManager dbManager;
    
    public DbFunction(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    
    @Override
    public String getName() {
        return "db";
    }
    
    @Override
    public int getMinParameters() {
        return 2;
    }
    
    @Override
    public int getMaxParameters() {
        return 2;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String type = arguments.get(0) != null ? arguments.get(0).toString() : "sqlite";
        String path = arguments.get(1) != null ? arguments.get(1).toString() : "";
        
        try {
            dbManager.connect(type, path);
            return true;
        } catch (Exception e) {
            throw new FunctionException("Database connection failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.STRING);
    }
}
