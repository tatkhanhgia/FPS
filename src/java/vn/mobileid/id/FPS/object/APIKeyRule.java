/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.object;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import static com.fasterxml.jackson.databind.node.JsonNodeType.BOOLEAN;
import static com.fasterxml.jackson.databind.node.JsonNodeType.STRING;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import vn.mobileid.id.FPS.enumeration.Rule;
import vn.mobileid.id.FPS.services.MyServices;

    @JsonInclude(JsonInclude.Include.NON_NULL)
@JsonRootName("attributes")
public class APIKeyRule {

    private List<Rule> attributes;
    
    @JsonCreator
    public APIKeyRule(
            @JsonProperty("attributes") JsonNode node) throws IOException {
        try {
            if (!node.isArray()) {
                node = node.get("attributes");
            }
            for (JsonNode element : node) {
                String fieldName = element.fieldNames().next();
                Rule rule = Rule.valueOf(fieldName.toUpperCase());
                JsonNodeType type = element.get(fieldName).getNodeType();
                    switch (type) {
                        case BOOLEAN:
                            rule.setData(element.get(fieldName).asBoolean());
                            break;
                        case STRING:
                            rule.setData(element.get(fieldName).asText());
                            break;
                        default:
                            throw new AssertionError();
                    }
                if (this.attributes == null) {
                    this.attributes = new ArrayList<>();
                }

                this.attributes.add(rule);
            }
        } catch (Exception ex) {
        }
    }

    public List<Rule> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Rule> attributes) {
        this.attributes = attributes;
    }

    public String toJson() throws JsonProcessingException {
        return MyServices.getJsonService().writeValueAsString(this);
    }

    public boolean isRuleEnabled(Rule ruleCheck) {
        try {
            for (Rule rule : this.attributes) {
                if (rule.checkSameRule(ruleCheck)) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    public Rule getRule(Rule ruleCheck){
        try {
            for (Rule rule : this.attributes) {
                if (rule.checkSameRule(ruleCheck)) {
                    return rule;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }
    
    public static void main(String[] args) throws IOException {
        String temp = "{\"attributes\":{\"attributes\":[{\"IS_CONVERT_DATE\":true,\"hello\":\"onetwo\"}]}}";

        APIKeyRule rule = MyServices.getJsonService()
//                .enable(DeserializationFeature.UNWRAP_ROOT_VALUE)
                .readValue(temp, APIKeyRule.class);

        System.out.println(rule.isRuleEnabled(Rule.IS_CONVERT_DATE));
    }
}
