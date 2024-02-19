/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.QryptoService.process;

import vn.mobileid.id.FPS.QryptoService.object.Property;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.FPS.QryptoService.object.Configuration;
import vn.mobileid.id.FPS.exception.LoginException;
import vn.mobileid.id.FPS.QryptoService.object.QRSchema;
import vn.mobileid.id.FPS.exception.QryptoException;
import vn.mobileid.id.FPS.QryptoService.object.QRSchema.field;
import vn.mobileid.id.FPS.QryptoService.request.ClaimRequest;
import vn.mobileid.id.FPS.QryptoService.request.GetTokenRequest;
import vn.mobileid.id.FPS.QryptoService.response.ClaimResponse;
import vn.mobileid.id.FPS.QryptoService.response.DownloadFileTokenResponse;
import vn.mobileid.id.FPS.QryptoService.response.GetTokenResponse;
import vn.mobileid.id.FPS.QryptoService.response.IssueQryptoWithFileAttachResponse;
import vn.mobileid.id.helper.http.HttpPostMultiPart2;
import vn.mobileid.id.helper.http.HttpResponse;
import vn.mobileid.id.helper.http.HttpUtils;

/**
 *
 * @author GiaTK
 */
public class QryptoSession implements ISession {

    private String bearerToken;
    private String refreshToken;
    private Property prop;
    private int retryLogin = 0;

    public QryptoSession(Property prop) {
        this.prop = prop;
    }

    @Override
    public void login() throws Exception {
//        System.out.println("____________auth/login____________");
        String authHeader = null;

//        if (refreshToken != null) {
//            authHeader = refreshToken;
//        } else {
        try {
            retryLogin++;
            authHeader = prop.getAuthorization();
        } catch (Throwable ex) {
            Logger.getLogger(QryptoSession.class.getName()).log(Level.SEVERE, null, ex);
        }
//        }
        GetTokenRequest loginRequest = new GetTokenRequest();
//        loginRequest.setRefresh_token(QryptoConstant.RefreshTokenTest);

        String jsonReq = new ObjectMapper().writeValueAsString(loginRequest);

        HttpResponse response = HttpUtils.sendPost(prop.getBaseUrl() + "/general/auth/login", jsonReq, authHeader);

        if (!response.isStatus()) {
            try {
                throw new QryptoException(response.getMsg());
            } catch (Exception ex) {
                LogHandler.info(QryptoSession.class, "Error - Detail:" + ex);
            }
        }

//        System.out.println("Response Message:" + response.getMsg());
        GetTokenResponse qryptoResp = new ObjectMapper().readValue(response.getMsg(), GetTokenResponse.class);
        if (qryptoResp.getCode() == 3005 || qryptoResp.getCode() == 3006) {
            refreshToken = null;
            if (retryLogin >= 5) {
                retryLogin = 0;
                LogHandler.info(QryptoSession.class,
                        "Err code: " + qryptoResp.getCode()
                        + "\nProblem: " + qryptoResp.getProblem()
                        + "\nDetails:" + qryptoResp.getDetails());
                throw new Exception(qryptoResp.getProblem() + " - " + qryptoResp.getDetails());
            }
            login();
        } else if (qryptoResp.getCode() != 0) {
            LogHandler.info(QryptoSession.class,
                    "Err code: " + qryptoResp.getCode()
                    + "\nProblem: " + qryptoResp.getProblem()
                    + "\nDetails:" + qryptoResp.getDetails());
            throw new QryptoException(qryptoResp.getProblem() + " - " + qryptoResp.getDetails());
        } else {
            this.bearerToken = "Bearer " + qryptoResp.getAccess_token();

            if (qryptoResp.getRefresh_token() != null) {
                this.refreshToken = "Bearer " + qryptoResp.getRefresh_token();
            }
        }
    }

