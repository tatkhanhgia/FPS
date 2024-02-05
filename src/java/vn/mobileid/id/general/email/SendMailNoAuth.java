/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.email;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author ADMIN
 */
public class SendMailNoAuth {

    public static void main(String[] args) throws Exception {
        String sendTo = "giatk@mobile-id.vn";
        String subject = "Test hàm gửi email";
        String content = "Test hàm gửi email";
        // parsing properties
        final Properties props = new Properties();
        FileInputStream input = new FileInputStream("C:\\Users\\Admin\\Documents\\NetBeansProjects\\SendMail\\config.properties");
        Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
        props.load(reader);

        // Get the Session object.
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(props.getProperty("mail.smtp.username"),
                        props.getProperty("mail.smtp.password"));
            }
        });

        // Create a default MimeMessage object.
        Message message = new MimeMessage(session);
        // Set From: header field of the header.
        message.setFrom(new InternetAddress(props.getProperty("mail.smtp.sendfromAddr"),
                props.getProperty("mail.smtp.sendfromName")));

        // Set To: header field of the header.
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(sendTo));

        // Set Subject: header field
        message.setSubject(subject);
        // Now set the actual message
        message.setContent(content, "text/html; charset=utf-8");
        // Send message
        Transport.send(message);
        System.out.println("OK");
    }
    
    public void originalFunction(){
//        String sendTo = args[1];
//        String subject = "Test hàm gửi email";
//        String content = "Test hàm gửi email";
//        // parsing properties
//        final Properties props = new Properties();
//        Reader reader = new InputStreamReader(new FileInputStream(args[0]), StandardCharsets.UTF_8);
//        props.load(reader);
//
//        // Get the Session object.
//        Session session = Session.getInstance(props,
//                new javax.mail.Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(props.getProperty("mail.smtp.username"),
//                        props.getProperty("mail.smtp.password"));
//            }
//        });
//
//        // Create a default MimeMessage object.
//        Message message = new MimeMessage(session);
//        // Set From: header field of the header.
//        message.setFrom(new InternetAddress(props.getProperty("mail.smtp.sendfromAddr"),
//                props.getProperty("mail.smtp.sendfromName")));
//
//        // Set To: header field of the header.
//        message.setRecipients(Message.RecipientType.TO,
//                InternetAddress.parse(sendTo));
//
//        // Set Subject: header field
//        message.setSubject(subject);
//        // Now set the actual message
//        message.setContent(content, "text/html; charset=utf-8");
//        // Send message
//        Transport.send(message);
//        System.out.println("OK");
    }
}
