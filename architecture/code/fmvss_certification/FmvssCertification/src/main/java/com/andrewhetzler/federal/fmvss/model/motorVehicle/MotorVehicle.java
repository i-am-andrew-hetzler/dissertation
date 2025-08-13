package com.andrewhetzler.federal.fmvss.model.motorVehicle;

import com.andrewhetzler.federal.fmvss.model.GrossAxleWeightRating;
import com.andrewhetzler.federal.fmvss.model.GrossVehicleWeightRating;
import com.andrewhetzler.federal.fmvss.model.Manufactured;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/11/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class MotorVehicle {
    private final String conformityStatement;
    private final List<String> documentationTables;
    private final List<GrossAxleWeightRating> grossAxleWeightRatings;
    private final List<GrossVehicleWeightRating> grossVehicleWeightRatings;
    private final Manufactured manufactured;
    private final String manufacturerName;
    private final String registeredImporter;
    private final String type;
    private final String vehicleIdentificationNumber;

    public MotorVehicle(
            @JsonProperty("conformityStatement") String conformityStatement,
            @JsonProperty("documentationTables") List<String> documentationTables,
            @JsonProperty("grossAxleWeightRatings") List<GrossAxleWeightRating> grossAxleWeightRatings,
            @JsonProperty("grossVehicleWeightRatings") List<GrossVehicleWeightRating> grossVehicleWeightRatings,
            @JsonProperty("manufactured") Manufactured manufactured,
            @JsonProperty("manufacturerName") String manufacturerName,
            @JsonProperty("registeredImporter") String registeredImporter,
            @JsonProperty("type") String type,
            @JsonProperty("vehicleIdentificationNumber") String vehicleIdentificationNumber
    ) {
        this.conformityStatement = conformityStatement;
        this.documentationTables = documentationTables;
        this.grossAxleWeightRatings = grossAxleWeightRatings;
        this.grossVehicleWeightRatings = grossVehicleWeightRatings;
        this.manufactured = manufactured;
        this.manufacturerName = manufacturerName;
        this.registeredImporter = registeredImporter;
        this.type = type;
        this.vehicleIdentificationNumber = vehicleIdentificationNumber;
    }

    public String getConformityStatement() {
        return conformityStatement;
    }

    public List<String> getDocumentationTables() {
        return documentationTables;
    }

    public List<GrossAxleWeightRating> getGrossAxleWeightRatings() {
        return grossAxleWeightRatings;
    }

    public List<GrossVehicleWeightRating> getGrossVehicleWeightRatings() {
        return grossVehicleWeightRatings;
    }

    public Manufactured getManufactured() {
        return manufactured;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public String getRegisteredImporter() {
        return registeredImporter;
    }

    public String getType() {
        return type;
    }

    public String getVehicleIdentificationNumber() {
        return vehicleIdentificationNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MotorVehicle that = (MotorVehicle) o;
        return Objects.equals(
                conformityStatement,
                that.conformityStatement
        ) && Objects.equals(
                documentationTables,
                that.documentationTables
        ) && Objects.equals(
                grossAxleWeightRatings,
                that.grossAxleWeightRatings
        ) && Objects.equals(
                grossVehicleWeightRatings,
                that.grossVehicleWeightRatings
        ) && Objects.equals(
                manufactured,
                that.manufactured
        ) && Objects.equals(
                manufacturerName,
                that.manufacturerName
        ) && Objects.equals(
                registeredImporter,
                that.registeredImporter
        ) && Objects.equals(
                type,
                that.type
        ) && Objects.equals(
                vehicleIdentificationNumber,
                that.vehicleIdentificationNumber
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                conformityStatement,
                documentationTables,
                grossAxleWeightRatings,
                grossVehicleWeightRatings,
                manufactured,
                manufacturerName,
                registeredImporter,
                type,
                vehicleIdentificationNumber
        );
    }

    @Override
    public String toString() {
        return "MotorVehicle{" +
                "conformityStatement='" + conformityStatement + '\'' +
                ", documentationTables=" + documentationTables +
                ", grossAxleWeightRatings=" + grossAxleWeightRatings +
                ", grossVehicleWeightRatings=" + grossVehicleWeightRatings +
                ", manufactured=" + manufactured +
                ", manufacturerName='" + manufacturerName + '\'' +
                ", registeredImporter='" + registeredImporter + '\'' +
                ", type='" + type + '\'' +
                ", vehicleIdentificationNumber='" + vehicleIdentificationNumber + '\'' +
                '}';
    }
}
