package ua.in.korneiko.testHiberLite;

import ua.in.korneiko.hiberlite.annotations.Autoincrement;
import ua.in.korneiko.hiberlite.annotations.Column;
import ua.in.korneiko.hiberlite.annotations.Entity;
import ua.in.korneiko.hiberlite.annotations.Id;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Company {

    @Id
    @Autoincrement
    @Column
    private int id;

    @Column
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
}
