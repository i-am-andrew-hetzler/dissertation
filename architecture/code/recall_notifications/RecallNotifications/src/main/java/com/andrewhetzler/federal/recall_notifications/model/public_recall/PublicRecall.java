package com.andrewhetzler.federal.recall_notifications.model.public_recall;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.hyperledger.fabric.contract.annotation.DataType;

import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/14/25
 **/

@JsonPropertyOrder(alphabetic = true)
@DataType
public class PublicRecall {
    private final Recall recall;
    private final String schemaVersion;
    private final Vehicle vehicle;

    public PublicRecall(
            @JsonProperty("recall") Recall recall,
            @JsonProperty("schemaVersion") String schemaVersion,
            @JsonProperty("vehicle") Vehicle vehicle
    ) {
        this.recall = recall;
        this.schemaVersion = schemaVersion;
        this.vehicle = vehicle;
    }

    public Recall getRecall() {
        return recall;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    @JsonIgnore
    public String getVehicleMake() {
        return vehicle.getMake();
    }

    @JsonIgnore
    public String getVehicleModel() {
        return vehicle.getModel();
    }

    @JsonIgnore
    public String getVehicleIdentificationNumber() {
        return vehicle.getIdentificationNumber();
    }

    @JsonIgnore
    public String getRecallCampaignNumber() {
        return recall.getCampaignNumber();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PublicRecall that = (PublicRecall) o;
        return Objects.equals(
                recall,
                that.recall
        ) && Objects.equals(
                schemaVersion,
                that.schemaVersion
        ) && Objects.equals(
                vehicle,
                that.vehicle
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                recall,
                schemaVersion,
                vehicle
        );
    }

    @Override
    public String toString() {
        return "PublicRecall{" +
                "recall=" + recall +
                ", schemaVersion='" + schemaVersion + '\'' +
                ", vehicle=" + vehicle +
                '}';
    }
}
