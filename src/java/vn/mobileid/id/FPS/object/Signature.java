///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package vn.mobileid.id.FPS.object;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.fasterxml.jackson.annotation.JsonRootName;
//import java.text.ParseException;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import vn.mobileid.id.utils.Utils;
//
///**
// *
// * @author GiaTK
// */
//@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonRootName("verification")
//public class Signature {
//
//    //Temporal
//    private String fieldName;
//
//    //Official
//    private String signatureValue;
//    private String signatureId;
//    private String signingLocation;
//    private String signingReason;
//    private boolean imageEnabled;
//    private List<String> certificateChain;
//    private String signerName;
//    private byte[] signerPhoto;
//    private String signerContact;
//    private String signatureStatus;
//    private Date signingTime;
//    private boolean ltv;
//    private boolean qualified;
//    private boolean certified;
//    private String certifiedPermission;
//    private Date timestampAt;
//    private String timestampAuthority;
//    private String subjectDn;
//    private String issuerDn;
//    private Date certValidFrom;
//    private Date certValidTo;
//    private String signatureType;
//    private String signatureApplication;
//    private String signatureAlgorithm;
//    private String signedHash; //tên hash
//    private String hashData; //dữ liệu hash
//
//    //ISO
//    private String signingTime_ISO;
//    private String timestampAt_ISO;
//    private String certValidFrom_ISO;
//    private String certValidTo_ISO;
//
//    //Some detail
//    private HashMap<String, String> issuerDN;
//
//    public Signature() {
//    }
//
//    public void setSignatureValue(String signatureValue) {
//        this.signatureValue = signatureValue;
//    }
//
//    public void setSigningLocation(String signingLocation) {
//        this.signingLocation = signingLocation;
//    }
//
//    public void setSigningReason(String signingReason) {
//        this.signingReason = signingReason;
//    }
//
//    public void setImageEnabled(boolean imageEnabled) {
//        this.imageEnabled = imageEnabled;
//    }
//
//    public void setCertificateChain(List<String> certificateChain) {
//        this.certificateChain = certificateChain;
//    }
//
//    public void setSignerName(String signerName) {
//        this.signerName = signerName;
//    }
//
//    public void setSignerPhoto(byte[] signerPhoto) {
//        this.signerPhoto = signerPhoto;
//    }
//
//    public void setSignatureStatus(String signatureStatus) {
//        this.signatureStatus = signatureStatus;
//    }
//
//    public void setSigningTime(Date signingTime) {
//        try {
//            this.signingTime_ISO = Utils.convertToUTC_String(signingTime);
//        } catch (ParseException ex) {
//            Logger.getLogger(Signature.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        this.signingTime = signingTime;
//    }
//
//    public void setLtv(boolean ltv) {
//        this.ltv = ltv;
//    }
//
//    public void setQualified(boolean qualified) {
//        this.qualified = qualified;
//    }
//
//    public void setCertified(boolean certified) {
//        this.certified = certified;
//    }
//
//    public void setCertifiedPermission(String certifiedPermission) {
//        this.certifiedPermission = certifiedPermission;
//    }
//
//    public void setTimestampAt(Date timestampAt) {
//        try {
//            this.timestampAt_ISO = Utils.convertToUTC_String(timestampAt);
//        } catch (ParseException ex) {
//        }
//        this.timestampAt = timestampAt;
//    }
//
//    public void setTimestampAuthority(String timestampAuthority) {
//        this.timestampAuthority = timestampAuthority;
//    }
//
//    public void setSubjectDn(String subjectDn) {
//        this.subjectDn = subjectDn;
//    }
//
//    public void setIssuerDn(String issuerDn) {
//        this.issuerDn = issuerDn;
//    }
//
//    public void setCertValidFrom(Date certValidFrom) {
//        try {
//            this.certValidFrom_ISO = Utils.convertToUTC_String(certValidFrom);
//        } catch (ParseException ex) {
//            Logger.getLogger(Signature.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        this.certValidFrom = certValidFrom;
//    }
//
//    public void setCertValidTo(Date certValidTo) {
//        try {
//            this.certValidTo_ISO = Utils.convertToUTC_String(certValidTo);
//        } catch (ParseException ex) {
//            Logger.getLogger(Signature.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        this.certValidTo = certValidTo;
//    }
//
//    public void setSignatureType(String signatureType) {
//        this.signatureType = signatureType;
//    }
//
//    public void setSignatureApplication(String signatureApplication) {
//        this.signatureApplication = signatureApplication;
//    }
//
//    public void setSignatureAlgorithm(String signatureAlgorithm) {
//        this.signatureAlgorithm = signatureAlgorithm;
//    }
//
//    public void setSignedHash(String signedHash) {
//        this.signedHash = signedHash;
//    }
//
//    public void setFieldName(String fieldName) {
//        this.fieldName = fieldName;
//    }
//
//    public void setHashData(String hashData) {
//        this.hashData = hashData;
//    }
//
//    public void setSignatureId(String signatureId) {
//        this.signatureId = signatureId;
//    }
//
//    public void setSignerContact(String signerContact) {
//        this.signerContact = signerContact;
//    }
//
//    public void setIssuerDN(HashMap<String, String> issuerDN) {
//        this.issuerDN = issuerDN;
//    }
//
//    //==========================================================================
//    @JsonProperty("signature_value")
//    public String getSignatureValue() {
//        return signatureValue;
//    }
//
//    @JsonProperty("signing_location")
//    public String getSigningLocation() {
//        return signingLocation;
//    }
//
//    @JsonProperty("signing_reason")
//    public String getSigningReason() {
//        return signingReason;
//    }
//
//    @JsonProperty("image_enabled")
//    public boolean isImageEnabled() {
//        return imageEnabled;
//    }
//
//    @JsonProperty("certificate_chain")
//    public List<String> getCertificateChain() {
//        return certificateChain;
//    }
//
//    @JsonProperty("signer_name")
//    public String getSignerName() {
//        return signerName;
//    }
//
//    @JsonProperty("signer_photo")
//    public byte[] getSignerPhoto() {
//        return signerPhoto;
//    }
//
//    @JsonProperty("signature_status")
//    public String getSignatureStatus() {
//        return signatureStatus;
//    }
//
//    @JsonProperty("signing_time")
//    public Date getSigningTime() {
//        return signingTime;
//    }
//
//    @JsonProperty("ltv")
//    public boolean isLtv() {
//        return ltv;
//    }
//
//    @JsonProperty("qualified")
//    public boolean isQualified() {
//        return qualified;
//    }
//
//    @JsonProperty("certified")
//    public boolean isCertified() {
//        return certified;
//    }
//
//    @JsonProperty("certified_permission")
//    public String getCertifiedPermission() {
//        return certifiedPermission;
//    }
//
//    @JsonProperty("timestamp_at")
//    public Date getTimestampAt() {
//        return timestampAt;
//    }
//
//    @JsonProperty("timestamp_authority")
//    public String getTimestampAuthority() {
//        return timestampAuthority;
//    }
//
//    @JsonProperty("subject_dn")
//    public String getSubjectDn() {
//        return subjectDn;
//    }
//
//    @JsonProperty("issuer_dn")
//    public String getIssuerDn() {
//        return issuerDn;
//    }
//
//    @JsonProperty("cert_valid_from")
//    public Date getCertValidFrom() {
//        return certValidFrom;
//    }
//
//    @JsonProperty("cert_valid_to")
//    public Date getCertValidTo() {
//        return certValidTo;
//    }
//
//    @JsonProperty("signature_type")
//    public String getSignatureType() {
//        return signatureType;
//    }
//
//    @JsonProperty("signature_application")
//    public String getSignatureApplication() {
//        return signatureApplication;
//    }
//
//    @JsonProperty("signature_algorithm")
//    public String getSignatureAlgorithm() {
//        return signatureAlgorithm;
//    }
//
//    @JsonProperty("signed_hash")
//    public String getSignedHash() {
//        return signedHash;
//    }
//
//    @JsonProperty("field_name")
//    public String getFieldName() {
//        return fieldName;
//    }
//
//    @JsonProperty("hash_data")
//    public String getHashData() {
//        return hashData;
//    }
//
//    @JsonProperty("signature_name")
//    public String getSignatureId() {
//        return signatureId;
//    }
//
//    @JsonProperty("signer_contact")
//    public String getSignerContact() {
//        return signerContact;
//    }
//
//    @JsonProperty("signing_time_iso")
//    public String getSigningTime_ISO() {
//        return signingTime_ISO;
//    }
//
//    @JsonProperty("timestamp_at_iso")
//    public String getTimestampAt_ISO() {
//        return timestampAt_ISO;
//    }
//
//    @JsonProperty("cert_valid_from_iso")
//    public String getCertValidFrom_ISO() {
//        return certValidFrom_ISO;
//    }
//
//    @JsonProperty("cert_valid_to_iso")
//    public String getCertValidTo_ISO() {
//        return certValidTo_ISO;
//    }
//
//    @JsonProperty("issuer_dn_object")
//    public HashMap<String, String> getIssuerDN() {
//        return issuerDN;
//    }
//
//    @JsonIgnoreProperties(ignoreUnknown = true)
//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    public static class Certificate {
//
//        private HashMap<String, String> subjectDn;
//        private HashMap<String, String> issuerDn;
//        private String encoded;
//
//        public Certificate() {
//        }
//
//        @JsonProperty("subject_dn_object")
//        public HashMap<String, String> getSubjectDn() {
//            return subjectDn;
//        }
//
//        public void setSubjectDn(HashMap<String, String> subjectDn) {
//            this.subjectDn = subjectDn;
//        }
//
//        @JsonProperty("issuer_dn_object")
//        public HashMap<String, String> getIssuerDn() {
//            return issuerDn;
//        }
//
//        public void setIssuerDn(HashMap<String, String> issuerDn) {
//            this.issuerDn = issuerDn;
//        }
//
//        @JsonProperty("encoded")
//        public String getEncoded() {
//            return encoded;
//        }
//
//        public void setEncoded(String encoded) {
//            this.encoded = encoded;
//        }
//
//    }
//}
