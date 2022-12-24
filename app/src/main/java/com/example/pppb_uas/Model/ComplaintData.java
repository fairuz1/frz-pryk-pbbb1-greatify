package com.example.pppb_uas.Model;

public class ComplaintData {
    private String id, title, descriptions, dateHappened, locationHappened, sender;

    public ComplaintData() {
        // empty constructor
    }

    public ComplaintData(String id, String title, String descriptions, String dateHappened, String locationHappened, String sender) {
        this.id = id;
        this.title = title;
        this.descriptions = descriptions;
        this.dateHappened = dateHappened;
        this.locationHappened = locationHappened;
        this.sender = sender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public String getDateHappened() {
        return dateHappened;
    }

    public void setDateHappened(String dateHappened) {
        this.dateHappened = dateHappened;
    }

    public String getLocationHappened() {
        return locationHappened;
    }

    public void setLocationHappened(String locationHappened) {
        this.locationHappened = locationHappened;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
