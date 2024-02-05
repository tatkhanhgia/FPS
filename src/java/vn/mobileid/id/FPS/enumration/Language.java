/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.enumration;

/**
 *
 * @author GiaTK
 */
public enum Language {
    ENGLISH("EN",new String[]{"English","ENGLISH", "english", "EN", "en", "En", "eN"}),
    VIETNAMESE("VN", new String[]{"VIETNAM", "vietnam", "vn", "VN", "Vn", "vN"});
    
    private String id;
    private String[] names;

    private Language(String id, String... names) {
        this.id = id;
        this.names = names;
    }

    public String getId() {
        return id;
    }
    
    public static Language getLanguage(String temp){
        try{
            for(Language language : values()){
                for(String name : language.names){
                    if(name.equalsIgnoreCase(temp)){
                        return language;
                    }
                }
            }
            return null;
        } catch(Exception ex){
            return null;
        }
    }
}
