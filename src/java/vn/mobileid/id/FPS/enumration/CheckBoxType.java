/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.enumration;

/**
 *
 * @author GiaTK
 */
public enum CheckBoxType {
    CHECK(com.itextpdf.forms.fields.properties.CheckBoxType.CHECK, "check", "Check", "CHECK"),
    CIRCLE(com.itextpdf.forms.fields.properties.CheckBoxType.CIRCLE, "circle","Circle","CIRCLE"), 
    CROSS(com.itextpdf.forms.fields.properties.CheckBoxType.CROSS, "cross", "Cross","CROSS"), 
    DIAMOND(com.itextpdf.forms.fields.properties.CheckBoxType.DIAMOND, "diamond", "Diamond", "DIAMOND"), 
    SQUARE(com.itextpdf.forms.fields.properties.CheckBoxType.SQUARE, "square", "Square", "SQUARE"), 
    STAR(com.itextpdf.forms.fields.properties.CheckBoxType.STAR, "star", "Star", "STAR");
    
    private com.itextpdf.forms.fields.properties.CheckBoxType type1;
    private String[] names;

    private CheckBoxType(com.itextpdf.forms.fields.properties.CheckBoxType type1, String... names) {
        this.type1 = type1;
        this.names = names;
    }

    public com.itextpdf.forms.fields.properties.CheckBoxType getType1() {
        return type1;
    }

    public String[] getNames() {
        return names;
    }
    
    public static com.itextpdf.forms.fields.properties.CheckBoxType getCheckBoxType(String input){
        for(CheckBoxType type : values()){
            for(String name : type.getNames()){
                if(name.equals(input)){
                    return type.getType1();
                }
            }
        }
        return com.itextpdf.forms.fields.properties.CheckBoxType.CHECK;
    }
}
