package com.semo.thanos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
  public static Values parseTokens(List<String> fileOpenerOutput, Values values) {
    ArrayList<String> Tokens = values.getTokens();
    Block currentBlock = new Block(null);
    Boolean recordingFunction = false;
    Boolean recordingClass = false;
    String functionBody = "";
    String className = "";
    String defName = "";
    String[] functionArguments = new String[]{null};
    for (String string : fileOpenerOutput) {
      string = string.stripLeading();
      String temp = "";
      for (int i = 0; i < string.length(); i++) {
        if (temp.equals("\t") || temp.equals(" ")) {
          temp = "";
          break;
        }

        temp += string.charAt(i);
        if (recordingFunction) {
          functionBody += string.charAt(i);
        }

        functionBody = functionBody.replace("endef", "");
        if (functionBody.startsWith(";")) {
          functionBody = functionBody.replaceFirst(";", "");
        }

        if (Tokens.contains(temp)) {
          if (temp.equals("endnf")) {
            recordingFunction = false;
            values.addFunction(defName, new Function(new FunctionProperties(defName, functionArguments, functionBody)));
            if (recordingClass) {
              if (values.getClass(className).getValues() == null) {
                values.getClass(className).setValues(new Values());
              }
              values.getClass(className).getValues().addFunction(defName, new Function(new FunctionProperties(defName, functionArguments, functionBody)));
            }
            continue;
          }

          if (temp.equals("endclass")) {
            recordingClass = false;
            continue;
          }

          if (!recordingFunction) {
            if (temp.equals("import:")) {
              String filename = Core.getFilename(values, string, i + 2)[1];
              List<String> file = Tokenizer.readFile(filename);
              values = Parser.parseTokens(file, values);
            }

            if (temp.equals("def")) {
              functionBody = "";
              defName = Core.getDefName(values, string, i + 2);
              functionArguments = Core.getDefArguments(values, string, i + 2);
              recordingFunction = true;
            }

            if (temp.equals("var")) {
              String[] splitValue = string.split("=");
              String varKey = splitValue[0].replaceFirst("var","").stripLeading().trim();
              String varValue = splitValue[1].stripLeading().trim();
              values.addTokenizedVariable(varKey,varValue);
            }

            if (temp.equals("class")) {
              functionBody = "";
              className = Core.getClassName(string, i + 2);
              String classExtended = Core.getClassExtendsName(string);
              values.addClass(className, new Class(className, classExtended));
              recordingClass = true;
            }

            if (temp.equals("let")) {
              String variableName = string.substring(string.indexOf("let") + 3, string.indexOf("=")).stripLeading().trim();
              String classDefName = string.substring(string.indexOf("=") + 1, string.indexOf("{}")).stripLeading().trim();
              getFunctions(variableName + ".", fileOpenerOutput, values, classDefName);
            }

            if (temp.equals("//")) break;

            if (temp.equals("endif")) {
              currentBlock = currentBlock.getParrentBlock();
              break;
            }
            if (temp.equals("else")) {
              if (!currentBlock.getSkipElse()) {
                currentBlock.setIfState(true);
                currentBlock.setInElse(true);
              } else currentBlock.setIfState(false);
              break;
            }
            if (temp.equals("elseif")) { // && currentBlock.getSkipElse()
              currentBlock.setIfState(false);
              if (!currentBlock.getExecuted() && !currentBlock.getSkipElse()) {
                currentBlock.setIfState(Core.getIfValue(values, string, i + 2));
                currentBlock = checkif(currentBlock);
              }
            }

            if (currentBlock.getIfState() || (!currentBlock.getSkipElse() && currentBlock.getInElse() && !currentBlock.getExecuted())) {
              if (temp.equals("if")) {
                currentBlock = new Block(currentBlock);
                currentBlock.setIfState(Core.getIfValue(values, string, i + 2));
                currentBlock = checkif(currentBlock);
              }
              if (temp.startsWith("print")) values = Core.print(values, string, i + 2, 2);
              if (temp.equals("var:")) values = Core.setVar(values, string, i + 2);
              if (temp.equals("$")) values = Core.setVar(values, string, i + 1);
              if (temp.equals("!")) values = Core.setArgVar(values, string, i + 1);
              if (temp.equals("input:")) values = Core.getInput(values, string, i + 2);
              break;
            }
          }
        }
      }
      functionBody += ";";
    }
    return values;
  }

  private static Block checkif(Block currentBlock) {
    if (currentBlock.getIfState()) {
      currentBlock.setSkipElse(true);
      currentBlock.setIfState(true);
    } else {
      currentBlock.setIfState(false);
      currentBlock.setSkipElse(false);
    }
    currentBlock.setExecuted(currentBlock.getIfState());
    return currentBlock;
  }

  public static Map<String, Values> getFunctions(String s, List<String> fileOpenerOutput, Values values, String classDefName) {
    Map<String, Values> functions = new HashMap<>();
    for (String string : fileOpenerOutput) {
      if (string.startsWith(s)) {
        String functionName = string.replaceFirst(s, "").split("\\(")[0].trim();;
        Class classValue = values.getClass(classDefName);
        Values newValues = getValues(functionName, classValue, values);
        Core.runFunction(newValues, string.replaceFirst(s, ""), 0);
      }
    }
    return functions;
  }

  private static Values getValues(String functionName, Class classValue, Values values) {
    if (classValue.getValues() != null && classValue.getValues().getFunction(functionName) != null) {
      return classValue.getValues();
    } else {
      Class parentClassValue = values.getClass(classValue.getParent());
      return getValues(functionName,parentClassValue,values);
    }
  }
}
