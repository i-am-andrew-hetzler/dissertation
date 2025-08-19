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
@DataType
public class PersistedProof {
    private final List<PersistedCertificateOfDeposit> certificateOfDeposits;
    private final PersistedInsurance insurance;
    private final String schemaVersion;
    private final PersistedSelfInsurer selfInsurer;

    public PersistedProof(
            @JsonProperty("certificateOfDeposits") List<PersistedCertificateOfDeposit> certificateOfDeposits,
            @JsonProperty("insurance") PersistedInsurance insurance,
            @JsonProperty("schemaVersion") String schemaVersion,
            @JsonProperty("selfInsurer") PersistedSelfInsurer selfInsurer
    ) {
        this.certificateOfDeposits = certificateOfDeposits;
        this.insurance = insurance;
        this.schemaVersion = schemaVersion;
        this.selfInsurer = selfInsurer;
    }

    public List<PersistedCertificateOfDeposit> getCertificateOfDeposits() {
        return certificateOfDeposits;
    }

    public PersistedInsurance getInsurance() {
        return insurance;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public PersistedSelfInsurer getSelfInsurer() {
        return selfInsurer;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PersistedProof that = (PersistedProof) o;
        return Objects.equals(
                certificateOfDeposits,
                that.certificateOfDeposits
        ) && Objects.equals(
                insurance,
                that.insurance
        ) && Objects.equals(
                schemaVersion,
                that.schemaVersion
        ) && Objects.equals(
                selfInsurer,
                that.selfInsurer
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                certificateOfDeposits,
                insurance,
                schemaVersion,
                selfInsurer
        );
    }

    @Override
    public String toString() {
        return "PersistedProof{" +
                "certificateOfDeposits=" + certificateOfDeposits +
                ", insurance=" + insurance +
                ", schemaVersion='" + schemaVersion + '\'' +
                ", selfInsurer=" + selfInsurer +
                '}';
    }
}
