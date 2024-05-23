/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.QryptoService.process;

import vn.mobileid.id.FPS.QryptoService.object.ItemsType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import vn.mobileid.id.general.PolicyConfiguration;
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
import vn.mobileid.id.FPS.object.fieldAttribute.QryptoFieldAttribute;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.general.LogHandler;

/**
 *
 * @author GiaTK
 * Using for create QR Scheme to call Qrypto
 */
public class CreateQRSchema {

    //<editor-fold defaultstate="collapsed" desc="Create QR Schema">
    /**
     * Create QRSchema to call Qrypto
     * @param fileData List of FileDataDetails
     * @param items List of ItemDetails
     * @param positionQR Position of QR aka QR_META_DATA
     * @param transactionID
     * @return
     * @throws InvalidFormatOfItems 
     */
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
            //<editor-fold defaultstate="collapsed" desc="Remove all case the item.getValue() is not satisfied">
            if (item.getValue() == null) {
                continue;
            }
            try {
                if (MyServices.getJsonService().writeValueAsString(item.getValue()).equals("{}")) {
                    continue;
                }
            } catch (Exception ex) {
            }
            //</editor-fold>
            
            QRSchema.data data = new QRSchema.data();
            QRSchema.field field = new QRSchema.field();

            ItemsType checkType = ItemsType.getItemsType(item.getType());
            try {
                switch (checkType) {
                    case Non_Editable: {
                    }
                    case String: { 
                        //<editor-fold defaultstate="collapsed" desc="Processing">
                        String random = Utils.generateRandomString(6);
                        data.setName(random);
                        data.setValue((String) item.getValue());
                        field.setName(item.getField());
                        field.setKvalue(random);
                        field.setType(QRSchema.fieldType.t2);
                        listData.add(data);
                        listField.add(field);
                        break;
                        //</editor-fold>
                    }
                    case URL: {
                        //<editor-fold defaultstate="collapsed" desc="Processing">
                        String temp = "{\"value\":" + MyServices.getJsonService().writeValueAsString(item.getValue()) + "}";
                        Item_URL url = MyServices.getJsonService().readValue(temp, Item_URL.class);

                        String random = Utils.generateRandomString(6);
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
                        //</editor-fold>
                    }
                    case TextBold: {
                        //<editor-fold defaultstate="collapsed" desc="Processing">
                        String random = Utils.generateRandomString(6);
                        data.setName(random);
                        data.setValue((String) item.getValue());
                        field.setName(item.getField());
                        field.setKvalue(random);
                        field.setType(QRSchema.fieldType.t2);
                        field.setM("b");
                        listData.add(data);
                        listField.add(field);
                        break;
                        //</editor-fold>
                    }
                    case Boolean: {
                        //<editor-fold defaultstate="collapsed" desc="Processing">
                        String random = Utils.generateRandomString(6);
                        data.setName(random);
                        data.setValue(((Boolean) item.getValue()) == true ? "true" : "false");
                        field.setName(item.getField());
                        field.setKvalue(random);
                        field.setType(QRSchema.fieldType.t2);
                        listData.add(data);
                        listField.add(field);
                        break;
                        //</editor-fold>
                    }
                    case Integer: {
                        //<editor-fold defaultstate="collapsed" desc="Processing">
                        String random = Utils.generateRandomString(6);
                        data.setName(random);
                        data.setValue(String.valueOf(item.getValue()));
                        field.setName(item.getField());
                        field.setKvalue(random);
                        field.setType(QRSchema.fieldType.t2);
                        listData.add(data);
                        listField.add(field);
                        break;
                        //</editor-fold>
                    }
                    case Date: {
                        //<editor-fold defaultstate="collapsed" desc="Processing">
                        String random = Utils.generateRandomString(6);
                        data.setName(random);
                        data.setValue((String) item.getValue());
                        field.setName(item.getField());
                        field.setKvalue(random);
                        field.setType(QRSchema.fieldType.t2);
                        listData.add(data);
                        listField.add(field);
                        break;
                        //</editor-fold>
                    }
                    case File: {
                        //<editor-fold defaultstate="collapsed" desc="Processing">
                        field.setType(QRSchema.fieldType.f1);
                        field.setName(item.getField());
                        field.setFile_type(item.getFile_format());
                        field.setFile_field(Utils.isNullOrEmpty(item.getFile_field())?Utils.generateRandomString(5):item.getFile_field());
                        field.setFile_name(item.getFile_name());
                        field.setShare_mode(3);
                        headers.put(field.getFile_field(), Base64.getDecoder().decode((String) item.getValue()));
                        listField.add(field);
                        break;
                        //</editor-fold>
                    }
                    case Binary: {    
                        //<editor-fold defaultstate="collapsed" desc="Processing">
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
                        //</editor-fold>
                    }
                    case Choice: {
                        //<editor-fold defaultstate="collapsed" desc="Processing">
                        String temp = "{\"value\":" + MyServices.getJsonService().writeValueAsString(item.getValue()) + "}";
                        Item_Choice choices = MyServices.getJsonService().readValue(temp, Item_Choice.class);

                        for (Element element : choices.getElements()) {
                            if (element.isChoice()) {
                                String random = Utils.generateRandomString(6);
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
                        //</editor-fold>
                    }
                    case Table: {
                        //<editor-fold defaultstate="collapsed" desc="Processing">
                        String temp = "{\"value\":" + MyServices.getJsonService().writeValueAsString(item.getValue()) + "}";
                        Item_Table itemTable = MyServices.getJsonService().readValue(temp, Item_Table.class);

                        String random = Utils.generateRandomString(6);
                        data.setName(random);

                        //Create Table                    
                        data.setTable(itemTable.getRows());

                        field.setName(item.getField());
                        field.setKvalue(random);
                        field.setType(QRSchema.fieldType._1l3_2l1);

                        listData.add(data);
                        listField.add(field);
                        break;
                        //</editor-fold>
                    }
                    case ID_Picture_with_4_labels: {
                        //<editor-fold defaultstate="collapsed" desc="Processing">
                        String temp = MyServices.getJsonService().writeValueAsString(item.getValue());
                        IDPicture4Label idPicture = MyServices.getJsonService().readValue(temp, IDPicture4Label.class);

                        String random = Utils.generateRandomString(6);
                        
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
                        //</editor-fold>
                    }
                    default: {
                        throw new Exception("Invalid type of items");
                    }
                }
            } catch (Exception ex) {
                LogHandler.error(
                        CreateQRSchema.class,
                        transactionID,
                        ex);
                throw new InvalidFormatOfItems(item.getField());
            }
        }

        format.setFields(listField);
        QR.setData(listData);
        QR.setFormat(format);
        QR.setTitle("FPS Service");
        QR.setCi("");
        QR.setHeader(headers);
        return QR;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Create Config">
    /**
     * Create configuration of Qrypto
     * @param field QryptoFieldAttribute data
     * @param user User data
     * @param pixel the width = the height = int pixel
     * @param transactionID
     * @return
     * @throws Exception 
     */
    public static Configuration createConfiguration(
            QryptoFieldAttribute field,
            User user,
            int pixel,
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

        config.setQryptoDimension(pixel == 0 ? 300 : pixel);
        config.setGetFileTokenList(true);

        return config;
    }
    //</editor-fold>
}
