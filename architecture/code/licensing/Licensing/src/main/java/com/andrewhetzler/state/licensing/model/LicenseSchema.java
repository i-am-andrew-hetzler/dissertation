package com.andrewhetzler.state.licensing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Map;
import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/17/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class LicenseSchema {
    private final License license;
    private final Licensee licensee;
    private final Map<String, String> other;
    private final String schemaVersion;

    public LicenseSchema(
            @JsonProperty("license") License license,
            @JsonProperty("licensee") Licensee licensee,
            @JsonProperty("other") Map<String, String> other,
            @JsonProperty("schemaVersion") String schemaVersion
    ) {
        this.license = license;
        this.licensee = licensee;
        this.other = other;
        this.schemaVersion = schemaVersion;
    }

    public License getLicense() {
        return license;
    }

    public Licensee getLicensee() {
        return licensee;
    }

    public Map<String, String> getOther() {
        return other;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    @JsonIgnore
    public String getLicenseNumber() {
        return license.getNumber();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LicenseSchema that = (LicenseSchema) o;
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
        return "LicenseSchema{" +
                "license=" + license +
                ", licensee=" + licensee +
                ", other=" + other +
                ", schemaVersion='" + schemaVersion + '\'' +
                '}';
    }
}
