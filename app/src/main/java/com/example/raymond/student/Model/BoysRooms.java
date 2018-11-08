package com.example.raymond.student.Model;

public class BoysRooms {
    private  String BedNumber, Room, Image, Status, RoomDescription;

    public BoysRooms() {
    }

    public BoysRooms(String bedNumber, String room, String image, String status, String roomDescription) {
        BedNumber = bedNumber;
        Room = room;
        Image = image;
        RoomDescription = roomDescription;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getBedNumber() {
        return BedNumber;
    }

    public void setBedNumber(String bedNumber) {
        BedNumber = bedNumber;
    }

    public String getRoom() {
        return Room;
    }

    public void setRoom(String room) {
        Room = room;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getRoomDescription() {
        return RoomDescription;
    }

    public void setRoomDescription(String roomDescription) {
        RoomDescription = roomDescription;
    }
}
