                                                                                                                                                                                                                                                                                                                                /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.services.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author GiaTK
 */
public class JSArray extends JSAtomic{
    private List<JSObject> array;

    public JSArray(){
        array = new ArrayList<>();
    }
  
    public List<JSObject> getArray() {
        return array;
    }

    public void setArray(List<JSObject> array) {
        this.array = array;
    }
    
    public void addIntoArray(JSObject object){
        array.add(object);
    }
}
