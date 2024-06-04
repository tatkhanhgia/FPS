/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.QryptoService.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonRootName("hash_values")
public class FileDataDetails {
//        
//    @JsonIgnore
//    private FileType file_type;
    
    private int file_type;
    private String file_field;
    private Object value;
    private String hash_value;

    public FileDataDetails() {
    }

    @JsonProperty("file_field")
    public String getFile_field() {
        return file_field;
    }

    public void setFile_field(String file_field) {
        this.file_field = file_field;
    }
        
    @JsonProperty("file_type")
    public int getFile_type() {
        return file_type;
    }

    @JsonProperty("value")
    public Object getValue() {
        return value;
    }

    @JsonProperty("hash_value")
    public String getHash_value() {
        return hash_value;
    }

    public void setHash_value(String hash_value) {
        this.hash_value = hash_value;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }

    public void setFile_type(int type) {
        this.file_type = type;
    }
    
    public enum FileType{
        FingerPrint(1),
        IdentityCard(2),
        Photo(3),
        PDF(4),
        Custom(5);
        
        private int value;
        
        FileType(int i){
            this.value = i;
        };
        
        public int getValue(){
            return value;
        }
    }
}

