/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.document.module;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 *
 * @author GiaTK
 */
public class QRGenerator {

    //<editor-fold defaultstate="collapsed" desc="Generate QR">
    public static byte[] generateQR(
            String encode,
            int width,
            int height,
            boolean isTransparent
    ) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        System.out.println("(QRGenerator)QR Width:"+width);
        System.out.println("QRGenerator)QR Height:"+height);
        
        BitMatrix byteMatrix = qrCodeWriter.encode(encode, BarcodeFormat.QR_CODE, width, height, hints);

        int matrixWidth = byteMatrix.getWidth();
        BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_ARGB);
        image.createGraphics();

        if (isTransparent) {
            for (int i = 0; i < matrixWidth; i++) {
                for (int j = 0; j < matrixWidth; j++) {
                    image.setRGB(i, j, byteMatrix.get(i, j) ? Color.BLACK.getRGB() : Color.TRANSLUCENT);
                }
            }
        } else {
            for (int i = 0; i < matrixWidth; i++) {
                for (int j = 0; j < matrixWidth; j++) {
                    image.setRGB(i, j, byteMatrix.get(i, j) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
                }
            }
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", bos);
        return bos.toByteArray();
    }
    //</editor-fold>

    public static void main(String[] args) throws WriterException, IOException {
//        BufferedImage myPicture = ImageIO.read(new File("C:\\Users\\Admin\\Downloads\\test.jpg"));
//        System.out.println(myPicture.getWidth());
//        System.out.println(myPicture.getHeight());
//        BufferedImage image = new BufferedImage(myPicture.getWidth(), myPicture.getHeight(), BufferedImage.TYPE_INT_ARGB);
//        image.createGraphics();
//        
//        for (int i = 0; i < myPicture.getWidth(); i++) {
//            for (int j = 0; j < myPicture.getHeight(); j++) {
//                image.setRGB(i, j, myPicture.getRGB(i, j) != myPicture.getRGB(0, 0)?  myPicture.getRGB(i, j) : Color.TRANSLUCENT);
////               image.setRGB(i, j, myPicture.getRGB(i, j));
////               image.setRGB(i, j, Color.BLACK.getRGB());
//            }
//        }
//        
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        ImageIO.write(image, "JPG", bos);

        FileOutputStream fos = new FileOutputStream("C:\\Users\\Admin\\Downloads\\test2.jpg");
//        fos.write(bos.toByteArray());
        fos.write(generateQR("", 1024, 1024, false));
        fos.close();
        
//        Graphics2D graphics = (Graphics2D) image.getGraphics();
////        graphics.setColor(Color.WHITE);
//
//        graphics.setColor(Color.WHITE);
//        graphics.fillRect(0, 0, matrixWidth, matrixWidth);
//        // Paint and save the image using the ByteMatrix
//        graphics.setColor(Color.BLACK);
//
//        for (int i = 0; i < matrixWidth; i++) {
//            for (int j = 0; j < matrixWidth; j++) {
//                if (byteMatrix.get(i, j)) {
//                    graphics.fillRect(i, j, 1, 1);
//                } 
//            }
//        }
//        ImageIO.write(image, "PNG", new File("C:\\Users\\Admin\\Downloads\\1.png"));
    }
}
