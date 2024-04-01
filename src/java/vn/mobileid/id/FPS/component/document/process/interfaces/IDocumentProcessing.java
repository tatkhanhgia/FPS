/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.document.process.interfaces;

import vn.mobileid.id.FPS.object.InternalResponse;

/**
 *
 * @author GiaTK
 */
public interface IDocumentProcessing {
    public InternalResponse processMultipleField(Object... object)throws Exception;
    
    public InternalResponse processField(Object... object)throws Exception;
    
}
