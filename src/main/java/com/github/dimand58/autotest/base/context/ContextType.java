package com.github.dimand58.autotest.base.context;

import lombok.Getter;

@Getter
public class ContextType<T> {
  private String entityName;

  public ContextType(String entityName) {
    this.entityName = entityName;
  }
}
