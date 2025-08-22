package com.andrewhetzler.state.licensing.model.persisted;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.hyperledger.fabric.contract.annotation.DataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/17/25
 **/
@JsonPropertyOrder(alphabetic = true)
@DataType
public class PersistedLicenseSchema {
    private final PersistedLicense license;
    private final PersistedLicensee licensee;
    private final Map<String, String> other;
    private final String schemaVersion;

    public PersistedLicenseSchema(
            @JsonProperty("license") PersistedLicense license,
            @JsonProperty("licensee") PersistedLicensee licensee,
            @JsonProperty("other") Map<String, String> other,
            @JsonProperty("schemaVersion") String schemaVersion
    ) {
        this.license = license;
        this.licensee = licensee;
        this.other = other != null ? other : new HashMap<>();
        this.schemaVersion = schemaVersion;
    }

    public PersistedLicense getLicense() {
        return license;
    }

    public PersistedLicensee getLicensee() {
        return licensee;
    }

    public Map<String, String> getOther() {
        return other;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    @JsonIgnore
    public List<String> getLicenseClasses() {
        return license.getClasses();
    }

    @JsonIgnore
    public String getLicenseNumber() {
        return license.getNumber();
    }

    @JsonIgnore
    public List<PersistedAddress> getLicenseeAddresses() {
        return licensee.getAddresses();
    }

    @JsonIgnore
    public PersistedBirthdate getLicenseeBirthdate() {
        return licensee.getBirthdate();
    }

    @JsonIgnore
    public Map<String, String> getLicenseeDescription() {
        return licensee.getDescription();
    }

    @JsonIgnore
    public String isLicenseeAVeteran() {
        return licensee.isVeteran();
    }

    @JsonIgnore
    public String getLicenseeName() {
        return licensee.getName();
    }

    @JsonIgnore
    public String getLicenseePhotograph() {
        return licensee.getPhotograph();
    }

    @JsonIgnore
    public String getLicenseeSignature() {
        return licensee.getSignature();
    }

    @JsonIgnore
    public String getLicenseeUniqueId() {
        return licensee.getUniqueId();
    }

    @Override
    public boolean equals(Object o) {

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PersistedLicenseSchema that = (PersistedLicenseSchema) o;
        return Objects.equals(
                license,
                that.license
        ) && Objects.equals(
                licensee,
                that.licensee
        ) && Objects.equals(
                other,
                that.other
        ) && Objects.equals(
                schemaVersion,
                that.schemaVersion
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                license,
                licensee,
                other,
                schemaVersion
        );
    }

    @Override
    public String toString() {
        return "PersistedLicenseSchema{" +
                "license=" + license +
                ", licensee=" + licensee +
                ", other=" + other +
                ", schemaVersion='" + schemaVersion + '\'' +
                '}';
    }
}
