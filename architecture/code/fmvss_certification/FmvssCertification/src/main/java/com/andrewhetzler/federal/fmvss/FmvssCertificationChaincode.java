package com.andrewhetzler.federal.fmvss;

import com.andrewhetzler.federal.fmvss.model.FmvssCertification;
import com.andrewhetzler.federal.fmvss.model.GrossAxleWeightRating;
import com.andrewhetzler.federal.fmvss.model.GrossVehicleWeightRating;
import com.andrewhetzler.federal.fmvss.model.Manufactured;
import com.andrewhetzler.federal.fmvss.model.alteredVehicle.AlteredVehicle;
import com.andrewhetzler.federal.fmvss.model.importedVehicle.ImportedVehicle;
import com.andrewhetzler.federal.fmvss.model.motorVehicle.MotorVehicle;
import com.andrewhetzler.federal.fmvss.model.multistageVehicle.FinalVehicle;
import com.andrewhetzler.federal.fmvss.model.multistageVehicle.IncompleteVehicle;
import com.andrewhetzler.federal.fmvss.model.multistageVehicle.IntermediateVehicle;
import com.andrewhetzler.federal.fmvss.model.multistageVehicle.MultistageVehicle;
import com.andrewhetzler.federal.fmvss.model.replicaVehicle.ReplicaVehicle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;

import static com.andrewhetzler.federal.fmvss.FmvssCertificationError.*;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/11/25
 **/
@Contract(
        name = "fmvssCertification",
        info = @Info(
                title = "FMVSS Certification",
                description = "The chaincode that powers the FMVSS certification use case.",
                version = "1.0.0",
                contact = @Contact(
                        email = "ahetzler@purdue.edu",
                        name = "Andrew Hetzler",
                        url = "www.andrewhetzler.com")))
