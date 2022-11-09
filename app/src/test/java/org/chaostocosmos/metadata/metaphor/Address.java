package org.chaostocosmos.metadata.metaphor;

public class Address {

    String country;

    String city;

    int zipCode;

    public Address() {
        this.country = "Korea";
        this.city = "Seoul";
        this.zipCode = 8282;
    }
    

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getZipCode() {
        return this.zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

}
