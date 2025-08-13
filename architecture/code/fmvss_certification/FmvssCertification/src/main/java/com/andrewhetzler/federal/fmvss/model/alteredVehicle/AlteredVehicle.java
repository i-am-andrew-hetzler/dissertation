package com.andrewhetzler.federal.fmvss.model.alteredVehicle;

import com.andrewhetzler.federal.fmvss.model.GrossAxleWeightRating;
import com.andrewhetzler.federal.fmvss.model.GrossVehicleWeightRating;
import com.andrewhetzler.federal.fmvss.model.sorter.GrossAxleWeightRatingSorter;
import com.andrewhetzler.federal.fmvss.model.sorter.GrossVehicleWeightRatingSorter;
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
    private static final GrossAxleWeightRatingSorter GROSS_AXLE_WEIGHT_RATING_SORTER = new GrossAxleWeightRatingSorter();
    private static final GrossVehicleWeightRatingSorter GROSS_VEHICLE_WEIGHT_RATING_SORTER = new GrossVehicleWeightRatingSorter();

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
        if (grossAxleWeightRatings != null && !grossAxleWeightRatings.isEmpty()) {
            grossAxleWeightRatings.sort(GROSS_AXLE_WEIGHT_RATING_SORTER);
        }

        return grossAxleWeightRatings;
    }

    public List<GrossVehicleWeightRating> getGrossVehicleWeightRatings() {
        if (grossVehicleWeightRatings != null && !grossVehicleWeightRatings.isEmpty()) {
            grossVehicleWeightRatings.sort(GROSS_VEHICLE_WEIGHT_RATING_SORTER);
        }
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
