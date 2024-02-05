/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.email;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author VUDP
 */
public class EmailResp implements Serializable {
    private int responseCode;
    private String responseMessage;
    private String billCode;
    private Date timestamp;
    private long logInstance;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public long getLogInstance() {
        return logInstance;
    }

    public void setLogInstance(long logInstance) {
        this.logInstance = logInstance;
    }
    
    
}
