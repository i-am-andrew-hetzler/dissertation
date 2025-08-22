package com.andrewhetzler.state.registration.model.persisted;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/18/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class PersistedRegistrant {
    private final List<PersistedAddress> addresses;
    private final String name;
    private final String uniqueId;

    public PersistedRegistrant(
            @JsonProperty("addresses") List<PersistedAddress> addresses,
            @JsonProperty("name") String name,
            @JsonProperty("uniqueId") String uniqueId
    ) {
        this.addresses = addresses;
        this.name = name;
        this.uniqueId = uniqueId;
    }

    public List<PersistedAddress> getAddresses() {
        return addresses;
    }

    public String getName() {
        return name;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PersistedRegistrant that = (PersistedRegistrant) o;
        return Objects.equals(
                addresses,
                that.addresses
        ) && Objects.equals(
                name,
                that.name
        ) && Objects.equals(
                uniqueId,
                that.uniqueId
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                addresses,
                name,
                uniqueId
        );
    }

    @Override
    public String toString() {
        return "PersistedRegistrant{" +
                "addresses=" + addresses +
                ", name='" + name + '\'' +
                ", uniqueId='" + uniqueId + '\'' +
                '}';
    }
}
