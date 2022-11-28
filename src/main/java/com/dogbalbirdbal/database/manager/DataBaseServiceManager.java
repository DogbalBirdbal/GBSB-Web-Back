package com.dogbalbirdbal.database.manager;

import com.dogbalbirdbal.database.data.DBTransaction;
import com.dogbalbirdbal.database.vo.UserInfo;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class DataBaseServiceManager {

    private static class SingleTon {
        private static final DataBaseServiceManager DATA_BASE_SERVICE_MANAGER = new DataBaseServiceManager();
    }

    public static DataBaseServiceManager getInstance() {
        return SingleTon.DATA_BASE_SERVICE_MANAGER;
    }


    ExecutorService executorService = Executors.newFixedThreadPool(5);
    CompletionService<UserInfo> service;
    DataSource dataSource;

    public void loadDataSource(String id, String password, String ip, int port, String database) {
        service = new ExecutorCompletionService<>(executorService);
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:postgresql://%s:%s/%s", ip, port, database)); // 데이터 서버의 IP , port , DB 명
        config.setUsername(id); // config 에 등록된 관리자 계정명
        config.setPassword(password); // config 에 등록된 관리자 패스워드
        config.addDataSourceProperty("cachePrepStmts", "true"); // PreparedStatement Caching을 비활성화하고 있기 때문에, 이 옵션을 허용해줘야 아래의 옵션값들이 실제 DB에 영향을 줄 수 있다.
        config.addDataSourceProperty("prepStmtCacheSize", "350"); // MySQL 드라이버가 Connection마다 캐싱할 PreparedStatement의 개수를 지정하는 옵션이다. HikariCP에서는 250 ~ 500개 정도를 추천한다
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048"); // default : 256 max : 2048 크게 중요하지 않다. 데이터 캐싱 관련이며 PreparedStatement 와 연관되어있다.

        config.setMaximumPoolSize(3);

        config.setMaxLifetime(580000);
        config.setIdleTimeout(10000);
        config.setConnectionTimeout(10000);
        config.setValidationTimeout(10000);
        config.setMinimumIdle(20);
        config.setPoolName("DB");
        config.setLeakDetectionThreshold(24000);
        dataSource = new HikariDataSource(config);

    }

    public void close() {
        if ( dataSource != null ) {
            ((HikariDataSource) dataSource).close();
            dataSource = null;
        }
    }

    public Connection getConnection() throws SQLException {
        if ( dataSource != null ) {
            return dataSource.getConnection();
        } else {
            return null;
        }
    }

    public UserInfo getUserInfo(String colType, String colData) {

        UserInfo userInfo = null;

        try ( Connection connection = getConnection() ) {
            PreparedStatement preparedStatement = connection.prepareStatement(String.format("select * from MyUser where %s = ?", colType));
            preparedStatement.setString(1, colData);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString("uid");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                String name = resultSet.getString("name");
                userInfo = new UserInfo(id, password, email,name);
            }
        } catch ( SQLException e) {
            e.printStackTrace();
        }

        return userInfo;

    }



    public boolean taskTransaction(DBTransaction dbTransaction) {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            dbTransaction.task(connection);
            connection.commit();
            connection.close();
            return true;
        } catch ( SQLException e ) {
            e.printStackTrace();
            try {
                if (connection != null) {
                    connection.rollback();
                    connection.close();
                }
            } catch ( SQLException es ) {
                es.printStackTrace();
            }
        }

        return false;

    }


}
