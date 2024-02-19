///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package vn.mobileid.id.FPS.fieldAttribute;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import java.util.List;
//
///**
// *
// * @author GiaTK
// */
//@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
//public class QRFieldAttribute extends BasicFieldAttribute {
//
//    private String value;
//    private String imageQR;
//    private boolean isTransparent;
//
//    public QRFieldAttribute() {
//    }
//
//    @JsonProperty("value")
//    public String getValue() {
//        return value;
//    }
//
//    @JsonProperty("is_transparent")
//    public boolean IsTransparent() {
//        return isTransparent;
//    }
//
//    @JsonProperty("image_qr")
//    public String getImageQR() {
//        return imageQR;
//    }
//
//    public void setValue(String value) {
//        this.value = value;
//    }
//
//    public void setTransparent(boolean isTransparent) {
//        this.isTransparent = isTransparent;
//    }
//
//    public void setImageQR(String imageQR) {
//        this.imageQR = imageQR;
//    }
//}
