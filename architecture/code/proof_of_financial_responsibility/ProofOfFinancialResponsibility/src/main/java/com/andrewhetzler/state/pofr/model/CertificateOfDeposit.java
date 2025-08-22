package com.andrewhetzler.state.pofr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/19/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class CertificateOfDeposit {
    private final String amount;
    private final String name;

    public CertificateOfDeposit(
            @JsonProperty("amount") String amount,
            @JsonProperty("name") String name
    ) {
        this.amount = amount;
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CertificateOfDeposit that = (CertificateOfDeposit) o;
        return Objects.equals(
                amount,
                that.amount
        ) && Objects.equals(
                name,
                that.name
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                amount,
                name
        );
    }

    @Override
    public String toString() {
        return "CertificateOfDeposit{" +
                "amount='" + amount + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
