package com.example.raymond.student.Model;

public class JambConfirmation {
    private String JambNo;
    private String Status;

    public JambConfirmation() {
    }

    public JambConfirmation(String jambNo, String status) {
        JambNo = jambNo;
        Status = status;
    }

    public String getJambNo() {
        return JambNo;
    }

    public void setJambNo(String jambNo) {
        JambNo = jambNo;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
