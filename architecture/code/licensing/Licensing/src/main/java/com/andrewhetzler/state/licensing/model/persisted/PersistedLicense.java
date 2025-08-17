package com.andrewhetzler.state.licensing.model.persisted;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/17/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class PersistedLicense {
    private final List<String> classes;
    private final String number;

    public PersistedLicense(
            @JsonProperty("classes") List<String> classes,
            @JsonProperty("number") String number
    ) {
        this.classes = classes != null ? classes : new ArrayList<>();
        this.number = number;
    }

    public List<String> getClasses() {
        return classes;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PersistedLicense that = (PersistedLicense) o;
        return Objects.equals(
                classes,
                that.classes
        ) && Objects.equals(
                number,
                that.number
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                classes,
                number
        );
    }

    @Override
    public String toString() {
        return "PersistedLicense{" +
                "classes=" + classes +
                ", number='" + number + '\'' +
                '}';
    }
}
