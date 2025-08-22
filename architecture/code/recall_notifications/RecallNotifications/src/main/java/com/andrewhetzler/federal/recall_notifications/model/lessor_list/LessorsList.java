package com.andrewhetzler.federal.recall_notifications.model.lessor_list;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.hyperledger.fabric.contract.annotation.DataType;

import java.util.List;
import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/16/25
 **/
@JsonPropertyOrder(alphabetic = true)
@DataType
public class LessorsList {
    private final List<Vehicle> vehicles;
    private final String schemaVersion;

    public LessorsList(
            @JsonProperty("vehicles") List<Vehicle> vehicles,
            @JsonProperty("schemaVersion") String schemaVersion
    ) {
        this.vehicles = vehicles;
        this.schemaVersion = schemaVersion;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LessorsList that = (LessorsList) o;
        return Objects.equals(
                vehicles,
                that.vehicles
        ) && Objects.equals(
                schemaVersion,
                that.schemaVersion
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                vehicles,
                schemaVersion
        );
    }

    @Override
    public String toString() {
        return "LessorsList{" +
                "vehicles=" + vehicles +
                ", schemaVersion='" + schemaVersion + '\'' +
                '}';
    }
}
