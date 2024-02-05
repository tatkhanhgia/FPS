///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package vn.mobileid.id.FPS.groupdoc;
//
//import com.groupdocs.parser.Parser;
//import com.groupdocs.parser.data.TextReader;
//import java.io.IOException;
//import java.io.InputStream;
//import com.groupdocs.parser.licensing.License;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//
///**
// *
// * @author GiaTK
// */
//public class Groupdoc_Parser {
//
//    public static void main(String[] args) throws IOException {
//        com.groupdocs.parser.licensing.License license = new com.groupdocs.parser.licensing.License();
//        InputStream input = null;
//        try {
//            input = new FileInputStream("C:\\Users\\Admin\\Documents\\NetBeansProjects\\Library_RSSP_SDK\\ProjectRSSP_newest\\FPS\\src\\java\\resources\\GroupDocs.ParserforJava.lic");
//        } catch (FileNotFoundException ex) {
//        }
//        license.setLicense(input);
//        // Create an instance of Parser class
//        Parser parser = new Parser("C:\\Users\\Admin\\Downloads\\captcha.png");
//        try (TextReader reader = parser.getText()) {
//            // Print a text from the document
//            // If text extraction isn't supported, a reader is null
//            System.out.println(reader == null ? "Text extraction isn't supported" : reader.readToEnd());
//        }
//
//    }
//}
