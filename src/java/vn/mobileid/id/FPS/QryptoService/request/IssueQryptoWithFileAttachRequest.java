/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.QryptoService.request;

import java.util.HashMap;
import java.util.List;
import vn.mobileid.id.FPS.QryptoService.object.Configuration;

/**
 *
 * @author GiaTK
 */
public class IssueQryptoWithFileAttachRequest {
    private String payload;        
    private Configuration configuration;
    private List<byte[]> imgs;
    private List<byte[]> demo;
    private List<byte[]> landscapes;
    private HashMap<String, byte[]> mapFile;

    public IssueQryptoWithFileAttachRequest() {
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public List<byte[]> getImgs() {
        return imgs;
    }

    public void setImgs(List<byte[]> imgs) {
        this.imgs = imgs;
    }

    public List<byte[]> getDemo() {
        return demo;
    }

    public void setDemo(List<byte[]> demo) {
        this.demo = demo;
    }

    public List<byte[]> getLandscapes() {
        return landscapes;
    }

    public void setLandscapes(List<byte[]> landscapes) {
        this.landscapes = landscapes;
    }            

    public HashMap<String, byte[]> getMapFile() {
        return mapFile;
    }

    public void setMapFile(HashMap<String, byte[]> mapFile) {
        this.mapFile = mapFile;
    }
    
    
}
