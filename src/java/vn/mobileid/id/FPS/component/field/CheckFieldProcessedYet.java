/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.field;

import com.fasterxml.jackson.databind.ObjectMapper;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.enumration.ProcessStatus;
import vn.mobileid.id.FPS.fieldAttribute.BasicFieldAttribute;
import vn.mobileid.id.FPS.object.InternalResponse;

/**
 *
 * @author GiaTK
 */
public class CheckFieldProcessedYet {
    //<editor-fold defaultstate="collapsed" desc="Check field is Processed yet?">
    public static InternalResponse checkProcessed(
            String fieldValue
    ){
        try {
            BasicFieldAttribute signatureField = new ObjectMapper().readValue(fieldValue, BasicFieldAttribute.class);
            if (signatureField.getProcessStatus().equals(ProcessStatus.PROCESSED.getName())) {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_FIELD,
                        A_FPSConstant.SUBCODE_FIELD_ALREADY_PROCESS
                );
            }
        } catch (Exception ex) {
        }
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,                
                ""
        );
    }
    //</editor-fold>
}
