package org.hse.mylaundryapplication;

public class Users {
    public String first_name, last_name, pat_name,  password, mail;
    public Integer dormitory, notifications;

    public Users() {
    }

    public Users(String first_name, String last_name, String pat_name, String password, String mail, Integer dormitory, Integer notifications) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.pat_name = pat_name;
        this.password = password;
        this.mail = mail;
        this.dormitory = dormitory;
        if (notifications == null) {
            this.notifications = 0;
        }
        else
            this.notifications = notifications;
    }
}
