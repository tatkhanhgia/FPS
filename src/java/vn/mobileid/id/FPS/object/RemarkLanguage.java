/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.object;

import vn.mobileid.id.helper.annotation.AnnotationORM;

/**
 *
 * @author GiaTK
 */
public class RemarkLanguage {
    private String languageName;
    
    private String value;

    public RemarkLanguage() {
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageId) {
        this.languageName = languageId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
