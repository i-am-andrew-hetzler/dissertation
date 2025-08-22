package com.andrewhetzler.state.licensing.model.persisted;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/17/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class PersistedLicensee {
    private final List<PersistedAddress> addresses;
    private final PersistedBirthdate birthdate;
    private final Map<String, String> description;
    @JsonProperty("isVeteran")
    private final String isVeteran;
    private final String name;
    private final String photograph;
    private final String signature;
    private final String uniqueId;

    public PersistedLicensee(
            @JsonProperty("addresses") List<PersistedAddress> addresses,
            @JsonProperty("birthdate") PersistedBirthdate birthdate,
            @JsonProperty("description") Map<String, String> description,
            @JsonProperty("isVeteran") String isVeteran,
            @JsonProperty("name") String name,
            @JsonProperty("photograph") String photograph,
            @JsonProperty("signature") String signature,
            @JsonProperty("uniqueId") String uniqueId
    ) {
        this.addresses = addresses;
        this.birthdate = birthdate;
        this.description = description;
        this.isVeteran = isVeteran;
        this.name = name;
        this.photograph = photograph;
        this.signature = signature;
        this.uniqueId = uniqueId;
    }

    public List<PersistedAddress> getAddresses() {
        return addresses;
    }

    public PersistedBirthdate getBirthdate() {
        return birthdate;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public String isVeteran() {
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

    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PersistedLicensee that = (PersistedLicensee) o;
        return Objects.equals(
                addresses,
                that.addresses
        ) && Objects.equals(
                birthdate,
                that.birthdate
        ) && Objects.equals(
                description,
                that.description
        ) && Objects.equals(
                isVeteran,
                that.isVeteran
        ) && Objects.equals(
                name,
                that.name
        ) && Objects.equals(
                photograph,
                that.photograph
        ) && Objects.equals(
                signature,
                that.signature
        ) && Objects.equals(
                uniqueId,
                that.uniqueId
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
                signature,
                uniqueId
        );
    }

    @Override
    public String toString() {
        return "PersistedLicensee{" +
                "addresses=" + addresses +
                ", birthdate=" + birthdate +
                ", description=" + description +
                ", isVeteran='" + isVeteran + '\'' +
                ", name='" + name + '\'' +
                ", photograph='" + photograph + '\'' +
                ", signature='" + signature + '\'' +
                ", uniqueId='" + uniqueId + '\'' +
                '}';
    }
}
