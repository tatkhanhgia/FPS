/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.services.others.threadManagement;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import vn.mobileid.id.FPS.controller.fms.FMS;
import vn.mobileid.id.FPS.controller.util.summary.UtilsSummaryInternal;
import vn.mobileid.id.FPS.controller.util.summary.micro.UpdateAPILog;
import vn.mobileid.id.FPS.object.APILog;
import vn.mobileid.id.FPS.object.FileCached;
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
        //<editor-fold defaultstate="collapsed" desc="Initial">
        System.out.println("\n=====Start Initial Scheduled Task=====");
        InternalResponse getAPILogs = new UtilsSummaryInternal().getAPILogs();
        if (!getAPILogs.isValid()) {
            LogHandler.error(ScheduledThreadManagement.class,
                    "",
                    "Cannot initial Scheduled Thread Management");
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
        System.out.println("TimeStamp:"+timestamp);
        System.out.println("Now:"+System.currentTimeMillis());
        System.out.println("\n=====Scheduled Task===== => Create thread delete File Cache after:"+(timestamp - System.currentTimeMillis())+" second");
        getThread().schedule(generateTaskDeleteFileCache(), timestamp - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    private TaskV2 generateTaskDeleteFileCache() {
        return new TaskV2(null, null) {
            @Override
            public Object call() {
                long now = System.currentTimeMillis();
                InternalResponse getAPILogs = new UtilsSummaryInternal().getAPILogs();
                if (!getAPILogs.isValid()) {
                    LogHandler.error(ScheduledThreadManagement.class,
                            "",
                            "Cannot initial Scheduled Thread Management");
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
                System.out.println("\n=====Scheduled Task ===== => Delete file cache successfull");
                System.out.println("\n=====Scheduled Task===== => Start create schedule for initial after " + nextTimeInSecond + " second");
                getThread().schedule(new TaskV2(null, null) {
                    @Override
                    public Object call() {
                        initial();
                        return null;
                    }
                }, nextTimeInSecond, TimeUnit.SECONDS);
                return null;
            }
        };
    }

    private TaskV2 generateInitial() {
        return new TaskV2(null, null) {
            @Override
            public Object call() {
                InternalResponse getAPILogs = new UtilsSummaryInternal().getAPILogs();
                if (!getAPILogs.isValid()) {
                    LogHandler.error(ScheduledThreadManagement.class,
                            "",
                            "Cannot initial Scheduled Thread Management");
                }
                List<APILog> apiLogs = (List<APILog>) getAPILogs.getData();
                for (APILog apiLog : apiLogs) {
                    List<FileCached> fileCaches = apiLog.getFileCaches();

                    for (FileCached fileCache : fileCaches) {
                        long timeStampOfFileCached = vn.mobileid.id.FPS.utils.Utils.getTimeStamp(fileCache.getTimeStamp());
                        if (timestamp > timeStampOfFileCached) {
                            timestamp = timeStampOfFileCached;
                        }
                        if (nextTimeInSecond > fileCache.getTime()) {
                            nextTimeInSecond = fileCache.getTime();
                        }
                    }
                }

                return null;
            }
        };
    }

    private ScheduledExecutorService getThread() {
        return (ScheduledExecutorService) scheduledPool.getExecutorService();
    }

    public static void main(String[] args) throws Exception {
        ScheduledThreadManagement temp = ScheduledThreadManagement.getInstance();
        
    }

}
