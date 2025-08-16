package com.andrewhetzler.federal.recall_notifications.model.lessor_list;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/16/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class Vehicle {
    private final String identificationNumber;
    private final Lessee lessee;
    private final String make;
    private final String model;
    private final Recall recall;;
    private final String year;

    public Vehicle(
            @JsonProperty("identificationNumber") String identificationNumber,
            @JsonProperty("lessee") Lessee lessee,
            @JsonProperty("make") String make,
            @JsonProperty("model") String model,
            @JsonProperty("recall") Recall recall,
            @JsonProperty("year") String year
    ) {
        this.identificationNumber = identificationNumber;
        this.lessee = lessee;
        this.make = make;
        this.model = model;
        this.recall = recall;
        this.year = year;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public Lessee getLessee() {
        return lessee;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public Recall getRecall() {
        return recall;
    }

    public String getYear() {
        return year;
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
                lessee,
                vehicle.lessee
        ) && Objects.equals(
                make,
                vehicle.make
        ) && Objects.equals(
                model,
                vehicle.model
        ) && Objects.equals(
                recall,
                vehicle.recall
        ) && Objects.equals(
                year,
                vehicle.year
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                identificationNumber,
                lessee,
                make,
                model,
                recall,
                year
        );
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "identificationNumber='" + identificationNumber + '\'' +
                ", lessee=" + lessee +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", recall=" + recall +
                ", year='" + year + '\'' +
                '}';
    }
}
