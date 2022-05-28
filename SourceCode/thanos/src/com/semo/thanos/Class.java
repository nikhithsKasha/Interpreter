package com.semo.thanos;

import java.util.ArrayList;

public class Class {
  private String name;
  private Values values;
  private String parent;

  public Class(String name, Values values) {
    this.name = name;
    this.values = values;
  }

  public Class(String name, Values values, String parent) {
    this.name = name;
    this.values = values;
    this.parent = parent;
  }

  public Class(String name) {
    this.name = name;
  }

  public Class(String name, String parent) {
    this.name = name;
    this.parent = parent;
  }

  public Class() {

  }

  public void run(Values values, ArrayList<String[]> inputArguments) {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public Values getValues() {
    return values;
  }

  public void setValues(Values values) {
    this.values = values;
  }

  public String getParent() {
    return parent;
  }

  public void setParent(String parent) {
    this.parent = parent;
  }
}
