package com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/15/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class Owner {
    private final Address address;
    private final String name;

    public Owner(
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
        Owner owner = (Owner) o;
        return Objects.equals(
                address,
                owner.address
        ) && Objects.equals(
                name,
                owner.name
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
        return "Owner{" +
                "address=" + address +
                ", name='" + name + '\'' +
                '}';
    }
}
