package com.andrewhetzler.federal.firmware.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Map;
import java.util.Objects;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/10/25
 **/
@JsonPropertyOrder(alphabetic = true)
public class Firmware {
    private final String hash;
    private final String make;
    private final Map<String, String> metadata;
    private final String model;
    private final String url;
    private final String version;
    private final String year;

    public Firmware(
            @JsonProperty("hash") final String hash,
            @JsonProperty("make") final String make,
            @JsonProperty("metadata") final Map<String, String> metadata,
            @JsonProperty("model") final String model,
            @JsonProperty("url") final String url,
            @JsonProperty("version") final String version,
            @JsonProperty("year") final String year
    ) {
        this.hash = hash;
        this.make = make;
        this.metadata = metadata;
        this.model = model;
        this.url = url;
        this.version = version;
        this.year = year;
    }

    public String getHash() {
        return hash;
    }

    public String getMake() {
        return make;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public String getModel() {
        return model;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }

    public String getYear() {
        return year;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Firmware firmware = (Firmware) o;
        return Objects.equals(
                hash,
                firmware.hash
        ) && Objects.equals(
                make,
                firmware.make
        ) && Objects.equals(
                metadata,
                firmware.metadata
        ) && Objects.equals(
                model,
                firmware.model
        ) && Objects.equals(
                url,
                firmware.url
        ) && Objects.equals(
                version,
                firmware.version
        ) && Objects.equals(
                year,
                firmware.year
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                hash,
                make,
                metadata,
                model,
                url,
                version,
                year
        );
    }

    @Override
    public String toString() {
        return "Firmware{" +
                "hash='" + hash + '\'' +
                ", make='" + make + '\'' +
                ", metadata=" + metadata +
                ", model='" + model + '\'' +
                ", url='" + url + '\'' +
                ", version='" + version + '\'' +
                ", year=" + year +
                '}';
    }
}
