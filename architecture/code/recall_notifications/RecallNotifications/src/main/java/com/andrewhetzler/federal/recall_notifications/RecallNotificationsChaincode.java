package com.andrewhetzler.federal.recall_notifications;

import com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list.Address;
import com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list.ImpactedOwnerList;
import com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list.Owner;
import com.andrewhetzler.federal.recall_notifications.model.public_recall.PublicRecall;
import com.andrewhetzler.federal.recall_notifications.model.public_recall.Recall;
import com.andrewhetzler.federal.recall_notifications.model.public_recall.Vehicle;
import com.andrewhetzler.federal.recall_notifications.model.public_recall.VehicleRecalls;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.contract.ClientIdentity;
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
import static com.andrewhetzler.federal.recall_notifications.RecallNotificationError.NO_LIST_EXISTS_FOR_CAMPAIGN_NUMBER;
import static com.andrewhetzler.federal.recall_notifications.RecallNotificationError.NO_RECALLS_EXIST_FOR_VEHICLE;
import static com.andrewhetzler.federal.recall_notifications.RecallNotificationError.UNAUTHORIZED_REQUEST;
import static com.andrewhetzler.federal.recall_notifications.RecallNotificationError.VEHICLE_RECALL_NOT_FOUND;

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
    private static final String NHTSA_MSP_ID = "NHTSAMSP";

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

        final String existingRecall = recallsForVehicle.getRecalls().stream().filter(recall -> recall
                .equalsIgnoreCase(campaignNumber)).findFirst().orElse(null);

        if (existingRecall == null) {
            recallsForVehicle.addRecall(campaignNumber);

            save(
                    context,
                    make,
                    model,
                    vin,
                    recallsForVehicle
            );
        }

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
                    VEHICLE_RECALL_NOT_FOUND.toString()
            );
        }

        return recall;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public PublicRecall saveVehicleRecall(
            final Context context,
            final String campaignNumber,
            final String date,
            final String description,
            final String remedyProgramDescription,
            final String remedyStatus,
            final String schemaVersion,
            final String make,
            final String model,
            final String vin
    ) throws
      IOException {
        if (isNullOrBlank(campaignNumber) || isNullOrBlank(date) || isNullOrBlank(description) ||
                isNullOrBlank(remedyProgramDescription) || isNullOrBlank(remedyStatus) || isNullOrBlank(schemaVersion) ||
                isNullOrBlank(make) || isNullOrBlank(model) || isNullOrBlank(vin)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        final PublicRecall recall = new PublicRecall(
                new Recall(
                        campaignNumber,
                        date,
                        description,
                        remedyProgramDescription,
                        remedyStatus
                ),
                schemaVersion,
                new Vehicle(
                        vin,
                        make,
                        model
                )
        );

        save(
                context,
                recall
        );
        saveRecallListForVehicle(
                context,
                campaignNumber,
                make,
                model,
                vin
        );

        return recall;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public ImpactedOwnerList viewImpactedOwnersForRecall(
            final Context context,
            final String campaignNumber,
            final String collection
    ) throws
      IOException {
        if (!isAuthorized(
                context.getClientIdentity(),
                collection
        )) {
            throw new ChaincodeException(
                    "Unauthorized request.",
                    UNAUTHORIZED_REQUEST.toString()
            );
        }

        if (isNullOrBlank(campaignNumber) || isNullOrBlank(collection)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        final ImpactedOwnerList impactedOwnersList = getImpactedOwnersListForRecall(
                context,
                campaignNumber,
                collection
        );

        if (impactedOwnersList == null) {
            throw new ChaincodeException(
                    String.format(
                            "No list exists for campaign number %s.",
                            campaignNumber
                    ),
                    NO_LIST_EXISTS_FOR_CAMPAIGN_NUMBER.toString()
            );
        }

        return impactedOwnersList;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public ImpactedOwnerList saveImpactedOwnersForRecall(
            final Context context,
            final String vin,
            final String name,
            final String street1,
            final String street2,
            final String city,
            final String state,
            final String zipCode,
            final String campaignNumber,
            final String remedyStatus,
            final String schemaVersion,
            final String collection
    ) throws
      IOException {
        if (!isAuthorized(
                context.getClientIdentity(),
                collection
        )) {
            throw new ChaincodeException(
                    "Unauthorized request.",
                    UNAUTHORIZED_REQUEST.toString()
            );
        }

        if (isNullOrBlank(vin) || isNullOrBlank(name) || isNullOrBlank(street1) || isNullOrBlank(city) || isNullOrBlank(state) || isNullOrBlank(zipCode) || isNullOrBlank(campaignNumber) || isNullOrBlank(remedyStatus) || isNullOrBlank(schemaVersion) || isNullOrBlank(collection)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        final ImpactedOwnerList impactedOwnersList = getImpactedOwnersListForRecall(
                context,
                campaignNumber,
                collection
        );

        final ImpactedOwnerList newList;
        com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list.Vehicle existingVehicle = impactedOwnersList != null ? impactedOwnersList.getVehicles().stream().filter(vehicle -> vehicle.getIdentificationNumber().equalsIgnoreCase(vin)).findFirst().orElse(null) : null;

        if (existingVehicle != null) {
            newList = new ImpactedOwnerList(
                    impactedOwnersList.getVehicles(),
                    schemaVersion
            );

            newList.getVehicles().set(
                    newList.getVehicles().indexOf(existingVehicle),
                    new com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list.Vehicle(
                            vin,
                            new Owner(
                                    new Address(
                                            street1,
                                            street2,
                                            city,
                                            state,
                                            zipCode
                                    ),
                                    name
                            ),
                            new com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list.Recall(
                                    campaignNumber,
                                    remedyStatus
                            )
                    )
            );
        }
        else {
            newList = new ImpactedOwnerList(
                    impactedOwnersList != null ? impactedOwnersList.getVehicles() : new ArrayList<>(),
                    schemaVersion
            );

            newList.getVehicles().add(
                    new com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list.Vehicle(
                            vin,
                            new Owner(
                                    new Address(
                                            street1,
                                            street2,
                                            city,
                                            state,
                                            zipCode
                                    ),
                                    name
                            ),
                            new com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list.Recall(
                                    campaignNumber,
                                    remedyStatus
                            )
                    )
            );
        }

        save(
                context,
                campaignNumber,
                collection,
                newList
        );

        return newList;
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

    private boolean isAuthorized(
            final ClientIdentity clientIdentity,
            final String collection
    ) {
        return NHTSA_MSP_ID.equalsIgnoreCase(clientIdentity.getMSPID()) || (clientIdentity.getMSPID().equalsIgnoreCase(collection));
    }

    private ImpactedOwnerList getImpactedOwnersListForRecall(
            final Context context,
            final String campaignNumber,
            final String collection
    ) throws
      IOException {
        final byte[] list = context.getStub().getPrivateData(
                collection.toUpperCase(),
                campaignNumber.toUpperCase()
        );

        if (list != null) {
            return objectMapper.readValue(
                    list,
                    ImpactedOwnerList.class
            );
        }

        return null;
    }

    private void save(
            final Context context,
            final String campaignNumber,
            final String collection,
            final ImpactedOwnerList impactedOwnersList
    ) throws
      JsonProcessingException {
        context.getStub().putPrivateData(
                collection.toUpperCase(),
                campaignNumber.toUpperCase(),
                objectMapper.writeValueAsBytes(impactedOwnersList)
        );
    }
}
