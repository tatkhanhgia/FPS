/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.FPS.object;

import java.util.HashMap;
import vn.mobileid.id.FPS.controller.A_FPSConstant;

/**
 *
 * @author ADMIN
 */
public class InternalResponse {

    private int status;
    private String message;
    private String transactionId;
    
    //Error Code and Error Code Description
    private int code;
    private int codeDescription;

    //Data User
    private User user;
    
    //Enterprise
    private Enterprise ent;
    
    //Data backend    
    private Object data;
    private HashMap<String, Object> headers;
    
    //List of data that the internal would like to store and communicate between class/package/function
    private InternalData internalData;
    
    //Exception
    private Exception exception;
    
    //Store Hierarchical Log
    private fps_core.utils.LogHandler.HierarchicalLog hierarchicalLog;
     
    public InternalResponse(int status, String message) {
        this.status = status;
        this.message = message;        
    }
    
    public InternalResponse(int status, int code, int codeDes){
        this.status = status;
        this.code = code;
        this.codeDescription = codeDes;       
    }
    
    public InternalResponse(int status, Object data) {
        this.status = status;
        this.data = data;
    }       

    public InternalResponse() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    public boolean isValid(){
        return this.status == A_FPSConstant.HTTP_CODE_SUCCESS;
    }

    public String getMessage() {
        return message;
    }

    public InternalResponse setMessage(String message) {
        this.message = message;
        return this;
    }     
 
    /**
     * Get data Object from Response
     * @return 
     */
    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public HashMap<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, Object> headers) {
        this.headers = headers;
    }   

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public User getUser() {
        return user;
    }

    public InternalResponse setUser(User user) {
        this.user = user;
        return this;
    }

    public Enterprise getEnt() {
        return ent;
    }

    public InternalResponse setEnt(Enterprise ent) {
        this.ent = ent;
        return this;
    }

    public InternalData getInternalData() {
        return internalData;
    }

    public InternalResponse setInternalData(InternalData internalData) {
        this.internalData = internalData;
        return this;
    }

    public Exception getException() {
        return exception;
    }

    public InternalResponse setException(Exception exception) {
        this.exception = exception;
        return this;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCodeDescription() {
        return codeDescription;
    }

    public void setCodeDescription(int codeDescription) {
        this.codeDescription = codeDescription;
    }
    
    public static class InternalData{
        private String name;
        private Object value;

        public InternalData() {
        }

        public String getName() {
            return name;
        }

        public InternalData setName(String name) {
            this.name = name;
            return this;
        }

        public Object getValue() {
            return value;
        }

        public InternalData setValue(Object value) {
            this.value = value;
            return this;
        }
    }

    public InternalResponse setHierarchicalLog(fps_core.utils.LogHandler.HierarchicalLog log){
        this.hierarchicalLog = log;
        return this;
    }
    
    public fps_core.utils.LogHandler.HierarchicalLog getHierarchicalLog(){
        return this.hierarchicalLog;
    }
}
