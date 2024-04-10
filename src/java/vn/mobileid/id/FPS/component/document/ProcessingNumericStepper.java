/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import fps_core.enumration.FieldTypeName;
import fps_core.objects.child.NumericStepperAttribute;
import fps_core.objects.core.ExtendedFieldAttribute;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.general.PolicyConfiguration;
import vn.mobileid.id.utils.Utils;

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
            String value) throws Exception {
        NumericStepperAttribute numeric = new ObjectMapper().readValue(fieldData.getDetailValue(), NumericStepperAttribute.class);
        numeric = (NumericStepperAttribute) fieldData.clone(numeric, fieldData.getDimension());

        if (value != null) {
            numeric.setValue(value);
        }

        numeric.setProcessBy(user.getAzp());
        SimpleDateFormat dateFormat = new SimpleDateFormat(PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat());
        numeric.setProcessOn(dateFormat.format(Date.from(Instant.now())));

        if (!Utils.isNullOrEmpty(value)) {
            numeric.setValue(value);
        } else {
            try {
                if (Utils.isNullOrEmpty(numeric.getValue())) {
                    numeric.setValue(String.valueOf(numeric.getDefaultValue()));
                }
            } catch (Exception ex) {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_FIELD_NUMERIC_STEPPER,
                        A_FPSConstant.SUBCODE_MISSING_DEFAULT_ITEMS_FOR_PROCESS
                );
            }
        }

        return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, numeric);
    }
}
