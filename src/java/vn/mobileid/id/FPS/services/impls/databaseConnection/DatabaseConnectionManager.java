/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.FPS.services.impls.databaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.FPS.services.interfaces.IDatabaseConnection;
import vn.mobileid.id.FPS.systemManagement.Configuration;

/**
 *
 * @author VUDP
 */
public class DatabaseConnectionManager implements IDatabaseConnection{

    private static final Logger LOG = LogManager.getLogger(DatabaseConnectionManager.class);
//
    private static DatabaseConnectionManager instance;

    public static DatabaseConnectionManager getInstance() {
        if (instance == null) {
            instance = new DatabaseConnectionManager();
        }
        return instance;
    }

    public final AtomicInteger activeSessions = new AtomicInteger(0);

    private final String readOnlyUrl;
    private final String readOnlyUsername;
    private final String readOnlyPassword;

    private final String writeOnlyUrl;
    private final String writeOnlyUsername;
    private final String writeOnlyPassword;

    public DatabaseConnectionManager() {

        readOnlyUrl = Configuration.getInstance().getDbReadOnlyUrl();        
        readOnlyUsername = Configuration.getInstance().getDbReadOnlyUsername();
        readOnlyPassword = Configuration.getInstance().getDbReadOnlyPassword();

        writeOnlyUrl = Configuration.getInstance().getDbWriteOnlyUrl();
        writeOnlyUsername = Configuration.getInstance().getDbWriteOnlyUsername();
        writeOnlyPassword = Configuration.getInstance().getDbWriteOnlyPassword();        
    }

    public Connection openConnectionUsingDataSource() {
        Connection conn = null;
        try {
            BasicDataSource bds = DataSource.getInstance().getBds();
            conn = bds.getConnection();
        } catch (Exception e) {
            LOG.error("Cannot open new connection. Details: " + e.toString());
            e.printStackTrace();
        }
        return conn;
    }

    public Connection openReadOnlyConnection() {
        if (readOnlyUrl == null
                || readOnlyUsername == null
                || readOnlyPassword == null) {
            return openConnectionUsingDataSource();
        } else {
            Connection conn = null;
            try {
                BasicDataSource bds = DataSourceReadOnly.getInstance().getBds();
                conn = bds.getConnection();                
            } catch (Exception e) {
                LOG.error("Cannot open new connection. Details: " + e.toString());
                e.printStackTrace();
            }
            return conn;
        }
    }

    public Connection openWriteOnlyConnection() {
        if (writeOnlyUrl == null
                || writeOnlyUsername == null
                || writeOnlyPassword == null) {
            return openConnectionUsingDataSource();
        } else {
            Connection conn = null;
            try {
                BasicDataSource bds = DataSourceWriteOnly.getInstance().getBds();
                conn = bds.getConnection();
            } catch (Exception e) {
                LOG.error("Cannot open new connection. Details: " + e.toString());
                e.printStackTrace();
            }
            return conn;
        }
    }

    public void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                activeSessions.decrementAndGet();
            } catch (SQLException e) {
                LOG.error("Cannot close connection. Details: " + e.toString());
                e.printStackTrace();
            }
        }
    }

    @Override
    public Connection getConnection() throws Exception {
        return this.openWriteOnlyConnection();
    }

    @Override
    public Connection getReadOnlyConnection() throws Exception {
        return this.openReadOnlyConnection();
    }

    @Override
    public Connection getWriteOnlyConnection() throws Exception {
        return this.openWriteOnlyConnection();
    }

    @Override
    public void closeConnection(Connection connection) throws Exception {
        this.close(connection);
    }
}
