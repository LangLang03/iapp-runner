package cn.langlang.yuweb.functions.database;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.database.DatabaseManager;
import cn.langlang.yuweb.database.Database;

import java.util.List;
import java.util.Map;

public class DbInsertFunction extends AbstractFunction {
    private DatabaseManager dbManager;
    
    public DbInsertFunction(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    
    @Override
    public String getName() {
        return "dbinsert";
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
        Database db = dbManager.getDefaultDatabase();
        if (db == null) {
            throw new FunctionException("Database not connected");
        }
        
        String table = arguments.get(0) != null ? arguments.get(0).toString() : "";
        Object dataObj = arguments.get(1);
        
        Map<String, Object> data = null;
        if (dataObj instanceof Map) {
            data = (Map<String, Object>) dataObj;
        }
        
        try {
            return db.insert(table, data);
        } catch (Exception e) {
            throw new FunctionException("Insert failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.OBJECT);
    }
}
