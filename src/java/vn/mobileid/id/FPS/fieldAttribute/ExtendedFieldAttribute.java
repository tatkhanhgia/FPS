/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.fieldAttribute;

import java.util.Date;
import java.util.List;
import vn.mobileid.id.FPS.enumration.ProcessStatus;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK Là class lưu trữ dạng super class bao gồm các giá trị để
 * mapping dưới DB
 */
public class ExtendedFieldAttribute extends BasicFieldAttribute {

    private long documentFieldId;
    private String fieldValue;
    private String detailValue;
    private long detailType;
    private String createdBy;
    private Date createdAt;
    private String modifiedBy;
    private Date modifiedAt;
    private String hash;

    //Internal Data for Signature Field
    private List<String> levelOfAssurance;

    public ExtendedFieldAttribute() {
    }

    public long getDocumentFieldId() {
        return documentFieldId;
    }

    public void setDocumentFieldId(long documentFieldId) {
        this.documentFieldId = documentFieldId;
    }

    public String getDetailValue() {
        return detailValue;
    }

    public void setDetailValue(String detailValue) {
        this.detailValue = detailValue;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public long getDetailType() {
        return detailType;
    }

    public void setDetailType(long detailType) {
        this.detailType = detailType;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public List<String> getLevelOfAssurance() {
        return levelOfAssurance;
    }

    public void setLevelOfAssurance(List<String> levelOfAssurance) {
        this.levelOfAssurance = levelOfAssurance;
    }

    public Object clone(BasicFieldAttribute source, Dimension dimension) {
        source.setDimension(dimension);
        source.setFieldName(this.getFieldName());
        source.setPage(this.getPage());
        source.setVisibleEnabled(this.getVisibleEnabled());
        source.setType(this.getType());
        source.setSuffix(this.getSuffix());
        source.setProcessBy(this.getProcessBy());
        source.setProcessOn(this.getProcessOn());
        source.setRotate(this.getRotate());
        source.setProcessStatus(
                Utils.isNullOrEmpty(this.getProcessStatus())?
                        Utils.isNullOrEmpty(source.getProcessStatus())?
                                ProcessStatus.UN_PROCESSED.getName():
                                source.getProcessStatus():
                        this.getProcessStatus());
        
        return source;
    }
}
