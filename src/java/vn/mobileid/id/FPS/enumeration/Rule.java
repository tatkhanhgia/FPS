/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author GiaTK
 * Using for auto generate code when the rule is enabled (get rule from DB)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Rule {
    IS_CONVERT_DATE(false), //Convert Date with Format Date in DATE_DATETIME field
    QRYPTO_DATE_FORMAT("temp"),
    AUTO_LOCK_SIGNATURE(true);

    private Object dataRule;

    private Rule(Object dataRule) {
        this.dataRule = dataRule;
    }

    public Object getData() {
        return dataRule;
    }

    public Rule setData(Object dataRule) {
        this.dataRule = dataRule;
        return this;
    }

    @JsonValue
    public Map<String, Object> toValue() {
        Map<String, Object> value = new HashMap<>();
        value.put(this.name(), getData());
        return value;
    }

    @JsonCreator
    public static Rule forValue(
            String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(json);
            String fieldName = node.fieldNames().next();
            while (fieldName != null) {
                try {
                    JsonNodeType type = node.get(fieldName).getNodeType();
                    switch (type) {
                        case BOOLEAN:
                            return Rule.valueOf(fieldName.toUpperCase()).setData(node.get(fieldName).asBoolean());
                        case STRING:
                            return Rule.valueOf(fieldName.toUpperCase()).setData(node.get(fieldName).asText());
                        default:
                            throw new AssertionError();
                    }
                } catch (Exception ex) {
                }
                fieldName = node.fieldNames().next();
            }

        } catch (Exception ex) {
            return null;
        }
        return null;
    }

    public boolean checkSameRule(Rule ruleCheck) {
        return this.name().equalsIgnoreCase(ruleCheck.name());
    }
}
