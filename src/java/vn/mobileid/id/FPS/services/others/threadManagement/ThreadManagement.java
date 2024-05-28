/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.services.others.threadManagement;

import java.util.concurrent.Future;
import vn.mobileid.id.FPS.services.others.threadManagement.interfaces.IThreadPool;

/**
 *
 * @author GiaTK
 */
public class ThreadManagement {
    private final IThreadPool threadPool;
    
    public ThreadManagement(IThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    public <T> Future<T> submitTask(TaskV2 working) throws Exception {
        return threadPool.getExecutorService().submit(working);
    }

    public <T> T executeTask(TaskV2 working) throws Exception {
        Future<T> response = threadPool.getExecutorService().submit(working);
        return response.get();
    }

    public void shutdown() {
        this.threadPool.getExecutorService().shutdown();
    }
}
