/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.enumration;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;

/**
 *
 * @author GiaTK
 */
public enum TextField_Color {
    BLACK(ColorConstants.BLACK, new String[]{"black"}),
    BLUE(ColorConstants.BLUE, new String[]{"blue"}),
    CYAN(ColorConstants.CYAN, new String[]{"cyan"}),
    DARK_GRAY(ColorConstants.DARK_GRAY, new String[]{"dark_gray"}),
    GRAY(ColorConstants.GRAY, new String[]{"gray"}),
    GREEN(ColorConstants.GREEN, new String[]{"green"}),
    LIGHT_GRAY(ColorConstants.LIGHT_GRAY, new String[]{"light_gray"}),
    MAGENTA(ColorConstants.MAGENTA, new String[]{"magenta"}),
    ORANGE(ColorConstants.ORANGE, new String[]{"orange"}),
    PINK(ColorConstants.PINK, new String[]{"pink"}),
    RED(ColorConstants.RED, new String[]{"red"}),
    WHITE(ColorConstants.WHITE, new String[]{"white"}),
    YELLOW(ColorConstants.YELLOW, new String[]{"yellow"});
    
    
    private Color itextColor;
    private String[] colorNames;

    private TextField_Color(Color itextColor, String... colorNames) {
        this.itextColor = itextColor;
        this.colorNames = colorNames;
    }

    public Color getItextColor() {
        return itextColor;
    }
    
    public static TextField_Color getColor(String nameInput){
        try{
            for(TextField_Color color : values()){
                for(String name : color.colorNames){
                    if(nameInput.equalsIgnoreCase(name)){
                        return color;
                    }
                }
            }
        } catch(Exception ex){
        }
        return TextField_Color.BLACK;
    }
}
