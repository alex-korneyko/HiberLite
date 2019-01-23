package ua.in.korneiko.testHiberLite;

import ua.in.korneiko.hiberlite.annotations.*;

@Entity
public class Post {

    @Id
    @Autoincrement
    @Column
    @NotNull
    private int id;

    @Column
    @NotNull
    private String postName;

    @Column
    private double salaryCoefficient;

    public Post() {
    }

    public Post(String postName, double salaryCoefficient) {
        this.postName = postName;
        this.salaryCoefficient = salaryCoefficient;
    }

    public Post(int id, String postName, double salaryCoefficient) {
        this.id = id;
        this.postName = postName;
        this.salaryCoefficient = salaryCoefficient;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public double getSalaryCoefficient() {
        return salaryCoefficient;
    }

    public void setSalaryCoefficient(double salaryCoefficient) {
        this.salaryCoefficient = salaryCoefficient;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", postName='" + postName + '\'' +
                ", salaryCoefficient=" + salaryCoefficient +
                '}';
    }
}
