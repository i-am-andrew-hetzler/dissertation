package com.andrewhetzler.federal.fmvss.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/11/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class ImportedVehicle {
    private final String conformityStatement;
    private final String importerName;
    private final int modelYear;
    private final String vehicleIdentificationNumberCompliance;

    public ImportedVehicle(
            @JsonProperty("conformityStatement") String conformityStatement,
            @JsonProperty("importerName") String importerName,
            @JsonProperty("modelYear") int modelYear,
            @JsonProperty("vehicleIdentificationNumberCompliance") String vehicleIdentificationNumberCompliance
    ) {
        this.conformityStatement = conformityStatement;
        this.importerName = importerName;
        this.modelYear = modelYear;
        this.vehicleIdentificationNumberCompliance = vehicleIdentificationNumberCompliance;
    }

    public String getConformityStatement() {
        return conformityStatement;
    }

    public String getImporterName() {
        return importerName;
    }

    public int getModelYear() {
        return modelYear;
    }

    public String getVehicleIdentificationNumberCompliance() {
        return vehicleIdentificationNumberCompliance;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ImportedVehicle that = (ImportedVehicle) o;
        return modelYear == that.modelYear && Objects.equals(
                conformityStatement,
                that.conformityStatement
        ) && Objects.equals(
                importerName,
                that.importerName
        ) && Objects.equals(
                vehicleIdentificationNumberCompliance,
                that.vehicleIdentificationNumberCompliance
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                conformityStatement,
                importerName,
                modelYear,
                vehicleIdentificationNumberCompliance
        );
    }

    @Override
    public String toString() {
        return "ImportedVehicle{" +
                "conformityStatement='" + conformityStatement + '\'' +
                ", importerName='" + importerName + '\'' +
                ", modelYear=" + modelYear +
                ", vehicleIdentificationNumberCompliance='" + vehicleIdentificationNumberCompliance + '\'' +
                '}';
    }
}
