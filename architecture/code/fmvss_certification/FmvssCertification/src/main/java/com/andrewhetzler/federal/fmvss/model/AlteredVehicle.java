package com.andrewhetzler.federal.fmvss.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/11/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class AlteredVehicle {
    private final String conformityStatement;
    private final List<GrossAxleWeightRating> grossAxleWeightRatings;
    private final List<GrossVehicleWeightRating> grossVehicleWeightRatings;
    private final String type;

    public AlteredVehicle(
            @JsonProperty("conformityStatement") String conformityStatement,
            @JsonProperty("grossAxleWeightRatings") List<GrossAxleWeightRating> grossAxleWeightRatings,
            @JsonProperty("grossVehicleWeightRatings") List<GrossVehicleWeightRating> grossVehicleWeightRatings,
            @JsonProperty("type") String type
    ) {
        this.conformityStatement = conformityStatement;
        this.grossAxleWeightRatings = grossAxleWeightRatings;
        this.grossVehicleWeightRatings = grossVehicleWeightRatings;
        this.type = type;
    }

    public String getConformityStatement() {
        return conformityStatement;
    }

    public List<GrossAxleWeightRating> getGrossAxleWeightRatings() {
        return grossAxleWeightRatings;
    }

    public List<GrossVehicleWeightRating> getGrossVehicleWeightRatings() {
        return grossVehicleWeightRatings;
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AlteredVehicle that = (AlteredVehicle) o;
        return Objects.equals(
                conformityStatement,
                that.conformityStatement
        ) && Objects.equals(
                grossAxleWeightRatings,
                that.grossAxleWeightRatings
        ) && Objects.equals(
                grossVehicleWeightRatings,
                that.grossVehicleWeightRatings
        ) && Objects.equals(
                type,
                that.type
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                conformityStatement,
                grossAxleWeightRatings,
                grossVehicleWeightRatings,
                type
        );
    }

    @Override
    public String toString() {
        return "AlteredVehicle{" +
                "conformityStatement='" + conformityStatement + '\'' +
                ", grossAxleWeightRatings=" + grossAxleWeightRatings +
                ", grossVehicleWeightRatings=" + grossVehicleWeightRatings +
                ", type='" + type + '\'' +
                '}';
    }
}
