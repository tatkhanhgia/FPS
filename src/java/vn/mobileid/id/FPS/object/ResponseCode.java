/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.FPS.object;

/**
 *
 * @author ADMIN
 */
public class ResponseCode {
    private long id;
    private String name;
    private String code;
    private String code_description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode_description() {
        return code_description;
    }

    public void setCode_description(String code_description) {
        this.code_description = code_description;
    }

   
}
