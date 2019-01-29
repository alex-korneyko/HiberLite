package ua.in.korneiko.testHiberLite;

import ua.in.korneiko.hiberlite.annotations.Column;
import ua.in.korneiko.hiberlite.annotations.Entity;
import ua.in.korneiko.hiberlite.annotations.JoinColumn;

@Entity
public class Employee extends Person {

    @Column
    private int salary;

    @JoinColumn
    private Post post;

    public Employee() {
    }

    public Employee(String name, String surname, int salary, Post post) {
        super(name, surname);
        this.salary = salary;
        this.post = post;
    }

    public Employee(int id, String name, String surname, int salary, Post post) {
        super(id, name, surname);
        this.salary = salary;
        this.post = post;
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

    @Override
    public String toString() {
        return "\nEmployee{" +
                super.toString() +
                "\n\t salary=" + salary +
                ",\n\t post=" + post +
                "\n} ";
    }
}
