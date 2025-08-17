package com.andrewhetzler.state.licensing;

import com.andrewhetzler.state.licensing.model.Address;
import com.andrewhetzler.state.licensing.model.License;
import com.andrewhetzler.state.licensing.model.LicenseSchema;
import com.andrewhetzler.state.licensing.model.Licensee;
import com.andrewhetzler.state.licensing.model.persisted.PersistedAddress;
import com.andrewhetzler.state.licensing.model.persisted.PersistedLicenseSchema;
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

import static com.andrewhetzler.state.licensing.LicenseChaincodeError.INVALID_REQUEST;
import static com.andrewhetzler.state.licensing.LicenseChaincodeError.LICENSE_DOES_NOT_EXIST;
import static com.andrewhetzler.state.licensing.LicenseChaincodeError.UNAUTHORIZED_REQUEST;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/17/25
 **/
@Contract(
        name = "licensing",
        info = @Info(
                title = "Licensing",
                description = "The chaincode that powers the licensing use case.",
                version = "1.0.0",
                contact = @Contact(
                        email = "ahetzler@purdue.edu",
                        name = "Andrew Hetzler",
                        url = "www.andrewhetzler.com")))
@Default
public class LicensingChaincode {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String STATE_LICENSE_COLLECTION = System.getenv().getOrDefault(
            "STATE_LICENSE_COLLECTION",
            "TestStateLicenseCollection"
    );
    private static final List<String> STATE_AGENCIES_MSP_IDS = Arrays.asList(System.getenv().getOrDefault(
            "STATE_AGENCIES_MSP_IDS",
            "TestStateMSP"
    ).split(";"));
    private static final String STATE_DMV_MSP_ID = System.getenv().getOrDefault(
            "STATE_DMV_MSP_ID",
            "TestStateDmvMSP"
    );
    private static final List<String> THIRD_PARTY_MSP_IDS = Arrays.asList(System.getenv().getOrDefault(
            "THIRD_PARTY_MSP_IDS",
            "TestInsuranceCoMSP"
    ).split(";"));

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public LicenseSchema viewLicense(
            final Context context,
            final String licenseNumber
    ) throws
      IOException {
        if (!isAuthorized(context.getClientIdentity())) {
            throw new ChaincodeException(
                    "Unauthorized request.",
                    UNAUTHORIZED_REQUEST.toString()
            );
        }

        if (isNullOrBlank(licenseNumber)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        PersistedLicenseSchema persistedLicense = getLicense(
                context,
                licenseNumber
        );

        if (persistedLicense == null) {
            throw new ChaincodeException(
                    String.format(
                            "No license exists for license number %s.",
                            licenseNumber
                    ),
                    LICENSE_DOES_NOT_EXIST.toString()
            );
        }
        /*
        if statemsp, return license;
        if dmv msp and uniqueid == clientidenty, then return license;
        if 3rd party, write to 3rd party collection and return locense;
        otherwise return error;
         */
        if (isMspIdInStateAgencies(context.getClientIdentity())) {
            final List<Address> addresses = convertPersistedAddressesToAddresses(persistedLicense.getLicenseeAddresses());

            return new LicenseSchema(
                    new License(
                            persistedLicense.getLicenseClasses(),
                            persistedLicense.getLicenseNumber()
                    ),
                    new Licensee(
                            addresses,
                            new Birthdate(
                                    persistedLicense.getLicenseeBirthdate().getDay(),
                                    persistedLicense.getLicenseeBirthdate().getMonth(),
                                    persistedLicense.getLicenseeBirthdate().getYear()
                            ),
                            persistedLicense.getLicenseeDescription(),
                            persistedLicense.isLicenseeAVeteran(),
                            persistedLicense.getLicenseeName(),
                            persistedLicense.getLicenseePhotograph(),
                            persistedLicense.getLicenseeSignature()
                    ),
                    persistedLicense.getOther(),
                    persistedLicense.getSchemaVersion()
            );
        }
        else if (isMspIdTheStateDmv(context.getClientIdentity())) {
            if (!context.getClientIdentity().getId().equals(persistedLicense.getLicenseeUniqueId())) {
                throw new ChaincodeException(
                        "Unauthorized request.",
                        UNAUTHORIZED_REQUEST.toString()
                );
            }

            final List<Address> addresses = convertPersistedAddressesToAddresses(persistedLicense.getLicenseeAddresses());

            return new LicenseSchema(
                    new License(
                            persistedLicense.getLicenseClasses(),
                            persistedLicense.getLicenseNumber()
                    ),
                    new Licensee(
                            addresses,
                            new Birthdate(
                                    persistedLicense.getLicenseeBirthdate().getDay(),
                                    persistedLicense.getLicenseeBirthdate().getMonth(),
                                    persistedLicense.getLicenseeBirthdate().getYear()
                            ),
                            persistedLicense.getLicenseeDescription(),
                            persistedLicense.isLicenseeAVeteran(),
                            persistedLicense.getLicenseeName(),
                            persistedLicense.getLicenseePhotograph(),
                            persistedLicense.getLicenseeSignature()
                    ),
                    persistedLicense.getOther(),
                    persistedLicense.getSchemaVersion()
            );
        }
        else if (isMspIdInThirdPartyMspIds(context.getClientIdentity())) {
            final List<Address> addresses = convertPersistedAddressesToAddresses(persistedLicense.getLicenseeAddresses());
            final LicenseSchema license = new LicenseSchema(
                    new License(
                            persistedLicense.getLicenseClasses(),
                            persistedLicense.getLicenseNumber()
                    ),
                    new Licensee(
                            addresses,
                            new Birthdate(
                                    persistedLicense.getLicenseeBirthdate().getDay(),
                                    persistedLicense.getLicenseeBirthdate().getMonth(),
                                    persistedLicense.getLicenseeBirthdate().getYear()
                            ),
                            persistedLicense.getLicenseeDescription(),
                            persistedLicense.isLicenseeAVeteran(),
                            persistedLicense.getLicenseeName(),
                            persistedLicense.getLicenseePhotograph(),
                            persistedLicense.getLicenseeSignature()
                    ),
                    persistedLicense.getOther(),
                    persistedLicense.getSchemaVersion()
            );

            saveLicenseDataTo3rdPartyCollection(
                    context,
                    license
            );

            return license;
        }
        else {
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
    public LicenseSchema viewLicenseIn3rdPartyCollection(
            final Context context,
            final String licenseNumber
    ) throws
      IOException {
        if (!isMspIdInThirdPartyMspIds(context.getClientIdentity())) {
            throw new ChaincodeException(
                    "Unauthorized request.",
                    UNAUTHORIZED_REQUEST.toString()
            );
        }

        if (isNullOrBlank(licenseNumber)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        final byte[] license = context.getStub().getPrivateData(
                String.format("%s_LICENSE_COLLECTION", context.getClientIdentity().getMSPID().toUpperCase()),
                licenseNumber.toUpperCase()
        );

        if (license == null) {
            throw new ChaincodeException(
                    String.format(
                            "No license exists for license number %s.",
                            licenseNumber
                    ),
                    LICENSE_DOES_NOT_EXIST.toString()
            );
        }

        return objectMapper.readValue(
                license,
                LicenseSchema.class
        );
    }

    private boolean isMspIdInStateAgencies(final ClientIdentity clientIdentity) {
        return STATE_AGENCIES_MSP_IDS.contains(clientIdentity.getMSPID());
    }

    private boolean isMspIdTheStateDmv(final ClientIdentity clientIdentity) {
        return STATE_DMV_MSP_ID.equalsIgnoreCase(clientIdentity.getMSPID());
    }

    private boolean isMspIdInThirdPartyMspIds(final ClientIdentity clientIdentity) {
        return THIRD_PARTY_MSP_IDS.contains(clientIdentity.getMSPID());
    }

    private boolean isAuthorized(final ClientIdentity clientIdentity) {
        return isMspIdInStateAgencies(clientIdentity) ||
                isMspIdTheStateDmv(clientIdentity) ||
                isMspIdInThirdPartyMspIds(clientIdentity);
    }

    private boolean isNullOrBlank(final String value) {
        return value == null || value.isBlank();
    }

    private PersistedLicenseSchema getLicense(
            final Context context,
            final String licenseNumber
    ) throws
      IOException {
        final byte[] persistedLicense = context.getStub().getPrivateData(
                STATE_LICENSE_COLLECTION,
                licenseNumber.toUpperCase()
        );

        return persistedLicense != null ? objectMapper.readValue(
                persistedLicense,
                PersistedLicenseSchema.class
        ) : null;
    }

    private List<Address> convertPersistedAddressesToAddresses(final List<PersistedAddress> persistedAddresses) {
        return persistedAddresses.stream().map(address -> new Address(
                address.getStreet1(),
                address.getStreet2(),
                address.getCity(),
                address.getState(),
                address.getZipCode()
        )).toList();
    }

    private void saveLicenseDataTo3rdPartyCollection(
            final Context context,
            final LicenseSchema license
    ) throws
      JsonProcessingException {
        context.getStub().putPrivateData(
                String.format(
                        "%s_LICENSE_COLLECTION",
                        context.getClientIdentity().getMSPID().toUpperCase()
                ),
                license.getLicenseNumber().toUpperCase(),
                objectMapper.writeValueAsBytes(license)
        );
    }
}
