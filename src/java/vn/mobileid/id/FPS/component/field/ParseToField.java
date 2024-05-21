/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.field;

import com.fasterxml.jackson.annotation.A;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fps_core.enumration.FieldTypeName;
import fps_core.objects.Font;
import fps_core.objects.child.AttachmentFieldAttribute;
import fps_core.objects.child.CameraFieldAttribute;
import fps_core.objects.child.ComboBoxFieldAttribute;
import fps_core.objects.child.DateTimeFieldAttribute;
import fps_core.objects.child.HyperLinkFieldAttribute;
import fps_core.objects.child.NumericStepperAttribute;
import fps_core.objects.child.RadioFieldAttribute;
import fps_core.objects.child.ToggleFieldAttribute;
import fps_core.objects.core.BasicFieldAttribute;
import fps_core.objects.core.CheckBoxFieldAttribute;
import fps_core.objects.core.CheckBoxFieldAttributeV2;
import fps_core.objects.core.FileFieldAttribute;
import fps_core.objects.core.InitialsFieldAttribute;
import fps_core.objects.core.QRFieldAttribute;
import fps_core.objects.core.SignatureFieldAttribute;
import fps_core.objects.core.TextFieldAttribute;
import fps_core.objects.interfaces.AbstractReplicateField;
import java.util.function.Consumer;
import java.util.function.Function;
import org.bouncycastle.util.encoders.Base64;
import vn.mobileid.id.FPS.QryptoService.object.ItemDetails;
import vn.mobileid.id.FPS.QryptoService.object.Item_IDPicture4Label;
import vn.mobileid.id.FPS.QryptoService.object.ItemsType;
import static vn.mobileid.id.FPS.QryptoService.object.ItemsType.Binary;
import static vn.mobileid.id.FPS.QryptoService.object.ItemsType.File;
import static vn.mobileid.id.FPS.QryptoService.object.ItemsType.ID_Picture_with_4_labels;
import vn.mobileid.id.FPS.component.document.CheckPayloadRequest;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.controller.ResponseMessageController;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.fieldAttribute.QryptoFieldAttribute;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.PolicyConfiguration;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public abstract class ParseToField {

    //<editor-fold defaultstate="collapsed" desc="ParseToField">
    /**
     * Từ url xác định loại field và parse vào loại đó Determine the type of
     * field and parse into that type. Based on URL
     *
     * @param url
     * @param payload
     * @param transactionId
     * @return
     */
    public static InternalResponse parseToField(
            String url,
            String payload,
            Boolean isCheckBasicField,
            Boolean isUpdate,
            String transactionId) {
        fps_core.utils.LogHandler.HierarchicalLog hierarchicalLog = new fps_core.utils.LogHandler.HierarchicalLog("Parse to Field");

        String typeField = url.substring(url.lastIndexOf("/") + 1);
        String temp = null;

        hierarchicalLog.addStartHeading1("Field from URL: " + typeField);

        switch (typeField) {
            case "in_person_signature":
                temp = FieldTypeName.INPERSON.getParentName();
            case "signature": {
                //<editor-fold defaultstate="collapsed" desc="Generate SignatureFieldAttribute from Payload">
//                hierarchicalLog.addStartHeading1("Start parse into " + typeField);

                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
                SignatureFieldAttribute field = null;
                try {
                    field = new ObjectMapper().readValue(payload, SignatureFieldAttribute.class);
                } catch (JsonProcessingException ex) {
                    hierarchicalLog.addEndHeading1("Parse into field fail");
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
                    ).setHierarchicalLog(hierarchicalLog);
                }
                hierarchicalLog.addEndHeading1("Parse into field successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check basic field">
                hierarchicalLog.addStartHeading1("Start check basic");
                if (isCheckBasicField) {
                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
                    if (!response.isValid()) {
                        hierarchicalLog.addEndHeading1("Checked fail");
                        return response.setHierarchicalLog(hierarchicalLog);
                    }
                }
                hierarchicalLog.addEndHeading1("Checked successfully");
                //</editor-fold>

                field.setType(Resources.getFieldTypes().get(
                        temp == null
                                ? FieldTypeName.SIGNATURE.getParentName()
                                : temp
                ));

                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());

                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field)
                        .setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
            case "date":
            case "datetime": {
                //<editor-fold defaultstate="collapsed" desc="Generate DateTime from Payload">
                hierarchicalLog.addStartHeading1("Start parse into " + typeField);

                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
                DateTimeFieldAttribute field = null;
                try {
                    field = new ObjectMapper().readValue(payload, DateTimeFieldAttribute.class);
                } catch (Exception ex) {
                    hierarchicalLog.addEndHeading1("Parse into field fail");
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
                    ).setHierarchicalLog(hierarchicalLog);
                }
                hierarchicalLog.addEndHeading1("Parse into field successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check basic field">
                hierarchicalLog.addStartHeading1("Start check basic");
                if (isCheckBasicField) {
                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
                    if (!response.isValid()) {
                        hierarchicalLog.addEndHeading1("Checked fail");
                        return response.setHierarchicalLog(hierarchicalLog);
                    }
                }
                hierarchicalLog.addEndHeading1("Checked successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check field type">
                if (!Utils.isNullOrEmpty(field.getTypeName())) {
                    hierarchicalLog.addStartHeading1("Start check field type");
                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.DATETIME);

                    if (!check) {
                        hierarchicalLog.addEndHeading1("Check field type fail");
                        return new InternalResponse(
                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                A_FPSConstant.CODE_FIELD_TEXT,
                                A_FPSConstant.SUBCODE_INVALID_TEXT_FIELD_TYPE
                        ).setHierarchicalLog(hierarchicalLog);
                    }
                    hierarchicalLog.addEndHeading1("Check field type successfully");
                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
                } else {
                    field.setType(Resources.getFieldTypes().get(FieldTypeName.DATETIME.getParentName()));
                }
                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Initial data of field">
                if (!isUpdate) {
                    if (field.getAlignment() == null) {
                        field.setDefaultAlignment();
                    }
                    if (field.getColor() == null) {
                        field.setColor("BLACK");
                    }
                    if (field.isReadOnly() == null) {
                        field.setReadOnly(false);
                    }
                    if (field.isMultiline() == null) {
                        field.setMultiline(false);
                    }
                    //<editor-fold defaultstate="collapsed" desc="Logger">
                    hierarchicalLog.addStartHeading1("Alignment: " + field.getAlignment());
                    hierarchicalLog.addStartHeading1("Text Color: " + field.getColor());
                    hierarchicalLog.addStartHeading1("Read Only: " + field.isReadOnly());
                    hierarchicalLog.addStartHeading1("Multiline: " + field.isMultiline());
                    //</editor-fold>
                }
                //</editor-fold>

                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field).setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
            case "text": {
                //<editor-fold defaultstate="collapsed" desc="Generate TextFieldAttribute from Payload">
                hierarchicalLog.addStartHeading1("Start parse into " + typeField);

                //<editor-fold defaultstate="collapsed" desc="Check type of TextField">
                String type = Utils.getFromJson("type", payload);
                Object checkDate = Utils.getFromJson_("date", payload);
                Object checkAddress = Utils.getFromJson_("address", payload);

                if (Utils.isNullOrEmpty(type) && !isUpdate) {
                    hierarchicalLog.addEndHeading1("Check type of textfield fail");
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FIELD_TEXT,
                            A_FPSConstant.SUBCODE_MISSING_TEXT_FIELD_TYPE
                    ).setHierarchicalLog(hierarchicalLog);
                }
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
                TextFieldAttribute field = null;
                try {
                    field = new ObjectMapper().readValue(payload, TextFieldAttribute.class);
                    if (type != null) {
                        if (checkDate != null || type.equalsIgnoreCase("datetime") || type.equalsIgnoreCase("date")) {
                            field = new ObjectMapper().readValue(payload, DateTimeFieldAttribute.class);
                        } else if (checkAddress != null || type.equalsIgnoreCase("hyperlink")) {
                            field = new ObjectMapper().readValue(payload, HyperLinkFieldAttribute.class);
                        }
                    }
                } catch (Exception ex) {
                    hierarchicalLog.addEndHeading1("Parse into field fail");
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
                    ).setHierarchicalLog(hierarchicalLog);
                }
                hierarchicalLog.addEndHeading1("Parse into field successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check basic field">
                hierarchicalLog.addStartHeading1("Start check basic");
                if (isCheckBasicField) {
                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
                    if (!response.isValid()) {
                        hierarchicalLog.addEndHeading1("Checked fail");
                        return response.setHierarchicalLog(hierarchicalLog);
                    }
                }
                hierarchicalLog.addEndHeading1("Checked successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check field type">
                if (!Utils.isNullOrEmpty(field.getTypeName())) {
                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.TEXTBOX);

                    if (!check) {
                        hierarchicalLog.addEndHeading1("Check field type fail");
                        return new InternalResponse(
                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                A_FPSConstant.CODE_FIELD_TEXT,
                                A_FPSConstant.SUBCODE_INVALID_TEXT_FIELD_TYPE
                        ).setHierarchicalLog(hierarchicalLog);
                    }
                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
                } else {
                    field.setType(Resources.getFieldTypes().get(FieldTypeName.TEXTBOX.getParentName()));
                }
                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Initial data of field">
                if (!isUpdate) {
                    if (field.getMaxLength() == null) {
                        field.setMaxLength(100);
                    }
                    if (field.getAlignment() == null) {
                        field.setDefaultAlignment();
                    }
                    if (field.getColor() == null) {
                        field.setColor("BLACK");
                    }
                    if (field.isReadOnly() == null) {
                        field.setReadOnly(false);
                    }
                    if (field.isMultiline() == null) {
                        field.setMultiline(false);
                    }
                    if (field.getFont() == null) {
                        field.setFont(Font.init());
                    }

                    //<editor-fold defaultstate="collapsed" desc="Logger">
                    hierarchicalLog.addStartHeading1("Alignment: " + field.getAlignment());
                    hierarchicalLog.addStartHeading1("Text Color: " + field.getColor());
                    hierarchicalLog.addStartHeading1("Read Only: " + field.isReadOnly());
                    hierarchicalLog.addStartHeading1("Multiline: " + field.isMultiline());
                    hierarchicalLog.addStartHeading1("Visible: " + field.getVisibleEnabled());
                    hierarchicalLog.addStartHeading1("Font name: " + field.getFont().getName());
                    hierarchicalLog.addStartHeading1("Font size: " + field.getFont().getSize());
                    //</editor-fold>
                }
                //</editor-fold>

                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field).setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
            case "checkbox": {
                //<editor-fold defaultstate="collapsed" desc="Generate CheckBoxFieldAttribute from Payload">
                hierarchicalLog.addStartHeading1("Start parse into " + typeField);

                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
                CheckBoxFieldAttribute field = null;
                try {
                    field = new ObjectMapper().readValue(payload, CheckBoxFieldAttribute.class);
                } catch (JsonProcessingException ex) {
                    hierarchicalLog.addEndHeading1("Parse into field fail");
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
                    ).setHierarchicalLog(hierarchicalLog);
                }
                hierarchicalLog.addEndHeading1("Parse into field successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check basic field">
                hierarchicalLog.addStartHeading1("Start check basic");
                if (isCheckBasicField) {
                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
                    if (!response.isValid()) {
                        hierarchicalLog.addEndHeading1("Checked fail");
                        return response.setHierarchicalLog(hierarchicalLog);
                    }
                }
                hierarchicalLog.addEndHeading1("Checked successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check field type">
                if (!Utils.isNullOrEmpty(field.getTypeName())) {
                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.CHECKBOX);

                    if (!check) {
                        hierarchicalLog.addEndHeading1("Check field type fail");
                        return new InternalResponse(
                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                A_FPSConstant.CODE_FIELD_CHECKBOX,
                                A_FPSConstant.SUBCODE_INVALID_CHECKBOX_FIELD_TYPE
                        ).setHierarchicalLog(hierarchicalLog);
                    }
                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
                } else {
                    field.setType(Resources.getFieldTypes().get(FieldTypeName.CHECKBOX.getParentName()));
                }
                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Initial data of field">
                if (!isUpdate) {
                    if (field.isChecked() == null) {
                        field.setChecked(false);
                    }
                    if (field.isReadOnly() == null) {
                        field.setReadOnly(false);
                    }

                    //<editor-fold defaultstate="collapsed" desc="Logger">
                    hierarchicalLog.addStartHeading1("Read Only: " + field.isReadOnly());
                    hierarchicalLog.addStartHeading1("Checked: " + field.isChecked());
                    //</editor-fold>
                }
                //</editor-fold>

                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field).setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
            case "checkboxV2": {
                //<editor-fold defaultstate="collapsed" desc="Generate CheckBoxFieldAttributeV2 from Payload">
                hierarchicalLog.addStartHeading1("Start parse into " + typeField);

                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
                CheckBoxFieldAttributeV2 field = null;
                try {
                    field = new ObjectMapper().readValue(payload, CheckBoxFieldAttributeV2.class);
                } catch (JsonProcessingException ex) {
                    hierarchicalLog.addEndHeading1("Parse into field fail");
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
                    ).setHierarchicalLog(hierarchicalLog);
                }
                hierarchicalLog.addEndHeading1("Parse into field successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check basic field">
                hierarchicalLog.addStartHeading1("Start check basic");
                if (isCheckBasicField) {
                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
                    if (!response.isValid()) {
                        hierarchicalLog.addEndHeading1("Checked fail");
                        return response.setHierarchicalLog(hierarchicalLog);
                    }
                }
                hierarchicalLog.addEndHeading1("Checked successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check field type">
                if (!Utils.isNullOrEmpty(field.getTypeName())) {
                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.CHECKBOXV2);

                    if (!check) {
                        hierarchicalLog.addEndHeading1("Check field type fail");
                        return new InternalResponse(
                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                A_FPSConstant.CODE_FIELD_CHECKBOX,
                                A_FPSConstant.SUBCODE_INVALID_CHECKBOX_FIELD_TYPE
                        ).setHierarchicalLog(hierarchicalLog);
                    }
                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
                } else {
                    field.setType(Resources.getFieldTypes().get(FieldTypeName.CHECKBOX.getParentName()));
                }
                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
                //</editor-fold>

                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field).setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
            case "radio": {
                //<editor-fold defaultstate="collapsed" desc="Generate RadioFieldAttribute from Payload">
                hierarchicalLog.addStartHeading1("Start parse into " + typeField);

                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
                RadioFieldAttribute field = null;
                try {
                    field = new ObjectMapper().readValue(payload, RadioFieldAttribute.class);
                } catch (JsonProcessingException ex) {
                    hierarchicalLog.addEndHeading1("Parse into field fail");
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
                    ).setHierarchicalLog(hierarchicalLog);
                }
                hierarchicalLog.addEndHeading1("Parse into field successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check basic field">
                hierarchicalLog.addStartHeading1("Start check basic");
                if (isCheckBasicField) {
                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
                    if (!response.isValid()) {
                        hierarchicalLog.addEndHeading1("Checked fail");
                        return response.setHierarchicalLog(hierarchicalLog);
                    }
                }
                hierarchicalLog.addEndHeading1("Checked successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check field type">
                if (!Utils.isNullOrEmpty(field.getTypeName())) {
                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.RADIOBOX);

                    if (!check) {
                        hierarchicalLog.addEndHeading1("Check field type fail");
                        return new InternalResponse(
                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                A_FPSConstant.CODE_FIELD_RADIO_BOX,
                                A_FPSConstant.SUBCODE_INVALID_TYPE_OF_RADIO
                        ).setHierarchicalLog(hierarchicalLog);
                    }
                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
                } else {
                    field.setType(Resources.getFieldTypes().get(FieldTypeName.RADIOBOX.getParentName()));
                }
                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Initial data of field">
                if (!isUpdate) {
                    if (field.isChecked() == null) {
                        field.setChecked(false);
                    }
                    if (field.isReadOnly() == null) {
                        field.setReadOnly(false);
                    }

                    //<editor-fold defaultstate="collapsed" desc="Logger">
                    hierarchicalLog.addStartHeading1("Read Only: " + field.isReadOnly());
                    hierarchicalLog.addStartHeading1("Checked: " + field.isChecked());
                    //</editor-fold>
                }
                //</editor-fold>

                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field).setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
            case "initial": {
                //<editor-fold defaultstate="collapsed" desc="Generate InitialsFieldAttribute from Payload">
                hierarchicalLog.addStartHeading1("Start parse into " + typeField);

                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
                InitialsFieldAttribute field = null;
                try {
                    field = new ObjectMapper().readValue(payload, InitialsFieldAttribute.class);
                } catch (JsonProcessingException ex) {
                    hierarchicalLog.addEndHeading1("Parse into field fail");
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
                    ).setHierarchicalLog(hierarchicalLog);
                }
                hierarchicalLog.addEndHeading1("Parse into field successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check basic field for initial">
                hierarchicalLog.addStartHeading1("Start check basic");
                if (isCheckBasicField) {
                    InternalResponse response = CheckPayloadRequest.checkAddInitialField(field, transactionId);
                    if (!response.isValid()) {
                        hierarchicalLog.addEndHeading1("Checked fail");
                        return response.setHierarchicalLog(hierarchicalLog);
                    }
                }
                hierarchicalLog.addEndHeading1("Checked successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check field type">
                field.setType(Resources.getFieldTypes().get(FieldTypeName.INITIAL.getParentName()));
                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Initial data of field">
                if (!isUpdate) {
                    if (field.isApplyToAll() == null) {
                        field.setApplyToAll(false);
                    }
                    if (field.isReplicateAllPages() == null) {
                        field.setReplicateAllPages(false);
                    }

                    //<editor-fold defaultstate="collapsed" desc="Logger">
                    hierarchicalLog.addStartHeading1("Apply to all: " + field.isApplyToAll());
                    hierarchicalLog.addStartHeading1("Replicate all pages: " + field.isReplicateAllPages());
                    //</editor-fold>
                }
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Upload image into FMS If need">
                if (field.getImage() != null && field.getImage().length()
                        > PolicyConfiguration.getInstant()
                                .getSystemConfig()
                                .getAttributes()
                                .get(0)
                                .getMaximumFile()) {
                    try {
                        hierarchicalLog.addStartHeading1("Start upload to FMS");
                        InternalResponse response = vn.mobileid.id.FMS.uploadToFMS(
                                Base64.decode(field.getImage()),
                                "png",
                                transactionId);
                        if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
                            hierarchicalLog.addEndHeading1("upload to FMS successfully");
                            String uuid = (String) response.getData();
                            field.setImage(uuid);
                        }

                    } catch (Exception ex) {
                        System.err.println("Cannot upload image from QR to FMS!. Using default");
                    }
                }
                //</editor-fold>

                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field).setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
            case "qrcode": {
                //<editor-fold defaultstate="collapsed" desc="Generate QRFieldAttribute from Payload">
                hierarchicalLog.addStartHeading1("Start parse into " + typeField);

                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
                QRFieldAttribute field = null;
                try {
                    field = new ObjectMapper().readValue(payload, QRFieldAttribute.class);
                } catch (JsonProcessingException ex) {
                    hierarchicalLog.addEndHeading1("Parse into field fail");
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
                    ).setHierarchicalLog(hierarchicalLog);
                }
                hierarchicalLog.addEndHeading1("Parse into field successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check basic field">
                hierarchicalLog.addStartHeading1("Start check basic");
                if (isCheckBasicField) {
                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
                    if (!response.isValid()) {
                        hierarchicalLog.addEndHeading1("Checked fail");
                        return response.setHierarchicalLog(hierarchicalLog);
                    }
                }
                hierarchicalLog.addEndHeading1("Checked successfully");
                //</editor-fold>

                if (Utils.isNullOrEmpty(field.getValue()) && !isUpdate) {
                    hierarchicalLog.addEndHeading1("Missing encode string of QR");
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FIELD_QR,
                            A_FPSConstant.SUBCODE_MISSING_ENCODE_STRING_OF_QR
                    ).setHierarchicalLog(hierarchicalLog);
                }

                //<editor-fold defaultstate="collapsed" desc="Check field type">
                if (!Utils.isNullOrEmpty(field.getTypeName())) {
                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.QR);

                    if (!check) {
                        hierarchicalLog.addEndHeading1("Check field type fail");
                        return new InternalResponse(
                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                A_FPSConstant.CODE_FIELD_QR,
                                A_FPSConstant.SUBCODE_INVALID_QR_TYTPE
                        ).setHierarchicalLog(hierarchicalLog);
                    }
                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
                } else {
                    field.setType(Resources.getFieldTypes().get(FieldTypeName.QR.getParentName()));
                }
                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Initial data of field">
                if (!isUpdate) {
                    if (field.IsTransparent() == null) {
                        field.setTransparent(false);
                    }
                    //<editor-fold defaultstate="collapsed" desc="Logger">
                    hierarchicalLog.addStartHeading1("Transparent: " + field.IsTransparent());
                    //</editor-fold>
                }
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Upload image QR into FMS if need">
                if (field.getImageQR() != null
                        && field.getImageQR().length()
                        > PolicyConfiguration.getInstant()
                                .getSystemConfig()
                                .getAttributes()
                                .get(0)
                                .getMaximumFile()) {
                    try {
                        hierarchicalLog.addStartHeading1("Start upload to FMS");
                        InternalResponse response = vn.mobileid.id.FMS.uploadToFMS(
                                Base64.decode(field.getImageQR()),
                                "png",
                                transactionId);
                        if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
                            hierarchicalLog.addEndHeading1("Upload to FMS successfully");
                            String uuid = (String) response.getData();
                            field.setImageQR(uuid);
                        }
                    } catch (Exception ex) {
                        System.err.println("Cannot upload image from QR to FMS!. Using default");
                    }
                }
                //</editor-fold>

                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field).setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
            case "qrcode-qrypto": {
                //<editor-fold defaultstate="collapsed" desc="Generate QryptoFieldAttribute from Payload">
                hierarchicalLog.addStartHeading1("Start parse into " + typeField);

                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
                QryptoFieldAttribute field = null;
                try {
                    field = new ObjectMapper().readValue(payload, QryptoFieldAttribute.class);
                } catch (JsonProcessingException ex) {
                    hierarchicalLog.addEndHeading1("Parse into field fail");
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
                    ).setHierarchicalLog(hierarchicalLog);
                }
                hierarchicalLog.addEndHeading1("Parse into field successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check basic field">
                hierarchicalLog.addStartHeading1("Start check basic");
                if (isCheckBasicField) {
                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
                    if (!response.isValid()) {
                        hierarchicalLog.addEndHeading1("Checked fail");
                        return response.setHierarchicalLog(hierarchicalLog);
                    }
                }
                hierarchicalLog.addEndHeading1("Checked successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check field type">
                if (!Utils.isNullOrEmpty(field.getTypeName())) {
                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.QRYPTO);

                    if (!check) {
                        hierarchicalLog.addEndHeading1("Check field type fail");
                        return new InternalResponse(
                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                A_FPSConstant.CODE_FIELD_QR,
                                A_FPSConstant.SUBCODE_INVALID_QR_TYTPE
                        ).setHierarchicalLog(hierarchicalLog);
                    }
                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
                } else {
                    field.setType(Resources.getFieldTypes().get(FieldTypeName.QRYPTO.getParentName()));
                }
                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="If item is not null => check type is file, upload it into FMS">
                if (!Utils.isNullOrEmpty(field.getItems())) {
                    hierarchicalLog.addStartHeading1("Start checking items in Qrypto");
                    try {
                        for (ItemDetails detail : field.getItems()) {
                            String file = null;
                            Item_IDPicture4Label.IDPicture4Label tempp = null;
                            switch (ItemsType.getItemsType(detail.getType())) {
                                case Binary:
                                case File: {
                                    file = (String) detail.getValue();
                                    break;
                                }
                                case ID_Picture_with_4_labels: {
                                    String temp_ = new ObjectMapper().writeValueAsString(detail.getValue());
                                    tempp = new ObjectMapper().readValue(temp_, Item_IDPicture4Label.IDPicture4Label.class);
                                    file = tempp.getBase64();
                                    break;
                                }
                                default: {
                                }
                            }
                            if (file != null) {
                                //<editor-fold defaultstate="collapsed" desc="Upload image into FMS If need">
                                if (file.length()
                                        > PolicyConfiguration.getInstant()
                                                .getSystemConfig()
                                                .getAttributes()
                                                .get(0)
                                                .getMaximumFile()) {
                                    try {
                                        hierarchicalLog.addStartHeading2("Upload image/file into FMS");
                                        InternalResponse response = vn.mobileid.id.FMS.uploadToFMS(
                                                Base64.decode(file),
                                                "png",
                                                transactionId);
                                        if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
                                            hierarchicalLog.addEndHeading2("Upload image/file into FMS successfully");
                                            String uuid = (String) response.getData();
                                            if (tempp != null) {
                                                tempp.setBase64(uuid);
                                                detail.setValue(tempp);
                                            } else {
                                                detail.setValue(uuid);
                                            }
                                        } else {
                                            hierarchicalLog.addEndHeading2("Upload image/file into FMS fail");
                                        }
                                    } catch (Exception ex) {
                                        System.err.println("Cannot upload image from QR to FMS!. Using default");
                                    }
                                }
                                //</editor-fold>
                            }
                        }
                    } catch (Exception ex) {
                        hierarchicalLog.addEndHeading1("Upload the image/file from QR into FMS fail");
                        LogHandler.error(ConnectorField.class, transactionId, ex);
                        return new InternalResponse(
                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                A_FPSConstant.CODE_FIELD_QR_Qrypto,
                                A_FPSConstant.SUBCODE_INVALID_TYPE_OF_ITEM
                        ).setException(ex);
                    }
                    hierarchicalLog.addEndHeading1("Checking items in Qrypto successfully");
                }
                //</editor-fold> 

                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field).setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
            case "stamp": {
                //<editor-fold defaultstate="collapsed" desc="Generate FileFieldAttribute from Payload">
                hierarchicalLog.addStartHeading1("Start parse into " + typeField);

                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
                FileFieldAttribute field = null;
                try {
                    field = new ObjectMapper().readValue(payload, FileFieldAttribute.class);
                } catch (JsonProcessingException ex) {
                    hierarchicalLog.addEndHeading1("Parse into field fail");
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
                    ).setHierarchicalLog(hierarchicalLog);
                }
                hierarchicalLog.addEndHeading1("Parse into field successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check basic field">
                hierarchicalLog.addStartHeading1("Start check basic");
                if (isCheckBasicField) {
                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
                    if (!response.isValid()) {
                        hierarchicalLog.addEndHeading1("Checked fail");
                        return response.setHierarchicalLog(hierarchicalLog);
                    }
                }
                hierarchicalLog.addEndHeading1("Checked successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check field type">
                if (!Utils.isNullOrEmpty(field.getTypeName())) {
                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.STAMP);

                    if (!check) {
                        hierarchicalLog.addEndHeading1("Check field type fail");
                        return new InternalResponse(
                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                A_FPSConstant.CODE_FIELD_STAMP,
                                A_FPSConstant.SUBCODE_INVALID_STAMP_FIELD_TYPE
                        ).setHierarchicalLog(hierarchicalLog);
                    }
                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
                } else {
                    field.setType(Resources.getFieldTypes().get(FieldTypeName.STAMP.getParentName()));
                }
                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Initial data of field">
                if (!isUpdate) {
                    if (field.isApplyToAll() == null) {
                        field.setApplyToAll(false);
                    }
                    if (field.isReplicateAllPages() == null) {
                        field.setReplicateAllPages(false);
                    }

                    //<editor-fold defaultstate="collapsed" desc="Logger">
                    hierarchicalLog.addStartHeading1("Apply to all: " + field.isApplyToAll());
                    hierarchicalLog.addStartHeading1("Replicate all pages: " + field.isReplicateAllPages());
                    //</editor-fold>
                }
                //</editor-fold>

                if (!Utils.isNullOrEmpty(field.getFile())) {
                    //<editor-fold defaultstate="collapsed" desc="Upload into FMS if need">
                    if (field.getFile() != null && field.getFile().length()
                            > PolicyConfiguration.getInstant()
                                    .getSystemConfig()
                                    .getAttributes()
                                    .get(0)
                                    .getMaximumFile()) {
                        try {
                            hierarchicalLog.addStartHeading2("Start upload image/file into FMS");
                            InternalResponse response = vn.mobileid.id.FMS.uploadToFMS(
                                    Base64.decode(field.getFile()),
                                    "png",
                                    transactionId);
                            if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
                                hierarchicalLog.addEndHeading2("Upload successfully");
                                String uuid = (String) response.getData();
                                field.setFile(uuid);
                            } else {
                                hierarchicalLog.addEndHeading2("Upload fail");
                            }
                        } catch (Exception ex) {
                            hierarchicalLog.addEndHeading2("Cannot upload image from ImageField to FMS!. Using default");
                        }
                    }
                    //</editor-fold>
                }

                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field).setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
            case "camera": {
                //<editor-fold defaultstate="collapsed" desc="Generate CameraFieldAttribute from Payload">
                hierarchicalLog.addStartHeading1("Start parse into " + typeField);

                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
                CameraFieldAttribute field = null;
                try {
                    field = new ObjectMapper().readValue(payload, CameraFieldAttribute.class);
                } catch (JsonProcessingException ex) {
                    hierarchicalLog.addEndHeading1("Parse into field fail");
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
                    ).setHierarchicalLog(hierarchicalLog);
                }
                hierarchicalLog.addEndHeading1("Parse into field successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check basic field">
                hierarchicalLog.addStartHeading1("Start check basic");
                if (isCheckBasicField) {
                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
                    if (!response.isValid()) {
                        hierarchicalLog.addEndHeading1("Checked fail");
                        return response.setHierarchicalLog(hierarchicalLog);
                    }
                }
                hierarchicalLog.addEndHeading1("Checked successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check field type">
                if (!Utils.isNullOrEmpty(field.getTypeName())) {
                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.CAMERA);

                    if (!check) {
                        hierarchicalLog.addEndHeading1("Check field type fail");
                        return new InternalResponse(
                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                A_FPSConstant.CODE_FIELD_CAMERA,
                                A_FPSConstant.SUBCODE_INVALID_CAMERA_FIELD_TYPE
                        ).setHierarchicalLog(hierarchicalLog);
                    }
                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
                } else {
                    field.setType(Resources.getFieldTypes().get(FieldTypeName.CAMERA.getParentName()));
                }
                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Initial data of field">
                if (!isUpdate) {
                    if (field.isShowIcon() == null) {
                        field.setShowIcon(false);
                    }
                    if (field.isApplyToAll() == null) {
                        field.setApplyToAll(false);
                    }
                    if (field.isReplicateAllPages() == null) {
                        field.setReplicateAllPages(false);
                    }

                    //<editor-fold defaultstate="collapsed" desc="Logger">
                    hierarchicalLog.addStartHeading1("Is show icon: " + field.isShowIcon());
                    hierarchicalLog.addStartHeading1("Apply to all: " + field.isApplyToAll());
                    hierarchicalLog.addStartHeading1("Replicate all pages: " + field.isReplicateAllPages());
                    //</editor-fold>
                }
                //</editor-fold>

                if (!Utils.isNullOrEmpty(field.getFile())) {
                    //<editor-fold defaultstate="collapsed" desc="Upload into FMS if need">
                    if (field.getFile() != null && field.getFile().length()
                            > PolicyConfiguration.getInstant()
                                    .getSystemConfig()
                                    .getAttributes()
                                    .get(0)
                                    .getMaximumFile() / 2) {
                        try {
                            hierarchicalLog.addStartHeading2("Upload image into FMS");
                            InternalResponse response = vn.mobileid.id.FMS.uploadToFMS(
                                    Base64.decode(field.getFile()),
                                    "png",
                                    transactionId);
                            if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
                                hierarchicalLog.addEndHeading2("Upload successfully");
                                String uuid = (String) response.getData();
                                field.setFile(uuid);
                            } else {
                                hierarchicalLog.addEndHeading2("Upload fail");
                            }
                        } catch (Exception ex) {
                            hierarchicalLog.addEndHeading2("Cannot upload image from ImageField to FMS!. Using default");
                        }
                    }
                    //</editor-fold>
                }

                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field).setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
            case "attachment": {
                //<editor-fold defaultstate="collapsed" desc="Generate Attachment from Payload">
                hierarchicalLog.addStartHeading1("Start parse into " + typeField);

                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
                AttachmentFieldAttribute field = null;
                try {
                    field = new ObjectMapper().readValue(payload, AttachmentFieldAttribute.class);
                } catch (JsonProcessingException ex) {
                    hierarchicalLog.addEndHeading1("Parse into field fail");
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
                    ).setHierarchicalLog(hierarchicalLog);
                }
                hierarchicalLog.addEndHeading1("Parse into field successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check basic field">
                hierarchicalLog.addStartHeading1("Start check basic");
                if (isCheckBasicField) {
                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
                    if (!response.isValid()) {
                        hierarchicalLog.addEndHeading1("Checked fail");
                        return response.setHierarchicalLog(hierarchicalLog);
                    }
                }
                hierarchicalLog.addEndHeading1("Checked successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check field type">
                if (!Utils.isNullOrEmpty(field.getTypeName())) {
                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.ATTACHMENT);

                    if (!check) {
                        hierarchicalLog.addEndHeading1("Check field type fail");
                        return new InternalResponse(
                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                A_FPSConstant.CODE_FIELD_ATTACHMENT,
                                A_FPSConstant.SUBCODE_INVALID_ATTACHMENT_FIELD_TYPE
                        ).setHierarchicalLog(hierarchicalLog);
                    }
                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
                } else {
                    field.setType(Resources.getFieldTypes().get(FieldTypeName.ATTACHMENT.getParentName()));
                }
                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Initial data of field">
                if (!isUpdate) {
                    if (field.isShowIcon() == null) {
                        field.setIsShowIcon(false);
                    }
                    if (field.isApplyToAll() == null) {
                        field.setApplyToAll(false);
                    }
                    if (field.isReplicateAllPages() == null) {
                        field.setReplicateAllPages(false);
                    }
                    //<editor-fold defaultstate="collapsed" desc="Logger">
                    hierarchicalLog.addStartHeading1("Is show icon: " + field.isShowIcon());
                    hierarchicalLog.addStartHeading1("Apply to all: " + field.isApplyToAll());
                    hierarchicalLog.addStartHeading1("Replicate all pages: " + field.isReplicateAllPages());
                    //</editor-fold>
                }
                //</editor-fold> 

                if (field.getFileData() != null) {
                    hierarchicalLog.addStartHeading1("Start checking file data");

                    //<editor-fold defaultstate="collapsed" desc="Check data of File">
                    hierarchicalLog.addStartHeading2("Checking file extension + file name");
                    if (Utils.isNullOrEmpty(field.getFileExtension()) && Utils.isNullOrEmpty(field.getFileName())) {
                        hierarchicalLog.addEndHeading2("Checking fail");
                        return new InternalResponse(
                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                A_FPSConstant.CODE_FIELD_ATTACHMENT,
                                A_FPSConstant.SUBCODE_MISSING_EXTENSION
                        ).setHierarchicalLog(hierarchicalLog);
                    }
                    hierarchicalLog.addStartHeading2("Checking file extension + file name successfully");

                    hierarchicalLog.addStartHeading2("Checking file data");
                    if (Utils.isNullOrEmpty(field.getFile())) {
                        hierarchicalLog.addEndHeading2("Checking file data fail");
                        return new InternalResponse(
                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                A_FPSConstant.CODE_FIELD_ATTACHMENT,
                                A_FPSConstant.SUBCODE_MISSING_FILE_DATA_OF_ATTACHMENT
                        ).setHierarchicalLog(hierarchicalLog);
                    }
                    hierarchicalLog.addStartHeading2("Checking file data successfully");

                    hierarchicalLog.addStartHeading2("Checking file extension");
                    if (Utils.isNullOrEmpty(field.getFileExtension())) {
                        try {
                            String fileName = field.getFileName();
                            String[] splits = fileName.split("\\.");
                            field.setFileExtension(splits[splits.length - 1]);
                        } catch (Exception e) {
                            hierarchicalLog.addEndHeading2("Checking file extension fail");
                            return new InternalResponse(
                                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                    A_FPSConstant.CODE_FIELD_ATTACHMENT,
                                    A_FPSConstant.SUBCODE_MISSING_EXTENSION
                            ).setHierarchicalLog(hierarchicalLog);
                        }
                    }
                    hierarchicalLog.addStartHeading2("Checking file extension successfully");
                    //</editor-fold>

                    //<editor-fold defaultstate="collapsed" desc="Upload into FMS if need">
                    if (field.getFile() != null && field.getFile().length()
                            > PolicyConfiguration.getInstant()
                                    .getSystemConfig()
                                    .getAttributes()
                                    .get(0)
                                    .getMaximumFile()) {
                        try {
                            hierarchicalLog.addStartHeading2("Upload file into FMS");
                            InternalResponse response = vn.mobileid.id.FMS.uploadToFMS(
                                    Base64.decode(field.getFile()),
                                    field.getFileExtension(),
                                    transactionId);
                            if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
                                hierarchicalLog.addStartHeading2("Upload successfully");
                                String uuid = (String) response.getData();
                                field.setFile(uuid);
                            } else {
                                hierarchicalLog.addStartHeading2("Upload fail");
                            }
                        } catch (Exception ex) {
                            hierarchicalLog.addStartHeading2("Cannot upload image from ImageField to FMS!. Using default");
                        }
                    }
                    //</editor-fold>

                    hierarchicalLog.addEndHeading1("Checking file data successfully");
                }

                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field).setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
            case "hyperlink": {
                //<editor-fold defaultstate="collapsed" desc="Generate HyperLinkFieldAttribute from Payload">
                hierarchicalLog.addStartHeading1("Start parse into " + typeField);

                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
                HyperLinkFieldAttribute field = null;
                try {
                    field = new ObjectMapper().readValue(payload, HyperLinkFieldAttribute.class);
                } catch (Exception ex) {
                    hierarchicalLog.addEndHeading1("Parse into field fail");
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
                    ).setHierarchicalLog(hierarchicalLog);
                }
                hierarchicalLog.addEndHeading1("Parse into field successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check basic field">
                hierarchicalLog.addStartHeading1("Start check basic");
                if (isCheckBasicField) {
                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
                    if (!response.isValid()) {
                        hierarchicalLog.addEndHeading1("Checked fail");
                        return response.setHierarchicalLog(hierarchicalLog);
                    }
                }
                hierarchicalLog.addEndHeading1("Checked successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check field type">
                if (!Utils.isNullOrEmpty(field.getTypeName())) {
                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.HYPERLINK);
                    if (!check) {
                        hierarchicalLog.addEndHeading1("Check field type fail");
                        return new InternalResponse(
                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                A_FPSConstant.CODE_FIELD_HYPERLINK,
                                A_FPSConstant.SUBCODE_INVALID_HYPERLINK_TYPE
                        ).setHierarchicalLog(hierarchicalLog);
                    }
                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
                } else {
                    field.setType(Resources.getFieldTypes().get(FieldTypeName.HYPERLINK.getParentName()));
                }
                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Initial data of field">
                if (!isUpdate) {
                    if (field.getAlignment() == null) {
                        field.setDefaultAlignment();
                    }
                    if (field.getColor() == null) {
                        field.setColor("BLACK");
                    }
                    if (field.isReadOnly() == null) {
                        field.setReadOnly(false);
                    }
                    if (field.isMultiline() == null) {
                        field.setMultiline(false);
                    }
                    //<editor-fold defaultstate="collapsed" desc="Logger">
                    hierarchicalLog.addStartHeading1("Alignment: " + field.getAlignment());
                    hierarchicalLog.addStartHeading1("Text Color: " + field.getColor());
                    hierarchicalLog.addStartHeading1("Read Only: " + field.isReadOnly());
                    hierarchicalLog.addStartHeading1("Multiline: " + field.isMultiline());
                    //</editor-fold>
                }
                //</editor-fold>

                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field).setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
            case "combo": {
                //<editor-fold defaultstate="collapsed" desc="Generate ComboBox Field from Payload">
                hierarchicalLog.addStartHeading1("Start parse into " + typeField);

                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
                ComboBoxFieldAttribute field = null;
                try {
                    field = new ObjectMapper().readValue(payload, ComboBoxFieldAttribute.class);
                } catch (Exception ex) {
                    LogHandler.error(ConnectorField.class, transactionId, ex);
                    hierarchicalLog.addEndHeading1("Parse into field fail");
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
                    ).setHierarchicalLog(hierarchicalLog);
                }
                hierarchicalLog.addEndHeading1("Parse into field successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check basic field">
                hierarchicalLog.addStartHeading1("Start check basic");
                if (isCheckBasicField) {
                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
                    if (!response.isValid()) {
                        hierarchicalLog.addEndHeading1("Checked fail");
                        return response.setHierarchicalLog(hierarchicalLog);
                    }
                }
                hierarchicalLog.addEndHeading1("Checked successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check field type">
                if (!Utils.isNullOrEmpty(field.getTypeName())) {
                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.COMBOBOX);
                    if (!check) {
                        hierarchicalLog.addEndHeading1("Check field type fail");
                        return new InternalResponse(
                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                A_FPSConstant.CODE_FIELD_COMBOBOX,
                                A_FPSConstant.SUBCODE_INVALID_COMBOBOX_FIELD_TYPE
                        ).setHierarchicalLog(hierarchicalLog);
                    }
                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
                } else {
                    field.setType(Resources.getFieldTypes().get(FieldTypeName.COMBOBOX.getParentName()));
                }
                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Initial data of field">
                if (!isUpdate) {
                    if (field.getAlignment() == null) {
                        field.setDefaultAlignment();
                    }
                    if (field.getColor() == null) {
                        field.setColor("BLACK");
                    }
                    if (field.isReadOnly() == null) {
                        field.setReadOnly(false);
                    }
                    if (field.isMultiline() == null) {
                        field.setMultiline(false);
                    }
                    //<editor-fold defaultstate="collapsed" desc="Logger">
                    hierarchicalLog.addStartHeading1("Alignment: " + field.getAlignment());
                    hierarchicalLog.addStartHeading1("Text Color: " + field.getColor());
                    hierarchicalLog.addStartHeading1("Read Only: " + field.isReadOnly());
                    hierarchicalLog.addStartHeading1("Multiline: " + field.isMultiline());
                    //</editor-fold>
                }
                //</editor-fold>

                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field).setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
            case "toggle": {
                //<editor-fold defaultstate="collapsed" desc="Generate Toogle Field from Payload">
                hierarchicalLog.addStartHeading1("Start parse into " + typeField);

                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
                ToggleFieldAttribute field = null;
                try {
                    field = new ObjectMapper().readValue(payload, ToggleFieldAttribute.class);
                } catch (Exception ex) {
                    LogHandler.error(ConnectorField.class, transactionId, ex);
                    hierarchicalLog.addEndHeading1("Parse into field fail");
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
                    ).setHierarchicalLog(hierarchicalLog);
                }
                hierarchicalLog.addEndHeading1("Parse into field successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check basic field">
                hierarchicalLog.addStartHeading1("Start check basic");
                if (isCheckBasicField) {
                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
                    if (!response.isValid()) {
                        hierarchicalLog.addEndHeading1("Checked fail");
                        return response.setHierarchicalLog(hierarchicalLog);
                    }
                }
                hierarchicalLog.addEndHeading1("Checked successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check field type">
                if (!Utils.isNullOrEmpty(field.getTypeName())) {
                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.TOGGLE);

                    if (!check) {
                        hierarchicalLog.addEndHeading1("Check field type fail");
                        return new InternalResponse(
                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                A_FPSConstant.CODE_FIELD_TOGGLE,
                                A_FPSConstant.SUBCODE_INVALID_TOGGLE_TYPE
                        ).setHierarchicalLog(hierarchicalLog);
                    }
                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
                } else {
                    field.setType(Resources.getFieldTypes().get(FieldTypeName.TOGGLE.getParentName()));
                }
                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Initial data of field">
                if (!isUpdate) {
                    if (field.getAlignment() == null) {
                        field.setDefaultAlignment();
                    }
                    if (field.getColor() == null) {
                        field.setColor("BLACK");
                    }
                    if (field.isReadOnly() == null) {
                        field.setReadOnly(false);
                    }
                    if (field.isMultiline() == null) {
                        field.setMultiline(false);
                    }
                    //<editor-fold defaultstate="collapsed" desc="Logger">
                    hierarchicalLog.addStartHeading1("Alignment: " + field.getAlignment());
                    hierarchicalLog.addStartHeading1("Text Color: " + field.getColor());
                    hierarchicalLog.addStartHeading1("Read Only: " + field.isReadOnly());
                    hierarchicalLog.addStartHeading1("Multiline: " + field.isMultiline());
                    //</editor-fold>
                }
                //</editor-fold>

                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field);
                //</editor-fold>
            }
            case "numeric_stepper": {
                //<editor-fold defaultstate="collapsed" desc="Generate Stepper Field from Payload">
                hierarchicalLog.addStartHeading1("Start parse into " + typeField);

                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
                NumericStepperAttribute field = null;
                try {
                    field = new ObjectMapper().readValue(payload, NumericStepperAttribute.class);
                } catch (Exception ex) {
                    LogHandler.error(ConnectorField.class, transactionId, ex);
                    hierarchicalLog.addEndHeading1("Parse into field fail");
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
                    ).setHierarchicalLog(hierarchicalLog);
                }
                hierarchicalLog.addEndHeading1("Parse into field successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check basic field">
                hierarchicalLog.addStartHeading1("Start check basic");
                if (isCheckBasicField) {
                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
                    if (!response.isValid()) {
                        hierarchicalLog.addEndHeading1("Checked fail");
                        return response.setHierarchicalLog(hierarchicalLog);
                    }
                }
                hierarchicalLog.addEndHeading1("Checked successfully");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check field type">
                if (!Utils.isNullOrEmpty(field.getTypeName())) {
                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.NUMERIC_STEP);

                    if (!check) {
                        hierarchicalLog.addEndHeading1("Check field type fail");
                        return new InternalResponse(
                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                A_FPSConstant.CODE_FIELD_NUMERIC_STEPPER,
                                A_FPSConstant.SUBCODE_INVALID_NUMERIC_TYPE
                        ).setHierarchicalLog(hierarchicalLog);
                    }
                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
                } else {
                    field.setType(Resources.getFieldTypes().get(FieldTypeName.NUMERIC_STEP.getParentName()));
                }
                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Initial data of field">
                if (!isUpdate) {
                    if (field.getAlignment() == null) {
                        field.setDefaultAlignment();
                    }
                    if (field.getColor() == null) {
                        field.setColor("BLACK");
                    }
                    if (field.isReadOnly() == null) {
                        field.setReadOnly(false);
                    }
                    if (field.isMultiline() == null) {
                        field.setMultiline(false);
                    }
                    //<editor-fold defaultstate="collapsed" desc="Logger">
                    hierarchicalLog.addStartHeading1("Alignment: " + field.getAlignment());
                    hierarchicalLog.addStartHeading1("Text Color: " + field.getColor());
                    hierarchicalLog.addStartHeading1("Read Only: " + field.isReadOnly());
                    hierarchicalLog.addStartHeading1("Multiline: " + field.isMultiline());
                    //</editor-fold>
                }
                //</editor-fold>

                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field).setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
        }
        return new InternalResponse(A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                new ResponseMessageController().writeStringField("error", "This type of Field not provide yet"))
                .setHierarchicalLog(hierarchicalLog);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="ParseToField Version 2">
    /**
     * Từ url xác định loại field và parse vào loại đó Determine the type of
     * field and parse into that type. Based on URL
     *
     * @param url
     * @param payload
     * @param transactionId
     * @return
     */
    public static InternalResponse parseToFieldV2(
            String url,
            String payload,
            Boolean isCheckBasicField,
            Boolean isUpdate,
            String transactionId) {
        fps_core.utils.LogHandler.HierarchicalLog hierarchicalLog = new fps_core.utils.LogHandler.HierarchicalLog("Parse to Field Version 2");

        String typeField = url.substring(url.lastIndexOf("/") + 1);

        hierarchicalLog.addStartHeading1("Field from URL: " + typeField);
        InternalResponse parseV2 = null;

        hierarchicalLog.addStartHeading1("Start parse into " + typeField);
        switch (typeField) {
            case "in_person_signature":
                //<editor-fold defaultstate="collapsed" desc="Generate SignatureFieldAttribute from Payload">
                parseV2 = createBufferer(new SignatureFieldAttribute(),
                        null)
                        .templateParseTopField(
                                FieldTypeName.INPERSON,
                                payload,
                                true,
                                transactionId);

                hierarchicalLog.addChildHierarchicalLog(parseV2.getHierarchicalLog());
                return parseV2.setHierarchicalLog(hierarchicalLog);
            //</editor-fold>
            case "signature": {
                //<editor-fold defaultstate="collapsed" desc="Generate SignatureFieldAttribute from Payload">
                parseV2 = createBufferer(new SignatureFieldAttribute(),
                        null)
                        .templateParseTopField(
                                FieldTypeName.SIGNATURE,
                                payload,
                                true,
                                transactionId);

                hierarchicalLog.addChildHierarchicalLog(parseV2.getHierarchicalLog());
                return parseV2.setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
            case "date":
            case "datetime": {
                //<editor-fold defaultstate="collapsed" desc="Generate DateTimeFieldAttribute from Payload">
                parseV2 = createBufferer(new DateTimeFieldAttribute(),
                        field -> {
                            //<editor-fold defaultstate="collapsed" desc="Initial lambda method">
                            if (field.getAlignment() == null) {
                                field.setDefaultAlignment();
                            }
                            if (field.getColor() == null) {
                                field.setColor("BLACK");
                            }
                            if (field.isReadOnly() == null) {
                                field.setReadOnly(false);
                            }
                            if (field.isMultiline() == null) {
                                field.setMultiline(false);
                            }
                            //</editor-fold>
                        })
                        .templateParseTopField(
                                FieldTypeName.DATETIME,
                                payload,
                                true,
                                transactionId);

                hierarchicalLog.addChildHierarchicalLog(parseV2.getHierarchicalLog());
                return parseV2.setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
            case "text": {
                //<editor-fold defaultstate="collapsed" desc="Generate TextFieldAttribute from Payload">
                parseV2 = createBufferer(new TextFieldAttribute(),
                        field -> {
                            //<editor-fold defaultstate="collapsed" desc="Initial lambda method">
                            if (field.getMaxLength() == null) {
                                field.setMaxLength(100);
                            }
                            if (field.getAlignment() == null) {
                                field.setDefaultAlignment();
                            }
                            if (field.getColor() == null) {
                                field.setColor("BLACK");
                            }
                            if (field.isReadOnly() == null) {
                                field.setReadOnly(false);
                            }
                            if (field.isMultiline() == null) {
                                field.setMultiline(false);
                            }
                            if (field.getFont() == null) {
                                field.setFont(Font.init());
                            }
                            //</editor-fold>
                        })
                        .templateParseTopField(
                                FieldTypeName.TEXTBOX,
                                payload,
                                true,
                                transactionId);

                hierarchicalLog.addChildHierarchicalLog(parseV2.getHierarchicalLog());
                return parseV2.setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
            case "checkbox": {
                //<editor-fold defaultstate="collapsed" desc="Generate CheckboxFieldAttribute from Payload">
                parseV2 = createBufferer(new CheckBoxFieldAttribute(),
                        field -> {
                            //<editor-fold defaultstate="collapsed" desc="Initial lambda method">
                            if (field.isChecked() == null) {
                                field.setChecked(false);
                            }
                            if (field.isReadOnly() == null) {
                                field.setReadOnly(false);
                            }
                            //</editor-fold>
                        })
                        .templateParseTopField(
                                FieldTypeName.CHECKBOX,
                                payload,
                                true,
                                transactionId);

                hierarchicalLog.addChildHierarchicalLog(parseV2.getHierarchicalLog());
                return parseV2.setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
            case "checkboxV2": {
                //<editor-fold defaultstate="collapsed" desc="Generate CheckboxFieldAttribute from Payload">
                parseV2 = createBufferer(new CheckBoxFieldAttributeV2(),
                        null)
                        .templateParseTopField(
                                FieldTypeName.CHECKBOX,
                                payload,
                                true,
                                transactionId);

                hierarchicalLog.addChildHierarchicalLog(parseV2.getHierarchicalLog());
                return parseV2.setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
            case "radio": {
                //<editor-fold defaultstate="collapsed" desc="Generate RadioFieldAttribute from Payload">
                parseV2 = createBufferer(new RadioFieldAttribute(),
                        field -> {
                            //<editor-fold defaultstate="collapsed" desc="Initial lambda method">
                            if (field.isChecked() == null) {
                                field.setChecked(false);
                            }
                            if (field.isReadOnly() == null) {
                                field.setReadOnly(false);
                            }
                            //</editor-fold>
                        })
                        .templateParseTopField(
                                FieldTypeName.RADIOBOX,
                                payload,
                                true,
                                transactionId);

                hierarchicalLog.addChildHierarchicalLog(parseV2.getHierarchicalLog());
                return parseV2.setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
            case "initial": {
                //<editor-fold defaultstate="collapsed" desc="Generate InitialFieldAttribute from Payload">
                parseV2 = createBufferer(new InitialsFieldAttribute(),
                        field -> {
                            //<editor-fold defaultstate="collapsed" desc="Initial lambda method">
                            if (field.isApplyToAll() == null) {
                                field.setApplyToAll(false);
                            }
                            if (field.isReplicateAllPages() == null) {
                                field.setReplicateAllPages(false);
                            }
                            //<editor-fold defaultstate="collapsed" desc="Upload image into FMS If need">
                            if (field.getImage() != null && field.getImage().length()
                            > PolicyConfiguration.getInstant()
                                    .getSystemConfig()
                                    .getAttributes()
                                    .get(0)
                                    .getMaximumFile()) {
                                try {
                                    hierarchicalLog.addStartHeading1("Start upload to FMS");
                                    InternalResponse response = vn.mobileid.id.FMS.uploadToFMS(
                                            Base64.decode(field.getImage()),
                                            "png",
                                            transactionId);
                                    if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
                                        hierarchicalLog.addEndHeading1("upload to FMS successfully");
                                        String uuid = (String) response.getData();
                                        field.setImage(uuid);
                                    }

                                } catch (Exception ex) {
                                    System.err.println("Cannot upload image from QR to FMS!. Using default");
                                }
                            }
                            //</editor-fold>
                            //</editor-fold>
                        })
                        .templateParseTopField(
                                FieldTypeName.INITIAL,
                                payload,
                                true,
                                transactionId);

                hierarchicalLog.addChildHierarchicalLog(parseV2.getHierarchicalLog());
                return parseV2.setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
            case "qrcode": {
                //<editor-fold defaultstate="collapsed" desc="Generate QrCodeFieldAttribute from Payload">
                parseV2 = createBufferer(new QRFieldAttribute(),
                        field -> {
                            //<editor-fold defaultstate="collapsed" desc="Initial lambda method">
                            //<editor-fold defaultstate="collapsed" desc="Initial data of field">
                            if (!isUpdate) {
                                if (field.IsTransparent() == null) {
                                    field.setTransparent(false);
                                }
                                //<editor-fold defaultstate="collapsed" desc="Logger">
                                hierarchicalLog.addStartHeading1("Transparent: " + field.IsTransparent());
                                //</editor-fold>
                            }
                            //</editor-fold>

                            //<editor-fold defaultstate="collapsed" desc="Upload image QR into FMS if need">
                            if (field.getImageQR() != null
                            && field.getImageQR().length()
                            > PolicyConfiguration.getInstant()
                                    .getSystemConfig()
                                    .getAttributes()
                                    .get(0)
                                    .getMaximumFile()) {
                                try {
                                    hierarchicalLog.addStartHeading1("Start upload to FMS");
                                    InternalResponse response = vn.mobileid.id.FMS.uploadToFMS(
                                            Base64.decode(field.getImageQR()),
                                            "png",
                                            transactionId);
                                    if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
                                        hierarchicalLog.addEndHeading1("Upload to FMS successfully");
                                        String uuid = (String) response.getData();
                                        field.setImageQR(uuid);
                                    }
                                } catch (Exception ex) {
                                    System.err.println("Cannot upload image from QR to FMS!. Using default");
                                }
                            }
                            //</editor-fold>
                            //</editor-fold>
                        },
                        (field) -> {
                            //<editor-fold defaultstate="collapsed" desc="Checker lambda method">
                            if (Utils.isNullOrEmpty(field.getValue()) && !isUpdate) {
                                hierarchicalLog.addEndHeading1("Missing encode string of QR");
                                return new InternalResponse(
                                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                        A_FPSConstant.CODE_FIELD_QR,
                                        A_FPSConstant.SUBCODE_MISSING_ENCODE_STRING_OF_QR
                                ).setHierarchicalLog(hierarchicalLog);
                            }
                            return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, "");
                            //</editor-fold>
                        }
                )
                        .templateParseTopField(
                                FieldTypeName.QR,
                                payload,
                                true,
                                transactionId);

                hierarchicalLog.addChildHierarchicalLog(parseV2.getHierarchicalLog());
                return parseV2.setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
            case "qrcode-qrypto": {
                //<editor-fold defaultstate="collapsed" desc="Generate QrCodeFieldAttribute from Payload">
                parseV2 = createBufferer(new QryptoFieldAttribute(),
                        null,
                        (field) -> {
                            //<editor-fold defaultstate="collapsed" desc="Initial lambda method">
                            if (!Utils.isNullOrEmpty(field.getItems())) {
                                hierarchicalLog.addStartHeading1("Start checking items in Qrypto");
                                try {
                                    for (ItemDetails detail : field.getItems()) {
                                        String file = null;
                                        Item_IDPicture4Label.IDPicture4Label tempp = null;
                                        switch (ItemsType.getItemsType(detail.getType())) {
                                            case Binary:
                                            case File: {
                                                file = (String) detail.getValue();
                                                break;
                                            }
                                            case ID_Picture_with_4_labels: {
                                                String temp_ = new ObjectMapper().writeValueAsString(detail.getValue());
                                                tempp = new ObjectMapper().readValue(temp_, Item_IDPicture4Label.IDPicture4Label.class);
                                                file = tempp.getBase64();
                                                break;
                                            }
                                            default: {
                                            }
                                        }
                                        if (file != null) {
                                            //<editor-fold defaultstate="collapsed" desc="Upload image into FMS If need">
                                            if (file.length()
                                            > PolicyConfiguration.getInstant()
                                                    .getSystemConfig()
                                                    .getAttributes()
                                                    .get(0)
                                                    .getMaximumFile()) {
                                                try {
                                                    hierarchicalLog.addStartHeading2("Upload image/file into FMS");
                                                    InternalResponse response = vn.mobileid.id.FMS.uploadToFMS(
                                                            Base64.decode(file),
                                                            "png",
                                                            transactionId);
                                                    if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
                                                        hierarchicalLog.addEndHeading2("Upload image/file into FMS successfully");
                                                        String uuid = (String) response.getData();
                                                        if (tempp != null) {
                                                            tempp.setBase64(uuid);
                                                            detail.setValue(tempp);
                                                        } else {
                                                            detail.setValue(uuid);
                                                        }
                                                    } else {
                                                        hierarchicalLog.addEndHeading2("Upload image/file into FMS fail");
                                                    }
                                                } catch (Exception ex) {
                                                    System.err.println("Cannot upload image from QR to FMS!. Using default");
                                                }
                                            }
                                            //</editor-fold>
                                        }
                                    }
                                } catch (Exception ex) {
                                    hierarchicalLog.addEndHeading1("Upload the image/file from QR into FMS fail");
                                    LogHandler.error(ConnectorField.class, transactionId, ex);
                                    return new InternalResponse(
                                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                            A_FPSConstant.CODE_FIELD_QR_Qrypto,
                                            A_FPSConstant.SUBCODE_INVALID_TYPE_OF_ITEM
                                    ).setException(ex);
                                }
                                hierarchicalLog.addEndHeading1("Checking items in Qrypto successfully");
                            }
                            return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, "");
                            //</editor-fold>
                        })
                        .templateParseTopField(
                                FieldTypeName.QRYPTO,
                                payload,
                                true,
                                transactionId);

                hierarchicalLog.addChildHierarchicalLog(parseV2.getHierarchicalLog());
                return parseV2.setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
            case "stamp": {
                //<editor-fold defaultstate="collapsed" desc="Generate StampFieldAttribute from Payload">
                parseV2 = createBufferer(new FileFieldAttribute(),
                        (field) -> {
                            //<editor-fold defaultstate="collapsed" desc="Initial lambda method">
                            if (!isUpdate) {
                                if (field.isApplyToAll() == null) {
                                    field.setApplyToAll(false);
                                }
                                if (field.isReplicateAllPages() == null) {
                                    field.setReplicateAllPages(false);
                                }

                                //<editor-fold defaultstate="collapsed" desc="Logger">
                                hierarchicalLog.addStartHeading1("Apply to all: " + field.isApplyToAll());
                                hierarchicalLog.addStartHeading1("Replicate all pages: " + field.isReplicateAllPages());
                                //</editor-fold>
                            }
                            //</editor-fold>
                        },
                        (field) -> {
                            //<editor-fold defaultstate="collapsed" desc="Checker lambda method">
                            if (!Utils.isNullOrEmpty(field.getFile())) {
                                //<editor-fold defaultstate="collapsed" desc="Upload into FMS if need">
                                if (field.getFile() != null && field.getFile().length()
                                > PolicyConfiguration.getInstant()
                                        .getSystemConfig()
                                        .getAttributes()
                                        .get(0)
                                        .getMaximumFile()) {
                                    try {
                                        hierarchicalLog.addStartHeading2("Start upload image/file into FMS");
                                        InternalResponse response = vn.mobileid.id.FMS.uploadToFMS(
                                                Base64.decode(field.getFile()),
                                                "png",
                                                transactionId);
                                        if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
                                            hierarchicalLog.addEndHeading2("Upload successfully");
                                            String uuid = (String) response.getData();
                                            field.setFile(uuid);
                                        } else {
                                            hierarchicalLog.addEndHeading2("Upload fail");
                                        }
                                    } catch (Exception ex) {
                                        hierarchicalLog.addEndHeading2("Cannot upload image from ImageField to FMS!. Using default");
                                    }
                                }
                                //</editor-fold>
                            }
                            return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, "");
                            //</editor-fold>
                        })
                        .templateParseTopField(
                                FieldTypeName.STAMP,
                                payload,
                                true,
                                transactionId);

                hierarchicalLog.addChildHierarchicalLog(parseV2.getHierarchicalLog());
                return parseV2.setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
            case "camera": {
                //<editor-fold defaultstate="collapsed" desc="Generate CameraFieldAttribute from Payload">
                parseV2 = createBufferer(new CameraFieldAttribute(),
                        (field) -> {
                            //<editor-fold defaultstate="collapsed" desc="Initial lambda method">
                            if (!isUpdate) {
                                if (field.isShowIcon() == null) {
                                    field.setShowIcon(false);
                                }
                                if (field.isApplyToAll() == null) {
                                    field.setApplyToAll(false);
                                }
                                if (field.isReplicateAllPages() == null) {
                                    field.setReplicateAllPages(false);
                                }

                                //<editor-fold defaultstate="collapsed" desc="Logger">
                                hierarchicalLog.addStartHeading1("Is show icon: " + field.isShowIcon());
                                hierarchicalLog.addStartHeading1("Apply to all: " + field.isApplyToAll());
                                hierarchicalLog.addStartHeading1("Replicate all pages: " + field.isReplicateAllPages());
                                //</editor-fold>
                            }
                            //</editor-fold>
                        },
                        (field) -> {
                            //<editor-fold defaultstate="collapsed" desc="Checker lambda method">
                            if (!Utils.isNullOrEmpty(field.getFile())) {
                                //<editor-fold defaultstate="collapsed" desc="Upload into FMS if need">
                                if (field.getFile() != null && field.getFile().length()
                                > PolicyConfiguration.getInstant()
                                        .getSystemConfig()
                                        .getAttributes()
                                        .get(0)
                                        .getMaximumFile() / 2) {
                                    try {
                                        hierarchicalLog.addStartHeading2("Upload image into FMS");
                                        InternalResponse response = vn.mobileid.id.FMS.uploadToFMS(
                                                Base64.decode(field.getFile()),
                                                "png",
                                                transactionId);
                                        if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
                                            hierarchicalLog.addEndHeading2("Upload successfully");
                                            String uuid = (String) response.getData();
                                            field.setFile(uuid);
                                        } else {
                                            hierarchicalLog.addEndHeading2("Upload fail");
                                        }
                                    } catch (Exception ex) {
                                        hierarchicalLog.addEndHeading2("Cannot upload image from ImageField to FMS!. Using default");
                                    }
                                }
                                //</editor-fold>
                            }
                            return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, "");
                            //</editor-fold>
                        })
                        .templateParseTopField(
                                FieldTypeName.CAMERA,
                                payload,
                                true,
                                transactionId);

                hierarchicalLog.addChildHierarchicalLog(parseV2.getHierarchicalLog());
                return parseV2.setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
            case "attachment": {
                //<editor-fold defaultstate="collapsed" desc="Generate AttachmentFieldAttribute from Payload">
                parseV2 = createBufferer(new AttachmentFieldAttribute(),
                        (field) -> {
                            //<editor-fold defaultstate="collapsed" desc="Initial lambda method">
                            if (!isUpdate) {
                                if (field.isShowIcon() == null) {
                                    field.setIsShowIcon(false);
                                }
                                if (field.isApplyToAll() == null) {
                                    field.setApplyToAll(false);
                                }
                                if (field.isReplicateAllPages() == null) {
                                    field.setReplicateAllPages(false);
                                }
                                //<editor-fold defaultstate="collapsed" desc="Logger">
                                hierarchicalLog.addStartHeading1("Is show icon: " + field.isShowIcon());
                                hierarchicalLog.addStartHeading1("Apply to all: " + field.isApplyToAll());
                                hierarchicalLog.addStartHeading1("Replicate all pages: " + field.isReplicateAllPages());
                                //</editor-fold>
                            }
                            //</editor-fold>
                        },
                        (field) -> {
                            //<editor-fold defaultstate="collapsed" desc="Checker lambda method">
                            if (field.getFileData() != null) {
                                hierarchicalLog.addStartHeading1("Start checking file data");

                                //<editor-fold defaultstate="collapsed" desc="Check data of File">
                                hierarchicalLog.addStartHeading2("Checking file extension + file name");
                                if (Utils.isNullOrEmpty(field.getFileExtension()) && Utils.isNullOrEmpty(field.getFileName())) {
                                    hierarchicalLog.addEndHeading2("Checking fail");
                                    return new InternalResponse(
                                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                            A_FPSConstant.CODE_FIELD_ATTACHMENT,
                                            A_FPSConstant.SUBCODE_MISSING_EXTENSION
                                    ).setHierarchicalLog(hierarchicalLog);
                                }
                                hierarchicalLog.addStartHeading2("Checking file extension + file name successfully");

                                hierarchicalLog.addStartHeading2("Checking file data");
                                if (Utils.isNullOrEmpty(field.getFile())) {
                                    hierarchicalLog.addEndHeading2("Checking file data fail");
                                    return new InternalResponse(
                                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                            A_FPSConstant.CODE_FIELD_ATTACHMENT,
                                            A_FPSConstant.SUBCODE_MISSING_FILE_DATA_OF_ATTACHMENT
                                    ).setHierarchicalLog(hierarchicalLog);
                                }
                                hierarchicalLog.addStartHeading2("Checking file data successfully");

                                hierarchicalLog.addStartHeading2("Checking file extension");
                                if (Utils.isNullOrEmpty(field.getFileExtension())) {
                                    try {
                                        String fileName = field.getFileName();
                                        String[] splits = fileName.split("\\.");
                                        field.setFileExtension(splits[splits.length - 1]);
                                    } catch (Exception e) {
                                        hierarchicalLog.addEndHeading2("Checking file extension fail");
                                        return new InternalResponse(
                                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                                A_FPSConstant.CODE_FIELD_ATTACHMENT,
                                                A_FPSConstant.SUBCODE_MISSING_EXTENSION
                                        ).setHierarchicalLog(hierarchicalLog);
                                    }
                                }
                                hierarchicalLog.addStartHeading2("Checking file extension successfully");
                                //</editor-fold>

                                //<editor-fold defaultstate="collapsed" desc="Upload into FMS if need">
                                if (field.getFile() != null && field.getFile().length()
                                > PolicyConfiguration.getInstant()
                                        .getSystemConfig()
                                        .getAttributes()
                                        .get(0)
                                        .getMaximumFile()) {
                                    try {
                                        hierarchicalLog.addStartHeading2("Upload file into FMS");
                                        InternalResponse response = vn.mobileid.id.FMS.uploadToFMS(
                                                Base64.decode(field.getFile()),
                                                field.getFileExtension(),
                                                transactionId);
                                        if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
                                            hierarchicalLog.addStartHeading2("Upload successfully");
                                            String uuid = (String) response.getData();
                                            field.setFile(uuid);
                                        } else {
                                            hierarchicalLog.addStartHeading2("Upload fail");
                                        }
                                    } catch (Exception ex) {
                                        hierarchicalLog.addStartHeading2("Cannot upload image from ImageField to FMS!. Using default");
                                    }
                                }
                                //</editor-fold>

                                hierarchicalLog.addEndHeading1("Checking file data successfully");
                            }
                            return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, "");
                            //</editor-fold>
                        })
                        .templateParseTopField(
                                FieldTypeName.ATTACHMENT,
                                payload,
                                true,
                                transactionId);

                hierarchicalLog.addChildHierarchicalLog(parseV2.getHierarchicalLog());
                return parseV2.setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
            case "hyperlink": {
                //<editor-fold defaultstate="collapsed" desc="Generate HyperLinkFieldAttribute from Payload">
                parseV2 = createBufferer(new HyperLinkFieldAttribute(),
                        (field) -> {
                            //<editor-fold defaultstate="collapsed" desc="Initial lambda method">
                            if (!isUpdate) {
                                if (field.getAlignment() == null) {
                                    field.setDefaultAlignment();
                                }
                                if (field.getColor() == null) {
                                    field.setColor("BLACK");
                                }
                                if (field.isReadOnly() == null) {
                                    field.setReadOnly(false);
                                }
                                if (field.isMultiline() == null) {
                                    field.setMultiline(false);
                                }
                                //<editor-fold defaultstate="collapsed" desc="Logger">
                                hierarchicalLog.addStartHeading1("Alignment: " + field.getAlignment());
                                hierarchicalLog.addStartHeading1("Text Color: " + field.getColor());
                                hierarchicalLog.addStartHeading1("Read Only: " + field.isReadOnly());
                                hierarchicalLog.addStartHeading1("Multiline: " + field.isMultiline());
                                //</editor-fold>
                            }
                            //</editor-fold>
                        })
                        .templateParseTopField(
                                FieldTypeName.HYPERLINK,
                                payload,
                                true,
                                transactionId);

                hierarchicalLog.addChildHierarchicalLog(parseV2.getHierarchicalLog());
                return parseV2.setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
            case "combo": {
                //<editor-fold defaultstate="collapsed" desc="Generate ComboFieldAttribute from Payload">
                parseV2 = createBufferer(new ComboBoxFieldAttribute(),
                        (field) -> {
                            //<editor-fold defaultstate="collapsed" desc="Initial lambda method">
                            if (!isUpdate) {
                                if (field.getAlignment() == null) {
                                    field.setDefaultAlignment();
                                }
                                if (field.getColor() == null) {
                                    field.setColor("BLACK");
                                }
                                if (field.isReadOnly() == null) {
                                    field.setReadOnly(false);
                                }
                                if (field.isMultiline() == null) {
                                    field.setMultiline(false);
                                }
                                //<editor-fold defaultstate="collapsed" desc="Logger">
                                hierarchicalLog.addStartHeading1("Alignment: " + field.getAlignment());
                                hierarchicalLog.addStartHeading1("Text Color: " + field.getColor());
                                hierarchicalLog.addStartHeading1("Read Only: " + field.isReadOnly());
                                hierarchicalLog.addStartHeading1("Multiline: " + field.isMultiline());
                                //</editor-fold>
                            }
                            //</editor-fold>
                        })
                        .templateParseTopField(
                                FieldTypeName.COMBOBOX,
                                payload,
                                true,
                                transactionId);

                hierarchicalLog.addChildHierarchicalLog(parseV2.getHierarchicalLog());
                return parseV2.setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
            case "toggle": {
                //<editor-fold defaultstate="collapsed" desc="Generate ComboFieldAttribute from Payload">
                parseV2 = createBufferer(new ToggleFieldAttribute(),
                        (field) -> {
                            //<editor-fold defaultstate="collapsed" desc="Initial lambda method">
                            if (!isUpdate) {
                                if (field.getAlignment() == null) {
                                    field.setDefaultAlignment();
                                }
                                if (field.getColor() == null) {
                                    field.setColor("BLACK");
                                }
                                if (field.isReadOnly() == null) {
                                    field.setReadOnly(false);
                                }
                                if (field.isMultiline() == null) {
                                    field.setMultiline(false);
                                }
                                //<editor-fold defaultstate="collapsed" desc="Logger">
                                hierarchicalLog.addStartHeading1("Alignment: " + field.getAlignment());
                                hierarchicalLog.addStartHeading1("Text Color: " + field.getColor());
                                hierarchicalLog.addStartHeading1("Read Only: " + field.isReadOnly());
                                hierarchicalLog.addStartHeading1("Multiline: " + field.isMultiline());
                                //</editor-fold>
                            }
                            //</editor-fold>
                        })
                        .templateParseTopField(
                                FieldTypeName.TOGGLE,
                                payload,
                                true,
                                transactionId);

                hierarchicalLog.addChildHierarchicalLog(parseV2.getHierarchicalLog());
                return parseV2.setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
            case "numeric_stepper": {
                //<editor-fold defaultstate="collapsed" desc="Generate ComboFieldAttribute from Payload">
                parseV2 = createBufferer(new NumericStepperAttribute(),
                        (field) -> {
                            //<editor-fold defaultstate="collapsed" desc="Initial lambda method">
                            if (!isUpdate) {
                                if (field.getAlignment() == null) {
                                    field.setDefaultAlignment();
                                }
                                if (field.getColor() == null) {
                                    field.setColor("BLACK");
                                }
                                if (field.isReadOnly() == null) {
                                    field.setReadOnly(false);
                                }
                                if (field.isMultiline() == null) {
                                    field.setMultiline(false);
                                }
                                //<editor-fold defaultstate="collapsed" desc="Logger">
                                hierarchicalLog.addStartHeading1("Alignment: " + field.getAlignment());
                                hierarchicalLog.addStartHeading1("Text Color: " + field.getColor());
                                hierarchicalLog.addStartHeading1("Read Only: " + field.isReadOnly());
                                hierarchicalLog.addStartHeading1("Multiline: " + field.isMultiline());
                                //</editor-fold>
                            }
                            //</editor-fold>
                        })
                        .templateParseTopField(
                                FieldTypeName.TOGGLE,
                                payload,
                                true,
                                transactionId);

                hierarchicalLog.addChildHierarchicalLog(parseV2.getHierarchicalLog());
                return parseV2.setHierarchicalLog(hierarchicalLog);
                //</editor-fold>
            }
        }
        return new InternalResponse(A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                new ResponseMessageController().writeStringField("error", "This type of Field not provide yet"))
                .setHierarchicalLog(hierarchicalLog);
    }
    //</editor-fold>

    //=====================INTERNAL METHOD======================================
    //<editor-fold defaultstate="collapsed" desc="Create Bufferer first (T type and Consumer initial and Function<> checker)">
    private static <T extends BasicFieldAttribute> Bufferer createBufferer(
            T data,
            Consumer<T> initial,
            Function<T, InternalResponse> checker) {
        return new Bufferer(data, initial, checker);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Create Bufferer first (T type and Consumer initial)">
    private static <T extends BasicFieldAttribute> Bufferer createBufferer(
            T data,
            Consumer<T> checker) {
        return new Bufferer(data, checker);
    }
    //</editor-fold>

    private static class Bufferer<T extends BasicFieldAttribute> {

        private T fieldType;
        private Consumer<T> initial;
        private Function<T, InternalResponse> checker;

        public Bufferer(T fieldType, Consumer<T> initial) {
            this.fieldType = fieldType;
            this.initial = initial;
        }

        public Bufferer(T fieldType, Consumer<T> initial, Function<T, InternalResponse> checker) {
            this.fieldType = fieldType;
            this.initial = initial;
            this.checker = checker;
        }

        //<editor-fold defaultstate="collapsed" desc="Template of Parse to Ffield">
        /**
         * Craete Template for parse to Field
         *
         * @return
         */
        private InternalResponse templateParseTopField(
                FieldTypeName parentType,
                String payload,
                boolean isCheckBasicField,
                String transactionId
        ) {
            fps_core.utils.LogHandler.HierarchicalLog hierarchicalLog = new fps_core.utils.LogHandler.HierarchicalLog("Parse to Field");

            //<editor-fold defaultstate="collapsed" desc="Generate Field Attribute from Payload">
            hierarchicalLog.addStartHeading1("Start parse into " + parentType);

            //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
            try {
                fieldType = new ObjectMapper().readValue(payload, (Class<T>) fieldType.getClass());
            } catch (Exception ex) {
                hierarchicalLog.addEndHeading1("Parse into field fail");
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_FAIL,
                        A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
                ).setHierarchicalLog(hierarchicalLog);
            }
            hierarchicalLog.addEndHeading1("Parse into field successfully");
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Check basic field">
            hierarchicalLog.addStartHeading1("Start check basic");
            if (isCheckBasicField) {
                InternalResponse response = CheckPayloadRequest.checkBasicField(fieldType, transactionId);
                hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
                if (!response.isValid()) {
                    hierarchicalLog.addEndHeading1("Checked fail");
                    return response.setHierarchicalLog(hierarchicalLog);
                }
            }
            hierarchicalLog.addEndHeading1("Checked successfully");
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Check field type">
            if (!Utils.isNullOrEmpty(fieldType.getTypeName())) {
                boolean check = CheckPayloadRequest.checkField(fieldType, parentType);

                if (!check) {
                    hierarchicalLog.addEndHeading1("Check field type fail");
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FIELD,
                            A_FPSConstant.SUBCODE_INVALID_FIELD_TYPE
                    ).setHierarchicalLog(hierarchicalLog);
                }
                fieldType.setType(Resources.getFieldTypes().get(fieldType.getTypeName()));
            } else {
                fieldType.setType(Resources.getFieldTypes().get(parentType.getParentName()));
            }
            hierarchicalLog.addStartHeading1("Final field type: " + fieldType.getType().getTypeName());
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Initial data of field">
            if (initial != null) {
                initial.accept(fieldType);
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Checker some case">
            if (this.checker != null) {
                InternalResponse temp = this.checker.apply(fieldType);
                if (!temp.isValid()) {
                    return temp;
                }
            }
            //</editor-fold>

            return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, fieldType).setHierarchicalLog(hierarchicalLog);
            //</editor-fold>
        }
        //</editor-fold>
    }
}
