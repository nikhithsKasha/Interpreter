package com.semo.thanos;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Core {
  private static String[] parseVar(Values values, String var, Integer i) {
    Pattern r = Pattern.compile("(\\d+)");
    String variable = "";
    Boolean stringEnabled = false;
    Boolean bool = false;
    String type = "";
    for (Integer j = i; j < var.length(); j++) {
      if (Character.toString(var.charAt(j)).equals("$") && !stringEnabled) return getVar(values, var, j + 1);
      if (Character.toString(var.charAt(j)).equals("!") && !stringEnabled) {
        String name = "";
        for (Integer k = j + 1; k < var.length(); k++) {
          name += var.charAt(k);
        }
        return values.getFunctionArguments(values.getCurrentFunction()).get(name);
      }

      if (var.charAt(j) == "\"".charAt(0)) {
        if (stringEnabled) {
          break;
        } else stringEnabled = !stringEnabled;
        continue;
      }
      if (r.matcher(Character.toString(var.charAt(j))).find()) {
        if (type.equals("")) type = "integer";
      } else if (stringEnabled) {
        type = "string";
      }
      variable += var.charAt(j);
    }


    if (!type.equals("string") && (variable.contains("true") || variable.contains("false"))) {
      type = "boolean";
    }


    if (variable.length() == 0 || variable.equals("null")) {
      type = "null";
      variable = "null";
    }

    if (variable.equals("line") && type.equals("")) {
      type = "newline";
      variable = "";
    }

    if (type.equals("")) Errors.RaiseError("Not a type at: \"" + var + "\"");

    variable = validateIfArithmeticExpression(var);

    return new String[]{type, variable};
  }

  @SuppressWarnings("removal")
  private static String validateIfArithmeticExpression(String var) {
    List<String> supportedExpressions = List.of("+", "-", "%", "/", "*");
    if (isNumeric(String.valueOf(var.charAt(0))) && isNumeric(var.substring(var.length() - 1))) {
      for (int i = 0; i < var.length(); i++) {
        if (isNumeric(String.valueOf(var.charAt(i))) || isContainedInTheList(supportedExpressions, String.valueOf(var.charAt(i)))) {
          ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
          ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("Nashorn");
          try {
            return String.valueOf(scriptEngine.eval(var));
          } catch (ScriptException e) {
          }
        } else {
          return var;
        }
      }

    }
    return var;
  }

  private static boolean isContainedInTheList(List<String> supportedExpressions, String expession) {
    return supportedExpressions.contains(expession);
  }

  public static boolean isNumeric(String str) {
    try {
      Double.parseDouble(str);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }


  private static int[] parseIntVarArray(Values values, String var, Integer i, Integer a) {

    for (Integer j = i; j <= var.length() - a; j++) {
      if (Character.toString(var.charAt(j)).equals("!")) {
        String name = "";
        for (Integer k = j + 1; k <= var.length() - a; k++) {
          name += var.charAt(k);
        }
        String[] strings = values.getFunctionArguments(values.getCurrentFunction()).get(name);
        String result = strings[1].replaceFirst("\\[", "");
        result = result.substring(0, result.length() - 1);
        return Arrays.stream(result.split(","))
            .mapToInt(Integer::parseInt)
            .toArray();
      }
    }
    return new int[]{};
  }

  private static String[] parseVar(Values values, String var, Integer i, Integer a) {
    Pattern r = Pattern.compile("(\\d+)");
    String variable = "";
    Boolean stringEnabled = false;
    Boolean bool = false;
    String type = "";
    for (Integer j = i; j <= var.length() - a; j++) {
      if (Character.toString(var.charAt(j)).equals("$") && !stringEnabled) return getVar(values, var, j + 1);
      if (Character.toString(var.charAt(j)).equals("!") && !stringEnabled) {
        String name = "";
        for (Integer k = j + 1; k <= var.length() - a; k++) {
          name += var.charAt(k);
        }
        return values.getFunctionArguments(values.getCurrentFunction()).get(name);
      }

      if (var.charAt(j) == "\"".charAt(0)) {
        if (stringEnabled) {
          break;
        } else stringEnabled = !stringEnabled;
        continue;
      }
      if (r.matcher(Character.toString(var.charAt(j))).find()) {
        if (type.equals("")) type = "integer";
      } else if (stringEnabled) {
        type = "string";
      }
      variable += var.charAt(j);
    }


    if (!type.equals("string") && (variable.contains("true") || variable.contains("false"))) {
      type = "boolean";
    }

    if (variable.contains("[]")) {
      type = "integer";
    }


    if (variable.length() == 0 || variable.equals("null")) {
      type = "null";
      variable = "null";
    }

    if (variable.equals("line") && type.equals("")) {
      type = "newline";
      variable = "";
    }

    if (type.equals("")) Errors.RaiseError("Not a type at: \"" + var + "\"");

    return new String[]{type, variable};
  }

  public static Values print(Values values, String string, Integer i, Integer a) {
    if (!values.getFunctions().isEmpty() && !values.isStackEmpty() && values.getCurrentFunction().equals("bubbleSort")) {
      int[] array = parseIntVarArray(values, string, i, a);
      bubbleSort(array);
      System.out.println(Arrays.toString(array));
    } else {
      System.out.println(parseVar(values, string, i, a)[1]);
    }
    return values;
  }

  private static String[] getVar(Values values, String string, Integer i) {
    String name = "";
    for (Integer j = i; j < string.length(); j++) {
      name += Character.toString(string.charAt(j));
    }
    return values.getVariable(name);
  }

  public static Values setVar(Values values, String string, Integer i) {
    Values new_values = values;
    String varname = "";
    Integer end_i = 0;
    for (Integer j = i; j < string.length(); j++) {
      String s = Character.toString(string.charAt(j));
      if (s.equals(" ")) {
        end_i = j + 3;
        break;
      }
      varname += s;
    }
    new_values.addVariable(varname, parseVar(new_values, string, end_i));
    return new_values;
  }

  public static Values setArgVar(Values values, String string, Integer i) {
    Values new_values = values;
    String varname = "";
    Integer end_i = 0;
    for (Integer j = i; j < string.length(); j++) {
      String s = Character.toString(string.charAt(j));
      if (s.equals(" ")) {
        end_i = j + 3;
        break;
      }
      varname += s;
    }
    new_values.getFunctionArguments(new_values.getCurrentFunction()).put(varname, parseVar(new_values, string, end_i));
    return new_values;
  }

  public static Values getInput(Values values, String string, Integer i) {
    Values new_values = values;
    String varname = "";
    String text = "";
    Integer end_i = 0;
    for (Integer j = i; j < string.length(); j++) {
      String s = Character.toString(string.charAt(j));
      if (s.equals(",")) {
        end_i = j + 2;
        break;
      }
      varname += s;
    }
    for (Integer j = end_i; j < string.length(); j++) {
      String s = Character.toString(string.charAt(j));
      text += s;
    }
    Scanner reader = new Scanner(System.in);
    System.out.print(">> " + parseVar(values, text, 0)[1]);
    new_values.addVariable(varname, parseVar(new_values, "\"" + reader.next() + "\"", 0));
//        reader.close();
    return new_values;
  }

  public static Boolean getIfValue(Values values, String string, Integer i) {
    Boolean state = true;
    Integer action = 0;
    String operator = "";
    String firstOperand = "";
    String secondOperand = "";
    for (Integer j = i; j < string.length(); j++) {
      String cc = Character.toString(string.charAt(j));
      if (cc.equals(")") || cc.equals("(")) continue;
      if (cc.equals(" ")) {
        action++;
        continue;
      }
      if (action == 0) firstOperand += cc;
      if (action == 1) operator += cc;
      if (action == 2) secondOperand += cc;
    }
    String[] parsedFirstOperand = parseVar(values, firstOperand, 0);
    String[] parsedSecondOperand = parseVar(values, secondOperand, 0);
//        System.out.println(operator);
    if (parsedFirstOperand == null || parsedSecondOperand == null) state = false;
    else {
      if (parsedFirstOperand[0].equals(parsedSecondOperand[0])) {
        switch (operator) {
          case "==":
            state = (parsedFirstOperand[0].equals(parsedSecondOperand[0])) && (parsedFirstOperand[1].equals(parsedSecondOperand[1]));
            break;
          case "!=":
            state = !((parsedFirstOperand[0].equals(parsedSecondOperand[0])) && (parsedFirstOperand[1].equals(parsedSecondOperand[1])));
            break;
          case "<":

            if (parsedFirstOperand[0].equals("integer") && parsedSecondOperand[0].equals("integer")) {
              state = Integer.parseInt(parsedFirstOperand[1]) < Integer.parseInt(parsedSecondOperand[1]);
            } else if (parsedFirstOperand[0].equals("string") && parsedSecondOperand[0].equals("string")) {
              state = parsedFirstOperand[1].length() < parsedSecondOperand[1].length();
            }
            break;
        }
      } else {
        if (parsedFirstOperand[0].equals("null") || parsedSecondOperand[0].equals("null")) return false;
        Errors.RaiseError("Incompatible types " + parsedFirstOperand[0] + " and " + parsedSecondOperand[0] + " at \"" + string + "\"");
      }
    }
    return state;
  }

  public static Values convert(Values values, String string, Integer i) {
    Values new_values = values;
    String varname = "";
    String toType = "";
    Integer action = 0;
    for (Integer j = i; j < string.length(); j++) {
      String cc = Character.toString(string.charAt(j));
      if (cc.equals(" ") || cc.equals("-")) {
        action++;
        continue;
      }
      if (action == 0) varname += cc;
      if (action == 2) toType += cc;
    }

    if (toType.equals("null")) new_values.addVariable(varname, new String[]{"null", "null"});

    String[] varval = new_values.getVariable(varname);

    if (toType.equals("boolean")) {
      String bool;
      if (varval[1].equals("0")) bool = "false";
      else bool = "true";
      new_values.addVariable(varname, new String[]{"boolean", bool});
    }

    if (toType.equals("string")) {
      Pattern r = Pattern.compile("^(.*)$");
      if (r.matcher(varval[1]).find()) {
        new_values.addVariable(varname, new String[]{"string", varval[1]});
      }
    }

    if (toType.equals("integer")) {
      Pattern r = Pattern.compile("^([0-9]*)$");
      if (r.matcher(varval[1]).find()) {
        new_values.addVariable(varname, new String[]{"integer", varval[1]});
      }
    }

    return new_values;
  }

  public static String getDefName(Values values, String string, Integer i) {
    String name = "";
    for (Integer j = i; j < string.length(); j++) {
      String cc = Character.toString(string.charAt(j));
      if (cc.equals("(")) {
        break;
      }
      name += cc;
    }
    return name.stripLeading().trim();
  }


  public static String getClassName(String string, Integer i) {
    String[] split = string.split(":");
    string = split[0];

    String name = "";
    for (Integer j = i; j < string.length(); j++) {
      String cc = Character.toString(string.charAt(j));
      if (!cc.equals(":")) {
        name += cc;
      }
    }
    return name.stripLeading().trim();
  }


  public static String getClassExtendsName(String string) {
    String[] strings = string.split(":");
    if (strings.length > 1) {
      return string.split(":")[1].stripLeading().trim();
    }
    return null;
  }

  public static Values runFunction(Values values, String string, Integer i) {
    String name = "";
    Integer action = 0;
    ArrayList<String[]> args = new ArrayList<>();
//    args.add(values.getFunction(string).getArguments());
    String temp = "";
    for (Integer j = i; j < string.length(); j++) {
      String cc = Character.toString(string.charAt(j));
      if (cc.equals("(")) {
        ++action;
        continue;
      }

      if (action == 0) name += cc;
      if (action == 1) {
        if (cc.equals(",") || cc.equals(")")) {
          if (temp.startsWith("[") && !temp.contains("]")) {
            temp += cc;
          } else {
            if (!temp.isEmpty()) {
              String argVal = values.getTokenizedVariable(temp);
              if (argVal == null) {
                args.add(parseVar(values, temp, 0));
              } else {
                args.add(parseVar(values, argVal, 0));
              }
            }
            temp = "";
          }
        } else {
          temp += cc;
        }
      }
    }
    values.setCurrentFunction(name.trim());
    return values.getFunction(name.trim()).run(values, args);
  }

  public static String[] getFilename(Values values, String string, Integer i) {
    String[] name;
    String rawname = "";
    if (String.valueOf(string.charAt(i)).equals("$")) return parseVar(values, string, i);
    for (Integer j = i; j < string.length(); j++) {
      rawname += string.charAt(j);
    }
    name = new String[]{"string", rawname};
    return name;
  }

  public static String[] getDefArguments(Values values, String string, Integer i) {
    Integer action = 0;
    String[] endargs;
    ArrayList<String> args = new ArrayList<>();
    Integer argindex = 0;
    String temp = "";
    for (Integer j = i; j < string.length(); j++) {
      String cc = Character.toString(string.charAt(j));
      if (cc.equals("(") && !Character.toString(string.charAt(j + 1)).equals(")")) {
        ++action;
        continue;
      }
      if (action == 1) {
        if (cc.equals(",") || cc.equals(")")) {
          args.add(temp);
          temp = "";
          argindex++;
        } else {
          temp += cc;
        }
      }
    }
    endargs = new String[args.size()];
    for (Integer k = 0; k < args.size(); k++) {
      endargs[k] = args.get(k);
    }

    return endargs;
  }

  public static Values calculate(Values values, String string, int i) {
    Values new_values = values;
    Integer action = 0;
    String operand_1 = "";
    String operand_2 = "";
    String operator = "";
    String varname = "";
    String type = "";

    for (Integer j = i; j < string.length(); j++) {
      String cc = Character.toString(string.charAt(j));
      if (cc.equals(" ")) {
        ++action;
        continue;
      }
      if (cc.equals(",")) {
        action = 100;
        continue;
      }
      if (action == 0) {
        operand_1 += cc;
      }
      if (action == 1) {
        operator += cc;
      }
      if (action == 2) {
        operand_2 += cc;
      }
      if (action == 100) {
        varname += cc;
      }
    }

    String[] parsed1 = parseVar(values, operand_1, 0);
    String[] parsed2 = parseVar(values, operand_2, 0);
    if (parsed1[0].equals("boolean") && parsed2[0].equals("boolean")) {
      Errors.RaiseError("Can not do operations with 2 booleans");
    }
    if (parsed1[0].equals("null") || parsed2[0].equals("null")) {
      Errors.RaiseError("Can not do operations with null");
    }
    if (operator.equals("+")) {
      if (parsed1[0].equals("string") || parsed2[0].equals("string")) {
        type = "string";
        new_values.addVariable(varname, new String[]{type, parsed1[1] + parsed2[1]});
      }
      if (parsed1[0].equals("integer") && parsed2[0].equals("integer")) {
        type = "integer";
        new_values.addVariable(varname, new String[]{type, String.valueOf(Integer.valueOf(parsed1[1]) + Integer.valueOf(parsed2[1]))});
      }

    }

    return new_values;
  }

  static void bubbleSort(int[] arr) {
    int n = arr.length;
    int temp = 0;
    for (int i = 0; i < n; i++) {
      for (int j = 1; j < (n - i); j++) {
        if (arr[j - 1] > arr[j]) {
          //swap elements
          temp = arr[j - 1];
          arr[j - 1] = arr[j];
          arr[j] = temp;
        }
      }
    }

  }

  public static String removeLastTwoChar(String s) {
    return (s == null || s.length() < 2)
        ? null
        : (s.substring(0, s.length() - 2));
  }

  public static String removeLastOneChar(String s) {
    return (s == null || s.length() == 0)
        ? null
        : (s.substring(0, s.length() - 1));
  }
}
