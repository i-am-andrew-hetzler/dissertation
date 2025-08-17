package com.andrewhetzler.state.licensing.model;

import com.andrewhetzler.state.licensing.Birthdate;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/17/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class Licensee {
    private final List<Address> addresses;
    private final Birthdate birthdate;
    private final Map<String, String> description;
    private final String isVeteran;
    private final String name;
    private final String photograph;
    private final String signature;

    public Licensee(
            @JsonProperty("addresses") List<Address> addresses,
            @JsonProperty("birthdate") Birthdate birthdate,
            @JsonProperty("description") Map<String, String> description,
            @JsonProperty("isVeteran") String isVeteran,
            @JsonProperty("name") String name,
            @JsonProperty("photograph") String photograph,
            @JsonProperty("signature") String signature
    ) {
        this.addresses = addresses != null ? addresses : new ArrayList<>();
        this.birthdate = birthdate;
        this.description = description;
        this.isVeteran = isVeteran;
        this.name = name;
        this.photograph = photograph;
        this.signature = signature;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public Birthdate getBirthdate() {
        return birthdate;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public String getIsVeteran() {
        return isVeteran;
    }

    public String getName() {
        return name;
    }

    public String getPhotograph() {
        return photograph;
    }

    public String getSignature() {
        return signature;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Licensee licensee = (Licensee) o;
        return Objects.equals(
                addresses,
                licensee.addresses
        ) && Objects.equals(
                birthdate,
                licensee.birthdate
        ) && Objects.equals(
                description,
                licensee.description
        ) && Objects.equals(
                isVeteran,
                licensee.isVeteran
        ) && Objects.equals(
                name,
                licensee.name
        ) && Objects.equals(
                photograph,
                licensee.photograph
        ) && Objects.equals(
                signature,
                licensee.signature
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                addresses,
                birthdate,
                description,
                isVeteran,
                name,
                photograph,
                signature
        );
    }

    @Override
    public String toString() {
        return "Licensee{" +
                "addresses=" + addresses +
                ", birthdate=" + birthdate +
                ", description=" + description +
                ", isVeteran='" + isVeteran + '\'' +
                ", name='" + name + '\'' +
                ", photograph='" + photograph + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}
