/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.document;

import fps_core.module.DocumentUtils_itext7;
import fps_core.objects.SignatureFieldAttribute;
import java.util.List;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.object.InternalResponse;

/**
 *
 * @author GiaTK
 */
public class PreCheckDocument {
    //<editor-fold defaultstate="collapsed" desc="Pre check Document">
    public static InternalResponse preCheckDocument(byte[] pdf){
        List<SignatureFieldAttribute> lists = DocumentUtils_itext7.getAllSignatures(pdf);
        if(lists == null || lists.isEmpty()){
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    ""
            );
        } 
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }
    //</editor-fold>
}
