/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fps_core.objects.core.BasicFieldAttribute;
import fps_core.objects.core.Signature;
import java.util.List;
import vn.mobileid.id.FPS.services.others.qryptoService.object.ItemDetails;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QryptoFieldAttribute extends BasicFieldAttribute {

    private String qryptoBase45;
    private String value;
    private String imageQR;
    private boolean isTransparent;
    
    private List<ItemDetails> items;
    
    private Signature verification;

    public QryptoFieldAttribute() {
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    @JsonProperty("is_transparent")
    public boolean IsTransparent() {
        return isTransparent;
    }

    @JsonProperty("image_qr")
    public String getImageQR() {
        return imageQR;
    }

    @JsonProperty("qrypto_base45")
    public String getQryptoBase45() {
        return qryptoBase45;
    }
    
    @JsonProperty("items")
    public List<ItemDetails> getItems() {
        return items;
    }

    @JsonProperty("verification")
    public Signature getVerification() {
        return verification;
    }

    public void setVerification(Signature verification) {
        this.verification = verification;
    }

    public void setItems(List<ItemDetails> items) {
        this.items = items;
    }
    
    public void setValue(String value) {
        this.value = value;
    }

    public void setTransparent(boolean isTransparent) {
        this.isTransparent = isTransparent;
    }

    public void setImageQR(String imageQR) {
        this.imageQR = imageQR;
    }

    public void setQryptoBase45(String qryptoBase45) {
        this.qryptoBase45 = qryptoBase45;
    }
}
