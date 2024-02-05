/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessingRequest {

    //Data for sign
    private String fieldName;
    private String signingReason;
    private String signingLocation;
    private boolean skipVerification;
    private String signatureValue;
    private String hashValue;
    private List<String> certificateChain;
    private String signatureAlgorithm;
    private String signedHash;  
    private String handSignatureImage;

    //Data for fillFormField
    private List<ProcessingFormFillRequest> text = new ArrayList<>();
    private List<ProcessingFormFillRequest> radio=new ArrayList<>();;
    private List<ProcessingFormFillRequest> checkbox=new ArrayList<>();;
    private List<ProcessingFormFillRequest> dropdown=new ArrayList<>();;
    private List<ProcessingFormFillRequest> listbox=new ArrayList<>();;
    
    public ProcessingRequest() {
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setSigningReason(String signingReason) {
        this.signingReason = signingReason;
    }

    public void setSigningLocation(String signingLocation) {
        this.signingLocation = signingLocation;
    }

    public void setSkipVerification(boolean skipVerification) {
        this.skipVerification = skipVerification;
    }

    public void setSignatureValue(String signatureValue) {
        this.signatureValue = signatureValue;
    }

    public void setCertificateChain(List<String> certificateChain) {
        this.certificateChain = certificateChain;
    }

    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

    public void setSignedHash(String signedHash) {
        this.signedHash = signedHash;
    }

    public void setText(List<ProcessingFormFillRequest> text) {
        this.text = text;
    }

    public void setRadio(List<ProcessingFormFillRequest> radio) {
        this.radio = radio;
    }

    public void setCheckbox(List<ProcessingFormFillRequest> checkbox) {
        this.checkbox = checkbox;
    }

    public void setDropdown(List<ProcessingFormFillRequest> dropdown) {
        this.dropdown = dropdown;
    }

    public void setListbox(List<ProcessingFormFillRequest> listbox) {
        this.listbox = listbox;
    }

    public void setHandSignatureImage(String handSignatureImage) {
        this.handSignatureImage = handSignatureImage;
    }
    
    @JsonProperty("field_name")
    public String getFieldName() {
        return fieldName;
    }

    @JsonProperty("signing_reason")
    public String getSigningReason() {
        return signingReason;
    }

    @JsonProperty("signing_location")
    public String getSigningLocation() {
        return signingLocation;
    }

    @JsonProperty("skip_verification")
    public boolean isSkipVerification() {
        return skipVerification;
    }

    @JsonProperty("signature_value")
    public String getSignatureValue() {
        return signatureValue;
    }

    @JsonProperty("certificate_chain")
    public List<String> getCertificateChain() {
        return certificateChain;
    }

    @JsonProperty("signature_algorithm")
    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    @JsonProperty("signed_hash")
    public String getSignedHash() {
        return signedHash;
    }
    
    @JsonProperty("hash_value")
    public String getHashValue() {
        return hashValue;
    }

    public void setHashValue(String hashValue) {
        this.hashValue = hashValue;
    }
    
    

    @JsonProperty("text")
    public List<ProcessingFormFillRequest> getText() {
        return text;
    }

    @JsonProperty("radio")
    public List<ProcessingFormFillRequest> getRadio() {
        return radio;
    }

    @JsonProperty("checkbox")
    public List<ProcessingFormFillRequest> getCheckbox() {
        return checkbox;
    }

    @JsonProperty("dropdown")
    public List<ProcessingFormFillRequest> getDropdown() {
        return dropdown;
    }

    @JsonProperty("listbox")
    public List<ProcessingFormFillRequest> getListbox() {
        return listbox;
    }

    @JsonProperty("hand_signature_image")
    public String getHandSignatureImage() {
        return handSignatureImage;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ProcessingFormFillRequest {

        private String fieldName;
        private Object value;
        private String radioGroupName;

        public ProcessingFormFillRequest() {
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public void setRadioGroupName(String radioGroupName) {
            this.radioGroupName = radioGroupName;
        }

        @JsonProperty("field_name")
        public String getFieldName() {
            return fieldName;
        }

        @JsonProperty("value")
        public Object getValue() {
            return value;
        }

        @JsonProperty("radio_group_name")
        public String getRadioGroupName() {
            return radioGroupName;
        }

    }
}
