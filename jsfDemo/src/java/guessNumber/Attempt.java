/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guessNumber;

import java.util.Date;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 *
 * @author rory0
 */
public class Attempt {
    private Timestamp atime;
    private int usernum;
    private int actualnum;

    public Attempt(int guess, int num) {
        /*
        Timestamp code from:
        http://alvinalexander.com/java/java-timestamp-example-current-time-now
        */
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        atime = new java.sql.Timestamp(now.getTime());
        System.out.println("TIMESTAMP:" + atime.toString());
       
        usernum = guess;
        actualnum = num;
    }
    
    public Attempt(Timestamp time, int guess, int num) {
        /*
        Timestamp code from:
        http://alvinalexander.com/java/java-timestamp-example-current-time-now
        */
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        atime = new java.sql.Timestamp(now.getTime());
        System.out.println("TIMESTAMP:" + atime.toString());
       
        usernum = guess;
        actualnum = num;
    }
    
    
    public Timestamp getAtime() {
        return atime;
    }

    public void setAtime(Timestamp atime) {
        this.atime = atime;
    }

    public int getUsernum() {
        return usernum;
    }

    public void setUsernum(int usernum) {
        this.usernum = usernum;
    }

    public int getActualnum() {
        return actualnum;
    }

    public void setActualnum(int actualnum) {
        this.actualnum = actualnum;
    }
    
}
