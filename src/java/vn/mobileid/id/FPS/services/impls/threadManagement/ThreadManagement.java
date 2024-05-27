/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.services.impls.threadManagement;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import vn.mobileid.id.utils.TaskV2;

/**
 *
 * @author GiaTK
 */
public class ThreadManagement {
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    
    public ThreadManagement(){
    }
    
    public <T> Future<T> submitTask(TaskV2 working){
        return executorService.submit(working);
    }
}
