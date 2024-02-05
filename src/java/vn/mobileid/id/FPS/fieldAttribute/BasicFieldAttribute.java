/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.fieldAttribute;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasicFieldAttribute{

    private String fieldName;
    private int page;
    private FieldType type;
    private String embedded;
    private String processStatus;
    private String processOn;
    private String processBy;
    private Dimension dimension;
    private boolean visibleEnabled;
    private boolean required;
    private int rotate;

    //Internal
    private String renamedAs;

    //For Signature
    private String handSignatureImage;
    
    //For Gateway
    private int suffix;
    
    public BasicFieldAttribute() {
    }

    @JsonProperty("field_name")
    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @JsonProperty("page")
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @JsonProperty("embedded")
    public String getEmbedded() {
        return embedded;
    }

    public void setEmbedded(String embedded) {
        this.embedded = embedded;
    }

    @JsonProperty("process_status")
    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    @JsonProperty("processed_on")
    public String getProcessOn() {
        return processOn;
    }

    public void setProcessOn(String processOn) {
        this.processOn = processOn;
    }

    @JsonProperty("processed_by")
    public String getProcessBy() {
        return processBy;
    }

    public void setProcessBy(String processBy) {
        this.processBy = processBy;
    }

    @JsonProperty("dimension")
    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    @JsonProperty("visible_enabled")
    public Boolean getVisibleEnabled() {
        return visibleEnabled;
    }

    public void setVisibleEnabled(Boolean visibleEnabled) {
        this.visibleEnabled = visibleEnabled;
    }

    @JsonProperty("required")
    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    @JsonProperty("type")
    public String getTypeName() {
        if (type == null) {
            return null;
        }
        return type.getTypeName();
    }

    public void setTypeName(String type) {
        this.type = new FieldType();
        this.type.setTypeName(type);
    }

    @JsonProperty("renamed_as")
    public String getRenamedAs() {
        return renamedAs;
    }

    public void setRenamedAs(String renamedAs) {
        this.renamedAs = renamedAs;
    }

    @JsonProperty("hand_signature_image")
    public String getHandSignatureImage() {
        return handSignatureImage;
    }

    public void setHandSignatureImage(String handSignatureImage) {
        this.handSignatureImage = handSignatureImage;
    }

    @JsonProperty("suffix")
    public int getSuffix() {
        return suffix;
    }

    public void setSuffix(int suffix) {
        this.suffix = suffix;
    }

    @JsonProperty("rotate")
    public int getRotate() {
        return rotate;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
    }
    
    public static void main(String[] args) throws JsonProcessingException {
        String json = "{\"field_name\":\"textbox1\",\"page\":1,\"type\":\"QR\",\"dimension\":{\"x\":100,\"y\":100,\"width\":200,\"height\":150},\"visible_enabled\":false}";
        BasicFieldAttribute attribute = new ObjectMapper().readValue(json, BasicFieldAttribute.class);
        String temp = "";
    }
}
