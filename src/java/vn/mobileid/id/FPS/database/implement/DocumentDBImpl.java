/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.database.implement;

import vn.mobileid.id.FPS.services.impls.databaseConnection.DatabaseConnectionManager;
import fps_core.enumration.DocumentStatus;
import fps_core.enumration.DocumentType;
import fps_core.objects.CustomPageSize;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import vn.mobileid.id.FPS.systemManagement.A_FPSConstant;
import vn.mobileid.id.FPS.database.interfaces.IDocumentDB;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.FPS.systemManagement.Configuration;
import vn.mobileid.id.FPS.systemManagement.LogHandler;
import vn.mobileid.id.helper.ORM_JPA.database.CreateConnection;
import vn.mobileid.id.helper.ORM_JPA.database.objects.DatabaseResponse;

/**
 *
 * @author GiaTK
 */
public class DocumentDBImpl implements IDocumentDB {

    private static int retryTimes = 1; // default no retry

    static {
        retryTimes = Configuration.getInstance().getRetry();
        if (retryTimes == 0) {
            retryTimes = 1;
        }
    }

    @Override
    public DatabaseResponse createDocumentPackage(
            String name,
            int enterpriseId,
            String enterpriseName,
            String hmac,
            String transactionId) throws Exception {
        String nameStore = "{ call USP_DOCUMENT_PACKAGE_ADD(?,?,?,?,?,?,?)}";

        HashMap<String, Object> datas = new HashMap<>();
        datas.put("pNAME", name);
        datas.put("pOWNER", enterpriseId);
        datas.put("pSTATUS", 1);
        datas.put("pHMAC", hmac);
        datas.put("pCREATED_BY", enterpriseName);

        HashMap<String, Integer> registerOutParam = new HashMap<>();
        registerOutParam.put("pDOCUMENT_PACKAGE_ID", Types.BIGINT);
        registerOutParam.put("pRESPONSE_CODE", Types.VARCHAR);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                datas,
                registerOutParam,
                "Create Document Package");

