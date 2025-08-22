package com.andrewhetzler.federal.fmvss.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/11/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class IntermediateAxleWeightRating {
    private final String order;
    private final String weight;

    public IntermediateAxleWeightRating(
            @JsonProperty("order") String order,
            @JsonProperty("weight") String weight
    ) {
        this.order = order;
        this.weight = weight;
    }

    public String getOrder() {
        return order;
    }

    public String getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IntermediateAxleWeightRating that = (IntermediateAxleWeightRating) o;
        return Objects.equals(
                order,
                that.order
        ) && Objects.equals(
                weight,
                that.weight
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                order,
                weight
        );
    }

    @Override
    public String toString() {
        return "IntermediateAxleWeightRating{" +
                "order=" + order +
                ", weight='" + weight + '\'' +
                '}';
    }
}
