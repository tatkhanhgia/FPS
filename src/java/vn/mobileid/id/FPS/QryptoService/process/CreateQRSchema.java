/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.QryptoService.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import fps_core.objects.QRFieldAttribute;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.PolicyConfiguration;
//import vn.mobileid.id.general.keycloak.obj.User;
//import vn.mobileid.id.general.objects.InternalResponse;
//import vn.mobileid.id.paperless.PaperlessConstant;
//import vn.mobileid.id.paperless.exception.InvalidFormatOfItems;
//import vn.mobileid.id.paperless.kernel.GetQRSize;
//import vn.mobileid.id.paperless.kernel.process.ProcessSecureQRTemplate;
//import vn.mobileid.id.paperless.object.enumration.ItemsType;
//import vn.mobileid.id.paperless.object.enumration.WorkflowAttributeTypeName;
//import vn.mobileid.id.paperless.objects.FileDataDetails;
//import vn.mobileid.id.paperless.objects.ItemDetails;
//import vn.mobileid.id.paperless.objects.Item_Choice;
//import vn.mobileid.id.paperless.objects.Item_Choice.Element;
//import vn.mobileid.id.paperless.objects.Item_IDPicture4Label.IDPicture4Label;
//import vn.mobileid.id.paperless.objects.Item_Table;
//import vn.mobileid.id.paperless.objects.Item_URL;
//import vn.mobileid.id.paperless.objects.QRSize;
//import vn.mobileid.id.paperless.objects.WorkflowAttributeType;
import vn.mobileid.id.FPS.QryptoService.object.Configuration;
import vn.mobileid.id.FPS.QryptoService.object.FileDataDetails;
import vn.mobileid.id.FPS.QryptoService.object.ItemDetails;
import vn.mobileid.id.FPS.QryptoService.object.Item_Choice;
import vn.mobileid.id.FPS.QryptoService.object.Item_Choice.Element;
import vn.mobileid.id.FPS.QryptoService.object.Item_IDPicture4Label.IDPicture4Label;
import vn.mobileid.id.FPS.QryptoService.object.Item_Table;
import vn.mobileid.id.FPS.QryptoService.object.Item_URL;
import vn.mobileid.id.FPS.QryptoService.object.QRSchema;
import vn.mobileid.id.FPS.QryptoService.object.qryptoEffectiveDate;
import vn.mobileid.id.FPS.exception.InvalidFormatOfItems;
import vn.mobileid.id.utils.Utils;
import vn.mobileid.id.FPS.QryptoService.process.ItemsType;
import vn.mobileid.id.FPS.object.User;

/**
 *
 * @author GiaTK
 */
public class CreateQRSchema {

