/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.field;

import com.fasterxml.jackson.databind.ObjectMapper;
import vn.mobileid.id.FPS.fieldAttribute.SignatureFieldAttribute;

/**
 *
 * @author GiaTK
 */
public class test {
    public static void main(String[] args)throws Exception {
        String a = "{\"field_name\":\"signature2\",\"page\":1,\"dimension\":{\"x\":100,\"y\":50,\"width\":100,\"height\":50},\"visible_enabled\":true,\"level_of_assurance\":[\"ESEAL\"]}";
        SignatureFieldAttribute temp = new ObjectMapper().readValue(a, SignatureFieldAttribute.class);
        SignatureFieldAttribute temp2 = new SignatureFieldAttribute();
        temp2.setLevelOfAssurance(temp.getLevelOfAssurance());
//        System.out.println(new ObjectMapper().writeValueAsString(temp));
        System.out.println(new ObjectMapper().writeValueAsString(temp2));
    }
}
