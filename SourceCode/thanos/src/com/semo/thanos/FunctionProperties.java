package com.semo.thanos;

public class FunctionProperties {
    public String name;
    public String[] arguments;
    public String code;

    public FunctionProperties(String name, String[] arguments, String code){
        this.name = name;
        this.arguments = arguments;
        this.code = code;
    }
}
