package com.andrewhetzler.federal.vehicle_state;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/13/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class Vehicle {
    private final String hash;
    private final String vin;

    public Vehicle(
            @JsonProperty("hash") String hash,
            @JsonProperty("vin") String vin
    ) {
        this.hash = hash;
        this.vin = vin;
    }

    public String getHash() {
        return hash;
    }

    public String getVin() {
        return vin;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(
                hash,
                vehicle.hash
        ) && Objects.equals(
                vin,
                vehicle.vin
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                hash,
                vin
        );
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "hash='" + hash + '\'' +
                ", vin='" + vin + '\'' +
                '}';
    }
}
