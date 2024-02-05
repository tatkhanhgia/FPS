
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import vn.mobileid.exsig_i7.Algorithm;
import vn.mobileid.exsig_i7.PdfForm;
import vn.mobileid.exsig_i7.PdfProfileCMS;
import vn.mobileid.id.FPS.component.document.module.DocumentUtils_rssp_i7;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author GiaTK
 */
public class TestItext7 {
    public static void main(String[] args) throws Exception {
        List<String> certChain = new ArrayList<>();
        certChain.add("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");
        PdfProfileCMS profileCMS = new PdfProfileCMS(PdfForm.B, Algorithm.SHA256, Algorithm.RSA);
        profileCMS.setSignatureName("hello");
        profileCMS.setFontSizeMin(3);
        profileCMS.setLocation("Location 1");
        profileCMS.setReason("Reason A");
        profileCMS.setSignerCertificate(certChain.get(0));
        profileCMS.setVisibleSignature("1", "100,0,200,50");
        
        DocumentUtils_rssp_i7.SigningMethodAsyncImp signInit = new DocumentUtils_rssp_i7.SigningMethodAsyncImp();

        //Save TemporalData
        List<byte[]> src = new ArrayList<>();
        src.add(IOUtils.toByteArray(new FileInputStream("C:\\\\Users\\\\Admin\\\\Downloads\\\\demou-background-potrait.pdf")));
        byte[] temporalData = profileCMS.createTemporalFile(signInit, src);
        FileOutputStream out = new FileOutputStream("C:\\Users\\Admin\\Downloads\\hello.pdf");
        out.write(profileCMS.getTempDataList().get(0));
        out.close();
    }
}
