/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.services.others.threadManagement;

import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import vn.mobileid.id.FPS.services.others.threadManagement.impls.CachedThreadPool;
import vn.mobileid.id.FPS.services.others.threadManagement.interfaces.IThreadPool;

/**
 *
 * @author GiaTK
 */
public class ThreadManagement {

    private IThreadPool threadPool;
    private final static IThreadPool systemPool = new CachedThreadPool(false);

    public ThreadManagement(IThreadPool threadPool) {
        ThreadPoolExecutor executor = systemPool.getThreadPoolExecutor();
        if (executor.getActiveCount() < executor.getMaximumPoolSize() && executor.getQueue().isEmpty()) {
        } else {
            this.threadPool = threadPool;
        }
    }

    public <T> Future<T> submitTask(TaskV2 working) throws Exception {
        return getPoolActive().getExecutorService().submit(working);
    }

    public <T> T executeTask(TaskV2 working) throws Exception {
        Future<T> response = getPoolActive().getExecutorService().submit(working);
        return response.get();
    }
    
    public IThreadPool getPoolActive(){
        return threadPool == null ? systemPool : threadPool;
    }

    public void shutdown() {
        this.threadPool.getExecutorService().shutdown();
    }
}
