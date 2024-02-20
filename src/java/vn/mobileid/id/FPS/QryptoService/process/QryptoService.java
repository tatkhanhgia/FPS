/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.QryptoService.process;


import java.util.HashMap;
import vn.mobileid.id.FPS.QryptoService.object.Configuration;
import vn.mobileid.id.FPS.QryptoService.object.Property;
import vn.mobileid.id.FPS.QryptoService.object.QRSchema;
import vn.mobileid.id.FPS.QryptoService.process.QryptoSession;
import vn.mobileid.id.FPS.QryptoService.process.SessionFactory;
import vn.mobileid.id.FPS.QryptoService.response.DownloadFileTokenResponse;
import vn.mobileid.id.FPS.QryptoService.response.IssueQryptoWithFileAttachResponse;

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
            return QryptoService.qryptoService;
        }
        return QryptoService.qryptoService;

    }

    private QryptoService(int i) throws Throwable {
        prop = new Property(
                vn.mobileid.id.general
                        .Configuration.getInstance()
                        .getHostQrypto(),
                vn.mobileid.id.general.Configuration.
                        getInstance()
                        .getQryptoAuthentication());
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
    ) throws Exception {
        IssueQryptoWithFileAttachResponse response = this.session.issueQryptoWithFileAttach(QR, configuration);
        return response;
    }

    public DownloadFileTokenResponse downloadFileToken(
            String fileToken
    ) throws Exception {
        return this.session.downloadFileToken(fileToken);
    }
}
