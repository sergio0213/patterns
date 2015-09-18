package co.edu.unbosque.swii;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by alejandro on 4/09/15.
 */
public class SingletonConnection {
    private static Connection connection;
    private static final Boolean control=true;

    private SingletonConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        String url="jdbc:postgresql://aretico.com:5432/software_2";
        synchronized (control) {
            if(connection!=null) return;
            connection = DriverManager.getConnection(url, "grupo2", "9AE7xst0iD");
        }
    }
    public static Connection getConnection() throws ClassNotFoundException, SQLException {

        if(connection==null){
            new SingletonConnection();
        }
        return connection;

    }
}
