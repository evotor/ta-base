package com.github.ddemin.autotest.base.api.model;

import com.github.ddemin.autotest.base.api.json.*;

import com.fasterxml.jackson.annotation.*;

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
