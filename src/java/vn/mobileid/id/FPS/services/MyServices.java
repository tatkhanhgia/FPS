/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import vn.mobileid.id.FPS.services.impls.JacksonJsonService;
import vn.mobileid.id.FPS.services.impls.databaseConnection.DatabaseConnectionManager;
import vn.mobileid.id.FPS.services.others.threadManagement.ThreadManagement;
import vn.mobileid.id.FPS.services.interfaces.IDatabaseConnection;
import vn.mobileid.id.FPS.services.interfaces.IJsonService;
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
     *         Một implementation của {@link IJsonService} cho việc chuyển đổi đối tượng Java thành chuỗi JSON và ngược lại. 
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
     *         Một implementation của {@link IJsonService} cho việc chuyển đổi đối tượng Java thành chuỗi JSON và ngược lại. 
     */
    public static IJsonService getJsonService(ObjectMapper objectMapper) {
        return new JacksonJsonService(objectMapper);
    }
    //</editor-fold>
     
    //<editor-fold defaultstate="collapsed" desc="Get Database Connection">
    /**
     * Get database Connection
     * @return IDatabaseConnection
     */
    public static IDatabaseConnection getDatabaseConnection(){
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
     *         Một instance của {@link ThreadManagement}.
     */
    public static ThreadManagement getThreadManagement(){
        return ThreadFactory.newCachedThreadPool();
    }
    //</editor-fold>
}