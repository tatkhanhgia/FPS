/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.document.summary.module;

import fps_core.module.DocumentUtils_itext7;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.utils.CreateInternalResponse;

/**
 *
 * @author GiaTK
 */
public class RemoveAppearanceSignature {
    //<editor-fold defaultstate="collapsed" desc="Remove Appearane (n1, n4) of Signature">
    /**
     * Remove Appearance(n1,n4) of Signature
     * @param filePDF
     * @param transactionId
     * @return InternalResponse with byte[] file Pdf as an Object
     */
    public InternalResponse removeAppearance(
            byte[] filePDF,
            String transactionId
    ){
        byte[] finalPdf = DocumentUtils_itext7.removeAppearanceSignature(filePDF);
        if(filePDF == null){
            return CreateInternalResponse.createBadRequestInternalResponse(
                    A_FPSConstant.CODE_DOCUMENT,
                    A_FPSConstant.SUBCODE_CANNOT_REMOMVE_APPEARANCE_OF_SIGNATURE);
        }
        return new InternalResponse().setData(finalPdf);
    }
    //</editor-fold>
    
    public static void main(String[] args) throws Exception{
        byte[] pdf = Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\ocb_file_loc.signed - Copy.pdf"));
        InternalResponse response = new RemoveAppearanceSignature().removeAppearance(pdf, "");
        FileOutputStream fos = new FileOutputStream("C:\\Users\\Admin\\Downloads\\remove.pdf");
        fos.write((byte[])response.getData());
        fos.close();
    }
}
