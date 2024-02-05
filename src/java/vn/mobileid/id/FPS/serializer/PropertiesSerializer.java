/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author GiaTK
 */
public class PropertiesSerializer implements JsonSerializable {
    
    private Properties prop;
    
    public PropertiesSerializer(Properties prop) {
        this.prop = prop;
    }
    
    @Override
    public void serialize(JsonGenerator jg, SerializerProvider sp) throws IOException {
        jg.writeStartObject();
        for (String key : prop.stringPropertyNames()) {            
            if (((String) prop.getProperty(key)).contains(";")) {
//                jg.writeArrayFieldStart(key);
                jg.writeFieldName(key);
                jg.writeStartArray();
                for (String temp : ((String) prop.get(key)).split(";")) {
                    jg.writeObject(temp);
                }
                jg.writeEndArray();                
            } else {
//                System.out.println("Key:" + key);
//                System.out.println("Value:" + prop.getProperty(key));
                String temp = prop.getProperty(key);
                if (prop.get(key) instanceof Integer) {
                    jg.writeNumberField(key, (Integer) prop.get(key));
                }                
                jg.writeStringField(key, temp);
            }
        }
        jg.writeEndObject();
    }
    
    @Override
    public void serializeWithType(JsonGenerator jg, SerializerProvider sp, TypeSerializer ts) throws IOException {        
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
