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
@ManagedBean(name="BookDatabase", eager = true)
@SessionScoped
public class BookDatabase implements Serializable {
    private static PreparedStatement poStmt;
    private static Statement selectStmt;
    private static ResultSet rs;
    private static Connection conn;
    
    private String query;
    private String title;
    private String authors;
    private String isbn;
    private String publisher;
    private String year;
    
    /*needed for properties file*/
    static Properties connProps = new Properties();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    
    public BookDatabase() {
        String db, userName, passwd, host, port;
        host=port=db=userName=passwd=null;
        
        this.query = null;
        this.title = null;
        this.authors = null;
        this.isbn = null;
        this.publisher = null;
        this.year = null;

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
    
    public Book findBook(long specificISBN) {
        System.out.println("FINDING Book.");
        Book b = null;

        try {
            selectStmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                                              ResultSet.CONCUR_READ_ONLY);
            rs = selectStmt.executeQuery("SELECT * FROM BOOKCATALOG WHERE isbn=" + specificISBN);
            while (rs.next()) {
                b = new Book(rs.getString("title"), rs.getString("authors"), rs.getLong("isbn"), rs.getString("publisher"), rs.getInt("published"));
            }

            selectStmt.close();
        } catch (SQLException sqle) {
            System.out.println("ERROR IN findBook");
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
        return b;
    }
    
    public List<Book> getBooks() {
        List<Book> list = new ArrayList<Book>();
        System.out.println("GETTING Book Catalog.");

        try {
            selectStmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                                              ResultSet.CONCUR_READ_ONLY);
            rs = selectStmt.executeQuery("SELECT * FROM DBUSER.BOOKCATALOG");
            while (rs.next()) {
                Book b = new Book(rs.getString("title"), rs.getString("authors"), rs.getLong("isbn"), rs.getString("publisher"), rs.getInt("published"));
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
    
    public String constructQuery() { //String title, String authors, String isbn, String publisher, String year
        boolean previous = false;
        
//        this.title = title;
//        this.authors = authors;
//        this.isbn = isbn;
//        this.publisher = publisher;
//        this.year = year;
        
        String q = "SELECT * FROM BOOKCATALOG WHERE";
        if (this.title.length() > 0) {
            q = q + " LOWER(title) LIKE LOWER( ? )";
            previous = true;
        }
        if (this.authors.length() > 0) {
            if (previous) {
                q = q + " AND";
            }
            q = q + " LOWER(authors) LIKE LOWER( ? )";
            previous = true;
        }
        if (this.isbn.length() > 0) {
            if (previous) {
                q = q + " AND";
            }
            q = q + " isbn = ?";
            previous = true;
        }
        if (this.publisher.length() > 0) {
            if (previous) {
                q = q + " AND";
            }
            q = q + " LOWER(publisher) LIKE LOWER( ? )";
            previous = true;
        }
        if (this.year.length() > 0) {
            if (previous) {
                q = q + " AND";
            }
            q = q + " year = ?";
            previous = true;
        }
        if(previous) {
            this.query = q;
        }
        else {
            this.query = null;
        }
        
        return "booksearch";
    }
    
    public List<Book> findBooks() {
        List<Book> list = new ArrayList<Book>();
        System.out.println("SEARCHING Book Catalog.");
        
        if (query == null) {
            return getBooks();
        }

        try {
            poStmt = conn.prepareStatement(query);
            int i = 1;
            if (this.title.length() > 0) {
                poStmt.setString(i, "%" + this.title + "%");
                i++;
            }
            if (this.authors.length() > 0) {
                poStmt.setString(i, "%" + this.authors + "%");
                i++;
            }
            if (this.isbn.length() > 0) {
                poStmt.setLong(i, Long.parseLong(this.isbn));
                i++;
            }
            if (this.publisher.length() > 0) {
                poStmt.setString(i, "%" + this.publisher + "%");
                i++;
            }
            if (this.year.length() > 0) {
                poStmt.setInt(i, Integer.parseInt(this.year));
                i++;
            }
            rs = poStmt.executeQuery();
            while (rs.next()) {
                Book b = new Book(rs.getString("title"), rs.getString("authors"), rs.getLong("isbn"), rs.getString("publisher"), rs.getInt("published"));
                list.add(b);
            }

            poStmt.close();
        } catch (SQLException sqle) {
            System.out.println("ERROR IN findBooks");
            System.out.println("Query: " + this.query);
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
    
    public String addBook(Book addition) {
        String q = "INSERT INTO BOOKCATALOG VALUES (?, ?, ?, ?, ?, ?)";
        try {
            poStmt = conn.prepareStatement(q);
            
            poStmt.setString(1, addition.getCover());
            poStmt.setString(2, addition.getTitle());
            poStmt.setString(3, addition.getAuthors());
            poStmt.setLong(4, addition.getIsbn());
            poStmt.setString(5, addition.getPublisher());
            poStmt.setInt(6, addition.getPublished());

            poStmt.executeUpdate();

            poStmt.close();
        } catch (SQLException sqle) {
            System.out.println("ERROR IN addBook");
            System.out.println("QUERY: " + q);
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

        return "booksearch";
    }
    
    public String addBook(String title, String author, long isbn, String publisher, int publishedOn) {
        Book b = new Book(title, author, isbn, publisher, publishedOn);
        return addBook(b);
    }
    
    public Boolean createTable() {
        try {
            selectStmt = conn.createStatement();
            DatabaseMetaData dmd = conn.getMetaData();
            ResultSet rs = dmd.getTables(null, null, "BOOKCATALOG", null);
            if(!rs.next()) {
                selectStmt.executeUpdate("CREATE TABLE BOOKCATALOG ("
                        + "cover varchar(128),"
                        + "title varchar(128) NOT NULL,"
                        + "authors varchar(128) NOT NULL,"
                        + "isbn number(13) NOT NULL,"
                        + "publisher varchar(128),"
                        + "published number(4),"
                        + "PRIMARY KEY (isbn)"
                        + ")");
                System.out.println("TABLE BOOKCATALOG CREATED.");
            }
            rs = dmd.getTables(null, null, "BOOKLISTS", null);
            if(!rs.next()) {
                selectStmt.executeUpdate("CREATE TABLE BOOKLISTS ("
                        + "username varchar(16) NOT NULL,"
                        + "isbn number(13) NOT NULL,"
                        + "finished char(1),"
                        + "PRIMARY KEY (username, isbn),"
                        + "FOREIGN KEY (isbn) REFERENCES BOOKCATALOG(isbn),"
                        + "CONSTRAINT chk_finished CHECK (finished = 'Y' OR finished = 'N')"
                        + ")");
                System.out.println("TABLE BOOKLISTS CREATED.");
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