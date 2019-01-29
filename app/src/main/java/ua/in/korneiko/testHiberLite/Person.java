package ua.in.korneiko.testHiberLite;

import ua.in.korneiko.hiberlite.annotations.Autoincrement;
import ua.in.korneiko.hiberlite.annotations.Column;
import ua.in.korneiko.hiberlite.annotations.Entity;
import ua.in.korneiko.hiberlite.annotations.Id;

@Entity
public class Person {

    @Column
    @Id
    @Autoincrement
    private int id;

    @Column
    private String name;

    @Column
    private String surname;

    public Person() {
    }

    public Person(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public Person(int id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public String toString() {
        return "\nPerson{" +
                "\n\tid=" + id +
                ",\n\t name='" + name + '\'' +
                ",\n\t surname='" + surname + '\'' +
                "\n}";
    }
}
