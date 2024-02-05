/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.email;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.general.Configuration;
import vn.mobileid.id.FPS.object.Attachment;
import vn.mobileid.id.general.database.DatabaseFactory;
import vn.mobileid.id.utils.Utils;
//import org.apache.log4j.Logger;
//import vn.mobileid.esigncloud.dao.Attachment;
//import vn.mobileid.esigncloud.dao.DatabaseResponse;
//import vn.mobileid.esigncloud.dao.SMTPProperties;
//import vn.mobileid.esigncloud.database.Database;
//import vn.mobileid.esigncloud.database.DatabaseImpl;
//import vn.mobileid.esigncloud.utils.Constant;
//import vn.mobileid.esigncloud.utils.LogManager;
//import vn.mobileid.esigncloud.utils.Utils;

/**
 *
 * @author VUDP
 */
public class Email {

    final private static Logger LOG = LogManager.getLogger(Email.class);

    private SMTPProperties smtpProperties;

    public Email(SMTPProperties smtpProperties) {
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content- handler=com.sun.mail.handlers.message_rfc822");
        if (smtpProperties == null) {
            Configuration.getInstance();
        } else {
            this.smtpProperties = smtpProperties;
        }
    }

    public EmailResp send(EmailReq emailReq) {
        EmailResp emailResponse = new EmailResp();
        DatabaseImpl db = DatabaseFactory.getDatabaseImpl();        
//        DatabaseResponse dpresp = db.getP2PLogID();
//        String billCode = Utils.generateBillCode(emailReq.getEntityName(), dpresp.getLogId(), dpresp.getLogDatetime());
        String billCode = Utils.generateOneTimePassword(5);
        try {
            final Properties props = new Properties(SMTPProperties.getProp());
            // parsing properties
//            props.putAll(smtpProperties.getProperties());   
//            System.out.println("Host:"+props.getProperty("mail.smtp.host"));
//            System.out.println("user:"+props.getProperty("mail.smtp.username"));
//            System.out.println("pass:"+props.getProperty("mail.smtp.password"));
//            System.out.println("FromA:"+props.getProperty("mail.smtp.sendfromaddr"));
//            System.out.println("FromN:"+props.getProperty("mail.smtp.sendfromname"));
            // Get the Session object.
            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(props.getProperty("mail.smtp.username"),
                            props.getProperty("mail.smtp.password"));
                }
            });
            session.setDebug(false);            
            // Create a default MimeMessage object.
            Message message = new MimeMessage(session);
            // Set From: header field of the header.            
            message.setFrom(new InternetAddress(props.getProperty("mail.smtp.sendfromaddr"),
                    props.getProperty("mail.smtp.sendfromname")));

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(emailReq.getSendTo()));

            // Set Subject: header field
            message.setSubject(emailReq.getSubject());

            List<Attachment> attachments = emailReq.getAttachments();

            if (attachments != null) {
                if (!attachments.isEmpty()) {
                    Multipart multipart = new MimeMultipart();
                    MimeBodyPart textBodyPart = new MimeBodyPart();
                    textBodyPart.setContent(emailReq.getContent(), "text/html; charset=utf-8");                    
                    multipart.addBodyPart(textBodyPart);
                    // process attachment
                    for (Attachment attachment : attachments) {
                        MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                        DataSource source = new ByteArrayDataSource(attachment.getData(), "application/octet-stream");
                        attachmentBodyPart.setDataHandler(new DataHandler(source));
                        attachmentBodyPart.setFileName(attachment.getFileName());
                        multipart.addBodyPart(attachmentBodyPart);
                    }
                    message.setContent(multipart);
                } else {
                    message.setContent(emailReq.getContent(), "text/html; charset=utf-8");
                }
            } else {
                message.setContent(emailReq.getContent(), "text/html; charset=utf-8");
            }
            
            Transport.send(message);
            if (LogHandler.isShowDebugLog()) {
                LOG.debug("Email has been sent to " + emailReq.getSendTo());
            }
            emailResponse.setResponseCode(A_FPSConstant.CODE_SUCCESS);
            emailResponse.setBillCode(billCode);
//            emailResponse.setTimestamp(dpresp.getLogDatetime());
//            emailResponse.setLogInstance(dpresp.getLogId());
            emailResponse.setTimestamp(Date.from(Instant.now()));
            emailResponse.setLogInstance(123);
            return emailResponse;
        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while sending an email to " + emailReq.getSendTo() + ".");
            }
//            e.printStackTrace();
            emailResponse.setResponseCode(1);
            emailResponse.setBillCode(billCode);
//            emailResponse.setTimestamp(dpresp.getLogDatetime());
//            emailResponse.setLogInstance(dpresp.getLogId());
            emailResponse.setTimestamp(Date.from(Instant.now()));
            emailResponse.setLogInstance(123);
            return emailResponse;

        }
    }
}
