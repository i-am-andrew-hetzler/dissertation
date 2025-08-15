package com.andrewhetzler.federal.recall_notifications;

import com.andrewhetzler.federal.recall_notifications.model.public_recall.PublicRecall;
import com.andrewhetzler.federal.recall_notifications.model.public_recall.VehicleRecalls;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;

import java.io.IOException;
import java.util.ArrayList;

import static com.andrewhetzler.federal.recall_notifications.RecallNotificationError.INVALID_REQUEST;
import static com.andrewhetzler.federal.recall_notifications.RecallNotificationError.NO_RECALLS_EXIST_FOR_VEHICLE;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/14/25
 **/
@Contract(
        name = "recallNotifications",
        info = @Info(
                title = "Recall Notifications",
                description = "The chaincode that powers the recall notifications use case.",
                version = "1.0.0",
                contact = @Contact(
                        email = "ahetzler@purdue.edu",
                        name = "Andrew Hetzler",
                        url = "www.andrewhetzler.com")))
@Default
public class RecallNotificationsChaincode {
    private final ObjectMapper objectMapper = new ObjectMapper();

    /*
    Given how Hyperledger Fabric works, the most out-of-the-box way to achieve this use case is to have two shapes:
    1. A shape to track all the recalls a vehicle has.
    2. A specific recall (the one that owners can report remedy status updates)
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public VehicleRecalls getRecallListForVehicle(
            final Context context,
            final String make,
            final String model,
            final String vin
    ) throws
      IOException {
        if (isNullOrBlank(make) || isNullOrBlank(model) || isNullOrBlank(vin)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        final VehicleRecalls recallsForVehicle = getAllRecallsForVehicle(
                context,
                make,
                model,
                vin
        );

        if (recallsForVehicle == null) {
            throw new ChaincodeException(
                    String.format(
                            "No recalls were found for vehicle %s.",
                            vin
                    ),
                    NO_RECALLS_EXIST_FOR_VEHICLE.toString()
            );
        }

        return recallsForVehicle;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public VehicleRecalls saveRecallListForVehicle(
            final Context context,
            final String campaignNumber,
            final String make,
            final String model,
            final String vin
    ) throws
      IOException {
        if (isNullOrBlank(make) || isNullOrBlank(model) || isNullOrBlank(campaignNumber) || isNullOrBlank(vin)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        VehicleRecalls recallsForVehicle = getAllRecallsForVehicle(
                context,
                make,
                model,
                vin
        );

        if (recallsForVehicle == null) {
            recallsForVehicle = new VehicleRecalls(new ArrayList<>());
        }

        recallsForVehicle.addRecallT(campaignNumber);

        save(
                context,
                make,
                model,
                vin,
                recallsForVehicle
        );

        return recallsForVehicle;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public PublicRecall getVehicleRecall(
            final Context context,
            final String campaignNumber,
            final String make,
            final String model,
            final String vin
    ) throws
      IOException {
        if (isNullOrBlank(make) || isNullOrBlank(model) || isNullOrBlank(campaignNumber) || isNullOrBlank(vin)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        final PublicRecall recall = getRecall(
                context,
                campaignNumber,
                make,
                model,
                vin
        );

        if (recall == null) {
            throw new ChaincodeException(
                    String.format(
                            "The recall # %s could not be found for vehicle %s.",
                            campaignNumber,
                            vin
                    ),
                    NO_RECALLS_EXIST_FOR_VEHICLE.toString()
            );
        }

        return recall;
    }

    private boolean isNullOrBlank(final String value) {
        return value == null || value.isBlank();
    }

    private VehicleRecalls getAllRecallsForVehicle(
            final Context context,
            final String make,
            final String model,
            final String vin
    ) throws
      IOException {
        final byte[] recalls = context.getStub().getState(
                String.format(
                        "%s-%s-%s",
                        make.toUpperCase(),
                        model.toUpperCase(),
                        vin.toUpperCase()
                )
        );

        return recalls != null ? objectMapper.readValue(
                recalls,
                VehicleRecalls.class
        ) : null;
    }

    private void save(
            final Context context,
            final String make,
            final String model,
            final String vin,
            final VehicleRecalls recalls
    ) throws
      JsonProcessingException {
        context.getStub().putState(
                String.format(
                        "%s-%s-%s",
                        make.toUpperCase(),
                        model.toUpperCase(),
                        vin.toUpperCase()
                ),
                objectMapper.writeValueAsBytes(recalls)
        );
    }

    private PublicRecall getRecall(
            final Context context,
            final String campaignNumber,
            final String make,
            final String model,
            final String vin
    ) throws
      IOException {
        final byte[] recall = context.getStub().getState(
                String.format(
                        "%s-%s-%s-%s",
                        make.toUpperCase(),
                        model.toUpperCase(),
                        vin.toUpperCase(),
                        campaignNumber.toUpperCase()
                )
        );

        return recall != null ? objectMapper.readValue(
                recall,
                PublicRecall.class
        ) : null;
    }

    private void save(
            final Context context,
            final PublicRecall recall
    ) throws
      JsonProcessingException {
        context.getStub().putState(
                String.format(
                        "%s-%s-%s-%s",
                        recall.getVehicleMake().toUpperCase(),
                        recall.getVehicleModel().toUpperCase(),
                        recall.getVehicleIdentificationNumber().toUpperCase(),
                        recall.getRecallCampaignNumber().toUpperCase()
                ),
                objectMapper.writeValueAsBytes(recall)
        );
    }
}
