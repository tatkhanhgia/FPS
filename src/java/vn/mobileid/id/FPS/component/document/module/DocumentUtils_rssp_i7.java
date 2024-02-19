///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package vn.mobileid.id.FPS.component.document.module;
//
//import com.itextpdf.io.font.PdfEncodings;
//import com.itextpdf.layout.properties.TextAlignment;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.Base64;
//import java.util.Calendar;
//import java.util.List;
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.io.IOUtils;
//import vn.mobileid.exsig_i7.Algorithm;
//import vn.mobileid.exsig_i7.Color;
//import vn.mobileid.exsig_i7.PdfEsealCMS;
//import vn.mobileid.exsig_i7.PdfForm;
//import vn.mobileid.exsig_i7.PdfProfile;
//import vn.mobileid.exsig_i7.PdfProfileCMS;
//import vn.mobileid.exsig_i7.PdfProfileCaDES;
//import vn.mobileid.exsig_i7.SigningMethodAsync;
//import vn.mobileid.id.FPS.component.field.RotateField;
//import vn.mobileid.id.FPS.enumration.LevelOfAssurance;
//import vn.mobileid.id.FPS.enumration.RotateDegree;
//import vn.mobileid.id.FPS.fieldAttribute.SignatureFieldAttribute;
//import vn.mobileid.id.FPS.object.CustomPageSize;
//import vn.mobileid.id.FPS.object.FileManagement;
//import vn.mobileid.id.FPS.object.Signature;
//import vn.mobileid.id.general.PolicyConfiguration;
//import vn.mobileid.id.utils.Utils;
//
///**
// *
// * @author GiaTK
// */
//public class DocumentUtils_rssp_i7 {
//
//    //<editor-fold defaultstate="collapsed" desc="Create Form Signature">
//    /*
//    Tạo form signature trên file. Dữ liệu trả về theo thứ tự
//    Obj[0]: binaryData của file đã tạo khung
//    Obj[1]: chuỗi hash của file. Sử dụng để ký
//    Obj[2]: temporalData
//    Obj[3]: SignatureName
//     */
//    public static Object[] createFormSignature(
//            String identifiedId,
//            byte[] filePdf,
//            SignatureFieldAttribute field,
//            String transactionId
//    ) throws Exception {
//        //Construct frame of Signature
//        Signature signatureData = field.getVerification();
//
//        PdfProfile profile = null;
//        LevelOfAssurance levelOfAssurance = LevelOfAssurance.ELECTRONIC_SIGNATURE;
//        if (!Utils.isNullOrEmpty(field.getLevelOfAssurance())) {
//            levelOfAssurance = LevelOfAssurance.getLevelOfAssurance(field.getLevelOfAssurance().get(0));
//        }
//
//        switch (levelOfAssurance) {
//            case ELECTRONIC_SIGNATURE: {
//                profile = new PdfProfileCMS(
//                        PdfForm.B,
//                        Algorithm.valueOf(signatureData.getSignedHash()),
//                        Algorithm.valueOf(signatureData.getSignatureAlgorithm()));
//                break;
//            }
//            case CADES_SIGNATURE: {
//                profile = new PdfProfileCaDES(
//                        PdfForm.B,
//                        Algorithm.valueOf(signatureData.getSignedHash()),
//                        Algorithm.valueOf(signatureData.getSignatureAlgorithm()));
//                break;
//            }
//            default: {
//                System.err.println("Cannot get Level of Assurance => Using default PdfProfileCMS");
//                profile = new PdfProfileCMS(
//                        PdfForm.B,
//                        Algorithm.valueOf(signatureData.getSignedHash()),
//                        Algorithm.valueOf(signatureData.getSignatureAlgorithm()));
//            }
//        }
//
//        //Create Signature Name
//        String signatureName = "";
//        if (!Utils.isNullOrEmpty(signatureData.getSignatureId())) {
//            signatureName = signatureData.getSignatureId();
//        } else {
//            signatureName = replaceDot(field.getFieldName());
//        }
//        profile.setIgnorePdfErrorStructure(true);
//        profile.setSignatureName(signatureName);
//        profile.setSigningTime(Calendar.getInstance().getTimeInMillis(),
//                PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat());
//        profile.setFontSizeMin(3);
//        profile.setLocation(signatureData.getSigningLocation());
//        profile.setReason(signatureData.getSigningReason());
//        profile.setSignerCertificate(field.getVerification().getCertificateChain().get(0));
//        if (!Utils.isNullOrEmpty(signatureData.getSignerContact())) {
//            profile.setSignerContact(signatureData.getSignerContact());
//        }
//        String dimension = "";
//        if(field.getRotate()>=0){
//            try{
//                FileManagement ana = DocumentUtils_itext7.analysisPDF_i7(filePdf);
//                float pageHeight = 0;
//                float pageWidth = 0;
//                for(CustomPageSize custom : ana.getDocumentCustom()){
//                    if(custom.getPageStart() == field.getPage()){
//                        pageHeight = custom.getPageHeight();
//                        pageWidth = custom.getPageWidth();
//                    }
//                }
//                
//                field.setDimension(RotateField.rotate(
//                        field.getDimension(),
//                        Math.round(pageHeight),
//                        Math.round(pageWidth),
//                        RotateDegree.getRotateDegree(field.getRotate()))
//                );
//            }catch(Exception ex){
//                System.err.println("Cannot Rotate Dimension. Using default!");
//            }
//        }
//        dimension += Math.round(field.getDimension().getX()) + ",";
//        dimension += Math.round(field.getDimension().getY()) + ",";
//        dimension += Math.round(field.getDimension().getWidth()) + ",";
//        dimension += Math.round(field.getDimension().getHeight());
//        if (field.getVisibleEnabled() != null && field.getVisibleEnabled()) {
//            profile.setVisibleSignature(String.valueOf(field.getPage()), dimension);
//        } else {
//            profile.setSigningPage(field.getPage());
//        }
//
//        //If exist hand signature image
//        if (!Utils.isNullOrEmpty(field.getHandSignatureImage())) {
//            try {
//                profile.setBackground(Base64.getDecoder().decode(field.getHandSignatureImage()));
//                profile.setOnlyBackground(true);
//            } catch (Exception ex) {
//                System.err.println("Cannot parse hand signature image! Invalid base64 encoded. Using default(DocumentUtils_rssp_i7: 81)");
//            }
//        }
//
//        //Load font        
//        byte[] temp = Utils.readFile("/resources/fonts/Roboto-Regular.ttf");
//        profile.setFont(
//                temp,
//                PdfEncodings.IDENTITY_H,
//                true,
//                10,
//                0,
//                TextAlignment.LEFT,
//                Color.BLACK);
//        //Construct Signature
//        SigningMethodAsyncImp signFinal = new SigningMethodAsyncImp();
//
//        List<byte[]> dataTobeSign = new ArrayList<>();
//        dataTobeSign.add(filePdf);
//
//        byte[] temporalData = profile.createTemporalFile(signFinal, dataTobeSign);
//        List<byte[]> filePdfAfter = profile.getTempDataList();
//
//        return new Object[]{filePdfAfter.get(0), profile.getHashList().get(0), temporalData, signatureName};
//    }
//    //</editor-fold>
//
//    //<editor-fold defaultstate="collapsed" desc="Append Signature">
//    public static byte[] appendSignatureValue(
//            byte[] temporalData,
//            SignatureFieldAttribute field,
//            String transactionId
//    ) throws Exception {
//        SigningMethodAsyncImp signFinal = new SigningMethodAsyncImp();
//        //Construct Certificate
//        List<String> chain = new ArrayList<>();
//        for (String certificate : field.getVerification().getCertificateChain()) {
//            System.out.println("Add Chain");
//            chain.add(certificate);
//        }
//        signFinal.setCertificateChain(chain);
//        //Construct Signature
//        List<String> signatures = new ArrayList<>();
//
//        signatures.add(field.getVerification().getSignatureValue());
//        signFinal.setSignatures(signatures);
//
//        LevelOfAssurance levelOfAssurance = LevelOfAssurance.ELECTRONIC_SIGNATURE;
//        if (!Utils.isNullOrEmpty(field.getLevelOfAssurance())) {
//            levelOfAssurance = LevelOfAssurance.getLevelOfAssurance(field.getLevelOfAssurance().get(0));
//        }
//
//        List<byte[]> results = null;
//        switch (levelOfAssurance) {
//            case ELECTRONIC_SIGNATURE: {
//                System.out.println("Append CMS");
//                results = PdfProfileCMS.sign(signFinal, temporalData);
//                break;
//            }
//            case ELECTRONIC_SEAL: {
//                results = PdfEsealCMS.sign(signFinal, temporalData);
//                break;
//            }
//            case CADES_SIGNATURE: {
//                System.out.println("Append CADES");
//                results = PdfProfileCaDES.sign(signFinal, temporalData);
//                break;
//            }
//            default: {
//                System.err.println("Cannot get Level of Assurance => Using PdfProfileCMS append");
//                results = PdfProfileCMS.sign(signFinal, temporalData);
//            }
//        }
//
//        return results.get(0);
//    }
//    //</editor-fold>
//
//    //<editor-fold defaultstate="collapsed" desc="Create Form Signature with Frame Eseal">
//    /*
//    Tạo form signature trên file với khung eseal. Dữ liệu trả về theo thứ tự
//    Obj[0]: binaryData của file đã tạo khung
//    Obj[1]: chuỗi hash của file. Sử dụng để ký
//    Obj[2]: temporalData
//    Obj[3]: signatureName
//     */
//    public static Object[] createEsealFormSignature(
//            String identifiedId,
//            byte[] filePdf,
//            SignatureFieldAttribute field,
//            String transactionId
//    ) throws Exception {
//        //Construct frame of Signature
//        Signature signatureData = field.getVerification();
//        PdfEsealCMS profile = new PdfEsealCMS(
//                PdfForm.B,
//                Algorithm.valueOf(signatureData.getSignedHash()),
//                Algorithm.valueOf(signatureData.getSignatureAlgorithm()));
//        profile.setIgnorePdfErrorStructure(true);
//        //Create Signature Name
//        String signatureName = "";
//        if (!Utils.isNullOrEmpty(signatureData.getSignatureId())) {
//            signatureName = signatureData.getSignatureId();
//        } else {
//            signatureName = replaceDot(field.getFieldName());
//        }
//        profile.setSignatureName(signatureName);
//        profile.setSigningTime(Calendar.getInstance().getTimeInMillis(),
//                PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat());
//        profile.setFontSizeMin(3);
//        profile.setLocation(signatureData.getSigningLocation());
//        profile.setReason(signatureData.getSigningReason());
//        profile.setSignerCertificate(field.getVerification().getCertificateChain().get(0));
//        String dimension = "";
//        dimension += Math.round(field.getDimension().getX()) + ",";
//        dimension += Math.round(field.getDimension().getY()) + ",";
//        dimension += Math.round(field.getDimension().getWidth()) + ",";
//        dimension += Math.round(field.getDimension().getHeight());
//        if (!Utils.isNullOrEmpty(signatureData.getSignerContact())) {
//            profile.setSignerContact(signatureData.getSignerContact());
//        }
//
//        //If exist hand signature image
//        System.out.println("Signature Field - get HandSignature 2:" + Utils.isNullOrEmpty(field.getHandSignatureImage()));
//        if (!Utils.isNullOrEmpty(field.getHandSignatureImage())) {
//            try {
//                System.out.println("Set BackGround");
//                profile.setBackground(Base64.getDecoder().decode(field.getHandSignatureImage()));
//                profile.setOnlyBackground(true);
//            } catch (Exception ex) {
//                System.err.println("Cannot parse hand signature image! Invalid base64 encoded. Using default(DocumentUtils_rssp_i7: 81)");
//            }
//        }
//        profile.createEseal(
//                field.getPage(),
//                (int) field.getDimension().getX(),
//                (int) field.getDimension().getY(),
//                "{signby}",
//                "{date}\nPurpose: Seal");
//
//        //Construct Signature
//        SigningMethodAsyncImp signFinal = new SigningMethodAsyncImp();
//
//        List<byte[]> dataTobeSign = new ArrayList<>();
//        dataTobeSign.add(filePdf);
//        byte[] temporalData = profile.createTemporalFile(signFinal, dataTobeSign);
//        List<byte[]> filePdfAfter = profile.getTempDataList();
////        signFinal.saveTemporalData(identifiedId, temporalData);
//
//        return new Object[]{filePdfAfter.get(0), profile.getHashList().get(0), temporalData, signatureName};
//    }
//    //</editor-fold>
//
//    //<editor-fold defaultstate="collapsed" desc="Replace Dot in Signature Field Name">
//    private  static String replaceDot(String fieldName){
//        return fieldName.replaceAll("\\.", "{dot}");
//    }
//    //</editor-fold> 
//    
//    public static class SigningMethodAsyncImp implements SigningMethodAsync {
//
//        public List<String> certificateChain;
//        public List<String> signatures;
//        public List<String> hashList;
//
//        public List<String> getCertificateChain() {
//            return certificateChain;
//        }
//
//        public void setCertificateChain(List<String> certificateChain) {
//            this.certificateChain = certificateChain;
//        }
//
//        public List<String> getSignatures() {
//            return signatures;
//        }
//
//        public void setSignatures(List<String> signatures) {
//            this.signatures = signatures;
//        }
//
//        public List<String> getHashList() {
//            return hashList;
//        }
//
//        public void setHashList(List<String> hashList) {
//            this.hashList = hashList;
//        }
//
//        @Override
//        public void generateTempFile(List<String> list) throws Exception {
//            this.hashList = list;
//        }
//
//        @Override
//        public List<String> getCert() throws Exception {
//            return this.certificateChain;
//        }
//
//        @Override
//        public List<String> pack() throws Exception {
//            return this.signatures;
//        }
//
//        public void saveTemporalData(String owner, byte[] temporalData) throws IOException {
//            String result = System.getProperty("java.io.tmpdir");
//            String fileName = result + owner + ".temp";
//            FileUtils.writeByteArrayToFile(new File(fileName), temporalData);
//        }
//
//        public byte[] loadTemporalData(String owner) throws IOException {
//            String result = System.getProperty("java.io.tmpdir");
//            String fileName = result + owner + ".temp";
//            System.out.println("FileNAME:" + fileName);
//            return Files.readAllBytes(Paths.get(fileName));
//        }
//    }
//
//    public static void main(String[] args) throws Exception {
////        SignatureFieldAttribute field = new SignatureFieldAttribute();
////        Signature sig = new Signature();
////        sig.setSignatureAlgorithm("RSA");
////        sig.setSignedHash("SHA256");
////        sig.setSigningLocation("Quanaj 1");
////        sig.setSigningReason("Reason");
//
////        sig.setCertificateChain(certChain);
////        
////        field.setVerification(sig);
//////        field.setFieldName("signature 1");
////        Dimension dimension = new Dimension();
////        dimension.setX(100);
////        dimension.setY(500);
////        dimension.setWidth(150);
////        dimension.setHeight(50);
////        field.setDimension(dimension);
////        field.setVisibleEnabled(true);
////        field.setPage(1);
////        Object[] ob = createFormSignature(
////                "identify",
////                Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\unsign.pdf")),
////                field,
////                "transactionId");
////        
////        byte[] file = (byte[]) ob[0];
////        String hash = (String) ob[1];
////        FileOutputStream out = new FileOutputStream("C:\\Users\\Admin\\Downloads\\result.pdf");
////        out.write(file);
////        out.close();
////        System.out.println("Hash:"+hash
////        );
////        SignatureFieldAttribute field = new SignatureFieldAttribute();
////        Signature sig = new Signature();
////        sig.setSignatureValue("RzIORqJ2Pif7tYW5Wkok/R+Zw8cdCQNfd7rw6gerIhVQLtHy/pliFtHT/SNgDFTYZW7p98r1qzOsdqjehAusYEWrKxfxzkA4OUQ1pOlVIL5QuxPeuHf0st3lpPLMt5cU6YAF8IK/kSLzE7essVWejbUL8E9yMTWttC4vZJoIs3FPSiwmY04djLom/hAy/CFylBf6RBrENTlFdSoUQj/oH3DQ39SylvkNTan1QuifD7EXLGTWB947AYRvLR+ZQcTjHj1HtNaNO9o90l0eD5U6AvKl39JXgtMw5N0aQK7K8Z2GLGcuaPtzPEl5QMYCzmPbl7uOC0nHQnu0pKnu8yFBnQ==");
////        List<String> certChain = new ArrayList<>();
////        certChain.add("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");
////        sig.setCertificateChain(certChain);
////        field.setVerification(sig);
////        
////        byte[] file = appendSignatureValue("identify", field, "transactionId");
////        FileOutputStream out = new FileOutputStream("C:\\Users\\Admin\\Downloads\\result.pdf");
////        out.write(file);
////        out.close();
//        List<String> certChain = new ArrayList<>();
//        certChain.add("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");
//        PdfProfileCMS profileCMS = new PdfProfileCMS(PdfForm.B, Algorithm.SHA256, Algorithm.RSA);
//        profileCMS.setSignatureName("hello");
//        profileCMS.setFontSizeMin(3);
//        profileCMS.setLocation("Location 1");
//        profileCMS.setReason("Reason A");
//        profileCMS.setSignerCertificate(certChain.get(0));
//        profileCMS.setVisibleSignature("1", "100,0,200,50");
//
//        SigningMethodAsyncImp signInit = new SigningMethodAsyncImp();
//
//        //Save TemporalData
//        List<byte[]> src = new ArrayList<>();
//        src.add(IOUtils.toByteArray(new FileInputStream("C:\\\\Users\\\\Admin\\\\Downloads\\\\unsign.pdf")));
//        byte[] temporalData = profileCMS.createTemporalFile(signInit, src);
//        FileOutputStream out = new FileOutputStream("C:\\Users\\Admin\\Downloads\\hello.pdf");
//        out.write(profileCMS.getTempDataList().get(0));
//        out.close();
//    }
//}
