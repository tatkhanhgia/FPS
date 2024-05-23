/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.QryptoService.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import vn.mobileid.id.FPS.services.MyServices;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Item_Choice extends Object{

    private List<Element> elements;

    public Item_Choice() {
    }

    @JsonProperty("value")
    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Element {

        private String elementName;
        private String id;
        private boolean choice;

        public Element() {
        }

        @JsonProperty("element")
        public String getElementName() {
            return elementName;
        }

        public void setElementName(String elementName) {
            this.elementName = elementName;
        }

        @JsonProperty("choice")
        public boolean isChoice() {
            return choice;
        }

        public void setChoice(boolean choice) {
            this.choice = choice;
        }

        @JsonProperty("id")
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
        
        
    }

    public static void main(String[] args) throws JsonProcessingException {
        String json = "{\n"
                + "            \"field\": \"Choice\",\n"
                + "            \"type\": 7,\n"
                + "            \"value\": [\n"
                + "                {\n"
                + "                    \"element\": \"Nhan vien\",\n"
                + "                    \"choice\": false\n"
                + "                },{\n"
                + "                    \"element\": \"Truong Phong\",\n"
                + "                    \"choice\": true\n"
                + "                },{\n"
                + "                    \"element\": \"Giam Doc\",\n"
                + "                    \"choice\": false\n"
                + "                }\n"
                + "            ]\n"
                + "        }";

        Item_Choice choices = MyServices.getJsonService().readValue(json, Item_Choice.class);
        for (Element element : choices.getElements()) {
            System.out.println(element.getElementName());
            System.out.println(element.isChoice());
        }
    }
}
