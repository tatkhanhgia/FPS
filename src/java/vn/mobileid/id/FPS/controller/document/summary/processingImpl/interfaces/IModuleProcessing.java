/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.document.summary.processingImpl.interfaces;

import vn.mobileid.id.FPS.object.InternalResponse;

/**
 *
 * @author GIATK
 */
public interface IModuleProcessing {
    public InternalResponse createFormField(Object...objects)throws Exception;
    
    public InternalResponse fillFormField(Object...objects)throws Exception;
    
    public InternalResponse deleteFormField(Object...objects)throws Exception;

    public InternalResponse replaceFormField(Object...objects)throws Exception;
}
