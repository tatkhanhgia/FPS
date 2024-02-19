/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.exception;

/**
 *
 * @author GiaTK
 */
public class QryptoException extends Exception{

    public QryptoException() {
    }

    public QryptoException(String string) {
        super(string);
    }

    public QryptoException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public QryptoException(Throwable thrwbl) {
        super(thrwbl);
    }

    public QryptoException(String string, Throwable thrwbl, boolean bln, boolean bln1) {
        super(string, thrwbl, bln, bln1);
    }
}
