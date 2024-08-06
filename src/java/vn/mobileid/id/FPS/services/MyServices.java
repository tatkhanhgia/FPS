/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import fps.readqr.enumeration.InputType;
import vn.mobileid.id.FPS.services.impls.FmsService;
import vn.mobileid.id.FPS.services.impls.json.JacksonJsonService;
import vn.mobileid.id.FPS.services.impls.QRDetectionService;
import vn.mobileid.id.FPS.services.impls.ZipService;
import vn.mobileid.id.FPS.services.impls.databaseConnection.DatabaseConnectionManager;
import vn.mobileid.id.FPS.services.others.threadManagement.ThreadManagement;
import vn.mobileid.id.FPS.services.interfaces.IDatabaseConnection;
import vn.mobileid.id.FPS.services.interfaces.IFms;
import vn.mobileid.id.FPS.services.interfaces.IJsonService;
import vn.mobileid.id.FPS.services.interfaces.IQRDetectionService;
import vn.mobileid.id.FPS.services.interfaces.IZip;
import vn.mobileid.id.FPS.services.others.threadManagement.ThreadFactory;

/**
 * Provides static factory methods to obtain instances of service interfaces.
 * <p>
 * Cung cấp các phương thức tĩnh để lấy các instance của các giao diện dịch vụ.
 *
 * @author GiaTK
 */
public class MyServices {

    //<editor-fold defaultstate="collapsed" desc="Get JsonService">
    /**
     * Returns an instance of {@link IJsonService} implemented by {@link JacksonJsonService}.
     * <p>
     * Trả về một instance của {@link IJsonService} được triển khai bởi {@link JacksonJsonService}.
     *
     * @return An implementation of {@link IJsonService} for JSON serialization and deserialization.
     * Một implementation của {@link IJsonService} cho việc chuyển đổi đối tượng Java thành chuỗi JSON và ngược lại.
     */
    public static IJsonService getJsonService() {
        return new JacksonJsonService();
    }

    /**
     * Returns an instance of {@link IJsonService} implemented by {@link JacksonJsonService}.
     * <p>
     * Trả về một instance của {@link IJsonService} được triển khai bởi {@link JacksonJsonService}.
     *
     * @param objectMapper
     * @return An implementation of {@link IJsonService} for JSON serialization and deserialization.
     * Một implementation của {@link IJsonService} cho việc chuyển đổi đối tượng Java thành chuỗi JSON và ngược lại.
     */
    public static IJsonService getJsonService(ObjectMapper objectMapper) {
        return new JacksonJsonService(objectMapper);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Database Connection">
    /**
     * Get database Connection
     *
     * @return IDatabaseConnection
     */
    public static IDatabaseConnection getDatabaseConnection() {
        return new DatabaseConnectionManager();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Thread Management">
    /**
     * Returns an instance of {@link ThreadManagement} for managing threads.
     * <p>
     * Trả về một instance của {@link ThreadManagement} để quản lý các luồng.
     *
     * @return An instance of {@link ThreadManagement}.
     * Một instance của {@link ThreadManagement}.
     */
    public static ThreadManagement getThreadManagement(int numberTask) {
        return ThreadFactory.newCachedThreadPool(numberTask);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Zip Service">
    /**
     * Returns an instance of the {@link IZip} interface implemented by the {@link ZipService} class.
     * <p>
     * Trả về một phiên bản của giao diện {@link IZip} được triển khai bởi lớp {@link ZipService}.
     *
     * @return An instance of {@link IZip} for performing ZIP compression and extraction operations.
     * Một phiên bản của {@link IZip} để thực hiện các thao tác nén và giải nén ZIP.
     */
    public static IZip getZipService() {
        return new ZipService();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Get FMS Service">
    public static IFms getFMSService(){
        return new FmsService();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Get FMS Service">
    public static IQRDetectionService getQRDetectionService(InputType type){
        return new QRDetectionService(type);
    }
    //</editor-fold>
}
