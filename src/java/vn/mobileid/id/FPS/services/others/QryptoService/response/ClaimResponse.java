/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.QryptoService.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClaimResponse extends Response{
    private String tan;

    public ClaimResponse(String tan) {
        this.tan = tan;
    }

    public ClaimResponse() {
    }

    @JsonProperty("tan")
    public String getTan() {
        return tan;
    }

    public void setTan(String tan) {
        this.tan = tan;
    }
    
    
}
