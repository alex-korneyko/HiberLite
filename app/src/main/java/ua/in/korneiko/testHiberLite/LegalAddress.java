package ua.in.korneiko.testHiberLite;

import ua.in.korneiko.hiberlite.EntityObject;
import ua.in.korneiko.hiberlite.annotations.*;

@Entity
public class LegalAddress implements EntityObject {

    @Column
    @Id
    @Autoincrement
    private int id;

    @Column
    @SearchKey
    private int postalCode;

    @Column
    @SearchKey
    private String Country;

    @Column
    @SearchKey
    private String city;

    @Column
    @SearchKey
    private String address;

    public LegalAddress() {
    }

    public LegalAddress(int postalCode, String country, String city, String address) {
        this.postalCode = postalCode;
        Country = country;
        this.city = city;
        this.address = address;
    }

    public LegalAddress(int id, int postalCode, String country, String city, String address) {
        this.id = id;
        this.postalCode = postalCode;
        Country = country;
        this.city = city;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "LegalAddress{" +
                "id=" + id +
                ", postalCode=" + postalCode +
                ", Country='" + Country + '\'' +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}

