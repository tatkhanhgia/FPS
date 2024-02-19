///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package vn.mobileid.id.FPS.component.document.module;
//
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfReader;
//import com.itextpdf.signatures.PdfPKCS7;
//import com.itextpdf.signatures.SignatureUtil;
//import com.itextpdf.text.pdf.BaseFont;
//import java.io.ByteArrayInputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.security.cert.X509Certificate;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.List;
//import org.apache.commons.io.FileUtils;
//import vn.mobileid.exsig.Algorithm;
//import vn.mobileid.exsig.Color;
//import vn.mobileid.exsig.PdfForm;
//import vn.mobileid.exsig.PdfProfileCMS_V2;
//import vn.mobileid.exsig.SigningMethodAsync;
//import vn.mobileid.exsig.TextAlignment;
//import vn.mobileid.id.FPS.fieldAttribute.Dimension;
//import vn.mobileid.id.FPS.fieldAttribute.SignatureFieldAttribute;
//import vn.mobileid.id.FPS.object.Signature;
//import vn.mobileid.id.general.PolicyConfiguration;
//import vn.mobileid.id.utils.Utils;
//
///**
// *
// * @author GiaTK
// */
//public class DocumentUtils_rssp_i5 {
//
//    public static byte[] appendSignature(
//            byte[] filePdf,
//            SignatureFieldAttribute field,
//            String transactionId
//    ) throws Exception {
//        //Construct frame of Signature
//        Signature signatureData = field.getVerification();
//        PdfProfileCMS_V2 profile = new PdfProfileCMS_V2(PdfForm.B, Algorithm.valueOf(signatureData.getSignedHash()));
//        profile.setSignatureName(field.getFieldName());
//        profile.setFontSizeMin(3);
//        profile.setSigningAlgorithm(Algorithm.valueOf(signatureData.getSignatureAlgorithm()));
//        profile.setLocation(signatureData.getSigningLocation());
//        profile.setReason(signatureData.getSigningReason());
//        String dimension = "";
//        dimension += Math.round(field.getDimension().getX()) + ",";
//        dimension += Math.round(field.getDimension().getY()) + ",";
//        dimension += Math.round(field.getDimension().getWidth()+field.getDimension().getX()) + ",";
//        dimension += Math.round(field.getDimension().getHeight()+field.getDimension().getY());
//        if (field.getVisibleEnabled()) {
//            profile.setVisibleSignature(String.valueOf(field.getPage()), dimension);
//        }
//
//        //Load font        
//        byte[] temp = Utils.readFile("/resources/D-Times.ttf");
//        profile.setFont(
//                temp,
//                BaseFont.IDENTITY_H,
//                true,
//                10,
//                0,
//                TextAlignment.ALIGN_LEFT,
//                Color.BLACK);
//        //Construct Signature
//        SigningMethodAsyncImp signFinal = new SigningMethodAsyncImp();
//        List<String> certificateChain = signatureData.getCertificateChain();
//
//        signFinal.setCertificateChain(certificateChain);
//
//        List<String> signatures = new ArrayList<>();
//        signatures.add(signatureData.getSignatureValue());
//        signFinal.setSignatures(signatures);
//
//        List<byte[]> dataTobeSign = new ArrayList<>();
//        dataTobeSign.add(filePdf);
//        //Process
//        List<byte[]> result = PdfProfileCMS_V2.sign(signFinal, profile.createTemporalFile(signFinal, dataTobeSign));
//       
//        return result.get(0);
//    }
//
//    public static SignatureFieldAttribute verificationPDF(
//            byte[] filePdf,
//            SignatureFieldAttribute fieldOld,
//            String transactionId
//    ) throws Exception {
//        SignatureFieldAttribute field = new SignatureFieldAttribute();
//        Signature signature = new Signature();
//        PdfReader reader = new PdfReader(new ByteArrayInputStream(filePdf));
//        PdfDocument doc = new PdfDocument(reader);
//
//        signature.setCertificateChain(fieldOld.getVerification().getCertificateChain());
//        signature.setSignatureValue(fieldOld.getVerification().getSignatureValue());
//        signature.setSignatureAlgorithm(fieldOld.getVerification().getSignatureAlgorithm());
//        signature.setSignedHash(fieldOld.getVerification().getSignedHash());
//        signature.setQualified(true);
//        signature.setSigningReason(fieldOld.getVerification().getSigningReason());
//        signature.setSigningLocation(fieldOld.getVerification().getSigningLocation());
//
//        SignatureUtil sigUtil = new SignatureUtil(doc);
//        List<String> names = sigUtil.getSignatureNames();
//        DateFormat dateFormat = new SimpleDateFormat(
//                PolicyConfiguration
//                        .getInstant()
//                        .getSystemConfig()
//                        .getAttributes()
//                        .get(0)
//                        .getDateFormat());
//        for (String name : names) {
//            if (name.equalsIgnoreCase(fieldOld.getFieldName())) {
//                PdfPKCS7 pkcs7 = sigUtil.readSignatureData(name);
//                signature.setSignerName(pkcs7.getSignName());
//                signature.setSigningTime(pkcs7.getSignDate().getTime());
//                if (pkcs7.verifySignatureIntegrityAndAuthenticity()) {
//                    signature.setSignatureStatus("VALID");
//                } else {
//                    signature.setSignatureStatus("INVALID");
//                }
//                X509Certificate cert = pkcs7.getSigningCertificate();
//                signature.setSubjectDn(cert.getSubjectDN().getName());
//                signature.setIssuerDn(cert.getIssuerDN().getName());
//                signature.setCertValidFrom(cert.getNotBefore());
//                signature.setCertValidTo(cert.getNotAfter());
//            }
//        }
//        field.setVerification(signature);
//        return field;
//    }
//
//    static class SigningMethodAsyncImp implements SigningMethodAsync {
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
//        List<String> certChain = new ArrayList<>();
//        certChain.add("MIIFATCCA+mgAwIBAgIMREhaVZuKCUcS0GteMA0GCSqGSIb3DQEBBQUAMG4xJDAiBgNVBAMMG0ZQVCBDZXJ0aWZpY2F0aW9uIEF1dGhvcml0eTEfMB0GA1UECwwWRlBUIEluZm9ybWF0aW9uIFN5c3RlbTEYMBYGA1UECgwPRlBUIENvcnBvcmF0aW9uMQswCQYDVQQGEwJWTjAeFw0yMjA4MTYwODA1NDhaFw0yNDA4MTUwODA1NDhaMIGsMQswCQYDVQQGEwJWTjEXMBUGA1UECAwOSOG7kiBDSMONIE1JTkgxFTATBgNVBAcMDFRo4bunIMSQ4bupYzEWMBQGA1UECgwNQ8O0bmcgVHkgRklDTzE3MDUGA1UEAwwuQ8OUTkcgVFkgQ+G7lCBQSOG6pk4gxJDhuqZVIFTGryAmIEtEIFZMWEQgRklDTzEcMBoGCgmSJomT8ixkAQEMDE1TVDoxMjM0NTY3ODCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBALxhQGfAC/63V9IGfUYf+CH1AUhJ6YiNFVLab7vHW57MD0iFbJNNLb4XwzDnj/Odkd5IEhgP0Y6S1YdHBnUyUfpPSVbC91PSrFdVCpLo/9L/j0Rq0Oi4kfoqPtaq476tsiLtMNK0tF8wqv+tbfU5S3zw07V1Gz+gyx/IuwZ1T3wnw1Bzf9739yhawZ399AoZdWyFiIMv1mak/6IlbqTSQMzWxSqtuXZvJk2HCdhf7ZnEwOkvozAyaTxZXdjPQT7KXHgp/mB32YR5/ph4oPRA4GLlB6WZj/HQXUJOCkTmxAnQDP5S6OtE679zSgNeRj9q0zaNO2LgiewZEFFWFlObXTkCAwEAAaOCAV4wggFaMAwGA1UdEwEB/wQCMAAwHwYDVR0jBBgwFoAUGcwKzXuZL1PAJvoV6V05gkJUfA8wPgYIKwYBBQUHAQEEMjAwMC4GCCsGAQUFBzABhiJodHRwOi8vbW9iaWxlLWlkLnZuL29jc3AvcmVzcG9uZGVyMEUGA1UdIAQ+MDwwOgYLKwYBBAGB7QMBBAEwKzApBggrBgEFBQcCARYdaHR0cHM6Ly9tb2JpbGUtaWQudm4vY3BzLmh0bWwwNAYDVR0lBC0wKwYIKwYBBQUHAwIGCCsGAQUFBwMEBgorBgEEAYI3CgMMBgkqhkiG9y8BAQUwPQYDVR0fBDYwNDAyoDCgLoYsaHR0cDovL21vYmlsZS1pZC52bi9jcmwvZ2V0P25hbWU9RlBUX0NBX1NIQTEwHQYDVR0OBBYEFDQFV5KD57ZWYrmoQ9Jb7/5dzLT+MA4GA1UdDwEB/wQEAwIE8DANBgkqhkiG9w0BAQUFAAOCAQEAXCxrXnp0lg1HrydKO0IVIeg0FTW5tIYvCDZNYd2sJdLUE+udMlgExgZflIMFAHhvH9NcBAYlI7N9UU8wz2450vvLXtf7Fb4J/YaeE4F3RQ/bR/RY0r0kbyvP+t6WLuCEyv/A3/h+j8+NyC2tOe52EtBvaebIGtwC15OYTlTQB/ndPYGNbi3KBDNDok49dXmVuYzypvopE5/uMYPaZHmRWJsUIywtoCzY2EOwysup4HNKgntm41ZeMeYh0WNZ7fyvF1WbbeyG9n87YzJzozgheT5RmiY1YzKh5Y4h4iNYkzYAkpRUwyNmDf7kRA/I4ZIsgYjqL7GlYxx2IcMRrPcUXQ==");
//        certChain.add("MIID2TCCAsGgAwIBAgIQVBA4gCBbbRpw4CyQ2OpvgzANBgkqhkiG9w0BAQsFADB+MR0wGwYDVQQDDBRNSUMgTmF0aW9uYWwgUm9vdCBDQTEbMBkGA1UECwwSTmF0aW9uYWwgQ0EgQ2VudGVyMTMwMQYDVQQKDCpNaW5pc3RyeSBvZiBJbmZvcm1hdGlvbiBhbmQgQ29tbXVuaWNhdGlvbnMxCzAJBgNVBAYTAlZOMB4XDTIwMDUwMjEzMjczOFoXDTI1MDUwMjEzMjczOFowbjEkMCIGA1UEAwwbRlBUIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MR8wHQYDVQQLDBZGUFQgSW5mb3JtYXRpb24gU3lzdGVtMRgwFgYDVQQKDA9GUFQgQ29ycG9yYXRpb24xCzAJBgNVBAYTAlZOMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnzt8oRMosASZg3wJCEqkYxKWB6StjEhqBC6K5RB5Qan63fMPr5bfz3Z30hoYCDjT47NMVbo1Dv85w+Lwqe/NJ19ELB3A6j6x68EbwAv8RkQGusI1TGZ1yFIbTdbXg0AeJm2o+Wq7I1ifc4ZaVZ2+r6sXCXFl7EbIEffRiwA+Rs3qpDamwLfslbTtE8iWcuEy4D7ssb3jz7oKv+S/njG9l/qPZFqqF42pTILflOoghJqFHdTsN01vTPiI0aNvjgAlXM+KAH4vH4K6mJ7Yusxy3/TGSAj0NuSOTuS+wAbAqKQAqWgOxlWV6Da3IQpLDRFtGkfwU+pWgvRn+b1uG1ofjQIDAQABo2MwYTAdBgNVHQ4EFgQUGcwKzXuZL1PAJvoV6V05gkJUfA8wDwYDVR0TAQH/BAUwAwEB/zAfBgNVHSMEGDAWgBTtqrXG3zq4106d/D1cfkRrFuv8IDAOBgNVHQ8BAf8EBAMCAYYwDQYJKoZIhvcNAQELBQADggEBABUyFff2NKZrGDeA/G9CN/wgH1fOAxkz7yRWxDg05qebOejXKf1wvsurnlQeWMi2hl8ppNM/e8rpm5KIr9UZdvteF5K89NUtA5HjRH5TE5EV+HpAJ7BYQp7GYDBpxCmCAQSsWROAWNVPwqpY6aW1q7Jn1GU751P3sWtKJPJKx2CGKfpUEUPjeIShtuSyO1xfVuxDtfNPPzo43CZMwIEPXJk6lt41vciQsZz4D9FsfbzQKLTg/Bt4AJuLDwNgSJpHOB36fAfTVwXmWrGGtios0NXw6JrTP8HXeXOlzmJBnNQIYeJ8LWOjA30PHPd/Dkzce2X4UtTWY9JVM3cycJ9XGX8=");
//        certChain.add("MIID6TCCAtGgAwIBAgIQVBBSesIo0n5SXiud4r01PzANBgkqhkiG9w0BAQUFADB+MR0wGwYDVQQDDBRNSUMgTmF0aW9uYWwgUm9vdCBDQTEbMBkGA1UECwwSTmF0aW9uYWwgQ0EgQ2VudGVyMTMwMQYDVQQKDCpNaW5pc3RyeSBvZiBJbmZvcm1hdGlvbiBhbmQgQ29tbXVuaWNhdGlvbnMxCzAJBgNVBAYTAlZOMB4XDTE5MDYwNDA4MjAwOFoXDTI5MDYwNDA4MjAwOFowfjEdMBsGA1UEAwwUTUlDIE5hdGlvbmFsIFJvb3QgQ0ExGzAZBgNVBAsMEk5hdGlvbmFsIENBIENlbnRlcjEzMDEGA1UECgwqTWluaXN0cnkgb2YgSW5mb3JtYXRpb24gYW5kIENvbW11bmljYXRpb25zMQswCQYDVQQGEwJWTjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAJr1tm6iDDtvs0HtFLksjBiCMAdu0XNgeQ1s7QT2tYvLM4S6FDtQryZ+HGEh9pI04IQ0bJ7DNM1F6N583mPxpcFgG0a5QXpyuJMcDOQ0+ih+KH2mgzmEeFN9HrL6HA0h/x7p7kyAprKRsaNdclNq8lVcxaJqBy2DRFptjhGErntbZQKP80vqiKwLIHi+xOddpI1mEGnB4D9NItbQz+1vKLHCtB20ywsJ30GMcu162T+PSM2PpK9u+U25ZrcfLa2EmBW0tiMmZuQl4PTyGmoPmup8K6THrt57XHHgRoA2svyDOWUuMCVABE5K31IHN3oWEOmJViry/lae+PYy7KV00y0CAwEAAaNjMGEwHQYDVR0OBBYEFO2qtcbfOrjXTp38PVx+RGsW6/wgMA8GA1UdEwEB/wQFMAMBAf8wHwYDVR0jBBgwFoAU7aq1xt86uNdOnfw9XH5Eaxbr/CAwDgYDVR0PAQH/BAQDAgGGMA0GCSqGSIb3DQEBBQUAA4IBAQCa3YjUBCFs9oTSovWxxY1Gd6hYMkUrPFeDX45K6LfIEMN8iotisF+Ss+zHe+rWF5mDA53547x3wdkJFxAEmTHwu5OXZbWfbtXQPu4b0CBFt53XamAyAv4MUqzFpgzCNj8dMD4WHHqlXd1++YcpN5+QAMW6ARqfgnYLItGtzm2tF9WmV51I6Zfbo4tfr9JY/9UlrgfjfTgnxZvXknQIwz9D7xgND9gUhPPkn6J/jW3H9r57ZxknoLty3MJOu3cwOljoEOhWWleN/iGrw7VIJc5U5BD3hsYHUITl0aSsJ5+7ikBDKs2EGTCduv97T4nlWOhV/JST6m8DynwYbChgwylt");
//        SignatureFieldAttribute field = new SignatureFieldAttribute();
//        field.setPage(1);
//        Dimension dimension = new Dimension();
//        dimension.setX(30);
//        dimension.setY(100);
//        dimension.setWidth(130);
//        dimension.setHeight(170);
//        field.setDimension(dimension);
//        Signature signatureData = new Signature();
//        signatureData.setSignatureAlgorithm("RSA");
//        signatureData.setSignedHash("SHA256");
//        signatureData.setSigningReason("reason");
//        signatureData.setSigningLocation("Location");
//        signatureData.setCertificateChain(certChain);
//        signatureData.setSignatureValue("JnJsbrlomUy4g6FEQkTCEyivOIov8QaaupOnoGD6Hu3IT3AC0Wk/TPePO8Hwav829pSDw4Vl4bSpUEzoK/tP0fbs02pcrkLaXust6JnI5XB4l1LEG5byc/aub6YsYM8+VxRpv0UCSMRTZHkJimlUXBqNsg79kh7BnHMedThzpUoG/K4yDDmdrWnA+7fehSmuNoer0fKkma0yv2RfjpBfFuEl79GwnW3lA5SEvbbIeCqH2pvDUW923iaJjuXVmupsMWNNlyPZr+bg643z/ie/V2DoJ6dfwNHsGgUbgIuuA8kDxNLvxORWSjBZN31Vorkpa041dZvugjtzNIZM95fNOw==");
//        field.setVerification(signatureData);
//
//        byte[] data = DocumentUtils_rssp_i5.appendSignature(
//                Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\Paperless Gateway Service - API Specification - V1.230224.pdf")),
//                field,
//                "");
//        FileOutputStream out = new FileOutputStream(new File("C:\\Users\\Admin\\Downloads\\tessttt.pdf"));
//        out.write(data);
//        out.close();
//    }
//}
