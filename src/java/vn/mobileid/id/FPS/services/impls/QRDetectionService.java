/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.services.impls;

import fps.readqr.enumeration.InputType;
import fps.readqr.factory.QRScanner;
import fps.readqr.factory.QRScannerFactory;
import fps.readqr.objects.ImageQRCode;
import java.util.List;
import vn.mobileid.id.FPS.services.interfaces.IQRDetectionService;

/**
 *
 * @author GiaTK
 */
public class QRDetectionService implements IQRDetectionService{
    private InputType typeOfDocument;
    
    public QRDetectionService(InputType type) {
        this.typeOfDocument = type;
    }
    
    @Override
    public List<ImageQRCode> scanDocument(String path) {
        QRScanner scanner = QRScannerFactory.createScanner(typeOfDocument);
        return scanner.scanQRCode(path);
    }

    @Override
    public List<ImageQRCode> scanDocument(byte[] fileData) {
        QRScanner scanner = QRScannerFactory.createScanner(typeOfDocument);
        return scanner.scanQRCode(fileData);
    }
}
