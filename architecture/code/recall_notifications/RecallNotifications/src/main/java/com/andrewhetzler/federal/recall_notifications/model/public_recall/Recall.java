package com.andrewhetzler.federal.recall_notifications.model.public_recall;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/14/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class Recall {
    private final String campaignNumber;
    private final String date;
    private final String description;
    private final String remedyProgramDescription;
    private final String remedyStatus;

    public Recall(
            @JsonProperty("campaignNumber") String campaignNumber,
            @JsonProperty("date") String date,
            @JsonProperty("description") String description,
            @JsonProperty("remedyProgramDescription") String remedyProgramDescription,
            @JsonProperty("remedyStatus") String remedyStatus
    ) {
        this.campaignNumber = campaignNumber;
        this.date = date;
        this.description = description;
        this.remedyProgramDescription = remedyProgramDescription;
        this.remedyStatus = remedyStatus;
    }

    public String getCampaignNumber() {
        return campaignNumber;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getRemedyProgramDescription() {
        return remedyProgramDescription;
    }

    public String getRemedyStatus() {
        return remedyStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Recall recall = (Recall) o;
        return Objects.equals(
                campaignNumber,
                recall.campaignNumber
        ) && Objects.equals(
                date,
                recall.date
        ) && Objects.equals(
                description,
                recall.description
        ) && Objects.equals(
                remedyProgramDescription,
                recall.remedyProgramDescription
        ) && Objects.equals(
                remedyStatus,
                recall.remedyStatus
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                campaignNumber,
                date,
                description,
                remedyProgramDescription,
                remedyStatus
        );
    }

    @Override
    public String toString() {
        return "Recall{" +
                "campaignNumber='" + campaignNumber + '\'' +
                ", date='" + date + '\'' +
                ", description='" + description + '\'' +
                ", remedyProgramDescription='" + remedyProgramDescription + '\'' +
                ", remedyStatus='" + remedyStatus + '\'' +
                '}';
    }
}
