package cn.langlang.yuweb.functions.database;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.database.DatabaseManager;
import cn.langlang.yuweb.database.Database;

import java.util.List;

public class DbSearchFunction extends AbstractFunction {
    private DatabaseManager dbManager;
    
    public DbSearchFunction(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    
    @Override
    public String getName() {
        return "dbsearch";
    }
    
    @Override
    public int getMinParameters() {
        return 3;
    }
    
    @Override
    public int getMaxParameters() {
        return 5;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        Database db = dbManager.getDefaultDatabase();
        if (db == null) {
            throw new FunctionException("Database not connected");
        }
        
        String table = arguments.get(0) != null ? arguments.get(0).toString() : "";
        Object fields = arguments.get(1);
        String keyword = arguments.get(2) != null ? arguments.get(2).toString() : "";
        
        int page = 1;
        int size = 10;
        
        if (arguments.size() > 3 && arguments.get(3) instanceof Number) {
            page = ((Number) arguments.get(3)).intValue();
        }
        if (arguments.size() > 4 && arguments.get(4) instanceof Number) {
            size = ((Number) arguments.get(4)).intValue();
        }
        
        try {
            return db.search(table, fields, keyword, page, size);
        } catch (Exception e) {
            throw new FunctionException("Search failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.OBJECT, ParamType.STRING, ParamType.INT, ParamType.INT);
    }
}
