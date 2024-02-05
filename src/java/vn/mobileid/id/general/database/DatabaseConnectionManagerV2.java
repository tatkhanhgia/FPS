/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.database;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author GiaTK
 */
public class DatabaseConnectionManagerV2 extends DatabaseConnectionManager{
    private static final Logger LOG = LogManager.getLogger(DatabaseConnectionManagerV2.class);
    
    private static final HashMap<String, Connection> transaction = new HashMap<>();
    
}
