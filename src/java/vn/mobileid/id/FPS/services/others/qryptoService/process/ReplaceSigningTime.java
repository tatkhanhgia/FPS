/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.services.others.qryptoService.process;

import fps_core.objects.core.Signature;
import java.text.SimpleDateFormat;
import java.util.List;
import vn.mobileid.id.FPS.controller.enterprise.summary.module.GetAPIKeyRuleOfEnterprise;
import vn.mobileid.id.FPS.enumeration.QryptoVariable;
import vn.mobileid.id.FPS.enumeration.Rule;
import vn.mobileid.id.FPS.object.APIKeyRule;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.FPS.services.others.qryptoService.object.Item_Table;
import vn.mobileid.id.FPS.systemManagement.LogHandler;
import vn.mobileid.id.FPS.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class ReplaceSigningTime {
    private final User user;
    private final List<Signature> listSignature;
    
    public ReplaceSigningTime(User user, List<Signature> listSignature){
        this.user = user;
        this.listSignature = listSignature;
    }
    
    public Item_Table replaceSigningTime(String json) {
        try {
            if (user != null && !Utils.isNullOrEmpty(listSignature)) {
                String format = getDateFormat();
                if (format == null) {
                    return MyServices.getJsonService().readValue(json, Item_Table.class);
                }
                try {
                    for (int i = 0; i < listSignature.size(); i++) {
                        Signature signature = listSignature.get(i);
                        if (signature == null || signature.getSigningTime() == null) {
                            continue;
                        }
                        String replacement = new SimpleDateFormat(format).format(signature.getSigningTime());
                        json = json.replace(QryptoVariable.SIGNER_NUMBER.getAnnotationName() + (i + 1), replacement);
                    }

                } catch (Exception e) {
                }
            }
            return MyServices.getJsonService().readValue(json, Item_Table.class);
        } catch (Exception ex) {
            LogHandler.error(CreateQRSchema.class, json, ex);
            return null;
        }
    }
    
    public String replaceSigningTimeReturnString(String json) {
        try {
            if (user != null && !Utils.isNullOrEmpty(listSignature)) {
                String format = getDateFormat();
                if (format == null) {
                    return json;
                }
                try {
                    for (int i = 0; i < listSignature.size(); i++) {
                        Signature signature = listSignature.get(i);
                        if (signature == null || signature.getSigningTime() == null) {
                            continue;
                        }
                        String replacement = new SimpleDateFormat(format).format(signature.getSigningTime());
                        json = json.replace(QryptoVariable.SIGNER_NUMBER.getAnnotationName() + (i + 1), replacement);
                    }

                } catch (Exception e) {
                }
            }
            return json;
        } catch (Exception ex) {
            LogHandler.error(CreateQRSchema.class, json, ex);
            return null;
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Get Date Format of User in DB">
    /**
     * Get DateFormat of APIKey in DB
     * @return String date format
     */
    private String getDateFormat() {
        InternalResponse response = new GetAPIKeyRuleOfEnterprise().getAPIKeyRule(user, "transactionId");
        if (response.isValid()) {
            APIKeyRule rule = (APIKeyRule) response.getData();
            return checkRuleOfUser(rule) ? getDateFormat(rule) : null; // Simplified conditional
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Check rule of User">
    /**
     * Check the Rule IS_CONVERT_DATE is enable?
     * @param rule
     * @return True if the rule is enable
     */
    private boolean checkRuleOfUser(APIKeyRule rule) {
        return rule != null && rule.isRuleEnabled(Rule.IS_CONVERT_DATE);
    }
    //</editor-fold>    
    
    //<editor-fold defaultstate="collapsed" desc="Get Date Format">
    /**
     * Get Date Format of the APIKeyRule
     * @param rule
     * @return String date format
     */
    private String getDateFormat(APIKeyRule rule) {
        if (rule.isRuleEnabled(Rule.QRYPTO_DATE_FORMAT)) {
            Object dateFormatData = rule.getRule(Rule.QRYPTO_DATE_FORMAT).getData();
            return dateFormatData instanceof String ? (String) dateFormatData : null; // Safe type casting
        }
        return "dd/MM/yyyy hh:mm:ss";
    }
    //</editor-fold>
}
