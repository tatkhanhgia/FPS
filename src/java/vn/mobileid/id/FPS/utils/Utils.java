/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.FPS.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fps_core.enumration.FPSFont;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.bind.DatatypeConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.hc.core5.http.HttpHeaders;
import vn.mobileid.id.FPS.controller.authorize.summary.AuthorizeSummary;
import vn.mobileid.id.FPS.controller.util.summary.micro.CreateAPILog;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.systemManagement.LogHandler;
import vn.mobileid.id.FPS.systemManagement.PolicyConfiguration;

/**
 *
 * @author ADMIN
 */
public class Utils {

    private static final Logger LOG = LogManager.getLogger(Utils.class);

    public static void sendMessage(
            HttpServletResponse response,
            int status,
            String contentType,
            Object message,
            String transactionId) throws IOException {
        switch (contentType) {
            case "application/json": {
                if (Utils.isNullOrEmpty((String) message)) {
                    if (status != 200) {
                        response.sendError(status);
                    } else {
                        response.setStatus(status);
                    }
                } else {
                    response.setStatus(status);
                    response.addHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
                    response.addHeader("x-transaction-id", transactionId);
                    response.getOutputStream()
                            .write(((String) message).getBytes());
                }
                return;
            }
            case "application/octet-stream": {
                if (Utils.isNullOrEmpty((byte[]) message)) {
                    response.sendError(status);
                } else {
                    response.setStatus(status);
                    response.addHeader(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
                    response.addHeader("x-transaction-id", transactionId);
                    response.getOutputStream()
                            .write((byte[]) message);
                }
                return;
            }
            default: {
                if (Utils.isNullOrEmpty((String) message)) {
                    response.sendError(status);
                } else {
                    response.setStatus(status);
                    response.addHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
                    response.addHeader("x-transaction-id", transactionId);
                    response.getOutputStream()
                            .write(((String) message).getBytes());
                }
                return;
            }
        }
    }

    public static void sendMessage(
            HttpServletResponse response,
            String contentType,
            InternalResponse res,
            String transactionId) throws IOException {
        switch (contentType) {
            case "application/json": {
                if (Utils.isNullOrEmpty(res.getMessage())) {
                    if (res.getStatus() != 200) {
                        response.sendError(res.getStatus());
                    } else {
                        response.setStatus(res.getStatus());
                    }
                } else {
                    response.setStatus(res.getStatus());
                    response.addHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
                    response.addHeader("x-transaction-id", transactionId);
                    for (String key : res.getHeaders().keySet()) {
                        response.addHeader(key, (String) res.getHeaders().get(key));
                    }
                    response.getOutputStream()
                            .write((res.getMessage()).getBytes());
                }
                return;
            }
            case "application/octet-stream": {
                if (Utils.isNullOrEmpty((byte[]) res.getData())) {
                    response.sendError(res.getStatus());
                } else {
                    response.setStatus(res.getStatus());
                    response.addHeader(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
                    response.addHeader("x-transaction-id", transactionId);
                    for (String key : res.getHeaders().keySet()) {
                        response.addHeader(key, (String) res.getHeaders().get(key));
                    }
                    response.getOutputStream()
                            .write((byte[]) res.getData());
                }
                return;
            }
            default: {
                if (Utils.isNullOrEmpty(res.getMessage())) {
                    if (res.getStatus() != 200) {
                        response.sendError(res.getStatus());
                    } else {
                        response.setStatus(res.getStatus());
                    }
                } else {
                    response.setStatus(res.getStatus());
                    response.addHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
                    response.addHeader("x-transaction-id", transactionId);
                    for (String key : res.getHeaders().keySet()) {
                        response.addHeader(key, (String) res.getHeaders().get(key));
                    }
                    response.getOutputStream()
                            .write((res.getMessage()).getBytes());
                }
                return;
            }
        }
    }

    public static String getPropertiesFile(String fileName) {
        return walk(System.getProperty("jboss.server.base.dir"), fileName);
    }

    public static String walk(String path, String fileName) {
        try (Stream<Path> walk = Files.walk(Paths.get(path))) {

            List<String> result = walk.filter(Files::isRegularFile)
                    .map(x -> x.toString()).collect(Collectors.toList());

            for (String f : result) {
                if (f.contains(fileName)) {
                    return f;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String generateTransactionID() {
        String billCode = null;
        try {
            billCode = generateOneTimePassword(4) + "-" + generateOneTimePassword(5) + "-" + generateOneTimePassword(5);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return billCode;
    }

    public static String generateSignatureName(String typeProfile) {
        String billCode = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
            sdf.setTimeZone(TimeZone.getTimeZone(System.getProperty("user.timezone")));
            String dateTime = sdf.format(new Date(System.currentTimeMillis()));
            billCode = "signature-" + typeProfile + "-" + dateTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return billCode;
    }

    public static String generateOneTimePassword(int len) {
        String numbers = "0123456789";
        Random rndm_method = new Random();
        char[] otp = new char[len];
        for (int i = 0; i < len; i++) {
            otp[i] = numbers.charAt(rndm_method.nextInt(numbers.length()));
        }
        return new String(otp);
    }

    //<editor-fold defaultstate="collapsed" desc="Generate Random String">
    public static String generateRandomString(int lenght) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = lenght;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString;
    }
    //</editor-fold>

    public static boolean isNullOrEmpty(String value) {
        if (value == null) {
            return true;
        }
        return value.compareTo("") == 0;
    }

    public static boolean isNullOrEmpty(String[] value) {
        if (value == null) {
            return true;
        }
        return value.length == 0;
    }

    public static boolean isNullOrEmpty(byte[] value) {
        if (value == null) {
            return true;
        }
        return value.length == 0;
    }

    public static boolean isNullOrEmpty(List value) {
        if (value == null) {
            return true;
        }
        return value.isEmpty();
    }
    
    public static boolean isNullOrEmpty(Object obj) {
        if (obj == null) {
            return true;
        }

        if (obj instanceof String) {
            return ((String) obj).isEmpty();
        }

        if (obj instanceof Collection) {
            return ((Collection<?>) obj).isEmpty();
        }

        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).isEmpty();
        }

        if (obj instanceof Array) {
            return Array.getLength(obj) == 0;
        }

        return false;
    }

    public static byte[] genRandomArray(int size) throws NoSuchAlgorithmException, NoSuchProviderException {
        // TODO Auto-generated method stub
        byte[] random = new byte[size];
        new Random().nextBytes(random);
        return random;
    }

    public static byte[] saveByteArrayOutputStream(InputStream body) {
        int c;
        byte[] r = null;
        try {
            ByteArrayOutputStream f = new ByteArrayOutputStream();
            while ((c = body.read()) > -1) {
                f.write(c);
            }
            r = f.toByteArray();
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }

    public static String getRequestHeader(final HttpServletRequest request, String headerName) {
        String headerValue = null;
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            headerValue = request.getHeader(key);
            if (key.compareToIgnoreCase(headerName) == 0) {
                return headerValue;
            } else {
                headerValue = null;
            }
        }
        return headerValue;
    }

    public static HashMap<String, String> getHashMapRequestHeader(final HttpServletRequest request) {
        String headerValue = null;
        HashMap<String, String> hashMap = new HashMap<>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            headerValue = request.getHeader(key);
            hashMap.put(key, headerValue);
        }
        return hashMap;
    }

    public static byte[] getBinaryStream(HttpServletRequest request) {
        byte[] stream = null;
        try {
            InputStream is = request.getInputStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            for (int nChunk = is.read(buf); nChunk != -1; nChunk = is.read(buf)) {
                bos.write(buf, 0, nChunk);
            }
            stream = bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stream;
    }

    public static String getPayload(HttpServletRequest request) throws IOException {
        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        body = stringBuilder.toString();
        return body;
    }

    public static String generateUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static Date convertToUTC(Date d) throws ParseException {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        String s = isoFormat.format(d);
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = isoFormat.parse(s);
        return date;
    }

    public static String convertToUTC_String(Date d) throws ParseException {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        String s = isoFormat.format(d);
        return s;
    }

    public static String convertToGMT(Date d) throws ParseException {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");
        isoFormat.setTimeZone(TimeZone.getTimeZone("GMT+07:00"));
        String s = isoFormat.format(d);
        return s;
    }

    public static Date sqlDateToJavaDate(LocalDateTime date) {
        return new Date(date.toInstant(ZoneOffset.ofHours(7)).toEpochMilli());
    }

    public static HashMap<String, String> getDataFromURLEncode(String payload) {
        try {
            HashMap<String, String> map = new HashMap<>();
            String[] temp = payload.split("&");
            for (String temp2 : temp) {
                StringTokenizer token = new StringTokenizer(temp2, "=", false);
                while (token.hasMoreElements()) {
                    String name = token.nextToken();
                    String value = URLDecoder.decode(token.nextToken(), StandardCharsets.UTF_8.toString());
                    map.put(name, value);
                }
            }
            return map;
        } catch (Exception e) {
            LOG.error("Cannot parse data from URL Encode");
            return null;
        }
    }

    public static String hashMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes());
            byte[] digest = md.digest();
            return DatatypeConverter.printHexBinary(digest).toUpperCase();
        } catch (NoSuchAlgorithmException ex) {
            return null;
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Get From JSON _ Read all tree">   
    public static Object getFromJson_(String name, String json) {
        try {
            JsonNode root = new ObjectMapper().readTree(json);
            Iterator<Map.Entry<String, JsonNode>> nodes = root.fields();
            while (nodes.hasNext()) {
                Entry<String, JsonNode> object = nodes.next();
                String key = object.getKey();
                if (key.equals(name)) {
                    if (object.getValue().isBoolean()) {
                        return object.getValue().asBoolean();
                    }
                    if (object.getValue().isFloat()) {
                        return object.getValue().asDouble();
                    }
                    if (object.getValue().isLong()) {
                        return object.getValue().asLong();
                    }
                    if (object.getValue().isInt()) {
                        return object.getValue().asInt();
                    }
                    if (object.getValue().isTextual()) {
                        return object.getValue().asText();
                    }
                    if (object.getValue().isArray()) {
                        return object.getValue().asToken().asByteArray();
                    }
                    if (object.getValue().isContainerNode()) {
                        Object temp = object.getValue().toPrettyString();
                        if (temp == null) {
                            continue;
                        }
                        return temp;
                    }
                    if (object.getValue().isDouble()) {
                        return object.getValue().asDouble();
                    }
                }
                if (object.getValue().isContainerNode()) {
                    Object temp = getFromJson_(name, object.getValue().toPrettyString());
                    if (temp == null) {
                        continue;
                    }
                    return temp;
                }
            }
            return null;
        } catch (Exception ex) {
            return null;
        }

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get From JSON">
    public static String getFromJson(String name, String json) {
        try {
            JsonNode node = new ObjectMapper().readTree(json);
            return node.findValue(name).asText();
        } catch (Exception ex) {
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Summarize payload">   
    public static JsonNode summarizePayload(String payload) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(payload);
            return summarizeNode(rootNode);
        } catch (Exception ex) {
            return null;
        } 
    }

    private static JsonNode summarizeNode(JsonNode node) {
        ObjectMapper mapper = new ObjectMapper();
        if (node.isObject()) {
            ObjectNode summarizedObject = mapper.createObjectNode();
            node.fields().forEachRemaining(entry -> {
                String key = entry.getKey();
                JsonNode value = entry.getValue();
                if (value.isTextual()) {
                    String originalValue = value.asText();
                    String summarizedValue = originalValue.length() > 150 ? originalValue.substring(0, 150) + "..." : originalValue;
                    summarizedObject.put(key, summarizedValue);
                } else if (value.isObject()) {
                    summarizedObject.set(key, summarizeNode(value));
                } else if (value.isArray()) {
                    ArrayNode summarizedArray = mapper.createArrayNode();
                    value.elements().forEachRemaining(element -> summarizedArray.add(summarizeNode(element)));
                    summarizedObject.set(key, summarizedArray);
                } else {
                    summarizedObject.set(key, value);
                }
            });
            return summarizedObject;
        } else {
            return node;
        }
    }
    //</editor-fold>

    public static String getTransactionId(HttpServletRequest request, String payload) {
        String temp = Utils.getFromJson("client_id", payload);
        if (temp == null) {
            return request.getRemoteAddr() + "_" + Utils.generateOneTimePassword(5);
        } else {
            return request.getRemoteAddr() + "_" + Utils.getFromJson("client_id", payload) + "_" + Utils.generateOneTimePassword(5);
        }
    }

    public static String getDataRequestToLog(
            HttpServletRequest request,
            String transactionId,
            String apiName,
            String payload) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n\t");
        builder.append("TransactionId:").append(transactionId);
        builder.append("\n\t");
        builder.append("API:").append(apiName);
        builder.append("\n\t");
        builder.append("ID:").append(Utils.getIdFromURL(request.getRequestURI()));
        builder.append("Method:").append(request.getMethod());
        builder.append("\n\t");
        builder.append("IP Address:").append(request.getRemoteAddr());
        builder.append("\n\t");
        builder.append("Payload:").append(payload);
        return builder.toString();
    }

    public static InternalResponse verifyAuthorizationToken(HttpServletRequest request, String transactionId) throws Exception {
        String token = Utils.getRequestHeader(request, "Authorization");
        if (Utils.isNullOrEmpty(token)) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_UNAUTHORIZED,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_MISSING_AUTHORIZATION_HEADER
            );
        }
        InternalResponse response = AuthorizeSummary.processVerify(token, transactionId);
        return response;
    }

    public static long getIdFromURL(String uri) {
        for (String temp : uri.split("/")) {
            try {
                long id = Long.parseLong(temp);
                return id;
            } catch (Exception ex) {
            }
        }
        return 0;
    }

    public static byte[] readFile(String path) throws Exception {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream(path);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[4];

        while ((nRead = stream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        byte[] targetArray = buffer.toByteArray();
        return targetArray;
    }

    //<editor-fold defaultstate="collapsed" desc="Read FPSFont File">
    public static byte[] readFile(FPSFont path) throws Exception {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream(path.getPath());
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[4];

        while ((nRead = stream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        byte[] targetArray = buffer.toByteArray();
        return targetArray;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get IP From HTTPRequest">
    public static String getIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Create API Log">
    public static void createAPILog(
            HttpServletRequest req,
            String payload,
            int documentId,
            InternalResponse response,
            Exception ex,
            String transactionId
    ) {
        //Show hierarchical log
        LogHandler.showHierarchicalLog(response.getHierarchicalLog());
        
        int entId = 0;
        String apiKey = "";
        if (response.getEnt() != null) {
            entId = response.getEnt().getId();
            apiKey = response.getEnt().getClientID();
        }
        if (response.getUser() != null) {
            entId = response.getUser().getAid();
            apiKey = response.getUser().getScope();
        }

        String exceptionSummary = Utils.summaryException(ex);

        //<editor-fold defaultstate="collapsed" desc="Summarize all data which is long data">
        JsonNode summary = Utils.summarizePayload(payload);
        JsonNode summary2 = Utils.summarizePayload(response.getMessage());
        //</editor-fold>

        CreateAPILog.createAPILog(
                entId,
                documentId,
                transactionId,
                req.getParameter("User-Agent"),
                apiKey,
                "FPS_V1",
                req.getParameter("User-Agent"),
                req.getRequestURI(),
                req.getMethod(),
                response.getStatus(),
                summary == null ? null : summary.toPrettyString(),
                summary2 == null ? null : summary2.toPrettyString(),
                exceptionSummary,
                "HMAC",
                Utils.getIp(req),
                transactionId);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Summary the Exception">
    public static String summaryException(Exception ex) {
        if (ex == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] trace = null;
        sb.append(ex.getClass().getName());
        sb.append(": ");
        sb.append(ex.getLocalizedMessage());
        if (ex.getCause() != null) {
            sb.append("\n");
            sb.append("Cause by:").append(ex.getCause().getMessage());
            trace = ex.getCause().getStackTrace();
        } else {
            trace = ex.getStackTrace();
        }
        sb.append("\n\t");
        List<String> temp = new ArrayList<>();
        for (int i = trace.length - 1; i >= 0; i--) {
//            if (trace[i].getClassName().equals(HttpServlet.class.getCanonicalName())) {
            for (int j = i; j >= 0; j--) {
                temp.add(trace[j].getClassName() + " at(" + trace[j].getMethodName() + ":" + trace[j].getLineNumber() + ")");
            }
//                break;
//            }
        }
        for (int i = (temp.size() - 1); i >= 0; i--) {
            sb.append(String.format("%5s", temp.get(i)));
            sb.append("\n\t");
        }
        return sb.toString();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="get User Email from JWT Token">
    public static User getUserFromBearerToken(String bearerToken) {
        if (bearerToken == null | bearerToken.isEmpty()) {
            return null;
        }
        bearerToken = bearerToken.replaceAll("Bearer ", "");

        String[] chunks = bearerToken.split("\\.");

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
            data = new ObjectMapper().readValue(payload, User.class);
        } catch (Exception e) {
            return null;
        }

        return data;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Merger JSON">
    public static JsonNode merge(String mainNode_, String updateNode_) throws JsonProcessingException {
        JsonNode mainNode = new ObjectMapper().readTree(mainNode_);
        JsonNode updateNode = new ObjectMapper().readTree(updateNode_);
        Iterator<String> fieldNames = updateNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode jsonNode = mainNode.get(fieldName);
            // if field exists and is an embedded object
            if (jsonNode != null && jsonNode.isObject()) {
                ((ObjectNode) mainNode).put(fieldName, merge(jsonNode.toPrettyString(), updateNode.get(fieldName).toPrettyString()));
            } else {
                if (mainNode instanceof ObjectNode) {
                    // Overwrite field
                    JsonNode value = updateNode.get(fieldName);
                    ((ObjectNode) mainNode).put(fieldName, value);
                }
            }

        }
        return mainNode;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Check newLine">
    public static int checkNewLine(String value) {
        String[] number = value.split("\n");
        return number.length;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get timestamp">
    public static String getTimestamp() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    PolicyConfiguration
                            .getInstant()
                            .getSystemConfig()
                            .getAttributes()
                            .get(0)
                            .getDateFormat());
            return dateFormat.format(new Date(System.currentTimeMillis()));
        } catch (Exception ex) {
            SimpleDateFormat dateFormat = new SimpleDateFormat();
            return dateFormat.format(System.currentTimeMillis());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Hash field in Dokobit rule">
    public static String hashAndExtractMiddleSixChars(String input) throws NoSuchAlgorithmException {
        // Sử dụng thuật toán SHA-256 để hash
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(input.getBytes());

        // Chuyển đổi byte array thành chuỗi hex
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        // Lấy 6 ký tự ở giữa của chuỗi hex
        int middleIndex = hexString.length() / 2 - 3; // Chọn giữa và lấy 6 ký tự
        return hexString.substring(middleIndex, middleIndex + 6);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Convert ISO Date String into Custom Format Date">
    public static String convertISOStringToCustom(String isoDate, String format) {
        // Parse the input date string into an Instant object
        Instant instant = Instant.parse(isoDate);

        // Convert the Instant to LocalDateTime with the desired time zone (UTC in this case)
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));

        // Define the desired output format
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(format);

        // Format the LocalDateTime object into the desired output format
        return dateTime.format(outputFormatter);
    }
    //</editor-fold>

    public static void main(String[] args) throws NoSuchAlgorithmException {
        String iso = "2024-04-23T07:08:13Z";
        System.out.println(convertISOStringToCustom(iso, "dd-MM-yyyy"));

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("DDD");
        String formattedDate = currentDate.format(formatter);
        System.out.println(formattedDate);
    }
}
