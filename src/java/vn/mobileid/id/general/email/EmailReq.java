/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.email;

import java.io.Serializable;
import java.util.List;
import vn.mobileid.id.FPS.object.Attachment;

/**
 *
 * @author VUDP
 */
public class EmailReq implements Serializable {

    private String entityName;
    private String sendTo;
    private String subject;
    private String content;
    private List<Attachment> attachments;

    public EmailReq() {
    }

    public EmailReq(
            String entityName,
            String sendTo,
            String subject,
            String content,
            List<Attachment> attachments) {
        this.entityName = entityName;
        this.sendTo = sendTo;
        this.subject = subject;
        this.content = content;
        this.attachments = attachments;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }
}
