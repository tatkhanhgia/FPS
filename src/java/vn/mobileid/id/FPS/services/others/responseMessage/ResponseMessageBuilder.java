/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.services.others.responseMessage;

/**
 *
 * @author GiaTK
 */
public interface ResponseMessageBuilder {
    ResponseMessageBuilder writeStringField(String name, String data);
    ResponseMessageBuilder writeBooleanField(String name, Boolean data);
    ResponseMessageBuilder writeNumberField(String name, Number data);
    String build();
}
