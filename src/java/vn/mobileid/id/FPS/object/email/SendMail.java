package vn.mobileid.id.FPS.object.email;

///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package vn.mobileid.id.general.email;
//
//import java.util.ArrayList;
//import java.util.List;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import vn.mobileid.id.general.LogHandler;
//import vn.mobileid.id.general.database.Database;
//import vn.mobileid.id.general.database.DatabaseImpl;
//import vn.mobileid.id.FPS.controller.A_FPSConstant;
//import vn.mobileid.id.FPS.object.Attachment;
//import vn.mobileid.id.utils.AnnotationJWT;
//
///**
// *
// * @author GiaTK
// */
//public class SendMail extends Thread {
//
//    final private static Logger LOG = LogManager.getLogger(SendMail.class);
//
//    private String sendTo;
//    private String Subject;
//    private String Content;
//    private String EntityName;
//    private byte[] file;
//    private String fileName;
//
//    public SendMail() {
//    }
//
//    ;
//    
//    /**
//     * Contructor for custom
//     * SendTo : email destination
//     * Subject: Subject of email
//     * Content: body of email
//     * EntityName : xxx
//     * file: binary of file PDF
//     * filename: name of file PDF
//    */
//    public SendMail(
//            String sendTo,
//            String Subject,
//            String Content,
//            String EntityName,
//            byte[] file,
//            String fileName) {
//        this.sendTo = sendTo;
//        this.Subject = Subject;
//        this.Content = Content;
//        this.EntityName = EntityName == null ? "Paperless Service" : EntityName;
//        this.file = file;
//        this.fileName = fileName;
//    }
//
//    /**
//     * Constructor for Elabor
//     *
//     * @param sendTo : email destination
//     * @param name: name of user email
//     * @param CCCD: cccd
//     * @param file: binary data of file PDF
//     * @param filename : filename of file PDF
//     */
//    public SendMail(
//            String sendTo,
//            String name,
//            String CCCD,
//            byte[] file,
//            String filename) {
//        this.sendTo = sendTo;
//        this.Subject = "eLaborContract - " + name + " - " + CCCD;
//        this.Content = "Dear " + name;
//        this.Content += "<br /><br /> Paperless service would like to thank you for trusting and using our services";
//        this.Content += "<br /><br /> Your eLaborContract has been created successfully.";
//        this.file = file;
//        this.fileName = filename;
//        this.EntityName = "eLaborContract";
//    }
//
//    /**
//     * Contructor for create email and send to user.
//     *
//     * @param sendTo
//     * @param enterprise_id
//     * @param name
//     * @param CCCD
//     * @param file
//     * @param filename
//     */
//    public SendMail(
//            String sendTo,
//            int enterprise_id,
//            String name,
//            String CCCD,
//            byte[] file,
//            String filename) {
//        this.sendTo = sendTo;
//        this.file = file;
//        this.fileName = filename;
//        EmailTemplate template = getTemplate(enterprise_id);
//        if (template == null) {
//            this.Subject = "eLaborContract - " + name + " - " + CCCD;
//            this.Content = "Dear " + name;
//            this.Content += "<br /><br /> Paperless service would like to thank you for trusting and using our services";
//            this.Content += "<br /><br /> Your eLaborContract has been created successfully.";
//        } else {
//            this.Subject = template.getSubject().replace(AnnotationJWT.Name.getNameAnnot(), name);
//            this.Subject = this.Subject.replace(AnnotationJWT.DocNumber.getNameAnnot(), CCCD);
//            this.Content = template.getBody().replace(AnnotationJWT.Name.getNameAnnot(), name);
//        }
////        System.out.println("Subject:"+this.Subject);
//    }
//
//    public void appendAuthorizationCode(String authorizeCode) {
//        this.Content = this.Content.replace(AnnotationJWT.AuthorizeCode.getNameAnnot(), authorizeCode);
//    }
//
//    public void appendNameUser(String name) {
//        this.Subject = this.Subject.replace(AnnotationJWT.Name.getNameAnnot(), name);
//        this.Content = this.Content.replace(AnnotationJWT.Name.getNameAnnot(), name);
//    }
//
//    public void appendDocNumber(String docNumber) {
//        this.Subject = this.Subject.replace(AnnotationJWT.DocNumber.getNameAnnot(), docNumber);
//        this.Content = this.Content.replace(AnnotationJWT.DocNumber.getNameAnnot(), docNumber);
//    }
//
//    public void appendEmailUser(String email) {
//        this.Subject = this.Subject.replace(AnnotationJWT.Email.getNameAnnot(), email);
//        this.Content = this.Content.replace(AnnotationJWT.Email.getNameAnnot(), email);
//    }
//
//    public void appendLink(String link) {
//        this.Subject = this.Subject.replace(AnnotationJWT.Link.getNameAnnot(), link);
//        this.Content = this.Content.replace(AnnotationJWT.Link.getNameAnnot(), link);
//    }
//
//    public void appendTypeProcess(String type) {
//        this.Subject = this.Subject.replace(AnnotationJWT.Type.getNameAnnot(), type);
//    }
//
//    public String getSendTo() {
//        return sendTo;
//    }
//
//    public void setSendTo(String sendTo) {
//        this.sendTo = sendTo;
//    }
//
//    public String getSubject() {
//        return Subject;
//    }
//
//    public void setSubject(String Subject) {
//        this.Subject = Subject;
//    }
//
//    public String getContent() {
//        return Content;
//    }
//
//    public void setContent(String Content) {
//        this.Content = Content;
//    }
//
//    public String getEntityName() {
//        return EntityName;
//    }
//
//    public void setEntityName(String EntityName) {
//        this.EntityName = EntityName;
//    }
//
//    public byte[] getFile() {
//        return file;
//    }
//
//    public void setFile(byte[] file) {
//        this.file = file;
//    }
//
//    public String getFileName() {
//        return fileName;
//    }
//
//    public void setFileName(String fileName) {
//        this.fileName = fileName;
//    }
//
//    public void send() {
//        try {
//            Subject = Subject.replace(AnnotationJWT.DocNumber.getNameAnnot(), "");
//            Subject = Subject.replace(AnnotationJWT.Name.getNameAnnot(), "");
//            Content = Content.replace(AnnotationJWT.DocNumber.getNameAnnot(), "");
//            Content = Content.replace(AnnotationJWT.Name.getNameAnnot(), "");
//            EmailReq request = new EmailReq();
//            request.setSendTo(sendTo);
//            request.setSubject(Subject);
//            if (file != null && fileName != null) {
//                List<Attachment> attachs = new ArrayList<>();
//                attachs.add(new Attachment(file, fileName));
//                request.setAttachments(attachs);
//            }
//
//            request.setContent(Content);
//            request.setEntityName(EntityName);
//
//            Email email = new Email(null);
//            email.send(request);
//        } catch (Exception ex) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Cannot Send mail! - Detail:" + ex);
//            }
//        }
//    }
//
//    private EmailTemplate getTemplate(int enterprise_id) {
//        try {
//            String email_notification = "";
//            Database db = new DatabaseImpl();
//            DatabaseResponse res = db.getEnterpriseInfo(enterprise_id, null);
//            if (res.getStatus() != A_FPSConstant.CODE_SUCCESS) {
//                email_notification = "defaultemail";
//            } else {
//                Enterprise ent = (Enterprise) res.getObject();
//                email_notification = ent.getEmail_notification();
//            }
//
//            res = db.getEmailTemplate(
//                    2, //langguageEN
//                    email_notification,
//                    "transactionID");
//            if (res.getStatus() != A_FPSConstant.CODE_SUCCESS) {
//                return null;
//            }
//            EmailTemplate email_template = (EmailTemplate) res.getObject();
//            return email_template;
//        } catch (Exception ex) {
//            LogHandler.error(
//                    SendMail.class,
//                    "transaction",
//                    "Error while get Email Template",
//                    ex);
//        }
//        return null;
//    }
//
//    @Override
//    public void run() {
//        send();
//    }
//}
