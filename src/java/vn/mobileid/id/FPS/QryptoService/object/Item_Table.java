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
import java.util.List;
import vn.mobileid.id.FPS.services.MyServices;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Item_Table {
    private List<Row> rows;

    public Item_Table() {
    }

    @JsonProperty("value")
    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Row{
        private String column_1;
        private String column_2;
        private String column_3;
        private String text;

        public Row() {
        }

        @JsonProperty("text")
        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        @JsonProperty("column_1")
        public String getColumn_1() {
            return column_1;
        }

        public void setColumn_1(String column_1) {
            this.column_1 = column_1;
        }

        @JsonProperty("column_2")
        public String getColumn_2() {
            return column_2;
        }

        public void setColumn_2(String column_2) {
            this.column_2 = column_2;
        }

        @JsonProperty("column_3")
        public String getColumn_3() {
            return column_3;
        }

        public void setColumn_3(String column_3) {
            this.column_3 = column_3;
        }
    }
    
    public static void main(String[] args) throws JsonProcessingException {
        String json = "{\n" +
"            \"field\": \"Table\",\n" +
"            \"type\": 8,\n" +
"            \"value\": [\n" +
"                {\n" +
"                    \"columns\":[\n" +
"                        \"td1\",\n" +
"                        \"td2\",\n" +
"                        \"td3\"\n" +
"                    ],\n" +
"                    \"text\": \"mini text\"\n" +
"                }\n" +
"            ]\n" +
"        }";
        Item_Table table = MyServices.getJsonService().readValue(json, Item_Table.class);
//        System.out.println(table.getColumns().get(0).getText());
//        System.out.println(table.getColumns().get(0).getColumns()[0]);
//        System.out.println(table.getColumns().get(0).getColumns()[1]);
//        System.out.println(table.getColumns().get(0).getColumns()[2]);
    }
}
