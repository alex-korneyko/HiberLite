package ua.in.korneiko.testHiberLite;

import org.jetbrains.annotations.Contract;
import ua.in.korneiko.hiberlite.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Company implements ua.in.korneiko.hiberlite.Entity {

    @Id
    @Autoincrement
    @Column
    private int id;

    @Column
    @SearchKey
    private String companyName;

    @Column
    private String companyLegalAddress;

    @Column
    private List<Branch> branches = new ArrayList<>();

    @Column
    private List<Employee> mainOfficeEmployees = new ArrayList<>();

    public Company() {
    }

    public Company(String companyName, String companyLegalAddress, List<Branch> branches) {
        this.companyName = companyName;
        this.companyLegalAddress = companyLegalAddress;
        this.branches = branches;
    }

    public Company(int id, String companyName, String companyLegalAddress, List<Branch> branches) {
        this.id = id;
        this.companyName = companyName;
        this.companyLegalAddress = companyLegalAddress;
        this.branches = branches;
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

    public String getCompanyLegalAddress() {
        return companyLegalAddress;
    }

    public void setCompanyLegalAddress(String companyLegalAddress) {
        this.companyLegalAddress = companyLegalAddress;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }

    public List<Employee> getMainOfficeEmployees() {
        return mainOfficeEmployees;
    }

    public void setMainOfficeEmployees(List<Employee> mainOfficeEmployees) {
        this.mainOfficeEmployees = mainOfficeEmployees;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", companyName='" + companyName + '\'' +
                ", companyLegalAddress='" + companyLegalAddress + '\'' +
                ", branches=" + branches +
                '}';
    }

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
