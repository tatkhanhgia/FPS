/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.document.summary.module;

import fps_core.enumration.FieldTypeName;
import fps_core.objects.child.ToggleFieldAttribute;
import fps_core.objects.core.ExtendedFieldAttribute;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import vn.mobileid.id.FPS.systemManagement.A_FPSConstant;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.ProcessingRequest.ProcessingFormFillRequest;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.FPS.systemManagement.PolicyConfiguration;
import vn.mobileid.id.FPS.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class ProcessingToggleField extends ProcessingTextFormField<ToggleFieldAttribute> {

    public ProcessingToggleField() {
        super(new ToggleFieldAttribute());
    }

    @Override
    public FieldTypeName getFieldTypeName() {
        return FieldTypeName.TOGGLE;
    }

    @Override
    public InternalResponse convert(
            User user,
            ExtendedFieldAttribute fieldData,
            ProcessingFormFillRequest processField) throws Exception {
        ToggleFieldAttribute toggle = MyServices.getJsonService().readValue(fieldData.getDetailValue(), ToggleFieldAttribute.class);
        toggle = (ToggleFieldAttribute) fieldData.clone(toggle, fieldData.getDimension());

        toggle.setProcessBy(user.getAzp());
        SimpleDateFormat dateFormat = new SimpleDateFormat(PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat());
        toggle.setProcessOn(dateFormat.format(Date.from(Instant.now())));

        //<editor-fold defaultstate="collapsed" desc="Check value is String?">
        if (processField != null && !Utils.isNullOrEmpty(processField.getValue())) {
            if (!(processField.getValue() instanceof String)) {
                if (toggle.getCombo() == null || Utils.isNullOrEmpty(toggle.getCombo().getDefaultItem())) {
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FIELD,
                            A_FPSConstant.SUBCODE_VALUE_MUST_BE_ENCODE_BASE64_FORMAT
                    );
                }
            }
            toggle.setValue((String)processField.getValue());
        } else {
            if (toggle.getCombo() == null || Utils.isNullOrEmpty(toggle.getCombo().getDefaultItem())) {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_FIELD_COMBOBOX,
                        A_FPSConstant.SUBCODE_MISSING_DEFAULT_ITEMS_FOR_PROCESS
                );
            }
            toggle.setValue(toggle.getCombo().getDefaultItem());
        }
        //</editor-fold>
        
        return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, toggle);
    }
}
