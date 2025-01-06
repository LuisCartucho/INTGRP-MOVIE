package easv.intgrpmovie.dal;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {

    // Database connection details
    private static final String DATABASE_NAME = "INTGRP-MOVIE";
    private static final String USER = "CSe2024b_e_18";
    private static final String PASSWORD = "CSe2024bE18!24";
    private static final String SERVER_NAME = "EASV-DB4";
    private static final int PORT_NUMBER = 1433;

    /**
     * Establishes and returns a connection to the database.
     *
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {

        SQLServerDataSource dataSource = new SQLServerDataSource();
        dataSource.setDatabaseName(DATABASE_NAME);
        dataSource.setUser(USER);
        dataSource.setPassword(PASSWORD);
        dataSource.setServerName(SERVER_NAME);
        dataSource.setPortNumber(PORT_NUMBER);
        dataSource.setTrustServerCertificate(true);

        return dataSource.getConnection();
    }
}



