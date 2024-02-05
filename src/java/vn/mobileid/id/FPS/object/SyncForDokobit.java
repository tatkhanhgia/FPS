/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import vn.mobileid.id.FPS.fieldAttribute.BasicFieldAttribute;
import vn.mobileid.id.FPS.fieldAttribute.SignatureFieldAttribute;

/**
 *
 * @author GiaTK
 */
//Using this class for defined all parameter in API Synchonized for Dokobit Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SyncForDokobit {
    private String uuid;
    private long document_id;
    private List<SignatureFieldAttribute> fields;

    public SyncForDokobit() {
    }

    @JsonProperty("uuid")
    public String getUuid() {
        return uuid;
    }

    @JsonProperty("document_id")
    public long getDocument_id() {
        return document_id;
    }

    @JsonProperty("fields")
    public List<SignatureFieldAttribute> getFields() {
        return fields;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setDocument_id(long document_id) {
        this.document_id = document_id;
    }

    public void setFields(List<SignatureFieldAttribute> fields) {
        this.fields = fields;
    }
    
    public static void main(String[] args) throws Exception{
        String payload = "{\"uuid\":\"A518260110D61763A102054E34BEAF6B\",\"document_id\":12,\"fields\":[{\"field_name\":\"field_one\",\"page\":3,\"dimension\":{\"x\":100,\"y\":50,\"width\":100,\"height\":50}},{\"field_name\":\"field_one\",\"page\":3,\"dimension\":{\"x\":100,\"y\":50,\"width\":100,\"height\":50}}]}";
        SyncForDokobit obj = new ObjectMapper().readValue(payload, SyncForDokobit.class);
        System.out.println(obj.getFields().get(0).getDimension().getX());
    }
}
