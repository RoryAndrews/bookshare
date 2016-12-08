/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookTracker;

import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author rory0
 */
@ManagedBean(name="AppController", eager = true)
@SessionScoped
public class AppController implements Serializable {
    private String username;
    private Book specificBook;
    private BookDatabase catalog;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public String viewBook(long isbn) {
        this.specificBook = catalog.findBook(isbn);
        return "book";
    }
    
    public String getBookCover() {
        return specificBook.getCover();
    }
    
    public String getBookTitle() {
        return specificBook.getTitle();
    }
    
    public String getBookAuthors() {
        return specificBook.getAuthors();
    }

    public String getBookISBN() {
        return specificBook.getISBNstring();
    }
    
    public String getBookPublisher() {
        return specificBook.getPublisher();
    }
    
    public String getBookPublished() {
        return Integer.toString(specificBook.getPublished());
    }
    
    public List<Book> getReadingList() {
        return catalog.getList(username, 'N');
    }
    
    public List<Book> getFinishedList() {
        return catalog.getList(username, 'Y');
    }
    
    public String addToList() {
        if (this.username != null) {
            return catalog.addToList(this.username, specificBook.getIsbn());
        }
        return "greeting";
    }
    
    public String moveToFinished(long isbn) {
        return catalog.updateList(this.username, isbn, true);
    }
    
    public String moveToReading(long isbn) {
        return catalog.updateList(this.username, isbn, false);
    }
    
    public String goReadingList() {
        if(username != null) {
            return "readinglist";
        }
        else {
            return "greeting";
        }
    }
    
    public AppController() {
        specificBook = null;
        username = null;
        catalog = new BookDatabase();
        catalog.createTable();
    }
}
