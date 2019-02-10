package ua.in.korneiko.testHiberLite;

import ua.in.korneiko.hiberlite.EntityObject;
import ua.in.korneiko.hiberlite.annotations.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Branch implements EntityObject {

    @Id
    @Autoincrement
    @Column
    private int id;

    @Column
    @SearchKey
    private String branchName;

    @JoinColumn
    private LegalAddress legalAddress;

    @JoinColumn
    private List<Employee> employees = new ArrayList<>();

    public Branch() {
    }

    public Branch(String branchName, LegalAddress legalAddressAddress, List<Employee> employees) {
        this.branchName = branchName;
        this.legalAddress = legalAddressAddress;
        this.employees = employees;
    }

    public Branch(int id, String branchName, LegalAddress legalAddress, List<Employee> employees) {
        this.id = id;
        this.branchName = branchName;
        this.legalAddress = legalAddress;
        this.employees = employees;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public LegalAddress getLegalAddress() {
        return legalAddress;
    }

    public void setLegalAddress(LegalAddress legalAddress) {
        this.legalAddress = legalAddress;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    @Override
    public String toString() {
        return "\nBranch{" +
                "\n\tid=" + id +
                ", \n\tbranchName='" + branchName + '\'' +
                ", \n\tlegalAddress='" + legalAddress + '\'' +
                ", \n\temployees=" + employees +
                "\n}";
    }
}
