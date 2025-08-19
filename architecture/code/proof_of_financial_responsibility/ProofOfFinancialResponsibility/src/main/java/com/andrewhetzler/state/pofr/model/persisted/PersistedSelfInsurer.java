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
public class PersistedSelfInsurer {
    private final String amount;
    private final String businessName;
    private final String name;
    private final String title;

    public PersistedSelfInsurer(
            @JsonProperty("amount") String amount,
            @JsonProperty("businessName") String businessName,
            @JsonProperty("name") String name,
            @JsonProperty("title") String title
    ) {
        this.amount = amount;
        this.businessName = businessName;
        this.name = name;
        this.title = title;
    }

    public String getAmount() {
        return amount;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PersistedSelfInsurer that = (PersistedSelfInsurer) o;
        return Objects.equals(
                amount,
                that.amount
        ) && Objects.equals(
                businessName,
                that.businessName
        ) && Objects.equals(
                name,
                that.name
        ) && Objects.equals(
                title,
                that.title
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                amount,
                businessName,
                name,
                title
        );
    }

    @Override
    public String toString() {
        return "PersistedSelfInsurer{" +
                "amount='" + amount + '\'' +
                ", businessName='" + businessName + '\'' +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
