package ua.in.korneiko.testHiberLite;

import ua.in.korneiko.hiberlite.EntityObject;
import ua.in.korneiko.hiberlite.annotations.Column;
import ua.in.korneiko.hiberlite.annotations.Entity;
import ua.in.korneiko.hiberlite.annotations.JoinColumn;

@Entity
public class Employee extends Person implements EntityObject {

    @Column
    private int salary;

    @JoinColumn
    private Post post;

    @JoinColumn
    private User user;

    public Employee() {
    }

    public Employee(String name, String surname, int salary, Post post, User user) {
        super(name, surname);
        this.salary = salary;
        this.post = post;
        this.user = user;
    }

    public Employee(int id, String name, String surname, int salary, Post post, User user) {
        super(id, name, surname);
        this.salary = salary;
        this.post = post;
        this.user = user;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "\nEmployee{" +
                super.toString() +
                "\n\t salary=" + salary +
                ",\n\t post=" + post +
                ",\n\t user=" + user +
                "\n} ";
    }
}
