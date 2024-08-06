/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.services.impls.json.objects;

/**
 *
 * @author GiaTK
 */
public class JSAtomic {
    private Object data;
    private String name;

    public JSAtomic(Object data, String name) {
        this.data = data;
        this.name = name;
    }

    public JSAtomic() {
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
