package com.andrewhetzler.federal.recall_notifications;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/14/25
 **/
public enum RecallNotificationError {
    ERROR_SAVING_RECALL_DATA,
    INVALID_REQUEST,
    NO_LIST_EXISTS_FOR_CAMPAIGN_NUMBER,
    NO_RECALLS_EXIST_FOR_VEHICLE,
    UNAUTHORIZED_REQUEST,
    VEHICLE_RECALL_NOT_FOUND
}
