/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.QryptoService.process;

import java.util.HashMap;
import vn.mobileid.id.FPS.QryptoService.object.Property;

/**
 *
 * @author GiaTK
 */
public class SessionFactory {    
    
    private QryptoSession session;
    
    private Property prop;

    private SessionFactory(Property prop) {        
        this.prop = prop;
    }
       
    public static SessionFactory getInstance(Property prop) throws Throwable {
        String key = prop.getBaseUrl();

        SessionFactory factory = new SessionFactory(prop);
        ((SessionFactory) factory).session = new QryptoSession(prop);

        return factory;
    }
    
    public QryptoSession getSession(){
        return session;
    }
}
