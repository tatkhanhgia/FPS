
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import fps_core.module.DocumentUtils_rssp_i7;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import vn.mobileid.exsig_i7.Algorithm;
import vn.mobileid.exsig_i7.PdfForm;
import vn.mobileid.exsig_i7.PdfProfile;
import vn.mobileid.exsig_i7.PdfProfileCMS;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author GiaTK
 */
public class TestFileError {

    public static void main(String[] args) throws Exception {
        PdfProfile profile = new PdfProfileCMS(
                PdfForm.B,
                Algorithm.valueOf("SHA256"),
                Algorithm.valueOf("RSA"));

        profile.setIgnorePdfErrorStructure(true);
        profile.setSignatureName("signaturename");
        profile.setLocation("Location");
        profile.setReason("Reason");
        profile.setSignerCertificate("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");
        profile.setVisibleSignature("1", "0,0,150,50");

        DocumentUtils_rssp_i7.SigningMethodAsyncImp signFinal = new DocumentUtils_rssp_i7.SigningMethodAsyncImp();
        try {
            byte[] temporalData = profile.createTemporalFile(signFinal, Arrays.asList(Files.readAllBytes(Paths.get("C:\\\\Users\\\\Admin\\\\Downloads\\\\GiayBaoNoBaoCo_ky_so_25122023_171418 (1).pdf"))));
        } catch (StackOverflowError ex) {
            System.err.println("Over flow");
        }
    }

//    public static void main(String[] args)throws Exception {
//        String path = "C:\\Users\\Admin\\Downloads\\GiayBaoNoBaoCo_ky_so_25122023_171418 (1).pdf";
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(Files.readAllBytes(Paths.get(path)));
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        PdfWriter writer = new PdfWriter(outputStream);
//        com.itextpdf.kernel.pdf.PdfReader reader = new com.itextpdf.kernel.pdf.PdfReader(inputStream);
//
//        com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(
//                reader,
//                writer,
//                new StampingProperties().useAppendMode()
//        );
//        PdfAcroForm acroform = PdfFormCreator.getAcroForm(pdfDoc, true);
//
//        //Load Font
//        byte[] fontData = Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Documents\\NetBeansProjects\\Library_RSSP_SDK\\ProjectRSSP_newest\\FPS\\src\\java\\resources\\D-Times.ttf"));
//        PdfFont font = PdfFontFactory.createFont(fontData, PdfEncodings.IDENTITY_H);
//
//        //Create text Field       
//        Rectangle rect = new Rectangle(0, 0, 100, 50);
//        PdfFormField form = new SignatureFormFieldBuilder(
//                pdfDoc,
//                "signature")
//                .setWidgetRectangle(rect)
//                .setPage(1)
//                .createSignature();
//        
//        //Set the widget properties        
//        form.getWidgets().get(0).setHighlightMode(PdfAnnotation.HIGHLIGHT_INVERT).setFlag(PdfAnnotation.PRINT);
//        form.getFirstFormAnnotation().setVisibility(PdfFormAnnotation.VISIBLE);
//
//        com.itextpdf.kernel.pdf.PdfDictionary mkDictionary = form.getWidgets().get(0).getAppearanceCharacteristics();
//        if (null == mkDictionary) {
//            mkDictionary = new com.itextpdf.kernel.pdf.PdfDictionary();
//        }
//
//        PdfArray black = new PdfArray();
//        black.add(new PdfNumber(ColorConstants.BLACK.getColorValue()[0]));
//        black.add(new PdfNumber(ColorConstants.BLACK.getColorValue()[1]));
//        black.add(new PdfNumber(ColorConstants.BLACK.getColorValue()[2]));
//        mkDictionary.put(com.itextpdf.kernel.pdf.PdfName.BC, black);
//
//        PdfArray white = new PdfArray();
//        white.add(new PdfNumber(ColorConstants.WHITE.getColorValue()[0]));
//        white.add(new PdfNumber(ColorConstants.WHITE.getColorValue()[1]));
//        white.add(new PdfNumber(ColorConstants.WHITE.getColorValue()[2]));
//        mkDictionary.put(com.itextpdf.kernel.pdf.PdfName.BG, white);
//
//        form.getWidgets().get(0).setAppearanceCharacteristics(mkDictionary);
//
//        acroform.addField(form);
//
//        PdfFormXObject xObject = new PdfFormXObject(rect);
//        PdfCanvas canvas = new PdfCanvas(xObject, pdfDoc);
//        canvas
//                .setStrokeColor(ColorConstants.BLUE)
//                .setFillColor(ColorConstants.LIGHT_GRAY)
//                .rectangle(0 + 0.5, 0 + 0.5, 200 - 0.5, 100 - 0.5)
//                .fillStroke()
//                .setFillColor(ColorConstants.BLUE);
//        new Canvas(canvas, rect).showTextAligned("SIGN HERE", 100, 50,
//                TextAlignment.CENTER, (float) Math.toRadians(25));
//
//        // Note that Acrobat doesn't show normal appearance in the highlight mode.
//        form.getWidgets().get(0).setNormalAppearance(xObject.getPdfObject());
//
//        pdfDoc.close();        
//    }
}
