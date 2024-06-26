/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.QryptoService.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import vn.mobileid.id.FPS.services.MyServices;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Item_IDPicture4Label {
    private IDPicture4Label idPicture;

    public Item_IDPicture4Label() {
    }

    @JsonProperty("value")
    public IDPicture4Label getIdPicture() {
        return idPicture;
    }

    public void setIdPicture(IDPicture4Label idPicture) {
        this.idPicture = idPicture;
    }
    
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class IDPicture4Label{
        private String label1; 
        private String label2;
        private String label3;
        private String label4;
        private String base64;
        public IDPicture4Label() {
        }

        @JsonProperty("label_1")
        public String getLabel1() {
            return label1;
        }

        public void setLabel1(String label1) {
            this.label1 = label1;
        }

        @JsonProperty("label_2")
        public String getLabel2() {
            return label2;
        }

        public void setLabel2(String label2) {
            this.label2 = label2;
        }

        @JsonProperty("label_3")
        public String getLabel3() {
            return label3;
        }

        public void setLabel3(String label3) {
            this.label3 = label3;
        }

        @JsonProperty("label_4")
        public String getLabel4() {
            return label4;
        }

        public void setLabel4(String label4) {
            this.label4 = label4;
        }

        @JsonProperty("file_data")
        public String getBase64() {
            return base64;
        }

        public void setBase64(String base64) {
            this.base64 = base64;
        }
    }
    
    public static void main(String[] args) throws JsonProcessingException {
        String temp = "{\"field\":\"Photo\",\"mandatory_enable\":false,\"type\":11,\"value\":{\"label_1\":\"one\",\"label_2\":\"two\",\"label_3\":\"three\",\"label_4\":\"four\",\"file_data\":\"{Base64_of_file}\"},\"file_field\":\"fileA\",\"file_format\":\"image/png\",\"file_name\":\"hello.png\"}";
        Item_IDPicture4Label tempp = MyServices.getJsonService().readValue(temp, Item_IDPicture4Label.class);
        
        System.out.println(tempp.getIdPicture().getLabel1());
        System.out.println(tempp.getIdPicture().getLabel2());
        System.out.println(tempp.getIdPicture().getLabel3());
        System.out.println(tempp.getIdPicture().getLabel4());
        System.out.println(tempp.getIdPicture().getBase64());
    }
}
