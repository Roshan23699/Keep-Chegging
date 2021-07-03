package com.example.demo;

public class Account {
    private String email;
    private String status;
    private String password;

    public Account(String email, String status, String password) {
        this.email = email;
        this.status = status;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public String getPassword() {
        return password;
    }
}
