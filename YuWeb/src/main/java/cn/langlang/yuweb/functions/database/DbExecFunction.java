package cn.langlang.yuweb.functions.database;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.DatabaseManager;
import cn.langlang.yuweb.database.Database;

import java.util.List;

public class DbExecFunction extends AbstractFunction {
    private DatabaseManager dbManager;
    
    public DbExecFunction(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    
    @Override
    public String getName() {
        return "dbexec";
    }
    
    @Override
    public int getMinParameters() {
        return 1;
    }
    
    @Override
    public int getMaxParameters() {
        return 1;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        Database db = dbManager.getDefaultDatabase();
        if (db == null) {
            throw new FunctionException("Database not connected");
        }
        
        String sql = arguments.get(0) != null ? arguments.get(0).toString() : "";
        
        try {
            db.execute(sql);
            return true;
        } catch (Exception e) {
            throw new FunctionException("Execute failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING);
    }
}
