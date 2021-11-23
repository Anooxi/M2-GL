package models;

public class User {
    private static int globalId = 0;
    private final int id;
    private String name;
    private int age;
    private String email;
    private String password; // TODO : crypt password

    public User(String newName, int newAge, String newEmail, String newPassword){
        this.name = newName;
        this.age = newAge;
        this.email = newEmail;
        this.password = newPassword;
        this.id = globalId++;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
