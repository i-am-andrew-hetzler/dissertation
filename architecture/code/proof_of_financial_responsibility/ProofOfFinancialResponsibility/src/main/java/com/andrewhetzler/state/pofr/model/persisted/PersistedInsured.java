package com.andrewhetzler.state.pofr.model.persisted;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/19/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class PersistedInsured {
    private final String name;

    public PersistedInsured(@JsonProperty("name") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                name
        );
    }

    @Override
    public String toString() {
        return "PersistedInsured{" +
                "name='" + name + '\'' +
                '}';
    }
}
