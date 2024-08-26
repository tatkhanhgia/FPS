/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.services.others.threadManagement;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import vn.mobileid.id.FPS.controller.fms.FMS;
import vn.mobileid.id.FPS.controller.util.summary.UtilsSummaryInternal;
import vn.mobileid.id.FPS.controller.util.summary.micro.UpdateAPILog;
import vn.mobileid.id.FPS.object.APILog;
import fps_core.objects.FileCached;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.services.others.threadManagement.impls.ScheduledThreadPool;
import vn.mobileid.id.FPS.services.others.threadManagement.interfaces.IThreadPool;
import vn.mobileid.id.FPS.systemManagement.LogHandler;

/**
 *
 * @author GiaTK
 */
public class ScheduledThreadManagement {

    private final static IThreadPool scheduledPool = new ScheduledThreadPool(false);

    private static long timestamp = Long.MAX_VALUE;
    private static int nextTimeInSecond = Integer.MAX_VALUE;

    private static volatile ScheduledThreadManagement instance;

    private static final int[] delayTimeInSeconds = new int[5];

    private static final int maxErrorCanNegotiate = 10;
    
    //Auto reconnection to DB
    private static final boolean isInProcessingReconnectDB = false;

    static {
        delayTimeInSeconds[0] = 60; //1 minute
        delayTimeInSeconds[1] = 60; //1 minute
        delayTimeInSeconds[2] = 300; //5 minute
        delayTimeInSeconds[3] = 900; //15 minute
        delayTimeInSeconds[4] = 3600;//1 hour
    }

    public static ScheduledThreadManagement getInstance() {
        if (instance == null) {
            instance = new ScheduledThreadManagement();
        }
        return instance;
    }

    private ScheduledThreadManagement() {
        initial();
    }

    public void initial() {
        try{
        //<editor-fold defaultstate="collapsed" desc="Initial">
        System.out.println("\n=====Start Initial Scheduled Task=====");
        InternalResponse getAPILogs = new UtilsSummaryInternal().getAPILogs();
        if (!getAPILogs.isValid()) {
            LogHandler.getInstance().error(ScheduledThreadManagement.class,
                    "",
                    "Cannot initial Scheduled Thread Management because cannot get API Logs");
            return;
        }
        List<APILog> apiLogs = (List<APILog>) getAPILogs.getData();
        for (APILog apiLog : apiLogs) {
            List<FileCached> fileCaches = apiLog.getFileCaches();

            for (FileCached fileCache : fileCaches) {
                long timeStampOfFileCached = vn.mobileid.id.FPS.utils.Utils.getTimeStampInMilis(fileCache.getTimeStamp());
                if (timestamp > timeStampOfFileCached) {
                    timestamp = timeStampOfFileCached;
                }
                if (nextTimeInSecond > fileCache.getTime()) {
                    nextTimeInSecond = fileCache.getTime();
                }
            }
        }
        //</editor-fold>

        System.out.println("\n=====Scheduled Task===== => Create thread delete File Cache after:" + (timestamp - System.currentTimeMillis()) + " second");
        getThread().schedule(generateTaskDeleteFileCache(), timestamp - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        } catch(RuntimeException ex){
            autoReloadSheduledWhenHaveTheProblem(generateInitial(), 1);
        }
    }

    private TaskV2 generateTaskDeleteFileCache() {
        return new TaskV2(null, null) {
            @Override
            public Object call() {
                try {
                    long now = System.currentTimeMillis();
                    InternalResponse getAPILogs = new UtilsSummaryInternal().getAPILogs();
                    if (!getAPILogs.isValid()) {
                        LogHandler.getInstance().error(ScheduledThreadManagement.class,
                                "",
                                "Cannot initial Scheduled Thread Management because cannot get API Logs");
                    }
                    List<APILog> apiLogs = (List<APILog>) getAPILogs.getData();
                    for (APILog apiLog : apiLogs) {
                        List<FileCached> fileCaches = apiLog.getFileCaches();

                        for (FileCached fileCache : fileCaches) {
                            long timeStampOfFileCached = vn.mobileid.id.FPS.utils.Utils.getTimeStampInMilis(fileCache.getTimeStamp());

                            if (now >= timeStampOfFileCached) {
                                try {
                                    FMS.deleteDocument(fileCache.getUuid(), true, "");
                                    UpdateAPILog.updateAPILog(apiLog.getId(), null, "System FPS", "");
                                } catch (Exception ex) {
                                }
                            }
                        }
                    }
//                System.out.println("=======");
                    System.out.println("\n=====Scheduled Task======> Delete file cache successfull");
                    System.out.println("\n=====Scheduled Task======> Start create schedule for initial after " + nextTimeInSecond + " second");
                    getThread().schedule(new TaskV2(null, null) {
                        @Override
                        public Object call() {
                            initial();
                            return null;
                        }
                    }, nextTimeInSecond, TimeUnit.SECONDS);
                    return null;
                } catch (Exception e) {
                    LogHandler.getInstance().error(ScheduledThreadManagement.class, "", e);
                    return null;
                }
            }
        };
    }

    private TaskV2 generateInitial() {
        return new TaskV2(null, null) {
            @Override
            public Object call() throws Exception {
                System.out.println("\n=====Start Initial Scheduled Task=====");
                InternalResponse getAPILogs = new UtilsSummaryInternal().getAPILogs();
                if (!getAPILogs.isValid()) {
                    LogHandler.getInstance().error(ScheduledThreadManagement.class,
                            "",
                            "Cannot initial Scheduled Thread Management");
                    return null;
                }
                List<APILog> apiLogs = (List<APILog>) getAPILogs.getData();
                for (APILog apiLog : apiLogs) {
                    List<FileCached> fileCaches = apiLog.getFileCaches();

                    for (FileCached fileCache : fileCaches) {
                        long timeStampOfFileCached = vn.mobileid.id.FPS.utils.Utils.getTimeStampInMilis(fileCache.getTimeStamp());
                        if (timestamp > timeStampOfFileCached) {
                            timestamp = timeStampOfFileCached;
                        }
                        if (nextTimeInSecond > fileCache.getTime()) {
                            nextTimeInSecond = fileCache.getTime();
                        }
                    }
                }
                //</editor-fold>

                System.out.println("\n=====Scheduled Task===== => Create thread delete File Cache after:" + (timestamp - System.currentTimeMillis()) + " second");
                getThread().schedule(generateTaskDeleteFileCache(), timestamp - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                return null;
            }
        };
    }

    private ScheduledExecutorService getThread() {
        return (ScheduledExecutorService) scheduledPool.getExecutorService();
    }

    public void autoReloadSheduledWhenHaveTheProblem(TaskV2 task, int errorTime) {
        try {
            if (errorTime == maxErrorCanNegotiate) {
                LogHandler.getInstance().error(
                        ScheduledThreadManagement.class,
                        "",
                        "Cannot execute Task anymore => reach max time delay");
                return;
            }
            if (errorTime >= 5) {
                getThread().schedule(task, delayTimeInSeconds[4], TimeUnit.SECONDS);
            } else {
                getThread().schedule(task, delayTimeInSeconds[errorTime], TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            autoReloadSheduledWhenHaveTheProblem(task, errorTime + 1);
        }
    }

    public static void main(String[] args) throws Exception {

    }

}
