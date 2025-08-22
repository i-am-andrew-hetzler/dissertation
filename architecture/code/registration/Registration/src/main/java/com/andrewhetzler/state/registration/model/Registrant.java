package com.andrewhetzler.state.registration.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/18/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class Registrant {
    private final List<Address> addresses;
    private final String name;

    public Registrant(
            @JsonProperty("addresses") List<Address> addresses,
            @JsonProperty("name") String name
    ) {
        this.addresses = addresses;
        this.name = name;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Registrant that = (Registrant) o;
        return Objects.equals(
                addresses,
                that.addresses
        ) && Objects.equals(
                name,
                that.name
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                addresses,
                name
        );
    }

    @Override
    public String toString() {
        return "Registrant{" +
                "addresses=" + addresses +
                ", name='" + name + '\'' +
                '}';
    }
}
