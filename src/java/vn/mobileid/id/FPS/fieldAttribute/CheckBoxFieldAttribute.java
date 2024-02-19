///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package vn.mobileid.id.FPS.fieldAttribute;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
//
///**
// *
// * @author GiaTK
// */
//@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
//public class CheckBoxFieldAttribute extends BasicFieldAttribute{  
//    private String value;
//    private boolean readOnly;
//    private boolean checked;
//    private String checkBoxType;
//
//    public CheckBoxFieldAttribute() {
//    }  
//
//    public void setValue(String value) {
//        this.value = value;
//    }
//
//    public void setReadOnly(boolean readOnly) {
//        this.readOnly = readOnly;
//    }
//
//    public void setChecked(boolean checked) {
//        this.checked = checked;
//    } 
//
//    public void setCheckBoxType(String checkBoxType) {
//        this.checkBoxType = checkBoxType;
//    }
//
//    @JsonProperty("value")
//    public String getValue() {
//        return value;
//    }
//
//    @JsonProperty("read_only")
//    public boolean isReadOnly() {
//        return readOnly;
//    }
//
//    @JsonProperty("checked")
//    public boolean isChecked() {
//        return checked;
//    }        
//
//    @JsonProperty("check_box_type")
//    public String getCheckBoxType() {
//        return checkBoxType;
//    }
//}
