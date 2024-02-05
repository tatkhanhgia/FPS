/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.object;

import java.util.Date;

/**
 *
 * @author GiaTK
 */

public class Enterprise extends Object{
    private int id;   //Id of enterprise
    private String name; //name of enterprise        
    private long ownerId;
    private String mobileNumber;
    private int status;
    private String domain;
    private String subdomain;    
    private String clientID;
    private String clientSecret;
    private String emailNotification;        
    private String createdBy;
    private Date createdAt;
    private String modifiedBy;
    private Date modifiedAt;
    
    private long idOfClientID;
    
    public Enterprise() {
    }           
        
    public Enterprise build(){
        return this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getEmailNotification() {
        return emailNotification;
    }

    public void setEmailNotification(String email_notification) {
        this.emailNotification = email_notification;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long owner_id) {
        this.ownerId = owner_id;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public long getIdOfClientID() {
        return idOfClientID;
    }

    public void setIdOfClientID(long idOfClientID) {
        this.idOfClientID = idOfClientID;
    }
    
    
}
