package com.andrewhetzler.state.registration.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.hyperledger.fabric.contract.annotation.DataType;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/18/25
 **/
@JsonPropertyOrder(alphabetic = true)
@DataType
public class RegistrationSchema {
    private final Map<String, String> other;
    private final List<Registrant> registrants;
    private final Registration registration;
    private final String schemaVersion;

    public RegistrationSchema(
            @JsonProperty("other") Map<String, String> other,
            @JsonProperty("registrants") List<Registrant> registrants,
            @JsonProperty("registration") Registration registration,
            @JsonProperty("schemaVersion") String schemaVersion
    ) {
        this.other = other;
        this.registrants = registrants;
        this.registration = registration;
        this.schemaVersion = schemaVersion;
    }

    public Map<String, String> getOther() {
        return other;
    }

    public List<Registrant> getRegistrants() {
        return registrants;
    }

    public Registration getRegistration() {
        return registration;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RegistrationSchema that = (RegistrationSchema) o;
        return Objects.equals(
                other,
                that.other
        ) && Objects.equals(
                registrants,
                that.registrants
        ) && Objects.equals(
                registration,
                that.registration
        ) && Objects.equals(
                schemaVersion,
                that.schemaVersion
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                other,
                registrants,
                registration,
                schemaVersion
        );
    }

    @Override
    public String toString() {
        return "RegistrationSchema{" +
                "other=" + other +
                ", registrants=" + registrants +
                ", registration=" + registration +
                ", schemaVersion='" + schemaVersion + '\'' +
                '}';
    }
}