    //<editor-fold defaultstate="collapsed" desc="Create QR Schema">
    public static QRSchema createQRSchema(
            List<FileDataDetails> fileData,
            List<ItemDetails> items,
            QRSchema.QR_META_DATA positionQR,
            String transactionID
    ) throws InvalidFormatOfItems {
        QRSchema QR = new QRSchema();
        QRSchema.format format = new QRSchema.format();
        QR.setScheme("QC1");
        format.setVersion("2");

        List<QRSchema.data> listData = new ArrayList<>();
        List<QRSchema.field> listField = new ArrayList<>();
        HashMap<String, byte[]> headers = new HashMap<>();

        for (ItemDetails item : items) {
            if (item.getValue() == null) {
                continue;
            }
            try {
                if (new ObjectMapper().writeValueAsString(item.getValue()).equals("{}")) {
                    continue;
                }
            } catch (Exception ex) {
            }
            QRSchema.data data = new QRSchema.data();
            QRSchema.field field = new QRSchema.field();

            ItemsType checkType = ItemsType.getItemsType(item.getType());
            try {
                switch (checkType) {
                    case Non_Editable: {
                    }
                    case String: { //Type String _ text
                        String random = Utils.generateOneTimePassword(6);
                        data.setName(random);
                        data.setValue((String) item.getValue());
                        field.setName(item.getField());
                        field.setKvalue(random);
                        field.setType(QRSchema.fieldType.t2);
                        listData.add(data);
                        listField.add(field);
                        break;
                    }
                    case URL: {
                        String temp = "{\"value\":" + new ObjectMapper().writeValueAsString(item.getValue()) + "}";
                        Item_URL url = new ObjectMapper().readValue(temp, Item_URL.class);

                        String random = Utils.generateOneTimePassword(6);
                        data.setName(random);
                        String urlValidator = vn.mobileid.id.general.PolicyConfiguration
                                .getInstant()
                                .getSystemConfig()
                                .getAttributes()
                                .get(0)
                                .getQrHost();
                        data.setValue(Utils.isNullOrEmpty(url.getValue().getUrl()) ? urlValidator : url.getValue().getUrl());
                        field.setName(Utils.isNullOrEmpty(url.getValue().getLabel()) ? "" : url.getValue().getLabel());
                        field.setKvalue(random);
                        field.setType(QRSchema.fieldType.url);
                        listData.add(data);
                        listField.add(field);
                        break;
                    }
                    case TextBold: { //Type String _ text
                        String random = Utils.generateOneTimePassword(6);
                        data.setName(random);
                        data.setValue((String) item.getValue());
                        field.setName(item.getField());
                        field.setKvalue(random);
                        field.setType(QRSchema.fieldType.t2);
                        field.setM("b");
                        listData.add(data);
                        listField.add(field);
                        break;
                    }
                    case Boolean: { //Type Boolean
                        String random = Utils.generateOneTimePassword(6);
                        data.setName(random);
                        data.setValue(((Boolean) item.getValue()) == true ? "true" : "false");
                        field.setName(item.getField());
                        field.setKvalue(random);
                        field.setType(QRSchema.fieldType.t2);
                        listData.add(data);
                        listField.add(field);
                        break;
                    }
                    case Integer: { //Type INT
                        String random = Utils.generateOneTimePassword(6);
                        data.setName(random);
                        data.setValue(String.valueOf((Integer) item.getValue()));
                        field.setName(item.getField());
                        field.setKvalue(random);
                        field.setType(QRSchema.fieldType.t2);
                        listData.add(data);
                        listField.add(field);
                        break;
                    }
                    case Date: { //Type DATE
                        String random = Utils.generateOneTimePassword(6);
                        data.setName(random);
                        data.setValue((String) item.getValue());
                        field.setName(item.getField());
                        field.setKvalue(random);
                        field.setType(QRSchema.fieldType.t2);
                        listData.add(data);
                        listField.add(field);
                        break;
                    }
                    case File: {
                        field.setType(QRSchema.fieldType.f1);
                        field.setName(item.getField());
                        field.setFile_type(item.getFile_format());
                        field.setFile_field(item.getFile_field());
                        field.setFile_name(item.getFile_name());
                        field.setShare_mode(3);
                        headers.put(item.getFile_field(), Base64.getDecoder().decode((String) item.getValue()));
                        listField.add(field);
                        break;
                    }
                    case Binary: { //Type Binary           
                        System.out.println("Append binary");
                        field.setType(QRSchema.fieldType.f1);
                        field.setName(item.getField());
                        field.setFile_type(item.getFile_format());
                        field.setFile_field(item.getFile_field());
                        field.setFile_name(item.getFile_name());
                        field.setShare_mode(3);
                        for (FileDataDetails file : fileData) {
                            if (item.getFile_field().equals(file.getFile_field())) {
                                if (file.getValue() instanceof String) {
                                    headers.put(item.getFile_field(), Base64.getDecoder().decode((String) file.getValue()));
                                } else {
                                    headers.put(item.getFile_field(), (byte[]) file.getValue());
                                }
                            }
                        }
                        if (positionQR != null) {
                            field.setQr_meta_data(positionQR);
                        }
                        listField.add(field);
                        break;
                    }
                    case Choice: {
                        String temp = "{\"value\":" + new ObjectMapper().writeValueAsString(item.getValue()) + "}";
                        Item_Choice choices = new ObjectMapper().readValue(temp, Item_Choice.class);

                        for (Element element : choices.getElements()) {
                            if (element.isChoice()) {
                                String random = Utils.generateOneTimePassword(6);
                                data.setName(random);
                                data.setValue(element.getElementName());
                                field.setName(item.getField());
                                field.setKvalue(random);
                                field.setType(QRSchema.fieldType.t2);
                                listData.add(data);
                                listField.add(field);
                            }
                        }
                        break;
                    }
                    case Table: {
                        String temp = "{\"value\":" + new ObjectMapper().writeValueAsString(item.getValue()) + "}";
                        Item_Table itemTable = new ObjectMapper().readValue(temp, Item_Table.class);

                        String random = Utils.generateOneTimePassword(6);
                        data.setName(random);

                        //Create Table                    
                        data.setTable(itemTable.getRows());

                        field.setName(item.getField());
                        field.setKvalue(random);
                        field.setType(QRSchema.fieldType._1l3_2l1);

                        listData.add(data);
                        listField.add(field);
                        break;
                    }
                    case ID_Picture_with_4_labels: {
                        String temp = new ObjectMapper().writeValueAsString(item.getValue());
                        IDPicture4Label idPicture = new ObjectMapper().readValue(temp, IDPicture4Label.class);

                        String random = Utils.generateOneTimePassword(6);
                        //Prepare field
                        field.setType(QRSchema.fieldType._4T1P);
                        field.setName(item.getField());
                        field.setFile_type(item.getFile_format() == null ? "image/png" : item.getFile_format());
                        field.setFile_field(random);
                        field.setFile_name(item.getFile_name() == null ? "none" : item.getFile_name());
                        field.setShare_mode(3);
                        field.setKvalue(random);

                        boolean enable = false;
                        if (!Utils.isNullOrEmpty(fileData)) {
                            for (FileDataDetails file : fileData) {
                                if (item.getFile_field() != null && item.getFile_field().equals(file.getFile_field())) {
                                    if (file.getValue() instanceof String) {
                                        headers.put(random, Base64.getDecoder().decode((String) file.getValue()));
                                        idPicture.setBase64((String) file.getValue());
                                    } else {
                                        headers.put(item.getFile_field(), (byte[]) file.getValue());
                                    }
                                    enable = true;
                                }
                            }
                        }

                        if (!enable) {
                            headers.put(random, Base64.getDecoder().decode(idPicture.getBase64() == null ? "" : idPicture.getBase64()));
                        }

                        data.setName(random);
                        data.setIdPictire4Label(idPicture);
                        listData.add(data);
                        listField.add(field);
                        break;
                    }
                    default: {
                        throw new Exception("Invalid type of items");
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new InvalidFormatOfItems(item.getField());
            }
        }

        format.setFields(listField);
        QR.setData(listData);
        QR.setFormat(format);
        QR.setTitle("GoPaperless Service");
        QR.setCi("");
        QR.setHeader(headers);
        return QR;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Create Config">
    public static Configuration createConfiguration(
            QRFieldAttribute field,
            User user,
            String transactionID) throws Exception {
        Configuration config = new Configuration();
        config.setContextIdentifier("QC1:");
        config.setIsTransparent(field.IsTransparent());

        qryptoEffectiveDate effectiveDate = new qryptoEffectiveDate();
        Calendar now = Calendar.getInstance();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now.getTime());

        now.add(Calendar.DATE, 
                Integer.parseInt(
                        PolicyConfiguration
                                .getInstant()
                                .getSystemConfig()
                                .getAttributes()
                                .get(0)
                                .getQrExpiredTime()));
        
        String timeStamp2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now.getTime());
        effectiveDate.setNotValidBefore(timeStamp);
        effectiveDate.setNotValidAfter(timeStamp2);

        config.setQryptoEffectiveDate(effectiveDate);

        config.setQryptoDimension(Math.round(field.getDimension().getWidth()));

        return config;
    }
    //</editor-fold>
}
