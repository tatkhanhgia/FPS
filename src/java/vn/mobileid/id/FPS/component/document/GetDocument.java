/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.document;

import com.groupdocs.conversion.filetypes.ImageFileType;
import fps_core.module.DocumentUtils_itext7;
import java.util.HashMap;
import java.util.List;
import vn.mobileid.id.FMS;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.general.database.DatabaseFactory;
import vn.mobileid.id.FPS.groupdoc.Conversion_22_8_1;
import vn.mobileid.id.general.database.DatabaseImpl_document;
import vn.mobileid.id.helper.database.objects.DatabaseResponse;
import vn.mobileid.id.utils.Broadcast;

/**
 *
 * @author GiaTK
 */
public class GetDocument extends Broadcast {

    //<editor-fold defaultstate="collapsed" desc="Get Package">
    /**
     * Trả về thông tin package - Return the information of the package
     *
     * @param packageId
     * @param transactionId
     * @return
     * @throws Exception
     */
    protected static InternalResponse getPackage(
            long packageId,
            String transactionId
    ) throws Exception {
        DatabaseResponse response = DatabaseFactory.getDatabaseImpl_document().getPackage(
                packageId,
                transactionId);

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            response.getStatus()
            );
        }

        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                response.getObject()
        );

    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Get Documents">
    /**
     * Trả về toàn bộ những Documents thuộc về packageId - Return all documents
     * where these documents belong to
     *
     * @param packageId
     * @param transactionId
     * @return
     * @throws Exception
     */
    protected static InternalResponse getDocuments(
            long packageId,
            String transactionId
    ) throws Exception {
        DatabaseResponse response = DatabaseFactory.getDatabaseImpl_document().getDocuments(
                packageId,
                transactionId);
        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            response.getStatus()
            );
        }

        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                response.getObject()
        );
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Get Document Image">
    /**
     * Trả về 1 page trong document - Return one page in document
     *
     * @param packageId
     * @param page
     * @param transactionId
     * @return
     * @throws Exception
     */
    protected static InternalResponse getDocumentImage(
            long packageId,
            int page,
            boolean itextEnabled,
            String imageType,
            String transactionId
    ) throws Exception {
        InternalResponse response = getDocuments(packageId, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        List<Document> documents = (List<Document>) response.getData();
        //Get the last documents and download from fms
        Document last = documents.get(documents.size() - 1);
        response = FMS.downloadDocumentFromFMS(last.getUuid(), transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        byte[] file = (byte[]) response.getData();

        //Extract page from PDF file - itext
        byte[] finalFile = null;
        if (itextEnabled) {
            finalFile = DocumentUtils_itext7.extractPageFromPDF_i7(file, page);
        } else {
            //Extract page from PDF - groupdoc        
//            License license = new License();
//            license.setLicense("C:\\Users\\Admin\\Documents\\NetBeansProjects\\Library_RSSP_SDK\\ProjectRSSP_newest\\FPS\\src\\java\\resources\\GroupDocs.ConversionProductFamily.lic");
//            Converter converter = new Converter(new ByteArrayInputStream(file));
//            ImageConvertOptions options = new ImageConvertOptions();            
//            options.setFormat(ImageFileType.Png);
//            options.setPageNumber(page);
//            options.setPagesCount(1);
//            ByteArrayOutputStream data = new ByteArrayOutputStream();
//            converter.convert(data, options);
//            finalFile = data.toByteArray();
            try {
                finalFile = Conversion_22_8_1.getInstance().convertToImage(
                        file,
                        page,
                        convert(imageType));
            } catch (Exception ex) {

            }
        }
        response.setStatus(A_FPSConstant.HTTP_CODE_SUCCESS);
        response.setData(finalFile);
        HashMap<String, Object> headers = new HashMap<>();
        headers.put("x-image-type", convert(imageType).getFileFormat());
        response.setHeaders(headers);
        return response;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Get Document Package from UUID">
    /**
     * Get Document Package from UUID and Client ID if exist
     * @param uuid
     * @param entityId
     * @param transactionId
     * @return long DocumentPackgeID
     * @throws Exception 
     */
    public static InternalResponse getDocumentPackage(
            String uuid,
            long entityId,
            String transactionId
    )throws Exception{
        DatabaseImpl_document callDB = DatabaseFactory.getDatabaseImpl_document();
        DatabaseResponse response = callDB.getDocumentIdFromUUID(uuid, entityId, transactionId);
        if(response.getStatus() != A_FPSConstant.CODE_SUCCESS){
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL, 
                            response.getStatus()
            );
        }
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                response.getObject()
        );
    }
    //</editor-fold>

    //==========================================================================
    private static ImageFileType convert(String imageType) {
        if (imageType == null) {
            return ImageFileType.Png;
        }
        switch (imageType) {
            case "PNG": {return ImageFileType.Png;}
            case "image/png":{return ImageFileType.Png;}
            case "png": {return ImageFileType.Png;}
            case "jpeg": {return ImageFileType.Jpeg;}
            case "JPEG": {return ImageFileType.Jpeg;}
            case "image/jpeg":{return ImageFileType.Jpeg;}
            default: {
                return ImageFileType.Png;
            }
        }
    }

    public static void main(String[] args) {
        String url = "fps/v1/documents/100/images/1";
        String[] tokens = url.split("/");
        for (String token : tokens) {
            System.out.println(token);
        }
    }
}
