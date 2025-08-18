package com.andrewhetzler.state.registration;

import com.andrewhetzler.state.registration.model.Address;
import com.andrewhetzler.state.registration.model.Registrant;
import com.andrewhetzler.state.registration.model.Registration;
import com.andrewhetzler.state.registration.model.RegistrationSchema;
import com.andrewhetzler.state.registration.model.persisted.PersistedRegistrant;
import com.andrewhetzler.state.registration.model.persisted.PersistedRegistrationSchema;
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
import java.util.Arrays;
import java.util.List;

import static com.andrewhetzler.state.registration.RegistrationChaincodeError.INVALID_REQUEST;
import static com.andrewhetzler.state.registration.RegistrationChaincodeError.REGISTRATION_DOES_NOT_EXIST;
import static com.andrewhetzler.state.registration.RegistrationChaincodeError.UNAUTHORIZED_REQUEST;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/18/25
 **/
@Contract(
        name = "registration",
        info = @Info(
                title = "Registration",
                description = "The chaincode that powers the registration use case.",
                version = "1.0.0",
                contact = @Contact(
                        email = "ahetzler@purdue.edu",
                        name = "Andrew Hetzler",
                        url = "www.andrewhetzler.com")))
@Default
public class RegistrationChaincode {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String STATE_REGISTRATION_COLLECTION = System.getenv().getOrDefault(
            "STATE_REGISTRATION_COLLECTION",
            "TestStateRegistrationCollection"
    );
    private static final List<String> STATE_AGENCIES_MSP_IDS = Arrays.asList(System.getenv().getOrDefault(
            "STATE_AGENCIES_MSP_IDS",
            "TestStateMSP"
    ).split(";"));
    private static final String STATE_DMV_MSP_ID = System.getenv().getOrDefault(
            "STATE_DMV_MSP_ID",
            "TestStateDmvMSP"
    );
    private static final String STATE_DMV_REGISTRANT_MSP_ID = System.getenv().getOrDefault(
            "STATE_DMV_REGISTRANT_MSP_ID",
            "TestStateDmvRegistrantMSP"
    );
    private static final List<String> THIRD_PARTY_MSP_IDS = Arrays.asList(System.getenv().getOrDefault(
            "THIRD_PARTY_MSP_IDS",
            "TestInsuranceCoMSP"
    ).split(";"));

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public RegistrationSchema viewRegistration(
            final Context context,
            final String registrationNumber
    ) throws
      IOException {
        if (!isAuthorized(context.getClientIdentity())) {
            throw new ChaincodeException(
                    "Unauthorized request.",
                    UNAUTHORIZED_REQUEST.toString()
            );
        }

        if (isNullOrBlank(registrationNumber)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        final PersistedRegistrationSchema persistedRegistration = getRegistration(
                context,
                registrationNumber
        );

        if (persistedRegistration == null) {
            throw new ChaincodeException(
                    String.format(
                            "No registration exists for registration number %s.",
                            registrationNumber
                    ),
                    REGISTRATION_DOES_NOT_EXIST.toString()
            );
        }

        if (isMspIdInStateAgencies(context.getClientIdentity()) || isMspIdTheStateDmv(context.getClientIdentity())) {
            return createRegistrationFromPersistedRegistration(persistedRegistration);
        }
        else if (isMspIdARegistrant(context.getClientIdentity())) {
            if (!isRequestorTheRegistrant(
                    context.getClientIdentity(),
                    persistedRegistration.getRegistrants()
            )) {
                throw new ChaincodeException(
                        "Unauthorized request.",
                        UNAUTHORIZED_REQUEST.toString()
                );
            }

            return createRegistrationFromPersistedRegistration(persistedRegistration);
        } else if (isMspIdInThirdPartyMspIds(context.getClientIdentity())) {
            final RegistrationSchema registration = createRegistrationFromPersistedRegistration(persistedRegistration);

            saveRegistrationDataTo3rdPartyCollection(
                    context,
                    registration
            );

            return registration;
        } else {
            /*
            This should not happen
             */
            throw new ChaincodeException(
                    "Unauthorized request.",
                    UNAUTHORIZED_REQUEST.toString()
            );
        }
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public RegistrationSchema viewRegistrationIn3rdPartyCollection(
            final Context context,
            final String registrationNumber
    ) {

        return null;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public RegistrationSchema issueRegistration(
            final Context context
    ) {

        return null;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void revokeRegistration(
            final Context context,
            final String registrationNumber
    ) {

    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void cancelRegistration(
            final Context context,
            final String registrationNumber
    ) {

    }

    private boolean isMspIdInStateAgencies(final ClientIdentity clientIdentity) {
        return STATE_AGENCIES_MSP_IDS.contains(clientIdentity.getMSPID());
    }

    private boolean isMspIdTheStateDmv(final ClientIdentity clientIdentity) {
        return STATE_DMV_MSP_ID.equalsIgnoreCase(clientIdentity.getMSPID());
    }

    private boolean isMspIdARegistrant(final ClientIdentity clientIdentity) {
        return STATE_DMV_REGISTRANT_MSP_ID.equalsIgnoreCase(clientIdentity.getMSPID());
    }

    private boolean isMspIdInThirdPartyMspIds(final ClientIdentity clientIdentity) {
        return THIRD_PARTY_MSP_IDS.contains(clientIdentity.getMSPID());
    }

    private boolean isAuthorized(final ClientIdentity clientIdentity) {
        return isMspIdInStateAgencies(clientIdentity) ||
                isMspIdTheStateDmv(clientIdentity) ||
                isMspIdARegistrant(clientIdentity) ||
                isMspIdInThirdPartyMspIds(clientIdentity);
    }

    private boolean isNullOrBlank(final String value) {
        return value == null || value.isBlank();
    }

    private PersistedRegistrationSchema getRegistration(
            final Context context,
            final String registrationNumber
    ) throws
      IOException {
        final byte[] persistedRegistration = context.getStub().getPrivateData(
                STATE_REGISTRATION_COLLECTION,
                registrationNumber.toUpperCase()
        );

        return persistedRegistration != null ? objectMapper.readValue(
                persistedRegistration,
                PersistedRegistrationSchema.class
        ) : null;
    }

    private List<Registrant> convertPersistedRegistrant(final List<PersistedRegistrant> registrants) {
        return registrants.stream().map(registrant -> new Registrant(
                registrant.getAddresses().stream().map(address -> new Address(
                        address.getStreet1(),
                        address.getStreet2(),
                        address.getCity(),
                        address.getCounty(),
                        address.getZipCode()
                )).toList(),
                registrant.getName()
        )).toList();
    }

    private boolean isRequestorTheRegistrant(
            final ClientIdentity clientIdentity,
            final List<PersistedRegistrant> registrants
    ) {
        return registrants.stream().map(registrant -> registrant.getUniqueId()).toList().contains(clientIdentity.getId());
    }

    private RegistrationSchema createRegistrationFromPersistedRegistration(final PersistedRegistrationSchema registration) {
        return new RegistrationSchema(
                registration.getOther(),
                convertPersistedRegistrant(registration.getRegistrants()),
                new Registration(
                        registration.getRegistration().getNumber(),
                        registration.getRegistration().getVehicleDescription()
                ),
                registration.getSchemaVersion()
        );
    }

    private void saveRegistrationDataTo3rdPartyCollection(
            final Context context,
            final RegistrationSchema registration
    ) throws
      JsonProcessingException {
        context.getStub().putPrivateData(
                String.format(
                        "%s_REGISTRATION_COLLECTION",
                        context.getClientIdentity().getMSPID().toUpperCase()
                ),
                registration.getRegistration().getNumber().toUpperCase(),
                objectMapper.writeValueAsBytes(registration)
        );
    }
}
