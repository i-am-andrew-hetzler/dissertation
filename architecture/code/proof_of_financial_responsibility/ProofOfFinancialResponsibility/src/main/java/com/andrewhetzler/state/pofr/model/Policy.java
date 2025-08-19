package com.andrewhetzler.state.pofr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/19/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class Policy {
    private final String effectiveDate;
    private final String expirationDate;
    private final String insurer;
    private final String policyNumber;

    public Policy(
            @JsonProperty("effectiveDate") String effectiveDate,
            @JsonProperty("expirationDate") String expirationDate,
            @JsonProperty("insurer") String insurer,
            @JsonProperty("policyNumber") String policyNumber
    ) {
        this.effectiveDate = effectiveDate;
        this.expirationDate = expirationDate;
        this.insurer = insurer;
        this.policyNumber = policyNumber;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public String getInsurer() {
        return insurer;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Policy policy = (Policy) o;
        return Objects.equals(
                effectiveDate,
                policy.effectiveDate
        ) && Objects.equals(
                expirationDate,
                policy.expirationDate
        ) && Objects.equals(
                insurer,
                policy.insurer
        ) && Objects.equals(
                policyNumber,
                policy.policyNumber
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                effectiveDate,
                expirationDate,
                insurer,
                policyNumber
        );
    }

    @Override
    public String toString() {
        return "Policy{" +
                "effectiveDate='" + effectiveDate + '\'' +
                ", expirationDate='" + expirationDate + '\'' +
                ", insurer='" + insurer + '\'' +
                ", policyNumber='" + policyNumber + '\'' +
                '}';
    }
}
