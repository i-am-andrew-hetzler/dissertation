package com.andrewhetzler.state.licensing;

import com.andrewhetzler.state.licensing.model.Address;
import com.andrewhetzler.state.licensing.model.Birthdate;
import com.andrewhetzler.state.licensing.model.License;
import com.andrewhetzler.state.licensing.model.LicenseSchema;
import com.andrewhetzler.state.licensing.model.Licensee;
import com.andrewhetzler.state.licensing.model.persisted.PersistedAddress;
import com.andrewhetzler.state.licensing.model.persisted.PersistedBirthdate;
import com.andrewhetzler.state.licensing.model.persisted.PersistedLicense;
import com.andrewhetzler.state.licensing.model.persisted.PersistedLicenseSchema;
import com.andrewhetzler.state.licensing.model.persisted.PersistedLicensee;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.andrewhetzler.state.licensing.LicenseChaincodeError.DESERIALIZATION_ERROR;
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
public class LicensingChaincode implements ContractInterface {
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
    private static final String STATE_DMV_LICENSEE_MSP_ID = System.getenv().getOrDefault(
            "STATE_DMV_LICENSEE_MSP_ID",
            "TestStateDmvLicenseeMSP"
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

        if (isMspIdInStateAgencies(context.getClientIdentity()) || isMspIdTheStateDmv(context.getClientIdentity())) {
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
        else if (isMspIdALicensee(context.getClientIdentity())) {
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
                String.format(
                        "%s_LICENSE_COLLECTION",
                        context.getClientIdentity().getMSPID().toUpperCase()
                ),
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

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public LicenseSchema issueLicense(
            final Context context,
            final String serializedClasses,
            final String licenseNumber,
            final String serializedAddresses,
            final String birthDay,
            final String birthMonth,
            final String birthYear,
            final String serializedLicenseeDescription,
            final String isVeteran,
            final String name,
            final String serializedPhotograph,
            final String serializedSignature,
            final String serializedOther,
            final String schemaVersion,
            final String licenseeId

    ) throws
      JsonProcessingException {
        if (!isMspIdTheStateDmv(context.getClientIdentity())) {
            throw new ChaincodeException(
                    "Unauthorized request.",
                    UNAUTHORIZED_REQUEST.toString()
            );
        }

        if (
                isNullOrBlank(licenseNumber) || isNullOrBlank(serializedAddresses) || isNullOrBlank(name)
                        || isNullOrBlank(serializedPhotograph) || isNullOrBlank(schemaVersion)
                        || !isNumber(schemaVersion) || isNullOrBlank(licenseeId)
        ) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        final List<String> classes = deserializeList(
                serializedClasses,
                "classes",
                String.class
        );
        final List<PersistedAddress> addresses = deserializeList(
                serializedAddresses,
                "addresses",
                PersistedAddress.class
        );
        final Map<String, String> licenseeDescription;
        final Map<String, String> other;

        try {
            licenseeDescription = objectMapper.readValue(
                    serializedLicenseeDescription,
                    Map.class
            );
            other = objectMapper.readValue(
                    serializedOther,
                    Map.class
            );
        }
        catch (Exception e) {
            throw new ChaincodeException(
                    "Unable to deserialize licenseeDescription or other.",
                    DESERIALIZATION_ERROR.toString()
            );
        }

        final PersistedLicenseSchema persistedLicense = new PersistedLicenseSchema(
                new PersistedLicense(
                        classes,
                        licenseNumber
                ),
                new PersistedLicensee(
                        addresses,
                        new PersistedBirthdate(
                                birthDay,
                                birthMonth,
                                birthYear
                        ),
                        licenseeDescription,
                        isVeteran,
                        name,
                        serializedPhotograph,
                        serializedSignature,
                        licenseeId
                ),
                other,
                schemaVersion
        );

        context.getStub().putPrivateData(
                STATE_LICENSE_COLLECTION,
                licenseNumber.toUpperCase(),
                objectMapper.writeValueAsBytes(persistedLicense)
        );

        return new LicenseSchema(
                new License(
                        classes,
                        licenseNumber
                ),
                new Licensee(
                        convertPersistedAddressesToAddresses(addresses),
                        new Birthdate(
                                birthDay,
                                birthMonth,
                                birthYear
                        ),
                        licenseeDescription,
                        isVeteran,
                        name,
                        serializedPhotograph,
                        serializedSignature
                ),
                other,
                schemaVersion
        );
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void revokeLicense(
            final Context context,
            final String licenseNumber
    ) throws
      IOException {
        if (!isMspIdTheStateDmv(context.getClientIdentity())) {
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

        final PersistedLicenseSchema license = getLicense(
                context,
                licenseNumber
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

        context.getStub().delPrivateData(
                STATE_LICENSE_COLLECTION,
                licenseNumber.toUpperCase()
        );
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void cancelLicense(
            final Context context,
            final String licenseNumber
    ) throws
      IOException {
        if (!isMspIdALicensee(context.getClientIdentity())) {
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

        final PersistedLicenseSchema license = getLicense(
                context,
                licenseNumber
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

        if (!license.getLicenseeUniqueId().equals(context.getClientIdentity().getId())) {
            throw new ChaincodeException(
                    "Unauthorized request.",
                    UNAUTHORIZED_REQUEST.toString()
            );
        }

        context.getStub().delPrivateData(
                STATE_LICENSE_COLLECTION,
                licenseNumber.toUpperCase()
        );
    }

    private boolean isMspIdInStateAgencies(final ClientIdentity clientIdentity) {
        return STATE_AGENCIES_MSP_IDS.contains(clientIdentity.getMSPID());
    }

    private boolean isMspIdTheStateDmv(final ClientIdentity clientIdentity) {
        return STATE_DMV_MSP_ID.equalsIgnoreCase(clientIdentity.getMSPID());
    }

    private boolean isMspIdALicensee(final ClientIdentity clientIdentity) {
        return STATE_DMV_LICENSEE_MSP_ID.equalsIgnoreCase(clientIdentity.getMSPID());
    }

    private boolean isMspIdInThirdPartyMspIds(final ClientIdentity clientIdentity) {
        return THIRD_PARTY_MSP_IDS.contains(clientIdentity.getMSPID());
    }

    private boolean isAuthorized(final ClientIdentity clientIdentity) {
        return isMspIdInStateAgencies(clientIdentity) ||
                isMspIdTheStateDmv(clientIdentity) ||
                isMspIdALicensee(clientIdentity) ||
                isMspIdInThirdPartyMspIds(clientIdentity);
    }

    private boolean isNullOrBlank(final String value) {
        return value == null || value.isBlank();
    }

    private boolean isNumber(final String value) {
        try {
            Integer.parseInt(value);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
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

    private <T> List<T> deserializeList(
            final String serializedList,
            final String attribute,
            final Class<T> clazz
    ) {
        try {
            return serializedList != null && !serializedList.isBlank() ? objectMapper.readValue(
                    serializedList,
                    objectMapper.getTypeFactory().constructCollectionType(
                            List.class,
                            clazz
                    )
            ) : new ArrayList<>();
        }
        catch (JsonMappingException e) {
            throw new ChaincodeException(
                    String.format(
                            "Unable to map the %s.",
                            attribute
                    ),
                    DESERIALIZATION_ERROR.toString()
            );
        }
        catch (JsonProcessingException e) {
            throw new ChaincodeException(
                    String.format(
                            "Unable to deserialize %s.",
                            attribute
                    ),
                    DESERIALIZATION_ERROR.toString()
            );
        }
    }
}
