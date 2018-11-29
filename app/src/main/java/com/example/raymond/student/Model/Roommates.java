package com.example.raymond.student.Model;

public class Roommates {
    private String fullName, phone, parentNo, bedNumber, profilePic, chaletName;

    public Roommates() {
    }

    public Roommates(String fullName, String phone, String parentNo, String profilePic , String bedNumber, String chaletName) {
        this.fullName = fullName;
        this.phone = phone;
        this.parentNo = parentNo;
        this.bedNumber = bedNumber;
        this.chaletName = chaletName;
        this.profilePic = profilePic;
    }



    public void setBedNumber(String bedNumber) {
        this.bedNumber = bedNumber;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getParentNo() {
        return parentNo;
    }

    public void setParentNo(String parentNo) {
        this.parentNo = parentNo;
    }

    public String getBedNumber() {
        return bedNumber;
    }

    public String getChaletName() {
        return chaletName;
    }

    public void setChaletName(String chaletName) {
        this.chaletName = chaletName;
    }
}
