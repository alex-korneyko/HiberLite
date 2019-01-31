package ua.in.korneiko.testHiberLite;

import ua.in.korneiko.hiberlite.annotations.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Branch {

    @Id
    @Autoincrement
    @Column
    private int id;

    @Column
    private String branchName;

    @Column
    private String branchAddress;

    @JoinColumn
    private Employee headmaster;

    @Column
    private List<Employee> employees = new ArrayList<>();

    @Column
    private List<String> otherListData = new ArrayList<>();

    public Branch() {
    }

    public Branch(String branchName, String branchAddress, List<Employee> employees) {
        this.branchName = branchName;
        this.branchAddress = branchAddress;
        this.employees = employees;
    }

    public Branch(int id, String branchName, String branchAddress, List<Employee> employees) {
        this.id = id;
        this.branchName = branchName;
        this.branchAddress = branchAddress;
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

    public String getBranchAddress() {
        return branchAddress;
    }

    public void setBranchAddress(String branchAddress) {
        this.branchAddress = branchAddress;
    }

    public Employee getHeadmaster() {
        return headmaster;
    }

    public void setHeadmaster(Employee headmaster) {
        this.headmaster = headmaster;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public List<String> getOtherListData() {
        return otherListData;
    }

    public void setOtherListData(List<String> otherListData) {
        this.otherListData = otherListData;
    }

    @Override
    public String toString() {
        return "\nBranch{" +
                "\n\tid=" + id +
                ", \n\tbranchName='" + branchName + '\'' +
                ", \n\tbranchAddress='" + branchAddress + '\'' +
                ", \n\theadmaster='" + headmaster + '\'' +
                ", \n\temployees=" + employees +
                ", \n\totherListData=" + otherListData +
                "\n}";
    }
}
