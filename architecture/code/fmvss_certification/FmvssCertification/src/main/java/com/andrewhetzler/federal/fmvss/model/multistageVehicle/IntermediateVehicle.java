package com.andrewhetzler.federal.fmvss.model.multistageVehicle;

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
public class IntermediateVehicle {
    private final List<GrossAxleWeightRating> grossAxleWeightRatings;
    private final List<GrossVehicleWeightRating> grossVehicleWeightRatings;
    private final Manufactured manufactured;
    private final String manufacturerName;
    private final String vehicleIdentificationNumber;

    public IntermediateVehicle(
            @JsonProperty("grossAxleWeightRatings") List<GrossAxleWeightRating> grossAxleWeightRatings,
            @JsonProperty("grossVehicleWeightRatings") List<GrossVehicleWeightRating> grossVehicleWeightRatings,
            @JsonProperty("manufactured") Manufactured manufactured,
            @JsonProperty("manufacturerName") String manufacturerName,
            @JsonProperty("vehicleIdentificationNumber") String vehicleIdentificationNumber
    ) {
        this.grossAxleWeightRatings = grossAxleWeightRatings;
        this.grossVehicleWeightRatings = grossVehicleWeightRatings;
        this.manufactured = manufactured;
        this.manufacturerName = manufacturerName;
        this.vehicleIdentificationNumber = vehicleIdentificationNumber;
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

    public String getVehicleIdentificationNumber() {
        return vehicleIdentificationNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IntermediateVehicle that = (IntermediateVehicle) o;
        return Objects.equals(
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
                vehicleIdentificationNumber,
                that.vehicleIdentificationNumber
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                grossAxleWeightRatings,
                grossVehicleWeightRatings,
                manufactured,
                manufacturerName,
                vehicleIdentificationNumber
        );
    }

    @Override
    public String toString() {
        return "IntermediateVehicle{" +
                "grossAxleWeightRatings=" + grossAxleWeightRatings +
                ", grossVehicleWeightRatings=" + grossVehicleWeightRatings +
                ", manufactured=" + manufactured +
                ", manufacturerName='" + manufacturerName + '\'' +
                ", vehicleIdentificationNumber='" + vehicleIdentificationNumber + '\'' +
                '}';
    }
}
