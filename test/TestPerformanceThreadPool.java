
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.FPS.services.others.threadManagement.TaskV2;
import vn.mobileid.id.FPS.services.others.threadManagement.ThreadManagement;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author GiaTK
 */
public class TestPerformanceThreadPool {

    public static long[] flow1(
            String input
    ) throws ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        TaskV2 task1 = new TaskV2(new Object[]{input}, "") {
            @Override
            public Object call() {
                try {
                    Object temp = countAWord((String) this.get()[0]);
                    return temp;
                } catch (Exception e) {
                }
                return null;
            }
        };

        TaskV2 task2 = new TaskV2(new Object[]{input}, "") {
            @Override
            public Object call() {
                try {
                    return (Object) countSpaceWord((String) this.get()[0]);
                } catch (Exception e) {
                }
                return null;
            }
        };

        TaskV2 task3 = new TaskV2(new Object[]{input}, "") {
            @Override
            public Object call() {
                try {
                    return (Object) countNewLineWord((String) this.get()[0]);
                } catch (Exception e) {
                }
                return null;
            }
        };

        TaskV2 task4 = new TaskV2(new Object[]{input}, "") {
            @Override
            public Object call() {
                try {
                    Object temp = countAWord((String) this.get()[0]);
                    return temp;
                } catch (Exception e) {
                }
                return null;
            }
        };

        TaskV2 task5 = new TaskV2(new Object[]{input}, "") {
            @Override
            public Object call() {
                try {
                    Object temp = countAWord((String) this.get()[0]);
                    return temp;
                } catch (Exception e) {
                }
                return null;
            }
        };

        TaskV2 task6 = new TaskV2(new Object[]{input}, "") {
            @Override
            public Object call() {
                try {
                    Object temp = countAWord((String) this.get()[0]);
                    return temp;
                } catch (Exception e) {
                }
                return null;
            }
        };

        long startTime1 = System.nanoTime();
        Future<Object> temp1 = executorService.submit(task1);

        long startTime2 = System.nanoTime();
        Future<Object> temp2 = executorService.submit(task2);

        long startTime3 = System.nanoTime();
        Future<Object> temp3 = executorService.submit(task3);

        long startTime4 = System.nanoTime();
        Future<Object> temp4 = executorService.submit(task4);

        long startTime5 = System.nanoTime();
        Future<Object> temp5 = executorService.submit(task5);

        long startTime6 = System.nanoTime();
        Future<Object> temp6 = executorService.submit(task6);

