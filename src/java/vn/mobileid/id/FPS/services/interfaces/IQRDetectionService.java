/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.services.interfaces;

import fps.readqr.objects.ImageQRCode;
import java.util.List;

/**
 *
 * @author GiaTK
 */
public interface IQRDetectionService {
    List<ImageQRCode> scanDocument(String path);
    List<ImageQRCode> scanDocument(byte[] fileData);
}
