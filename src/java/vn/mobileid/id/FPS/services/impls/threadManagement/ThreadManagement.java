/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.services.impls.threadManagement;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author GiaTK
 */
public class ThreadManagement {

    private final ExecutorService executorService = new ThreadPoolExecutor(
            0, // corePoolSize: 0 (không có thread nào được tạo sẵn)
            10, // maximumPoolSize: 10 (giới hạn số lượng thread tối đa)
            60L, // keepAliveTime: 60 giây (thread nhàn rỗi sẽ bị hủy sau 60 giây)
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>() // Không lưu trữ tác vụ trong queue
    );

    public ThreadManagement() {
    }

    public <T> Future<T> submitTask(TaskV2 working) throws Exception {
        return executorService.submit(working);
    }

    public <T> T executeTask(TaskV2 working) throws Exception {
        Future<T> response = executorService.submit(working);
        return response.get();
    }

    public void shutdown() {
        this.executorService.shutdown();
    }
}
