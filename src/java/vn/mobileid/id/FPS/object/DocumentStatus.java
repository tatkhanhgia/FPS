/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.object;

/**
 *
 * @author GiaTK
 */
public enum DocumentStatus {
    PENDING(1),
    UPLOADED(2),
    PROCESSING(3),
    PROCESSED(4),
    READY(5),
    DELETED(6);
    
    private int typeId;

    private DocumentStatus(int typeId) {
        this.typeId = typeId;
    }

    public int getTypeId() {
        return typeId;
    }
    
    public static DocumentStatus getStatus(int typeId){
        for(DocumentStatus status : values()){
            if(status.getTypeId() == typeId){
                return status;
            }
        }
        return null;
    }
}
