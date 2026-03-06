package cn.langlang.yuweb.functions.database;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.DatabaseManager;
import cn.langlang.yuweb.database.Database;

import java.util.List;
import java.util.Map;

public class DbUpdateFunction extends AbstractFunction {
    private DatabaseManager dbManager;
    
    public DbUpdateFunction(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    
    @Override
    public String getName() {
        return "dbupdate";
    }
    
    @Override
    public int getMinParameters() {
        return 3;
    }
    
    @Override
    public int getMaxParameters() {
        return 3;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        Database db = dbManager.getDefaultDatabase();
        if (db == null) {
            throw new FunctionException("Database not connected");
        }
        
        String table = arguments.get(0) != null ? arguments.get(0).toString() : "";
        Object dataObj = arguments.get(1);
        Object condition = arguments.get(2);
        
        Map<String, Object> data = null;
        if (dataObj instanceof Map) {
            data = (Map<String, Object>) dataObj;
        }
        
        try {
            return db.update(table, data, condition);
        } catch (Exception e) {
            throw new FunctionException("Update failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.OBJECT, ParamType.OBJECT);
    }
}
