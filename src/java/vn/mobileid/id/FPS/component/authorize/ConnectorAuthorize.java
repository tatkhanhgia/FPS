/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.authorize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import javax.servlet.http.HttpServletRequest;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.controller.ResponseMessageController;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.Token;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.FPS.systemManagement.LogHandler;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class ConnectorAuthorize {

    //<editor-fold defaultstate="collapsed" desc="Process Login">
    public static InternalResponse processLogin(
            HttpServletRequest request,
            String payload,
            String transactionID) throws Exception {
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_NO_PAYLOAD_FOUND);
        }

        Token object = new Token();
        try {
            object = MyServices.getJsonService().readValue(payload, Token.class);
        } catch (JsonProcessingException ex) {
            LogHandler.error(
                    ManageTokenWithDB.class,
                    transactionID,
                    "Cannot parse payload!");
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
            );
        }

        //Login 
        if (Utils.isNullOrEmpty(object.getGrantType())) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_KEYCLOAK,
                    A_FPSConstant.SUBCODE_MISSING_GRANT_TYPE);
        }
        switch (object.getGrantType()) {
            case "client_credentials": {
                if (Utils.isNullOrEmpty(object.getClientId())) {
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_KEYCLOAK,
                            A_FPSConstant.SUBCODE_MISSING_CLIENT_ID);
                }
                if (Utils.isNullOrEmpty(object.getClientSecret())) {
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_KEYCLOAK,
                            A_FPSConstant.SUBCODE_MISSING_CLIENT_SECRET
                    );
                }
                return ManageTokenWithDB.login(
                        object,
                        transactionID);
            }
            default: {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_KEYCLOAK,
                        A_FPSConstant.SUBCODE_UNSUPPORTED_GRANT_TYPE);
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Process Verify">
    public static InternalResponse processVerify(
            String token,
            String transactionID
    ) throws Exception {
        if (Utils.isNullOrEmpty(token)) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_MISSING_ACCESS_TOKEN
            );
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
            LogHandler.error(
                    ManageTokenWithDB.class,
                    transactionID,
                    "Error while decode token!");
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_UNAUTHORIZED,
                    A_FPSConstant.CODE_KEYCLOAK,
                    A_FPSConstant.SUBCODE_INVALID_TOKEN);
        } catch (Exception e) {
            LogHandler.error(
                    ManageTokenWithDB.class,
                    transactionID,
                    "Error while parsing Data!");
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_UNAUTHORIZED,
                    A_FPSConstant.CODE_KEYCLOAK,
                    A_FPSConstant.SUBCODE_INVALID_TOKEN
            );
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

}
