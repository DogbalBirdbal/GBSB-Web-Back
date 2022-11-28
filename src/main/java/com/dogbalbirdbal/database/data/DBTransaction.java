package com.dogbalbirdbal.database.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Future;

public interface DBTransaction {

    void task(Connection connection) throws SQLException;

}
