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
}
