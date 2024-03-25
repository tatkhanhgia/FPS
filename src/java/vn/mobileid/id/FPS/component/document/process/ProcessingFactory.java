/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.document.process;

/**
 *
 * @author GiaTK
 */
public class ProcessingFactory {
    public static DocumentProcessing createType(TypeProcess type) throws Exception{
        switch(type){
            case TEXTFIELD:{
                return new TextFieldProcessing();
            }
            case SIGNATURE:{
                return new SignatureProcessing();
            }
            case CHECKBOX:{
                return new CheckboxProcessing();
            }
            case INITIALS:{
                return  new InitialsProcessing();
            }
            case QRYPTO:{
                return new QryptoProcessing();
            }
            case IMAGE:{
                return new ImageProcessing();
            }
        }
        throw new IllegalArgumentException();
    }
    
    public static ModuleProcessing createType_Module(TypeProcess type)throws Exception{
        switch(type){
            case TEXTFIELD:{
                return new TextFieldProcessing();
            }
            case SIGNATURE:{
                return new SignatureProcessing();
            }
            case CHECKBOX:{
                return new CheckboxProcessing();
            }
            case INITIALS:{
                return  new InitialsProcessing();
            }
        }
        throw new IllegalArgumentException();
    }
    
    public static enum TypeProcess{
        SIGNATURE,
        TEXTFIELD,
        CHECKBOX,
        INITIALS,
        QRYPTO,
        IMAGE
    }
}
