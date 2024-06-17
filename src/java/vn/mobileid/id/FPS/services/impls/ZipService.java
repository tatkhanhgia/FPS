/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.services.impls;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import vn.mobileid.id.FPS.services.interfaces.IZip;

/**
 *
 * @author GiaTK
 */
public class ZipService implements IZip {

    @Override
    public byte[] compressFiles(
            Map<String, byte[]> mapping) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(bos);

        mapping.forEach((name, binary) -> {
            ZipEntry entry = new ZipEntry(name);
            try {
                zip.putNextEntry(entry);
                zip.write(binary);
            } catch (IOException ex) {
                Logger.getLogger(ZipService.class.getName()).log(Level.SEVERE, null, ex);
            }

        });
        zip.close();
        return bos.toByteArray();
    }
}
