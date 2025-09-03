package com.andrewhetzler.federal.fmvss.model;

import com.andrewhetzler.federal.fmvss.model.alteredVehicle.AlteredVehicle;
import com.andrewhetzler.federal.fmvss.model.importedVehicle.ImportedVehicle;
import com.andrewhetzler.federal.fmvss.model.motorVehicle.MotorVehicle;
import com.andrewhetzler.federal.fmvss.model.multistageVehicle.FinalVehicle;
import com.andrewhetzler.federal.fmvss.model.multistageVehicle.IncompleteVehicle;
import com.andrewhetzler.federal.fmvss.model.multistageVehicle.IntermediateVehicle;
import com.andrewhetzler.federal.fmvss.model.multistageVehicle.MultistageVehicle;
import com.andrewhetzler.federal.fmvss.model.replicaVehicle.ReplicaVehicle;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.hyperledger.fabric.contract.annotation.DataType;

import java.util.List;
import java.util.Objects;

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
    private final String schemaVersion;

    public FmvssCertification(
            @JsonProperty("alteredVehicle") AlteredVehicle alteredVehicle,
            @JsonProperty("importedVehicle") ImportedVehicle importedVehicle,
            @JsonProperty("motorVehicle") MotorVehicle motorVehicle,
            @JsonProperty("multistageVehicle") MultistageVehicle multistageVehicle,
            @JsonProperty("replicaVehicle") ReplicaVehicle replicaVehicle,
            @JsonProperty("schemaVersion") String schemaVersion
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

    @JsonIgnore
    public IncompleteVehicle getIncompleteVehicle() {
        return multistageVehicle.getIncompleteVehicle();
    }

    @JsonIgnore
    public List<IntermediateVehicle> getIntermediateVehicles() {
        return multistageVehicle.getIntermediateVehicles();
    }

    @JsonIgnore
    public FinalVehicle getFinalVehicle() {
        return multistageVehicle.getFinalVehicle();
    }

    public ReplicaVehicle getReplicaVehicle() {
        return replicaVehicle;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FmvssCertification that = (FmvssCertification) o;
        return Objects.equals(
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
        ) && Objects.equals(
                schemaVersion,
                that.schemaVersion
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
