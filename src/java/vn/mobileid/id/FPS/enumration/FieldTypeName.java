/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.enumration;

/**
 *
 * @author GiaTK
 */
public enum FieldTypeName {
    TEXTBOX("TEXTBOX"),
    CHECKBOX("CHECKBOX"),
    RADIOBOX("RADIOBOX"),
    QR("QR"),
    INITIAL("INITIAL"),
    INPERSON("INPERSON"),
    SIGNATURE("SIGNATURE"),
    DROPDOWN("DROPDOWN"),
    LISTBOX("LISTBOX"),
    STAMP("STAMP"),
    COMBOBOX("COMBOBOX"),
    TOGGLE("TOGGLE"),
    NUMERIC_STEP("NUMERIC_STEPPER");

    private String parentName;

    private FieldTypeName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentName() {
        return parentName;
    }

}
