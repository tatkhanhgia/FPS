/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.enterprise;

import vn.mobileid.id.FPS.object.InternalResponse;

/**
 *
 * @author GiaTK
 */
public class ConnectorEnterprise {
   
   public static InternalResponse getEnterpriseInfo(
            int id,
            String transactionId
    ) throws Exception {       
       return GetEnterpriseInfo.getEnterpriseInfo(id, transactionId);
   }
   
   public static InternalResponse getKEYAPI(
           String clientId,
           String transactionId
   ) throws Exception{
       return GetKEYAPI.getKEYAPI(clientId, transactionId);
   }
   
   public static InternalResponse getKeyAPIRule(
           long apiKeyType,
           String transactionId
   )throws Exception{
       return GetAPIKeyRule.getAPIKeyRule(apiKeyType, transactionId);
   }
}
