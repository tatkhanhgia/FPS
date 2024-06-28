/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.util.summary.module;

import vn.mobileid.id.FPS.services.MyServices;

/**
 *
 * @author GiaTK
 */
public class ReloadThreadManagement {
    //<editor-fold defaultstate="collapsed" desc="Reload Thread Management">
    /**
     * Reload Thread Management of the System
     * @param transactionId 
     */
    public static void reloadThreadManagement(String transactionId){
        MyServices.getThreadManagement(1).reloadAllThread();
    }
    //</editor-fold>
}
