package com.andrewhetzler.federal.vehicle_state;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.hyperledger.fabric.contract.annotation.DataType;

import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/13/25
 **/

@JsonPropertyOrder(alphabetic = true)
@DataType
public class VehicleState {
    private final String schemaVersion;
    private final Vehicle vehicle;

    public VehicleState(
            @JsonProperty("schemaVersion") String schemaVersion,
            @JsonProperty("vehicle") Vehicle vehicle
    ) {
        this.schemaVersion = schemaVersion;
        this.vehicle = vehicle;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    @JsonIgnore
    public String getVehicleHash() {
        return vehicle.getHash();
    }

    @JsonIgnore
    public String getVehicleIdentificationNumber() {
        return vehicle.getVin();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VehicleState that = (VehicleState) o;
        return Objects.equals(
                schemaVersion,
                that.schemaVersion
        ) && Objects.equals(
                vehicle,
                that.vehicle
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                schemaVersion,
                vehicle
        );
    }

    @Override
    public String toString() {
        return "VehicleState{" +
                "schemaVersion='" + schemaVersion + '\'' +
                ", vehicle=" + vehicle +
                '}';
    }
}
