package com.example.rfidwriter;

import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlConnectionClass {
    String classes = "net.sourceforge.jtds.jdbc.Driver";
    protected static    String ip = "10.250.200.10";
    protected static    String port = "1546";
    protected static    String db = "MES_SWRE_BACKUP";
    protected static    String un = "mesadmin";
    protected static    String password = "it85@sewon";

    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new  StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy((policy));

        Connection conn = null;

        try {
            Class.forName(classes);

            String conUrl = "jdbc:jtds:sqlserver://"+ip + ":" + port + "/" +db;
            conn = DriverManager.getConnection(conUrl,un,password);

        } catch (ClassNotFoundException | SQLException e) {

            throw  new RuntimeException(e);
        }

        return conn;

    }
}
