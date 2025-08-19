package com.andrewhetzler.state.pofr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/19/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class Insurance {
    private final List<Insured> insured;
    private final Policy policy;

    public Insurance(
            @JsonProperty("insured") List<Insured> insured,
            @JsonProperty("policy") Policy policy
    ) {
        this.insured = insured;
        this.policy = policy;
    }

    public List<Insured> getInsured() {
        return insured;
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
                insured,
                insurance.insured
        ) && Objects.equals(
                policy,
                insurance.policy
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                insured,
                policy
        );
    }

    @Override
    public String toString() {
        return "Insurance{" +
                "insured=" + insured +
                ", policy=" + policy +
                '}';
    }
}
