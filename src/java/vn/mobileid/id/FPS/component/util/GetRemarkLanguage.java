/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.util;

import fps_core.enumration.Language;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.object.RemarkLanguage;
import vn.mobileid.id.FPS.database.DatabaseFactory;
import vn.mobileid.id.helper.ORM_JPA.database.objects.DatabaseResponse;

/**
 *
 * @author GiaTK
 */
public class GetRemarkLanguage {

    //<editor-fold defaultstate="collapsed" desc="Get Remark Language">
    public static RemarkLanguage getRemark(
            String name,
            Language languageId
    ) {
        try {
            DatabaseResponse response = DatabaseFactory.getDatabaseImpl().getRemarkLanguage(
                    "RESPONSE_CODE",
                    name,
                    languageId.getId(),
                    "none");
            if(response.getStatus() != A_FPSConstant.CODE_SUCCESS){
                return null;
            }
            String value = (String) response.getObject();
            
            RemarkLanguage remark_ = new RemarkLanguage();
            remark_.setLanguageName(languageId.getId());
            remark_.setValue(value);
            return remark_;
        } catch (Exception ex) {
            System.err.println("Cannot get Remark of "+ name);
        }
        return null;
    }
    //</editor-fold>
    
    public static void main(String[] args) {
        RemarkLanguage remark = getRemark("11", Language.ENGLISH);
        System.out.println(remark.getValue());
    }
}
