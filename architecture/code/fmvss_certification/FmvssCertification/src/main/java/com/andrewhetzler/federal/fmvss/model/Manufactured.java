package com.andrewhetzler.federal.fmvss.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/11/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class Manufactured {
    private final String month;
    private final String year;

    public Manufactured(
            @JsonProperty("month") String month,
            @JsonProperty("year") String year
    ) {
        this.month = month;
        this.year = year;
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
        Manufactured that = (Manufactured) o;
        return Objects.equals(
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
                month,
                year
        );
    }

    @Override
    public String toString() {
        return "Manufactured{" +
                "month='" + month + '\'' +
                ", year=" + year +
                '}';
    }
}
