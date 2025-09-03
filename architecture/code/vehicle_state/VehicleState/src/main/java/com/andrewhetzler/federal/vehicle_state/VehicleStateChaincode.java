package com.andrewhetzler.federal.vehicle_state;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.contract.ClientIdentity;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static com.andrewhetzler.federal.vehicle_state.VehicleStateError.ERROR_SAVING;
import static com.andrewhetzler.federal.vehicle_state.VehicleStateError.INVALID_REQUEST;
import static com.andrewhetzler.federal.vehicle_state.VehicleStateError.STATE_ALREADY_EXISTS;
import static com.andrewhetzler.federal.vehicle_state.VehicleStateError.STATE_DOES_NOT_EXIST;
import static com.andrewhetzler.federal.vehicle_state.VehicleStateError.STATE_DOES_NOT_MATCH;
import static com.andrewhetzler.federal.vehicle_state.VehicleStateError.UNAUTHORIZED_REQUEST;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/13/25
 **/
@Contract(
        name = "vehicleState",
        info = @Info(
                title = "Vehicle State",
                description = "The chaincode that powers the vehicle state use case.",
                version = "1.0.0",
                contact = @Contact(
                        email = "ahetzler@purdue.edu",
                        name = "Andrew Hetzler",
                        url = "www.andrewhetzler.com")))
