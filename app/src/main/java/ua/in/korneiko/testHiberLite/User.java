package ua.in.korneiko.testHiberLite;

import ua.in.korneiko.hiberlite.EntityObject;
import ua.in.korneiko.hiberlite.annotations.*;

@Entity
public class User implements EntityObject {

    @Column
    @Id
    @Autoincrement
    private int id;

    @Column
    @SearchKey
    private String userName;

    @Column
    private String password;

    @Column
    private boolean enabled;


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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
