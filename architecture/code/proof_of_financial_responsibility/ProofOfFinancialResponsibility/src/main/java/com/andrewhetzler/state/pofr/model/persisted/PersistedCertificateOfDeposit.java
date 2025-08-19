package com.andrewhetzler.state.pofr.model.persisted;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.hyperledger.fabric.contract.annotation.DataType;

import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/19/25
 **/
@JsonPropertyOrder(alphabetic = true)
@DataType
public class PersistedCertificateOfDeposit {
    private final String amount;
    private final String name;
    private final String uniqueId;

    public PersistedCertificateOfDeposit(
            @JsonProperty("amount") String amount,
            @JsonProperty("name") String name,
            @JsonProperty("uniqueId") String uniqueId
    ) {
        this.amount = amount;
        this.name = name;
        this.uniqueId = uniqueId;
    }

    public String getAmount() {
        return amount;
    }

    public String getName() {
        return name;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PersistedCertificateOfDeposit that = (PersistedCertificateOfDeposit) o;
        return Objects.equals(
                amount,
                that.amount
        ) && Objects.equals(
                name,
                that.name
        ) && Objects.equals(
                uniqueId,
                that.uniqueId
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                amount,
                name,
                uniqueId
        );
    }

    @Override
    public String toString() {
        return "PersistedCertificateOfDeposit{" +
                "amount='" + amount + '\'' +
                ", name='" + name + '\'' +
                ", uniqueId='" + uniqueId + '\'' +
                '}';
    }
}
