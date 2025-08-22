package com.andrewhetzler.federal.recall_notifications.model.public_recall;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/14/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class Vehicle {
    private final String identificationNumber;
    private final String make;
    private final String model;

    public Vehicle(
            @JsonProperty("identificationNumber") String identificationNumber,
            @JsonProperty("make") String make,
            @JsonProperty("model") String model
    ) {
        this.identificationNumber = identificationNumber;
        this.make = make;
        this.model = model;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
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
                make,
                vehicle.make
        ) && Objects.equals(
                model,
                vehicle.model
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                identificationNumber,
                make,
                model
        );
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "identificationNumber='" + identificationNumber + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                '}';
    }
}
