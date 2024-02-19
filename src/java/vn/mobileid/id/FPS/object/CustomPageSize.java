///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package vn.mobileid.id.FPS.object;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.fasterxml.jackson.annotation.JsonRootName;
//import java.util.List;
//
///**
// *
// * @author GiaTK
// */
//@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
//public class CustomPageSize {
//
//    public CustomPageSize() {
//    }
//    private int pageStart;
//    private int pageEnd;
//    private int rotate;
//    private float pageWidth;
//    private float pageHeight;
//
//    public void setPageStart(int pageStart) {
//        this.pageStart = pageStart;
//    }
//
//    public void setPageEnd(int pageStop) {
//        this.pageEnd = pageStop;
//    }
//
//    public void setRotate(int rotate) {
//        this.rotate = rotate;
//    }
//
//    public void setPageWidth(float pageWidth) {
//        this.pageWidth = pageWidth;
//    }
//
//    public void setPageHeight(float pageHeight) {
//        this.pageHeight = pageHeight;
//    }
//
//    @JsonProperty("page_start")
//    public int getPageStart() {
//        return pageStart;
//    }
//
//    @JsonProperty("page_end")
//    public int getPageEnd() {
//        return pageEnd;
//    }
//
//    @JsonProperty("page_rotate")
//    public int getRotate() {
//        return rotate;
//    }
//
//    @JsonProperty("page_width")
//    public float getPageWidth() {
//        return pageWidth;
//    }
//
//    @JsonProperty("page_height")
//    public float getPageHeight() {
//        return pageHeight;
//    }
//}