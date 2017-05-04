package com.github.ddemin.autotest.base.cucumber;

import lombok.Getter;

@Getter
public class ContextType<T> {

  private String name;

  public ContextType(String name) {
    this.name = name;
  }
}
