//package vn.mobileid.id.FPS.groupdoc;
//
//import com.groupdocs.conversion.Converter;
//
//import com.groupdocs.conversion.contracts.Pair;
//import com.groupdocs.conversion.converting.image.PdfToImageConverter;
//import com.groupdocs.conversion.contracts.PossibleConversions;
//import com.groupdocs.conversion.contracts.SaveDocumentStream;
//import com.groupdocs.conversion.contracts.SavePageStream;
//import com.groupdocs.conversion.contracts.TargetConversion;
//import com.groupdocs.conversion.contracts.documentinfo.IDocumentInfo;
//import com.groupdocs.conversion.filetypes.FileType;
//import com.groupdocs.conversion.filetypes.ImageFileType;
//import com.groupdocs.conversion.filetypes.PresentationFileType;
//import com.groupdocs.conversion.filetypes.SpreadsheetFileType;
//import com.groupdocs.conversion.filetypes.WordProcessingFileType;
//import com.groupdocs.conversion.licensing.License;
//import com.groupdocs.conversion.options.convert.ConvertOptions;
//import com.groupdocs.conversion.options.convert.ImageConvertOptions;
//import com.groupdocs.conversion.options.convert.PdfConvertOptions;
//import com.groupdocs.conversion.options.convert.PresentationConvertOptions;
//import com.groupdocs.conversion.options.convert.SpreadsheetConvertOptions;
//import com.groupdocs.conversion.options.convert.WordProcessingConvertOptions;
//import com.groupdocs.conversion.options.load.LoadOptions;
//import com.groupdocs.conversion.pipeline.a;
//import com.groupdocs.conversion.savers.SaverFactory;
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.nio.file.FileSystems;
//import java.nio.file.Files;
//import java.util.stream.Stream;
//
///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
///**
// *
// * @author GiaTK
// */
//public class Conversion {
//
//    private static Conversion instance;
//
//    public static Conversion getInstance() {
//        if (Conversion.instance == null) {
//            Conversion.instance = new Conversion();
//        }
//        return Conversion.instance;
//    }
//
//    Conversion() {
////        init();
//    }
//
//    void init() {
//        //Applying License
//        System.out.println("==============LOAD LICENSE GROUPDOC===============");
//        ClassLoader loader = Thread.currentThread().getContextClassLoader();
//        License license = new License();
//        license.setLicense(loader.getResourceAsStream("resources/GroupDocs.ConversionProductFamily.lic"));
//        System.out.println("==============LOAD SUCCESSFULL===============");
//    }
//
//    public Converter loadDocument(String path) {
//        return new Converter(path);
//    }
//
//    public Converter loadDocument(byte[] binary) {
//        init();                
//        return new Converter(() -> {
//            return new ByteArrayInputStream(binary);
//        });
//    }
//
//    public void possibleConvertion(Converter converter) {
//        //Get Possible conversion
//        PossibleConversions possibleConvertion = converter.getPossibleConversions();
//        System.out.print(String.format("%s is of type %s and could be converted to:\n",
//                "C:\\Users\\Admin\\Downloads\\tesstsdsdtt.pdf", possibleConvertion.getSource().getExtension()));
//        for (TargetConversion conversion : possibleConvertion.getAll()) {
//            System.out.print(String.format("\t %s as %s conversion.\n",
//                    conversion.getFormat().getExtension(),
//                    conversion.isPrimary() ? "primary" : "secondary"));
//        }
//    }
//
//    public void getDocumentInfo(Converter converter) {
//        IDocumentInfo info = converter.getDocumentInfo();
//        System.out.println("Date:" + info.getCreationDate());
//        System.out.println("Format:" + info.getFormat());
//        System.out.println("Pages:" + info.getPagesCount());
//        System.out.println("Size:" + info.getSize());
//        //Can convert to more Object to present the properties of the file
//        //PdfDocumentInfo pdfInfo = (PdfDocumentInfo) info;
//        //ProjectManagementDocumentInfo docInfo = (ProjectManagementDocumentInfo) info;
//        //ImageDocumentInfo docInfo = (ImageDocumentInfo) info;
//        //PresentationDocumentInfo docInfo = (PresentationDocumentInfo) info;
//        //SpreadsheetDocumentInfo docInfo = (SpreadsheetDocumentInfo) info;
//        //CadDocumentInfo docInfo = (CadDocumentInfo) info;
//        //EmailDocumentInfo docInfo = (EmailDocumentInfo) info;
//    }
//
//    public byte[] convertTo_(Converter converter, Type type) {
//        switch (type) {
//            case Word_doc: {
//                return Word(converter, type);
//            }
////                WordProcessingConvertOptions options = new WordProcessingConvertOptions();
////              options.setFormat(WordProcessingFileType.Doc);
////                converter.convert("converted.docx", (ConvertOptions) type.getType());
////                break;
////            }
////            case Excel: {
////                converter.convert("converted.xlsx", (ConvertOptions) type.getType());
////                break;
////            }
//            case Image_jpeg: {
//                return Image(converter, type);
//            }
//            case Image_png: {
//                return Image(converter, type);
//            }
////            case PowerPoint: {
////                converter.convert("converted.xlsx", (ConvertOptions) type.getType());
////                break;
////            }
//        }
//        return null;
//    }
//
//    private byte[] Word(Converter converter, Type type) {
//        //ConvertOption
//        WordProcessingConvertOptions options = (WordProcessingConvertOptions) type.getType();
//        options.setFormat(type.getFileType());
//
//        //Save file
//        SaveDocumentStream out = new DocumentSavedStream();
//        converter.convert(out, options);
//        ByteArrayOutputStream data = (ByteArrayOutputStream) out.get();
//        return data.toByteArray();
//    }
//
//    private byte[] Image(Converter converter, Type type) {
//        //ConvertOption
//        ImageConvertOptions options = (ImageConvertOptions) type.getType();
//        options.setFormat(type.getFileType());
//        options.setPageNumber(1);
//
//        //Save file
////        SaveDocumentStream out = new DocumentSavedStream();
//        PageSavedStream out = new PageSavedStream();
//        converter.convert(out, options);
//        ByteArrayOutputStream data = (ByteArrayOutputStream) out.invoke(0);
//        return data.toByteArray();
//    }
//
//    public static enum Type {
//        Word_doc(new WordProcessingConvertOptions(), WordProcessingFileType.Doc),
//        Word_docx(new WordProcessingConvertOptions(), WordProcessingFileType.Docx),
//        Excel_xlsx(new SpreadsheetConvertOptions(), SpreadsheetFileType.Xlsx),
//        PowerPoint_pptx(new PresentationConvertOptions(), PresentationFileType.Pptx),
//        Image_jpeg(new ImageConvertOptions(), ImageFileType.Jpeg),
//        Image_png(new ImageConvertOptions(), ImageFileType.Png);
//
//        private Object type;
//        private FileType fileType;
//
//        private Type(Object a, FileType fileType) {
//            this.type = a;
//            this.fileType = fileType;
//        }
//
//        public Object getType() {
//            return type;
//        }
//
//        public FileType getFileType() {
//            return fileType;
//        }
//    }
//    
//    public static void main(String[] args) throws IOException{
//        byte[] one = Files.readAllBytes(new File(
//                "C:\\Users\\Admin\\Documents\\NetBeansProjects\\Library_RSSP_SDK\\ProjectRSSP_newest\\FPS\\src\\java\\resources\\Paperless Gateway Service - API Specification - V1.230224.pdf")
//                .toPath());
//         ClassLoader loader = Thread.currentThread().getContextClassLoader();
//        License license = new License();
//        license.setLicense(loader.getResourceAsStream("resources/GroupDocs.ConversionProductFamily.lic"));
//        Converter converter =  new Converter(() -> {
//            return new ByteArrayInputStream(one);
//        });
//        
//        ImageConvertOptions options = new ImageConvertOptions();
//        options.setFormat(ImageFileType.Png);
//        options.setPageNumber(1);
//
//        //Save file
////        SaveDocumentStream out = new DocumentSavedStream();
//        PageSavedStream outs = new PageSavedStream();
//        converter.convert(outs, options);
//        ByteArrayOutputStream data = (ByteArrayOutputStream) outs.invoke(0);        
//        
//        FileOutputStream out = new FileOutputStream("C:\\Users\\Admin\\Downloads\\temp.png");
//        out.write(data.toByteArray());
//        out.close();
//    }
//}
