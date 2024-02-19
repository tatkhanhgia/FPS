/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.QryptoService.process;

/**
 *
 * @author GiaTK
 */
public enum ItemsType {
    String(1),
    Boolean(2),
    Integer(3),
    Date(4),
    Binary(5),
    TextBold(6),
    Choice(7),
    Table(8),
    File(9),
    URL(10),
    ID_Picture_with_4_labels(11),
    Non_Editable(12),
    Empty(13)
    ;
    
    private int id;

    private ItemsType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
    public static ItemsType getItemsType(int id){
        for(ItemsType type : values()){
            if(type.getId() == id){
                return type;
            }
        }
        return null;
    }
}
