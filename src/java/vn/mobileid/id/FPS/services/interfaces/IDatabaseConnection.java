/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.services.interfaces;

import java.sql.Connection;

/**
 *
 * @author GiaTK
 */
public interface IDatabaseConnection {
    Connection getConnection()throws Exception; 
    Connection getReadOnlyConnection()throws Exception; 
    Connection getWriteOnlyConnection()throws Exception; 
    void closeConnection(Connection connection) throws Exception;
}
