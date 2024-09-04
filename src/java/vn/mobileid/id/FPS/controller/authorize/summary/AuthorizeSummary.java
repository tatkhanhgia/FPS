/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.authorize.summary;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import javax.servlet.http.HttpServletRequest;
import vn.mobileid.id.FPS.systemManagement.A_FPSConstant;
import vn.mobileid.id.FPS.controller.authorize.summary.micro.ManageTokenWithDB;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.Token;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.FPS.systemManagement.LogHandler;
import vn.mobileid.id.FPS.utils.CreateInternalResponse;
import vn.mobileid.id.FPS.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class AuthorizeSummary {

    //<editor-fold defaultstate="collapsed" desc="Process Login">
    /**
     * Process login request of the client
     *
     * @param request
     * @param payload
     * @param transactionID
     * @return
     * @throws Exception
     */
    public static InternalResponse processLogin(
            HttpServletRequest request,
            String payload,
            String transactionID) throws Exception {
        InternalResponse response = Checker.checkRevokeRequest(payload);
        if (!response.isValid()) {
            return response;
        }

        Token object = new Token();
        try {
            object = MyServices.getJsonService().readValue(payload, Token.class);
        } catch (JsonProcessingException ex) {
            return CreateInternalResponse.createBadRequestInternalResponse(A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE)
                    .setException(ex);
        }

        //Login 
        if (Utils.isNullOrEmpty(object.getGrantType())) {
            return CreateInternalResponse.createBadRequestInternalResponse(A_FPSConstant.CODE_KEYCLOAK,
                    A_FPSConstant.SUBCODE_MISSING_GRANT_TYPE);
        }
        switch (object.getGrantType()) {
            case "client_credentials": {
                if (Utils.isNullOrEmpty(object.getClientId())) {
                    return CreateInternalResponse.createBadRequestInternalResponse(A_FPSConstant.CODE_KEYCLOAK,
                            A_FPSConstant.SUBCODE_MISSING_CLIENT_ID);
                }
                if (Utils.isNullOrEmpty(object.getClientSecret())) {
                    return CreateInternalResponse.createBadRequestInternalResponse(A_FPSConstant.CODE_KEYCLOAK,
                            A_FPSConstant.SUBCODE_MISSING_CLIENT_SECRET);
                }
                return ManageTokenWithDB.login(
                        object,
                        transactionID);
            }
            default: {
                return CreateInternalResponse.createBadRequestInternalResponse(A_FPSConstant.CODE_KEYCLOAK,
                        A_FPSConstant.SUBCODE_UNSUPPORTED_GRANT_TYPE);
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Process Verify">
    /**
     * Process verify request from the client
     *
     * @param token
     * @param transactionID
     * @return
     * @throws Exception
     */
    public static InternalResponse processVerify(
            String token,
            String transactionID
    ) throws Exception {
        if (Utils.isNullOrEmpty(token)) {
            return CreateInternalResponse.createBadRequestInternalResponse(A_FPSConstant.SUBCODE_MISSING_ACCESS_TOKEN);
        }
        token = token.replaceAll("Bearer ", "");

        //Decode JWT
        String[] chunks = token.split("\\.");

        String header = null;
        String payload = null;
        String signature = null;
        String alg = null;
        User data = null;

        try {
            header = new String(Base64.getUrlDecoder().decode(chunks[0]), "UTF-8");
            payload = new String(Base64.getUrlDecoder().decode(chunks[1]), "UTF-8");
            signature = chunks[2];
            int pos = header.indexOf("alg");
            int typ = header.indexOf("typ");
            alg = header.substring(pos + 6, typ - 3);
            data = MyServices.getJsonService().readValue(payload, User.class);
        } catch (UnsupportedEncodingException e) {
            return CreateInternalResponse.createErrorInternalResponse(
                    A_FPSConstant.HTTP_CODE_UNAUTHORIZED,
                    A_FPSConstant.CODE_KEYCLOAK,
                    A_FPSConstant.SUBCODE_INVALID_TOKEN).setException(e);
        } catch (Exception e) {
            return CreateInternalResponse.createErrorInternalResponse(
                    A_FPSConstant.HTTP_CODE_UNAUTHORIZED,
                    A_FPSConstant.CODE_KEYCLOAK,
                    A_FPSConstant.SUBCODE_INVALID_TOKEN).setException(e);
        }
        String stringToBeVerify = chunks[0] + "." + chunks[1];
        return ManageTokenWithDB.verify(
                data,
                stringToBeVerify,
                signature,
                transactionID,
                true);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Process Revoke Token">
    /**
     * Process revoke token of the client
     *
     * @param request
     * @param payload
     * @param transactionID
     * @return
     * @throws Exception
     */
    public static InternalResponse processRevoke(
            HttpServletRequest request,
            String payload,
            String transactionID) throws Exception {
        InternalResponse response = Checker.checkRevokeRequest(payload);
        if (!response.isValid()) {
            return response;
        }

        Token object = new Token();
        try {
            object = MyServices.getJsonService().readValue(payload, Token.class);
        } catch (JsonProcessingException ex) {
            return CreateInternalResponse.createBadRequestInternalResponse(A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE)
                    .setException(ex);
        }

        //Login 
        if (Utils.isNullOrEmpty(object.getGrantType())) {
            return CreateInternalResponse.createBadRequestInternalResponse(A_FPSConstant.CODE_KEYCLOAK,
                    A_FPSConstant.SUBCODE_MISSING_GRANT_TYPE);
        }
        switch (object.getGrantType()) {
            case "client_credentials": {
                if (Utils.isNullOrEmpty(object.getClientId())) {
                    return CreateInternalResponse.createBadRequestInternalResponse(A_FPSConstant.CODE_KEYCLOAK,
                            A_FPSConstant.SUBCODE_MISSING_CLIENT_ID);
                }
                if (Utils.isNullOrEmpty(object.getClientSecret())) {
                    return CreateInternalResponse.createBadRequestInternalResponse(A_FPSConstant.CODE_KEYCLOAK,
                            A_FPSConstant.SUBCODE_MISSING_CLIENT_SECRET);
                }
                return ManageTokenWithDB.login(
                        object,
                        transactionID);
            }
            default: {
                return CreateInternalResponse.createBadRequestInternalResponse(A_FPSConstant.CODE_KEYCLOAK,
                        A_FPSConstant.SUBCODE_UNSUPPORTED_GRANT_TYPE);
            }
        }
    }
    //</editor-fold>

    public static void main(String[] args) {
        try {
            String temp = "abc";
            String[] chunks = temp.split(" ");
            String payload = new String(Base64.getUrlDecoder().decode(chunks[1]), "UTF-8");
        } catch (Exception e) {
            System.out.println(Utils.summaryException(e));
        }
    }
}
