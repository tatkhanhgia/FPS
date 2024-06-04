/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.services.others.threadManagement;

import java.util.concurrent.ScheduledExecutorService;
import vn.mobileid.id.FPS.services.others.threadManagement.impls.ScheduledThreadPool;
import vn.mobileid.id.FPS.services.others.threadManagement.interfaces.IThreadPool;

/**
 *
 * @author GiaTK
 */
public class ScheduledThreadManagement {
    private final static IThreadPool scheduledPool = new ScheduledThreadPool(false);
    
    public ScheduledThreadManagement(){
    }
    
    public static void main(String[] args)throws Exception {
        ScheduledExecutorService thread = (ScheduledExecutorService)scheduledPool.getExecutorService();
        TaskV2 task = new TaskV2(null,null){
            @Override
            public Object call(){
                
                return null;
            }
        };
    }
    
    private ScheduledExecutorService getThread(){
        return (ScheduledExecutorService)scheduledPool.getExecutorService();
    }
    
}
