///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package vn.mobileid.id.FPS.component.document.module;
//
//import com.itextpdf.forms.PdfAcroForm;
//import com.itextpdf.forms.fields.CheckBoxFormFieldBuilder;
//import com.itextpdf.forms.fields.PdfButtonFormField;
//import com.itextpdf.forms.fields.PdfFormAnnotation;
//import com.itextpdf.forms.fields.PdfFormCreator;
//import com.itextpdf.forms.fields.PdfFormField;
//import com.itextpdf.forms.fields.PdfTextFormField;
//import com.itextpdf.forms.fields.SignatureFormFieldBuilder;
//import com.itextpdf.forms.fields.TextFormFieldBuilder;
//import com.itextpdf.io.font.PdfEncodings;
//import com.itextpdf.io.image.ImageData;
//import com.itextpdf.io.image.ImageDataFactory;
//import com.itextpdf.kernel.colors.ColorConstants;
//import com.itextpdf.kernel.colors.DeviceRgb;
//import com.itextpdf.kernel.font.PdfFont;
//import com.itextpdf.kernel.font.PdfFontFactory;
//import com.itextpdf.kernel.geom.Rectangle;
//import com.itextpdf.kernel.pdf.PdfArray;
//import com.itextpdf.kernel.pdf.PdfDictionary;
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfNumber;
//import com.itextpdf.kernel.pdf.PdfPage;
//import com.itextpdf.kernel.pdf.PdfReader;
//import com.itextpdf.kernel.pdf.PdfString;
//import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.kernel.pdf.StampingProperties;
//import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
//import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
//import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
//import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
//import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
//import com.itextpdf.layout.Canvas;
//import com.itextpdf.layout.Document;
//import com.itextpdf.layout.borders.Border;
//import com.itextpdf.layout.element.Image;
//import com.itextpdf.layout.element.Paragraph;
//import com.itextpdf.layout.layout.LayoutArea;
//import com.itextpdf.layout.layout.LayoutContext;
//import com.itextpdf.layout.layout.LayoutResult;
//import com.itextpdf.layout.properties.TextAlignment;
//import com.itextpdf.layout.properties.VerticalAlignment;
//import com.itextpdf.layout.renderer.RootRenderer;
//import com.itextpdf.signatures.PdfPKCS7;
//import com.itextpdf.signatures.PdfSignature;
//import com.itextpdf.signatures.SignatureUtil;
//import com.itextpdf.text.pdf.PdfStamper;
//import java.awt.Color;
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.security.GeneralSecurityException;
//import java.security.Security;
//import java.security.cert.X509Certificate;
//import java.util.ArrayList;
//import java.util.Base64;
//import java.util.HashMap;
//import java.util.List;
//import org.apache.commons.codec.binary.Hex;
//import org.bouncycastle.jce.provider.BouncyCastleProvider;
//import org.bouncycastle.tsp.TimeStampToken;
//import vn.mobileid.id.FPS.component.field.RotateField;
//import vn.mobileid.id.FPS.enumration.ProcessStatus;
//import vn.mobileid.id.FPS.enumration.RotateDegree;
//import vn.mobileid.id.FPS.enumration.TextField_Color;
//import vn.mobileid.id.FPS.enumration.TextField_Font;
//import vn.mobileid.id.FPS.fieldAttribute.BasicFieldAttribute;
//import vn.mobileid.id.FPS.fieldAttribute.CheckBoxFieldAttribute;
//import vn.mobileid.id.FPS.fieldAttribute.Dimension;
//import vn.mobileid.id.FPS.fieldAttribute.InitialsFieldAttribute;
//import vn.mobileid.id.FPS.fieldAttribute.SignatureFieldAttribute;
//import vn.mobileid.id.FPS.fieldAttribute.TextFieldAttribute;
//import vn.mobileid.id.FPS.object.CustomPageSize;
//import vn.mobileid.id.FPS.object.FileManagement;
//import vn.mobileid.id.FPS.object.Signature;
//import vn.mobileid.id.general.LogHandler;
//import vn.mobileid.id.general.Resources;
//import vn.mobileid.id.utils.Crypto;
//import vn.mobileid.id.utils.Utils;
//
///**
// *
// * @author GiaTK
// */
//public class DocumentUtils_itext7 {
//
//    //<editor-fold defaultstate="collapsed" desc="Analysys PDF Itext7">
//    /**
//     * Analysis the basic data of file PDF
//     *
//     * @param pdf
//     * @return
//     */
//    public static FileManagement analysisPDF_i7(byte[] pdf) {
//        com.itextpdf.kernel.pdf.PdfReader reader = null;
//        PdfDocument pdfDoc = null;
//        try {
//            reader = new com.itextpdf.kernel.pdf.PdfReader(new ByteArrayInputStream(pdf));
//            pdfDoc = new PdfDocument(reader);
//
//            int page = pdfDoc.getNumberOfPages();
//            int rotate = pdfDoc.getFirstPage().getRotation();
//            float width = pdfDoc.getPage(1).getPageSize().getWidth();
//            float height = pdfDoc.getPage(1).getPageSize().getHeight();
//            List<CustomPageSize> list = new ArrayList<>();
//            int start = 1;
//            float beforeWidth = pdfDoc.getPage(1).getPageSize().getWidth();
//            float beforeHeight = pdfDoc.getPage(1).getPageSize().getHeight();
//            for (int i = 1; i <= page; i++) {
//                float widthCheck = pdfDoc.getPage(i).getPageSize().getWidth();
//                float heightCheck = pdfDoc.getPage(i).getPageSize().getHeight();
//
//                if (widthCheck == beforeWidth && heightCheck == beforeHeight) {
//                    if (i == page) {
//                        CustomPageSize custom = new CustomPageSize();
//                        custom.setPageStart(start);
//                        custom.setPageEnd(i);
//                        custom.setPageWidth(widthCheck);
//                        custom.setPageHeight(heightCheck);
//                        custom.setRotate(pdfDoc.getPage(i).getRotation());
//                        list.add(custom);
//                    }
//                    continue;
//                }
//                if (widthCheck != beforeWidth || heightCheck != beforeHeight) {
//                    CustomPageSize custom = new CustomPageSize();
//                    custom.setPageStart(start);
//                    custom.setPageEnd(i - 1);
//                    custom.setPageWidth(beforeWidth);
//                    custom.setPageHeight(beforeHeight);
//                    custom.setRotate(pdfDoc.getPage(i).getRotation());
//                    list.add(custom);
//
//                    start = i;
//                    beforeWidth = widthCheck;
//                    beforeHeight = heightCheck;
//                    if (i == page) {
//                        CustomPageSize custom2 = new CustomPageSize();
//                        custom2.setPageStart(i);
//                        custom2.setPageEnd(i);
//                        custom2.setPageWidth(widthCheck);
//                        custom2.setPageHeight(heightCheck);
//                        custom2.setRotate(pdfDoc.getPage(i).getRotation());
//                        list.add(custom2);
//                    }
//                }
//
//            }
//            FileManagement temp = new FileManagement();
//            temp.setRotate(rotate);
//            temp.setPages(page);
//            if (list.isEmpty() || list.size() < 1) {
//                temp.setWidth(width);
//                temp.setHeight(height);
//            } else {
//                temp.setDocumentCustom(list);
//                temp.setWidth(width);
//                temp.setHeight(height);
//            }
//            temp.setSize(reader.getFileLength());
//            temp.setDigest(Hex.encodeHexString(Crypto.hashData(pdf, temp.getAlgorithm().getName())));
//            return temp;
//        } catch (IOException ex) {
//            return null;
//        } finally {
//            try {
//                pdfDoc.close();
//                reader.close();
//            } catch (Exception ex) {
//            }
//        }
//    }
//    //</editor-fold>
//
//    //<editor-fold defaultstate="collapsed" desc="Extract Page From PDF">
//    public static byte[] extractPageFromPDF_i7(byte[] pdf, int page) {
//        try {
//            com.itextpdf.kernel.pdf.PdfReader reader = new com.itextpdf.kernel.pdf.PdfReader(new ByteArrayInputStream(pdf));
//            PdfDocument pdfDoc = new PdfDocument(reader);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            PdfDocument temp = new PdfDocument(new PdfWriter(baos));
//            pdfDoc.copyPagesTo(page, page, temp);
//            temp.close();
//            return baos.toByteArray();
//        } catch (IOException ex) {
//            return null;
//        }
//    }
//    //</editor-fold>
//
//    //<editor-fold defaultstate="collapsed" desc="Verify Document">
//    public static List<Signature> verifyDocument_i7(
//            byte[] pdf
//    ) throws Exception {
//        List<Signature> listSignature = new ArrayList<>();
//        try {
//            com.itextpdf.kernel.pdf.PdfReader reader = new com.itextpdf.kernel.pdf.PdfReader(new ByteArrayInputStream(pdf));
//            PdfDocument pdfDoc = new PdfDocument(reader);
//
//            SignatureUtil sigUtil = new SignatureUtil(pdfDoc);
//            List<String> names = sigUtil.getSignatureNames();
//
//            for (String name : names) {
//                PdfPKCS7 pkcs7 = sigUtil.readSignatureData(name);
//                PdfSignature pdfSignature = sigUtil.getSignature(name);
//
//                listSignature.add(getVerification(pkcs7, name, pdfSignature));
//            }
//        } catch (IOException ex) {
//            return null;
//        }
//        return listSignature;
//    }
//    //</editor-fold>
//
//    //<editor-fold defaultstate="collapsed" desc="Create Text Form Field">
//    /**
//     * Using itext 7 to append Text into pdf
//     *
//     * @param pdf
//     * @param field
//     * @param transactionId
//     * @return
//     * @throws Exception
//     */
//    public static byte[] createTextFormField_i7(
//            byte[] pdf,
//            TextFieldAttribute field,
//            String transactionId
//    ) throws Exception {
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(pdf);
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
//        PdfFont font = null;
//        try {
//            byte[] fontData = Utils.readFile(TextField_Font.getPath(field.getFont().getName()));
//            font = PdfFontFactory.createFont(fontData, PdfEncodings.IDENTITY_H);
//        } catch (Exception ex) {
//            byte[] fontData = Utils.readFile(TextField_Font.D_Times);
//            font = PdfFontFactory.createFont(fontData, PdfEncodings.IDENTITY_H);
//        }
//
//        //Create text Field
//        Dimension dimension = field.getDimension();
//        if (field.getRotate() > 0) {
//            dimension = RotateField.rotate(
//                    dimension,
//                    pdfDoc.getPage(field.getPage()).getPageSize().getWidth(),
//                    pdfDoc.getPage(field.getPage()).getPageSize().getHeight(),
//                    RotateDegree.getRotateDegree(field.getRotate()));
//        }
//        Rectangle rect = new Rectangle(dimension.getX(), dimension.getY(), dimension.getWidth(), dimension.getHeight());
//
//        PdfTextFormField textField = null;
//        if (field.isMultiline()) {
//            textField = new TextFormFieldBuilder(pdfDoc, field.getFieldName())
//                    .setWidgetRectangle(rect)
//                    .setPage(field.getPage())
//                    .createMultilineText();
//        } else {
//            textField = new TextFormFieldBuilder(pdfDoc, field.getFieldName())
//                    .setWidgetRectangle(rect)
//                    .setPage(field.getPage())
//                    .createText();
//        }
//
//        //Append value
//        textField.getFirstFormAnnotation().setBackgroundColor(ColorConstants.WHITE);
//        textField.setValue(field.getValue());
//        textField.setColor(TextField_Color.getColor(field.getColor()).getItextColor());
//        textField.setFont(font);
//        try {
//            textField.setFontSize(field.getFont().getSize());
//        } catch (Exception ex) {
//            textField.setFontSize(5);
//        }
//        textField.setJustification(TextAlignment.valueOf(field.getAlign().getName()));
//
//        //Config (optional)
//        if (field.getVisibleEnabled()) {
//            textField.getFirstFormAnnotation().setVisibility(PdfFormAnnotation.VISIBLE);
//        }
//        if (field.getMaxLength() > 0) {
//            textField.setMaxLen(field.getMaxLength());
//        }
//        textField.setReadOnly(field.isReadOnly());
//        textField.setMultiline(field.isMultiline());
//
//        acroform.addField(textField);
//
//        pdfDoc.close();
//        return outputStream.toByteArray();
//    }
//    //</editor-fold>
//
//    //<editor-fold defaultstate="collapsed" desc="Create Text Field with PdfCanvas">
//    /**
//     * Using itext 7 to append Text into pdf
//     *
//     * @param pdf
//     * @param field
//     * @param transactionId
//     * @return
//     * @throws Exception
//     */
//    public static byte[] createTextField_i7(
//            byte[] pdf,
//            TextFieldAttribute field,
//            String transactionId
//    ) throws Exception {
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(pdf);
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
//        //Load Font
//        PdfFont font = null;
//        try {
//            byte[] fontData = Utils.readFile(TextField_Font.getPath(field.getFont().getName()));
//            font = PdfFontFactory.createFont(fontData, PdfEncodings.IDENTITY_H);
//        } catch (Exception ex) {
//            byte[] fontData = Utils.readFile(TextField_Font.D_Times);
////            byte[] fontData = Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Documents\\NetBeansProjects\\Library_RSSP_SDK\\ProjectRSSP_newest\\FPS\\src\\java\\resources\\fonts\\OpenSans-Bold.ttf"));
//            font = PdfFontFactory.createFont(fontData, PdfEncodings.IDENTITY_H);
//        }
//
//        //Create text Field
//        Dimension dimension = field.getDimension();
//        if (field.getRotate() > 0) {
//            dimension = RotateField.rotate(
//                    dimension,
//                    pdfDoc.getPage(field.getPage()).getPageSize().getWidth(),
//                    pdfDoc.getPage(field.getPage()).getPageSize().getHeight(),
//                    RotateDegree.getRotateDegree(field.getRotate()));
//        }
//        System.out.println("DimensionX:" + dimension.getX());
//        System.out.println("DimensionY:" + dimension.getY());
//        System.out.println("DimensionW:" + dimension.getWidth());
//        System.out.println("DimensionH:" + dimension.getHeight());
//        Rectangle rect = new Rectangle(dimension.getX(), dimension.getY(), dimension.getWidth(), dimension.getHeight());
//
//        PdfPage page = pdfDoc.getPage(field.getPage());
//
//        Paragraph paragraph = new Paragraph(
//                field.getValue())
//                .setMargin(0)
//                .setMultipliedLeading(0.75f)
//                .setFont(font)
//                .setFontSize(field.getFont().getSize())
//                .setTextAlignment(TextAlignment.CENTER)                
//                .setHeight(dimension.getHeight())
//                .setWidth(dimension.getWidth());
//    
////        String[] text = field.getValue().split("\n");
////
////        PdfCanvas pdfCanvas = new PdfCanvas(page);
////        pdfCanvas.beginText()
////                    .setFontAndSize(font, field.getFont().getSize())
////                    .moveText(dimension.getX(), dimension.getY());
////        pdfCanvas.showText(text[0]);
////        for (int i = 1; i < text.length; i++) {
////            pdfCanvas.newlineShowText(text[i]);
////        }
////        pdfCanvas.endText();
//
//        Canvas canvas = new Canvas(page, rect);
////        RootRenderer canvasRenderer = canvas.getRenderer();
////        while (paragraph
////                .createRendererSubTree()
////                .setParent(canvasRenderer)
////                .layout(new LayoutContext(new LayoutArea(
////                        field.getPage(), 
////                        new Rectangle(allowedWidth, field.getFont().getSize() * 2)))).getStatus() != LayoutResult.FULL) {
////            paragraph.setFontSize(--fontSize);
////        }
////        float xCoord = 151;
////        float yCoord = 73;
//
//        canvas.add(paragraph);
//        canvas.close();
//        pdfDoc.close();
//        return outputStream.toByteArray();
//    }
//    //</editor-fold>
//
//    //<editor-fold defaultstate="collapsed" desc="Create Signature Form">
//    /**
//     * Using itext 7 to create signature form
//     *
//     * @param pdf
//     * @param field
//     * @param transactionId
//     * @return
//     * @throws Exception
//     */
//    public static byte[] createSignatureForm_i7(
//            byte[] pdf,
//            SignatureFieldAttribute field,
//            String transactionId
//    ) throws Exception {
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(pdf);
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
////        byte[] fontData = Utils.readFile("/resources/D-Times.ttf");
//        byte[] fontData = Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Documents\\NetBeansProjects\\Library_RSSP_SDK\\ProjectRSSP_newest\\FPS\\src\\java\\resources\\D-Times.ttf"));
//        PdfFont font = PdfFontFactory.createFont(fontData, PdfEncodings.IDENTITY_H);
//
//        //Create text Field
//        Dimension dimension = field.getDimension();
//        Rectangle rect = new Rectangle(dimension.getX(), dimension.getY(), dimension.getWidth(), dimension.getHeight());
//
//        PdfFormField form = new SignatureFormFieldBuilder(pdfDoc, field.getFieldName())
//                .setWidgetRectangle(rect)
//                .setPage(field.getPage())
//                .createSignature();
//
//        //Set the widget properties        
//        form.getWidgets().get(0).setHighlightMode(PdfAnnotation.HIGHLIGHT_INVERT).setFlag(PdfAnnotation.PRINT);
//        if (field.getVisibleEnabled()) {
//            form.getFirstFormAnnotation().setVisibility(PdfFormAnnotation.VISIBLE);
//        } else {
//            form.getFirstFormAnnotation().setVisibility(PdfFormAnnotation.HIDDEN);
//        }
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
//        return outputStream.toByteArray();
//    }
//    //</editor-fold>
//
//    //<editor-fold defaultstate="collapsed" desc="Append Value">
//    /**
//     * Using itext7 to append value into TextField and flattern it
//     *
//     * @param pdf: file PDF
//     * @param field: TextFieldAttribute
//     * @param transactionId
//     * @return binary of file PDF after processed
//     * @throws java.lang.Exception
//     */
//    public static byte[] appendValue_i7(
//            byte[] pdf,
//            BasicFieldAttribute field,
//            String value,
//            String transactionId
//    ) throws Exception {
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(pdf);
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
//        PdfFormField form = acroform.getField(field.getFieldName());
//
//        if (form == null) {
//            return null;
//        }
//
//        form.setValue(value);
//
//        acroform.partialFormFlattening(field.getFieldName());
//        pdfDoc.close();
//        return outputStream.toByteArray();
//    }
//    //</editor-fold>
//
//    //<editor-fold defaultstate="collapsed" desc="Delete Form Field">
//    /**
//     * Using itext 7 to delete signature form
//     *
//     * @param pdf
//     * @param field
//     * @param transactionId
//     * @return
//     * @throws Exception
//     */
//    public static byte[] deleteFormField_i7(
//            byte[] pdf,
//            String fieldName,
//            String transactionId
//    ) throws Exception {
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(pdf);
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
//        acroform.removeField(fieldName);
//
//        pdfDoc.close();
//        return outputStream.toByteArray();
//    }
//    //</editor-fold>
//
//    //<editor-fold defaultstate="collapsed" desc="Check signature in file PDF">
//    public static List<SignatureFieldAttribute> getAllSignatures(byte[] data) {
//        PdfReader reader = null;
//        PdfDocument pdfDoc = null;
//        try {
//            BouncyCastleProvider provider = new BouncyCastleProvider();
//            Security.addProvider(provider);
//            List<SignatureFieldAttribute> fields = new ArrayList<>();
//
//            reader = new PdfReader(
//                    new ByteArrayInputStream(data));
//            pdfDoc = new PdfDocument(reader);
//
//            SignatureUtil sigUtil = new SignatureUtil(pdfDoc);
//            PdfAcroForm acro = PdfAcroForm.getAcroForm(pdfDoc, false);
//            List<String> names = sigUtil.getSignatureNames();
//
//            if (names == null || names.isEmpty()) {
//                return null;
//            }
//
//            for (String name : names) {
//                SignatureFieldAttribute field = new SignatureFieldAttribute();
//                Signature signature = new Signature();
//
//                //Basic field data                
//                PdfFormField formField = acro.getField(name);
//                List<PdfWidgetAnnotation> widgets = formField.getWidgets();
//                for (PdfWidgetAnnotation widget : widgets) {
//                    PdfArray rectangle = widget.getRectangle();
//                    Dimension dimension = new Dimension(
//                            rectangle.getAsNumber(0).floatValue(),
//                            rectangle.getAsNumber(1).floatValue(),
//                            rectangle.getAsNumber(2).floatValue() - rectangle.getAsNumber(0).floatValue(),
//                            rectangle.getAsNumber(3).floatValue() - rectangle.getAsNumber(1).floatValue()
//                    );
//                    field.setDimension(dimension);
//                    PdfDictionary dict = widget.getPdfObject();
//                    field.setVisibleEnabled(!CheckVisible.isInVisible(dict));
//                    PdfPage pages = widget.getPage();
//                    field.setPage(pdfDoc.getPageNumber(pages));
//                }
//                field.setFieldName(name);
//                Resources.reloadFieldTypes();
//                field.setType(Resources.getFieldTypes().get("SIGNATURE"));
//
//                PdfPKCS7 pkcs7 = sigUtil.readSignatureData(name);
//                //PKCS7 Data
//                signature.setSignatureStatus(pkcs7.verifySignatureIntegrityAndAuthenticity() ? "VALID" : "INVALID");
//                signature.setSignedHash(pkcs7.getDigestAlgorithmName());
//                signature.setSigningLocation(pkcs7.getLocation());
//                signature.setSigningReason(pkcs7.getReason());
//                signature.setSigningTime(pkcs7.getSignDate().getTime());
//                signature.setSignerName(pkcs7.getSignName());
//                signature.setSignatureAlgorithm(pkcs7.getSignatureAlgorithmName());
//                signature.setLtv(false);
//                signature.setQualified(true);
//                signature.setSignatureType(pkcs7.getFilterSubtype().getValue());
//                signature.setSignatureId(name);
//
//                //X509Certificate data
//                X509Certificate cer = pkcs7.getSigningCertificate();
//                signature.setCertValidFrom(cer.getNotBefore());
//                signature.setCertValidTo(cer.getNotAfter());
//                signature.setIssuerDn(cer.getIssuerDN().getName());
//                String[] issuerDn = cer.getIssuerDN().getName().split(",");
//
//                HashMap<String, String> issuerDn_Object = new HashMap<>();
//                for (String object : issuerDn) {
//                    String[] temp = object.split("=");
//                    issuerDn_Object.put(temp[0], temp[1]);
//                }
//                signature.setIssuerDN(issuerDn_Object);
//
//                signature.setSubjectDn(cer.getSubjectDN().getName());
//
//                //TimeStamp data
//                PdfSignature pdfSignature = sigUtil.getSignature(name);
//                PdfString content = pdfSignature.getContents();
//                if (pkcs7.isTsp()) {
//                    TimeStampToken tst = ReadTimestampToken.getSignatureTimeStamp(content.getEncoding().getBytes());
//                    signature.setTimestampAuthority(tst.getTimeStampInfo().getTsa().getName().toString());
//                    signature.setTimestampAt(tst.getTimeStampInfo().getGenTime());
//                }
//                field.setVerification(signature);
//                field.setProcessStatus(ProcessStatus.PROCESSED.getName());
//                fields.add(field);
//            }
//
//            return fields;
//        } catch (Exception ex) {
//            LogHandler.error(DocumentUtils_itext7.class, "", ex);
//        } finally {
//            try {
//                pdfDoc.close();
//                reader.close();
//            } catch (Exception ex) {
//            }
//        }
//        return null;
//    }
//    //</editor-fold>
//
//    //<editor-fold defaultstate="collapsed" desc="Renamed Field in PDF">
//    public static byte[] renamedField(
//            byte[] pdf,
//            String nameOld,
//            String nameNew,
//            String transactionId
//    ) throws Exception {
//        ByteArrayInputStream in = new ByteArrayInputStream(pdf);
//        PdfReader reader = new PdfReader(in);
//
//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        PdfDocument pdfDoc = new PdfDocument(reader, new PdfWriter(os));
//        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, false);
//        form.renameField(nameOld, nameNew);
//
//        pdfDoc.close();
//        return os.toByteArray();
//    }
//    //</editor-fold>
//
//    //<editor-fold defaultstate="collapsed" desc="Create CheckBox Form Field">
//    /**
//     * Using itext 7 to append CheckBox into pdf
//     *
//     * @param pdf
//     * @param field
//     * @param transactionId
//     * @return
//     * @throws Exception
//     */
//    public static byte[] createCheckBoxFormField_i7(
//            byte[] pdf,
//            CheckBoxFieldAttribute field,
//            String transactionId
//    ) throws Exception {
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(pdf);
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
////        byte[] fontData = Utils.readFile("/resources/D-Times.ttf");
////        PdfFont font = PdfFontFactory.createFont(fontData, PdfEncodings.IDENTITY_H);
//        //Create text Field
//        Dimension dimension = field.getDimension();
//        Rectangle rect = new Rectangle(dimension.getX(), dimension.getY(), dimension.getWidth(), dimension.getHeight());
//
//        PdfButtonFormField checkboxField = new CheckBoxFormFieldBuilder(pdfDoc, field.getFieldName())
//                .setWidgetRectangle(rect)
//                .setPage(field.getPage())
//                .createCheckBox();
//        if (field.isChecked()) {
//            checkboxField.setValue("yes");
//        }
//        checkboxField.setCheckType(vn.mobileid.id.FPS.enumration.CheckBoxType.getCheckBoxType(field.getCheckBoxType()));
//        checkboxField.setReadOnly(field.isReadOnly());
//        checkboxField.regenerateField();
//
//        acroform.addField(checkboxField);
//
//        pdfDoc.close();
//        return outputStream.toByteArray();
//    }
//    //</editor-fold>
//
//    //<editor-fold defaultstate="collapsed" desc="Create Initials Form">
//    /**
//     * Create Initials Form
//     *
//     * @param pdf
//     * @param field
//     * @param transactionId
//     * @return
//     * @throws java.io.IOException
//     */
//    public static byte[] createInitialsForm(
//            byte[] pdf,
//            InitialsFieldAttribute field,
//            String transactionId
//    ) throws IOException {
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(pdf);
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        PdfWriter writer = new PdfWriter(outputStream);
//        com.itextpdf.kernel.pdf.PdfReader reader = new com.itextpdf.kernel.pdf.PdfReader(inputStream);
//        reader.setUnethicalReading(false);
//
//        try (com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(
//                reader,
//                writer,
//                new StampingProperties().useAppendMode()
//        )) {
//            Dimension dimension = field.getDimension();
//            if (field.getRotate() >= 0) {
//                dimension = RotateField.rotate(
//                        field.getDimension(),
//                        pdfDoc.getPage(field.getPage()).getPageSize().getWidth(),
//                        pdfDoc.getPage(field.getPage()).getPageSize().getHeight(),
//                        RotateDegree.getRotateDegree(field.getRotate()));
//            }
//            Rectangle rect = new Rectangle(
//                    dimension.getX(),
//                    dimension.getY(),
//                    dimension.getWidth(),
//                    dimension.getHeight());
//
//            ImageData img = ImageDataFactory.create(Base64.getDecoder().decode(field.getImage()));
//
//            Image image = new Image(img);
//
//            if (field.isApplyToAll()) {
//                for (int page = 1; page <= pdfDoc.getNumberOfPages(); page++) {
//                    PdfPage pdfPage = pdfDoc.getPage(page);
//                    new PdfCanvas(
//                            pdfPage.newContentStreamAfter(),
//                            pdfPage.getResources(),
//                            pdfDoc).addImageFittedIntoRectangle(img, rect, false);
////                    new Canvas(pdfPage, rect).add(image);
////                    new Canvas(canvas, rect).add(image);
//                }
//            } else if (!Utils.isNullOrEmpty(field.getPages())) {
//                for (int page : field.getPages()) {
//                    PdfPage pdfPage = pdfDoc.getPage(page);
//                    new PdfCanvas(
//                            pdfPage.newContentStreamAfter(),
//                            pdfPage.getResources(),
//                            pdfDoc).addImageFittedIntoRectangle(img, rect, false);
////                    new Canvas(pdfPage, rect).add(image);
////                    new Canvas(canvas, rect).add(image);
//                }
//            }
//            pdfDoc.close();
//
//            //            PdfAcroForm acroform = PdfFormCreator.getAcroForm(pdfDoc, true);
//            //
//            //            Dimension dimension = field.getDimension();
//            //            Rectangle rect = new Rectangle(dimension.getX(), dimension.getY(), dimension.getWidth(), dimension.getHeight());
//            //
//            //            PdfButtonFormField InitialField = new PushButtonFormFieldBuilder(pdfDoc, field.getFieldName())
//            //                    .setWidgetRectangle(rect)
//            //                    .setPage(field.getPage())
//            //                    .createPushButton();
//            //
//            //            DeviceRgb color = new DeviceRgb(255, 255, 255);
//            //            
//            //            DeviceRgb color2 = DeviceRgb.makeLighter(color);
//            //            InitialField.setValue(field.getImage());
//            //            
//            //            if (field.isApplyToAll()) {
//            //                for (int page = 1; page <= pdfDoc.getNumberOfPages(); page++) {
//            //                    PdfButtonFormField temp = new PushButtonFormFieldBuilder(pdfDoc, field.getFieldName())
//            //                            .setWidgetRectangle(rect)
//            //                            .setPage(page)
//            //                            .createPushButton();
//            //
//            //                    temp.setValue(field.getImage());
//            //                    temp.getFirstFormAnnotation().setBackgroundColor(color2);
//            //
//            //                    InitialField.addKid(temp);
//            //                }
//            //            } else if (!Utils.isNullOrEmpty(field.getPages())) {
//            //                for (int page : field.getPages()) {
//            //                    PdfButtonFormField temp = new PushButtonFormFieldBuilder(pdfDoc, field.getFieldName())
//            //                            .setWidgetRectangle(rect)
//            //                            .setPage(page)
//            //                            .createPushButton();
//            //                    temp.setValue(field.getImage());
//            //
//            //                    InitialField.addKid(temp);
//            //                }
//            //            }
//            //
//            //            acroform.addField(InitialField);
//        }
//        return outputStream.toByteArray();
//    }
//    //</editor-fold>
//
//    //<editor-fold defaultstate="collapsed" desc="Add Image into PDF">
//    public static byte[] addImage_test(
//            byte[] pdf,
//            byte[] img,
//            int page,
//            float x,
//            float y,
//            float width,
//            float height
//    ) throws Exception {
//        System.out.println("(DocumentUtils_itext7)AddImage X:" + x);
//        System.out.println("(DocumentUtils_itext7)AddImage Y:" + y);
//        System.out.println("(DocumentUtils_itext7)AddImage W:" + width);
//        System.out.println("(DocumentUtils_itext7)AddImage H:" + height);
//        PdfReader reader = new PdfReader(new ByteArrayInputStream(pdf));
//        reader.setUnethicalReading(true);
//
//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        PdfDocument pdfDoc = new PdfDocument(reader, new PdfWriter(os));
//
//        try (Document document = new Document(pdfDoc)) {
//            //Read image
//            int maxPage = pdfDoc.getNumberOfPages();
//
//            ImageData imgData = ImageDataFactory.create(img);
//
//            Image image = new Image(imgData)
//                    .setFixedPosition(page > maxPage ? maxPage : page, x, y) // Sign the last page if pageSign is greater than maxPage
//                    .setHeight(height)
//                    .setWidth(width);
//
//            document.add(image);
//        }
//
//        return os.toByteArray();
//    }
//    //</editor-fold>
//
//    //==========================================================================
//    //<editor-fold defaultstate="collapsed" desc="Get Verification">
//    private static Signature getVerification(
//            PdfPKCS7 pkcs7,
//            String name,
//            PdfSignature pdfSignature) throws GeneralSecurityException {
//        Signature signature = new Signature();
//        //PKCS7 Data
//        signature.setSignatureStatus(pkcs7.verifySignatureIntegrityAndAuthenticity() ? "VALID" : "INVALID");
//        signature.setSignedHash(pkcs7.getDigestAlgorithmName());
//        signature.setSigningLocation(pkcs7.getLocation());
//        signature.setSigningReason(pkcs7.getReason());
//        signature.setSigningTime(pkcs7.getSignDate().getTime());
//        signature.setSignerName(pkcs7.getSignName());
//        signature.setSignatureAlgorithm(pkcs7.getSignatureAlgorithmName());
//        signature.setLtv(false);
//        signature.setQualified(true);
//        signature.setSignatureType(pkcs7.getFilterSubtype().getValue());
//        signature.setSignatureId(name);
//        signature.setFieldName(name);
//
//        //X509Certificate data
//        X509Certificate cer = pkcs7.getSigningCertificate();
//        signature.setCertValidFrom(cer.getNotBefore());
//        signature.setCertValidTo(cer.getNotAfter());
//        signature.setIssuerDn(cer.getIssuerDN().getName());
//        String[] issuerDn = cer.getIssuerDN().getName().split(",");
//
//        HashMap<String, String> issuerDn_Object = new HashMap<>();
//        for (String object : issuerDn) {
//            String[] temp = object.split("=");
//            issuerDn_Object.put(temp[0], temp[1]);
//        }
//        signature.setIssuerDN(issuerDn_Object);
//
//        signature.setSubjectDn(cer.getSubjectDN().getName());
//
//        //TimeStamp data
//        PdfString content = pdfSignature.getContents();
//        if (pkcs7.isTsp()) {
//            TimeStampToken tst = ReadTimestampToken.getSignatureTimeStamp(content.getEncoding().getBytes());
//            signature.setTimestampAuthority(tst.getTimeStampInfo().getTsa().getName().toString());
//            signature.setTimestampAt(tst.getTimeStampInfo().getGenTime());
//        }
//        return signature;
//    }
//    //</editor-fold>
//
//    public static void main(String[] args) throws IOException, Exception {
//
//        //Create Signature form field
////        SignatureFieldAttribute sig = new SignatureFieldAttribute();
////        sig.setFieldName("gia");
////        sig.setDimension(new Dimension(0,0,100,100));
////        sig.setVisibleEnabled(true);
////        sig.setPage(1);
////        Signature temp = new Signature();
////        temp.setSignedHash("SHA256");
////        temp.setSignatureAlgorithm("RSA");
////        temp.setSigningLocation("Q1");
////        temp.setSigningReason("Reason");
////        sig.setVerification(temp);
////        Object[] datas = DocumentUtils_rssp_i7.createFormSignature(
////                "gia",
////                Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\paperless.pdf")),
////                sig,
////                "transaction");
////        FileOutputStream os = new FileOutputStream("C:\\Users\\Admin\\Downloads\\result.pdf");
////        os.write((byte[])datas[0]);
////        os.close();
////         //Delete form field
////        byte[] data= DocumentUtils_itext7.deleteFormField_i7(
////                 Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\paperless.pdf")),
////                 "gia",
////                 "tramscatopmod");
////        
////        FileOutputStream os = new FileOutputStream("C:\\Users\\Admin\\Downloads\\resultAfterDelete.pdf");
////        os.write(data);
////        os.close();
//        //Verify itext7
////        byte[] data = Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\response.pdf"));
////        List<SignatureFieldAttribute> lists = DocumentUtils_itext7.getAllSignatures(data);
////        for (SignatureFieldAttribute signature : lists) {
////            System.out.println("Name:" + signature.getFieldName());
////            System.out.println(signature.getType().getTypeId());
////            System.out.println(signature.getTypeName());
////            System.out.println("Visble:" + signature.getVisibleEnabled());
////            System.out.println("Page:" + signature.getPage());
////            System.out.println("Object:" + new ObjectMapper().writeValueAsString(signature));
////        }
////        //Create CheckBox
////        byte[] data = Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\Report - Hành vi NTD VN trên internet - 2015.pdf"));
////        InitialsFieldAttribute temp = new InitialsFieldAttribute();
//////        temp.setPage(1);
////        temp.setFieldName("Field1");
////        temp.setDimension(new Dimension(200, 100, 20, 20));
////
////        List<Integer> pages = new ArrayList<>();
////        pages.add(1);
////        pages.add(2);
////        pages.add(3);
////        temp.setPages(pages);
//////        temp.setApplyToAll(true);
////        temp.setImage("iVBORw0KGgoAAAANSUhEUgAAADIAAAAyCAYAAAAeP4ixAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAepJREFUeNrs2L9Lw0AUB/DXo1ZrFB2kgrjUWcWtuhVxFf8IwU2hm4NjQbeAboJ/hLiKjuKsczsIoiFQRIMoRcyLXklL86PhvTYt7yDQBpLcJ/cu+V4yDdteA4BZGPKWdTfT3crDDlEwIk0gAhGIQAQiEIH8RxSvPT58praThqGguDQeDbk4t+Hq8i3Vdxwh1ZMFDxVYWvXad+pLp177gqPDZ3Ccn8HNkeWVPDuGHXJQKXglsbk1zYpR3AgN8P/mwKh+IML2UWEUNwIveFx9aV2QC6O4EXih+zun7e5RY/D8ihuhO9/5nxJjWU06SBgiqK4pMapfCG5MpmHbtzfX72UcnkTRoZiD0oYRCxEWOTAixTmu+wt34g8CBN+1rNcmVPafeuoMYsyzxXTFeGNKQWF+rMcgmCN70Hgj4qbfctLgiJ3Z3ZvzfuNoYGlhiUU1nBc4P/Rxp6YFzkey0sJzZXX6TboeweOwI9gprHes+yhMJyIuPnCOrOZpSst9WHh3VC+CEBO0EKJGkM+ROBguBHnWCsNwItrW7JQY/aLTGNy3vTPDhmBLv50jw41gXY/4MdwIltLqVmb4nuFEsEM0BtcLSXPUwEvL37gRLYhOr8PY8GFSWp+kS7+DbvI1XiACEYhABCKQUYL8CjAAcwxl0E1pwTEAAAAASUVORK5CYII=");
////        byte[] os = createInitialsForm(data, temp, "");
////        FileOutputStream fos = new FileOutputStream("C:\\Users\\Admin\\Downloads\\result.pdf");
////        fos.write(os);
////        fos.close();
//        //Append Image
////        byte[] filePdf = Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\unsign.pdf"));
////        byte[] image = Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\captcha.png"));
////        byte[] pdfResult = addImage_test(filePdf, image, 1, 0, 0, 250, 100);
////
////        FileOutputStream fos = new FileOutputStream("C:\\Users\\Admin\\Downloads\\result.pdf");
////        fos.write(pdfResult);
////        fos.close();
////          //Create TextFormField
//        byte[] pdf = Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\unsign.pdf"));
//        TextFieldAttribute textField = new TextFieldAttribute();
//        textField.setColor("Black");
//        textField.setDimension(new Dimension(0, 0, 100, 100));
//        textField.setFieldName("gia");
//        textField.setAlign(TextFieldAttribute.Align.LEFT);
//        textField.setPage(1);
//
//        TextFieldAttribute.Font font = new TextFieldAttribute.Font();
//        font.setSize(10);
//
//        textField.setFont(font);
//        textField.setValue("TẤT KHÁNH GIA một hai ba bốn năm sáu bảy tám chín mười\n Yêu Châu Trần Anh Thư\nhehe");
//        byte[] result = createTextField_i7(pdf, textField, "");
//        FileOutputStream fos = new FileOutputStream("C:\\Users\\Admin\\Downloads\\response.pdf");
//        fos.write(result);
//        fos.close();
////        InitialsFieldAttribute field = new InitialsFieldAttribute();
////        field.setFieldName("init");
////        field.setApplyToAll(true);
////        field.setDimension(new Dimension(590, 126, 79, 76));
////        List<Integer> pages = new ArrayList<>();
////        pages.add(1);
////        field.setPages(pages);
////        field.setVisibleEnabled(true);
////        field.setImage("iVBORw0KGgoAAAANSUhEUgAAATwAAACfCAYAAABk+mXyAAAAAXNSR0IArs4c6QAAIABJREFUeF7sfQWYVNX7/+dOd89sdxFLNyygNEgsDRJS0ikliCAiKCoqmBgYIN3dDQu7dCwL250zG5M7df7/mf2i6A9lExDmPg+P7jP3nnvez/uec9/zJhW9dypp3vsrCq7LhYALARcCLzgC1IUdEwhFUXD8o9FoaNHna9fm94Iz3UWeC4GXFQHX5vayct5FtwuBlxAB6uzWcX/R8BxanuOf65j7EkqDi2QXAi84Ai4N7wVnsIs8FwIuBP5EgDq37c3/o+G57HguEXEh4ELgRUTApeG9iFx10eRCwIXAYxGgonZN+kPDc2l2LilxIeBC4EVGoMIa3qNxexd3TiSt+31X4TFeZEBdtLkQcCHw/CJAnd8+3qnhPfTOPk7Lu7R7MmkZ+Y1rY3t++eiamQsBFwLlQICqTKbFmS1jic1mQ4fXf3Fugpf3TCGO/7qOxOVA3HWLCwEXAs8MAer05jF/0fAcmh6dTnfF4T0zlrhe7ELAhUBNIVChY6pDG7Tb7Xh4vHUchyMGfF+hMWqKENe4LgRcCLgQeBICf3hpXdkVT4LK9bsLARcC/3UEKqWdOTQ9hw2vVd9vK/X8fx001/xdCLgQ+G8iUC4v7X+TNNesXQi4EHAh8FcEnKllbQf+8Ie3tbo8rQ7PbXWN5WKaCwEXAi4EqgMB50bncD44bHiuIOLqgNQ1hgsBFwLPKwLl2vAcNjsHAQ9LRv397+eVONe8XAi4EHAh8CgCNXKkfZiZ8fcwFhf0zx8Cl/ZMJy37rHE5n54/1rhmVAMIPHHDcxQX+DdvrGNzcxyHXV7bGuCOa0gXAi4EqhWBch1pq/WNrsFcCLgQcCHwjBBwbXjPCHjXa10IuBB4+gg88Uhbnin927HXFZ5SHgRd97gQcCHwNBCgjq0fQTqPWO/U9Bwb16OtGp9kv3t0gpWpuvI0CHS9w4WACwEXAg8RcJaHMhqNYDKZrjg8l1y4EHAh8EIj4NTsTm4cRRgMBtoN+vGPjAtCyB9VUV5oBFzEuRBwIfDSIOC04bFYLFfxzpeG5S5CXQi8vAhQDu3uYeXiv9vhHv27Iva8lxdOF+UuBFwIPM8IUBd2TCAO+93DtLHqnOzz1gvj8u7ppEWkK6vg8t4ZpEXv1a7siuoUdtdY/wkEnE4Lq9UKhw3vcZueK6zkP8FH1yRdCLgQKAcCzorHrh4W5UDKdYsLARcC/3kEqIPrhhCJRPLYkJSnYbeL2jmFtOr3tfN4dWnXNGdVlpZ9vyzzFruOoP95AXMR4ELgeULgj9QyR3MeDodTI7a854lg11xcCLgQeHkR+CPDorS0FI7wFMfx1tGY+1F7Xk3Xv4veM8PZKrJZ7y9chvSXVxZdlLsQqHEEnH1pXxmyzrnRuNLDahxv1wtcCLgQeIYIUEd/G07EYvEzCzx+1E7n0vSeoSS4Xu1C4CVAwBmW4jjOOsJSqrvt4r9pjM9Sm3S825E652oy9BJIuItEFwKPIOA8yl7cOdG5AbTpv/aZ2NBi9s50emdfVhvelvXzSF5+Pnx8fBA58P1nwgPXqnAh8DIg4OxLGzHg+2pv0/gygFcZGjf8sJAkp6QhIyMTmuISpGdlQWcwQiQSoW7dukhIiEd6WipCggMx+o3hGPT6u64NsDJA18AzW39bTHbt2AU3pQpf/HjCxZcawLimh6SObxhJ+Hw+HHXwzGaz00v7aLvGR4+eNXEMdWh3/2XNLmr3FNIqsiyO8PyuWSSi7+fO/z+0dQHpPuhDat23b5ETZy7gwqVrSMuwQK5iQyiWwVRqQUBAIJLiExAYGITbt25DJBZCIhGD2C0wlxogEnLh5aFAz+4dMX7SJ64FVtOr4X/jvz2rE2FyhWCweFj2we/gcAFvbzEaNmiI6MsxkEll0JVowWHR0bZlE/h5KzHn/Y0u/jwl/lTlNRVmkiP39uHR91HtsCqTeFGf7dU1kBw5kYRXOjWAVOmJ2PvJ4AjEzoX0ICERbAYTYi4facnJiIiIgE6vB4vFhKnUAK22CHdu3sOo0ZGIuXwOBq0aHV5piYH9eqJbr0UV5tuLinFN0DVqZHOy90A06tYLQmBwbXB5Ipw8eRq+fgEoLixGaGgozp87By83JVRiAUo0mTh5OdPFk5pgRjWP6TzSmkwmp4YnEAic8XcPO5E9jMU7u3UceVgrr5rf7xzu/LZJThtexMBvy2yKO6Y4/27dv0xzet5sfCe2TyWOQO3Og75xzu/Eltmk4+BV1Jk9S0j7PkupTq0kJC6+CJ5+MljsDCSk5IHGZILQ2dCbrJAqVJDKFOAyWbDpDSjV6yESi1Gs1UKpUsHbxwsxV2Igl4mRk5OGjh0ioNepcf7sBbirWBg8qC/eeWfLHwvs2I5PiEaTD0JKMWS8qyhAVWR0/OgW5OrNe/ALrIVinQUpadmw2GhQqdyh1RkQ4BeAwsJC8Pk8qHNzIGTQkJUah6Qcq2vDqwrwT+nZv9jwKnJkPbVpNHl16M/PPZOfRnrcQ16tXTWOXLl6A4nJacgt0KBh42YQy5TYd+AIeAIxmjRviYysXFy/cQssFgcBvr7Iz0iFWCiAWlOE13r2RlJqGk6dOQ+ZQoZOnTri+PGjkMlFYLMcrTCNMJfqwGQSRLRuju+/PUP179WEnDx+FXIFDZkZdijkQM+eDfHtLzeee948JRmv0Gs+Wf46+eLLjfDw9kCzlu1x/eY9cPkSiMQylGj1SE1NhcViQXBwMCibFWn34yHg2HEjzqXhVQjoZ3SzM/CYx+M5NbvHFRJ4dGNzeHMfte9Vx5yfFIfniM1r3qdMa3keNL3TO+eSV/r9aU/buG4BychWY+vWrdBoiiCRiKDT6SARi1FSXAIOhwuKoiE1tQQhIQoMGzYMDpupXm/A9Ll/zSzxcwMJDgtFnfBG+OmXLQgKCQAoOkrNpZDLpeDzuUhJTUChRo26dULRvFkr7Nl5GEqlGxo0qI17sdfBYdth1KvRq2cnzFm4zbXpVUJIB/YKI9duPoCbZyCEEjeIpSokJKWhqLgESqUKBoMBQqEAcokMpVojLpw9Br0FLqwrgfXTfqRKGRZPU3t62sCU9339+7UgcQkZ8PbywNUrVxBeOxRJCQ/QpGF93LhyC1YzsHD+OExaUFY+v7zX9o2fkXHj34JfgD9U7l5ISEyGwWhAaFgIMjPSkZaWg0aN60Es9cCZM2fg7aUCk2kDm2mGrqQAPbu3w5ffnK3QO8s7t5fhvi8/mkyWvP8NWrdthcxcNax2Grg8PgICApCQkAC5XI7E+GS0a/0qThzfj7TsXBfW/wHBcJZ4dwQe/5sN79GqyP8Bmmpkise2zCJsDg/t+ix3Cvbni3uTm3FpiH6gRmaeDhSxQcBlQSUTIiU+GaYSYMO6Jeg3emmFFsL53QtIROSHzmd2bviUTJg8B8NGjkVmZh7OXohCvXr1oVKpcPfuHRQW61C/aSuoCzUoLMyBpbQY4XX8ER93DTy2DVeu6iv07hoB7j886KGtq8nnX/6AfLUWOkMpRo4ajV9//dVpZ1UoFCgpNsBoAOqFh+Kn335xYf0f4DV1fvdEEhH5XYWZFbVnPGnVpyx+72W7Vi8bTjZt3g4zOFAE1gdhcJGdkQo2nUAmYCOiRSMsWf5rtWBzYseXZPa8JchT6xBevykSktJRWKSFp5cPdEYTKBYHQokIJpMWanUW/HyUKC7MApNmxP375mqZw4vM36u7xpAmfctyyctzzZoynJw5FwUGkw2xRIHMjDx4echw7PTFco9Rnvf81+85ueddcu3abWg0WmzcdAIFBYBKBdSrH4qCAjXEYgn8/P1hNJtw89ZNpzPIy9MLHDYHXC4X/v5+SEtPwaEjh+Dh6QGVSglHoWKNWgO7lcBN6QYPD0+YTaUoKChwmhkUCjmCQ4Igl8tgMOhQUJCPBcs3UZ8ufZ0wWXTMWLCeog7vGk0cNiY6RQeTxgSdYiKi59OrWnJx30TSutefG+6e7wYQuUSCiCH/66C2dQJpMejPDJBLu6aTln3LyrSf3zWDRPStWa/k2X0LiIkAXXqXaV3dOgeThAeZkIg8kJqRBaWXEmabEWIBF0kP0rFy2TSMn15Wz686r+aBHNKwaTvojBRiH6TBYqPDxz8ALBYd0VeiEVanFho3bYSTJ46AzSZo36YRGtTxw/CxZRqp66oeBDb99D75af12UByJc9EGeKtw4cwhXLziclo8ivDEoS3JtRv34BcQBv/AMFy7eRsWmx1t27cHoSjEJ8QjLT0dTVs0h0AgREZyBnIzcyHgCmB2njjtCK0TjLTsVKRkJEEoFqDXa71w4exFZKZkQavWYdjQETh/4TLsBAgKCoRGo8GJk8dht9vw2ms9nCFeZ8+eQfMWzaDVFuPBgwdVM7Tu/+U10nPUgT8W1PEfh5BO4zY7/76wezpp87f+ERf2jydtev63tMKTuxeQDpEfUps2zyenzsRg396T8PWqBZXcDxaLGXfuXYKfnxJJCWnIzqsank9akpMHtSVnL96CrpQOqcIbObn5EIv4sNit4AoEKNKWgMGig0m3IS87Das+nI9xU1e6NrwnAVuB3z9aNJxs3HkUFFeOYm0JmtYPBpsy4PdtMS6c/4fjpm9mks9Xfw+rjQW9yQ6BWA6OQAgrIUhJTwNfwIdfgB8SU1IAGgMqhTv0hTpoNVp4u/tAKhajSKuBzlQMvoyHguJ86AxamAylUErdYNFZwbSzoJCpwORwIJPL4eit7dAC7969C3d3d5gtpcjNzUFISDCuX7+G9u3awbE6qSP7JhAaHBoeDQyKjnavVbzJzfXdE0mj/x2LL+2dRVr2Lss2OLhpGuELRGjf68laxoGtUwiPzcKrfcqefd6uiZPak/MXr8HfrzbOn4mGRKgAn8eGSELHvXtpKNL+dbO7uGc2ad1nVbXT0q1lKBFKvZCQkgU2RwA7IRCIRUjPzoa6qBD1G4RDr9MgN8PxBXXZ8Kpbjj58ZxjZdegceDJv5KvVsOgLMDCyM5Z/XPahd13ArnUzye69xyCTeyI9Mw+FJXqIpFJQDDrux8dD5a5CcGgwbt+JRYGmBLVC60LClyArNQv52QXOI63STQmBVIBsTTYaNmuEB/Hx0OuNYICF+rUaIjU+FblZ2fDyUaFAnYvIyEjExcU5j70On0R8/AMYDHr0698XUVEXodGoIZPJKqeRnNzxprPYQMcB/+x5PLbrHdK575M3uv+KgAQG0QibK4ZUrIS3h48zJCEu9haMpnxMnjQGI9/89KkJ/NBeEeTq9ViI5W7IKyiC0sMLWbm5kKsUsBErcrJS4angY+LYIZg66/n8gPxX+P73eTo0vA3bD4Mj9QSLzcHd69FYuWw+Jk53adKPYnVw09vETljQG8zgCYSwEYLIoX9mCO3asYzcvBMLBkuAK5evI+l+MigrBTHfYSqQQmcyIi07AzypEEarBTQGAzYrAZ8jQkG2BiF+IRDwWCjRZkDlJsG9e/cglUqdDr3CQg1oNMpZAYrJZDhPYjm52c58derg5gmERgE0GgGDRsCkU2DQKNBpQPOela+esubj0YSicZCdrUZCQgrU+YXo3LEL4h8kwm61Qy6Twc1LgWJLAfyCvPDmmDJt6ODOxaRHv+enYsjuTUvIys/WIqdAD1OpFXabFdZSC5o1roNb12Px/nuTMG5GWYbIo9flfTNIi141Z1/s260xkat8cPNuItgCCUwWKwQiAe7Hx0EsZKNp/VD4uYuw/OM/MzL+q5vM8zTvzT8uIl/+sAlaCxNePr7ISonDzZtpT+1j9zxhUVNzOb71K9Jp0FRqy6+ryNnLl7B1925IZEqMnzAZCXFJiL19DyKuCHXC/HD//gVYzIU4dCrrDx4c2rWCdO+7kPr2izcJh8N2anezF25y/k6d3TOH0CiCiN5VO37t/H02mTptFfwDeUhONcBoBARCCr4+wdCWGFGQXwQeR4DgoFBcOH8R7ip3ePqqQPHNyFVnwMNdhde6dkZYkD96Rc4ttwCd2TyetB9SZhc8v20modNpaNXvs3I//ySmrVr+Jlm15mdI5F4ID6+PjLQE0FCKgpwkvLdoNoaMqxpuT3r/P/2+bMEQsu63reDLfJCRUwiRVAaZQo7iYg04LAqwatG9Qyt8tnpvtWFR2bm+SM/tWr+MfPvzNuQUmSFXKqEvzEJ0dKIL4xpkskQG4u7jA7WmBKGBYXhw7wHatmqLhPtXoddmITET1PG9i0mn3k9WlKiTW2YQh3bn8Iow6HYwGHY071mxMJWuHX1IUZEWKSlFUKqEkEiVULl54/y5y+Dzpagf3gi3b8eCRjEBQgOdzoTNYoPJaoCFYQBhWFGkLoJCxoPVZEDvHp3QqF4YprxVlkv7LK6Tu2YQTQkdyz/+GToTC3bwkJiYDG9PAUQ8gmaNA/Hr1tvO+R3ZMJ50Hf70nTF9uoWS1q/2QdSVO7h26xYIKDCYDousBTyGFTMmjcK4Ca4qK9UpPxu/X+h0WtjZElhtNrAoHfbvczksqhPjv481cNCrxAIGUtOyYTaZ0aheA+hLtIi5dAyTxnXHO8v3l3ufKPeNp7dPJTbCQceBZbaq3799hxw5fgZHz5x37GEQCsVgMFjIyy2Al7cv7HYKTCYXpSYL+DxHupUeZrMVBkMpZFI5GjVshFuxt2CwGVC3fh2kpSaDIlYUq/PQIDwM9+/eQOsWjfDjhjPUkW2LSddHCmOe3TGWsFgUWvYqsyHu/uVtQtFo6DNyhfPvQ9uWEDqDQpe+75Wbvscx7OvPxpJvvt8DpXsYUtPy0LhRfaQm3oCbnI4DJx5UaezqEJAvVgwnSz7agODawUhMSYWntzfYHDa0xWpYjYWgWaxISqucnbY65vcijvHW+C5k+/7T8AwKR35BHoRcE25cK3jmsvAiYu2gacNPS8kPv22FBSzQGWwkJyahUYP6yM5Ih7c7H7sPXKsQ9s6bL++ZS1r0KdMEYvZNIs16ldmkonaPIa0i/xqUeWzHSmKxsrDo3RUwWgCWQIqA4FBEXbjoNApy2GzcvXsfnl4q50ozGIxw5Op6enjCarMiOTkFdAYDYpEYBZpCsHgCCMRiFDo8XqVGuKnkkAp5EPLZCPT3xImj+5GS+9dFe3bXbNKu759HyTUfTyfFJSXw8fPBnTu3kZ6ZhpKSIhQVFmDgwEiwGART31r/BzDn9swgbf+Xn/t3QTm1fRR5dUBZ1HxYEEh+EQNcvgcosCAScGAoTkOzRn7Ytv9OhYCuKYF0zLFIBwwYMhB2isKlS5echQYYKMX5s9nlnuOF7VNJmwFflfv+mqLneR/365WTyP7jl1BUSkOpxYBaQUJs3BDtwq2GGDd2WDty9vItcAQKqDy84OHmhmtXLqO4MB+ZmYYK406d2DiZsJh0tB34v+bXeycTUAQten1LXdo3hRQWG0BnCtFl8Bpq8vDm5NcN0eDx4fBywN0rEIZSAg5fiNzsLLCYjo1MAL2uGFxHyIZIABAbGHQKOp0WHB4H7h4eTiNiVnY2BEIpWGwxkpLSwWKxUS+8LpKTEmHQaUGnEcilQiQnJyM4UIlrsflO4o5t/4B0HlDm7XmlMYtEXTfj1S5NcefuXWRlG+HtwwdoBCIRH4RYkJ5aBH9fBpRSAd4YPggj3yzf0fOLla+T9VuOIr+QDoudAz+fALgrxIi5cACZ+c9XKaBB/eqRlIxMWAkglkigLSlCi6b18fXXpyssEDUkty/UsD4eTCJ2C0CxrgDzZw/F1EnPzvTyQgH7GGJWLh1Htuw8CoHUHRYbgb+fN/Lzs8Bi2HDwwJUKy3e5H1i7ajxJTVdj287DUKh84O0TjMxsNaJjrsPX18+ZPJ+ZngIel4EAf2+kJMcjPT0TYhEdNpsNhUWAQAT4+StQoitBZqYZbDYQFlIX6Wm5zqoizVu0xM2bt6FWq52xNG5uSlgtJpjNegQH+mDyxLHo+/pM55x/+mIa2X/4OOJS1BDLPeHu6YFSsxmXYy5DIBRgyNCB+OH7b6CQCdDw/9sD87OTMXHsCAwZ8VeHRtSut0irvp9Rl/fOJjZHDb7eq5wG0InT34dGB7h514KNsMGg0WAzlcBuzEZ8SsW/LE9TMLduWUQGDf6g3LyN3jedNO/1+PjLfdtmEyaDjm59P37seDH7J5Bm/+DNP71zOnmlX8XjOp8mVpV51+D+7YnBzkG+OgPNG7thzRcny411Zd73Mj/z45r5ZP3m/RDKPXHl2nVIJHzIpFy0al4Pqz6reDUg6vzuycRisUJbrHVuOl2Glx1hj22eThgMPqx2JtIy1Zg262sEhcpgNNOgM9phsVNgs7hQymUwm0wQ8DnITE+G2aRFndpBCPT3Qp06wc7Nz6FpSWUSdI6cT21dP58IxUJ0772I2rz+HRJ19jry84pxOfoKQDHh5u4NFoePlNR0MFmOwF4xLGYTKMoOS6kBsfE5zvlxAOLpw4FI4YM8jdZZSUQgEjmj3/VGPVq0aAqNOhd2iw5SERstm9bFmtVHyyWYv/84h8x951OIVT6w0QUIDK6FW9evol6YHzLir+FuvCugtzKL8OD298mly1eQmpaJzKwcZGZkwmQAWreuCzuxgc/joOdr3RE5rCyN73m8Vi9/kxw5cxW5xRbwhQww7Ok4ebLMhnd+73QS0bt8G/yXq14nHm4eGDD82Xj5n0dsHzen7b+tJOvW7waNLUJicjL4fDpYTDM6dWiB95dVvKw+dWbXONK+74/OjS8isqyCr+M6se0dYrezkZlTiK07DiAzrwgleisaNGkFdZEO5y5ehpeXGzpGNMXFc8fBpNMwftwozFrw10DXY1sWEUe+Z/u+ZVVDDm2dQ7oP+r9But99OI6cvxgDBlsETbERxXoTLDYKJrMNHTp2xHdr10Ihl8HXxxNnz12junZuThxJyBSDB75AjKTUVOdxLrx+OFLTUxF19iJ8g1Ro0rA2rkefw8ypYzHzrR+cczi+aQLpNPSvMYant0whrwz+mjq6dRnZtucEDp+OgdjNF+m5ahhLjRDymVAImUiPy4XB+mI6AmaPb0o2bL4CswWw2gG5ggVHgHlYaBAMOjXkMi56dmuHN9/aUKENaduGJU7eXr95F1m5BbBYCbhcIYQCPlgMCnaLGR4ebtBpi5Gfl4PWLZvhlXZtoC7IwcQ5ZTx7Xq7vP5tOVnz2PXhyb4SE+iGiqSfmzv/TPvxv85y3cDA5dOgQGjVsAC6Lid83nASXA0yeNBx37tyEn68nPltz5Lmi91njPn/6EHLs9FUU6SwQS8VQKvkoKkxHWIgb1m+oxJF29899iFQqB4POhdVKQ/u+f018j+wQTqKv3YVU6YVigwWlNgre/oHIzi+AxVQEmy4Pg/q9igb16mLKW+U3ep/eN5+80msldXH3XGK32BExcBV1fOu75MqNOJw8G41SOxMJyZnOSGx1UQkaN2nmXHwxMdHoG9kHe/fuhlFvRe1aISg1maEzGmEsNYEvEqB+/XA8iL8Hk7EE3h4yEIsWs6eNg1LMQsc+T47VadskiBjBg0dACBgCLu4/uAO5hIOc1PsoVRchLefF3PDqBYCIZe5w8wgCRRfi9p04eHl5IbxuGJITbyMnKx6NGwRiQL8u6Nz/o3ItzDHDI0hU9A1k5ujg7uUBvlDqTCeyExrclEokxMWCTtkRFhoMg16L1OQ0iPg0eLorERebCzYTWLb0TUyY93xsfJ++N4ps3XsSZpoAOp0aCXFProM3f+4gsn7LLgilcmcBUUf0P+xWeHl4ISUpDR7uHkhIiINcJkRSwj00qBeKXfuefRTAs97sHO9v1zSAeAc0QK5Gh8JiNXhcGxRyJiLa1MGcueX70DxKB3Vm7xukfe/HlzL6avl0smjJGnh4u0Oi9ERQrXAcPXkaZjsBTyyGkGeDgluICxfKjplOzXDnPNKxX5m9Z8vqSDJ4xu7HLowzu98i7SPL7GmnNwwnrwz/U2vY/NNccvdBOo6dugSjmUJBkQ4WG9C1a3enF9JRUdhRCaFhvXq4GXMFocEhMFstzsRkDp8HsUSI9PQU5/FaxKdjYN/uWLnq8fP4O1O/WjadvPvBGoAng8TdA2q9Bl6+SjyIvYVWjYPgxmNgx9775Vrsz4PAPGkOBzZNIq8NLfPKt6jLJ2yeB3LyTFC6B8Jh0khJSYFMxgdsWtBQAm1RNho39MHmg+n/isHKxa+T95dvRIs2taA12kBjCiCWuYFicFCiM8FqpyDk8dG4Xh3E3r7l3OwUcgm8PVVISXoAIZeFurWDseHXH0GsJgT4eWLShDF47Y1lzxT7fl1CicQ9GOkFekRdPANd0T9//DZ/N5ccOnURl67fg5nGRv1GzZxRCvn5eXBTKdGoYUMUFpTgxPHjeK1HF7BZFK5fi4LNokPDeqH4fcvVZ0rrk2TnafzuKQXp2HUASgw2JCXHQ6vLhJcHDwveHo+evZdUGB/q1M7hxOpw74ENiuKh08AyLW3GmK7k+3VH0Lp1E1y9fhdCqcIZ9uDj74vc/BzIlDLMnDYSI4ZMq/BL/wmoCzsnkzb9yo7Vh7YtJVojwarV3yFfo4fZRkdGVhHq1q+LjMwccLh86IpLQLOYoZJJIRA68vWsIDQrSkoK0LhhGBQyPuQiLrp2aIuOkU/u9LV//XySkWPF9r1nkKU2Qa0zwC/EH0wWgTo3CQWZqVgydzymzSmfp/dpCER1vmPUwMbE178+bt1JRVaOHkqltzP26V7sbXi4i9C8WW2cOrnLWZzg/aXTMGjU48tgdWsVQHwDQ5GamYPoazdRq05dZyZIVk6eswevo01lqdmGnMwsSPhcGLRaeHp6gkGnOZ1DjmMtZbcjL1eNurV9oVJKceP6Tei0wLSpfbB09Z5qk7mK4hfoBuIXWhcciQeSkmIxY2IvTJ72V/PI0Y3LyPIVnzsXqUDqicBa9ZBVoEGJTg82hwGZTOTU6EpNJrCYXDRr1hynTpyEwznkiPWMvXtfLli1AAAgAElEQVQTMpkA168+wMcfz8SkaU+vXFtF8ajp+9s3r0XslBg2Ggv+Ad7Q6zNx//4lxMVVrtYjdejngaT76DJvx6ndc8mrkZ9Q+7Z/SPoPWgCxhAOl0h96vRlCgSPExAK7TYvGjWqhdpgv3vmg/IUTKwvMsrc6k/xCEw4cOw8bxUdgrYZQuPsjPTMfXDYX6uxMiAV8ZGSkO5OIw8Lcoddm4I1h3dFrQPmOXY/ObXDfBuTCpTiY7QKw2BLnwlQqZPDzkqEoPwlD+nXAjEUVK9deWdqf9nOfLetPvv52ByxWwMMrACKRO3Q6q7PY6ImTR9G8RQNodTm4F5eIEW90wJdr/q93cmjPziQ+PgN0JhvunirwBHSUWooRdekiQkI9MKB/X1AUBb3OCAGPBy93N1hKLbhx/Q4uXboCDkeERo2bg05jI+5+PECjYDDpYbGZYDLrQGcSDBrSDxMnPptk/TA/BjFauIh4tStKDFnoNyAcY4b9+QGcPa0HKVLbwWbIcfnSbZSaCBo1bgJNYT7iE+6ARjciNNQbAiET0TGX0bBpY/j5B+PooTPIyyvGqDfG40F8EuKTEyBViMDkWsHjEezfdf2ZbfJPWw4fvu/wllXko1W/IiNHhyK9CbXrBMHNjQW7PR87d96qFB7U4XVDCddRwmnQWurUnoUkt7AUKz75FppiAoHQE1qtDUw6G0waQLMb0aZFHbzWPQL9Rz9ZY6oOoI7+NpmUGIGoqw+wYdtx0HlimAkHHt7BSExMgYTLgZ+3t/OIm5eXCjZLhxu3yh9w+/c5Th7/CjkfFQudgQVPzxCkJmVDJZfDXSlwFtecPrEHVn53sFJgVwceNT3Gr9+8SXbsOoT4hBzYCRdeXiHIzC5AcEgorl6Pho+fCr0iu+DQkV2IeiSwuUfnMHL82H0MG/w6Ll645qw9ZrMbEBCowJUr17BgwTDMW/z7v+K2/fs55Icff0dUdDa4XMDH3w8GkwUSuQwp6Smo1ygc9+7fRU6+FnXqeeGNof0wqwaKrf4bxq91DCc8kRcuX70JlZcAV6IS/qCpW88AkptpgKGEBjeZHyRCJYo0xc6G3RRlQv16QWjaLAxCIQ3Z2Yk4H3UeB48+AJcPtIuIQEGeEdExsejWoy94EjGir18GR0xBXZiK6ZOHY9qbL5emd2TLavLrxqMwWpkoNphQWJQLFkuHZk0D8NXX5Yu4+DsvqYu7ZpDWfVdTp3fPI69EfkwN7VubbN9zD0Gh7vDxdXTCSoJcKnfmuEpFTAyI7IiZ7z4b4AdHNiMHT8QgIKQOCIMHNosPQ1ExYu/cQdMmTZGaeh+BgVJcjk6q1IZ0dM8SsufgOVy/mYKk1CIolX7gsSVgUIAmPx2N6vth454TlRq7pjeq6hx/9+9vEw5Xhm795lFvjuxFrly5DZWHJ7JzssBkUaAx7bh6LdVhdy8zf0zvSu7fy4CmwAwhV4VaYeGIux8LFsuGu7GX8P3aT9Gt71sVwm3W2O7k1OkoNGjcHBcvxYDOYmHQ0KE4cuwYdAYdwmqHQZObjsG9X8Wk2X9GF1QnDo8bq0eHesTbPxxHT5zFa7264+uvfqR+/GkZmTr5XfTo1s6ZL16QW4DbN+/A3U2JJo3qoW6dYMz/4P9W1Hl0/I8XjySXL9+FROKNQ0fPIKxefdRqUBvHzx6Cj48MOVn3EXvNWCEMaxqLmh5/TP8IcvzsHfgF1YO7tw/u3L2KosJEjBndB8s/3FEpLKhzu2YTQjFQYjAjPjkbC97dDKUHAzoDBR/fUKSnZUMsEMJsKMaAyK5Y89OzKTd0fOcykpxZiC27DiNHrYfOZEed2g2Qk57trISq0RQiMNALWVmxSEnVVQqMA9vfIT//thtxCblIz9SCyZKCzRDA19sTcXevIiRAjui7L18poH3bviX9B07ClKkjsf/gfmeLQh6fjfMX46leveuR+/fjERwYjpTEbFB2Fvg8PmxWI+7GJmLG9D5YuabyNrf35o4g+w8edyaOC0QyKFQeMBrNkMrkMJtKoM5+gIRsK3Vi+3uk44Cq5U6XZwF3aVuXSBV+cPf2A58vxv3YB05bZOvWLbFjxyb4+XmAy6Hh1VdbITjIG32HLaiwLDap5UvoPAHyitRQeSmQmZMEkZCGVo1rY92PFQ/FKA9dz+M9Hy8aT06dvwuVZxCUHh6IjbuOjLSbuHX7yZ7xf6LnL8zo2EZF7t7Pg29AMHQGglIzcZZxosEGTW4aFi+cicFj5v/xzOVdM0mLvk9f26tTV0oYHBEoioOighLIpW7O/F1Hb9bsrPv4cPlbkPB16Dmw4sUv27ZQkPRsHWgMGeh0IUCYkIhEUEg5iLl0Ej+tXYo+wyvuHXoeBaoic1o0+3Vy/MQ5KJQeKCwqASgaBCI+snIzIFcqYDJY4ePp59RuOGwGAvzc0Kvnq+jevywzpirXh4tGkStX7yElJRee3kHQqHUoKTFBqZCgYf0A7Nr5K5ILSJXfU545Spggcjd3dOrWHVeib6EotxQ8Fg/h9YNxNzYaYikN565VPaQkyJtPFF5eECkkoLMIzp2LQcvGjtzyP+u+lWe+/+V7GgVJCMVWQlcKsLgcGIyFKNZk4KcfP0Bk/8qZ1KiDm98mDCYH2XnFePudz8EVClCss0Eic4fBYILJqIe3u9zZXT3qWupTEaonMWnFB2PI+k3bQAgbdHBhtzEhlbmhtNQITw8hxo7qg8iBcyo112YNhERfygRP4AmDgUBbYgIFO7zcJdDkJeKTD2ej97CXb8Nz8ETKBgkIcIeHR4BT42JyufDwdcexE6fg4+UBNsuRsyzG5ag7eGtWP3z4+c5/5EHUnpmkVZ/yfyxXvTeB/PDDRlgsbPh4h+DBgzTw+Tx4eMvAYJrh6yPFL9tqPsWrlo+cePuFQlNSDDelNwQ0GQoLNEhJi8Wo0f2w6NPyZVo8ScbnTx9ALly57rTlZedlof0rrXHmxCHcvv7yHGt7v9qQ2JkS5Km1kMilkEjYcFMy8eXX5QsxexzG1MFtiwmdycWxkxdx4NBZGErpsNiYzvQuR/WTvJw0eLuLER7mhV82n6/UJvIk5lbm9zGjWpLs7EJERd2Hr08gZDJPFJcUO43BOXmVz3Vt0VBE7JQQhlImiootYDB4zuKk2RkJMOpy8c78UZix8OXsQTqgSxhhsZUoLrY5N5578YlQ+rihXsO62LVzK0KD/RBe2x+tW9TD+GnVX4fv8yUTyeKl3yHIzx/9+w1H9NXryMrPhcVmRESbBsjPTcC2gzVXueTXL5eQPfvOgMbi4dTZU2jWtAnqh9TCb+t+xNsLp2Pm0urZ7B6uh/BafJKj0SOsbiiUShlsZj1eadkYs9+unhaglVl3T+uZnz+fSxYtXQXf4HrILyyG0k2B4qIsdO3UDJ9XwURC7d+xlGRm5uPipVs4dOQ8zFYOBCIFzBYblCoZ+FwKpYY8tGkahm9+PvNMN7xjO2aSzv2/oPZsnksKi/V4/4NvoHLzwL24bNSu3RgcDg8l2nw0axqMtT/+2U2tvEw6uXs5eff9T2GwsJCnNgEUH75+wSjUFMBmLkFJYQbemv465i+teA5feefwPN53Yd9s0qZXWc6nlAZSu3YtGI0s6Ett0NtN4AhY8PaSIy8nCekpWdA5lOIaurZ+t4y8t/hjtGjRHgWFRqj1FmgNOqiUPORlJ2DIoK5458Oa2RBWzJ9EDh+7AKPFBncvJbIzE4DSIix7bz7YHAod+lXumPVvUDVprCRcoQRcZ7xiEcJD/bH2p7M1hm8Nsa3Cw/78xUJy/EwMHBu+DRSCw/xxNeYsJo4biAlTKl/RnNq+cSFhMPnYuu0wzpy7DoHI09lL0tGrNu7+bTRqGIzUxOsYO7I3ps7+6Q+gr+4YQ5r0r/k4vH9C6uC2uSQ9S4P1G/fDz78ejp+4DLnMDRKpADIpHfsPVy5KfeSAxuTkuWsIq9sc6ZlqZ0+Ofn0jcfvGZVhMBXh9SGcseMk2vEd58EpDD5KZZYZCEQIv3xBcv++oVcZAp44tEX8/BvXreOOjVf98lK2w5D/mgWUzBpBVq7ejefMGCG/WCVGXr6JQkwlfbzkK8hJwLa76C3Ie3bqaLH1/NXLztWDzuAit44/4BzHwVNhwNKpyQbDlweL9xaPIgSMnUaIzgMumY/SIgZg26yvq+I53SKf+L06TrL9jMfH1buRM1HX4h9RGqc2KUrMWt2/eRElJ1T6mzg1sxdLXyXtLN4LBhDOHUunmg/SMdMgVAvj7SSEV2XHkaNwz/6qc3zOdRPT589iw9tOhZOmHm1C/cT0UFxFkZOTDx9cL3bq1weKllWuGPXFkG7Lv0AV4+oU5NzzHkVav00IlF0EpY8FmzUPUteJnjkV5FktN3DNzVBeSk2tHXFw+CrVmGOwmiOUcaNQpUEhpeJBYVknm9L4Z5JUabGL05XsjyE+/7oJAUctZHNJmMcKo18BSqkHTJrWwdn31hw+NiuxBaAwu7ty7A47ADoMhHVdum2pcFuqHK0jDRs1w4vhhzJr+JuYseDEzfR6V1wHd2hArxUKRwQSdSQexmIWwEE988/W+KuFN7dv5DiktpbBn71lER8eDwVLCYCTOvpCNmtTGju0/oFEDHxw7Vtao5MTWcaTjoOcn06BnR3fC4LohJaUEjuouCYlJePvtKVi89HPq0K45pHvfirVP/OjdweSX33dB4RmCvHy90xNsMVvAZhJ4uglxNeYmioxV+8rUxEb0tMb84v2x5NbtXNy9m+usntOuc1vcvXcFt25exJrP38WoN59ermuv9l4kQ01HcpoanTp0QKE6z9miMjszB999uwRsZin6DK94ts0/YSmjQFq0boM64SHYdeB3JGVYqrT4ysuz7p3qE62+FHKZGMXqdJy+VPnA+vK+81net3vdp2Tuwg8gVLjBUT1C56hDadchsuer+PSTysXfPaSH2rphMhk0/Btq3sz+5PMvdsDTKwDGUgpcAc9RoB10ZjEC/YQ4fjzlqTD370Bf2j2RtPxfk+/HMeHorvfIug17cfHiA9QNb4mLUZfRoEEwzl4ofypO9O65pFhfis7D1lCrV4wg73+0HkoPBfQmCmYzDTaLo5pHAJITbsNmNiL3XxLGn6WgPI13r1k+mnyxZjNYbG9weBJYYUJxSRYYdD2S02te23mUxmO7lpC5i9bABiEYNCZ8vHzKqm4L2UhPi8PR89XXTeyb5e+QM2di4OhpmpOfChtViAtXKh8PVhFezZs+lFy5dhtBgb4w6XOxfkflzDUVeeezvHfeuCEk5uZ9lJRaIPdSwmo3gs4w4diBG1Xeg/4YYPKYDmTrtlMQSQIhljo6hqeDxbGBySqGgG/CnFkjMXrs86PZPcqQHp38ybXrmfD2qQOpTIWYmNP4+ut3MGxkxQNRD+9YSiZOWwKuSA6KIYJarYebyhO6Eg3kUi7q1/VxlpxfsLSsz+XLeMl4ID4+tVFqoSBX8pCeFotlS+fgjfFPLr1VXXid3fMWadfnM2rW1C7kcsw9qAu00BSUICQoDHarBTdvJWDLppXoPfTPuNGqvDtYISCdO/fGpegoJKcm46u1CzB87NMpVPrlR3PI+t+3wmY1oUP7Jvjku0MvtOw1DfIiNI4YGr0BHgGeyMhOhErJxeVzVVe6qD2bxhAWW4br17Ow6rONEIh8YSNcCCUiuHmIUKC5Dy5Hh+FDu2DGzK3PJdAfLh1K1q8/DhZHBT+/EBRrc6BQmLF9Z8U6Gj1cEK2bSUiexgqRxBM2OwdpqRnObAu9tgCebjwE+Svx8+bLzyUWVVnU5X2WTYG4uatgMFggFNGQlaHGts0r0HvwwmeCybzZvUlaar6zerJEqAKNosHHWwVQBiz9pOI10x6HQ8taAcTbOwQ6QxG4AjvatAvDnEX/nhtcXjzLc1/zOt6E2M3o3LE5Vnxd/raE5Rn7ebpn05dLybadx1FktKIUgGeABzJzEhEQIMPv605VWb6oY7vHE0dMVWEhG2PHfwoPT39kZBc5O4BpDXkQS61gMovRrXNjfP55zcU4VRb0E7veJmo9DbNmfQLQpCjWlqJBgxDExV3BN19Ow+DhFXdevD27FzlyIgZypT+0OhtoFAva4kLwuTRo8pKhkLERdUNTZfArS/Ozfq5+HREpNYucucy+3hJwWKXYvv+vx42re6eRJr0rjn1laZOLQBrWr4WUpDz4+wci9u4tGAxmFFuqbm/d8fNyMnvWCqc9NyTMHzYUIXJABKbNqZnwl79jsHj6cHLz9n3cuXMNixdOwxuzyh+wXVk8n9Vz88b0I1dvJMLO5CE5KxMsIQOEZkSnTs3w7RdVbypPHdg0mHC4ChgMYnyyagOy8ynkFpjAFQggkDDB5RlgseaiTctQrPuxrPH083hFdm9B8jXE6TnkCWjw82MiooU/Zs0pX+7vsd/HkM7DysJsVn00gnz+5XrIFP7QaEqd6WXEZkNIsA/u3bkEJh2Y/dYIzFpYPdpDefE8+PtM0mPY/xX2I1vmERqDQuf+T6dk0uD+zUjM1WTwuEIo5WzEx8Uho6DqG0t5cXjcfdMndCB2IkR+vgkajQ5ymQxabT66d2+NqbMrH7fleNfKJW+SS1FxEApk0BsKQaPrse3A07OjRTTyJXXCGyH+/h2ciq4+u2RV8K6pZxdMeoNcuHQHr3TujsMnjkDmxoemKA0D+3fC3NlV9047F/iFAxNIm9fWUotm9Sdffbsb4Y3aITUrF5k5qQgKVUJTkAIBB3D0yn5I6In140nHEVWfQFWBO755Euk05FvKYVfiS73AFqtQrMsHIRkYPbwzPvnwWIU36V1b55NRY1cirFagM8CWzRKjuMiRxZGK4cN7IfZuFG7cSEeJoQyPszsnkHb9/loEsjJ0Xdg2n7QZuJLa/s10UqK3YOvOvbh4KRNMLqAzAHQ24BuggKO7WmaWo8Q+QJmBiJb1cOvubXD5FJq2bOSsQ1e/YQOYjCZYLTZoCgrh6+UNAZeLEeMqbtf8Oy2ffjSF7NpzCjq9EXSaCSlJ2dBUMT6qMng9+szmdXPJpGmfoFHzZrh5JxHeXn7QFmsAazGS0osqLAOPjj1hXHdy4cJ1MOhs6ErUaN6kLjbuenomDW8vFlG4eUIkYOLs2fgq0VJVnGvy+V++XEQ+W7MZTI7c2Vg+INALem06UlPy8Nu6WRg8quK58X+fL3V2V3/Sru8O6syeeaR9n4+peqEeRKwIQkJqFkQyEexEB2IrBJ9tRI/OrfDRJ9Uf31QdIPIpEI+AIHBlnqCxbSgquosObcPw83ePP4Zf2juLtOxdBmDUvmmkVa+/Hr/entuL/LZ+P8QiX9jtHBQV6eDj4wadPhtyORNqTRpGDuuBhYvLauOd2j6JvDrg30sAlYfO01sWk/GT3geNCWfjYQuYyMpTo3W7V52l9Xfs3Ic6Deuibbv2OHroGER0IaymUjRr1RhnL5wAk0uDWCpEgVqNvLwC5yI1G81oEF4PVy/fAGUHInu1RmryAwwc0BVhYVL0HFr+XiQOGlavmkkWLfkCCqUETLoNFrMW33z5Abr3qf5Mg/Jg9vCeSWO7EqbAEykZhVCpvGHUlsBQlIte3dpgzNTFld4o2rcLJFqDDZ7unshMTcLAvj2w8P2fKz1eRWj64bvF5Mzlq0jLzETr5vXx0Qe/PZX3VmSO1XXvpDd6kVwNA1yBCmaLBYWaDDRp4I2oc7tx9kr1mJCoU1v6E9B40Bm5MFrF+HT1RiSkFkLq5gu+UOgsukdsOiikdFC2EowZPgDT5j49Y215wZRxQYQKf6gNNijcRDCVpqBuiBgnDle+ukTDelIiEvsiMSEbXl5+4HLZuHX7EgQCCrXqeCMnIxm371X9KBe9eyqxEwZa/q/yzIgePiQxORtZuVYIpRwU6UzORH02j48inQ42O4FEJgPf0eEtSwtHEySTxYghrw/E+k2/QaaQoV79hrhz+x5q16oHo96I8Fq1AbsFh/fvQqmxGK1bNXb2T7iXUfH579zyMRk0dB78/KQYOmQQrl09j7GjBqD/62Wd6Z7V9daUPuTA8WjkFhjB5YlhM5sR6K0AizLg7JWKHwUPbJlHXhv8MeXvRydsjgQsJgsFudmYPWM8GoYHg27X4dV+NeuZrh0iJY4GziaLHlMnvYHZMyv2cfo3Xvy0sjeh6EyMmVO12Lbq4neAG5sIZaGwgQtfXx/nhldYcB+ffTwfrw2seJmtx82LOr5hAOk0fDt1Yts00nHgl9T4N14l0dcTkZpVCF//YDi6XRQV5sHbUwIWzQQ3KQfjRg1B58h3qXN75pO2fZ6O3ehJoHZp14hYKBlSsgshU4nA52shE5nxxsCO6DuofEbe41snkE6D1lJHds8hXSM/pRbO608uXoyFwUhBJvPAnbt34OvrgfT0eEhkHBRr1Bg2qAM+/LxmqnTs+30h0RrMWPvjOlyO0cDbD2jRuhVKtDrExt2DrtiKti06obhQj8SURGc7SblSDr3BCKuNglzu7rQ/lhRrkZOZCR9PN2e7RSGfAQ4HcFOxcehcRqU2qWaNvIhWZ4anhzfS0+LRukVd/Lrl6R3zHicPe7d8QpZ9vBYUUwweXwohn4+stAcoyktBYlblq4x0fCWEmCyOXhv5cJOLcO5S1cMjniTPD39vWt+f1KpfFxTTiPU/V6+c9WrJJTwhC/0GRGLwhKfjgPknug9tXkk+XbMeJQYOirRm+Pp5QySg4+jBfdBXY1tU6sL2sUSrN6HbG79TR7fPI10GfEw1a6wkV27kIzDEDxYrDdoSHUQCDoQ8GkoN+WjRtDY2bHt6RtsnCcfit/qTb37YA77EDxRbCDvMAE0DW2kOFs0djUlTK3/8iGjpRRyFQIuKSvEgPhkdO3ZAXn4O7t27hQb1Q8CiGTF65CAMG1c1w/iTaHz09xXv9yeHDx9GWqoeMrECDIYAAUGBeBCfAIOx1NnM3JEPfe3qHcik7lAXFDo7hFHEjPbtm+Ne7FXEx9+CgG9BTGzlKsts3fAJ+fmXLUhMyEBwkD/clBz8vPl0pTbPitD+b/fu27ySTJy5GBRTAqudDpFQALrdBENRFtZ8tgR9hr9b4fkd3P0pWfrBZ3DYVB0phlIhBzdjq2YTLC+9k17vRc7H3IJEJcTF6DuwVYPH+eG7v3n/DfLzz7+CJwB6R3bA7GXVu5mWl8aH940bFEES04pgMHOhM1nB4bCcaYI9OjdDh3bh6NKvekKe/hCAQ7+NId1Hlnkpf//tbbJsxdew2vkAJQKdznUawI36QgQGlDkxpk0bjWnVqF5XFKBH7+/aNoRcuZEO/6AGECs8oSnOh06fCToKMXJIV7yz+K+e2pg9s0mzPo/v+H5u93TSNvLPfN0Plgwlq9dscvZ0KCw0gMHkorhEBy8vT6jVWWBQBtisJXiQ/HQKUJ7at5C82muFk08bf5hMkpI1uHU7Hrl5+bgcnQY7ARo1DsO9uFSwmCKIhCqUFOvgrlJBp9NApRLAZNIANBOYdC2uxlZe82lc14PQaBLk5+WBz7Ph9SE9sOjDZ1tJpkO7cMLiK2EyEzBoFIL9PJGXlYDXB3bDgNGVO3K3aelLGCw+3N3dkJWWgHOXKqcVV1TGVWwaadm2AwjDiIBQDtasqT77easwCWnapDFu3j6J8ROGYvjUZxtI369LQ1JQZIVUFQA76GBz6Ei4fx0rls5Cj37V1xmROrpuFOEJBKBYNJjsdnTsV2Yj+GDZCPLd91vB5XnDYmHBYgYMBi18vCVITbuLdu0bYczwXug7sHJCVFHm/9P9az+dShYs/gosjhQiqSe0RkeHcoftMR00SosWjYOwe++dCn/ZH31faCCIr18tlJoZSE7OcXpI+/YdiBMnj4DJKIVSwUOzZvXw24ZnX7Zn++8rydtvL4W3dy0YDY7uYDb4+QVDo1EDlAUpqXcR2a8rjh3fi1Ufv4M+g96pNDbN67mTZs274/z5S9AWZ4LDMiE2zUyd27uItO39QaXHraxsbPh2Pnl7qaOIrQo2wkBRoQYB3irkZCRgyoShaNQgCN0Hl39ep/a8T4x2Hl4fMcdpwvD29kJhQQbuPiitcdq2rv2IzJm7En5BIUjPjkVKTuXaFjwOy29WTiBLl6zFK+1bICc7Bmdu2Wucnn/j6ZEtn5P3lq9CQkouRHIP8PgCcDl0FKrT8c68iRg1qfryof8PoSf3TSWaEjMGDPue+nD5BHLw0BWkpRVDKFQ5jeFp6Q/AZFvAYBrRrmUtrP3+2R5jFswYRH7fehihYY1QoNEhPjEV3Xp0Q0ZmIvJyk5CdmYv1Py/FwKFLqBO755GOkWVNwv9+nd84nUS8/vgCjp8sH0x++GkLJDJf2G085BcYwOaIIJfLYLPrUVCQDi6HoF+/rlj2wZ8NxSu7cKv63M/fLiCTJ33obCCTn68HKBYsViukciFEEiau34xGkbbizoq/z6t7u1CSkW1D8+YRSE2JRfz9K5g1YxRmvlt5E0JVaP/uk6lk5Zp18A0Kh8rNG+qCfFgMRchOf4Bvv1yBzn1nV2phv9ImkNgpBvS6EkjFLBw/U/N9TUZGdiRiUSAOHzkC3wARTlyq2kf7UVwbhnCJQl7HGdc3cXwXLFhZtQokVeHZw2f7dW9DktKyYKMxoVQpnY41NzkPO/dVb7IDdWHzDGIs1YPQreAKOYjo8x11fO9UQmgSdO75AbVk4RTywYqvoVIFwMPTG3Q2QVpmPDy8JJDwrVg0fQQ6Rf5zbNfVvZNJk95V6yp1acsbpOXgX6kre2aRpn3+GoszZVxfsn7zfigU7tBqjQDFhJuHB7KzM9ChYwQSEq6hXUR9rF5dtSjtOdM7kT37TiM8vA0MBjriE7Kc3tLWEU1x5eoF6PVq6HRa9OvbCr/8ElWphVUdgvPoGN0jwoneQDk9ljl5+dAaitG6bRPcjr2Cmzernvh+eOtnZPKM5SgusaB9u+bQqKTH/twAACAASURBVNOhUafiVuLTLSLgoPnM9g/Imeg72Lb/NGw0LihHzFxxEZRSPjJTErFi6QyMnr66wnzZt2UlGf3mfDRqUhemUj3atWmM5Z/UbL0/Bz1iBkhIYH0I+DwIRaXYd6b8xTD+TY5+/X46WfnROkjEwZCK2ejZLQyT3n62oS7TRw8kZy5egpu3N27duwuhkAeJkI2YK9XfUoI6t2kaaTv031OAfln7OVm85BOIpUr4hwQgOS0BeQUZcFOwoeRa0L9XZ0xZWL6Mhupc1B8tGkHWrt/vbMydmZEOq9mKrl174NChY5AplChQ50Ku4MLNjYtRI3ti3NiqBS62biImCkUwEpMKwGTKnLaGfE0u5CoxAoO8cfv2ZdAoPWZMH4Pp076r8OKqTmwejtWjbV1y7cY9+PoHolhf4qxuwhUy0SuyKz6shkKmQyM7kaSUPHh7ucGgL0RBXhomTRyBMdMfbyOtCRofjvnVx9PJiYs3oDXaYSq1Ql2QhxA/DxhKcjBvxjh0GVC50IZ2LXwJly+C1WZC396dMHV2zfL2lx/eIls3XkJupg6dO7ZH08Y+GPDmvGqRp4F9w0lsbA5CgpvCWlqM/cef/cd5woiBJComBgaLEVKlBNk5afByk+Hy5cxqoflRmaMO/fgGEUkEaDPg6/97vN29kHSILDOQvz1zLFnz9U+o17ABcgryYadsEAtZ4NGMaNuyMVZ9W7nGuBVdAAc2LCAMtgiXoq9jxSdbEdogBDQmFxQpRVZGOgJ8A2GxAGnp+fDy8YbFrgdoWkyZMhBTKtHI+PyBaSTitS+pE3vfJh17f0S1aqQgFjMXEomfs1rI/eQEcPhsSKV8cLg0ZGY+QFZmKdq1dcPpU1XXoiqKz9/vP7jlY/Le0pWQqf5fe98dHlWxv/+ebdnessluek/oSO9VsCIkNBWQJtI7AoIgoigoV1HAXhBEQFooXgVBOiT0XkJJ78kmm+19fr89K8JVvGkbiN979nnyR55zzpyZd2Y+59Pm/QRBW1EBm9sGroCN+IbR2LjmeK0X1IRRfcjFK+nIzMiESqmgKYxu3riIGdNewZgZ1deoajPeES90JZdv5YLFE4PH81Kic4gFfiwrjp8tpg5vX0C69a+6D+9uXzq0CCL5RcUwmVxYvfJ1vDCibpmGu3QKIlJhHAqyy5CedgWrPn4dL02r/Tu//mwKWfLuKsgUoTCZCbp2fAzfrqt+KYTazNGfnx0z6EmScvY6RHIRyo1lCIlQg8124uqFa7RWPnqcb9cQveBTkycTikXQrt9fhd79HZw99QWy+vNNSGgYC6FEivJyLUR8CtmZmejZrR02bb+Xh/XLpplEyGehW+LfE3Ae3z2DdHqu6lrX3k0LyZMvvE1tWvMOGT7qdTRqHoXMwjLwhQL4y/1AHFbkZBRBHaiBH18FvckCi9MMDs+CyVMHIT5KjsFJ94rLnNk+kbTuXzVz+9CuGaR73xXUvzcvIQsW/AtSSSgqjE64OVxQXDZkcjEdWSoozIJSLoRCIcBv+85i7beLMGDwow3sLJieSDZt3QmJUgWLy4HSch34YhZyb9beWf3vbR+SGbPfpItvX7+ahujIaNgsBuTm3MTtwtq3X53N1bq5hpRbCGQeqn+5Am6HFZRdD5ZTj99SaqYt7Nu+gry24G0YLVYUFZuhM9Xe91nZmFq2VJMQTRNolEHYuH49dm1egZ6DZ9T649S/b0tyJ7MAEdFxOHPuApa+PRcjRtQ8aFXZOKpyPUzGI+FxTREZFwazowLZebdRXp6PFk3isW3r9VqP+c99oFJ2TCBWq5Wm1On2vLdmxemdU0ibfv9p5u5PXkicEOLLbzajsNiI8MiGuJ52CxSHApdDobQ4D/0Tn8YHK+omvH14xxLSLXEBtfn75WTUK7MRGhkBrkCMnOJiOvrYIEaNvMzbkPDFsNtYaNy4E06dvQy2kAsHKjBgYCd0bh+Lof2rx4D8oEnb8Pk8Mnb8UoSEqsASSCGWK1FQkA+JTAyeHweZmbchk/IRGCBHXk42+vXpjs+/frTBned6JxDPodwCrRYsHg9Xb9yBqcQ3mzcpsQW5nZYDAU+GzPRcREVEQq1WoG3bxliw9F4dlKpsgJreM3dqEvlxxx6oQmJhtrlg0OvBY7kh47vRolEkvtmUWqPN88GiUeSLb9dDGRAInh+FIyl1m5Iyf/4QcuDwWZQU6CHk8JD4ZFe8var2JBU/freCvPHWUjhZgCZUjbziHLy3ZC6e718zM7+m83T/c1Of70r2HU9DVIOmuHTjHMKiAlBUmomcbBu++nQ6Ro6o2oGB6vSF2rdhCOk9pHq5U0MHdSVHj16CSB4InkSK/MI8yCR8UG4LrIZi9O7ZEd9+7/uSjjs2/Yu8OPxVhEVHw0n5QVuhR3hMGEpKMmHRFyJMLUWT+Ea4eC4NfEEYhJIA5GmLUKLLRngUFxPGJGLqy77ZgGMHdyGHjp5FSHQjOlFSJJYgItLDFm0Fm03Rf4cO7kXrlk0QHhoAIdeNZR9562ke3TmfdOnndRXU9e/A9tdJz/7vUKuWTyKLlnyCmAZR4EukyMjJxuih/fDWG7UvObl23Xwyb/a7iI1qhLycMoSFRNBFssX/n7VmzeaHQ1b5WCM5YfHliGnUEulZObh98yYClWK4zKVIz6p5Gsm8SX3JwWMnwRMI8ewzvTD3ja/qdN76PNeWhEU2xKmUM7h97SoqfieoqO06SXqyN8nIKwBPxIaDZUZRWQZWfbgYA559NBrej/+aSr5Ztw2B0a3Alytx7ORv0ITKUVaRjUun6q5mDJX81bNEJlVCLJABLjbaJt2zmU/8OIKA7ULHgf+ZavHLtpVkyvTF8BNrwJEqoFIHolxbAJO+GE0aRNAJgwkxQdicfM1ni2PnD0vIoGEL0L5LG1y4lgG+JAB8sQD5xZmIiwnE9XM38NEHU8ByAosXrYLKPwrF5TaExcfCRspRpruCtxZOxIRh/91s//PCOrN7PGn93IOd1JOGPUl2/HIEclUIXdbSbLODYnHAZnPA4bBht5lgs+mhlPnBpC/FsiWvYegrvsspqu4mEAtA4htFQ2+xIygsFDyix2+/+maOOGyQFk2jEayOw7EjqVAqZKAoC7p2bYqnn2qHASPqjh34o3fGkqUrvkRwZDQ4QiVKtDroyrWIDAlEYWYaCmuhyQ58ohG5k10AnUGPmTMmYcqrvq09e/8cfr16Llnwzko0aNoK2RnpCJQJkFqDM8APWhdqqZoEhYfDzbWC8Ey4fD0dG7+fjxcf0of3z30a1jWO3MnWotTmB5kmCC6WCQUlt2E0u2CsQ6qxGguknev/RYaMfBX+kTH0BhIKuPD47OCyICIsAOm3r8BmduKD9+bixRHe87a/Jc8jbBZB937eTf/b1unk8YGVq62fLB1P3l3+OcJjGqDc5IbFyYLZRjBqzEis+W4V5FJg5tThmDBqGbVtyxKy8Ydd0OuFOHn2BgQKOYLD5bhyLRVLFg3HnCm+DcE/1/sxkpFTQp9d5XCFsNpcEAqlkMuVoCgCvV4Lk7EMTRpF4rd9R/Fk74bYvMv3von7F9TZXZNIq75/FezTJyaRXXsOo0xvw2Ot2yE3/QJu3/QNC0XPLpHEYuShUcN2IG42Llw8DYEAYLONaNeuMZav9rLK1MXvrUVjSerpi+AKJLhxKxM8PwFkYgFC1QqcSzmAZYvnoP/Imjn9e3WIIVYXBZPFhInjR+OVSTVrpyrjXvHOdLJh2z44CAWBH0FK6lWfYLZg+mSycfNeNGrWHBfTTqFl+wRcv5OKtFMGn7RflbHdf8/SSQPJwUPHwJeo4OBKYCVu8ARu2JxlmDx1JAYmVf8IYFX7QA/4+LbxhM3ion2S1293esd04nY50W5A5cwMLBZIQrN4OFxOmM0muFwuqNWBoOCG22VDubYIzzz1OL787K/Jjft3vk569XvwAvps+SySnpGHlJPncOXaLfD4YogkMticLrgpFpwuFwQCNvy4VmTe0WLnjvfw7HPe+gUJ0QKiCmgCbbkDFqcTqgAR3O4CSPhGHDnqmw1+Ysds0jHRGwDp+0wTcunyDajVEfD3D8W1q3fgcFAQiiTQVZTD5XYgOFgJDs+FrKw7YHOBGTNHonF8FBKTFj20RTd5dG8iD4zG4RMXUaZ3wKTLQWZmsU/eP2/SYLJ1y2+QSDWQ+yuRkXMN7Tu1gFabC4O+HPExMVj74wmfvMuD+f4t00mvQd6PZViMmnjWnVwmh8lghFTIh8VQDo1KgoljXsKQCTXXLvkckLCoMKhUCgzq/yxmzq55W5Vtyic7NCUstj9i4+OwcctXKPVBcrjnndFBIhIa3hR2J1BuLIUiUIjL1y/BVIea1N+N9eD3H5JZcxYgJDwKOqMJerMRApEfKgwleHPxPDw/sm4DfNTP3wwlUqkUnQfVjMvtx7WLyYzX3obZ5oQ6KBh+AgnuZGTB6QYEAj6CgzW4eeMGuCygdct4EIcZ7do8hg8++ola8f5IcuXqJTRo2BhikQJZWYVgUQKUlOhx+PBJGIw2xMY1QHZOLoRCIco9hI6UCwFqFUxmI+QSHvwlbCQ+9zgm3Zcb1aNTFLl8pQTNW3ZDQXEJtGV5kIoteOrxx7Dq8wPUieSZpGOSbw/7D3u+BTlz5iqCNDGQyYJw+fIt+KuC6MP8brghU4hhc5jAE1C4eecqPLZ3TJgGvdq1gUYhw7RFdX9CY+GrSWRT8n4QjhwBQdEozk3D8vnD0H907QM5P69fQVasWAuLnQ2hVIzQaH+cu3AScoUEbBYHMrESAi6FH5IP+0zoeTbV9xs/JZ98vRFOmwXEYUNEsAYCDnDiyH7MmjYBk+dXLQr/oA26ef1K8uEnX0EgkSIkRI31a+ou4fibj6aTb778CboyJ0QSPzRq5o+122r/gXhnwYvkiy+3QyYPR6AmDDaXFSXl+cjIycSm799D/0TfFDmqTJjfvT5pYG+SX1wBncGGouICtGqdgMuXT2PWzCl4afK9DIqqtlfd+6g9a14iEokEXC4XDocDHA4H7X4/PJ+aPIXc1foqa3jJ/BHkmzXfI7ZBU2TmFCA0MhY5+QXwV6lQXl4Om9UEsYiPgEAFCvKzkZdTivg4DSwmM3Q6PTTqEJiMNjqpV6GQIjAwCFabA+np6WjctDEKC/PA4hBweRRAOREWEYLigmyMHfo85i3yOt53bHyVJL74L2rki13Inl8vQq6MgCJADZ2+CCKhCQEKN37ZW3fUPi8PbUW2bz8LiYSCSKSC1Q7IFZ4E6HJI5DKUlpUgOFQDp9uOopJCKKUChAZIYKrQAsSNFi2a4bPvfB/suX/u1KEg8oAgBIfHI+Paecwb2w/jZv81Cnhy11TSrm/V/VX//v4dMv/1D0FxZCBsDmxuEygOoTXx4sJSqFWhkAj9kJlxE1of1vUdNSqRXL5VAJvFiMjQIJQW5iAv6xZKC93YsG4REofXXGPo82xjkpaZh5j4BOz55SSWLH4ZC+b6Juj15/00+MmWxKTnwmolKChKx7X8Up98GAY+05TYHBLkF1hQ7qHo5oCmT3O6Dej7THssfrP2QavKZMPd6yP6tiIXr+QgOKwhTp+9jkYNY1BRdgMB/jzsO/Vwclap41snEo+g4/P5f5i0VR2A575jm+eTzoO9Ecc3p/QlKz/dheAIOcoMVogVKhAWBzy+EGm3bkLpr4RSpUBhUT78lQooZHII/fg4f+Y8AlRqqD2MsnmFEArECA4Jhd5QAa22BGaLHnKFkP7y2ex6FJUUQaORY+TQF/DqtL8GFJa/9TLZ/fNZFGntMNmcCI/U4PqNo+jULg4/7b5J9zVly0zSYVDVtLyUHVNIh8SqFaTZ8NV0sv6H7VB4iAyMThSVVMBidaJp85YoKCpGbn4BJDIpAtVqlJTkQ1uche7dOuL8ubPIySlCq1ZxcDosMJt0eHn0MLwysWpZ/fuSXyO9k+4FRA5un0p69P+rwGrVWU10Zgfk/kEwFOdi8uBemPq6bwggP1gwnhxNuQp1cAS+37QeLdu0grasAnw/MSJCYlFaUgS4jLh69TxemzcYc9/0VsE78P0rpOdL1Y9+HtjyNpn59jfQWVkQC7jQluTRZAHEaUTKudqb6okDmpFbWXnQG43wV8lw4Vjt23zQ3tq+9g2y/L210ATGoKTYE1W+hHwf5ftFa0Bate4Nh1OInPxSGCwGhEeFIK/gNnIyb8HoI7O5Mpkxd1pPUlziRE6eCWU6T5ECEeKig7F39xZ89+0CJA6vfkJ4Ze980HXq4IYxxG63g8fjoccQb93Zkzumeng//9D0qttwggqkcYtWuJNTCDZfAovDjbYdO+N4aiq0Oh3iGzbA+fPnoFKqwCVs8Hl8lJfr6D6EhoSiQqdDYVEB+H4cOscuLj4cFy+dhkTGgZs4YbMDU6YMxptzvBtm/9b5pNfAd6n9ybNIr6QPqBXvjCbvvrcWMQmtkZVfjFbtmsPuyMfttFNIv+mb3LPKMPFY7wYjIFf6o7TMDLHYHxRbAIPJDoeTIDwiGpevXqYP9Id5KIyKiwA4Qdw2PNa8MUymcricZvgrRWjWtAHmzasdy/TGTYuIzmTFjp/34NL1NLD9BOA5zVg5fzL6vOi7Y2BSFkiHzh1QVFaOm3cyoVaHwe3iQMCTQiwSwWGvgIDvQGbmJYgFwIqlr+K5kdU3qb9bNpwseX8dVNGNYHKwYDXrwOM4YbeWIyZSjT0Ha3/Av9/AluTUhcuITYijKfGzrzl9onX9ee2MG/osSTl+lSaZVSgliI1TYfln1UsV+7v12DhCQCiWCkpVFKSKAGTn50EZKAcoGziUAfm3z+BaZt3uiQ8/fIGsWbsdgeoY5OUbYTQQtG3bDRfPHMHwQZ2w6P1NdYLrAwXef9u4Kdsnkw6/00X9t/uubZlGGg36mDq1cQ5p++L71E9fzSfrNiWDcMW4ejsLZgcBTyiBAyyU6w0wW23wE/AR4B8Ic4UZoUEhMBgM0OsrwONwwPfjws+PDavVCF1FEUQiz0kGLizWcrRs3Qj9k57FoN8jvQ/q1+5Nb5FRY95As1bdYHOzcf3WFegMxejeJQoTRg/EoMF17yvw9OubFaPIx6vXQK5Q07V+ReIAiET+OH/xOlQBwRCKpbhy8wYoDguRURGIjgrHoUP7IRb7IVAlA9+PhUuXzoFFAUIh0LbtY1D5yyES+cFqMUGuVIFQAqSn54DH5qK0qBjHDl+FvwLo0b0bpBIJftm7F4TFgtFqg7YCiIyVwwECiUIByqJDYqemWPzhUXrBpe6YQdonVv3ky4Ow79IilGTnVyBp0FDs+fUQgkNjEBoShbQbGTAZDMjNvY3wMCUGDHgca75dBZfVjZyy6m24T5dNIMuXf4bQiHCYXEIYzXbYrOUIVIkQExWIp5/ohBHjq6aR3z+GdV9MJ6mp57Bj9xG4OYCfWAqeWAKzxYSXXnoeyxbUvlDTgzDr90RPUlxkQlFRMcIjgjDmlYEYNmZmrYXAb9veJss/WIuCIisUqggEBkXg6o3rcMIJiUyARgnhuJC6Hw1iQ7D5Z99EhO8f3/r108j02R+jVZtYpKVlwWJxo03rjhAKZNi88ScsXTIT8+ZXzcqqTMGo6nVq1xfP0z48Pz8/+pmOA6qXp/bfXrT5iwWkoFSP91esgpPioEGTZrA53XCzWLA7nNDrDLCbnbAYLQgK0kCpkNOmndvtQHx8FG7dvInYWA1KSwuh0QgxdNgAzJhZedb53m1LyJgJb8LNVkKs1EBvLkdsggqlxTcxa/JLGDO6amZiVUG8e1/KtnGkw4C/borVy4aTbdv34sKlIoRHhIDLk0ImC4Cb8kNc4xY4ciIFWVmZiImOQqtWj8HhsKCwIBcWs8e3qYJMKsLtO2l05NuT1uM50me1mNGrd29cunYdVqsd8bHx8JQzO3niJO0miImKRlFhIRRKJfTGCigDFMgpyKbPQDuIA2AB+RmF6NjAD/8+XvPE3LtjP7BlKuk5aCW15du3ySsTFyI4PApmO+DHl8Fo9JS6ZMHtcqBZswTcuHEOBAYoFGK4QaGkpASdOnXAti2HqUlTBxDAjU9WJtMbfvP2FWRwf++xqkWLJ5PTp8+huEgLvd4IsUgKiUgOnbYUPJ4Tly9l4NPV0zB6YtXPX44b/gTJK6zA5YsXoVT6IyGhIdzgILdICz+RBIVaLTRBgSBuC/o+3QMzp71da0F0/7paMG8aST15HXw/CfLy8mG3GTHspX6Yt8A3Jp5GAKIJCQAoCe1X97Clm20WlOlKIBTwEBIYiGuXz0MkZmHWrAkYWkOS1D/vlVUrXyWffb0WoeHRyM3Ph1KpAItyQ1uSj4w7ZRj7ch989NHDLyhOHdsygfbheczJTgOrH9E6t3UMaTnQawo/6Hdg+3LSs/9sqlm8gOQXWtCuYzNcunoNRpMTgYFKcCg+crLyIREL0KxZU+Tn5SA7qwDR0YHg8Sh06tAaQiEbby1/ML3Trz+MI08M/U8hs3frYjJ5xnuwESkoPym4QjZc0KKooBirP5iC0SNWUb/8OJM8/fzD/bosf/Ml8t3aLTCa3PBXBqGkzATP6QBNcDg4XA7OnD6N8LBQ6HTlEAr4cDrt4HLYYLMouFxOyGUy6MrLaepyEAIWl4Kb5YTZagab4kDEF4NyUXDZXfBQH3s0w4AAFc6eO4WmzRsCbCfSbl+jfTgJDePgMGmxYYvvaw2/NKgHOZZ6BQmNWsLmpJCVnUsXQGKz3SgpyYGuvBSRUUqw2Gxw+EqYLA6U67To0+dZnD17GkVFhVD6K+Dv74+0tJvQ5pugUEsQG5OArKwcWC129Or1BHIyMlBekEfXmbBZy3DmSvUc3/MnPkWS91xEi1bdaf9iZnoGCPEUSPJHdl4RAoND4CcWQS6XoKQoB9qiTIwZloQ3l9U8cPHJiqlk5erPIZLK0bJ1F6SeugGbgwOb1YkAlQq5OVlwOPRgsy0ICOChf2IPLFniPaFTm1/P9g3IzdtZkCiUsLtccLodkMtViIpIgFKpxLlzx2Cza7F48asYXAtS2I3fvU3eeOsDVJjdiI5riIysLHDYQFSEBkGBEuzYfhQ/rJ2DwUMfzEtZmzFW5VnqxLZJhKIosFisGvvsqvKiP9+z+btZJDevEOFhMdBqdbh6+TL9pW/bpjUCVEqYTAaMm101R/bxrVNIp4H/acI816slOXMxEyExDQGuGxHRcpw/fxDTxg3BtEleKvtH+Xv39RFk777DOHM+E+ogf6hUAfQcUIQCj+cHuCnYbHYI+UIQQqFCp4fb5YZCroTD7qCxsrssCI/1sPvakZ2ZC4VMiTYt2iI/J4/GM1ClglQiwp30NDpa2rptI3Tt0QHxCVHo83zdlVQcktSBXLiSBYOFgC+WwmDSIyY2DMXFWbh1tYgaPa4HOXjgIHh8HvwDE+By+yE7JxtNmjRGaWkJ8vJyaUGtVqthNJpgtXo1xOjoWBQWFqNMqwOfL0CX9m2x68f1iAj2x4QJIzH0lepFZAf2jiOlRiUqDBSaNm6ISxfPw+1yQSKVori0DFKlPwxmM50R4MejkH3nKsRcO8aOGIzRs6ru99y5YTF5Y/EyiGVqFGuNYHGFCPJUZGJxUVahh9XuhN3uon3aBoOJLj7ktFvAYdtpJunE57qDzzVj8Tu1I+pc+d40smnLNlrgqQICUFRigMUugtFkhVTKhUhMgWIZ8Wyfrlj0WvXSpL759FXy1ZpNkCnCUK63o7TMhO49euLihXN49qkeSDm6B0V5aXhrwTQkvrSUOpo8jXS571TXw9qL1Old00mbvpWfdnhYHfLVe4LlIPLAWBidFMoMWgRoeOD7WTG4Xw8sXug1l+rLb+u6ReTU6TO4dvUaMtKzUFrsBkUBUgkHFTpvyaZGDRtCLlMiIyOL1uYCA9UoN5SAI7TCbNWDxeLAarKCcrPQuUMnxMbE0Cw2QWoVGiTEot/Y2hfgripe33+5kGzfdQTKgDDkFRaCw6fA4Vlx9eop3LriNZ/Xb5pM9u45hB/WX0G3bk2h1+vhdDrhcNgRGRVJjz8l5TRioiPBYrGRnZ0LimLBXxkAp9OFfE+0W8hHfEQQpk0ai37Dqp9Ptmv9m2T2wi9AsRU0TiqFDGXaEpSVlUHu749mLVri7MWL0GpLERAgQ3iwCsmb/o0vV83G0ClV9wNPeeUJUl5hx7HUC1CoQqEJicK1G3fgIi4EBCkQFKLBzZs3YTHbERYSCalYiVtp6fDjsBAWrMC1K8fRo1tjrN9Y+6pwHy0bTb76eg3MFkIL3WIdB26Kh/iECJrENivrOrJzjIiMBPh+QLcurdG2TSu8POI/rajlS4eQk6nncPbcDfR4/Bn89POvsDspaIIjoC3TQ6MJhtloQLDGHylHT6FFY3+8PmcSEofXbVnLytYodeTHcX9oeGw2G56/hy0AU3ZOIx36ef0uO794kWjUarRL9ArhlK3TSIeB93wyZ7ZNJ60HVC6gw1RcoglrAsITwc1xwWjJgzqAi0F9e2DqpL83wSsD7FFcP7L9fZJy4jTOnr0AEDYsZisosBDXMArN28TASSwYPaVy3+bD6vs3q18lS5d9CS5fDrPDBoptA1g6dO3eHOu+vviXj82+5KXk8OEj8LD2ZGVlITMzA04ngdVKW+4wGYHoaCmCgkJoP1tcbDwiIiKQl5uL8LAQ9BtRc2E+ZvgT5OLlO6goKwafy0JosAZNmzbB4WPHUFhSioCgYLjcbuh0JXisSQPkZdzAkoVz0LsatTFUYpDImGB4SAHyiipoHsU27Trj2o2ryC+8hYRGUSguLkRZmQ7+8iDIJBoUFxgg5AnBZTnB51lgsWQg5XTNayw/aO7HjepGTl3MgkoTDh6PjeAQNYoK8yCVilBUlAeHw4bCvHyo/FVwOQE/Lp/OlQ1UaWAyWuh0KxZPAIeb0LTsIrGAZgqKjAxBcVEeTqdeQstm2Hm+6wAAESNJREFUQTh+suCRKhgfLH2ZHD6SQhemf6QdqYsN+MuGaaSo1IUVq7ehwsJFXkkFwqJDUF6RDofNijfnD8erM++dpz2yZQpxu210wnXnAdX3YdbFGP4vtBkoAdEERyO+YQJuZ1zGjZu5sNYw4fjnNVPIM6OqFnU9sWsG6dj3XqQ5Zdco0qFv5TU23lk4mIj8uFDKJfQJIYfbjY0/bsHu/fc267ZN88iAF7xHy1KSp5EOVTTJ1n0+m8yasxwx8dHQhETjWloGHaW32a2w2ctgtpRBIvODSCSCttQALlsGqSAUTjtQWpiP2JhAEJKHfUfSfb5f33tnLDl8/BQKCgtht9nxWPMWuH79BtxuAg6bB6lEjjJtBUxGK5TKAFoL9QTJPC4XlVoDoUyG/Yf2o1HDOAgELDjtFaBgQWlRCd5fOhtDRlZdE/blut+9+Q3ywYerkJdfDj8/PlhsPk3wQR3eNPYvGl7b37UtX3agsrbO7JhAWifeO952KnkcaZt0T40+tWMSaZv44AjyieTXScck75ncXeumkuOpaTh7qRhcoQa3svJhdZrgH8hGcUEalr05AyNG1i71orKx/K9fX/fpLDJn7gdQBUTS5KwCkRsVxpu4dPXhlLKsj/j/+M1ccvrcFZSWm8DmCnHm7EVoNBoI/Xi4fv08/NV8hEcEIyc3H9mZOgTIo6BShECjDsT2bdvx3rKxmDj3S58LvLtYDR/cnFy5fB1FhXYkeMxbox0KRSA4HD5KS8rBF4ihUPiDy/NDZmYWPIm6ngR6T8mHI8cOIic7Dw3iA9C8SQyaNIzEnEUPL7fu7+a7X68Q4iHzoFg82l9KUWzfaHgHdkwnPX83QStbbMd2jiWd+/l+4o7tnEs69/Oyssye8DT5/KtfoA6NR3zTFth38Bc0eywMLnsRpo8fhpGj75nEBzaMJz2HeNNUUndOJe37Vf04VWVjrcr1s7smk1Z9/56k4fi2saTTAC9ep3dPIm2e813aUFX6V5N7dv2wmCxa9AUCAqNRUFCI4FApnunTAtPq6FhWTfr452eO/TSddO5TuavEF++628bGbxcRYmNh397dSM85j0ZNo9GpSydkphci85YOHJYUn2/6ldqx5h2SOKrueet+Xj+XPDPMu4c8v/Ufv0k+XvkJtGU6WGwO6M2AwA/g8ICIyGCIJEKw/NjYfzCNfubXne+TJ/r5pvaGL3F+49UnyW8HDsHudIJOS7kbpfX47x62dndyx2zS7nfWkT8P8uTuqaTdc1UTQIe2zyHd+3tD3R+/M5a8+946gKtEaFQ8MnJugScwQSK04/23XkXSwEfrOPXlZNbHtsYNaUsuXiiF0ciGzbNR9PlYuOhlTH6tZgQV9XGMTJ/+mQhQJ3dMowVe234P9+tWF3Dt2fw6KS2z4vzFLBw5noaMnHJIFP5o37EV9v32I+wWE/Q6r1Z7cOtM0mPgw83Dq4sx18c2u7bVEIOeD7eLi+jocMhkFL7bvL/OzLH6iAHTp/qJAL0Ij26ZTFgsCp0GVM0xXFdD2b9+LOk1rPbmbrBKRmLjW8Nip3An/Q7i4kIRFiZBWWkaxozqhyEjHizoju2YQDr/7kc8vnMc6dSvbo4S1RV+9aHd+FgxiY5tghtpt8HlERgMWrz5xmiMH//ocx/rAz5MHx4tAtSRzZNJ18FeH1LqjumkfRV9cb7u9n8zbR/0Lk90tesgr4DeuWE6EQgD8UTifKrNY7EkPVMLFkcGpb8aJrMBHI4DAj8rwkPF2Lv/HtvwL1uHkaf/RF9//7tSfxpP2vepm2NovsavPrT3+ecLyaefbUB6Ri569uqCzOwrEIlNSDn0aJh16wMmTB/qFwL1RuDdhWXvhsnkySFeAXxw53ziJgSPJ1adZTYmQkkefyIJ+w+m0pEZuyf0b9WBTRkxa8ZozJ6zkkrePJ0kDb4vcLF1Eun5gLq89WuqHm1vjiVPJp2T/hpc+fe2yeTZAaupVR+NI0uXfgGpNJRmpw4JVcPm0OLA/iuMKftop455+30I/G7STvndpK1agODvEDy1cxqpSdBj37rxpPfwe5rUno3zyFMvVk3I7dm+mDzV30uT/tKQnuSHjQcQGhFCm7NcngB8AQ82qx7l2hysX7sa/ZMm/7EB9+2eSXo/5zVvjybPIF2SmHSVmu6Oto9JicHAQkRkM0THJODzL7+CG77JAqhpn5jnGAT+jIBPBV5t4D2+Yz4xW9zo/aKXxPKLf80gqSfP4tqNW8jNK0BIaBC6dOlEF7w2GnRQqZSIjY3BmbNn8POefZDKA1CmsyA7rwR8kQINGjdH2s1bKMrNRkRsBKIi1Di09xSjbdRmkv7m2TlTe5Pz5+5ALAnBndtFkEiVmDx1Al4cMeIveB/dNoN0GcB8WOpgGpgmq4CAT0xaD2tyTZhWHtS/QY/HEJOVQmm5BemZBYhLaAy5Qonbd27BZDGgYaNYlJQU4Pr1QpojTi71kILSxElo0LgZrly/BavDDQ6PD5PFgrCwEJSXlUApFyEkyB9yCR9CPhtNG8fjtXle1otDW+eR7gP/qlGm7ppO2vvgnPGJ7eOJhxigfeI//yTHuk9mEaeLICBAjedenEt9tXIa+Xjlp1AowtG+fS+sXr0GYeHRkCsleP75vqioyELDhGD4y3l4IrHuCAuqsNaZWxgE4BOBV1scj2ydSbr+niIiA4hSyUFUXHOcu3ADPL4EzVu0pOne72TcRGhYIDRB/sgvyEJ+Xh785QGIjU3A2fMXoApUQyCRICg0FBmZWaDYLEREhOPqlUuwmA3gsSm0btEMp06eAIu4kBAVgmljhwAOE54dyaSo/Hkef/1xIcnJK8XLMz+j3po7lLz/rx8gk4Omp/d8ZDxsNw0ahMPfPxBHj55D7179cOnSLboWSdfuXXDixEE0bRqFG9dO09TusVGhkAoEEPD4+Dr5AqNt13bjMM9XG4FKTdrj2yeSTv29msmJZG+ScoffNZXUnRNJ+373tJbaMq8c2r6MfLNmG06fvQmuQIVGTdrg+s0MuNxAVEwk7A4zTp89Ab6AhcBAOW7fuoVA/xC4nARCsRhWux12twNWmw0sDhsWmwVsNguREeEoKSpCmbYULZo1g7a4mC6aIxey8FS35nh71V9rpqbsnE069Hs05wCrPYt19MDHS0aRz75cC5lCDbPViciYeNxJz0KjJs2Rk5ODkpJihIWpUaEzoLhID73ejri4JoiPb4Cf/v0TWrZsgqAgOXTl+cjKuIGYiBAEqpQwlFfg6qWreP/d19H3ZSYJvI6mj2n2AQhQqTtmEptHQLAodBlUeR3a6qLoOclxfwnIv6ON3/PDLPLNt5uRkVUOgUgDbYUTcmUwikorAIoNk8XoPWuYnw6K5YRGraRrYZQUVsBmdYFiU+AL+WBz2eDyedBo1AgJC8HPP/+M+LhYmkvObDDBbDIhNjoGhgo9SvJv48lujbFizSFaoB/YNJf0fMF7tObwlmmk26CqM+dWF5f6fn/yD2+RRW+9B4V/MAjlh6KScvgHBCGvsBhsrh8UCiUMhgqYjeWIi41DVmY+Xb+CEA5YnnOLLEBbWgQW5YRIxIHbaUbGnTTEx0Sh1+M9kX7rDo4dPIR1X89C90FV55ar77gx/avfCFAHN04gPV70Hvk5vm0K8Rwve1S5eJ+8PYRs2PgTXBBBGRiJ9IwC8IUyUBwuXC4HzFYDhGIuDMYyuq7DnVu5iI2KAwscuhC4xWqB3WmjhZ6HwZnFYcHpdIC43XDYnVAqlDAbTTTJptlkhkTgBtdVjplTh2HUnOoRHtbvaa197xa/PoT8sCEZoeGxKK+wwGp3w08oRlZOHsBigcPl0dRMd25dh9vpQlBQKB7v+QTWrvuB9u81bNAQN65fg4eJRuUvgzpQSVMGmQw6CPh+0HoITE1WrPt8KnoPqV12QO1Hy7Twv4JAvRB4u78dQURiFXoO/oDatXYheWfZx3TQwlPli+snojU8s8VI/0VGhcJqM8LtsiEjw4DYqCC6DKCn8rzJbALFoiAUCmgOswqDHjKpFBaLBRKRGI0aNaI513KyclDoqfcg5cJuLMWEV/pi0iIvhfyx5Nmkc9L/tinrweGVEV3J6bNXwOaI6CLrHJ6Qdi2YLVYoVSqUlJYgOioCFFwwGg0oKS5BTHQcsrNzwOPxER4aDo/7w2wy0uzVHgp/D9W302mlaxvIZBI0jg1C785x6JpU/apl/ysblBmnbxGoc5O2pt3d+t0ikptXjNKyCoSFh4PATddFKCzKg91mhkgkwMx5a6iFs58nFqsDujIdSku1MJlMNDV6WbkOZWV6+PvLoNNVQF8BeOoUicUUgoKCEBHpTVUJC+Rj/LxHT2VTU5zq6rknugaTmzfzIZXLIVcGwGyxwWAyQSAUIjw8HJeuXEJFhQEhwSqoVP5Iv3Mber0L/fs/ibOnzyIvtxRcDhAUFAydzqvVRUSEIS4uEhERoYiOCkPi4HlM4KKuJpBp94EI1CuBd2LXXNKx7z16mvt7vPvH18lzz3s576rz+2Xze+TpwV767/3J75NeSX9PX3Ng80TSc/A/P3WkOvhUdm/HpmISF98QWg/tuVKBQHUgQj2pPuVa5OfnITI6AgqFHMrf6dEnz/qOWrV8OLGYrMjMyMEzz/RBn+frntqosnEw1xkEPAjUC5P2/qk4UAfHvA4lv0W6J71RbWHJLBEGAQaB/1sIPBSBd3LHVDrx9m6tjD///38LUmY0DAIMAvUVAcpTbf5eWsqjY9M9tXsqafs3ZJ/HfhpDOvfxFt45sm006TqAoRqqrwuK6ReDQH1GgDq21VuX1puO8r+bd1afJ4npG4MAg4BvEKCObZt4T+A95HoOtR3CyV3TSLu+jJCuLY7M8wwC/ysI/CHwPD62DomPlvH4fwV0ZpwMAgwCjwYB6uhW7/lYj0lbXwVeyg4PXx8LD9Lm7tfyPPd5xvKwK489mqlj3sogwCBQXQSoI1u81EWeP4/QY0zE6kLI3M8gwCDwT0GAFnh3NbwHCTzGT/ZPmUqmnwwCDAKVIfCHwLur4THmYGWQMdcZBBgE/qkIUIc3j/sPk5YReP/UqWT6zSDAIFAZArSG5zFlO/V/dEnHlXWSuc4gwCDAIOALBKjfNrxMCzwul8sIPV8gyrTBIMAgUG8RoE4kT6YFHhOdrbdzxHSMQYBBwEcIUHvXjSAcDofW8Dx/9TkXr772zUdzwTTDIMAgUMcIUMe3T6IF3sPQ8E7tnkIIIWjX11s7I3XnZNK+n+/raNQxZkzzDAIMAv9QBGgNz2PSempA1EcNz3N64n7NjskL/IeuNKbbDAL1AAEmSlsPJoHpAoMAg8DDQYD67Ycxf0RpPRpe274rGGbgh4M98xYGAQaBh4wAdXSLN0rbsX/1aZY8hbk7JnlLPDI/BgEGAQaB+o4AdWCDN/GYy+XUSOjV9wEy/WMQYBBgELiLAJWSPIN0SGLMWGZJMAgwCPzfR4D6dd0YOi2Fw/FqeR5tr23fD6lTu2YQTy3Yh5Gu8n8fZmaEDAIMAvUBAUbDqw+zwPSBQYBB4KEgQGt4d314jIb3UDBnXsIgwCDwiBCgjm6Z8nuU9iMm2vqIJoF5LYMAg8DDQaDSKO2J5EmkYxJDHfVwpoN5C4MAg0BdIkDt+/4VOmjB43HRacBKRsurS7SZthkEGAQeKQLU/vVj/xB4NUk+fqS9Z17OIMAgwCBQDQQqNWmr0RZzK4MAgwCDQL1GgDq0aSIdtPDk4XVIYgIX9Xq2mM4xCDAI1AoB6vCPk/5IS2nX70PGh1crOJmHGQQYBOozAkxaSn2eHaZvDAIMAj5FgDq+bRphsai/mLOeo2Vu4gJTttGneDONMQgwCDxCBP4fs6WVA2ESL6IAAAAASUVORK5CYII=");
////
////        byte[] pdf = Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\Report - Hành vi NTD VN trên internet - 2015.pdf"));
////
////        byte[] result = createInitialsForm(pdf, field, "");
////        FileOutputStream fos = new FileOutputStream("C:\\Users\\Admin\\Downloads\\response2.pdf");
////        fos.write(result);
////        fos.close();
//    }
//}
