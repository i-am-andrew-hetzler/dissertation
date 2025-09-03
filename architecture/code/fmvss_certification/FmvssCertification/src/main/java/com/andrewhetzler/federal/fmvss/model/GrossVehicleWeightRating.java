    package com.andrewhetzler.federal.fmvss.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/11/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class GrossVehicleWeightRating {
    private final String order;
    private final String value;

    public GrossVehicleWeightRating(
            @JsonProperty("order") String order,
            @JsonProperty("value") String value
    ) {
        this.order = order;
        this.value = value;
    }

    public String getOrder() {
        return order;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GrossVehicleWeightRating that = (GrossVehicleWeightRating) o;
        return Objects.equals(
                order,
                that.order
        ) && Objects.equals(
                value,
                that.value
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                order,
                value
        );
    }

    @Override
    public String toString() {
        return "GrossVehicleWeightRating{" +
                "order=" + order +
                ", value='" + value + '\'' +
                '}';
    }
}
