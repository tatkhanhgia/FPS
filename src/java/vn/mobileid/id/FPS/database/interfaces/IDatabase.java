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
public interface  IDatabase {
    public DatabaseResponse getListResponseCode() throws Exception;

    public DatabaseResponse getPolicies(
            int type,
            String transactionId
    ) throws Exception;

    public DatabaseResponse temporalAdd(
            String documentHash,
            String hash,
            int type,
            byte[] data,
            String transactionId
    ) throws Exception;

    public DatabaseResponse temporalGet(
            String identifyName,
            String identifyName2,
            String transactionId
    ) throws Exception;

    public DatabaseResponse temporalDelete(
            String identifyName,
            String transactionId
    ) throws Exception;

    public DatabaseResponse temporalList(
            String identifyName,
            String transactionId
    ) throws Exception;

    public DatabaseResponse createAPILog(
            long pENTERPRISE_ID,
            long pDOCUMENT_ID,
            String pTRANSACTION_ID,
            String pAPP_NAME,
            String pAPI_KEY,
            String pVERSION,
            String pSERVICE_NAME,
            String pURL,
            String pHTTP_VERB,
            int pSTATUS_CODE,
            String pREQUEST,
            String pRESPONSE,
            String pException,
            String pHMAC,
            String pCREATED_BY,
            String transactionId
    ) throws Exception;

    public DatabaseResponse getRemarkLanguage(
            String table,
            String name,
            String languageName,
            String transactionId) throws Exception;
}
