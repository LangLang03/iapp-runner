package cn.langlang.yuweb.cache;

import cn.langlang.iapp.ast.Program;

public class CachedScript {
    private final Program program;
    private final long compileTime;
    private final String sourceHash;
    
    public CachedScript(Program program, long compileTime, String sourceHash) {
        this.program = program;
        this.compileTime = compileTime;
        this.sourceHash = sourceHash;
    }
    
    public Program getProgram() {
        return program;
    }
    
    public long getCompileTime() {
        return compileTime;
    }
    
    public String getSourceHash() {
        return sourceHash;
    }
}
