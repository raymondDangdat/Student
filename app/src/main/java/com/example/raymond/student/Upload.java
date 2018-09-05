package com.example.raymond.student;

public class Upload {
    private String surname;
    private String firstName;
    private String username;
    private String email;
    private String gender;
    private String department;
    private String image;

    //empty constructor
    public Upload(){

    }

    public Upload(String surname, String firstName, String username, String image, String email, String gender, String department) {
        this.surname = surname;
        this.firstName = firstName;
        this.username = username;
        this.email = email;
        this.gender = gender;
        this.department = department;
        this.image = image;
    }

    public String getSurname() {
        return surname;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image){
        this.image = image;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
