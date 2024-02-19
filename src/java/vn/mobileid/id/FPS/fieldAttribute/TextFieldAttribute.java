///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package vn.mobileid.id.FPS.fieldAttribute;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
///**
// *
// * @author GiaTK
// */
//@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
//public class TextFieldAttribute extends BasicFieldAttribute {
//
//    private String value;
//    private boolean readOnly;
//    private boolean multiline;
//    private int maxLength;
//    private String format; //For date
//    private String color;
//    private Font font;
//    private Align align;
//    private FormatType formatType;
//
//    private String suffix;
//
//    public enum FormatType {
//        ALPHANUMERIC("ALPHANUMERIC"),
//        NUMBER("NUMBER"),
//        TEXT("TEXT"),
//        EMAIL("EMAIL"),
//        TIME("TIME"),
//        DATE("DATE");
//
//        private String formatType;
//
//        private FormatType(String formatType) {
//            this.formatType = formatType;
//        }
//
//        public String getFormatType() {
//            return formatType;
//        }
//    }
//
//    public enum Align {
//        RIGHT("RIGHT"),
//        LEFT("LEFT"),
//        CENTER("CENTER");
//        private String name;
//
//        private Align(String name) {
//            this.name = name;
//        }
//
//        public String getName() {
//            return name;
//        }
//    }
//
//    public TextFieldAttribute() {
//    }
//
//    public void setValue(String value) {
//        this.value = value;
//    }
//
//    public void setReadOnly(boolean readOnly) {
//        this.readOnly = readOnly;
//    }
//
//    public void setMultiline(boolean multiline) {
//        this.multiline = multiline;
//    }
//
//    public void setMaxLength(int maxLength) {
//        this.maxLength = maxLength;
//    }
//
//    public void setFormat(String format) {
//        this.format = format;
//    }
//
//    public void setColor(String color) {
//        this.color = color;
//    }
//
//    public void setAlign(Align align) {
//        this.align = align;
//    }
//
//    public void setFormatType(FormatType formatType) {
//        this.formatType = formatType;
//    }
//
//    public void setFormatType(String formatType) {
//        this.formatType = FormatType.valueOf(formatType);
//    }
//
//    public void setFont(Font font) {
//        this.font = font;
//    }
//    
//    //==========================================================================
//    @JsonProperty("value")
//    public String getValue() {
//        return value;
//    }
//
//    @JsonProperty("read_only")
//    public boolean isReadOnly() {
//        return readOnly;
//    }
//
//    @JsonProperty("multiline")
//    public boolean isMultiline() {
//        return multiline;
//    }
//
//    @JsonProperty("max_length")
//    public int getMaxLength() {
//        return maxLength;
//    }
//
//    @JsonProperty("format")
//    public String getFormat() {
//        return format;
//    }
//
//    @JsonProperty("color")
//    public String getColor() {
//        return color;
//    }
//
//    @JsonProperty("align")
//    public Align getAlign() {
//        return align;
//    }
//
//    @JsonProperty("format_type")
//    public FormatType getFormatType() {
//        return formatType;
//    }
//
//    @JsonProperty("font")
//    public Font getFont() {
//        return font;
//    }
//
//    @JsonIgnoreProperties(ignoreUnknown = true)
//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    public static class Font {
//
//        private String name;
//        private int size;
//        private int embeddedSize;
//
//        public Font() {
//        }
//
//        @JsonProperty("name")
//        public String getName() {
//            return name;
//        }
//
//        @JsonProperty("size")
//        public int getSize() {
//            return size;
//        }
//
//        @JsonProperty("embedded_size")
//        public int getEmbeddedSize() {
//            return embeddedSize;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public void setSize(int size) {
//            this.size = size;
//        }
//
//        public void setEmbeddedSize(int embeddedSize) {
//            this.embeddedSize = embeddedSize;
//        }
//    }
//
//    public static void main(String[] arhs) throws JsonProcessingException {
//        String data = "{\"field_name\":\"text box 1\",\"page\":1,\"type\":\"TEXT\",\"value\":\"String\",\"placeholder\":\"String\",\"max_length\":0,\"format\":\"String\",\"format_type\":\"NUMBER\",\"multiline\":true,\"font\":{\"name\":\"Arial\",\"size\":12,\"embedded_size\":\"String\"},\"dimension\":{\"x\":0,\"y\":0,\"width\":100,\"height\":100}}";
//        TextFieldAttribute a = new ObjectMapper().readValue(data, TextFieldAttribute.class);
//        System.out.println("Align:" + a.getAlign().getName());
//        System.out.println("Type:" + a.getType());
//        String as = "as";
//    }
//}