    @Override
    public IssueQryptoWithFileAttachResponse issueQryptoWithFileAttach(
            QRSchema QR,
            Configuration configuration
    ) throws Exception {
        //        System.out.println("____________auth/login____________");
        String authHeader = null;

        if (bearerToken != null) {
            authHeader = bearerToken;
        } else {
            retryLogin++;
            this.login();
            if (retryLogin == 2) {
                throw new Exception("Cannot login again!");
            }
        }

        Map<String, String> headers = new HashMap<>();
//        headers.put("Content-Type", "application/json; charset=UTF-8");
        headers.put("Accept-Charset", "UTF-8");
        headers.put("Authorization", bearerToken);

        String temp = new ObjectMapper().writeValueAsString(QR);

        HttpPostMultiPart2 a = new HttpPostMultiPart2();
        Map<String, Object> bodypart = new HashMap<>();
        bodypart.put("payload", temp);

        bodypart.put("configuration", new ObjectMapper().writeValueAsString(configuration));
//        System.out.println("Config:" + new ObjectMapper().writeValueAsString(configuration));

        HashMap<String,String> names = new HashMap<>();
        for (String key : QR.getHeader().keySet()) {
//            System.out.println("Put bodypart:" + key);
            bodypart.put(key, QR.getHeader().get(key));
            for (field field : QR.getFormat().getFields()) {
                if (field.getType().equals(QRSchema.fieldType.f1) && field.getFile_field().equals(key)) {
                    names.put(key,field.getFile_name() != null ? !"".equals(field.getFile_name()) ? field.getFile_name() : key : key);
//                    System.out.println("Putname:"+names.get(key));
                }
                if (field.getType().equals(QRSchema.fieldType._4T1P) && field.getFile_field().equals(key)) {
                    names.put(key,field.getFile_name() != null ? !"".equals(field.getFile_name()) ? field.getFile_name() : key : key);
//                    System.out.println("Putname:"+names.get(key));
                }
            }
        }

//        System.out.println("Payload:" + temp);

        org.apache.http.HttpResponse response = a.sendPost(prop.getBaseUrl() + "/issuance/qrci/issueQryptoWithAttachment", headers, bodypart, names);
        StringBuilder sb = new StringBuilder();
        for (int ch; (ch = response.getEntity().getContent().read()) != -1;) {
            sb.append((char) ch);
        }
        String message = sb.toString();
        IssueQryptoWithFileAttachResponse responses = new IssueQryptoWithFileAttachResponse();
        responses = new ObjectMapper().readValue(message, IssueQryptoWithFileAttachResponse.class);
        if (responses.getStatus() == 1009 && response.getStatusLine().getStatusCode() == 401) {
            throw new LoginException(responses.getStatus() + "\n" + responses.getData() + "\n" + responses.getMessage());
        }
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new QryptoException(responses.getStatus() + "\n" + responses.getData() + "\n" + responses.getMessage());
        }
        return responses;
    }

    @Override
    public ClaimResponse dgci_wallet_claim(ClaimRequest request) throws Exception {
        String authHeader = null;

        if (bearerToken != null) {
            authHeader = bearerToken;
        } else {
            retryLogin++;
            this.login();
            if (retryLogin == 2) {
                throw new Exception("Cannot login again!");
            }
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", bearerToken);

        String jsonReq = new ObjectMapper().writeValueAsString(request);
        System.out.println("Qrypto Session - JsonRequest:" + jsonReq);
        HttpResponse response = HttpUtils.sendPost(prop.getBaseUrl() + "/issuance/qrci/wallet/claim", jsonReq, authHeader);
        if (response.getHttpCode() != 0) {
            try {
                throw new Exception(response.getMsg());
            } catch (Exception ex) {
                ex.printStackTrace();
                LogHandler.error(QryptoSession.class, "Error - Detail:", ex);
            }
        }
//        System.out.println("credentials/list response.getMsg() = "+ response.getMsg());
        ClaimResponse responses = new ObjectMapper().readValue(response.getMsg(), ClaimResponse.class);

        return responses;
    }

    @Override
    public DownloadFileTokenResponse downloadFileToken(String fileToken) throws Exception {
        String authHeader = null;

        if (bearerToken != null) {
            authHeader = bearerToken;
        } else {
            retryLogin++;
            this.login();
            if (retryLogin == 2) {
                throw new Exception("Cannot login again!");
            }
        }

        System.out.println("URL:" + (prop.getBaseUrl() + "/verifier/" + fileToken + "/download/base64"));
        HttpResponse response = HttpUtils.sendGet(
                prop.getBaseUrl() + "/verifier/" + fileToken + "/download/base64",
                HttpUtils.ContentType.JSON,
                null,
                authHeader);

        System.out.println("Status:" + response.getHttpCode());
//        System.out.println("Response Message:" + response.getMsg());
        DownloadFileTokenResponse qryptoResp = new ObjectMapper().readValue(response.getMsg(), DownloadFileTokenResponse.class);
        if (qryptoResp.getCode() == 3005 || qryptoResp.getCode() == 3006) {
            refreshToken = null;
            if (retryLogin >= 5) {
                retryLogin = 0;
                LogHandler.info(QryptoSession.class,
                        "Err code: " + qryptoResp.getCode()
                        + "\nProblem: " + qryptoResp.getProblem()
                        + "\nDetails:" + qryptoResp.getDetails());
                throw new Exception(qryptoResp.getProblem() + " - " + qryptoResp.getDetails());
            }
            login();
            return downloadFileToken(fileToken);
        } else if (qryptoResp.getCode() != 0) {
            LogHandler.info(QryptoSession.class,
                    "Err code: " + qryptoResp.getCode()
                    + "\nProblem: " + qryptoResp.getProblem()
                    + "\nDetails:" + qryptoResp.getDetails());
            throw new QryptoException(qryptoResp.getProblem() + " - " + qryptoResp.getDetails());
        } else {
            return qryptoResp;
        }
    }

}
