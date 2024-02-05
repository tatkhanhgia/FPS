/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.enumration;

/**
 *
 * @author GiaTK
 */
public enum TextField_Font {
    OpenSans_Bold("OpenSans-Bold.ttf",new String[]{"opensans_bold"}),
    OpenSans_BoldItalic("OpenSans-BoldItalic.ttf",new String[]{"opensans_bold_italic"}),
    OpenSans_ExtraBold("OpenSans-ExtraBold.ttf",new String[]{"opensans_extrabold"}),
    OpenSans_ExtraBoldItalic("OpenSans-ExtraBoldItalic.ttf",new String[]{"opensans_extrabolditalic"}),
    OpenSans_Italic("OpenSans-Italic.ttf",new String[]{"opensans_italic"}),
    OpenSans_Italic_VariableFont("OpenSans-Italic-VariableFont_wdth,wght.ttf",new String[]{"opensans_italic_variablefont"}),
    OpenSans_Light("OpenSans-Light.ttf",new String[]{"opensans_light"}),
    OpenSans_LightItalic("OpenSans-LightItalic.ttf",new String[]{"opensans_lightitalic"}),
    OpenSans_Medium("OpenSans-Medium.ttf",new String[]{"opensans_medium"}),
    OpenSans_MediumItalic("OpenSans-MediumItalic.ttf",new String[]{"opensans_mediumitalic"}),
    OpenSans_Regular("OpenSans-Regular.ttf",new String[]{"opensans_regular"}),
    OpenSans_SemiBold("OpenSans-SemiBold.ttf",new String[]{"opensans_semibold"}),
    OpenSans_SemiBoldItalic("OpenSans-SemiBoldItalic.ttf",new String[]{"opensans_semibolditalic"}),
    OpenSans_VariableFont("OpenSans-VariableFont_wdth,wght.ttf",new String[]{"opensans_variablefont"}),
    
    Roboto_Black("Roboto-Black.ttf",new String[]{"roboto_black"}),
    Roboto_Bold("Roboto-Bold.ttf",new String[]{"roboto_bold"}),
    Roboto_Thin("Roboto-Thin.ttf",new String[]{"roboto_thin"}),
    Roboto_Regular("Roboto-Regular.ttf",new String[]{"roboto_regular"}),
    
    D_Arial("D-Arial.ttf",new String[]{"d_arial"}),
    
    D_Times("D-Times.ttf",new String[]{"d_times"}),
    
    FunkySignature_Regular("FunkySignature-Regular.ttf",new String[]{"funkysignature_regular"}),
    
    Fz_Jim_Sintergate("Fz-Jim-Sintergate.ttf",new String[]{"fz_jim_sintergate"}),
    
    MyriadPro_Regular("MyriadPro-Regular.otf",new String[]{"myriadpro_regular"}),
    
    Verdana("verdana.ttf",new String[]{"verdana"}),
    Verdana_Bold("verdana-bold.ttf",new String[]{"verdana_bold"}),
    Verdana_Bold_Italic("verdana-bold-italic.ttf",new String[]{"verdana_bold_italic"});
    
    
    public static String resource = "/resources/fonts/";
    private String path;
    private String[] fontNames;

    private TextField_Font(String path, String[] fontNames) {
        this.path = path;
        this.fontNames = fontNames;
    }

    public String getPath() {
        return resource + path;
    }

    public String[] getFontNames() {
        return fontNames;
    }
    
    public static TextField_Font getPath(String nameFont){
        try{
            for(TextField_Font font : values()){
                for(String name : font.getFontNames()){
                    if(name.equalsIgnoreCase(nameFont)){
                        return font;
                    }
                }
            }
        }catch(Exception ex){
        }
        return TextField_Font.D_Arial;
    }
    
}
