package com.semo.thanos;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;

public class Values {
  private static ArrayList<String> Tokens;
  private static Hashtable<String, String[]> Variables;
  private static Hashtable<String, String> tokenizedVariables;
  private Hashtable<String, Function> functions = new Hashtable<>();
  private static Hashtable<String, Function> Functions;
  private static Hashtable<String, Class> Classes = new Hashtable<>();
  private static Hashtable<String, Hashtable<String, String[]>> FunctionArguments;
  private static Stack<String> currentFunction;

  Values() {
    Tokens = new ArrayList<>();
    tokenizedVariables = new Hashtable<>();
    Variables = new Hashtable<>();
    Functions = new Hashtable<>();
    currentFunction = new Stack<>();
    FunctionArguments = new Hashtable<>();
    Tokens.add("if");
    Tokens.add("else");
    Tokens.add("elseif");
    Tokens.add("endif");
    Tokens.add("def");
    Tokens.add("endnf");
    Tokens.add("endfor");
    Tokens.add("print");
    Tokens.add("var");
    Tokens.add("$");
    Tokens.add("@");
    Tokens.add("input:");
    Tokens.add("//");
    Tokens.add("import:");
    Tokens.add("class");
    Tokens.add("endclass");
    Tokens.add("let");

  }

  public ArrayList<String> getTokens() {
    return Tokens;
  }

  public void addFunction(String name, Function function) {
    functions.put(name, function);
  }

  public void addClass(String name, Class aClass) {
    Classes.put(name, aClass);
  }

  public Hashtable<String, Function> getFunctions() {
    return functions;
  }

  public Hashtable<String, Function> getMainFunctions() {
    return Functions;
  }

  public Hashtable<String, Class> getClasses() {
    return Classes;
  }

  public Function getFunction(String name) {
    return functions.get(name);
  }

  public Class getClass(String name) {
    return Classes.get(name);
  }


  public void addFunctionArguments(String name, Hashtable<String, String[]> arguments) {
    FunctionArguments.put(name, arguments);
  }

  public Hashtable<String, String[]> getFunctionArguments(String name) {
    return FunctionArguments.get(name);
  }

  public void addTokenizedVariable(String name, String value) {
    tokenizedVariables.put(name, value);
  }

  public void addVariable(String name, String[] value) {
    Variables.put(name, value);
  }

  public String getTokenizedVariable(String name) {
    return tokenizedVariables.get(name);
  }


  public String[] getVariable(String name) {
    return Variables.get(name);
  }

  public void setCurrentFunction(String name) {
    currentFunction.push(name);
  }


  public boolean isStackEmpty() {
    return currentFunction.empty();
  }

  public String getCurrentFunction() {
    String func = currentFunction.pop();
    setCurrentFunction(func);
    return func;
  }

  public void deleteLastFunction() {
    currentFunction.pop();
  }
}
