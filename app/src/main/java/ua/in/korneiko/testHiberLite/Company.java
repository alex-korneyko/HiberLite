package ua.in.korneiko.testHiberLite;

import org.jetbrains.annotations.Contract;
import ua.in.korneiko.hiberlite.annotations.*;

import java.util.List;
import java.util.Objects;

@Entity
public class Company {

    @Id
    @Autoincrement
    @Column
    private int id;

    @Column
    @SearchKey
    private String companyName;

    @Column
    private List<String> threeAnyStrings;

//    @Column
//    private List<Floor> floors;


    public Company() {
    }

    public Company(String companyName, List<String> threeAnyStrings/*, List<Floor> floors*/) {
        this.companyName = companyName;
        this.threeAnyStrings = threeAnyStrings;
//        this.floors = floors;
    }

    public Company(int id, String companyName, List<String> threeAnyStrings, List<Floor> floors) {
        this.id = id;
        this.companyName = companyName;
        this.threeAnyStrings = threeAnyStrings;
//        this.floors = floors;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public List<String> getThreeAnyStrings() {
        return threeAnyStrings;
    }

    public void setThreeAnyStrings(List<String> threeAnyStrings) {
        this.threeAnyStrings = threeAnyStrings;
    }

//    public List<Floor> getFloors() {
//        return floors;
//    }
//
//    public void setFloors(List<Floor> floors) {
//        this.floors = floors;
//    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return Objects.equals(companyName, company.companyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyName);
    }
}
