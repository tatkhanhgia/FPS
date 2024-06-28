/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.util.summary.module;

import vn.mobileid.id.FPS.systemManagement.Resources;

/**
 *
 * @author GiaTK
 */
public class ReloadRAM {
    //<editor-fold defaultstate="collapsed" desc="Reload Resources">
    /**
     * Reload Resources of System
     * @param transactionId 
     */
    public static void reloadResources(String transactionId){
        Resources.init_();
    }
    //</editor-fold>
    
}