@Default
public class VehicleStateChaincode implements ContractInterface {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String COLLECTION = System.getenv().getOrDefault(
            "VEHICLE_STATE_COLLECTION",
            "purdue-motor-company-vehicles"
    );
    public static final String VEHICLE_STATE_PROPERTIES = "vehicle_state_properties";
    private static final String AUTHORIZED_RECORD_INITIAL_STATE_MSP_IDS = System.getenv().getOrDefault(
            "VEHICLE_STATE_AUTHORIZED_RECORD_INITIAL_STATE_MSP_IDS",
            "PurdueFinalAssemblerMSP;PurdueMotorCompanyMSP"
    );
    public static final String AUTHORIZED_UPDATE_STATE_MSP_IDS = System.getenv().getOrDefault(
            "VEHICLE_STATE_AUTHORIZED_UPDATE_STATE_MSP_IDS",
            "PurdueDealerTechnicianMSP;PurdueVehicleOwnerMSP;PurdueMotorCompanyMSP"
    );
    public static final String AUTHORIZED_OVERRIDE_STATE_MSP_IDS = System.getenv().getOrDefault(
            "VEHICLE_STATE_AUTHORIZED_OVERRIDE_STATE_MSP_IDS",
            "PurdueDealerTechnicianMSP;PurdueMotorCompanyMSP"
    );

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean isValid(
            final Context context,
            final String vin,
            final String calculatedHash
    ) throws
      IOException {
        if (isNullOrBlank(vin) || isNullOrBlank(calculatedHash)) {
            throw new ChaincodeException(
                    String.format(
                            "Invalid request.",
                            vin
                    ),
                    INVALID_REQUEST.toString()
            );
        }

        final VehicleState state = getState(
                context,
                vin
        );

        if (state == null) {
            throw new ChaincodeException(
                    String.format(
                            "No state found for vehicle %s.",
                            vin
                    ),
                    STATE_DOES_NOT_EXIST.toString()
            );
        }

        return calculatedHash.equalsIgnoreCase(state.getVehicleHash());
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void recordInitialState(final Context context) throws
                                                          IOException {
        if (!isAuthorized(
                AUTHORIZED_RECORD_INITIAL_STATE_MSP_IDS,
                context.getClientIdentity()
        )) {
            throw new ChaincodeException(
                    "Unauthorized request.",
                    UNAUTHORIZED_REQUEST.toString()
            );
        }

        if (context.getStub().getTransient() == null) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        Map<String, byte[]> transientMap = context.getStub().getTransient();

        if (!transientMap.containsKey(VEHICLE_STATE_PROPERTIES)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        final VehicleState request;

        try {
            request = objectMapper.readValue(
                    transientMap.get(VEHICLE_STATE_PROPERTIES),
                    VehicleState.class
            );
        } catch (Exception e) {
            throw new ChaincodeException(
                    "Unable to deserialize the vehicle_state_properties.",
                    INVALID_REQUEST.toString()
            );
        }

        final VehicleState state = getState(
                context,
                request.getVehicleIdentificationNumber()
        );

        if (state != null) {
            throw new ChaincodeException(
                    String.format(
                            "State already exists for vehicle %s.",
                            request.getVehicleIdentificationNumber()
                    ),
                    STATE_ALREADY_EXISTS.toString()
            );
        }

        saveState(
                context,
                request
        );
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void updateState(final Context context) throws
                                                   IOException {
        if (!isAuthorized(
                AUTHORIZED_UPDATE_STATE_MSP_IDS,
                context.getClientIdentity()
        )) {
            throw new ChaincodeException(
                    "Unauthorized request.",
                    UNAUTHORIZED_REQUEST.toString()
            );
        }

        if (context.getStub().getTransient() == null) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        Map<String, byte[]> transientMap = context.getStub().getTransient();

        if (!transientMap.containsKey(VEHICLE_STATE_PROPERTIES)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (!transientMap.containsKey("calculated_hash")) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        final VehicleState request;
        final String calculatedHash = new String(transientMap.get("calculated_hash"));

        try {
            request = objectMapper.readValue(
                    transientMap.get(VEHICLE_STATE_PROPERTIES),
                    VehicleState.class
            );
        } catch (Exception e) {
            throw new ChaincodeException(
                    "Unable to deserialize the vehicle_state_properties.",
                    INVALID_REQUEST.toString()
            );
        }

        final VehicleState state = getState(
                context,
                request.getVehicleIdentificationNumber()
        );

        if (state == null) {
            throw new ChaincodeException(
                    String.format(
                            "No state found for vehicle %s.",
                            request.getVehicleIdentificationNumber()
                    ),
                    STATE_DOES_NOT_EXIST.toString()
            );
        }

//        if (!new String(transientMap.get("calculated_hash")).equalsIgnoreCase(state.getVehicleHash())) {
        if (!state.getVehicleHash().equals(calculatedHash)) {
            System.out.println(String.format("Calculated hash: %s versus expected hash: %s", calculatedHash, state.getVehicleHash()));

            throw new ChaincodeException(
                    String.format(
                            "The calculated state does not match the expected state for vehicle %s.",
                            request.getVehicleIdentificationNumber()
                    ),
                    STATE_DOES_NOT_MATCH.toString()
            );
        }

        saveState(
                context,
                request
        );
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void overrideState(final Context context) throws
                                                     IOException {
        if (!isAuthorized(
                AUTHORIZED_OVERRIDE_STATE_MSP_IDS,
                context.getClientIdentity()
        )) {
            throw new ChaincodeException(
                    "Unauthorized request.",
                    UNAUTHORIZED_REQUEST.toString()
            );
        }

        if (context.getStub().getTransient() == null) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        Map<String, byte[]> transientMap = context.getStub().getTransient();

        if (!transientMap.containsKey(VEHICLE_STATE_PROPERTIES)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        final VehicleState request = objectMapper.readValue(
                transientMap.get(VEHICLE_STATE_PROPERTIES),
                VehicleState.class
        );
        final VehicleState state = getState(
                context,
                request.getVehicleIdentificationNumber()
        );

        if (state == null) {
            throw new ChaincodeException(
                    String.format(
                            "No state found for vehicle %s.",
                            request.getVehicleIdentificationNumber()
                    ),
                    STATE_DOES_NOT_EXIST.toString()
            );
        }

        saveState(
                context,
                request
        );
    }

    private boolean isNullOrBlank(final String value) {
        return value == null || value.isBlank();
    }

    private VehicleState getState(
            final Context context,
            final String vin
    ) throws
      IOException {
        final byte[] state = context.getStub().getPrivateData(
                COLLECTION,
                vin.toUpperCase()
        );

        if (state != null && state.length > 0) {
            return objectMapper.readValue(
                    state,
                    VehicleState.class
            );
        }

        return null;
    }

    private void saveState(
            final Context context,
            final VehicleState state
    ) throws
      ChaincodeException {
        try {
            context.getStub().putPrivateData(
                    COLLECTION,
                    state.getVehicleIdentificationNumber().toUpperCase(),
                    objectMapper.writeValueAsBytes(state)
            );
        }
        catch (Exception e) {
            throw new ChaincodeException(
                    String.format(
                            "There was an error saving the state for vehicle %s.",
                            state.getVehicleIdentificationNumber()
                    ),
                    ERROR_SAVING.toString()
            );
        }
    }

    private boolean isAuthorized(
            final String authorizedMspIds,
            final
            ClientIdentity requestorIdentity
    ) {
        System.out.println(String.format("Authorized MSPs: %s", authorizedMspIds));
        System.out.println(String.format("Requestor MSP: %s:  ", requestorIdentity.getMSPID()));


        return Arrays.stream(authorizedMspIds.split(";")).anyMatch(msp -> msp.equals(requestorIdentity.getMSPID()));
    }
}
