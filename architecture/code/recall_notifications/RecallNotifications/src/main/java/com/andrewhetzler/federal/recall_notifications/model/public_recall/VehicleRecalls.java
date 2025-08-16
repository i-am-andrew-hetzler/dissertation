package com.andrewhetzler.federal.recall_notifications.model.public_recall;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.hyperledger.fabric.contract.annotation.DataType;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/14/25
 **/
@JsonPropertyOrder(alphabetic = true)
@DataType
public class VehicleRecalls {
    private final List<String> recalls;

    public VehicleRecalls(@JsonProperty("recalls") List<String> recalls) {
        this.recalls = recalls != null ? recalls : Collections.emptyList();
    }

    public void addRecall(String recallT) {
        this.recalls.add(recallT);
    }

    public List<String> getRecalls() {
        return recalls;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VehicleRecalls that = (VehicleRecalls) o;
        return Objects.equals(
                recalls,
                that.recalls
        );
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(recalls);
    }

    @Override
    public String toString() {
        return "VehicleRecalls{" +
                "recalls=" + recalls +
                '}';
    }
}
