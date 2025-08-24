package com.andrewhetzler.state.pofr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/19/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class Insurance {
    private final Map<String, String> descriptionOfVehicle;
    private final List<Insured> insured;
    private final Map<String, String> other;
    private final Policy policy;

    public Insurance(
            @JsonProperty("descriptionOfVehicles") Map<String, String> descriptionOfVehicle,
            @JsonProperty("insured") List<Insured> insured,
            @JsonProperty("other") Map<String, String> other,
            @JsonProperty("policy") Policy policy
    ) {
        this.descriptionOfVehicle = descriptionOfVehicle;
        this.insured = insured;
        this.other = other;
        this.policy = policy;
    }

    public Map<String, String> getDescriptionOfVehicle() {
        return descriptionOfVehicle;
    }

    public List<Insured> getInsured() {
        return insured;
    }

    public Map<String, String> getOther() {
        return other;
    }

    public Policy getPolicy() {
        return policy;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Insurance insurance = (Insurance) o;
        return Objects.equals(
                descriptionOfVehicle,
                insurance.descriptionOfVehicle
        ) && Objects.equals(
                insured,
                insurance.insured
        ) && Objects.equals(
                other,
                insurance.other
        ) && Objects.equals(
                policy,
                insurance.policy
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                descriptionOfVehicle,
                insured,
                other,
                policy
        );
    }

    @Override
    public String toString() {
        return "Insurance{" +
                "descriptionOfVehicle=" + descriptionOfVehicle +
                ", insured=" + insured +
                ", other=" + other +
                ", policy=" + policy +
                '}';
    }
}
