/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.enumration;

/**
 *
 * @author GiaTK
 */
public enum ProcessStatus {
    PROCESSED("PROCESSED"),
    UN_PROCESSED("UN_PROCESSED"),
    IN_PROCESS("IN_PROCESS"),
    FAIL("FAIL");
    
    private String name;

    private ProcessStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}