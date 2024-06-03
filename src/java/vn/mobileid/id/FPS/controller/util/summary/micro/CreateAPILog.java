/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.util.summary.micro;

import java.util.HashSet;
import java.util.Set;
import vn.mobileid.id.FPS.database.DatabaseFactory;
import vn.mobileid.id.FPS.database.implement.DatabaseImpl;
import vn.mobileid.id.helper.ORM_JPA.database.objects.DatabaseResponse;

/**
 *
 * @author GiaTK
 */
public class CreateAPILog {
    //<editor-fold defaultstate="collapsed" desc="Create API Log">
    public static void createAPILog(
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
            String pEXCEPTION,
            String pHMAC, 
            String pCREATED_BY,
            String transactionId
    ){
        try{
            DatabaseResponse response = DatabaseFactory.getDatabaseImpl().createAPILog(
                    pENTERPRISE_ID, 
                    pDOCUMENT_ID,
                    pTRANSACTION_ID,
                    pAPP_NAME,
                    pAPI_KEY, 
                    pVERSION, 
                    pSERVICE_NAME, 
                    pURL, 
                    pHTTP_VERB,
                    pSTATUS_CODE,
                    pREQUEST,
                    pRESPONSE, 
                    pEXCEPTION,
                    pHMAC, 
                    pCREATED_BY, 
                    transactionId);
        } catch(Exception ex){
            System.err.println("Cannot create API Log for transaction:"+transactionId);
        }
    }
    //</editor-fold>
    
    public static void main(String[] args) {
        createAPILog(
                0, 
                0,
                "app",
                "app", 
                "api", 
                "ver", 
                "ser",
                "url",
                "post", 
                100,
                "reques", 
                "resp", 
                "ex",
                "hmac",
                "gia", 
                "transactionId");
    }
}
