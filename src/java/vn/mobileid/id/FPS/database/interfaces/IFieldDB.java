/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.database.interfaces;

import fps_core.enumration.RotateDegree;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.helper.ORM_JPA.database.objects.DatabaseResponse;

/**
 *
 * @author GiaTK
 */
public interface IFieldDB {
    /**
     * Get về giá trị field + field_detail của documentId + field_name truyền
     * vào
     *
     * @param documentId
     * @param fieldName
     * @param transactionId
     * @return
     * @throws Exception
     */
    public DatabaseResponse getFieldData(
            long documentId,
            String fieldName,
            String transactionId
    ) throws Exception;

    /**
     * Get về giá trị field + field_detail documentFieldId truyền vào
     *
     * @param documentFieldId
     * @param transactionId
     * @return
     * @throws Exception
     */
    public DatabaseResponse getFieldData(
            long documentFieldId,
            String transactionId
    ) throws Exception;

    public DatabaseResponse getFieldType(
            String transactionId
    ) throws Exception;

    public DatabaseResponse addField(
            long documentId,
            Object field,
            String hmac,
            String createdBy,
            String transactionId
    ) throws Exception;

    /**
     * Thêm chi tiết field (field_detail)
     *
     * @param documentFieldId
     * @param fieldTypeId
     * @param data
     * @param hmac
     * @param createdBy
     * @param transactionId
     * @return
     * @throws Exception
     */
    public DatabaseResponse addFieldDetails(
            long documentFieldId,
            long fieldTypeId,
            Object data,
            String hmac,
            String createdBy,
            String transactionId
    ) throws Exception;

    /**
     * Cập nhật lại thông tin Value trong DocumentField khi đã xử lý xong
     *
     * @param documentFieldId
     * @param dataAfterProcess
     * @param transactionId
     * @return
     * @throws Exception
     */
    public DatabaseResponse updateValuesOfField(
            long documentFieldId,
            User user,
            String dataAfterProcess,
            String transactionId
    ) throws Exception;

    /**
     * Cập nhật lại thông tin Hash trong DocumentField khi đã xử lý xong
     *
     * @param documentFieldId
     * @param dataAfterProcess
     * @param transactionId
     * @return
     * @throws Exception
     */
    public DatabaseResponse updateHashOfField(
            long documentFieldId,
            User user,
            String hash,
            String transactionId
    ) throws Exception;

    /**
     * Cập nhật lại field details
     *
     * @param documentFieldId
     * @param documentFieldDetailType
     * @param value
     * @param hmac
     * @param modifiedBy
     * @param transactionId
     * @return
     * @throws java.lang.Exception
     */
    public DatabaseResponse updateFieldDetails(
            long documentFieldId,
            long documentFieldDetailType,
            String value,
            String hmac,
            String modifiedBy,
            String transactionId
    ) throws Exception;

    public DatabaseResponse getFieldsData(
            long documentId,
            String trasactionId
    ) throws Exception;

    public DatabaseResponse deleteField(
            long documentId,
            String fieldName,
            String transactionId
    ) throws Exception;

    public DatabaseResponse deleteField(
            long documentFieldId,
            String transactionId
    ) throws Exception;

    public DatabaseResponse updateField(
            long documentFieldId,
            int type,
            String name,
            String value,
            String hash,
            int pageNumber,
            float width,
            float height,
            float top,
            float left,
            RotateDegree rotate,
            boolean visible,
            String hmac,
            String modifiedBy,
            String transactionId
    ) throws Exception;
}
