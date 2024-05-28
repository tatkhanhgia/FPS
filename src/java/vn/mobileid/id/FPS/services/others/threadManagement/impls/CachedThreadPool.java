/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.services.others.threadManagement.impls;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import vn.mobileid.id.FPS.services.others.threadManagement.interfaces.IThreadPool;

/**
 *
 * @author GiaTK
 */
public class CachedThreadPool implements IThreadPool {
    
    private final ExecutorService executor;

    public CachedThreadPool() {
        executor = generateThreadPool();
    }

    @Override
    public ExecutorService generateThreadPool() {
        return new ThreadPoolExecutor(
                0, // corePoolSize: 0 (không có thread nào được tạo sẵn)
                10, // maximumPoolSize: 10 (giới hạn số lượng thread tối đa)
                60L, // keepAliveTime: 60 giây (thread nhàn rỗi sẽ bị hủy sau 60 giây)
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>() // Không lưu trữ tác vụ trong queue
        );
    }

    @Override
    public ExecutorService getExecutorService() {
        return this.executor;
    }

}
