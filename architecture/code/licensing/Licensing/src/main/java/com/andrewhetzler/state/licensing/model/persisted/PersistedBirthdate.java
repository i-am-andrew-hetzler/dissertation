package com.andrewhetzler.state.licensing.model.persisted;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/17/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class PersistedBirthdate {
    private final String day;
    private final String month;
    private final String year;

    public PersistedBirthdate(
            @JsonProperty("day") String day,
            @JsonProperty("month") String month,
            @JsonProperty("year") String year
    ) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public String getDay() {
        return day;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PersistedBirthdate that = (PersistedBirthdate) o;
        return Objects.equals(
                day,
                that.day
        ) && Objects.equals(
                month,
                that.month
        ) && Objects.equals(
                year,
                that.year
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                day,
                month,
                year
        );
    }

    @Override
    public String toString() {
        return "PersistedBirthdate{" +
                "day='" + day + '\'' +
                ", month='" + month + '\'' +
                ", year='" + year + '\'' +
                '}';
    }
}
