package com.andrewhetzler.state.pofr.model.persisted;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/19/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class PersistedPolicy {
    private final String effectiveDate;
    private final String expirationDate;
    private final String insurer;
    private final String policyNumber;

    public PersistedPolicy(
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
        PersistedPolicy that = (PersistedPolicy) o;
        return Objects.equals(
                effectiveDate,
                that.effectiveDate
        ) && Objects.equals(
                expirationDate,
                that.expirationDate
        ) && Objects.equals(
                insurer,
                that.insurer
        ) && Objects.equals(
                policyNumber,
                that.policyNumber
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
        return "PersistedPolicy{" +
                "effectiveDate='" + effectiveDate + '\'' +
                ", expirationDate='" + expirationDate + '\'' +
                ", insurer='" + insurer + '\'' +
                ", policyNumber='" + policyNumber + '\'' +
                '}';
    }
}
