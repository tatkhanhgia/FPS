/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.field;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.controller.ResponseMessageController;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.Signature;
import vn.mobileid.id.general.database.DatabaseFactory;
import vn.mobileid.id.helper.database.objects.DatabaseResponse;
import vn.mobileid.id.utils.Broadcast;

/**
 *
 * @author GiaTK
 */
public class AddField extends Broadcast{
    //<editor-fold defaultstate="collapsed" desc="Add Field">
    /**
     * Thêm field vào documentId - Add the field into the documentId.
     * Lưu ý: Khi thêm vào sẽ trả về thông tin field (id, name) thì khi đó sử dụng id Field để thêm chi tiết (nếu có)
     * Note: When the processing of "Add field" done, the response data (include id, name of the field) will return and use that
     * documentFieldId to add more detail of the field
     * @param documentId
     * @param field
     * @param hmac
     * @param createdBy
     * @param transactionId
     * @return int FieldId
     * @throws Exception 
     */
    protected static InternalResponse addField(
            long documentId,
            Object field,
            String hmac,
            String createdBy,
            String transactionId
    )throws Exception{
        DatabaseResponse response = DatabaseFactory.getDatabaseImpl_field().addField(
                documentId,
                field,
                hmac,
                createdBy,
                transactionId);
        
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

    //<editor-fold defaultstate="collapsed" desc="Add Detail Field">
    /**
     * Thêm chi tiết field vào documentField - Add more details of the field
     * @param documentFieldId
     * @param fieldTypeId
     * @param field
     * @param hmac
     * @param createdBy
     * @param transactionId
     * @return none
     * @throws Exception 
     */
    protected static InternalResponse addDetailField(
            long documentFieldId,
            long fieldTypeId,
            Object field,
            String hmac,
            String createdBy,
            String transactionId
    )throws Exception{
        DatabaseResponse response = DatabaseFactory.getDatabaseImpl_field().addFieldDetails(
                documentFieldId,
                fieldTypeId, 
                field,
                hmac, 
                createdBy,
                transactionId);
        if(response.getStatus() != A_FPSConstant.CODE_SUCCESS){
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            response.getStatus()
            );
        } 
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }
    //</editor-fold>
    
    
    public static void main(String[] args) throws JsonProcessingException, Exception{
        //Test add field
//        String data = "{\"field_name\":\"signature1\",\"page\":1,\"dimension\":{\"x\":0,\"y\":0,\"width\":100,\"height\":50},\"visible_enabled\":true}";        
//        BasicFieldAttribute field = new ObjectMapper().readValue(data, BasicFieldAttribute.class);
//        Resources.reloadFieldTypes();
//        HashMap<String, FieldType> temppp = Resources.getFieldTypes();
//        String q = "";
//        for(String key : temppp.keySet()){
//            q = key;
//        }
//        field.setType(temppp.get(q));
//        InternalResponse res = AddField.addField(
//                2,
//                field,
//                "hmac",
//                "giatk",
//                "TRANSCTION");
//        
//        if(res.getStatus() !=A_FPSConstant.HTTP_CODE_SUCCESS){
//            System.out.println("Message:"+res.getMessage());
//        } else {
//            System.out.println("Id:"+res.getData());
//        }

          //Test function add detail field
          HashMap<String, Object> data = new HashMap<>();
          Signature sig = new Signature();
          sig.setLtv(true);
          sig.setSigningLocation("Quan 2");
          sig.setSigningReason("Testing");
          List<String> level = new ArrayList<>();
          level.add("SIGNATURE");
          data.put("verification", sig);
          data.put("level_of_assurance", level);
          InternalResponse res = AddField.addDetailField(
                  1,
                  1, //textbox
                  data,
                  "hmac",
                  "GIATK",
                  "TRANSACTIONID");
          if(res.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS){
              System.out.println("Message:"+res.getMessage());
          } else {
              System.out.println("Status:"+res.getStatus());
          }
    }
}
