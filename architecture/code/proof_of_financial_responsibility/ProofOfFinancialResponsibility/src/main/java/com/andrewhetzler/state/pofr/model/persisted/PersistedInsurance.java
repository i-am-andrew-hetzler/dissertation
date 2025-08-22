package com.andrewhetzler.state.pofr.model.persisted;

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
public class PersistedInsurance {
    private final Map<String, String> descriptionOfVehicle;
    private final List<PersistedInsured> insured;
    private final PersistedPolicy policy;

    public PersistedInsurance(
            @JsonProperty("descriptionOfVehicle") Map<String, String> descriptionOfVehicle,
            @JsonProperty("insured") List<PersistedInsured> insured,
            @JsonProperty("policy") PersistedPolicy policy
    ) {
        this.descriptionOfVehicle = descriptionOfVehicle;
        this.insured = insured;
        this.policy = policy;
    }

    public Map<String, String> getDescriptionOfVehicle() {
        return descriptionOfVehicle;
    }

    public List<PersistedInsured> getInsured() {
        return insured;
    }

    public PersistedPolicy getPolicy() {
        return policy;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PersistedInsurance that = (PersistedInsurance) o;
        return Objects.equals(
                descriptionOfVehicle,
                that.descriptionOfVehicle
        ) && Objects.equals(
                insured,
                that.insured
        ) && Objects.equals(
                policy,
                that.policy
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                descriptionOfVehicle,
                insured,
                policy
        );
    }

    @Override
    public String toString() {
        return "PersistedInsurance{" +
                "descriptionOfVehicle=" + descriptionOfVehicle +
                ", insured=" + insured +
                ", policy=" + policy +
                '}';
    }
}
