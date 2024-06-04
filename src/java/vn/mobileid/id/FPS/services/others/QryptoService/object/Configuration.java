/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.QryptoService.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Configuration {
    private String contextIdentifier;
    private int qryptoDimension;
    private boolean isTransparent;
    private qryptoEffectiveDate qryptoEffectiveDate;
    private boolean getFileTokenList;

    public Configuration() {
    }

    @JsonProperty("contextIdentifier")
    public String getContextIdentifier() {
        return contextIdentifier;
    }

    public void setContextIdentifier(String contextIdentifier) {
        this.contextIdentifier = contextIdentifier;
    }

    @JsonProperty("qryptoDimension")
    public int getQryptoDimension() {
        return qryptoDimension;
    }

    public void setQryptoDimension(int qryptoHeight) {
        this.qryptoDimension = qryptoHeight;
    }

    @JsonProperty("isTransparent")
    public boolean IsTransparent() {
        return isTransparent;
    }

    public void setIsTransparent(boolean isTransparent) {
        this.isTransparent = isTransparent;
    }

    @JsonProperty("qryptoEffectiveDate")
    public qryptoEffectiveDate getQryptoEffectiveDate() {
        return qryptoEffectiveDate;
    }

    public void setQryptoEffectiveDate(qryptoEffectiveDate qryptoEffectiveDate) {
        this.qryptoEffectiveDate = qryptoEffectiveDate;
    }

    @JsonProperty("getFileTokenList")
    public boolean isGetFileTokenList() {
        return getFileTokenList;
    }

    public void setGetFileTokenList(boolean getFileTokenList) {
        this.getFileTokenList = getFileTokenList;
    }
}
