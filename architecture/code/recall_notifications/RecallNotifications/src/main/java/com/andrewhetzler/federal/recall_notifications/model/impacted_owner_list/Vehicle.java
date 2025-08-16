package com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/15/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class Vehicle {
    private final String identificationNumber;
    private final Owner owner;
    private final Recall recall;

    public Vehicle(
            @JsonProperty("identificationNumber") String identificationNumber,
            @JsonProperty("owner") Owner owner,
            @JsonProperty("recall") Recall recall
    ) {
        this.identificationNumber = identificationNumber;
        this.owner = owner;
        this.recall = recall;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public Owner getOwner() {
        return owner;
    }

    public Recall getRecall() {
        return recall;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(
                identificationNumber,
                vehicle.identificationNumber
        ) && Objects.equals(
                owner,
                vehicle.owner
        ) && Objects.equals(
                recall,
                vehicle.recall
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                identificationNumber,
                owner,
                recall
        );
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "identificationNumber='" + identificationNumber + '\'' +
                ", owner=" + owner +
                ", recall=" + recall +
                '}';
    }
}
