package vn.mobileid.id.FPS.QryptoService.object;

import java.util.Base64;

public class Property {

    private String profile = "rssp-119.432-v2.0";
    private String baseUrl;
    private String authorization;

    public Property(String baseUrl, String authorization) {
        this.baseUrl = baseUrl;
        this.authorization = authorization;
    }    
         
    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    

    public String getAuthorization() throws Exception, Throwable {        
        return "Basic "+this.authorization;
    }          
}
