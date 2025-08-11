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
public class GrossAxleWeightRating {
    private final String front;
    private final List<IntermediateAxleWeightRating> intermediate;
    private final int order;
    private final String rear;

    public GrossAxleWeightRating(
            @JsonProperty("front") String front,
            @JsonProperty("intermediate") List<IntermediateAxleWeightRating> intermediate,
            @JsonProperty("order") int order,
            @JsonProperty("rear") String rear
    ) {
        this.front = front;
        this.intermediate = intermediate;
        this.order = order;
        this.rear = rear;
    }

    public String getFront() {
        return front;
    }

    public List<IntermediateAxleWeightRating> getIntermediate() {
        return intermediate;
    }

    public int getOrder() {
        return order;
    }

    public String getRear() {
        return rear;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GrossAxleWeightRating that = (GrossAxleWeightRating) o;
        return order == that.order && Objects.equals(
                front,
                that.front
        ) && Objects.equals(
                intermediate,
                that.intermediate
        ) && Objects.equals(
                rear,
                that.rear
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                front,
                intermediate,
                order,
                rear
        );
    }

    @Override
    public String toString() {
        return "GrossAxleWeightRating{" +
                "front='" + front + '\'' +
                ", intermediate=" + intermediate +
                ", order=" + order +
                ", rear='" + rear + '\'' +
                '}';
    }
}
