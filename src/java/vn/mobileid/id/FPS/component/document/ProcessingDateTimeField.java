/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import fps_core.enumration.FieldTypeName;
import fps_core.objects.child.DateTimeFieldAttribute;
import fps_core.objects.core.ExtendedFieldAttribute;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import vn.mobileid.id.FPS.component.enterprise.ConnectorEnterprise;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.enumeration.Rule;
import vn.mobileid.id.FPS.object.APIKeyRule;
import vn.mobileid.id.FPS.object.Enterprise;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.ProcessingRequest;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.PolicyConfiguration;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class ProcessingDateTimeField extends ProcessingTextFormField<DateTimeFieldAttribute> {

    public ProcessingDateTimeField() {
        super(new DateTimeFieldAttribute());
    }

    @Override
    public FieldTypeName getFieldTypeName() {
        return FieldTypeName.DATETIME;
    }

    @Override
    public InternalResponse convert(
            User user,
            ExtendedFieldAttribute fieldData,
            ProcessingRequest.ProcessingFormFillRequest processField) throws Exception {

        //<editor-fold defaultstate="collapsed" desc="Get Enterprise Rule">
        InternalResponse response = ConnectorEnterprise.getKEYAPI(user.getScope(), "transaction");
        Enterprise enterprise = null;
        APIKeyRule rule = null;

        if (response.isValid()) {
            enterprise = response.getEnt();
            response = ConnectorEnterprise.getKeyAPIRule(
                    enterprise.getApiKeyRule(), 
                    "transactionID");

            if (response.isValid()) {
                rule = (APIKeyRule) response.getData();
            }
        }
        //</editor-fold>

        DateTimeFieldAttribute dateTime = new ObjectMapper().readValue(fieldData.getDetailValue(), DateTimeFieldAttribute.class);
        dateTime = (DateTimeFieldAttribute) fieldData.clone(dateTime, fieldData.getDimension());

        dateTime.setProcessBy(user.getAzp());
        SimpleDateFormat dateFormat = new SimpleDateFormat(PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat());
        dateTime.setProcessOn(dateFormat.format(Date.from(Instant.now())));

        //<editor-fold defaultstate="collapsed" desc="Generate Simple Date Format based on DateTime Field">
        String dateFormat2 = PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat();
        try {
            if (!Utils.isNullOrEmpty(dateTime.getFormat())) {
                dateFormat2 = dateTime.getFormat();
            }
        } catch (Exception e) {
            LogHandler.error(ProcessingDateTimeField.class,
                    "transaction",
                    "Cannot generate Date Format from Field Attribute => Using default");
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Check value in ProcessField and default value in Field is valid">
        if (processField != null && !Utils.isNullOrEmpty(processField.getValue())) {
            if (!(processField.getValue() instanceof String) && Utils.isNullOrEmpty(dateTime.getDefaultDate())) {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_FIELD,
                        A_FPSConstant.SUBCODE_VALUE_MUST_BE_ENCODE_BASE64_FORMAT
                );
            }
            if (enterprise != null && rule != null && rule.isRuleEnabled(Rule.IS_CONVERT_DATE)) {
                dateTime.setValue(Utils.convertISOStringToCustom((String) processField.getValue(), dateFormat2));
            } else {
                dateTime.setValue((String) processField.getValue());
            }
        } else {
            if (Utils.isNullOrEmpty(dateTime.getDefaultDate())) {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_FIELD_DATETIME,
                        A_FPSConstant.SUBCODE_MISSING_DEFAULT_ITEMS_FOR_PROCESS
                );
            }
            if (enterprise != null && rule != null && rule.isRuleEnabled(Rule.IS_CONVERT_DATE)) {
                dateTime.setValue(Utils.convertISOStringToCustom(dateTime.getDefaultDate(), dateFormat2));
            } else {
                dateTime.setValue(dateTime.getDefaultDate());
            }
        }
        //</editor-fold>

//        if (!Utils.isNullOrEmpty(value)) {
////            dateTime.setValue(Utils.convertISOStringToCustom(value, dateFormat2));
//            if (enterprise != null && rule != null && rule.isRuleEnabled(Rule.IS_CONVERT_DATE)) {
//                dateTime.setValue(Utils.convertISOStringToCustom(value, dateFormat2));
//            } else {
//                dateTime.setValue(value);
//            }
//        } else { 
//            try {
////                dateTime.setValue(Utils.convertISOStringToCustom(value, dateFormat2));
//                if (enterprise != null && rule != null && rule.isRuleEnabled(Rule.IS_CONVERT_DATE)) {
//                    dateTime.setValue(Utils.convertISOStringToCustom(dateTime.getDefaultDate(), dateFormat2));
//                } else {
//                    dateTime.setValue(dateTime.getDefaultDate());
//                }
//            } catch (Exception ex) {
//                return new InternalResponse(
//                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                        A_FPSConstant.CODE_FIELD_DATETIME,
//                        A_FPSConstant.SUBCODE_MISSING_DEFAULT_ITEMS_FOR_PROCESS
//                );
//            }
//        }
        return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, dateTime);
    }

    public static void main(String[] args) throws Exception {
        String inputDate = "2024-04-19T04:23:45Z";

        // Parse the input date string into an Instant object
        Instant instant = Instant.parse(inputDate);

        // Convert the Instant to LocalDateTime with the desired time zone (UTC in this case)
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));

        // Define the desired output format
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");

        // Format the LocalDateTime object into the desired output format
        String outputDate = dateTime.format(outputFormatter);

        // Output the formatted date string
        System.out.println("Formatted date: " + outputDate);
    }
}
