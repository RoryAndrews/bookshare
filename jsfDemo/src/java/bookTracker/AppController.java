/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookTracker;

import java.io.Serializable;
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
    private BookDatabase catalog;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        
    }
    
    public AppController() {
        catalog = new BookDatabase();
        catalog.createTable();
    }
}
