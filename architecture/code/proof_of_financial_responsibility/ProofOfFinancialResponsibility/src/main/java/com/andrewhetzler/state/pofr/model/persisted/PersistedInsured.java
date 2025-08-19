package com.andrewhetzler.state.pofr.model.persisted;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/19/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class PersistedInsured {
    private final String name;
    private final List<Map<String, String>> descriptionOfVehicles;

    public PersistedInsured(
            @JsonProperty("name") String name,
            @JsonProperty("descriptionOfVehicles") List<Map<String, String>> descriptionOfVehicles
    ) {
        this.name = name;
        this.descriptionOfVehicles = descriptionOfVehicles;
    }

    public String getName() {
        return name;
    }

    public List<Map<String, String>> getDescriptionOfVehicles() {
        return descriptionOfVehicles;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PersistedInsured that = (PersistedInsured) o;
        return Objects.equals(
                name,
                that.name
        ) && Objects.equals(
                descriptionOfVehicles,
                that.descriptionOfVehicles
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                name,
                descriptionOfVehicles
        );
    }

    @Override
    public String toString() {
        return "PersistedInsured{" +
                "name='" + name + '\'' +
                ", descriptionOfVehicles=" + descriptionOfVehicles +
                '}';
    }
}
