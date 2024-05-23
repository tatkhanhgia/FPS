/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Date;
import java.util.List;
import fps_core.enumration.DocumentType;
import fps_core.enumration.DocumentStatus;
import fps_core.objects.CustomPageSize;
import vn.mobileid.id.FPS.services.MyServices;
/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Document {

    private long id;
    private long packageId;
    private String name;
    private long size;
    private DocumentStatus status;
    private String url;
    private DocumentType type;
    private String digest;
    private String content;
    private String uuid;
    private String dms;
    private int revision;
    private String createdBy;
    private String modifiedBy;
    private Date createdAt;
    private Date modifiedAt;
    private boolean enabled;
    private float documentHeight;
    private float documentWidth;
    private int documentPages;
    private List<CustomPageSize> documentCustomPageSize;

    public Document() {
    }

    @JsonProperty("document_id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPackageId() {
        return packageId;
    }

    public void setPackageId(long packageId) {
        this.packageId = packageId;
    }

    @JsonProperty("document_name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public DocumentStatus getStatus() {
        return status;
    }
    
    @JsonProperty("document_status")
    public int getStatusId() {
        if(status==null){
            status = DocumentStatus.READY;
        }
        return status.getTypeId();
    }

    public void setStatus(DocumentStatus status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public DocumentType getType() {
        return type;
    }

    public void setType(DocumentType type) {
        this.type = type;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDms() {
        return dms;
    }

    public void setDms(String dms) {
        this.dms = dms;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    @JsonProperty("uploaded_by")
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @JsonProperty("modified_by")
    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @JsonProperty("uploaded_on")
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("modified_on")
    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @JsonProperty("document_height")
    public float getDocumentHeight() {
        return documentHeight;
    }

    public void setDocumentHeight(float documentHeight) {
        this.documentHeight = documentHeight;
    }

    @JsonProperty("document_width")
    public float getDocumentWidth() {
        return documentWidth;
    }

    public void setDocumentWidth(float documentWidth) {
        this.documentWidth = documentWidth;
    }

    @JsonProperty("document_custom_page")
    public List<CustomPageSize> getDocumentCustomPageSize() {
        return documentCustomPageSize;
    }

    public void setDocumentCustomPageSize(List<CustomPageSize> documentCustomPageSize) {
        this.documentCustomPageSize = documentCustomPageSize;
    }

    @JsonProperty("document_pages")
    public int getDocumentPages() {
        return documentPages;
    }

    public void setDocumentPages(int documentPages) {
        this.documentPages = documentPages;
    }
    
    public static void main(String[] args) throws JsonProcessingException {
        String json = "{ \"document_custom_page\":[{\"page_start\":1,\"page_end\":2,\"page_rotate\":90,\"page_width\":100,\"page_height\":100}] }";
        Document document = MyServices.getJsonService().readValue(json, Document.class);
        List<CustomPageSize> cus = document.getDocumentCustomPageSize();
        String temp = "";
    }
}