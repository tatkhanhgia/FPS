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
public class qryptoEffectiveDate {
    private String notValidBefore;
    private String notValidAfter;

    public qryptoEffectiveDate() {
    }

    public qryptoEffectiveDate(String notValidBefore, String notValidAfter) {
        this.notValidBefore = notValidBefore;
        this.notValidAfter = notValidAfter;
    }
    
    

    @JsonProperty("notValidBefore")
    public String getNotValidBefore() {
        return notValidBefore;
    }

    public void setNotValidBefore(String notValidBefore) {
        this.notValidBefore = notValidBefore;
    }

    @JsonProperty("notValidAfter")
    public String getNotValidAfter() {
        return notValidAfter;
    }

    public void setNotValidAfter(String notValidAfter) {
        this.notValidAfter = notValidAfter;
    }
    
    
}
