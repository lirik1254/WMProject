package org.hse.mylaundryapplication;

public class Users {
    public String first_name, last_name, pat_name,  password;

    public Users() {
    }

    public Users(String first_name, String last_name, String pat_name, String password) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.pat_name = pat_name;
        this.password = password;
    }
}
