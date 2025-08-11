package com.andrewhetzler.federal.fmvss.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.shim.ChaincodeException;

import java.util.List;
import java.util.Objects;

import static com.andrewhetzler.federal.fmvss.FmvssCertificationError.INVALID_REQUEST;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/11/25
 **/
@JsonPropertyOrder(alphabetic = true)
@DataType
public class FmvssCertification {
    private final AlteredVehicle alteredVehicle;
    private final ImportedVehicle importedVehicle;
    private final MotorVehicle motorVehicle;
    private final MultistageVehicle multistageVehicle;
    private final ReplicaVehicle replicaVehicle;
    private final int schemaVersion;

    public FmvssCertification(
            @JsonProperty("alteredVehicle") AlteredVehicle alteredVehicle,
            @JsonProperty("importedVehicle") ImportedVehicle importedVehicle,
            @JsonProperty("motorVehicle") MotorVehicle motorVehicle,
            @JsonProperty("multistageVehicle") MultistageVehicle multistageVehicle,
            @JsonProperty("replcaVehicle") ReplicaVehicle replicaVehicle,
            @JsonProperty("schemaVersion") int schemaVersion
    ) {
        this.alteredVehicle = alteredVehicle;
        this.importedVehicle = importedVehicle;
        this.motorVehicle = motorVehicle;
        this.multistageVehicle = multistageVehicle;
        this.replicaVehicle = replicaVehicle;
        this.schemaVersion = schemaVersion;
    }

    public AlteredVehicle getAlteredVehicle() {
        return alteredVehicle;
    }

    public ImportedVehicle getImportedVehicle() {
        return importedVehicle;
    }

    public MotorVehicle getMotorVehicle() {
        return motorVehicle;
    }

    public MultistageVehicle getMultistageVehicle() {
        return multistageVehicle;
    }

    public ReplicaVehicle getReplicaVehicle() {
        return replicaVehicle;
    }

    public int getSchemaVersion() {
        return schemaVersion;
    }

    public void validate(List<Validation> validations) throws
                                                       ChaincodeException {
        final boolean result = validations.parallelStream().allMatch(validation -> validation.validate(this));

        if (!result) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FmvssCertification that = (FmvssCertification) o;
        return schemaVersion == that.schemaVersion && Objects.equals(
                alteredVehicle,
                that.alteredVehicle
        ) && Objects.equals(
                importedVehicle,
                that.importedVehicle
        ) && Objects.equals(
                motorVehicle,
                that.motorVehicle
        ) && Objects.equals(
                multistageVehicle,
                that.multistageVehicle
        ) && Objects.equals(
                replicaVehicle,
                that.replicaVehicle
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                alteredVehicle,
                importedVehicle,
                motorVehicle,
                multistageVehicle,
                replicaVehicle,
                schemaVersion
        );
    }

    @Override
    public String toString() {
        return "FmvssCertification{" +
                "alteredVehicle=" + alteredVehicle +
                ", importedVehicle=" + importedVehicle +
                ", motorVehicle=" + motorVehicle +
                ", multistageVehicle=" + multistageVehicle +
                ", replicaVehicle=" + replicaVehicle +
                ", schemaVersion=" + schemaVersion +
                '}';
    }
}
