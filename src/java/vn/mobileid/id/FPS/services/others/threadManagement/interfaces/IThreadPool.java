/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.services.others.threadManagement.interfaces;

import java.util.concurrent.ExecutorService;
import vn.mobileid.id.FPS.services.others.threadManagement.TaskV2;

/**
 *
 * @author GiaTK
 */
public interface IThreadPool {
    ExecutorService generateThreadPool();
    
    ExecutorService getExecutorService();
}
