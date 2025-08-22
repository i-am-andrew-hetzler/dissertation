package com.andrewhetzler.federal.recall_notifications.model.lessor_list;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/16/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class Lessee {
    private final Address address;
    private final String name;

    public Lessee(
            @JsonProperty("address") Address address,
            @JsonProperty("name") String name
    ) {
        this.address = address;
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Lessee lessee = (Lessee) o;
        return Objects.equals(
                address,
                lessee.address
        ) && Objects.equals(
                name,
                lessee.name
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                address,
                name
        );
    }

    @Override
    public String toString() {
        return "Lessee{" +
                "address=" + address +
                ", name='" + name + '\'' +
                '}';
    }
}
