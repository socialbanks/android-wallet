package com.bitcoin.socialbanks.Model;

public class Address {

    private String street;
    private String city;
    private String state;
    private String country;

    public Address(String street, String city, String state, String country){
        this.street = street;
        this.city = city;
        this.state = state;
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getStreet() {
        return street;
    }
}
