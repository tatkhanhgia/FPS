/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.services.objects;

import java.util.List;

/**
 *
 * @author GiaTK
 */
public class JSObject extends JSAtomic{
    private List<? super JSAtomic> data;

    public JSObject(List<JSAtomic> data, String name) {
        this.data = data;
    }

    public JSObject() {
    }

    @Override
    public List<JSAtomic> getData() {
        return (List<JSAtomic>) data;
    }

    public void setData(List<? super JSAtomic> data) {
        this.data = data;
    }
}
