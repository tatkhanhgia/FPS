/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.services.others.responseMessage;

import fps_core.enumration.Language;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.controller.util.summary.micro.GetRemarkLanguage;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.RemarkLanguage;
import vn.mobileid.id.FPS.object.ResponseCode;
import vn.mobileid.id.FPS.systemManagement.LogHandler;
import vn.mobileid.id.FPS.systemManagement.Resources;
import vn.mobileid.id.FPS.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class ResponseMessageController implements ResponseMessageBuilder {

    private static final Logger LOG = LogManager.getLogger(ResponseMessageController.class);
    private List<String> messages = new ArrayList<>();

    //<editor-fold defaultstate="collapsed" desc="Create Error Message">
    /**
     * Send basic error Message with format: { "Message": "ErrorDescription" }
     *
     * @param message
     * @return
     */
    public static String errorMessage(
            String message) {
        StringBuilder builder = new StringBuilder();
        builder.append(JSONAnnotation.StartObject.getCharacter());
        builder.append(JSONAnnotation.writeStringField(
                "Message",
                message,
                false));
        builder.append(JSONAnnotation.EndObject.getCharacter());
        return builder.toString();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Create Error Message Advanced">
    /**
     * Send an advanced error Message with format: { "Error": "INVALID_TOKEN",
     * "Message": "Your token is expired" }
     *
     * @param error
     * @param errorDescription
     * @return
     */
    public static String errorMessageAdvanced(
            String error,
            String errorDescription) {
        StringBuilder builder = new StringBuilder();
        builder.append(JSONAnnotation.StartObject.getCharacter());
        builder.append(JSONAnnotation.writeStringField(
                "error",
                error,
                true));
        builder.append(JSONAnnotation.writeStringField(
                "error_description",
                errorDescription,
                false));
        builder.append(JSONAnnotation.EndObject.getCharacter());
        return builder.toString();
    }
    //</editor-fold>        

    //<editor-fold defaultstate="collapsed" desc="Create Error Message Advanced with Remark">
    /**
     * Send an advanced error Message with format: { "Error": "INVALID_TOKEN",
     * "Message": "Your token is expired" }
     *
     * @param error
     * @param errorDescription
     * @return
     */
    public static String errorMessageAdvanced_withRemark(
            String error,
            String errorDescription,
            List<RemarkLanguage> remarks
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append(JSONAnnotation.StartObject.getCharacter());
        builder.append(JSONAnnotation.writeStringField(
                "error",
                error,
                true));
        builder.append(JSONAnnotation.writeStringField(
                "error_description",
                errorDescription,
                false));
        if (!Utils.isNullOrEmpty(remarks)) {
            builder.append(JSONAnnotation.Comma.getCharacter());

            builder.append(JSONAnnotation.Quotation.getCharacter())
                    .append("remark")
                    .append(JSONAnnotation.Quotation.getCharacter())
                    .append(JSONAnnotation.Colon.getCharacter());
                    

            builder.append(JSONAnnotation.StartArray.getCharacter());
            for (int i = 0; i < remarks.size(); i++) {
                builder.append(JSONAnnotation.StartObject.getCharacter())
                        .append(JSONAnnotation.Quotation.getCharacter())
                        .append("language_name")
                        .append(JSONAnnotation.Quotation.getCharacter())
                        .append(JSONAnnotation.Colon.getCharacter())
                        .append(JSONAnnotation.Quotation.getCharacter())
                        .append(remarks.get(i).getLanguageName())
                        .append(JSONAnnotation.Quotation.getCharacter())
                        .append(JSONAnnotation.Comma.getCharacter())
                        .append(JSONAnnotation.Quotation.getCharacter())
                        .append("remark_message")
                        .append(JSONAnnotation.Quotation.getCharacter())
                        .append(JSONAnnotation.Colon.getCharacter())
                        .append(JSONAnnotation.Quotation.getCharacter())
                        .append(remarks.get(i).getValue())
                        .append(JSONAnnotation.Quotation.getCharacter())
                        .append(JSONAnnotation.EndObject.getCharacter());
                if (i < (remarks.size() - 1)) {
                    builder.append(JSONAnnotation.Comma.getCharacter());
                }
            }
            builder.append(JSONAnnotation.EndArray.getCharacter());
        }
        builder.append(JSONAnnotation.EndObject.getCharacter());
        return builder.toString();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Create Error Message with Remark">
    /**
     * Send basic error Message with format: { "Message": "ErrorDescription" }
     *
     * @param message
     * @return
     */
    public static String errorMessage_withRemark(
            String message,
            List<RemarkLanguage> remarks
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append(JSONAnnotation.StartObject.getCharacter());
        builder.append(JSONAnnotation.writeStringField(
                "Message",
                message,
                false));
        if (!Utils.isNullOrEmpty(remarks)) {
            builder.append(JSONAnnotation.Comma.getCharacter());

            builder.append(JSONAnnotation.Quotation.getCharacter())
                    .append("remark")
                    .append(JSONAnnotation.Quotation.getCharacter())
                    .append(JSONAnnotation.Colon.getCharacter());

            builder.append(JSONAnnotation.StartArray.getCharacter());
            for (int i = 0; i < remarks.size(); i++) {
                builder.append(JSONAnnotation.StartObject.getCharacter())
                        .append(JSONAnnotation.Quotation.getCharacter())
                        .append("language_name")
                        .append(JSONAnnotation.Quotation.getCharacter())
                        .append(JSONAnnotation.Colon.getCharacter())
                        .append(JSONAnnotation.Quotation.getCharacter())
                        .append(remarks.get(i).getLanguageName())
                        .append(JSONAnnotation.Quotation.getCharacter())
                        .append(JSONAnnotation.Comma.getCharacter())
                        .append(JSONAnnotation.Quotation.getCharacter())
                        .append("remark_message")
                        .append(JSONAnnotation.Quotation.getCharacter())
                        .append(JSONAnnotation.Colon.getCharacter())
                        .append(JSONAnnotation.Quotation.getCharacter())
                        .append(remarks.get(i).getValue())
                        .append(JSONAnnotation.Quotation.getCharacter())
                        .append(JSONAnnotation.EndObject.getCharacter());
                if (i < (remarks.size() - 1)) {
                    builder.append(JSONAnnotation.Comma.getCharacter());
                }
            }
            builder.append(JSONAnnotation.EndArray.getCharacter());
        }
        builder.append(JSONAnnotation.EndObject.getCharacter());
        return builder.toString();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Error Message Advanced">
    /**
     * Send basic error Message with Code - SubCode to get errorDes in DB
     *
     * @param code
     * @param subCode
     * @param responseMessage
     * @param lang
     * @param transactionID
     * @return
     */
    public static String getErrorMessageAdvanced(
            int code,
            int subCode,
            String responseMessage,
            String lang,
            String transactionID) {
        try {
            if(code == 0 && subCode ==0){
                return responseMessage;
            }
            String strCode = String.valueOf(code) + String.valueOf(subCode);
            ResponseCode responseCode = Resources.getResponseCodes().get(strCode);
            if (responseCode == null) {
                Resources.reloadResponseCodes();
                responseCode = Resources.getResponseCodes().get(strCode);
            }

            if (responseCode != null) {
                List<RemarkLanguage> remarks = new ArrayList<>();
                if (Language.getLanguage(lang) == null
                        || Language.getLanguage(lang).equals(Language.ENGLISH)) {
                    RemarkLanguage remarkEN = GetRemarkLanguage.getRemark(
                            strCode,
                            Language.ENGLISH);
                    if (remarkEN != null) {
                        remarks.add(remarkEN);
                    }
                } else {
                    RemarkLanguage remarkEN = GetRemarkLanguage.getRemark(
                            strCode,
                            Language.ENGLISH);
                    if (remarkEN != null) {
                        remarks.add(remarkEN);
                    }
                    RemarkLanguage remarkOther = GetRemarkLanguage.getRemark(
                            strCode,
                            Language.getLanguage(lang));

                    if (remarkOther != null) {
                        remarks.add(remarkOther);
                    }
                }
                if (responseCode.getCode() == null) {
                    return errorMessage_withRemark(responseCode.getCode_description(), remarks);
                } else {
                    return errorMessageAdvanced_withRemark(
                            responseCode.getCode(),
                            responseCode.getCode_description(),
                            remarks);
                }
            } else {
                LOG.error("Response code " + code + " is not defined in database.");
                return errorMessageAdvanced(
                        String.valueOf(code),
                        String.valueOf(subCode));
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("UNKNOWN EXCEPTION.");
            }
            return A_FPSConstant.INTERNAL_EXP_MESS;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Create List Error of ErrorField - Cannot fill all form field">
    public static String createErrorMessage(List<InternalResponse.InternalData> listOfErrorField) {
        ResponseCode responseCode = Resources.getResponseCodes().get(
                String.valueOf(A_FPSConstant.CODE_FIELD)
                + String.valueOf(A_FPSConstant.SUBCODE_CANNOT_FILL_ALL_FORM_FIELD));
        if (responseCode == null) {
            try {
                Resources.reloadResponseCodes();
                responseCode = Resources.getResponseCodes().get(String.valueOf(A_FPSConstant.CODE_FIELD)
                        + String.valueOf(A_FPSConstant.SUBCODE_CANNOT_FILL_ALL_FORM_FIELD));
            } catch (Exception ex) {
                return "{Cannot create Multiple Message}";
            }
        }

        StringBuilder builder = new StringBuilder();

        //Error
        builder.append(JSONAnnotation.StartObject.getCharacter())
                .append(JSONAnnotation.writeStringField(
                        "error",
                        responseCode.getCode_description(),
                        true));

        //ErrorDes
        builder.append(JSONAnnotation.writeString("error_description"));
        builder.append(JSONAnnotation.StartArray.getCharacter());

        for (int i = 0; i < listOfErrorField.size(); i++) {
            InternalResponse.InternalData error = listOfErrorField.get(i);
            String mess = "Cannot get Error Message for this Field Name";
            try {
                responseCode = Resources.getResponseCodes().get(error.getValue());
                if (responseCode == null) {
                    Resources.reloadResponseCodes();
                    responseCode = Resources.getResponseCodes().get(error.getValue());
                }
                if (responseCode == null) {
                    mess = (String) error.getValue();
                } else {
                    mess = responseCode.getCode_description();
                }
            } catch (Exception ex) {
            }

            builder.append(JSONAnnotation.StartObject.getCharacter());
            builder.append(JSONAnnotation.writeString("field_error"))
                    .append(":")
                    .append(JSONAnnotation.writeString(error.getName()))
                    .append(JSONAnnotation.Comma.getCharacter());

            builder.append(JSONAnnotation.writeString("field_error_description"))
                    .append(":")
                    .append(JSONAnnotation.writeString(mess));
            builder.append(JSONAnnotation.EndObject.getCharacter());

            if (i < (listOfErrorField.size() - 2)) {
                builder.append(",");
            }
        }

        builder.append(JSONAnnotation.EndArray.getCharacter())
                .append(JSONAnnotation.EndObject.getCharacter());

        return builder.toString();
    }
//</editor-fold>

    @Override
    public ResponseMessageBuilder writeStringField(String name, String data
    ) {
        messages.add(JSONAnnotation.writeStringField(name, data, false));
        return this;
    }

    @Override
    public ResponseMessageBuilder writeNumberField(String name, Number data
    ) {
        messages.add(JSONAnnotation.writeNumberField(name, data, false));
        return this;
    }

    @Override
    public String build() {
        StringBuilder builder = new StringBuilder();
        builder.append(JSONAnnotation.StartObject.getCharacter());
        for (int i = 0; i < this.messages.size(); i++) {
            builder.append(messages.get(i));
            if (i != (this.messages.size() - 1)) {
                builder.append(",");
            }
        }
        builder.append(JSONAnnotation.EndObject.getCharacter());
        return builder.toString();

    }

    public static enum JSONAnnotation {
        StartObject("{"),
        Quotation("\""),
        Colon(":"),
        Comma(","),
        EndObject("}"),
        StartArray("["),
        EndArray("]");

        private String character;

        private JSONAnnotation(String character) {
            this.character = character;
        }

        public String getCharacter() {
            return character;
        }

        public static String writeStringField(String name, String data, boolean appendComma) {
            StringBuilder buf = new StringBuilder();
            buf.append(writeString(name));
            buf.append(Colon.getCharacter());
            buf.append(writeString(data));
            if (appendComma) {
                buf.append(Comma.getCharacter());
            }
            return buf.toString();
        }

        public static String writeNumberField(String name, Number data, boolean appendComma) {
            StringBuilder buf = new StringBuilder();
            buf.append(writeString(name));
            buf.append(Colon.getCharacter());
            buf.append(data);
            if (appendComma) {
                buf.append(Comma.getCharacter());
            }
            return buf.toString();
        }

        public static String writeString(String data) {
            StringBuilder buf = new StringBuilder();
            buf.append(Quotation.getCharacter());
            for (char c : data.toCharArray()) {
                buf.append(c);
            }
            buf.append(Quotation.getCharacter());
            return buf.toString();
        }
    }

}
