package org.example.entity;

public class Teacher {
    private String name;
    private String email;
    private String field;

    public Teacher() {
    }

    public Teacher(String name, String email, String field) {
        this.name = name;
        this.email = email;
        this.field = field;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    @Override
    public String toString() {
        return "{ \"name\": \"" + name + "\", "
                + "\"email\": \"" + email + "\", "
                + "\"field\": \"" + field + "\" }";
    }
}
