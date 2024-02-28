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
public class ItemDetails {

    private String field;
    private boolean mandatory_enable;
    private int type;
    private Object value;
    private String remark;

    //For FrontEnd
    private String ordinary_id;

    //File
    private String file_field;
    private String file_format;
    private String file_name;

    //Remark language
    private String field_name;
    private String field_name_vn;

    public ItemDetails() {
    }

    @JsonProperty("file_format")
    public String getFile_format() {
        return file_format;
    }

    public ItemDetails setFile_format(String file_format) {
        this.file_format = file_format;
        return this;
    }

    @JsonProperty("file_field")
    public String getFile_field() {
        return file_field;
    }

    public ItemDetails setFile_field(String file_field) {
        this.file_field = file_field;
        return this;
    }

    @JsonProperty("mandatory_enable")
    public boolean isMandatory_enable() {
        return mandatory_enable;
    }

    public ItemDetails setMandatory_enable(boolean mandatory_enable) {
        this.mandatory_enable = mandatory_enable;
        return this;
    }

    @JsonProperty("field")
    public String getField() {
        return field;
    }

    public ItemDetails setField(String field) {
        this.field = field;
        return this;
    }

    @JsonProperty("type")
    public int getType() {
        return type;
    }

    public ItemDetails setType(int type) {
        this.type = type;
        return this;
    }

    @JsonProperty("value")
    public Object getValue() {
        return value;
    }

    public ItemDetails setValue(Object value) {
        this.value = value;
        return this;
    }

    @JsonProperty("remark")
    public String getRemark() {
        return remark;
    }

    public ItemDetails setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    @JsonProperty("field_name")
    public String getField_name() {
        return field_name;
    }

    public ItemDetails setField_name(String field_name) {
        this.field_name = field_name;
        return this;
    }

    @JsonProperty("field_name_vn")
    public String getField_name_vn() {
        return field_name_vn;
    }

    public ItemDetails setField_name_vn(String field_name_vn) {
        this.field_name_vn = field_name_vn;
        return this;
    }

    @JsonProperty("file_name")
    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    @JsonProperty("ordinary_id")
    public String getOrdinary_id() {
        return ordinary_id;
    }

    public void setOrdinary_id(String ordinary_id) {
        this.ordinary_id = ordinary_id;
    }

    public static ItemDetails genNew() {
        return new ItemDetails();
    }
}
