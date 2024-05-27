/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.object.email;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author GiaTK
 */
public class SMTPProperties {

    private static final Logger LOG = LogManager.getLogger(SMTPProperties.class);

    private  boolean ssl_enable;
    private  boolean startTLS;
    private  boolean auth;
    private  String host;
    private  int port;
    private  String username;
    private  String password;
    private  String sendFromAddr;
    private  String sendFromName;
    private static  Properties prop;

    public static Properties getProp() {
        return SMTPProperties.prop;
    }

    public static void setProp(Properties prop) {
        SMTPProperties.prop = prop;
    }
    
    
    
    public  Logger getLOG() {
        return LOG;
    }

    public  boolean isSsl_enable() {
        return ssl_enable;
    }

    public  boolean isStartTLS() {
        return startTLS;
    }

    public  boolean isAuth() {
        return auth;
    }

    public  String getHost() {
        return host;
    }

    public  int getPort() {
        return port;
    }

    public  String getUsername() {
        return username;
    }

    public  String getPassword() {
        return password;
    }

    public  String getSendFromAddr() {
        return sendFromAddr;
    }

    public  String getSendFromName() {
        return sendFromName;
    }

    public  void setSsl_enable(boolean ssl_enable) {
        ssl_enable = ssl_enable;
    }

    public  void setStartTLS(boolean startTLS) {
        startTLS = startTLS;
    }

    public  void setAuth(boolean auth) {
        auth = auth;
    }

    public  void setHost(String host) {
        host = host;
    }

    public  void setPort(int port) {
        port = port;
    }

    public  void setUsername(String username) {
        username = username;
    }

    public  void setPassword(String password) {
        password = password;
    }

    public  void setSendFromAddr(String sendFromAddr) {
        sendFromAddr = sendFromAddr;
    }

    public  void setSendFromName(String sendFromName) {
        sendFromName = sendFromName;
    }

    
    
//    public HashMap<String, Object> getProperties() {
//        HashMap<String, Object> map = new HashMap<>();
//        Field[] fields = WorkflowDetail_Option.class.getDeclaredFields();
//        for (Field field : fields) {
//            try {
//                if (field.get(this) instanceof Integer) {
//                    Integer temp = field.getInt(this);
//                    map.put(field.getName(), temp);
//                }
//                if (field.get(this) instanceof Boolean) {                    
//                    map.put(field.getName(), field.getBoolean(this));
//                }
//                if (field.get(this) instanceof String) {                    
//                    map.put(field.getName(),(String) field.get(this));
//                }
//            } catch (IllegalArgumentException ex) {
//                java.util.logging.Logger.getLogger(SMTPProperties.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (IllegalAccessException ex) {
//                java.util.logging.Logger.getLogger(SMTPProperties.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//        }
//        return map;
//    }
}
