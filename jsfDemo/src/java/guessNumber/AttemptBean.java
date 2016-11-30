/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guessNumber;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import oracle.jdbc.pool.OracleDataSource;

/**
 *
 * @author rory0
 */
@ManagedBean(name="AttemptBean")
@SessionScoped
public class AttemptBean implements Serializable{
    
    /* Private Members*/
    private static PreparedStatement poStmt;
    private static Statement selectStmt;
    private static ResultSet rs;
    private static Connection conn;

    /*needed for properties file*/
    static Properties connProps = new Properties();

    public AttemptBean() {
        String db, userName, passwd, host, port;
        host=port=db=userName=passwd=null;

        try {
            db="XE";
            userName="DBUSER";
            passwd="ics321";
            host="localhost";
            port="1521";
            System.out.println("OPENING CONNECTION.");
            conn=openConn(db,userName,passwd,host,port);
        }catch (NullPointerException ne) {
            System.out.println("NullPointerException");
        }
    }
    
    public List<Attempt> getAttempts() {
        List<Attempt> list = new ArrayList<Attempt>();
        System.out.println("GETTING ATTEMPTS.");

        try {
            selectStmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                                              ResultSet.CONCUR_READ_ONLY);
            rs = selectStmt.executeQuery("SELECT * FROM DBUSER.ATTEMPTS");
            while (rs.next()) {
                Attempt a = new Attempt(rs.getTimestamp("atime"), rs.getInt("usernum"), rs.getInt("actualnum"));
                list.add(a);
            }

            selectStmt.close();
        } catch (SQLException sqle) {
            System.out.println("ERROR IN getAttempts");
            System.out.println("Error Msg: "+ sqle.getMessage());
            System.out.println("SQLState: "+sqle.getSQLState());
            System.out.println("SQLError: "+sqle.getErrorCode());
            System.out.println("Rollback the transaction and quit the program");
            System.out.println();
            try {
                conn.setAutoCommit(false);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
                System.exit(-1);
            }
            try {
                conn.rollback();
            } catch (Exception e) {
                JdbcException jdbcExc = new JdbcException(e, conn);
                jdbcExc.handle();
            }
            System.exit(1);
        }
        return list;
    }
    
    public Boolean addAttempt(Attempt addition) {
        try {
            selectStmt = conn.createStatement();
            rs = selectStmt.executeQuery("SELECT count(*) as num FROM Attempts");
            
            if (rs.next() && rs.getInt("num") >= 20) {
                selectStmt.executeUpdate("DELETE FROM Attempts "
                                       + "WHERE timestamp >= ALL (SELECT timestamp FROM Attempts)");
            }
            
            selectStmt.executeUpdate("INSERT INTO Attempts VALUES (TIMESTAMP'"
                    + addition.getAtime().toString() + "'," + addition.getActualnum() + "," + addition.getUsernum() + ")");

            selectStmt.close();
        } catch (SQLException sqle) {
            System.out.println("ERROR IN addAttempt");
            System.out.println("Error Msg: "+ sqle.getMessage());
            System.out.println("SQLState: "+sqle.getSQLState());
            System.out.println("SQLError: "+sqle.getErrorCode());
            System.out.println("Rollback the transaction and quit the program");
            System.out.println();
            try {
                conn.setAutoCommit(false);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
                System.exit(-1);
            }
            try {
                conn.rollback();
            } catch (Exception e) {
                JdbcException jdbcExc = new JdbcException(e, conn);
                jdbcExc.handle();
            }
            System.exit(1);
        }

        return true;
    }
    
    public Boolean createTable() {
        try {
            selectStmt = conn.createStatement();
            DatabaseMetaData dmd = conn.getMetaData();
            ResultSet rs = dmd.getTables(null, null, "ATTEMPTS", null);
            if(rs.next()) {
                selectStmt.executeUpdate("DROP TABLE Attempts");
            }
            selectStmt.executeUpdate("CREATE TABLE Attempts (atime Timestamp, usernum integer, actualnum integer)");
            System.out.println("TABLE CREATED.");

            selectStmt.close();
        } catch (SQLException sqle) {
            System.out.println("ERROR IN createTable");
            System.out.println("Error Msg: "+ sqle.getMessage());
            System.out.println("SQLState: "+sqle.getSQLState());
            System.out.println("SQLError: "+sqle.getErrorCode());
            System.out.println("Rollback the transaction and quit the program");
            System.out.println();
            try {
                conn.setAutoCommit(false);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
                System.exit(-1);
            }
            try {
                conn.rollback();
            } catch (Exception e) {
                JdbcException jdbcExc = new JdbcException(e, conn);
                jdbcExc.handle();
            }
            System.exit(1);
        }

        return true;
    }
    
    private static Connection openConn(String db, String userName, String passwd, String host, String port) {
        Connection conn=null;

        try {
            OracleDataSource ds;

            ds = new OracleDataSource();

            /** Create Database URL and establish DB Connection **/
            String databaseURL = "jdbc:oracle:thin:@"+host+":"+port+":"+db;
            ds.setURL(databaseURL); 

            conn = ds.getConnection(userName,passwd);

            /** print any error messages **/
            if (conn==null)System.out.println("Connection Failed \n");
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return conn;

    }
}


class JdbcException extends Exception {
    Connection conn;

    public JdbcException(Exception e) {
        super(e.getMessage());
        conn = null;
    }

    public JdbcException(Exception e, Connection con) {
        super(e.getMessage());
        conn = con;
    }

    public void handle() {
        System.out.println(getMessage());
        System.out.println();

        if (conn != null) {
            try {
                System.out.println("--Rollback the transaction-----");
                conn.rollback();
                System.out.println("  Rollback done!");
            } catch (Exception e) {
            };
        }
    } // handle

    public void handleExpectedErr() {
        System.out.println();
        System.out.println(
            "**************** Expected Error ******************\n");
        System.out.println(getMessage());
        System.out.println(
            "**************************************************");
    } // handleExpectedError
} // JdbcException