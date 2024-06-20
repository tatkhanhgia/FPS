/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.object;

import java.util.ArrayList;
import java.util.List;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.FPS.services.objects.JSArray;
import vn.mobileid.id.FPS.services.objects.JSAtomic;
import vn.mobileid.id.FPS.services.objects.JSObject;

/**
 *
 * @author GiaTK
 */
public class APILog {
    private long id;
    private List<FileCached> fileCaches = new ArrayList<>();
    private String fileCacheString;
     
    public APILog(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFileCacheString() {
        return fileCacheString;
    }

    public void setFileCacheString(String fileCacheString) {
        this.fileCacheString = fileCacheString;
        try{
            JSAtomic json = MyServices.getJsonService().readTree(fileCacheString);
            if(json instanceof JSArray){
                 JSArray array = (JSArray) json;
                 List<JSObject> objects = array.getArray();
                 for(JSObject object : objects){
                     FileCached fileCached = new FileCached(fileCacheString, 0, fileCacheString);
                     List<JSAtomic> fields = object.getData();
                     for(JSAtomic atomic : fields ){
                         if("timeStamp".equals(atomic.getName())){
                             fileCached.setTimeStamp((String)atomic.getData());
                         }
                         if("time".equals(atomic.getName())){
                             fileCached.setTime((int)atomic.getData());
                         }
                         if("uuid".equals(atomic.getName())){
                             fileCached.setUuid((String)atomic.getData());
                         }
                     }
                     fileCaches.add(fileCached);
                 }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public List<FileCached> getFileCaches() {
        return fileCaches;
    }

    public void setFileCaches(List<FileCached> fileCaches) {
        this.fileCaches = fileCaches;
    }
    
    public static void main(String[] args)throws Exception{
        APILog log = new APILog();
        log.setFileCacheString("[{\"uuid\":\"CBFC9BF3924F769390EC2C2436866167\",\"time\":86400,\"timeStamp\":\"12/06/2024 03:33:44\"}]");
        System.out.println(log.getFileCaches().get(0).getUuid());
    }
}