@Default
public class FmvssCertificationChaincode implements ContractInterface {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String viewCertification(
            final Context context,
            final String vin
    ) throws
            IOException {
        final FmvssCertification certification = getCertification(
                context,
                vin
        );

        if (certification == null) {
            throw new ChaincodeException(
                    String.format(
                            "No certification found for vehicle %s.",
                            vin
                    ),
                    CERTIFICATION_DOES_NOT_EXIST.toString()
            );
        }

        return objectMapper.writeValueAsString(certification);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String certifyAlteredVehicle(
            final Context context,
            final String vin,
            final String conformityStatement,
            final String serializedGawr,
            final String serializedGvwr,
            final String type,
            final String schemaVersion
    ) throws
            IOException {
        if (!hasConformityStatement(conformityStatement)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (isNullOrBlank(schemaVersion) || !isNumber(schemaVersion)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        final List<GrossAxleWeightRating> grossAxleWeightRatings = deserializeList(
                serializedGawr,
                "gross axle weight ratings",
                GrossAxleWeightRating.class
        );
        final List<GrossVehicleWeightRating> grossVehicleWeightRatings = deserializeList(
                serializedGvwr,
                "gross vehicle weight ratings",
                GrossVehicleWeightRating.class
        );

        if (grossAxleWeightRatings != null && !grossAxleWeightRatings.isEmpty() && !isValidGrossAxleWeightRatings(grossAxleWeightRatings)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (grossVehicleWeightRatings != null && !grossVehicleWeightRatings.isEmpty() && !isValidGrossVehicleWeightRatings(grossVehicleWeightRatings)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (type != null && type.isBlank()) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        final FmvssCertification certification = getCertification(
                context,
                vin
        );
        if (certification == null || !hasAtleastOneCertificationExists(certification)) {
            throw new ChaincodeException(
                    String.format(
                            "No prior certification found for vehicle %s.",
                            vin
                    ),
                    CERTIFICATION_DOES_NOT_EXIST.toString()
            );
        }

        final FmvssCertification alteredCertification = new FmvssCertification(
                new AlteredVehicle(
                        conformityStatement,
                        grossAxleWeightRatings,
                        grossVehicleWeightRatings,
                        type
                ),
                certification.getImportedVehicle(),
                certification.getMotorVehicle(),
                certification.getMultistageVehicle(),
                certification.getReplicaVehicle(),
                schemaVersion
        );

        context.getStub().putState(
                vin.toUpperCase(),
                objectMapper.writeValueAsBytes(alteredCertification)
        );

        return objectMapper.writeValueAsString(alteredCertification);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String certifyImportedVehicle(
            final Context context,
            final String vin,
            final String conformityStatement,
            final String importerName,
            final String modelYear,
            final String vinCompliance,
            final String schemaVersion
    ) throws
            JsonProcessingException {
        if (isNullOrBlank(vin)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (!hasConformityStatement(conformityStatement)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (isNullOrBlank(schemaVersion) || !isNumber(schemaVersion)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (isNullOrBlank(importerName)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (isNullOrBlank(modelYear)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (isNullOrBlank(vinCompliance)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        final FmvssCertification importedCertification = new FmvssCertification(
                null,
                new ImportedVehicle(
                        conformityStatement,
                        importerName,
                        modelYear,
                        vinCompliance
                ),
                null,
                null,
                null,
                schemaVersion
        );

        context.getStub().putState(
                vin.toUpperCase(),
                objectMapper.writeValueAsBytes(importedCertification)
        );

        return objectMapper.writeValueAsString(importedCertification);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String certifyMotorVehicle(
            final Context context,
            final String vin,
            final String conformityStatement,
            final String serializedDocumentationTables,
            final String serializedGawr,
            final String serializedGvwr,
            final String manufacturedMonth,
            final String manufacturedYear,
            final String manufacturerName,
            final String registeredImporter,
            final String type,
            final String schemaVersion
    ) throws
            JsonProcessingException {
        final List<String> documentationTables = deserializeList(
                serializedDocumentationTables,
                "documentation tables",
                String.class
        );
        final List<GrossAxleWeightRating> grossAxleWeightRatings = deserializeList(
                serializedGawr,
                "gross axle weight ratings",
                GrossAxleWeightRating.class
        );
        final List<GrossVehicleWeightRating> grossVehicleWeightRatings = deserializeList(
                serializedGvwr,
                "gross vehicle weight ratings",
                GrossVehicleWeightRating.class
        );

        if (isNullOrBlank(vin)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (!hasConformityStatement(conformityStatement)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (isNullOrBlank(schemaVersion) || !isNumber(schemaVersion)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (isNullOrBlank(serializedGawr) || grossAxleWeightRatings.isEmpty() || !isValidGrossAxleWeightRatings(grossAxleWeightRatings)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (isNullOrBlank(serializedGvwr) || grossVehicleWeightRatings.isEmpty() || !isValidGrossVehicleWeightRatings(grossVehicleWeightRatings)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (!hasValidManufactured(
                manufacturedMonth,
                manufacturedYear
        )) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (isNullOrBlank(manufacturerName)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (isNullOrBlank(type)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        FmvssCertification motorVehicleCertification = new FmvssCertification(
                null,
                null,
                new MotorVehicle(
                        conformityStatement,
                        documentationTables,
                        grossAxleWeightRatings,
                        grossVehicleWeightRatings,
                        new Manufactured(
                                manufacturedMonth,
                                manufacturedYear
                        ),
                        manufacturerName,
                        registeredImporter,
                        type,
                        vin
                ),
                null,
                null,
                schemaVersion
        );

        context.getStub().putState(
                vin.toUpperCase(),
                objectMapper.writeValueAsBytes(motorVehicleCertification)
        );

        return objectMapper.writeValueAsString(motorVehicleCertification);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String certifyIncompleteVehicle(
            final Context context,
            final String vin,
            final String serializedGawr,
            final String serializedGvwr,
            final String manufacturedMonth,
            final String manufacturedYear,
            final String manufacturerName,
            final String schemaVersion
    ) throws
            IOException {
        if (isNullOrBlank(vin)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (isNullOrBlank(schemaVersion) || !isNumber(schemaVersion)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (isNullOrBlank(serializedGawr) || isNullOrBlank(serializedGvwr)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        final List<GrossAxleWeightRating> grossAxleWeightRatings = deserializeList(
                serializedGawr,
                "gross axle weight ratings",
                GrossAxleWeightRating.class
        );
        final List<GrossVehicleWeightRating> grossVehicleWeightRatings = deserializeList(
                serializedGvwr,
                "gross vehicle weight ratings",
                GrossVehicleWeightRating.class
        );

        if (grossAxleWeightRatings.isEmpty() || !isValidGrossAxleWeightRatings(grossAxleWeightRatings)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (grossVehicleWeightRatings.isEmpty() || !isValidGrossVehicleWeightRatings(grossVehicleWeightRatings)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (!hasValidManufactured(
                manufacturedMonth,
                manufacturedYear
        )) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (isNullOrBlank(manufacturerName)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        final FmvssCertification certification = getCertification(
                context,
                vin
        );

        if (certification != null && certification.getIncompleteVehicle() != null) {
            throw new ChaincodeException(
                    String.format(
                            "No prior certification found for vehicle %s.",
                            vin
                    ),
                    CERTIFICATION_ALREADY_EXISTS.toString()
            );
        }

        FmvssCertification incompleteVehicleCertification = new FmvssCertification(
                null,
                null,
                null,
                new MultistageVehicle(
                        null,
                        new IncompleteVehicle(
                                grossAxleWeightRatings,
                                grossVehicleWeightRatings,
                                new Manufactured(
                                        manufacturedMonth,
                                        manufacturedYear
                                ),
                                manufacturerName,
                                vin
                        ),
                        null
                ),
                null,
                schemaVersion
        );

        context.getStub().putState(
                vin.toUpperCase(),
                objectMapper.writeValueAsBytes(incompleteVehicleCertification)
        );

        return objectMapper.writeValueAsString(incompleteVehicleCertification);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String certifyIntermediateVehicle(
            final Context context,
            final String vin,
            final String serializedGawr,
            final String serializedGvwr,
            final String manufacturedMonth,
            final String manufacturedYear,
            final String manufacturerName,
            final String schemaVersion
    ) throws
            IOException {
        if (isNullOrBlank(vin)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (isNullOrBlank(schemaVersion) || !isNumber(schemaVersion)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (!hasValidManufactured(
                manufacturedMonth,
                manufacturedYear
        )) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (isNullOrBlank(manufacturerName)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        final List<GrossAxleWeightRating> grossAxleWeightRatings = deserializeList(
                serializedGawr,
                "gross axle weight ratings",
                GrossAxleWeightRating.class
        );
        final List<GrossVehicleWeightRating> grossVehicleWeightRatings = deserializeList(
                serializedGvwr,
                "gross vehicle weight ratings",
                GrossVehicleWeightRating.class
        );

        final FmvssCertification certification = getCertification(
                context,
                vin
        );

        if (certification == null || certification.getIncompleteVehicle() == null) {
            throw new ChaincodeException(
                    String.format(
                            "An incomplete vehicle certification does not exist for %s.",
                            vin
                    ),
                    INVALID_REQUEST.toString()
            );
        }

        final List<IntermediateVehicle> intermediateVehicleCertifications = new ArrayList<>(certification.getIntermediateVehicles());
        intermediateVehicleCertifications.add(
                new IntermediateVehicle(
                        grossAxleWeightRatings,
                        grossVehicleWeightRatings,
                        new Manufactured(
                                manufacturedMonth,
                                manufacturedYear
                        ),
                        manufacturerName,
                        vin
                )
        );

        FmvssCertification intermediateVehicleCertification = new FmvssCertification(
                null,
                null,
                null,
                new MultistageVehicle(
                        null,
                        certification.getIncompleteVehicle(),
                        intermediateVehicleCertifications
                ),
                null,
                schemaVersion
        );

        context.getStub().putState(
                vin.toUpperCase(),
                objectMapper.writeValueAsBytes(intermediateVehicleCertification)
        );

        return objectMapper.writeValueAsString(intermediateVehicleCertification);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String certifyFinalVehicle(
            final Context context,
            final String vin,
            final String conformityStatement,
            final String serializedGawr,
            final String serializedGvwr,
            final String manufacturedMonth,
            final String manufacturedYear,
            final String manufacturerName,
            final String type,
            final String schemaVersion
    ) throws
            IOException {
        if (isNullOrBlank(vin)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (!hasConformityStatement(conformityStatement)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (isNullOrBlank(schemaVersion) || !isNumber(schemaVersion)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (isNullOrBlank(serializedGawr) || isNullOrBlank(serializedGvwr)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        final List<GrossAxleWeightRating> grossAxleWeightRatings = deserializeList(
                serializedGawr,
                "gross axle weight ratings",
                GrossAxleWeightRating.class
        );
        final List<GrossVehicleWeightRating> grossVehicleWeightRatings = deserializeList(
                serializedGvwr,
                "gross vehicle weight ratings",
                GrossVehicleWeightRating.class
        );

        if (grossAxleWeightRatings.isEmpty() || !isValidGrossAxleWeightRatings(grossAxleWeightRatings)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (grossVehicleWeightRatings.isEmpty() || !isValidGrossVehicleWeightRatings(grossVehicleWeightRatings)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (!hasValidManufactured(
                manufacturedMonth,
                manufacturedYear
        )) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (isNullOrBlank(manufacturerName)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (isNullOrBlank(type)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        final FmvssCertification certification = getCertification(
                context,
                vin
        );

        if (certification == null || certification.getIncompleteVehicle() == null) {
            throw new ChaincodeException(
                    String.format(
                            "An incomplete vehicle certification does not exist for %s.",
                            vin
                    ),
                    INVALID_REQUEST.toString()
            );
        }

        final FmvssCertification finalVehicleCertification = new FmvssCertification(
                null,
                null,
                null,
                new MultistageVehicle(
                        new FinalVehicle(
                                conformityStatement,
                                grossAxleWeightRatings,
                                grossVehicleWeightRatings,
                                new Manufactured(
                                        manufacturedMonth,
                                        manufacturedYear
                                ),
                                manufacturerName,
                                type,
                                vin
                        ),
                        certification.getIncompleteVehicle(),
                        certification.getIntermediateVehicles()
                ),
                null,
                schemaVersion
        );

        context.getStub().putState(
                vin.toUpperCase(),
                objectMapper.writeValueAsBytes(finalVehicleCertification)
        );

        return objectMapper.writeValueAsString(finalVehicleCertification);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String certifyReplicaVehicle(
            final Context context,
            final String vin,
            final String exemptionStatement,
            final String serializedGawr,
            final String serializedGvwr,
            final String manufacturedMonth,
            final String manufacturedYear,
            final String manufacturerName,
            final String replicaStatement,
            final String schemaVersion
    ) throws
            JsonProcessingException {
        final List<GrossAxleWeightRating> grossAxleWeightRatings = deserializeList(
                serializedGawr,
                "gross axle weight ratings",
                GrossAxleWeightRating.class
        );
        final List<GrossVehicleWeightRating> grossVehicleWeightRatings = deserializeList(
                serializedGvwr,
                "gross vehicle weight ratings",
                GrossVehicleWeightRating.class
        );

        if (isNullOrBlank(vin)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (isNullOrBlank(exemptionStatement)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (isNullOrBlank(schemaVersion) || !isNumber(schemaVersion)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (isNullOrBlank(serializedGawr) || grossAxleWeightRatings.isEmpty() || !isValidGrossAxleWeightRatings(grossAxleWeightRatings)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (isNullOrBlank(serializedGvwr) || grossVehicleWeightRatings.isEmpty() || !isValidGrossVehicleWeightRatings(grossVehicleWeightRatings)) {

            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (!hasValidManufactured(
                manufacturedMonth,
                manufacturedYear
        )) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (isNullOrBlank(manufacturerName)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        if (isNullOrBlank(replicaStatement)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        FmvssCertification replicaCertification = new FmvssCertification(
                null,
                null,
                null,
                null,
                new ReplicaVehicle(
                        exemptionStatement,
                        grossAxleWeightRatings,
                        grossVehicleWeightRatings,
                        new Manufactured(
                                manufacturedMonth,
                                manufacturedYear
                        ),
                        manufacturerName,
                        replicaStatement,
                        vin
                ),
                schemaVersion
        );

        context.getStub().putState(
                vin.toUpperCase(),
                objectMapper.writeValueAsBytes(replicaCertification)
        );

        return objectMapper.writeValueAsString(replicaCertification);
    }

    private FmvssCertification getCertification(
            final Context context,
            String vin
    ) throws
            IOException {
        final byte[] certification = context.getStub().getState(vin.toUpperCase());

        if (certification != null && certification.length > 0) {
            return deserialize(certification);
        }

        return null;
    }

    private FmvssCertification deserialize(final byte[] certification) throws
            IOException {
        return objectMapper.readValue(
                certification,
                FmvssCertification.class
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
        } catch (JsonMappingException e) {
            throw new ChaincodeException(
                    String.format(
                            "Unable to map the %s.",
                            attribute
                    ),
                    DESERIALIZATION_ERROR.toString()
            );
        } catch (JsonProcessingException e) {
            throw new ChaincodeException(
                    String.format(
                            "Unable to deserialize %s.",
                            attribute
                    ),
                    DESERIALIZATION_ERROR.toString()
            );
        }
    }

    private boolean isAlteredVehicleNull(FmvssCertification certification) {
        return certification.getAlteredVehicle() == null;
    }

    private boolean isImportedVehicleNull(FmvssCertification certification) {
        return certification.getImportedVehicle() == null;
    }

    private boolean isMotorVehicleNull(FmvssCertification certification) {
        return certification.getMotorVehicle() == null;
    }

    private boolean isMultistageVehicleNull(FmvssCertification certification) {
        return certification.getMultistageVehicle() == null;
    }

    private boolean isReplicaVehicleNull(FmvssCertification certification) {
        return certification.getReplicaVehicle() == null;
    }

    private boolean hasAtleastOneCertificationExists(FmvssCertification certification) {
        return !isAlteredVehicleNull(certification) || !isImportedVehicleNull(certification) || !isMotorVehicleNull(certification) || !isMultistageVehicleNull(certification) || !isReplicaVehicleNull(certification);
    }

    private boolean hasConformityStatement(final String conformityStatement) {
        return conformityStatement != null && !conformityStatement.isBlank();
    }

    private boolean isValidGrossAxleWeightRatings(final List<GrossAxleWeightRating> grossAxleWeightRatings) {
        return grossAxleWeightRatings.stream().allMatch(this::isValidGrossAxleWeightRating);
    }

    private boolean isValidGrossVehicleWeightRatings(final List<GrossVehicleWeightRating> grossVehicleWeightRatings) {
        return grossVehicleWeightRatings.stream().allMatch(this::isValidGrossVehicleWeightRating);
    }

    private boolean isValidGrossAxleWeightRating(final GrossAxleWeightRating grossAxleWeightRating) {
        if (isNullOrBlank(grossAxleWeightRating.getFront())) {
            return false;
        }

        if (isNullOrBlank(grossAxleWeightRating.getOrder()) || !isNumber(grossAxleWeightRating.getOrder())) {
            return false;
        }

        if (isNullOrBlank(grossAxleWeightRating.getRear())) {
            return false;
        }

        if (!grossAxleWeightRating.getIntermediate().isEmpty()) {
            grossAxleWeightRating.getIntermediate().stream().allMatch(axle -> (!isNullOrBlank(axle.getOrder()) && isNumber(axle.getOrder())) && !isNullOrBlank(axle.getWeight()));
        }

        return true;
    }

    private boolean isValidGrossVehicleWeightRating(GrossVehicleWeightRating grossVehicleWeightRating) {
        if (isNullOrBlank(grossVehicleWeightRating.getValue())) {
            return false;
        }

        if (isNullOrBlank(grossVehicleWeightRating.getOrder()) || !isNumber(grossVehicleWeightRating.getOrder())) {
            return false;
        }

        return true;
    }

    private boolean isNullOrBlank(final String value) {
        return value == null || value.isBlank();
    }

    private boolean isNumber(final String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean hasValidManufactured(
            final String month,
            final String year
    ) {
        return !isNullOrBlank(month) && !isNullOrBlank(year) && isNumber(year);
    }
}
