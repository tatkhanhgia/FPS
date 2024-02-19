///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package vn.mobileid.id.FPS.component.document.module;
//
//import com.itextpdf.forms.fields.PdfFormField;
//import com.itextpdf.kernel.pdf.PdfDictionary;
//import com.itextpdf.kernel.pdf.PdfName;
//import com.itextpdf.kernel.pdf.PdfNumber;
//import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
//
///**
// *
// * @author GiaTK
// */
//public class CheckVisible {
//    //<editor-fold defaultstate="collapsed" desc="Is InVisible">
//    public static boolean isInVisible(PdfDictionary dict){
//        PdfNumber num = (PdfNumber)dict.getAsNumber(PdfName.F);
//        int numm = num.intValue();
//        return num == null ? false : ((num.intValue() & PdfAnnotation.INVISIBLE)== PdfAnnotation.INVISIBLE);
//    }
//    //</editor-fold>
//    
//    //<editor-fold defaultstate="collapsed" desc="Is Hidden">
//    public static boolean isHidden(PdfDictionary dict){
//        PdfNumber num = (PdfNumber)dict.getAsNumber(PdfName.F);
//        return num == null ? false : ((num.intValue() & PdfAnnotation.HIDDEN)== PdfAnnotation.HIDDEN);
//    }
//    //</editor-fold>
//}
