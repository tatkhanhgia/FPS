/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.object.policy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import java.util.List;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonRootName("attributes")
public class PolicySystemConfiguration  extends Attributes{
    
    private List<SystemConfiguration> attributes;
       
    public PolicySystemConfiguration() {
    }

    @JsonProperty("attributes")
    public List<SystemConfiguration> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<SystemConfiguration> attributes) {
        this.attributes = attributes;
    }
}
