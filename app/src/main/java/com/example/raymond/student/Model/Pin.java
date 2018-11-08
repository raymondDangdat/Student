package com.example.raymond.student.Model;

public class Pin {
    private int Pin;
    private String status;

    public Pin() {
    }

    public Pin(int pin, String status) {
        Pin = pin;
        this.status = status;
    }

    public int getPin() {
        return Pin;
    }

    public void setPin(int pin) {
        Pin = pin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
