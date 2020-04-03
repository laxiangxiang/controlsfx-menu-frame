package com.shdq.menu_frame.frame.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author shdq-fjy
 */
@Slf4j
public class DBPoolUtil {
    private static DruidDataSource druidDataSource = null;
    static {
        Properties properties = loadPropertiesFile();
        try {
            druidDataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("数据库连接池配置失败。");
        }
    }

    private DBPoolUtil() {
    }

    private static class Holder{
        private static DBPoolUtil dbPoolUtil = new DBPoolUtil();
    }

    public static DBPoolUtil getInstance(){
        return Holder.dbPoolUtil;
    }

    private static Properties loadPropertiesFile() {
        InputStream is = DBPoolUtil.class.getResourceAsStream("/db_server.properties");
        Properties properties = new Properties();
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return properties;
    }

    public static DruidPooledConnection getConnection() throws SQLException {
        return druidDataSource.getConnection();
    }
}
