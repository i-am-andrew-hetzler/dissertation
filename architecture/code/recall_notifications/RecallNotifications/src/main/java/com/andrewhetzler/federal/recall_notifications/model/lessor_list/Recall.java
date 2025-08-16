package com.andrewhetzler.federal.recall_notifications.model.lessor_list;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/16/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class Recall {
    private final String campaignNumber;
    private final String dateNotificationMailed;

    public Recall(
            @JsonProperty("campaignNumber") String campaignNumber,
            @JsonProperty("dateNotificationMailed") String dateNotificationMailed
    ) {
        this.campaignNumber = campaignNumber;
        this.dateNotificationMailed = dateNotificationMailed;
    }

    public String getCampaignNumber() {
        return campaignNumber;
    }

    public String getDateNotificationMailed() {
        return dateNotificationMailed;
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
                dateNotificationMailed,
                recall.dateNotificationMailed
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                campaignNumber,
                dateNotificationMailed
        );
    }

    @Override
    public String toString() {
        return "Recall{" +
                "campaignNumber='" + campaignNumber + '\'' +
                ", dateNotificationMailed='" + dateNotificationMailed + '\'' +
                '}';
    }
}
