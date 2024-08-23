/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.authorize.summary.micro;

import vn.mobileid.id.FPS.controller.authorize.summary.micro.ManageRefreshToken;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.Future;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import vn.mobileid.id.FPS.controller.enterprise.summary.EnterpriseSummary;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.object.Enterprise;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.Token;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.FPS.systemManagement.LogHandler;
import vn.mobileid.id.FPS.utils.Crypto;
import vn.mobileid.id.FPS.services.others.threadManagement.TaskV2;
import vn.mobileid.id.FPS.services.others.threadManagement.ThreadManagement;
import vn.mobileid.id.FPS.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class ManageTokenWithDB {

    private static long now;

    //<editor-fold defaultstate="collapsed" desc="Login">
    public static InternalResponse login(
            Token data,
            String transactionID) throws Exception {
        InternalResponse callDb = EnterpriseSummary.getKEYAPI(
                data.getClientId(),
                transactionID);

        if (callDb.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return callDb;
        }
        Enterprise ent = (Enterprise) callDb.getData();

        //<editor-fold defaultstate="collapsed" desc="Check data in ent + TokenRequest">
        if (ent == null) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_UNAUTHORIZED,
                    A_FPSConstant.CODE_KEYCLOAK,
                    A_FPSConstant.SUBCODE_INVALID_CLIENT_ID);
        }
        if (!data.getClientId().equals(ent.getClientID())) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_UNAUTHORIZED,
                    A_FPSConstant.CODE_KEYCLOAK,
                    A_FPSConstant.SUBCODE_INVALID_CLIENT_ID
            );
        }
        if (!data.getClientSecret().equals(ent.getClientSecret())) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_UNAUTHORIZED,
                            A_FPSConstant.CODE_KEYCLOAK,
                            A_FPSConstant.SUBCODE_INVALID_CLIENT_SECRET
            );
        }
        //</editor-fold>

        InternalResponse res2 = EnterpriseSummary.getEnterpriseInfo(
                ent.getId(),
                transactionID);
        if (res2.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return res2;
        }
        Enterprise enterprise = (Enterprise) res2.getData();
        enterprise.setIdOfClientID(ent.getIdOfClientID());
        enterprise.setClientID(ent.getClientID());
        enterprise.setClientSecret(ent.getClientSecret());
        try {
            String[] temp = createAccess_RefreshToken(enterprise);

            String accessToken = temp[0];
            String refreshtoken = temp[1];
            String sessionID = temp[2];
            String iat = temp[3];
            String exp = temp[4];

            Token response = new Token();
            response.setAccessToken(accessToken);
            if (data.isRememberMeEnabled()) {
                response.setRefreshToken(refreshtoken);
                response.setRefreshEpiresIn((int) A_FPSConstant.refresh_token_expired_in);
            }
            response.setExpiresIn((int) A_FPSConstant.expired_in);
            response.setTokenType(A_FPSConstant.TOKEN_TYPE_BEARER);

            //Write refreshtoken into DB                       
            InternalResponse internalResponse = ManageRefreshToken.write(
                    "null",
                    sessionID,
                    true,
                    ent.getClientID(),
                    new Date(Long.parseLong(iat)),
                    new Date(Long.parseLong(exp)),
                    "HMAC",
                    enterprise.getName(),
                    transactionID);
            if (internalResponse.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return internalResponse;
            }

            InternalResponse res = new InternalResponse(
                    A_FPSConstant.HTTP_CODE_SUCCESS,
                    MyServices.getJsonService().writeValueAsString(response));
            res.setEnt(ent);
            return res;
        } catch (Exception e) {
            throw new Exception("Cannot create token!", e);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Verify">
    public static InternalResponse verify(
            User data,
            String stringToBeVerify,
            String signature,
            String transactionID,
            boolean isRefreshToken) throws Exception {
//        ExecutorService executor = Executors.newFixedThreadPool(2);
        try(ThreadManagement threadPool = MyServices.getThreadManagement(2)){
        Future<?> verify = threadPool.submit(new TaskV2(new Object[]{stringToBeVerify, signature, getPublicKey()}, transactionID) {
            @Override
            public Object call() {
                InternalResponse response = new InternalResponse();
                try {
                    String stringToBeVerify = (String) this.get()[0];
                    String signature = (String) this.get()[1];
                    PublicKey pub = (PublicKey) this.get()[2];
                    boolean check = verifyTokenByCode(stringToBeVerify, signature, pub);
                    if (check) {
                        response.setStatus(A_FPSConstant.HTTP_CODE_SUCCESS);
                    } else {
                        response.setStatus(A_FPSConstant.HTTP_CODE_BAD_REQUEST);
                    }
                } catch (Exception ex) {
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_UNAUTHORIZED,
                                    A_FPSConstant.CODE_KEYCLOAK,
                                    A_FPSConstant.SUBCODE_INVALID_TOKEN);
                }
                return response;
            }
        });

        Future<?> date = threadPool.submit(new TaskV2(new Object[]{data}, transactionID) {
            @Override
            public Object call() {
                InternalResponse response = new InternalResponse();
                try {
                    User data = (User) this.get()[0];
                    Date date = new Date();
                    if (data.getExp() < date.getTime()) {
                        return new InternalResponse(
                                A_FPSConstant.HTTP_CODE_UNAUTHORIZED,
                                        A_FPSConstant.CODE_KEYCLOAK,
                                        A_FPSConstant.SUBCODE_TOKEN_EXPIRED);
                    }
                    //Check accessToken in DB
                    if (!isRefreshToken) {
                        InternalResponse res = ManageRefreshToken.checkToken(
                                data.getJti(),
                                data.getSid(),
                                transactionID);
                        if (res.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                            return res;
                        }
                    }
                    response.setStatus(A_FPSConstant.HTTP_CODE_SUCCESS);
                    response.setData(data);
                    return response;
                } catch (Exception ex) {
                    return response;
                }
            }
        });

        InternalResponse temp = (InternalResponse) verify.get();
        InternalResponse temp2 = (InternalResponse) date.get();

        if (temp.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return temp;
        }
        return temp2;
        } catch (Exception ex){
            throw ex;
        }
    }
    //</editor-fold>

    //==========================================================================
    //<editor-fold defaultstate="collapsed" desc="Create Header">
    /**
     * Create Header of JWT
     *
     * @return
     */
    private static String createHeader() {
        String temp = "{";
        temp += "\"alg\":" + "\"" + "RS256" + "\",";
        temp += "\"typ\":" + "\"" + A_FPSConstant.typ + "\"";
        temp += "}";
        return temp;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Create Basic Payload Data in JWT">
    /**
     * Create data of JWT
     *
     * @param user
     * @param enterprise
     * @return
     */
    private static User createBasicPayloadData(
            Enterprise enterprise
    ) {
        User temp = new User();
        temp.setIat(Date.from(Instant.now()).getTime()); //Issue at
        temp.setExp(Date.from(Instant.now().plusSeconds(A_FPSConstant.expired_in)).getTime()); //Expired                
//        temp.setJti(""); //JWT ID
        temp.setIss("https://fps.mobile-id.vn");  //Issuer
        temp.setAud("enterprise"); //Audience
        temp.setSub("FPS"); //Subject
        temp.setTyp(A_FPSConstant.TOKEN_TYPE_BEARER);
        temp.setAzp(enterprise.getName()); //Authorized Party
        temp.setAid(enterprise.getId());
        Long temps = enterprise.getIdOfClientID();
        temp.setIci(temps.intValue()); //Identity of Client ID
//        temp.setSession_state("");
//        temp.setAcr(email);    //Authentication context class
        temp.setScope(enterprise.getClientID());
        temp.setSid(Utils.generateTransactionID());  //Session-ID
//        temp.setEmail_verified(false);
//        temp.setName(user.getName());
//        temp.setPreferred_username(enterprise_name);
//        temp.setGiven_name(name);
//        temp.setFamily_name(name);
//        temp.setEmail(enterprise.get);
        temp.setMobile(enterprise.getMobileNumber());
        return temp;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Create Access + Refresh Token">
    /**
     * Create an Access Token + RefreshToke + SessionID + Time Position of
     * parameter [0] : accessToken [1] : refreshToken [2] : sessionID [3] : Iat
     * (Issue At) [4] : expired RefreshToken
     *
     * @param user
     * @param enterprise
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     * @throws Exception
     */
    private static String[] createAccess_RefreshToken(
            Enterprise enterprise) throws IOException, GeneralSecurityException, Exception {
        String header = createHeader();
        User temp = createBasicPayloadData(enterprise);
        String payload = MyServices.getJsonService().writeValueAsString(temp);
        header = Base64.getUrlEncoder().withoutPadding().encodeToString(header.getBytes());
        payload = Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes());
        String signature = Crypto.sign(
                header + "." + payload,
                getPrivateKey(),
                "base64",
                A_FPSConstant.alg);
        String[] result = new String[5];
        result[0] = header + "." + payload + "." + signature;
        result[1] = createRefreshToken(temp);
        result[2] = temp.getSid();
        result[3] = String.valueOf(temp.getIat());
        Date temps = new Date(temp.getIat());
        result[4] = String.valueOf(temps.toInstant().plusSeconds(A_FPSConstant.refresh_token_expired_in).toEpochMilli());
        return result;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Create Refresh Token">
    private static String createRefreshToken(
            User user) throws JsonProcessingException, IOException, GeneralSecurityException, Exception {
        //Create Data
        user.setName(null);
        user.setEmail(null);
        user.setTyp(A_FPSConstant.TOKEN_TYPE_REFRESH);
        long now = user.getIat();
        Date temp = new Date(now);
        user.setExp(temp.toInstant().plusSeconds(A_FPSConstant.refresh_token_expired_in).toEpochMilli());
        String payload = MyServices.getJsonService().writeValueAsString(user);
        String header = createHeader();
        header = Base64.getUrlEncoder().withoutPadding().encodeToString(header.getBytes());
        payload = Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes());
        String signature = Crypto.sign(
                header + "." + payload,
                getPrivateKey(),
                "base64",
                A_FPSConstant.alg);
        return header + "." + payload + "." + signature;
    }
    //</editor-fold>

    private static String hmacSha256(String data, String secret) throws Exception {
        try {
            byte[] hash = secret.getBytes(StandardCharsets.UTF_8);
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(hash, "HmacSHA256");
            sha256Hmac.init(secretKey);

            byte[] signedBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            return Base64.getUrlEncoder().withoutPadding().encodeToString(signedBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
//            LogHandler.error(
//                    ManageTokenWithDB.class,
//                    "transaction",
//                    "Cannot Create Signature for JWT!",
//                    ex);
//            return null;
            throw new Exception("Cannot create Signature for JWT!", ex);
        }
    }

    private static String getPrivateKey() throws IOException, GeneralSecurityException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream input = loader.getResourceAsStream("resources/config/key.key");
        String file = IOUtils.toString(input, StandardCharsets.UTF_8);
        PrivateKey pri = Crypto.getPrivateKeyFromString(file, "base64");
        return Base64.getEncoder().encodeToString(pri.getEncoded());
    }

    private static PublicKey getPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream input = loader.getResourceAsStream("resources/config/key.pub");
        String file = IOUtils.toString(input, StandardCharsets.UTF_8);
        String privateKeyPEM = file;
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN RSA PUBLIC KEY-----\n", "");
        privateKeyPEM = privateKeyPEM.replace("-----END RSA PUBLIC KEY-----", "");
        byte[] encoded = DatatypeConverter.parseBase64Binary(privateKeyPEM);
        X509EncodedKeySpec spec
                = new X509EncodedKeySpec(encoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    /**
     *
     * @param token
     * @param publicKey
     * @return
     */
    private static InternalResponse verifyTokenByOAUTH(
            String token,
            PublicKey publicKey) {
        try {
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) publicKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            DecodedJWT result = verifier.verify(token);
            return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, "");
        } catch (TokenExpiredException e) {
            return new InternalResponse(A_FPSConstant.HTTP_CODE_UNAUTHORIZED,
                            A_FPSConstant.CODE_KEYCLOAK,
                            A_FPSConstant.SUBCODE_TOKEN_EXPIRED).setException(e);
        } catch (JWTVerificationException e) {
            return new InternalResponse(A_FPSConstant.HTTP_CODE_UNAUTHORIZED,
                            A_FPSConstant.CODE_KEYCLOAK,
                            A_FPSConstant.SUBCODE_INVALID_TOKEN).setException(e);
        }
    }

    /**
     * Verify token with my code
     *
     * @param data
     * @param signature
     * @param pub
     * @return
     * @throws Exception
     */
    private static boolean verifyTokenByCode(
            String data,
            String signature,
            PublicKey pub) throws Exception {
        try {
            Security.addProvider(new BouncyCastleProvider());
            Signature sign = Signature.getInstance(A_FPSConstant.alg);
            sign.initVerify(pub);
            sign.update(data.getBytes());
            return sign.verify(Base64.getUrlDecoder().decode(signature));
        } catch (NoSuchAlgorithmException ex) {
//            throw new Exception("NoSuchAlgorithm!", ex);
            return false;
        } catch (InvalidKeyException ex) {
//            throw new Exception("InvalidKeyException!", ex);
            return false;
        } catch (SignatureException ex) {
//            throw new Exception("SignatureException!", ex);
            return false;
        }
    }

    /**
     * Check payload of the request is meet the condition (user + pass)
     *
     * @param payload
     * @return
     */
    private static boolean checkPayload(String payload) {
        if (!payload.contains("username") || !payload.contains("password")) {
            return false;
        }

        String[] count = payload.split("password");
        if (count.length <= 2) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, FileNotFoundException, IOException, GeneralSecurityException, Exception {
//        System.out.println(createHeader());
//        User a = new User();
//        a.setEmail("giatk@mobile-id.vn");
//        System.out.println(createPayload(a));
//        String a = createAccess_RefreshToken("Tất Khánh Gia", "giatk@mobile-id.vn", "1", "mobile-id");
//        System.out.println(a);
//        String signature = hmacSha256(a, getPrivateKey());
//        System.out.println("Signature:"+signature);
//        a += "." + signature;
//        System.out.println("JWT:"+a);
//        KeyPair kp = Crypto.generateRSAKey(100);
//        Key pub = kp.getPublic();
//        Key pri = kp.getPrivate();
//        String outFile = "key";
//        Writer out = new FileWriter("file/"+outFile + ".key");
//        out.write("-----BEGIN RSA PRIVATE KEY-----\n");
//        out.write(Base64.getEncoder().encodeToString(pri.getEncoded()));
//        out.write("\n-----END RSA PRIVATE KEY-----\n");
//        out.close();
//
//        out = new FileWriter("file/"+outFile + ".pub");
//        out.write("-----BEGIN RSA PUBLIC KEY-----\n");
//        out.write(Base64.getEncoder().encodeToString(pub.getEncoded()));
//        out.write("\n-----END RSA PUBLIC KEY-----\n");
//        out.close();
//        System.out.println(hmacSha256("khanhgia1234567889", pri.getEncoded().));

//Login
//        String email = "giatk@mobile-id.vn";
//        String pass = "thienthan123";
//        Database db = new DatabaseImpl();
//        try {
//            //Login
//            DatabaseResponse res = db.login(email, pass);
//            if (res.getStatus() != A_FPSConstant.CODE_SUCCESS) {
//                String message = ResponseMessageController.getErrorMessage(A_FPSConstant.CODE_FAIL,
//                        res.getStatus(),
//                        "en",
//                        null);
//
//            }
//
//            User info = (User) res.getObject();
//            String access = createAccess_RefreshToken(info);
//            System.out.println("Ace:" + access);
//        } catch (Exception e) {
//            System.out.println("Ex" + e);
//
//        }
//Get publickey
//    verifyTokenByOAUTH("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NzY0MjY3ODk5MDksImlhdCI6MTY3NjQyNjc4OTYwOSwiaXNzIjoiZW50ZXJwcmlzZWlkIiwiYXVkIjoiYWNjb3VudCIsInR5cCI6IkJlYXJlciIsIm5hbWUiOiJUP3QgS2jhbmggR2lhIiwiZW1haWwiOiJnaWF0a0Btb2JpbGUtaWQudm4iLCJhenAiOiJlbnRlcnByaXNlTmFtZSIsIm1vYmlsZSI6IjA1NjY0Nzc4NDcifQ.J9B-U5ZX2mWj6SUiCUgf4_p1BMHMx77tA_QgQmKbVlYkBHquXIL7jceKI7P4ffeEV_l3mQGJAl4p_3p87Vf_4mj6XVFtkQVm3N905tRbp4kKc7Ay7TQkxx3zZSt3c2kPRXxbVs16GF0FhILbnnItIqOCAN8ouMw7g8Lk-2T0SaOPmsKSyiNoMg6wRqzccfwcOi3PpZbXgQ95yRCHU3PKIJeX6dzpkt4ziNCrvVNXgOcl-zjjtevnQwjNeoIxkl8Nf3Rn0N_V_vmJOMPw770KBq1ifWquuDn8KAc8i1Xq7onZVKC_mCsSka7BzLMNYSIxOfhnwKEfi65hgSL3fwTJlQ", getPublicKey());
//        String data = "tatkhanhgia";
//        
//        
//        Security.addProvider(new BouncyCastleProvider());
//
//        String privateKeyPEM = "";
//        List<String> fata = Files.readAllLines(new File("D:\\NetBean\\qrypto\\src\\java\\key.key").toPath());
//        for (String temp : fata) {
//            privateKeyPEM += temp + "\n";
//        }
//
//        privateKeyPEM = privateKeyPEM.replace("-----BEGIN RSA PRIVATE KEY-----\n", "");
//        privateKeyPEM = privateKeyPEM.replace("-----END RSA PRIVATE KEY-----", "");
//        
//        byte[] encoded = DatatypeConverter.parseBase64Binary(privateKeyPEM);
//        KeyFactory kf = KeyFactory.getInstance("RSA");
//        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
//        PrivateKey privKey = (PrivateKey) kf.generatePrivate(keySpec);
//        Signature sig = Signature.getInstance("SHA1withRSA");
//        sig.initSign(privKey);
//        sig.update(data.getBytes());
//        
//        byte[] signature =sig.sign();
//        String signature2= Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
//        
//        
//        
//        
//        byte[] decodeSig = Base64.getUrlDecoder().decode(signature2);
//        String publicPEM = "";
//        List<String>fata2 = Files.readAllLines(new File("D:\\NetBean\\qrypto\\src\\java\\key.pub").toPath());
//        for (String temp : fata2) {
//            publicPEM += temp + "\n";
//        }
//        publicPEM = publicPEM.replace("-----BEGIN RSA PUBLIC KEY-----\n", "");
//        publicPEM = publicPEM.replace("-----END RSA PUBLIC KEY-----", "");
//        publicPEM = publicPEM.trim();
//        System.out.println("Public:"+publicPEM);
//        byte[]encoded2 = DatatypeConverter.parseBase64Binary(publicPEM);
//        X509EncodedKeySpec spec
//                = new X509EncodedKeySpec(encoded2);
//        kf = KeyFactory.getInstance("RSA");
//        X509EncodedKeySpec spec2
//                = new X509EncodedKeySpec(encoded2);        
//        RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(spec2);
//        sig.initVerify(pubKey);
//        sig.update(data.getBytes());
//        System.out.println("Boo:"+sig.verify(decodeSig));
        //Test
//    String signature = Crypto.sign("tatkhanhgia", getPrivateKey(), "base64");
//        System.out.println("Ve:"+verifyTokenByCode("tatkhanhgia",Base64.getUrlDecoder().decode(signature), getPublicKey()));
    }
}
