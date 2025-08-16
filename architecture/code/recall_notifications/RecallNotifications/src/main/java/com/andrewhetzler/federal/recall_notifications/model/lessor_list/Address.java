package com.andrewhetzler.federal.recall_notifications.model.lessor_list;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/16/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class Address {
    private final String street1;
    private final String street2;
    private final String city;
    private final String state;
    private final String zipCode;

    public Address(
            @JsonProperty("street1") String street1,
            @JsonProperty("street2") String street2,
            @JsonProperty("city") String city,
            @JsonProperty("state") String state,
            @JsonProperty("zipCode") String zipCode
    ) {
        this.street1 = street1;
        this.street2 = street2;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    public String getStreet1() {
        return street1;
    }

    public String getStreet2() {
        return street2;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Address address = (Address) o;
        return Objects.equals(
                street1,
                address.street1
        ) && Objects.equals(
                street2,
                address.street2
        ) && Objects.equals(
                city,
                address.city
        ) && Objects.equals(
                state,
                address.state
        ) && Objects.equals(
                zipCode,
                address.zipCode
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                street1,
                street2,
                city,
                state,
                zipCode
        );
    }

    @Override
    public String toString() {
        return "Address{" +
                "street1='" + street1 + '\'' +
                ", street2='" + street2 + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                '}';
    }
}
