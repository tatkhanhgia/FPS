/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.services.others.threadManagement.impls;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import vn.mobileid.id.FPS.services.others.threadManagement.interfaces.IThreadPool;

/**
 *
 * @author GiaTK
 */
public class ScheduledThreadPool implements IThreadPool {
    
    private final ExecutorService executor;

    public ScheduledThreadPool(boolean allowCoreThreadTimeOut){
        executor = generateThreadPool(false);
    }
    
    public ScheduledThreadPool() {
        executor = generateThreadPool();
    }

    @Override
    public ExecutorService generateThreadPool() {
        return generateThreadPool(true);
    }

    @Override
    public ExecutorService getExecutorService() {
        return this.executor;
    }

    private ExecutorService generateThreadPool(boolean allowCoreThreadTimeOut){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        return scheduler;
    }

    @Override
    public ThreadPoolExecutor getThreadPoolExecutor() {
        return (ThreadPoolExecutor) this.executor;
    }
}
