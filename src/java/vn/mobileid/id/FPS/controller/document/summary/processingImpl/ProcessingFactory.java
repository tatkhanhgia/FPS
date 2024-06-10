/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.document.summary.processingImpl;

import fps_core.objects.core.BasicFieldAttribute;
import vn.mobileid.id.FPS.controller.document.summary.processingImpl.interfaces.IDocumentProcessing;
import vn.mobileid.id.FPS.controller.document.summary.processingImpl.interfaces.IModuleProcessing;
import vn.mobileid.id.FPS.controller.document.summary.processingImpl.interfaces.IVersion.Version;

/**
 *
 * @author GiaTK
 * The factory of the System, distribute the request
 */
public class ProcessingFactory<T extends BasicFieldAttribute> {
    private T type;

    public ProcessingFactory() {
    }

    public ProcessingFactory(T type) {
        this.type = type;
    }
    
    public  IDocumentProcessing createType(TypeProcess type) throws Exception{
        return createType(type, Version.V1);
    }
    
    public  IDocumentProcessing createType(
            TypeProcess type, 
            Version version) throws Exception{
        switch(type){
            case TEXTFIELD:{
                return new TextFieldProcessing<T>(this.type);
            }
            case SIGNATURE:{
                return new SignatureProcessing();
            }
            case CHECKBOX:{
                return new CheckboxProcessing(version);
            }
            case INITIALS:{
                return new InitialsProcessing(version);
            }
            case QRYPTO:{
                return new QryptoProcessing();
            }
            case IMAGE:{
                return new FileProcessing(version);
            }
            case RADIO:{
                return new RadioProcessing(version);
            }
            case ATTACHMENT:{
                return new AttachmentProcessing();
            }
            case HYPERLINK:{
                return new HyperLinkProcessing();
            }
        }
        throw new IllegalArgumentException();
    }
    
    public  IModuleProcessing createType_Module(TypeProcess type)throws Exception{
        return createType_Module(type, Version.V1);
    }
    
    public  IModuleProcessing createType_Module(TypeProcess type, Version version)throws Exception{
        switch(type){
            case TEXTFIELD:{
                return new TextFieldProcessing(this.type);
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
        ATTACHMENT,
        HYPERLINK
    }
}
