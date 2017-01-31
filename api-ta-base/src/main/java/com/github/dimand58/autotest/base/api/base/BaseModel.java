package com.github.dimand58.autotest.base.api.base;

import com.github.dimand58.autotest.base.api.util.JsonHelper;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class BaseModel {

  @JsonIgnore private String jsonRepresentation;

  @JsonIgnore
  public String getJson() {
    jsonRepresentation = JsonHelper.toJson(this);
    return jsonRepresentation;
  }

  @JsonIgnore
  public void setJson(String json) {
    this.jsonRepresentation = json;
  }

  @JsonIgnore
  public String getJsonWo(String... pathsToNodes) {
    jsonRepresentation = JsonHelper.toJsonWo(this, pathsToNodes);
    return jsonRepresentation;
  }
}