        try {
            Object result1 = temp1.get();
            long endTime1 = System.nanoTime();
            long duration1 = endTime1 - startTime1;
//            System.out.println("Task 1 execution time: " + duration1 + " ms");
//            System.out.println("Task 1 Result: " + result1);

            Object result2 = temp2.get();
            long endTime2 = System.nanoTime();
            long duration2 = endTime2 - startTime2;
//            System.out.println("Task 2 execution time: " + duration2 + " ms");
//            System.out.println("Task 2 Result: " + result2);

            Object result3 = temp3.get();
            long endTime3 = System.nanoTime();
            long duration3 = endTime3 - startTime3;
//            System.out.println("Task 3 execution time: " + duration3 + " ms");
//            System.out.println("Task 3 Result: " + result3);

            Object result4 = temp4.get();
            long endTime4 = System.nanoTime();
            long duration4 = endTime4 - startTime4;

            Object result5 = temp5.get();
            long endTime5 = System.nanoTime();
            long duration5 = endTime5 - startTime5;

            Object result6 = temp6.get();
            long endTime6 = System.nanoTime();
            long duration6 = endTime6 - startTime6;

            long[] result = new long[6];
            result[0] = duration1;
            result[1] = duration2;
            result[2] = duration3;
            result[3] = duration4;
            result[4] = duration5;
            result[5] = duration6;
            executorService.shutdown();
            return result;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            executorService.shutdown();
        }
        return null;
    }

    public static long[] flow2(
            String input
    ) throws ExecutionException, Exception {
//        ThreadManagement executorService = MyServices.getThreadManagement();
        ExecutorService executorService =  new ThreadPoolExecutor(
                1, // corePoolSize: 0 (không có thread nào được tạo sẵn)
                6, // maximumPoolSize: 10 (giới hạn số lượng thread tối đa)
                10L, // keepAliveTime: 60 giây (thread nhàn rỗi sẽ bị hủy sau 60 giây)
                TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>() // Không lưu trữ tác vụ trong queue,
        );
        TaskV2 task1 = new TaskV2(new Object[]{input}, "") {
            @Override
            public Object call() {
                try {
                    Object temp = countAWord((String) this.get()[0]);
                    return temp;
                } catch (Exception e) {
                }
                return null;
            }
        };

        TaskV2 task2 = new TaskV2(new Object[]{input}, "") {
            @Override
            public Object call() {
                try {
                    return (Object) countSpaceWord((String) this.get()[0]);
                } catch (Exception e) {
                }
                return null;
            }
        };

        TaskV2 task3 = new TaskV2(new Object[]{input}, "") {
            @Override
            public Object call() {
                try {
                    return (Object) countNewLineWord((String) this.get()[0]);
                } catch (Exception e) {
                }
                return null;
            }
        };

        TaskV2 task4 = new TaskV2(new Object[]{input}, "") {
            @Override
            public Object call() {
                try {
                    return (Object) countNewLineWord((String) this.get()[0]);
                } catch (Exception e) {
                }
                return null;
            }
        };

        TaskV2 task5 = new TaskV2(new Object[]{input}, "") {
            @Override
            public Object call() {
                try {
                    return (Object) countNewLineWord((String) this.get()[0]);
                } catch (Exception e) {
                }
                return null;
            }
        };

        TaskV2 task6 = new TaskV2(new Object[]{input}, "") {
            @Override
            public Object call() {
                try {
                    return (Object) countNewLineWord((String) this.get()[0]);
                } catch (Exception e) {
                }
                return null;
            }
        };

        long startTime1 = System.nanoTime();
        Future<Object> temp1 = executorService.submit(task1);

        long startTime2 = System.nanoTime();
        Future<Object> temp2 = executorService.submit(task2);

        long startTime3 = System.nanoTime();
        Future<Object> temp3 = executorService.submit(task3);

        long startTime4 = System.nanoTime();
        Future<Object> temp4 = executorService.submit(task4);

        long startTime5 = System.nanoTime();
        Future<Object> temp5 = executorService.submit(task5);

        long startTime6 = System.nanoTime();
        Future<Object> temp6 = executorService.submit(task6);

        try {
            Object result1 = temp1.get();
            long endTime1 = System.nanoTime();
            long duration1 = endTime1 - startTime1;
//            System.out.println("Task 1 execution time: " + duration1 + " ms");
//            System.out.println("Task 1 Result: " + result1);

            Object result2 = temp2.get();
            long endTime2 = System.nanoTime();
            long duration2 = endTime2 - startTime2;
//            System.out.println("Task 2 execution time: " + duration2 + " ms");
//            System.out.println("Task 2 Result: " + result2);

            Object result3 = temp3.get();
            long endTime3 = System.nanoTime();
            long duration3 = endTime3 - startTime3;
//            System.out.println("Task 3 execution time: " + duration3 + " ms");
//            System.out.println("Task 3 Result: " + result3);

            Object result4 = temp4.get();
            long endTime4 = System.nanoTime();
            long duration4 = endTime4 - startTime4;

            Object result5 = temp5.get();
            long endTime5 = System.nanoTime();
            long duration5 = endTime5 - startTime5;

            Object result6 = temp6.get();
            long endTime6 = System.nanoTime();
            long duration6 = endTime6 - startTime6;

            long[] result = new long[6];
            result[0] = duration1;
            result[1] = duration2;
            result[2] = duration3;
            result[3] = duration4;
            result[4] = duration5;
            result[5] = duration6;
            executorService.shutdown();
            return result;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            executorService.shutdown();
        }
        return null;
    }

    private static int countAWord(String count) {
        int i = 0;
        for (int j = 0; j < count.length(); j++) {
            if (count.charAt(j) == 'a') {
                i++;
            }
        }
        return i;
    }

    private static int countSpaceWord(String count) {
        int i = 0;
        for (int j = 0; j < count.length(); j++) {
            if (count.charAt(j) == ' ') {
                i++;
            }
        }
        return i;
    }

    private static int countNewLineWord(String count) {
        int i = 0;
        for (int j = 0; j < count.length(); j++) {
            if (count.charAt(j) == '\n') {
                i++;
            }
        }
        return i;
    }

    private int countAWordV2(String count) {
        int i = 0;
        String[] split = count.split("A");
        return split.length - 1;
    }

    public static void main(String[] args) throws Exception {
        String count = new String(Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\groupdocs-conversion-22.8.1.jar")));
        long[] result1 = flow1(count);
        long[] result2 = flow1(count);
        long[] result3 = flow1(count);

        long temp1 = (result1[0] + result2[0] + result3[0]) / 3;
        long temp2 = (result1[1] + result2[1] + result3[1]) / 3;
        long temp3 = (result1[2] + result2[2] + result3[2]) / 3;
        long temp4 = (result1[3] + result2[3] + result3[3]) / 3;
        long temp5 = (result1[4] + result2[4] + result3[4]) / 3;
        long temp6 = (result1[5] + result2[5] + result3[5]) / 3;
        System.out.println("Average Task1:" + temp1);
        System.out.println("Average Task2:" + temp2);
        System.out.println("Average Task3:" + temp3);
        System.out.println("Average Task4:" + temp4);
        System.out.println("Average Task5:" + temp5);
        System.out.println("Average Task6:" + temp6);
        System.out.println("Total Average:"+(temp1+temp2+temp3+temp4+temp5+temp6)/6);

        System.out.println("==================================");

        long[] resultV21 = flow2(count);
        long[] resultV22 = flow2(count);
        long[] resultV23 = flow2(count);
        long temp12 = (resultV21[0] + resultV22[0] + resultV23[0]) / 3;
        long temp22 = (resultV21[1] + resultV22[1] + resultV23[1]) / 3;
        long temp32 = (resultV21[2] + resultV22[2] + resultV23[2]) / 3;
        long temp42 = (resultV21[3] + resultV22[3] + resultV23[3]) / 3;
        long temp52 = (resultV21[4] + resultV22[4] + resultV23[4]) / 3;
        long temp62 = (resultV21[5] + resultV22[5] + resultV23[5]) / 3;
        System.out.println("Average v2 Task1:" + temp12);
        System.out.println("Average v2 Task2:" + temp22);
        System.out.println("Average v2 Task3:" + temp32);
        System.out.println("Average v2 Task4:" + temp42);
        System.out.println("Average v2 Task5:" + temp52);
        System.out.println("Average v2 Task6:" + temp62);
        System.out.println("Total Average:"+(temp12+temp22+temp32+temp42+temp52+temp62)/6);

        System.out.println("==================================");
        
        System.out.println("V2 Task1 better:" + (temp12 < temp1));
        System.out.println("V2 Task2 better:" + (temp22 < temp2));
        System.out.println("V2 Task3 better:" + (temp32 < temp3));
        System.out.println("V2 Task4 better:" + (temp42 < temp4));
        System.out.println("V2 Task5 better:" + (temp52 < temp5));
        System.out.println("V2 Task6 better:" + (temp62 < temp6));
    }
}
