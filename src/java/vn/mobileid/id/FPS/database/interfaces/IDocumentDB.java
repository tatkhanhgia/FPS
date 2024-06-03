/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.database.interfaces;

import fps_core.enumration.DocumentStatus;
import fps_core.enumration.DocumentType;
import fps_core.objects.CustomPageSize;
import java.util.List;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.helper.ORM_JPA.database.objects.DatabaseResponse;

/**
 *
 * @author GiaTK
 */
public interface  IDocumentDB {
    public DatabaseResponse createDocumentPackage(
            String name,
            int enterpriseId,
            String enterpriseName,
            String hmac,
            String transactionId
    ) throws Exception;

    public DatabaseResponse uploadDocument(
            long documentPackageId,
            long entityId,
            int revision,
            String documentName,
            long documentSize,
            String url,
            DocumentType documentType,
            DocumentStatus status,
            int rotate,
            int pages,
            float documentHeight,
            float documentWidth,
            List<CustomPageSize> documentCustomePage,
            String digest,
            String content,
            String uuid,
            String dmsProperty,
            String hmac,
            String createdBy,
            String transactionId
    ) throws Exception;

    public DatabaseResponse getPackage(
            long packageId,
            String transactionId
    ) throws Exception;

    public DatabaseResponse getDocuments(
            long packageId,
            String transactionId
    ) throws Exception;

    public DatabaseResponse updateStatusOfDocument(
            long documentId,
            User user,
            DocumentStatus status,
            String transactionId
    ) throws Exception;

    public DatabaseResponse getDocumentIdFromUUID(
            String uuid,
            long pENTITY_ID,
            String transactionId
    ) throws Exception;
}
