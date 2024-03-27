/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.policy.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SystemConfiguration {
    private String dateFormat;
    private String qrExpiredTime;
    private String qrHost;
    private long maximumFile;

    public SystemConfiguration() {
    }

    @JsonProperty("dateFormat")
    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    @JsonProperty("qr_expired_time")
    public String getQrExpiredTime() {
        return qrExpiredTime;
    }

    public void setQrExpiredTime(String qrExpiredTime) {
        this.qrExpiredTime = qrExpiredTime;
    }

    @JsonProperty("qr_host")
    public String getQrHost() {
        return qrHost;
    }

    public void setQrHost(String qrHost) {
        this.qrHost = qrHost;
    }

    @JsonProperty("max_size_store")
    public long getMaximumFile() {
        return maximumFile;
    }

    public void setMaximumFile(long maximumFile) {
        this.maximumFile = maximumFile;
    }
    
    
}
