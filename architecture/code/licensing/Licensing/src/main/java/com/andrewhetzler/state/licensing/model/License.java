package com.andrewhetzler.state.licensing.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/17/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class License {
    private final List<String> classes;
    private final String number;

    public License(
            @JsonProperty("classes") List<String> classes,
            @JsonProperty("number") String number
    ) {
        this.classes = classes;
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
        License license = (License) o;
        return Objects.equals(
                classes,
                license.classes
        ) && Objects.equals(
                number,
                license.number
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
        return "License{" +
                "classes=" + classes +
                ", number='" + number + '\'' +
                '}';
    }
}
