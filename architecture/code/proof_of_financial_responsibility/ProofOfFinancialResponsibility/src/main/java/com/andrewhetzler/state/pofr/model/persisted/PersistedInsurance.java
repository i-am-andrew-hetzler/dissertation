package com.andrewhetzler.state.pofr.model.persisted;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.hyperledger.fabric.contract.annotation.DataType;

import java.util.List;
import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/19/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class PersistedInsurance {
    private final List<PersistedInsured> insured;
    private final PersistedPolicy policy;

    public PersistedInsurance(
            @JsonProperty("insured") List<PersistedInsured> insured,
            @JsonProperty("policy") PersistedPolicy policy
    ) {
        this.insured = insured;
        this.policy = policy;
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
                insured,
                policy
        );
    }

    @Override
    public String toString() {
        return "PersistedInsurance{" +
                "insured=" + insured +
                ", policy=" + policy +
                '}';
    }
}
