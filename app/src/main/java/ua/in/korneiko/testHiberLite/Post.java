package ua.in.korneiko.testHiberLite;

import org.jetbrains.annotations.Contract;
import ua.in.korneiko.hiberlite.annotations.*;

import java.util.Objects;

@Entity
public class Post implements ua.in.korneiko.hiberlite.Entity {

    @Id
    @Autoincrement
    @Column
    @NotNull
    private int id;

    @Column
    @NotNull
    @SearchKey
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

    public int getId() {
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

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(postName, post.postName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postName);
    }
}
