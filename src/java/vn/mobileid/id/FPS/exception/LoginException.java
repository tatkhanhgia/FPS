/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.exception;

/**
 *
 * @author GiaTK
 */
public class LoginException extends Exception{

    public LoginException() {
    }

    public LoginException(String string) {
        super(string);
    }

    public LoginException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public LoginException(Throwable thrwbl) {
        super(thrwbl);
    }

    public LoginException(String string, Throwable thrwbl, boolean bln, boolean bln1) {
        super(string, thrwbl, bln, bln1);
    }
    
}
