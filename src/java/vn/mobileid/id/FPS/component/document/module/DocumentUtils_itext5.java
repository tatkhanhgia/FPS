/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.document.module;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfString;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.util.Base64;
import java.util.Enumeration;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DLSet;
import org.bouncycastle.asn1.DLTaggedObject;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import vn.mobileid.id.FPS.fieldAttribute.SignatureFieldAttribute;

/**
 *
 * @author GiaTK
 */
public class DocumentUtils_itext5 {

    /**
     * Get signature value (String) in file PDF with signatureId - Lấy về giá
     * trị signature của signatureId trong file PDF
     *
     * @param pdf
     * @param signatureId
     * @return
     * @throws IOException
     */
    public static String getSignatureValue_i5(
            byte[] pdf,
            String signatureId) throws IOException {
        PdfReader reader = new PdfReader(pdf);
        PdfDictionary dict = reader.getTrailer().getAsDict(PdfName.ROOT).getAsDict(PdfName.DSS);

        AcroFields acro = reader.getAcroFields();
        List<String> signame = acro.getSignatureNames();
        if (signame != null || !signame.isEmpty()) {
            for (String name : signame) {
                if (signatureId.equalsIgnoreCase(name)) {
                    BouncyCastleProvider provider = new BouncyCastleProvider();
                    Security.addProvider(provider);

                    com.itextpdf.text.pdf.security.PdfPKCS7 pkcs = acro.verifySignature(name, provider.getName());
                    PdfString contents = acro.getSignatureDictionary(name).getAsString(PdfName.CONTENTS);
                    String sigvalue = getSigValue(contents);
                    return sigvalue;
                }
            }
        }
        return null;
    }

    private static String getSigValue(PdfString contents) throws UnsupportedEncodingException, IOException {
        ASN1InputStream aIn = new ASN1InputStream(new ByteArrayInputStream(contents.getOriginalBytes()));

        ASN1Sequence sed = ASN1Sequence.getInstance(aIn.readObject());

        Enumeration enumration = sed.getObjects();
        while (enumration.hasMoreElements()) {
            Object obj = enumration.nextElement();
            if (obj instanceof DLTaggedObject) {
                DLTaggedObject dl = (DLTaggedObject) obj;
                ASN1Object test = dl.getBaseObject();
                if (test instanceof ASN1Encodable) {
                    ASN1Sequence encodeable = (ASN1Sequence) test;
                    Enumeration enumration2 = encodeable.getObjects();
                    while (enumration2.hasMoreElements()) {
                        Object obj2 = enumration2.nextElement();
                        if (obj2 instanceof DLSet) {
                            DLSet set = (DLSet) obj2;
                            Enumeration enum3 = set.getObjects();
                            while (enum3.hasMoreElements()) {
                                Object ob3 = enum3.nextElement();
                                if (ob3 instanceof ASN1Sequence) {
                                    ASN1Sequence sequence4 = (ASN1Sequence) ob3;
                                    Enumeration enum4 = sequence4.getObjects();
                                    while (enum4.hasMoreElements()) {
                                        Object ob4 = enum4.nextElement();
                                        if (ob4 instanceof DEROctetString) {
                                            DEROctetString message = (DEROctetString) ob4;
                                            return Base64.getEncoder().encodeToString(message.getOctets());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return "";
    }
    
    

    public static byte[] sign_i7(
            byte[] pdf,
            SignatureFieldAttribute field,
            String transactionId
    ) throws Exception {
        return null;
    }

    public static void main(String[] args) throws IOException, Exception {
//        byte[] data = Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\Paperless Gateway Service - API Specification - V1.230224.pdf"));
//        TextFieldAttribute temp = new TextFieldAttribute();
//        temp.setFieldName("name");
//        temp.setVisibleEnabled(true);
//        temp.setMaxLength(0);
//        temp.setReadOnly(false);
//        temp.setMultiline(false);
//        temp.setAlign(TextFieldAttribute.Align.LEFT);
//        temp.setValue("hellloo");
//        temp.setPage(1);
//        Dimension dimension = new Dimension();
//        dimension.setX(100);
//        dimension.setY(100);
//        dimension.setWidth(100);
//        dimension.setHeight(50);
//        temp.setDimension(dimension);
//        byte[] result = appendText_i7(data, temp, "transactionId");
//        FileOutputStream out = new FileOutputStream("C:\\Users\\Admin\\Downloads\\appendForm.pdf");
//        out.write(result);
//        out.close();

//        byte[] data = Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\signByCodeappendForm.pdf"));
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        PdfWriter writer = new PdfWriter(outputStream);
//        com.itextpdf.kernel.pdf.PdfReader reader = new com.itextpdf.kernel.pdf.PdfReader(inputStream);
//
//        com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(
//                reader,
//                writer,
//                new StampingProperties().useAppendMode()
//        );
//
//        PdfAcroForm acroform = PdfFormCreator.getAcroForm(pdfDoc, true);
//        PdfFormField form = acroform.getField("name");
//        form.setValue("new value");
//        pdfDoc.close();
//        FileOutputStream out = new FileOutputStream("C:\\Users\\Admin\\Downloads\\appendForm2.pdf");
//        out.write(outputStream.toByteArray());
//        out.close();
        //===================CREATE SIGNATURE FORM============================
//          byte[] data = Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\Paperless Gateway Service - API Specification - V1.230224.pdf"));
//       SignatureFieldAttribute temp = new SignatureFieldAttribute();
//        temp.setFieldName("name");
//        temp.setVisibleEnabled(true);       
//        temp.setPage(1);
//        Dimension dimension = new Dimension();
//        dimension.setX(100);
//        dimension.setY(100);
//        dimension.setWidth(100);
//        dimension.setHeight(50);
//        temp.setDimension(dimension);
//        byte[] result = createSignatureForm_i7(data, temp, "transactionid");
//        FileOutputStream out = new FileOutputStream("C:\\Users\\Admin\\Downloads\\signatureForm5.pdf");
//        out.write(result);
//        out.close();
    }
}
