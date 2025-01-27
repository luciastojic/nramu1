package models;

public class Users {
    private String name;
    private String email;

    // Prazni konstruktor
    public Users() {
    }

    // Konstruktor s imenom i e-mailom
    public Users(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getteri i setteri
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

    @Override
    public String toString() {
        return "Users{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
