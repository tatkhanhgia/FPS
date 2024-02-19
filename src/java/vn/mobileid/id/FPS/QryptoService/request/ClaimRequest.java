/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.QryptoService.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author GIATK    
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClaimRequest {
    private String sigAlg;
    private String signature;
    private String DGCI;
    private String certHash;
    private String tanHash;
    private PublicKeyData publicKeyData;

    public ClaimRequest() {
    }

    public ClaimRequest(String sigAlg, String signature, String DGCI, String certHash, String tanHash, PublicKeyData publicKeyData) {
        this.sigAlg = sigAlg;
        this.signature = signature;
        this.DGCI = DGCI;
        this.certHash = certHash;
        this.tanHash = tanHash;
        this.publicKeyData = publicKeyData;
    }

    @JsonProperty("sigAlg")
    public String getSigAlg() {
        return sigAlg;
    }

    public void setSigAlg(String sigAlg) {
        this.sigAlg = sigAlg;
    }

    @JsonProperty("signature")
    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @JsonProperty("DGCI")
    public String getDGCI() {
        return DGCI;
    }

    public void setDGCI(String DGCI) {
        this.DGCI = DGCI;
    }

    @JsonProperty("certhash")
    public String getCertHash() {
        return certHash;
    }

    public void setCertHash(String certHash) {
        this.certHash = certHash;
    }

    @JsonProperty("TANHash")
    public String getTanHash() {
        return tanHash;
    }

    public void setTanHash(String tanHash) {
        this.tanHash = tanHash;
    }

    @JsonProperty("publicKey")
    public PublicKeyData getPublicKeyData() {
        return publicKeyData;
    }

    public void setPublicKeyData(PublicKeyData publicKeyData) {
        this.publicKeyData = publicKeyData;
    }        
}
