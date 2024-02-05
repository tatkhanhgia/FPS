/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.database;

import org.apache.commons.dbcp2.BasicDataSource;
import vn.mobileid.id.general.Configuration;

/**
 *
 * @author VUDP
 */
public class DataSourceReadOnly {

    private static final String DRIVER_CLASS_NAME = Configuration.getInstance().getDbReadOnlyDriver();
    private static final String DB_URL = Configuration.getInstance().getDbReadOnlyUrl();
    private static final String DB_USER = Configuration.getInstance().getDbReadOnlyUsername();
    private static final String DB_PASSWORD = Configuration.getInstance().getDbReadOnlyPassword();
    
    private static final int CONN_POOL_SIZE = Configuration.getInstance().getMaxConnection();
    private static final int INIT_CONN = Configuration.getInstance().getInitPoolSize();
    private static final int MIN_IDLE = Configuration.getInstance().getMinPoolIdle();
    private static final int MAX_IDLE = Configuration.getInstance().getMaxPoolIdle();

    private BasicDataSource bds = new BasicDataSource();

    private DataSourceReadOnly() {
        bds.setDriverClassName(DRIVER_CLASS_NAME);
        bds.setUrl(DB_URL);
        bds.setUsername(DB_USER);
        bds.setPassword(DB_PASSWORD);

        bds.setInitialSize(INIT_CONN);
        bds.setMinIdle(MIN_IDLE);
        bds.setMaxIdle(MAX_IDLE);
        bds.setMaxTotal(CONN_POOL_SIZE);
        bds.setTestWhileIdle(true);
        bds.setValidationQuery("SELECT 1;");
        bds.setValidationQueryTimeout(1);
        bds.setTimeBetweenEvictionRunsMillis(60000);
        bds.setDefaultAutoCommit(true);
        bds.setMaxWaitMillis(3000);
    }

    private static class DataSourceHolder {

        private static final DataSourceReadOnly INSTANCE = new DataSourceReadOnly();
    }

    public static DataSourceReadOnly getInstance() {
        return DataSourceHolder.INSTANCE;
    }

    public BasicDataSource getBds() {
        return bds;
    }

    public void setBds(BasicDataSource bds) {
        this.bds = bds;
    }
}
