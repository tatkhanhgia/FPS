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
import java.util.List;

/**
 *
 * @author GiaTK
 */
public class FieldsSerializer implements JsonSerializable {

    private Object[] types;
    private String[] names = new String[]{
        "textbox",
        "checkbox", 
        "radiobox",
        "qr", 
        "initial",
        "inperson", 
        "signature", 
        "dropdown", 
        "listbox",
        "qrypto",
        "stamp",
        "camera",
        "attachment",
        "hyperlink"};

    @Override
    public void serialize(JsonGenerator jg, SerializerProvider sp) throws IOException {
        jg.writeStartObject();
        //loop throught types
        for (int i = 0; i < types.length; i++) {
            if (types[i] != null) {
                jg.writeFieldName(names[i]);
                jg.writeStartArray();
                List<?> fields = (List<?>) types[i];
                for (Object field : fields) {
                    jg.writeObject(field);
                }
                jg.writeEndArray();
            }
        }
        jg.writeEndObject();
    }

     public FieldsSerializer(Object[] types) {
        this.types = types;
    }   
    
    @Override
    public void serializeWithType(JsonGenerator jg, SerializerProvider sp, TypeSerializer ts) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
