/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.enumeration;

/**
 *
 * @author GiaTK
 * Using for auto generate value for Annotation in Qrypto QR
 */
public enum QryptoVariable {
    SIGNER_1("@Signer1"),
    SIGNER_2("@Signer2"),
    SIGNER_NUMBER("@Signer");
    
    private String annotationName;

    private QryptoVariable(String annotationName) {
        this.annotationName = annotationName;
    }

    public String getAnnotationName() {
        return annotationName;
    }
    
    
}
