/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.field;

import com.fasterxml.jackson.databind.ObjectMapper;
import fps_core.enumration.ProcessStatus;
import fps_core.objects.core.BasicFieldAttribute;
import fps_core.objects.core.ExtendedFieldAttribute;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class CheckFieldProcessedYet {

    //<editor-fold defaultstate="collapsed" desc="Check field is Processed yet?">
    public static InternalResponse checkProcessed(
            String fieldValue
    ) {
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

    //<editor-fold defaultstate="collapsed" desc="Check field is Processed yet?">
    public static InternalResponse checkProcessed(
            ExtendedFieldAttribute field
    ) {
        if (!Utils.isNullOrEmpty(field.getProcessStatus())
                && field.getProcessStatus().equals(ProcessStatus.PROCESSED.getName())) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FIELD,
                    A_FPSConstant.SUBCODE_FIELD_ALREADY_PROCESS
            );
        }
        try {
            BasicFieldAttribute signatureField = new ObjectMapper().readValue(field.getFieldValue(), BasicFieldAttribute.class);
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
