package vn.mobileid.id.FPS.groupdoc;

import com.groupdocs.conversion.Converter;
import com.groupdocs.conversion.contracts.PossibleConversions;
import com.groupdocs.conversion.contracts.TargetConversion;
import com.groupdocs.conversion.filetypes.ImageFileType;
import com.groupdocs.conversion.filetypes.PresentationFileType;
import com.groupdocs.conversion.filetypes.WordProcessingFileType;
import com.groupdocs.conversion.licensing.License;
import com.groupdocs.conversion.options.convert.ImageConvertOptions;
import com.groupdocs.conversion.options.convert.PresentationConvertOptions;
import com.groupdocs.conversion.options.convert.WordProcessingConvertOptions;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author GiaTK
 */
public class Conversion_22_8_1 {

    private static Conversion_22_8_1 instance;

    public static Conversion_22_8_1 getInstance() {
        if (Conversion_22_8_1.instance == null) {
            Conversion_22_8_1.instance = new Conversion_22_8_1();
        }
        return Conversion_22_8_1.instance;
    }

    Conversion_22_8_1() {
        License license = new License();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("/resources/GroupDocs.ConversionProductFamily_newest.lic");
//        InputStream input = classLoader.getResourceAsStream("/resources/GroupDocs.ConversionProductFamily.lic");
//        InputStream input = null;
//        try {
//            input = new FileInputStream("C:\\Users\\Admin\\Documents\\NetBeansProjects\\Library_RSSP_SDK\\ProjectRSSP_newest\\FPS\\src\\java\\resources\\GroupDocs.ConversionProductFamily.lic");
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(Conversion_22_8_1.class.getName()).log(Level.SEVERE, null, ex);
//        }
        license.setLicense(input);
    }

    public byte[] convertToImage(
            byte[] file,
            int page,
            ImageFileType format
    ) {
        Converter converter = new Converter(new ByteArrayInputStream(file));
        ImageConvertOptions options = new ImageConvertOptions();
        options.setFormat(format);
        options.setPageNumber(page);
        options.setPagesCount(1);
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        converter.convert(data, options);
        return data.toByteArray();
    }

    public byte[] convertToPPT(
            byte[] file,
            int page,
            PresentationFileType format
    ) {
        Converter converter = new Converter(new ByteArrayInputStream(file));
        PresentationConvertOptions options = new PresentationConvertOptions();
        options.setFormat(format);
        options.setPageNumber(page);
        options.setPagesCount(10);        
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        converter.convert(data, options);
        return data.toByteArray();
    }
    
    public byte[] convertToDocs(
            byte[] file,
            int page,
            WordProcessingFileType format
    ) {
        Converter converter = new Converter(new ByteArrayInputStream(file));
        WordProcessingConvertOptions options = new WordProcessingConvertOptions();
        options.setFormat(format);
        options.setPageNumber(page);
        options.setPagesCount(10);        
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        converter.convert(data, options);
        return data.toByteArray();
    }

    public PossibleConversions a(
            byte[] file) {
        Converter converter = new Converter(new ByteArrayInputStream(file));
        PossibleConversions possibleConvertion = converter.getPossibleConversions();
        System.out.print(String.format("%s is of type %s and could be converted to:\n",
                "C:\\Users\\Admin\\Downloads\\tesstsdsdtt.pdf", possibleConvertion.getSource().getExtension()));
        for (TargetConversion conversion : possibleConvertion.getAll()) {
            System.out.print(String.format("\t %s as %s conversion.\n",
                    conversion.getFormat().getExtension(),
                    conversion.isPrimary() ? "primary" : "secondary"));
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        for (int i = 1; i <= 90; i = i + 3) {
            byte[] data = Conversion_22_8_1.getInstance().convertToPPT(
                    Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\Phần Cung Cầu (1).pdf")),
                    i,
                    PresentationFileType.Pptx);

            FileOutputStream out = new FileOutputStream("C:\\Users\\Admin\\Downloads\\"+i+".pptx");
            out.write(data);
            out.close();
        }

//        Conversion_22_8_1.getInstance().a(Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\tempp.pdf")));
    }
}
