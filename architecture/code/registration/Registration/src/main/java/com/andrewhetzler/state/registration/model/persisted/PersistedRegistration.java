package com.andrewhetzler.state.registration.model.persisted;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Map;
import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/18/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class PersistedRegistration {
    private final String number;
    private final Map<String, String> vehicleDescription;

    public PersistedRegistration(
            @JsonProperty("number") String number,
            @JsonProperty("vehicleDescription") Map<String, String> vehicleDescription
    ) {
        this.number = number;
        this.vehicleDescription = vehicleDescription;
    }

    public String getNumber() {
        return number;
    }

    public Map<String, String> getVehicleDescription() {
        return vehicleDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PersistedRegistration that = (PersistedRegistration) o;
        return Objects.equals(
                number,
                that.number
        ) && Objects.equals(
                vehicleDescription,
                that.vehicleDescription
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                number,
                vehicleDescription
        );
    }

    @Override
    public String toString() {
        return "PersistedRegistration{" +
                "number='" + number + '\'' +
                ", vehicleDescription=" + vehicleDescription +
                '}';
    }
}
