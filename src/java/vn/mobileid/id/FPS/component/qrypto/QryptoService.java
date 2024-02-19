/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.qrypto;

import java.util.HashMap;
import vn.mobileid.id.FPS.QryptoService.object.Configuration;
import vn.mobileid.id.FPS.QryptoService.object.Property;
import vn.mobileid.id.FPS.QryptoService.object.QRSchema;
import vn.mobileid.id.FPS.QryptoService.process.QryptoSession;
import vn.mobileid.id.FPS.QryptoService.process.SessionFactory;
import vn.mobileid.id.FPS.QryptoService.response.IssueQryptoWithFileAttachResponse;
import vn.mobileid.id.FPS.QryptoService.response.DownloadFileTokenResponse;

/**
 *
 * @author GiaTK
 */
public class QryptoService {

    private int enterprise_id_instant;

    private Property prop;

    private QryptoSession session;
    private SessionFactory sessionFactory;

    private static HashMap<Integer, QryptoService> listSession = new HashMap<>();
    private static HashMap<Integer, QryptoSession> listQryptoSession = new HashMap<>();
    private static QryptoService qryptoService;

    public static QryptoService getInstance(int i) throws Throwable {
        if (i <= 0) {
            return null;
        }
        if (QryptoService.qryptoService == null) {
            QryptoService.qryptoService = new QryptoService(i);
            if (QryptoService.listSession.containsKey(i)) {
                QryptoService.listSession.replace(i, QryptoService.qryptoService);
            }
            return QryptoService.qryptoService;
        }
        if (QryptoService.listSession.containsKey(i)) {
            return QryptoService.listSession.get(i);
        } else {
            QryptoService.qryptoService = new QryptoService(i);
            if (QryptoService.listSession.containsKey(i)) {
                QryptoService.listSession.replace(i, QryptoService.qryptoService);
            }
            return QryptoService.qryptoService;
        }
    }

    private QryptoService(int i) throws Throwable {
        prop = new Property(
                vn.mobileid.id.general.Configuration.getInstance().getHostQrypto(),
                vn.mobileid.id.general.Configuration.getInstance().getQryptoAuthentication());
        if (!QryptoService.listQryptoSession.containsKey(i)) {
            this.sessionFactory = SessionFactory.getInstance(prop);
            this.session = this.sessionFactory.getSession();
            QryptoService.listQryptoSession.put(i, session);
            this.session.login();
        } else {
            this.session = QryptoService.listQryptoSession.get(i);
        }
    }

    public void login() throws Exception {
        this.session.login();
    }

    public IssueQryptoWithFileAttachResponse generateQR(
            QRSchema QR,
            Configuration configuration,
            String transaction
    ) throws Exception{
        IssueQryptoWithFileAttachResponse response = this.session.issueQryptoWithFileAttach(QR, configuration);
        return response;
    }

    public DownloadFileTokenResponse downloadFileToken(
            String fileToken
    )throws Exception{
        return this.session.downloadFileToken(fileToken);
    }
    
    public static void main(String[] args) throws Throwable {
//        QRSchema schema = new QRSchema();
//
//        List<QRSchema.data> listData = new ArrayList<>();
//        List<QRSchema.field> listField = new ArrayList<>();
//
//        schema.setScheme("QC1");
//
//        QRSchema.data data = new QRSchema.data();
//        data.setName("kvalue");
//        data.setValue("TATKHANHGIA");
//
//        QRSchema.format format = new QRSchema.format();
//        format.setVersion("2");
//
//        QRSchema.field field1 = new QRSchema.field();
//        field1.setName("Name");
//        field1.setType(QRSchema.fieldType.t2);
//        field1.setKvalue("kvalue");
//        listField.add(field1);
//        listData.add(data);
//
//        format.setFields(listField);
//
//        schema.setData(listData);
//        schema.setFormat(format);
//
//        vn.mobileid.id.qrypto.object.Configuration configuration = new vn.mobileid.id.qrypto.object.Configuration();
//        configuration.setContextIdentifier("QC1:");
//        configuration.setQryptoHeight(1080);
//        configuration.setQryptoWidth(1080);
//        configuration.setIsTransparent(true);
//        configuration.setQryptoEffectiveDate(
//                new qryptoEffectiveDate("2023-03-22 00:00:00", "2023-04-30 00:00:00"));
//
//        String transcation = "1";
//        IssueQryptoWithFileAttachResponse res = QryptoService.getInstance(1).generateQR(schema, configuration, transcation);
//        System.out.println("DataL:" + new ObjectMapper().writeValueAsString(res));

        DownloadFileTokenResponse response = getInstance(1).downloadFileToken("NWI1NTZlMmYtMTRlZS00YzU3LWI5MzQtMWUxODU3YWQ1YzFh");
    }
}
