/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.database.interfaces;

import vn.mobileid.id.helper.ORM_JPA.database.objects.DatabaseResponse;

/**
 *
 * @author GiaTK
 */
public interface IEnterpriseDB {
    public DatabaseResponse getEntepriseInfo(
            int enterpriseId,
            String enterpriseName,
            String transactionID) throws Exception;

    public DatabaseResponse getRule(
            long apiKeyRule,
            String transactionID
    ) throws Exception;
}
