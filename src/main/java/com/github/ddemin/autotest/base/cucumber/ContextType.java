package com.github.ddemin.autotest.base.cucumber;

import lombok.*;

@Getter
public class ContextType<T> {
  private String entityName;

  public ContextType(String entityName) {
    this.entityName = entityName;
  }
}
