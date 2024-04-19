/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.enumeration;

/**
 *
 * @author GiaTK
 */
public enum EnterpriseRule {
    IS_CONVERT_DATE("IS_CONVERT_DATE",1); // Convert Date input into Custom Format? If not will return String not convert
    
    private String name;
    private int id;

    private EnterpriseRule(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
    
    public boolean isRule(int id){
        return this.getId() == id;
    }
}
