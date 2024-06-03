package vn.mobileid.id.FPS.controller.field.summary.module;

///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package vn.mobileid.id.FPS.component.field;
//
//import com.itextpdf.kernel.geom.Rectangle;
//import vn.mobileid.id.FPS.component.enterprise.ProcessModuleForEnterprise;
//import vn.mobileid.id.FPS.enumration.RotateDegree;
//import vn.mobileid.id.FPS.fieldAttribute.Dimension;
//
///**
// *
// * @author GiaTK
// */
//public class RotateField {
//
//    //<editor-fold defaultstate="collapsed" desc="Rotate Dimension">
//    public static Dimension rotate(
//            Dimension dimensionOfClient,
//            float pageWidthOriginal,
//            float pageHeightOriginal,
//            RotateDegree degree) {
//        try {            
//            switch (degree) {
//                case Rotate_90: {
//                    float xOriginal = pageWidthOriginal - dimensionOfClient.getY() - dimensionOfClient.getHeight();
//                    float yOriginal = dimensionOfClient.getX();
//                    float widthOriginal = dimensionOfClient.getHeight();
//                    float heightOriginal = dimensionOfClient.getWidth();                    
//                    return new Dimension(xOriginal, yOriginal, widthOriginal, heightOriginal);
//                }
//                case Rotate_180: {
//                    break;
//                }
//                case Rotate_270: {
//                    break;
//                }
//            }
//            return dimensionOfClient;
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//    //</editor-fold>
//}
