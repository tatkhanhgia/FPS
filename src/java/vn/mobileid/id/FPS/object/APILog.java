/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.object;

/**
 *
 * @author GiaTK
 */
public class APILog {
    private long id;
    private String fileCache;
     
    public APILog(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFileCache() {
        return fileCache;
    }

    public void setFileCache(String fileCache) {
        this.fileCache = fileCache;
    }
}
