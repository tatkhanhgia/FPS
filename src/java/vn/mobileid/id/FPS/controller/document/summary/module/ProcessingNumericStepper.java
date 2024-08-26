/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.document.summary.module;

import fps_core.enumration.FieldTypeName;
import fps_core.objects.child.NumericStepperAttribute;
import fps_core.objects.core.ExtendedFieldAttribute;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import vn.mobileid.id.FPS.systemManagement.A_FPSConstant;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.ProcessingRequest;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.FPS.systemManagement.PolicyConfiguration;
import vn.mobileid.id.FPS.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class ProcessingNumericStepper extends ProcessingTextFormField<NumericStepperAttribute> {

    public ProcessingNumericStepper() {
        super(new NumericStepperAttribute());
    }

    @Override
    public FieldTypeName getFieldTypeName() {
        return FieldTypeName.NUMERIC_STEP;
    }

    @Override
    public InternalResponse convert(
            User user,
            ExtendedFieldAttribute fieldData,
            ProcessingRequest.ProcessingFormFillRequest processField) throws Exception {
        NumericStepperAttribute numeric = MyServices.getJsonService().readValue(fieldData.getDetailValue(), NumericStepperAttribute.class);
        numeric = (NumericStepperAttribute) fieldData.clone(numeric, fieldData.getDimension());

        numeric.setProcessBy(user.getAzp());
        SimpleDateFormat dateFormat = new SimpleDateFormat(PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat());
        numeric.setProcessOn(dateFormat.format(Date.from(Instant.now())));

        //<editor-fold defaultstate="collapsed" desc="Check value is String?">
        if (processField != null && !Utils.isNullOrEmpty(processField.getValue())) {
            if (!(processField.getValue() instanceof String) && Utils.isNullOrEmpty(numeric.getDefaultValue())) {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_FIELD,
                        A_FPSConstant.SUBCODE_VALUE_MUST_BE_ENCODE_BASE64_FORMAT
                );
            }
            numeric.setValue((String)processField.getValue());
        } else {
            if(Utils.isNullOrEmpty(numeric.getDefaultValue())){
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_FIELD_NUMERIC_STEPPER,
                        A_FPSConstant.SUBCODE_MISSING_DEFAULT_ITEMS_FOR_PROCESS
                );
            }
            numeric.setValue(String.valueOf(numeric.getDefaultValue()));
        }
        //</editor-fold>
        
        return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, numeric);
    }
}
