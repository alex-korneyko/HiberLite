package ua.in.korneiko.testHiberLite;

import ua.in.korneiko.hiberlite.annotations.Autoincrement;
import ua.in.korneiko.hiberlite.annotations.Column;
import ua.in.korneiko.hiberlite.annotations.Entity;
import ua.in.korneiko.hiberlite.annotations.Id;

@Entity
public class User {

    @Column
    @Id
    @Autoincrement
    private int id;

    @Column
    private String userName;

    @Column
    private String password;

    public User() {
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
