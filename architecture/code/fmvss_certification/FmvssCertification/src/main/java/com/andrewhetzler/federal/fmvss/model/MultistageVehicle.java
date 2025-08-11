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
public class MultistageVehicle {
    private final FinalVehicle finalVehicle;
    private final IncompleteVehicle incompleteVehicle;
    private final List<IntermediateVehicle> intermediateVehicles;

    public MultistageVehicle(
            @JsonProperty("finalVehicle") FinalVehicle finalVehicle,
            @JsonProperty("incompleteVehicle") IncompleteVehicle incompleteVehicle,
            @JsonProperty("intermediateVehicles") List<IntermediateVehicle> intermediateVehicles
    ) {
        this.finalVehicle = finalVehicle;
        this.incompleteVehicle = incompleteVehicle;
        this.intermediateVehicles = intermediateVehicles;
    }

    public FinalVehicle getFinalVehicle() {
        return finalVehicle;
    }

    public IncompleteVehicle getIncompleteVehicle() {
        return incompleteVehicle;
    }

    public List<IntermediateVehicle> getIntermediateVehicles() {
        return intermediateVehicles;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MultistageVehicle that = (MultistageVehicle) o;
        return Objects.equals(
                finalVehicle,
                that.finalVehicle
        ) && Objects.equals(
                incompleteVehicle,
                that.incompleteVehicle
        ) && Objects.equals(
                intermediateVehicles,
                that.intermediateVehicles
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                finalVehicle,
                incompleteVehicle,
                intermediateVehicles
        );
    }

    @Override
    public String toString() {
        return "MultistageVehicle{" +
                "finalVehicle=" + finalVehicle +
                ", incompleteVehicle=" + incompleteVehicle +
                ", intermediateVehicles=" + intermediateVehicles +
                '}';
    }
}
