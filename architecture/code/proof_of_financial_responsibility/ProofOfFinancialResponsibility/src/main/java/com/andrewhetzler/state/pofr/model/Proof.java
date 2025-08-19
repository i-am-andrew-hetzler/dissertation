package com.andrewhetzler.state.pofr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.hyperledger.fabric.contract.annotation.DataType;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/19/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class Proof {
    private final List<CertificateOfDeposit> certificateOfDeposits;
    private final Insurance insurance;
    private final String schemaVersion;
    private final SelfInsurer selfInsurer;

    public Proof(
            @JsonProperty("certificateOfDeposits") List<CertificateOfDeposit> certificateOfDeposits,
            @JsonProperty("insurance") Insurance insurance,
            @JsonProperty("schemaVersion") String schemaVersion,
            @JsonProperty("selfInsurer")  SelfInsurer selfInsurer
    ) {
        this.certificateOfDeposits = certificateOfDeposits;
        this.insurance = insurance;
        this.schemaVersion = schemaVersion;
        this.selfInsurer = selfInsurer;
    }

    public List<CertificateOfDeposit> getCertificateOfDeposits() {
        return certificateOfDeposits;
    }

    public Insurance getInsurance() {
        return insurance;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public SelfInsurer getSelfInsurer() {
        return selfInsurer;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Proof proof = (Proof) o;
        return Objects.equals(
                certificateOfDeposits,
                proof.certificateOfDeposits
        ) && Objects.equals(
                insurance,
                proof.insurance
        ) && Objects.equals(
                schemaVersion,
                proof.schemaVersion
        ) && Objects.equals(
                selfInsurer,
                proof.selfInsurer
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
        return "Proof{" +
                "certificateOfDeposits=" + certificateOfDeposits +
                ", insurance=" + insurance +
                ", schemaVersion='" + schemaVersion + '\'' +
                ", selfInsurer=" + selfInsurer +
                '}';
    }
}
