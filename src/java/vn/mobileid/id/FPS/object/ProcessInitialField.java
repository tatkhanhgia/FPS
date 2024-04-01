/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessInitialField {

    private String fieldName;
    private String value;
    private List<String> initialFieldNames;

    public ProcessInitialField() {
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setInitialFieldNames(List<String> initialFieldNames) {
        this.initialFieldNames = initialFieldNames;
    }

    @JsonProperty("field_name")
    public String getFieldName() {
        return fieldName;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    @JsonProperty("initial_field_name")
    public List<String> getInitialFieldNames() {
        return initialFieldNames;
    }
}
