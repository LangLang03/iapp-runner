package cn.langlang.yuweb.functions.database;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.DatabaseManager;
import cn.langlang.yuweb.database.Database;

import java.util.List;

public class DbPageFunction extends AbstractFunction {
    private DatabaseManager dbManager;
    
    public DbPageFunction(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    
    @Override
    public String getName() {
        return "dbpage";
    }
    
    @Override
    public int getMinParameters() {
        return 4;
    }
    
    @Override
    public int getMaxParameters() {
        return 4;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        Database db = dbManager.getDefaultDatabase();
        if (db == null) {
            throw new FunctionException("Database not connected");
        }
        
        String table = arguments.get(0) != null ? arguments.get(0).toString() : "";
        Object condition = arguments.get(1);
        int page = 1;
        int size = 10;
        
        if (arguments.get(2) instanceof Number) {
            page = ((Number) arguments.get(2)).intValue();
        }
        if (arguments.get(3) instanceof Number) {
            size = ((Number) arguments.get(3)).intValue();
        }
        
        try {
            return db.findPage(table, condition, page, size);
        } catch (Exception e) {
            throw new FunctionException("Page query failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.OBJECT, ParamType.INT, ParamType.INT);
    }
}
