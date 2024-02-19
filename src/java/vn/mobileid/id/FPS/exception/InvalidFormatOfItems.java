/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.exception;

/**
 *
 * @author GiaTK
 */
public class InvalidFormatOfItems extends Exception{
    private String field;
    
    public InvalidFormatOfItems() {
    }

    public InvalidFormatOfItems(String string) {
        super(string);
        this.field = string;
    }

    public InvalidFormatOfItems(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public InvalidFormatOfItems(Throwable thrwbl) {
        super(thrwbl);
    }

    public InvalidFormatOfItems(String string, Throwable thrwbl, boolean bln, boolean bln1) {
        super(string, thrwbl, bln, bln1);
    }

    public String getField() {
        return field;
    }
}
