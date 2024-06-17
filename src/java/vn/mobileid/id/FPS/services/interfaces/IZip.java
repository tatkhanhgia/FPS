/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.services.interfaces;

import java.util.List;
import java.util.Map;

/**
 *
 * @author GiaTK
 */
public interface IZip {
    byte[] compressFiles(Map<String, byte[]> mapping)throws Exception;
}
