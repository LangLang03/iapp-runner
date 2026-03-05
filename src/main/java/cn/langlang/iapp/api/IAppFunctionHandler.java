package cn.langlang.iapp.api;

@FunctionalInterface
public interface IAppFunctionHandler {
    Object call(IAppScript script, Object[] args);
}