        LogHandler.getInstance().debug(this.getClass(), transactionId + " _ " + response.getDebugString());

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return response;
        }

        long documentId = 0;
        List<HashMap<String, Object>> rows = response.getRows();
        for (HashMap<String, Object> row : rows) {
            documentId = (long) row.get("pDOCUMENT_PACKAGE_ID");
        }
        response.setObject(documentId);

        return response;
    }

    @Override
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
            List<CustomPageSize> documentCustomPage,
            String digest,
            String content,
            String uuid,
            String dmsProperty,
            String hmac,
            String createdBy,
            String transactionId) throws Exception {
        String nameStore = "{ call USP_DOCUMENT_ADD(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

        HashMap<String, Object> datas = new HashMap<>();
        datas.put("pDOCUMENT_PACKAGE_ID", documentPackageId);
        datas.put("pENTITY_ID", entityId);
        datas.put("pENABLED", 1);
        datas.put("pDOCUMENT_NAME", documentName);
        datas.put("pDOCUMENT_SIZE", documentSize);
        datas.put("pDOCUMENT_STATUS", status.getTypeId());
        datas.put("pROTATE", rotate);
        datas.put("pURL", url);
        datas.put("pDOCUMENT_TYPE", documentType.getType());
        datas.put("pMIME_TYPE", documentType.getMime());
        datas.put("pDOCUMENT_PAGES", (pages <= 0 ? 0 : pages));
        datas.put("pDOCUMENT_HEIGHT", documentHeight);
        datas.put("pDOCUMENT_WIDTH", documentWidth);
        Document temp = new Document();
        temp.setDocumentCustomPageSize(documentCustomPage);
        datas.put("pDOCUMENT_CUSTOM_PAGESIZE", documentCustomPage != null ? MyServices.getJsonService().writeValueAsString(temp) : null);
        datas.put("pDIGEST", digest);
        datas.put("pCONTENT", content);
        datas.put("pDOCUMENT_UUID", uuid);
        datas.put("pDMS_PROPERTY", dmsProperty);
        datas.put("pREVISION", revision);
        datas.put("pHMAC", hmac);
        datas.put("pCREATED_BY", createdBy);

        HashMap<String, Integer> output = new HashMap<>();
        output.put("pDOCUMENT_ID", java.sql.Types.BIGINT);
        output.put("pRESPONSE_CODE", java.sql.Types.VARCHAR);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                datas,
                output,
                "Upload Document to Package");

        LogHandler.getInstance().debug(this.getClass(), transactionId + " _ " + response.getDebugString());

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return response;
        }

        long id = 0;
        List<HashMap<String, Object>> rows = response.getRows();
        for (HashMap<String, Object> row : rows) {
            id = (long) row.get("pDOCUMENT_ID");
        }
        response.setObject(id);
        return response;
    }

    @Override
    public DatabaseResponse getPackage(
            long packageId,
            String transactionId) throws Exception {
        String nameStore = "{ call USP_DOCUMENT_PACKAGE_GET(?,?)}";

        HashMap<String, Object> datas = new HashMap<>();
        datas.put("pDOCUMENT_PACKAGE_ID", packageId);

        HashMap<String, Integer> registerOutParam = new HashMap<>();
        registerOutParam.put("pRESPONSE_CODE", Types.VARCHAR);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                datas,
                registerOutParam,
                "Get Document Package");

        LogHandler.getInstance().debug(this.getClass(), transactionId + " _ " + response.getDebugString());

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return response;
        }

        long id = 0;
        List<HashMap<String, Object>> rows = response.getRows();
        for (HashMap<String, Object> row : rows) {
            id = (long) row.get("ID");
        }
        response.setObject(id);

        return response;
    }

    @Override
    public DatabaseResponse getDocuments(
            long packageId,
            String transactionId) throws Exception {
        String nameStore = "{ call USP_DOCUMENT_PACKAGE_GET_DOCUMENT(?,?)}";

        HashMap<String, Object> datas = new HashMap<>();
        datas.put("pDOCUMENT_PACKAGE_ID", packageId);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                datas,
                null,
                "Get Documents inside Package");

        LogHandler.getInstance().debug(this.getClass(), transactionId + " _ " + response.getDebugString());

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return response;
        }

        List<Document> listDoc = new ArrayList<>();
        List<HashMap<String, Object>> rows = response.getRows();
        for (HashMap<String, Object> row : rows) {
            Document doc = new Document();
            doc.setId((long) row.get("ID"));
            doc.setPackageId((long) row.get("DOCUMENT_PACKAGE_ID"));
            doc.setName((String) row.get("DOCUMENT_NAME"));
            doc.setSize((long) row.get("DOCUMENT_SIZE"));
            doc.setStatus(DocumentStatus.getStatus((int) row.get("DOCUMENT_STATUS")));
            doc.setEnabled(!((boolean) row.get("ENABLED")));
            doc.setUrl((String) row.get("URL"));
            doc.setType(DocumentType.PDF);
            doc.setDocumentHeight((float) row.get("DOCUMENT_HEIGHT"));
            doc.setDocumentWidth((float) row.get("DOCUMENT_WIDTH"));
            if ((String) row.get("DOCUMENT_CUSTOM_PAGESIZE") != null) {
                Document temp = MyServices.getJsonService().readValue((String) row.get("DOCUMENT_CUSTOM_PAGESIZE"), Document.class);
                doc.setDocumentCustomPageSize(temp.getDocumentCustomPageSize());
            }
            doc.setDocumentPages((int) row.get("DOCUMENT_PAGES"));
            doc.setDigest((String) row.get("DIGEST"));
            doc.setContent((String) row.get("CONTENT"));
            doc.setUuid((String) row.get("DOCUMENT_UUID"));
            doc.setDms((String) row.get("DMS_PROPERTY"));
            doc.setRevision((int) row.get("REVISION"));
            doc.setCreatedBy((String) row.get("CREATED_BY"));
            doc.setModifiedBy((String) row.get("LAST_MODIFIED_BY"));
            LocalDateTime date = (LocalDateTime) row.get("CREATED_AT");
            doc.setCreatedAt(new Date(date.toInstant(ZoneOffset.ofHours(7)).toEpochMilli()));
            date = (LocalDateTime) row.get("LAST_MODIFIED_AT");
            doc.setModifiedAt(new Date(date.toInstant(ZoneOffset.ofHours(7)).toEpochMilli()));
            listDoc.add(doc);
        }
        response.setObject(listDoc);

        return response;
    }

    @Override
    public DatabaseResponse updateStatusOfDocument(
            long documentId,
            User user,
            DocumentStatus status,
            String transactionId) throws Exception {
        String nameStore = "{ call USP_DOCUMENT_UPDATE(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

        HashMap<String, Object> datas = new HashMap<>();
        datas.put("pDOCUMENT_ID", documentId);
        datas.put("pDOCUMENT_NAME", null);
        datas.put("pDOCUMENT_SIZE", null);
        datas.put("pDOCUMENT_STATUS", status.getTypeId());
        datas.put("pURL", null);
        datas.put("pDOCUMENT_TYPE", null);
        datas.put("pMIME_TYPE", null);
        datas.put("pDOCUMENT_HEIGHT", null);
        datas.put("pDOCUMENT_WIDTH", null);
        datas.put("pDOCUMENT_CUSTOM_PAGESIZE", null);
        datas.put("pDIGEST", null);
        datas.put("pCONTENT", null);
        datas.put("pDOCUMENT_UUID", null);
        datas.put("pDMS_PROPERTY", null);
        datas.put("pHMAC", null);
        datas.put("pLAST_MODIFIED_BY", user.getEmail());

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                datas,
                null,
                "Get Documents inside Package");

        LogHandler.getInstance().debug(this.getClass(), transactionId + " _ " + response.getDebugString());

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return response;
        }

        List<Document> listDoc = new ArrayList<>();
        List<HashMap<String, Object>> rows = response.getRows();
        for (HashMap<String, Object> row : rows) {
            Document doc = new Document();
            doc.setId((long) row.get("ID"));
            doc.setPackageId((long) row.get("DOCUMENT_PACKAGE_ID"));
            doc.setName((String) row.get("DOCUMENT_NAME"));
            doc.setSize((long) row.get("DOCUMENT_SIZE"));
            doc.setStatus(DocumentStatus.getStatus((int) row.get("DOCUMENT_STATUS")));
            doc.setEnabled(!((boolean) row.get("ENABLED")));
            doc.setUrl((String) row.get("URL"));
            doc.setType(DocumentType.PDF);
            doc.setDocumentHeight((float) row.get("DOCUMENT_HEIGHT"));
            doc.setDocumentWidth((float) row.get("DOCUMENT_WIDTH"));
            if ((String) row.get("DOCUMENT_CUSTOM_PAGESIZE") != null) {
                Document temp = MyServices.getJsonService().readValue((String) row.get("DOCUMENT_CUSTOM_PAGESIZE"), Document.class);
                doc.setDocumentCustomPageSize(temp.getDocumentCustomPageSize());
            }
            doc.setDocumentPages((int) row.get("DOCUMENT_PAGES"));
            doc.setDigest((String) row.get("DIGEST"));
            doc.setContent((String) row.get("CONTENT"));
            doc.setUuid((String) row.get("DOCUMENT_UUID"));
            doc.setDms((String) row.get("DMS_PROPERTY"));
            doc.setRevision((int) row.get("REVISION"));
            doc.setCreatedBy((String) row.get("CREATED_BY"));
            doc.setModifiedBy((String) row.get("LAST_MODIFIED_BY"));
            LocalDateTime date = (LocalDateTime) row.get("CREATED_AT");
            doc.setCreatedAt(new Date(date.toInstant(ZoneOffset.ofHours(7)).toEpochMilli()));
            date = (LocalDateTime) row.get("LAST_MODIFIED_AT");
            doc.setModifiedAt(new Date(date.toInstant(ZoneOffset.ofHours(7)).toEpochMilli()));
            listDoc.add(doc);
        }
        response.setObject(listDoc);

        return response;
    }

    @Override
    public DatabaseResponse getDocumentIdFromUUID(
            String uuid,
            long pCLIENT_ID,
            String transactionId) throws Exception {
        String nameStore = "{ call USP_DOCUMENT_GET_FROM_UUID_ENTITY(?,?,?,?)}";

        HashMap<String, Object> datas = new HashMap<>();
        datas.put("pDOCUMENT_UUID", uuid);
        datas.put("pCLIENT_ID", pCLIENT_ID);

        HashMap<String, Integer> registerOutParam = new HashMap<>();
        registerOutParam.put("pDOCUMENT_ID", Types.BIGINT);
        registerOutParam.put("pRESPONSE_CODE", Types.VARCHAR);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                datas,
                registerOutParam,
                "Get Document Package from UUID");

        LogHandler.getInstance().debug(this.getClass(), transactionId + " _ " + response.getDebugString());

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return response;
        }

        long id = 0;
        List<HashMap<String, Object>> rows = response.getRows();
        for (HashMap<String, Object> row : rows) {
            id = (long) row.get("pDOCUMENT_ID");
        }
        response.setObject(id);

        return response;
    }
}
