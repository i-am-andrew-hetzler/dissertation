package com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/15/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class Recall {
    private final String campaignNumber;
    private final String remedyStatus;

    public Recall(
            @JsonProperty("campaignNumber") String campaignNumber,
            @JsonProperty("remedyStatus") String remedyStatus
    ) {
        this.campaignNumber = campaignNumber;
        this.remedyStatus = remedyStatus;
    }

    public String getCampaignNumber() {
        return campaignNumber;
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
                remedyStatus,
                recall.remedyStatus
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                campaignNumber,
                remedyStatus
        );
    }

    @Override
    public String toString() {
        return "Recall{" +
                "campaignNumber='" + campaignNumber + '\'' +
                ", remedyStatus='" + remedyStatus + '\'' +
                '}';
    }
}
