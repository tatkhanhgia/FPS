//package vn.mobileid.id.FPS.controller;
//
//import com.groupdocs.conversion.Converter;
//import com.groupdocs.conversion.contracts.ConvertedPageStream;
//import com.groupdocs.conversion.filetypes.ImageFileType;
//import com.groupdocs.conversion.licensing.License;
//import com.groupdocs.conversion.options.convert.ImageConvertOptions;
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.file.Files;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import vn.mobileid.id.utils.Utils;
//
///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
///**
// *
// * @author Admin
// */
//public class test extends HttpServlet {
//
//    @Override
//    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        try {
//            byte[] one = Files.readAllBytes(new File(
//                    "C:\\Users\\Admin\\Documents\\NetBeansProjects\\Library_RSSP_SDK\\ProjectRSSP_newest\\FPS\\src\\java\\resources\\Paperless Gateway Service - API Specification - V1.230224.pdf")
//                    .toPath());
//
//            License license = new License();
//            license.setLicense("C:\\Users\\Admin\\Documents\\NetBeansProjects\\Library_RSSP_SDK\\ProjectRSSP_newest\\FPS\\src\\java\\resources\\GroupDocs.ConversionProductFamily.lic");
//            Converter converter = new Converter(new ByteArrayInputStream(one));
//
//            ImageConvertOptions options = new ImageConvertOptions();
//            options.setFormat(ImageFileType.Png);
//            options.setPageNumber(10);
//            options.setPagesCount(1);
//
//            //Save file
//            ByteArrayOutputStream data = new ByteArrayOutputStream();
//            ConvertedPageStream page = new ConvertedPageStream() {
//                @Override
//                public com.groupdocs.conversion.internal.c.a.ms.System.IO.Stream invoke(int i, com.groupdocs.conversion.internal.c.a.ms.System.IO.Stream stream) {
//                    return new com.groupdocs.conversion.internal.c.a.ms.System.IO.MemoryStream();
//                }
//            };
//            converter.convert(data, options);            
//
//            FileOutputStream out = new FileOutputStream("C:\\Users\\Admin\\Downloads\\temp.png");
//            out.write(data.toByteArray());
//            out.close();
//
//            Utils.sendMessage(
//                    resp,
//                    200,
//                    "application/octet-stream",
//                    data.toByteArray());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            Utils.sendMessage(
//                    resp,
//                    500,
//                    "application/json",
//                    null);
//        }
//    }
//}
