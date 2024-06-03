/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.services.interfaces;

/**
 *
 * @author GiaTK
 */
public interface IFms {
    byte[] downloadFile(String uuid) throws Exception;
    
    String uploadFile(byte[] binaryData) throws Exception; 
}
