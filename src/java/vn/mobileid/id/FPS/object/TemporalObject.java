/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.object;

/**
 *
 * @author GiaTK
 */
public class TemporalObject {
    private String parentName;
    private String childName;
    private int type;
    private byte[] data;

    public TemporalObject() {
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }
    
    

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    
    public enum Type{
        TEMPORAL_DATA(1);
        
        private int id;

        private Type(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
        
        
    }
}
