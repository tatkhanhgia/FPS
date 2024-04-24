/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import fps_core.enumration.FieldTypeName;
import fps_core.objects.child.ComboBoxFieldAttribute;
import fps_core.objects.core.ExtendedFieldAttribute;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.ProcessingRequest;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.general.PolicyConfiguration;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class ProcessingComboBoxField extends ProcessingTextFormField<ComboBoxFieldAttribute> {

    public ProcessingComboBoxField() {
        super(new ComboBoxFieldAttribute());
    }

    @Override
    public FieldTypeName getFieldTypeName() {
        return FieldTypeName.COMBOBOX;
    }

    @Override
    public InternalResponse convert(
            User user,
            ExtendedFieldAttribute fieldData,
            ProcessingRequest.ProcessingFormFillRequest processField) throws Exception {
        ComboBoxFieldAttribute comboField = new ObjectMapper().readValue(fieldData.getDetailValue(), ComboBoxFieldAttribute.class);
        comboField = (ComboBoxFieldAttribute) fieldData.clone(comboField, fieldData.getDimension());

        comboField.setProcessBy(user.getAzp());
        SimpleDateFormat dateFormat = new SimpleDateFormat(PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat());
        comboField.setProcessOn(dateFormat.format(Date.from(Instant.now())));

        //<editor-fold defaultstate="collapsed" desc="Check value is String?">
        if (processField != null && !Utils.isNullOrEmpty(processField.getValue())) {
            if (!(processField.getValue() instanceof String)) {
                if (comboField.getCombo() == null || Utils.isNullOrEmpty(comboField.getCombo().getDefaultItem())) {
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FIELD,
                            A_FPSConstant.SUBCODE_VALUE_MUST_BE_ENCODE_BASE64_FORMAT
                    );
                }
            }
            comboField.setValue((String)processField.getValue());
        } else {
            if (comboField.getCombo() == null || Utils.isNullOrEmpty(comboField.getCombo().getDefaultItem())) {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_FIELD_COMBOBOX,
                        A_FPSConstant.SUBCODE_MISSING_DEFAULT_ITEMS_FOR_PROCESS
                );
            }
            comboField.setValue(comboField.getCombo().getDefaultItem());
        }
        //</editor-fold>

        return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, comboField);
    }
}
