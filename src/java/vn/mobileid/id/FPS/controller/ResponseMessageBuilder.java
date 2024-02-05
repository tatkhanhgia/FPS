/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller;

/**
 *
 * @author GiaTK
 */
public interface ResponseMessageBuilder {
    ResponseMessageBuilder writeStringField(String name, String data);
    ResponseMessageBuilder writeNumberField(String name, Number data);
    String build();
}
