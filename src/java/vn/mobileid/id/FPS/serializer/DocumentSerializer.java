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
import vn.mobileid.id.FPS.object.CustomPageSize;
import vn.mobileid.id.FPS.object.Document;

/**
 *
 * @author GiaTK
 */
public class DocumentSerializer implements JsonSerializable {
    private long id;
    private List<Document> list;

    public DocumentSerializer(List<Document> list, long id) {
        this.list = list;
        this.id = id;
    }        
    
    @Override
    public void serialize(JsonGenerator jg, SerializerProvider sp) throws IOException {
        jg.writeStartArray();
        //loop
        for (int i = 0; i < list.size(); i++) {
            jg.writeStartObject();
            //Write data
                jg.writeNumberField("document_id", id);
                jg.writeStringField("document_name", list.get(i).getName());
                jg.writeStringField("document_status", list.get(i).getStatus().name());
                jg.writeStringField("document_type", list.get(i).getType().getType());
                jg.writeNumberField("document_height", list.get(i).getDocumentHeight());
                jg.writeNumberField("document_width", list.get(i).getDocumentWidth());
                jg.writeNumberField("document_pages", list.get(i).getDocumentPages());
                jg.writeNumberField("document_revision", list.get(i).getRevision());
                jg.writeStringField("uploaded_by", list.get(i).getCreatedBy());
                if(list.get(i).getDocumentCustomPageSize() != null){
                    jg.writeFieldName("document_custom_page");
                    jg.writeStartArray();
                    for(CustomPageSize custom: list.get(i).getDocumentCustomPageSize()){
                        jg.writeStartObject();
                            jg.writeNumberField("page_start", custom.getPageStart());
                            jg.writeNumberField("page_end", custom.getPageEnd());
                            jg.writeNumberField("page_rotate", custom.getRotate());
                            jg.writeNumberField("page_height", custom.getPageHeight());
                            jg.writeNumberField("page_width", custom.getPageWidth());
                        jg.writeEndObject();
                    }
                    jg.writeEndArray();
                }
            jg.writeEndObject();
        }
        jg.writeEndArray();
    }

    @Override
    public void serializeWithType(JsonGenerator jg, SerializerProvider sp, TypeSerializer ts) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
