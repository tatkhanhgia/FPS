/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.enterprise;

import fps_core.module.DocumentUtils_itext7;
import fps_core.objects.Dimension;
import fps_core.objects.FileManagement;
import fps_core.objects.Signature;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.List;
import vn.mobileid.id.FMS;
import vn.mobileid.id.FPS.component.document.GetDocument;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.controller.ResponseMessageController;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.User;

/**
 *
 * @author GiaTK Each enterprise need a Response for there API
 */
public class ProcessModuleForEnterprise {

    private String clientId;

    ProcessModuleForEnterprise(String clientId) {
        this.clientId = clientId;
    }

    public static ProcessModuleForEnterprise getInstance(User user) {
        switch (user.getScope()) {
            case "Dokobit_Gateway": {
                return new ProcessModuleForEnterprise("Dokobit_Gateway");
            }
        }
        return new ProcessModuleForEnterprise("Default");
    }

    //<editor-fold defaultstate="collapsed" desc="Process Response of API Sign Document">
    public InternalResponse processResponse(
            String clientId,
            long packageId,
            String transactionId
    ) throws Exception {
        //Dokobit Gateway
        switch (this.clientId) {
            case "Dokobit_Gateway": {
                return process(packageId, transactionId);
            }
            default: {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_SUCCESS,
                        ""
                );
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Parse percentage to point unit in API Add Field">
    public Dimension parse(Document document, Dimension field) {
        if (clientId.equals("Dokobit_Gateway")) {
            Dimension result = new Dimension();
            result.setX(field.getX() * document.getDocumentWidth() / 100);
            result.setHeight(field.getHeight() * document.getDocumentHeight() / 100);
            result.setWidth(field.getWidth() * document.getDocumentWidth() / 100);
            result.setY((1 - field.getY()/100) * document.getDocumentHeight() - result.getHeight());
            return result;
        } else {
            Dimension dimension = new Dimension();
            dimension.setX((field.getX() * document.getDocumentWidth()) / 100);
            dimension.setY((field.getY() * document.getDocumentHeight()) / 100);
            dimension.setWidth((field.getWidth() * document.getDocumentWidth()) / 100);
            dimension.setHeight((field.getHeight() * document.getDocumentHeight()) / 100);
            return dimension;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Reverse Parse point unit to percentage in API Get Fields">
    public Dimension reverseParse(Document document, Dimension field) {
        if (clientId.equals("Dokobit_Gateway")) {
            Dimension result = new Dimension();
            result.setX(field.getX() / document.getDocumentWidth() * 100);
            result.setHeight(field.getHeight() / document.getDocumentHeight() * 100);
            result.setWidth(field.getWidth() / document.getDocumentWidth() * 100);
            result.setY((1 - (field.getY() + field.getHeight()) / document.getDocumentHeight()) * 100);
            return result;
        } else {            
            return field;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Create another Status Code in API "Fill Form Field"">
    public int getStatusCodeFillFormField(List<InternalResponse.InternalData> listOfErrorField){
        if(!clientId.equalsIgnoreCase("Dokobit_Gateway")){
            return A_FPSConstant.HTTP_CODE_BAD_REQUEST;
        }
        for(InternalResponse.InternalData internalData : listOfErrorField){
            String value = (String)internalData.getValue();
            if(!value.equalsIgnoreCase(String.valueOf(A_FPSConstant.CODE_FIELD) +
                        String.valueOf(A_FPSConstant.SUBCODE_FIELD_ALREADY_PROCESS))){
                return A_FPSConstant.HTTP_CODE_BAD_REQUEST;
            }
        }
        return A_FPSConstant.HTTP_CODE_SUCCESS;
    }
    //</editor-fold>
    //===========================RP=============================================
    //<editor-fold defaultstate="collapsed" desc="Dokobit Gateway">
    private InternalResponse process(
            long packageId,
            String transactionId
    ) throws Exception {
        //Get Documents in DB
        InternalResponse response = GetDocument.getDocuments(
                packageId,
                transactionId);

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        List<Document> documents = (List<Document>) response.getData();

        //Get the last Document
        Document document_ = null;
        for (Document document : documents) {
            if (document.getRevision() == documents.size()) {
                document_ = document;
            }
        }
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        //Download document from FMS
        response = FMS.downloadDocumentFromFMS(document_.getUuid(),
                transactionId);

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        //Analysis
        byte[] data = (byte[]) response.getData();
        FileManagement file = DocumentUtils_itext7.analysisPDF_i7(data);
        List<Signature> signatures = DocumentUtils_itext7.verifyDocument_i7(data);
        boolean hardcode = false;
        if (signatures.isEmpty()) {
            hardcode = true;
        }

        ResponseMessageController temp = new ResponseMessageController();
        temp.writeStringField("uuid", document_.getUuid());
        temp.writeStringField("file_data",Base64.getEncoder().encodeToString(data));
        temp.writeNumberField("file_size", file.getSize());
        temp.writeStringField("file_name", document_.getName());
        temp.writeStringField("digest", file.getDigest());
        temp.writeStringField("signature_name", signatures.get(signatures.size()-1).getSignatureId());
        temp.writeStringField("signature_algorithm", hardcode
                ? "RSA"
                : signatures.get(signatures.size()-1).getSignatureAlgorithm() == null
                ? "RSA"
                : signatures.get(signatures.size()-1).getSignatureAlgorithm());
        temp.writeStringField("signed_hash",
                hardcode
                        ? "SHA256"
                        : signatures.get(signatures.size()-1).getSignedHash() == null
                        ? "SHA256"
                        : signatures.get(signatures.size()-1).getSignedHash());
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        temp.writeStringField("signed_time", format.format(signatures.get(signatures.size()-1).getSigningTime()));
        

        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                temp.build()
        );
    }
    //</editor-fold>
}
