package com.andrewhetzler.federal.fmvss.model.replicaVehicle;

import com.andrewhetzler.federal.fmvss.model.GrossAxleWeightRating;
import com.andrewhetzler.federal.fmvss.model.GrossVehicleWeightRating;
import com.andrewhetzler.federal.fmvss.model.Manufactured;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/11/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class ReplicaVehicle {
    private final String exemptionStatement;
    private final List<GrossAxleWeightRating> grossAxleWeightRatings;
    private final List<GrossVehicleWeightRating> grossVehicleWeightRatings;
    private final Manufactured manufactured;
    private final String manufacturerName;
    private final String replicaStatement;
    private final String vehicleIdentificationNumber;

    public ReplicaVehicle(
            @JsonProperty("exemptionStatement") String exemptionStatement,
            @JsonProperty("grossAxleWeightRatings") List<GrossAxleWeightRating> grossAxleWeightRatings,
            @JsonProperty("grossVehicleWeightRatings") List<GrossVehicleWeightRating> grossVehicleWeightRatings,
            @JsonProperty("manufactured") Manufactured manufactured,
            @JsonProperty("manufacturerName") String manufacturerName,
            @JsonProperty("replicaStatement") String replicaStatement,
            @JsonProperty("vehicleIdentificationNumber") String vehicleIdentificationNumber
    ) {
        this.exemptionStatement = exemptionStatement;
        this.grossAxleWeightRatings = grossAxleWeightRatings;
        this.grossVehicleWeightRatings = grossVehicleWeightRatings;
        this.manufactured = manufactured;
        this.manufacturerName = manufacturerName;
        this.replicaStatement = replicaStatement;
        this.vehicleIdentificationNumber = vehicleIdentificationNumber;
    }

    public String getExemptionStatement() {
        return exemptionStatement;
    }

    public List<GrossAxleWeightRating> getGrossAxleWeightRatings() {
        return grossAxleWeightRatings;
    }

    public List<GrossVehicleWeightRating> getGrossVehicleWeightRatings() {
        return grossVehicleWeightRatings;
    }

    public Manufactured getManufactured() {
        return manufactured;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public String getReplicaStatement() {
        return replicaStatement;
    }

    public String getVehicleIdentificationNumber() {
        return vehicleIdentificationNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReplicaVehicle that = (ReplicaVehicle) o;
        return Objects.equals(
                exemptionStatement,
                that.exemptionStatement
        ) && Objects.equals(
                grossAxleWeightRatings,
                that.grossAxleWeightRatings
        ) && Objects.equals(
                grossVehicleWeightRatings,
                that.grossVehicleWeightRatings
        ) && Objects.equals(
                manufactured,
                that.manufactured
        ) && Objects.equals(
                manufacturerName,
                that.manufacturerName
        ) && Objects.equals(
                replicaStatement,
                that.replicaStatement
        ) && Objects.equals(
                vehicleIdentificationNumber,
                that.vehicleIdentificationNumber
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                exemptionStatement,
                grossAxleWeightRatings,
                grossVehicleWeightRatings,
                manufactured,
                manufacturerName,
                replicaStatement,
                vehicleIdentificationNumber
        );
    }

    @Override
    public String toString() {
        return "ReplicaVehicle{" +
                "exemptionStatement='" + exemptionStatement + '\'' +
                ", grossAxleWeightRatings=" + grossAxleWeightRatings +
                ", grossVehicleWeightRatings=" + grossVehicleWeightRatings +
                ", manufactured=" + manufactured +
                ", manufacturerName='" + manufacturerName + '\'' +
                ", replicaStatement='" + replicaStatement + '\'' +
                ", vehicleIdentificationNumber='" + vehicleIdentificationNumber + '\'' +
                '}';
    }
}
