/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookTracker;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import oracle.jdbc.pool.OracleDataSource;

/**
 *
 * @author rory0
 */
@ManagedBean(name="BookCatalogBean")
@SessionScoped
public class BookCatalogBean implements Serializable {
    private static PreparedStatement poStmt;
    private static Statement selectStmt;
    private static ResultSet rs;
    private static Connection conn;
    
    /*needed for properties file*/
    static Properties connProps = new Properties();
    
    public BookCatalogBean() {
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
    
    public List<Book> getBooks() {
        List<Book> list = new ArrayList<Book>();
        System.out.println("GETTING Book Catalog.");

        try {
            selectStmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                                              ResultSet.CONCUR_READ_ONLY);
            rs = selectStmt.executeQuery("SELECT * FROM DBUSER.BOOKCATALOG");
            while (rs.next()) {
                Book b = new Book(rs.getString("title"), rs.getString("author"), rs.getLong("isbn"), rs.getString("publisher"), LocalDate.ofEpochDay(rs.getLong("published")));
                list.add(b);
            }

            selectStmt.close();
        } catch (SQLException sqle) {
            System.out.println("ERROR IN getBooks");
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
    
    public Boolean addBook(Book addition) {
        try {
            selectStmt = conn.createStatement();

            selectStmt.executeUpdate("INSERT INTO BOOKCATALOG VALUES ('"
                    + addition.getTitle() + "','" + addition.getAuthors() + "'," + addition.getISBN() + ",'" + addition.getPublisher() + "','" + addition.getPublished() + "')");

            selectStmt.close();
        } catch (SQLException sqle) {
            System.out.println("ERROR IN addBook");
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
            ResultSet rs = dmd.getTables(null, null, "BOOKCATALOG", null);
            if(!rs.next()) {
                selectStmt.executeUpdate("CREATE TABLE BOOKCATALOG ("
                        + "title varchar(256) NOT NULL,"
                        + "author varchar(256) NOT NULL,"
                        + "isbn number(13,0) NOT NULL,"
                        + "publisher varchar(256),"
                        + "published long,"
                        + "PRIMARY KEY (isbn),"
                        + "CHECK (isbn <= 9999999999999)"
                        + ")");
                System.out.println("TABLE CREATED.");
            }
            rs = dmd.getTables(null, null, "BOOKLISTS", null);
            if(!rs.next()) {
                selectStmt.executeUpdate("CREATE TABLE BOOKLISTS ("
                        + "user varchar(32) NOT NULL,"
                        + "isbn number(13,0) NOT NULL,"
                        + "PRIMARY KEY (user, isbn),"
                        + "FOREIGN KEY (isbn) REFERENCES BOOKCATALOG(isbn)"
                        + ")");
                System.out.println("TABLE CREATED.");
            }

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