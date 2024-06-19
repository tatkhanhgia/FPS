/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.object;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import vn.mobileid.id.FPS.services.MyServices;

/**
 *
 * @author GiaTK
 */
public class APILog {
    private long id;
    private List<FileCached> fileCaches;
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
    }
}
