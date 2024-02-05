/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.enumration;

/**
 *
 * @author GiaTK
 */
public enum LevelOfAssurance {
    ELECTRONIC_SIGNATURE("electronic signature"),
    ELECTRONIC_SEAL("electronic seal"),
    CADES_SIGNATURE("cades signature");
    
    String name;

    private LevelOfAssurance(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public static LevelOfAssurance getLevelOfAssurance(String levelOfAssurance){
        for(LevelOfAssurance level : values()){
            if(level.getName().equalsIgnoreCase(levelOfAssurance)){
                return level;
            }
        }
        System.err.println("Cannot get Level of Assurance => Using default PdfProfileCMS");
        return ELECTRONIC_SIGNATURE;
    }
}
