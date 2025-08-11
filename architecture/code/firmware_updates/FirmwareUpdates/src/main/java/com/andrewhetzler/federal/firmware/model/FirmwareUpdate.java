package com.andrewhetzler.federal.firmware.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.shim.ChaincodeException;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.andrewhetzler.federal.firmware.FirmwareUpdateError.INVALID_REQUEST;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/10/25
 **/
@JsonPropertyOrder(alphabetic = true)
@DataType
public class FirmwareUpdate {
    private final Firmware firmware;
    private final int schemaVersion;

    public FirmwareUpdate(
            @JsonProperty("firmware") Firmware firmware,
            @JsonProperty("schemaVersion") int schemaVersion
    ) {
        this.firmware = firmware;
        this.schemaVersion = schemaVersion;
    }

    public Firmware getFirmware() {
        return firmware;
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
        FirmwareUpdate that = (FirmwareUpdate) o;
        return schemaVersion == that.schemaVersion && Objects.equals(
                firmware,
                that.firmware
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                firmware,
                schemaVersion
        );
    }

    @Override
    public String toString() {
        return "FirmwareUpdate{" +
                "firmware=" + firmware +
                ", schemaVersion=" + schemaVersion +
                '}';
    }

    @JsonIgnore
    public String getHash() {
        return firmware.getHash();
    }

    @JsonIgnore
    public String getMake() {
        return firmware.getMake();
    }

    @JsonIgnore
    public Map<String, String> getMetadata() {
        return firmware.getMetadata();
    }

    @JsonIgnore
    public String getModel() {
        return firmware.getModel();
    }

    @JsonIgnore
    public String getUrl() {
        return firmware.getUrl();
    }

    @JsonIgnore
    public String getVersion() {
        return firmware.getVersion();
    }

    @JsonIgnore
    public int getYear() {
        return firmware.getYear();
    }
}
