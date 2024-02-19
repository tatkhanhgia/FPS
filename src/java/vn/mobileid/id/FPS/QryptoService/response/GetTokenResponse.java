/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.QryptoService.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetTokenResponse extends Response{
    private String access_token;
    private String access_token_expired_in;
    private String refresh_token;
    private String refresh_token_expired_in;

    public GetTokenResponse() {
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getAccess_token_expired_in() {
        return access_token_expired_in;
    }

    public void setAccess_token_expired_in(String access_token_expired_in) {
        this.access_token_expired_in = access_token_expired_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getRefresh_token_expired_in() {
        return refresh_token_expired_in;
    }

    public void setRefresh_token_expired_in(String refresh_token_expired_in) {
        this.refresh_token_expired_in = refresh_token_expired_in;
    }
    
    
}
