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
    private final int year;

    public Manufactured(
            @JsonProperty("month") String month,
            @JsonProperty("year") int year
    ) {
        this.month = month;
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Manufactured that = (Manufactured) o;
        return year == that.year && Objects.equals(
                month,
                that.month
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
