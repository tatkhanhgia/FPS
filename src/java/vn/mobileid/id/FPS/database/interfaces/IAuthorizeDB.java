/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.database.interfaces;

import java.util.Date;
import vn.mobileid.id.helper.ORM_JPA.database.objects.DatabaseResponse;

/**
 *
 * @author GiaTK
 */
public interface  IAuthorizeDB {
    public DatabaseResponse getAPIKey(
            String clientId,
            String transactionID) throws Exception;

    public DatabaseResponse writeRefreshToken(
            String sessionId,
            String jwtId,
            boolean clientCredentials,
            String clientId,
            Date issuedAt,
            Date expiredAt,
            String hmac,
            String createdBy,
            String transactionId
    ) throws Exception;

    public DatabaseResponse checkSessionId(
            String jwtId,
            String sessionId,
            String transactionId
    ) throws Exception;
    
    public DatabaseResponse deleteToken(
            String sessionId,
            String transactionId
    ) throws Exception;
}
