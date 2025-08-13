package com.andrewhetzler.federal.vehicle_state;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;

import java.io.IOException;
import java.util.Map;

import static com.andrewhetzler.federal.vehicle_state.VehicleStateError.ERROR_SAVING;
import static com.andrewhetzler.federal.vehicle_state.VehicleStateError.INVALID_REQUEST;
import static com.andrewhetzler.federal.vehicle_state.VehicleStateError.STATE_ALREADY_EXISTS;
import static com.andrewhetzler.federal.vehicle_state.VehicleStateError.STATE_DOES_NOT_EXIST;

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
public class VehicleStateChaincode {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String COLLECTION = System.getenv().getOrDefault(
            "vehicle_state_collection",
            "test_vehicle_state_collection"
    );
    public static final String VEHICLE_STATE_PROPERTIES = "vehicle_state_properties";

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean isValid(
            final Context context,
            final String vin,
            final String submittedHash
    ) throws
      IOException {
        if (isNullOrBlank(vin) || isNullOrBlank(submittedHash)) {
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

        return submittedHash.equalsIgnoreCase(state.getVehicleHash());
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void recordInitialState(final Context context) throws
                                                          IOException {
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

        if (state != null) {
            throw new ChaincodeException(
                    String.format(
                            "State already exists for vehicle %s.",
                            request.getVehicleIdentificationNumber()
                    ),
                    STATE_ALREADY_EXISTS.toString()
            );
        }

        try {
            context.getStub().putPrivateData(
                    VEHICLE_STATE_PROPERTIES,
                    request.getVehicleIdentificationNumber(),
                    objectMapper.writeValueAsBytes(request)
            );
        }
        catch (Exception e) {
            throw new ChaincodeException(
                    String.format(
                            "There was an error saving the state for vehicle %s",
                            request.getVehicleIdentificationNumber()
                    ),
                    ERROR_SAVING.toString()
            );
        }
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void updateState(final Context context) {
        Map<String, byte[]> transientMap = context.getStub().getTransient();
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

        if (state != null) {
            return objectMapper.readValue(
                    state,
                    VehicleState.class
            );
        }

        return null;
    }
}
