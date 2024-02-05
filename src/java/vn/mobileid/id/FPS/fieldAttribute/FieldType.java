/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.fieldAttribute;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author GiaTK    
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldType {
    private String typeName;
    private int id;
    private String parentType;
    private boolean required;

    public FieldType() {
    }

    @JsonProperty("type")
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String name) {
        this.typeName = name;
    }

    @JsonProperty("type_id")
    public int getTypeId() {
        return id;
    }

    public void setTypeId(int id) {
        this.id = id;
    }

    @JsonProperty("parent_type")
    public String getParentType() {
        return parentType;
    }

    public void setParentType(String type) {
        this.parentType = type;
    }

    @JsonProperty("required")
    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }        
    
}
