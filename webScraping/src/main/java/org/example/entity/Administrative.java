package org.example.entity;

public class Administrative extends Teacher{

    private String branch_line;
    private String room;

    public Administrative() {
    }

    public Administrative(String name, String email, String field, String branch_line, String room) {
        super(name, email, field);
        this.branch_line = branch_line;
        this.room = room;
    }

    public String getBranch_line() {
        return branch_line;
    }

    public void setBranch_line(String branch_line) {
        this.branch_line = branch_line;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    @Override
    public String toString() {
        return "{ \"name\": \"" + this.getName() + "\", "
                + "\"email\": \"" + this.getEmail() + "\", "
                + "\"field\": \"" + this.getField() + "\", "
                + "\"branch_line\": \"" + this.getBranch_line() + "\", "
                + "\"room\": \"" + this.getRoom() + "\" }";
    }
}
