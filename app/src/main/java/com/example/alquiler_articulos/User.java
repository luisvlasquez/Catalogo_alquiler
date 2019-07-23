package com.example.alquiler_articulos;

public class User {
    private String Id;
    private String Name;
    private String Lastname;
    private String Email;
    private String Password;

    public User() {

    }

    public User(String id, String name, String lastname, String email, String password) {
        Id = id;
        Name = name;
        Lastname = lastname;
        Email = email;
        Password = password;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLastname() {
        return Lastname;
    }

    public void setLastname(String lastname) {
        Lastname = lastname;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
