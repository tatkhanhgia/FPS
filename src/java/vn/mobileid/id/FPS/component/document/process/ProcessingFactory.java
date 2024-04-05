/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.document.process;

import vn.mobileid.id.FPS.component.document.process.interfaces.IDocumentProcessing;
import vn.mobileid.id.FPS.component.document.process.interfaces.IModuleProcessing;
import vn.mobileid.id.FPS.component.document.process.interfaces.IVersion;
import vn.mobileid.id.FPS.component.document.process.interfaces.IVersion.Version;

/**
 *
 * @author GiaTK
 */
public class ProcessingFactory {
    public static IDocumentProcessing createType(TypeProcess type) throws Exception{
        return createType(type, Version.V1);
    }
    
    public static IDocumentProcessing createType(TypeProcess type, Version version) throws Exception{
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
                return new InitialsProcessing(version);
            }
            case QRYPTO:{
                return new QryptoProcessing();
            }
            case IMAGE:{
                return new FileProcessing();
            }
            case RADIO:{
                return new RadioProcessing();
            }
            case ATTACHMENT:{
                return new AttachmentProcessing();
            }
        }
        throw new IllegalArgumentException();
    }
    
    public static IModuleProcessing createType_Module(TypeProcess type)throws Exception{
        return createType_Module(type, Version.V1);
    }
    
    public static IModuleProcessing createType_Module(TypeProcess type, Version version)throws Exception{
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
                return  new InitialsProcessing(version);
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
        IMAGE,
        RADIO,
        ATTACHMENT
    }
}
