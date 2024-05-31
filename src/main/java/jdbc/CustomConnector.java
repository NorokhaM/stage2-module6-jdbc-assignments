package jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public class CustomConnector {
    public Connection getConnection(String url) throws SQLException{
        return getConnection(url);
    }

    public Connection getConnection(String url, String user, String password)  throws SQLException   {
        return getConnection(url, user, password);
    }
}
