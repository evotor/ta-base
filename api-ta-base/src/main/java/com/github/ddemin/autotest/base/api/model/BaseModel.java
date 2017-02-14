package com.github.ddemin.autotest.base.api.model;

import com.github.ddemin.autotest.base.api.json.*;

import java.util.*;

import com.fasterxml.jackson.annotation.*;

public abstract class BaseModel {

  @JsonIgnore
  private String jsonRepresentation;

  @JsonIgnore
  public String asJson() {
    return Optional.ofNullable(jsonRepresentation).orElseGet(this::refreshedJson);
  }

  @JsonIgnore
  public String asJsonWo(String... pathsToNodes) {
    return JsonHelper.toJsonWo(this, pathsToNodes);
  }

  @JsonIgnore
  public void setJson(String json) {
    this.jsonRepresentation = json;
  }

  @JsonIgnore
  public String refreshedJson() {
    this.jsonRepresentation = JsonHelper.toJson(this);
    return jsonRepresentation;
  }

}
