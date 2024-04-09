/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import fps_core.enumration.FieldTypeName;
import fps_core.objects.child.ToggleFieldAttribute;
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
            String value) throws Exception {
        ToggleFieldAttribute toggle = new ObjectMapper().readValue(fieldData.getDetailValue(), ToggleFieldAttribute.class);
        toggle = (ToggleFieldAttribute) fieldData.clone(toggle, fieldData.getDimension());

        if (value != null) {
            toggle.setValue(value);
        }

        toggle.setProcessBy(user.getAzp());
        SimpleDateFormat dateFormat = new SimpleDateFormat(PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat());
        toggle.setProcessOn(dateFormat.format(Date.from(Instant.now())));

        if (!Utils.isNullOrEmpty(value)) {
            toggle.setValue(value);
        } else {
            try {
                if (Utils.isNullOrEmpty(toggle.getValue())) {
                    toggle.setValue(toggle.getCombo().getDefaultItem());
                }
            } catch (Exception ex) {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_FIELD_COMBOBOX,
                        A_FPSConstant.SUBCODE_MISSING_DEFAULT_ITEMS_FOR_PROCESS
                );
            }
        }

        return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, toggle);
    }
}
