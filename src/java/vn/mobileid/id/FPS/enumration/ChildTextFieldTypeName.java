/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.enumration;

/**
 *
 * @author GiaTK
 */
public enum ChildTextFieldTypeName {
    TEXTBOX(1),
    NAME(10),
    EMAIL(11),
    JOB_TITLE(12),
    COMPANY(13),
    DATE(14),
    TEXT_FIELD(15),
    TEXT_AREA(16),
    NUMBER(20),
    MULTILINE(21),
    DATE_TIME(24),
    LABEL(26),
    HYPERLINK(27);
    
    private int id;

    private ChildTextFieldTypeName(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
