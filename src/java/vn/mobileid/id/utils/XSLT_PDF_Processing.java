///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package vn.mobileid.id.utils;
//
//import com.itextpdf.html2pdf.ConverterProperties;
//import com.itextpdf.html2pdf.HtmlConverter;
//import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
//import com.itextpdf.kernel.geom.PageSize;
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.layout.Document;
//import com.itextpdf.layout.font.FontProvider;
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.JAXBException;
//import javax.xml.bind.Marshaller;
//import javax.xml.bind.SchemaOutputResolver;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.transform.Result;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;
//import javax.xml.transform.stream.StreamSource;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.w3c.dom.NodeList;
//import vn.mobileid.id.general.LogHandler;
//
///**
// *
// * @author GiaTK
// */
//public class XSLT_PDF_Processing {
//
//    final private static Logger LOG = LogManager.getLogger(XSLT_PDF_Processing.class);
//
//    public static void genSchema(Object ob) throws JAXBException, IOException {
//        JAXBContext jc = JAXBContext.newInstance(ob.getClass());
//        jc.generateSchema(new SchemaOutputResolver() {
//
//            @Override
//            public Result createOutput(String namespaceURI, String suggestedFileName)
//                    throws IOException {
//                StreamResult result = new StreamResult(System.out);
//                result.setSystemId(suggestedFileName);
//                return result;
//            }
//
//        });
//    }
//
//    public static byte[] appendData(Object ob, byte[] xslt) {
//        try {
//            ByteArrayInputStream inputStream = new ByteArrayInputStream(xslt);
//
//            JAXBContext jc = JAXBContext.newInstance(ob.getClass());
//
//            Marshaller mar = jc.createMarshaller();
//            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            mar.marshal(ob, out);
//
//            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//            try ( InputStream is = new ByteArrayInputStream(out.toByteArray())) {
//                DocumentBuilder db = dbf.newDocumentBuilder();
//
//                ByteArrayOutputStream output = new ByteArrayOutputStream(); //chua thay fileResult
//                TransformerFactory tf = TransformerFactory.newInstance();
//                Transformer transformer = tf.newTransformer(
//                        new StreamSource(inputStream));
//
//                StreamResult result = new StreamResult(output);
//                transformer.transform(new DOMSource(db.parse(is)), result);
//
//                return output.toByteArray();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } catch (JAXBException ex) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while append User Data into XSLT! - Detail" + ex);
//            }
//        }
//        return null;
//    }
//
//    public static byte[] convertHTMLtoPDF(byte[] contentHTML) {
//        try {
//            String temp = new String(contentHTML, StandardCharsets.UTF_8);            
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            PdfWriter writer = new PdfWriter(outputStream);
//            ConverterProperties converter = new ConverterProperties();
//
//            FontProvider fontProvider = loadFont(); 
//            converter.setFontProvider(fontProvider);
//            PdfDocument pdfDoc = new PdfDocument(writer);
//            pdfDoc.setDefaultPageSize(new PageSize(PageSize.A3));
//
//            Document document = HtmlConverter.convertToDocument(temp, pdfDoc, converter);
//            document.close();
//
//            return outputStream.toByteArray();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while append User Data into XSLT! - Detail" + ex);
//            }
//        }
//        return null;
//    }
//
////    public static Item_JSNObject getValueFromXSLT(byte[] xslt) {
////        try {
////            ByteArrayInputStream inputStream = new ByteArrayInputStream(xslt);
////
////            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
////            DocumentBuilder db = dbf.newDocumentBuilder();
////            org.w3c.dom.Document doc = db.parse(inputStream);
////
////            Item_JSNObject item = new Item_JSNObject();
////            doc.getDocumentElement().normalize();
////            NodeList map = doc.getElementsByTagName("xsl:value-of");
////            for (int i = 0; i < map.getLength(); i++) {
////                ItemDetails detail = new ItemDetails();
////                String value = map.item(i).getAttributes().item(0).getFirstChild().getNodeValue();
////                detail.setField(value);
////                detail.setMandatory_enable(true);
////                detail.setType(1);
////                detail.setValue(value);
////                item.appendData(detail);
////            }
////            return item;
////        } catch (Exception ex) {            
////           LogHandler.error(XSLT_PDF_Processing.class,
////                   "Cannot parse Data XSLT to object",
////                   ex);
////        } 
////        return null;
////    }
//
//    public static FontProvider loadFont() {
//        try {
//            FontProvider fontProvider = new DefaultFontProvider(false, false, false);
//            ClassLoader loader = Thread.currentThread().getContextClassLoader();
//            InputStream input = loader.getResourceAsStream("resources/verdana.ttf");
//
//            //Read font 1
//            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//            int nRead;
//            byte[] data = new byte[4];
//            while ((nRead = input.read(data, 0, data.length)) != -1) {
//                buffer.write(data, 0, nRead);
//            }
//            buffer.flush();
//            byte[] font1 = buffer.toByteArray();
//
//            //Read font 2
//            input = loader.getResourceAsStream("resources/verdana-bold.ttf");
//            buffer = new ByteArrayOutputStream();
//            nRead = 0;
//            data = new byte[4];
//            while ((nRead = input.read(data, 0, data.length)) != -1) {
//                buffer.write(data, 0, nRead);
//            }
//            buffer.flush();
//            byte[] font2 = buffer.toByteArray();
//            
//            //Read Font 3
//            input = loader.getResourceAsStream("resources/verdana-bold-italic.ttf");
//            buffer = new ByteArrayOutputStream();
//            nRead = 0;
//            data = new byte[4];
//            while ((nRead = input.read(data, 0, data.length)) != -1) {
//                buffer.write(data, 0, nRead);
//            }
//            buffer.flush();
//            byte[] font3 = buffer.toByteArray();
//            
//            fontProvider.addFont(font1);
//            fontProvider.addFont(font2);
//            fontProvider.addFont(font3);
//            return fontProvider;
//        } catch (IOException ex) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Cannot read font file");
//            }
//            return null;
//        }
//    }
//
//    
//    
////    public static void main(String[] arhs) throws IOException {
////        byte[] html = XSLT_PDF_Processing.appendData(new KYC(), Files.readAllBytes(new File("D:\\NetBean\\qrypto\\file\\result.xslt").toPath()));
////        byte[] pdf = XSLT_PDF_Processing.convertHTMLtoPDF(html);
////        try ( FileOutputStream fileOuputStream = new FileOutputStream("D:\\NetBean\\qrypto\\file\\result2.pdf")) {
////            fileOuputStream.write(pdf);
////        }
////
////        //Get data from XSLT
//////        Item_JSNObject item = XSLT_PDF_Processing.getValueFromXSLT(Files.readAllBytes(new File("D:\\NetBean\\qrypto\\file\\test.xslt").toPath()));
//////        System.out.println(new ObjectMapper().writeValueAsString(item));
////        //Data get from DB
//////        String xslt = "D:\\NetBean\\qrypto\\file\\test.xslt";
//////        String a = "D:\\NetBean\\qrypto\\file\\file\\300x400.png";
//////        String image = Base64.getEncoder().encodeToString(Files.readAllBytes(new File(a).toPath()));
//////        byte[] xsltB = Files.readAllBytes(new File(xslt).toPath());
////        //Begin flow 
//////        KYC object = new KYC("TATKHANHGIA",
//////                "07/09/2000",
//////                "VN",
//////                "079200011188",
//////                "IssuanceDate",
//////                "PlaceOfResidence",
//////                "19/01/2023",
//////                "19/01/2024",
//////                "18",
//////                "01",
//////                "2022");
//////        KYC object = new KYC();
//////
//////        byte[] html = XSLT_PDF_Processing.appendData(object, xsltB);
//////
//////        byte[] pdf = XSLT_PDF_Processing.convertHTMLtoPDF(html);
//////        Files.write(new File("D:\\NetBean\\qrypto\\file\\result.pdf").toPath(), pdf, StandardOpenOption.CREATE);
//////        List<byte[]> result1 = SigningService.getInstant(3).signHashWitness("TATKHANHGIA", image, pdf);
//////        
//////        for(byte[] temp : result1){
//////            List<byte[]> result2 = SigningService.getInstant(3).signHashBussiness( temp);
//////            Files.write(new File("D:\\NetBean\\qrypto\\file\\result.pdf").toPath(), result2.get(0), StandardOpenOption.CREATE);
//////            
//////        }
////    }
//}
