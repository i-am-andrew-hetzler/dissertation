package com.andrewhetzler.state.licensing.model.persisted;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/17/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class PersistedAddress {
    private final String street1;
    private final String street2;
    private final String city;
    private final String state;
    private final String zipCode;

    public PersistedAddress(
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
        PersistedAddress that = (PersistedAddress) o;
        return Objects.equals(
                street1,
                that.street1
        ) && Objects.equals(
                street2,
                that.street2
        ) && Objects.equals(
                city,
                that.city
        ) && Objects.equals(
                state,
                that.state
        ) && Objects.equals(
                zipCode,
                that.zipCode
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
        return "PersistedAddress{" +
                "street1='" + street1 + '\'' +
                ", street2='" + street2 + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                '}';
    }
}
