/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.QryptoService.process;

import java.util.HashMap;
import java.util.List;
import vn.mobileid.id.FPS.QryptoService.object.Configuration;
import vn.mobileid.id.FPS.QryptoService.object.QRSchema;
import vn.mobileid.id.FPS.QryptoService.request.ClaimRequest;
import vn.mobileid.id.FPS.QryptoService.response.ClaimResponse;
import vn.mobileid.id.FPS.QryptoService.response.DownloadFileTokenResponse;
import vn.mobileid.id.FPS.QryptoService.response.IssueQryptoWithFileAttachResponse;

/**
 *
 * @author GiaTK
 */
public interface ISession {
    public void login() throws Exception;
    
    public IssueQryptoWithFileAttachResponse issueQryptoWithFileAttach(
            QRSchema schema,
            Configuration configuration
    ) throws Exception;
    
    public IssueQryptoWithFileAttachResponse issueQryptoWithFileAttach(
            String schema,
            HashMap<String, byte[]> headers,
            QRSchema.format format,
            Configuration configuration
    ) throws Exception;
            
    public ClaimResponse dgci_wallet_claim(
            ClaimRequest request
    ) throws Exception;
    
    public DownloadFileTokenResponse downloadFileToken(
            String fileToken
    ) throws Exception;
}
