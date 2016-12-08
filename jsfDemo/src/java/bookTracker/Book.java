/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookTracker;

import java.time.LocalDate;

/**
 *
 * @author rory0
 */
public class Book {
    private String title;
    private String authors;
    private long isbn;
    private String publisher;
    private int published;
    private String cover;

    public long getIsbn() {
        return isbn;
    }

    public void setIsbn(long isbn) {
        this.isbn = isbn;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
    
    public Book(String title, String authors, long isbn, String publisher, int published) {
        this.title = title;
        this.authors = authors;
        this.isbn = isbn;
        this.publisher = publisher;
        this.published = published;
        this.cover = "http://images.amazon.com/images/P/" + isbn + ".01.LZ.jpg";
    }

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
    
    public String getISBNstring() {
        long temp = isbn;
        int n;
        if (isbn <= 999999999) {
            n = 9;
        }
        else {
            n = 13;
        }
        
        String s = "";
        for (int i = 0; i < n; i++) {
            s = Long.toString(temp % 10) + s;
            temp = temp / 10;
        }
        return s;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getPublished() {
        return published;
    }

    public void setPublished(int published) {
        this.published = published;
    }
    
    
}
