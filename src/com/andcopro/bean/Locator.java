package com.andcopro.bean;

public class Locator
{
  int id = 0;
  boolean isDefault = false;
  String name;
  boolean isBusy = false;
  int priority = 0;
  
  public Locator(int id, boolean isDefault, String name, boolean isBusy, int priority)
  {
    this.id = id;
    this.isDefault = isDefault;
    this.name = name;
    this.isBusy = isBusy;
    this.priority = priority;
  }
  
  public int getPriority()
  {
    return this.priority;
  }
  
  public int getId()
  {
    return this.id;
  }
  
  public boolean isDefault()
  {
    return this.isDefault;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public boolean isBusy()
  {
    return this.isBusy;
  }
  
  public String toString()
  {
    return String.format("%s[id=%d, %s]", new Object[] { getClass().getSimpleName(), Integer.valueOf(getId()), getName() });
  }
}
