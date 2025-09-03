package com.andrewhetzler.federal.fmvss;

import com.andrewhetzler.federal.fmvss.model.FmvssCertification;
import com.andrewhetzler.federal.fmvss.model.GrossAxleWeightRating;
import com.andrewhetzler.federal.fmvss.model.GrossVehicleWeightRating;
import com.andrewhetzler.federal.fmvss.model.IntermediateAxleWeightRating;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/11/25
 **/
class FmvssCertificationChaincodeTest {
    @Mock
    private Context mockedContext;
    @Mock
    private ChaincodeStub mockedChaincodeStub;
    private FmvssCertificationChaincode subject;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private AlteredVehicle alteredVehicle;
    private ImportedVehicle importedVehicle;
    private MotorVehicle motorVehicle;
    private MultistageVehicle multistageVehicle;
    private ReplicaVehicle replicaVehicle;
    private List<GrossAxleWeightRating> grossAxleWeightRatings;
    private List<GrossVehicleWeightRating> grossVehicleWeightRatings;
    private List<IntermediateAxleWeightRating> intermediateAxleWeightRatings;
    private FmvssCertification expectedAlteredVehicle;

    @BeforeEach
    void setUp() {
        openMocks(this);

        subject = new FmvssCertificationChaincode();
        grossAxleWeightRatings = new ArrayList<>();
        grossVehicleWeightRatings = new ArrayList<>();
        intermediateAxleWeightRatings = new ArrayList<>();

        intermediateAxleWeightRatings.add(new IntermediateAxleWeightRating(
                "1",
                "WEIGHT HERE"
        ));
        grossAxleWeightRatings.add(
                new GrossAxleWeightRating(
                        "FRONT",
                        intermediateAxleWeightRatings,
                        "1",
                        "REAR"
                )
        );
        grossVehicleWeightRatings.add(new GrossVehicleWeightRating(
                "1",
                "ABC"
        ));

        alteredVehicle = new AlteredVehicle(
                "This conforms.",
                grossAxleWeightRatings,
                grossVehicleWeightRatings,
                "CAR"
        );
        importedVehicle = new ImportedVehicle(
                "This conforms.",
                "West Lafayette Co.",
                "2024",
                "The VIN is located on the dashboard."
        );
        motorVehicle = new MotorVehicle(
                "This conforms.",
                List.of("See documentation."),
                grossAxleWeightRatings,
                grossVehicleWeightRatings,
                new Manufactured(
                        "December",
                        "2020"
                ),
                "Purdue Motor Co.",
                "Importers United",
                "CAR",
                "1DH"
        );
        multistageVehicle = new MultistageVehicle(
                new FinalVehicle(
                        "This conforms.",
                        grossAxleWeightRatings,
                        grossVehicleWeightRatings,
                        new Manufactured(
                                "May",
                                "2020"
                        ),
                        "Purdue Motor Co.",
                        "CAR",
                        "1DF"
                ),
                new IncompleteVehicle(
                        grossAxleWeightRatings,
                        grossVehicleWeightRatings,
                        new Manufactured(
                                "May",
                                "2020"
                        ),
                        "Purdue Motor Co.",
                        "1DF"
                ),
                List.of(
                        new IntermediateVehicle(
                                grossAxleWeightRatings,
                                grossVehicleWeightRatings,
                                new Manufactured(
                                        "May",
                                        "2020"
                                ),
                                "Purdue Motor Co.",
                                "1DF"
                        )
                )
        );
        replicaVehicle = new ReplicaVehicle(
                "This is exemplt.",
                grossAxleWeightRatings,
                grossVehicleWeightRatings,
                new Manufactured(
                        "May",
                        "2020"
                ),
                "Purdue Motor Co.",
                "Replica statement.",
                "1DF"
        );

        expectedAlteredVehicle = new FmvssCertification(
                new AlteredVehicle(
                        "This conforms.",
                        grossAxleWeightRatings,
                        grossVehicleWeightRatings,
                        "CAR"
                ),
                new ImportedVehicle(
                        "This also conforms.",
                        "Purdue Motor Company",
                        "2025",
                        "test"
                ),
                null,
                null,
                null,
                "1"
        );
    }

    @Test
    void viewCertificationShouldThrowExceptionBecauseCertificationDoesNotExist() {
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState(anyString())).thenReturn(null);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewCertification(
                            mockedContext,
                            "test"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("No certification found for vehicle test."));
    }

    @Test
    void viewCertificationShouldReturnAlteredVehicleCertificationBecauseCertificationExists() throws
            IOException {
        final String certification = objectMapper.writeValueAsString(new FmvssCertification(
                alteredVehicle,
                null,
                null,
                null,
                null,
                "1"
        ));
        final byte[] expected = certification.getBytes();
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState(anyString())).thenReturn(expected);

        final String result = subject.viewCertification(
                mockedContext,
                "altered-car-1"
        );

        assertEquals(
                certification,
                result
        );
    }

    @Test
    void viewCertificationShouldReturnImportedVehicleCertificationBecauseCertificationExists() throws
            IOException {
        final String certification = objectMapper.writeValueAsString(new FmvssCertification(
                null,
                importedVehicle,
                null,
                null,
                null,
                "1"
        ));
        final byte[] expected = certification.getBytes();
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState(anyString())).thenReturn(expected);

        final String result = subject.viewCertification(
                mockedContext,
                "imported-car-1"
        );

        assertEquals(
                certification,
                result
        );
    }

    @Test
    void viewCertificationShouldReturnMotorVehicleCertificationBecauseCertificationExists() throws
            IOException {
        final String certification = objectMapper.writeValueAsString(new FmvssCertification(
                null,
                null,
                motorVehicle,
                null,
                null,
                "1"
        ));
        final byte[] expected = certification.getBytes();
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState(anyString())).thenReturn(expected);

        final String result = subject.viewCertification(
                mockedContext,
                "motor-vehicle-car-1"
        );

        assertEquals(
                certification,
                result
        );
    }

    @Test
    void viewCertificationShouldReturnMultistageVehicleCertificationBecauseCertificationExists() throws
            IOException {
        final FmvssCertification certification = new FmvssCertification(
                null,
                null,
                null,
                multistageVehicle,
                null,
                "1"
        );
        final byte[] expected = objectMapper.writeValueAsBytes(certification);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState(anyString())).thenReturn(expected);

        final String result = subject.viewCertification(
                mockedContext,
                "motor-vehicle-car-1"
        );

        assertEquals(
                objectMapper.writeValueAsString(certification),
                result
        );
    }

    @Test
    void viewCertificationShouldReturnReplicaVehicleCertificationBecauseCertificationExists() throws
            IOException {
        final String certification = objectMapper.writeValueAsString(new FmvssCertification(
                null,
                null,
                null,
                null,
                replicaVehicle,
                "1"
        ));
        final byte[] expected = certification.getBytes();
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState(anyString())).thenReturn(expected);

        final String result = subject.viewCertification(
                mockedContext,
                "motor-vehicle-car-1"
        );

        assertEquals(
                certification,
                result
        );
    }

    @Test
    void certifyAlteredVehicleShouldThrowExceptionDueToBadSerializedGawr() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyAlteredVehicle(
                            mockedContext,
                            "1F",
                            "Conforms.",
                            "asdasdasds",
                            "",
                            "CAR",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Unable to deserialize gross axle weight ratings."));
    }

    @Test
    void certifyAlteredVehicleShouldThrowExceptionDueToBadSerializedGvwr() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyAlteredVehicle(
                            mockedContext,
                            "1F",
                            "Conforms",
                            null,
                            "asdsad",
                            "",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Unable to deserialize gross vehicle weight ratings."));
    }

    @Test
    void certifyAlteredVehicleShouldThrowExceptionBecauseNoCertificationFound() {
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState(anyString())).thenReturn(null);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyAlteredVehicle(
                            mockedContext,
                            "test",
                            "Conforms.",
                            null,
                            null,
                            null,
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("No prior certification found for vehicle test."));
    }

    @Test
    void certifyAlteredVehicleShouldThrowExceptionBecauseNoCertificationExists() throws
            JsonProcessingException {
        final byte[] expected = objectMapper.writeValueAsBytes(new FmvssCertification(
                null,
                null,
                null,
                null,
                null,
                "1"
        ));

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState(anyString())).thenReturn(expected);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyAlteredVehicle(
                            mockedContext,
                            "test",
                            "Conforms",
                            null,
                            null,
                            null,
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("No prior certification found for vehicle test."));
    }

    @Test
    void certifyAlteredVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveConformityStatement() throws
            JsonProcessingException {
        final byte[] expected = objectMapper.writeValueAsBytes(new FmvssCertification(
                new AlteredVehicle(
                        null,
                        grossAxleWeightRatings,
                        grossVehicleWeightRatings,
                        "CAR"
                ),
                null,
                null,
                null,
                null,
                "1"
        ));

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState(anyString())).thenReturn(expected);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyAlteredVehicle(
                            mockedContext,
                            "test",
                            null,
                            null,
                            null,
                            null,
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyAlteredVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveValidGAWR() throws
            JsonProcessingException {
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState(anyString())).thenReturn(objectMapper.writeValueAsBytes(expectedAlteredVehicle));

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyAlteredVehicle(
                            mockedContext,
                            "test",
                            "This conforms.",
                            objectMapper.writeValueAsString(List.of(new GrossAxleWeightRating(
                                    null,
                                    null,
                                    null,
                                    null
                            ))),
                            null,
                            null,
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyAlteredVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveValidGVWR() throws
            JsonProcessingException {

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState(anyString())).thenReturn(objectMapper.writeValueAsBytes(expectedAlteredVehicle));

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyAlteredVehicle(
                            mockedContext,
                            "test",
                            "This conforms.",
                            null,
                            objectMapper.writeValueAsString(List.of(new GrossVehicleWeightRating(
                                    null,
                                    null
                            ))),
                            null,
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyAlteredVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveSchemaVersion() throws
            JsonProcessingException {

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState("TEST")).thenReturn(objectMapper.writeValueAsBytes(expectedAlteredVehicle));

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyAlteredVehicle(
                            mockedContext,
                            "TEST",
                            "This conforms.",
                            null,
                            objectMapper.writeValueAsString(List.of(new GrossVehicleWeightRating(
                                    null,
                                    null
                            ))),
                            null,
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyAlteredVehicleShouldSave() throws
            IOException {
        final String expected = objectMapper.writeValueAsString(new FmvssCertification(
                new AlteredVehicle(
                        "This conforms.",
                        grossAxleWeightRatings,
                        grossVehicleWeightRatings,
                        "CAR"
                ),
                new ImportedVehicle(
                        "This also conforms.",
                        "Purdue Motor Company",
                        "2025",
                        "test"
                ),
                null,
                null,
                null,
                "1"
        ));

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState("TEST")).thenReturn(objectMapper.writeValueAsBytes(expectedAlteredVehicle));

        final String result = subject.certifyAlteredVehicle(
                mockedContext,
                "test",
                "This conforms.",
                objectMapper.writeValueAsString(grossAxleWeightRatings),
                objectMapper.writeValueAsString(grossVehicleWeightRatings),
                "CAR",
                "1"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getState("TEST");
        verify(
                mockedChaincodeStub,
                times(1)
        ).putState(
                "TEST",
                expected.getBytes()
        );

        assertEquals(
                expected,
                result
        );
    }

    @Test
    void certifyImportedVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveVin() {

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyImportedVehicle(
                            mockedContext,
                            null,
                            "This conforms.",
                            "Indiana Importer",
                            "2025",
                            "1D",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyImportedVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveConformityStatement() {

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyImportedVehicle(
                            mockedContext,
                            "TEST",
                            null,
                            "Indiana Importer",
                            "2025",
                            "1D",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyImportedVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveSchemaVersion() {

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyImportedVehicle(
                            mockedContext,
                            "TEST",
                            null,
                            "Indiana Importer",
                            "2025",
                            "1D",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyImportedVehicleShouldThrowExceptionBecauseTheSchemaVersionIsNotANumber() {

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyImportedVehicle(
                            mockedContext,
                            "TEST",
                            "Conforms",
                            "Indiana Importer",
                            "2025",
                            "1D",
                            "a"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyImportedVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveImporterName() {

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyImportedVehicle(
                            mockedContext,
                            "TEST",
                            "This conforms.",
                            null,
                            "2025",
                            "1D",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyImportedVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveModelYear() {

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyImportedVehicle(
                            mockedContext,
                            "TEST",
                            "This conforms.",
                            "Indiana Importer",
                            null,
                            "1D",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyImportedVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveVinCompliance() {

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyImportedVehicle(
                            mockedContext,
                            "TEST",
                            "This conforms.",
                            "Indiana Importer",
                            "2025",
                            null,
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyImportedVehicleShouldSave() throws
            IOException {
        final String expected = objectMapper.writeValueAsString(new FmvssCertification(
                null,
                new ImportedVehicle(
                        "This conforms.",
                        "Indiana Importer",
                        "2025",
                        "test"
                ),
                null,
                null,
                null,
                "1"
        ));

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        final String result = subject.certifyImportedVehicle(
                mockedContext,
                "test",
                "This conforms.",
                "Indiana Importer",
                "2025",
                "test",
                "1"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).putState(
                "TEST",
                expected.getBytes()
        );

        assertEquals(
                expected,
                result
        );
    }

    @Test
    void certifyMotorVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveVin() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyMotorVehicle(
                            mockedContext,
                            null,
                            "This conforms.",
                            null,
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            null,
                            "CAR",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyMotorVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveConformityStatement() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyMotorVehicle(
                            mockedContext,
                            "test",
                            null,
                            null,
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            null,
                            "CAR",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyMotorVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveSchemaVersion() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyMotorVehicle(
                            mockedContext,
                            "test",
                            "This conforms.",
                            null,
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            null,
                            "CAR",
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyMotorVehicleShouldThrowExceptionBecauseTheSchemaVersionIsNotANumber() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyMotorVehicle(
                            mockedContext,
                            "test",
                            "This conforms.",
                            null,
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            null,
                            "CAR",
                            "a"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyMotorVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveManufacturedMonth() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyMotorVehicle(
                            mockedContext,
                            "test",
                            "This conforms.",
                            null,
                            serializedGawr,
                            serializedGvwr,
                            null,
                            "2025",
                            "Purdue Motor Company",
                            null,
                            "CAR",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyMotorVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveManufacturedYear() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyMotorVehicle(
                            mockedContext,
                            "test",
                            "This conforms.",
                            null,
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            null,
                            "Purdue Motor Company",
                            null,
                            "CAR",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyMotorVehicleShouldThrowExceptionBecauseTheManufacturedYearIsNotANumber() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyMotorVehicle(
                            mockedContext,
                            "test",
                            "This conforms.",
                            null,
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "a",
                            "Purdue Motor Company",
                            null,
                            "CAR",
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyMotorVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveSerializedGawr() throws
            JsonProcessingException {

        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyMotorVehicle(
                            mockedContext,
                            "test",
                            "This conforms.",
                            null,
                            null,
                            serializedGvwr,
                            "December",
                            null,
                            "Purdue Motor Company",
                            null,
                            "CAR",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyMotorVehicleShouldThrowExceptionBecauseTheSerializedGawrIsNotValid() throws
            JsonProcessingException {

        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyMotorVehicle(
                            mockedContext,
                            "test",
                            "This conforms.",
                            null,
                            objectMapper.writeValueAsString(
                                    List.of(new GrossAxleWeightRating(
                                            null,
                                            null,
                                            null,
                                            null
                                    ))
                            ),
                            serializedGvwr,
                            "December",
                            "a",
                            "Purdue Motor Company",
                            null,
                            "CAR",
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyMotorVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveSerializedGvwr() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyMotorVehicle(
                            mockedContext,
                            "test",
                            "This conforms.",
                            null,
                            serializedGawr,
                            null,
                            "December",
                            null,
                            "Purdue Motor Company",
                            null,
                            "CAR",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyMotorVehicleShouldThrowExceptionBecauseTheSerializedGvwrIsInvalid() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyMotorVehicle(
                            mockedContext,
                            "test",
                            "This conforms.",
                            null,
                            serializedGawr,
                            objectMapper.writeValueAsString(
                                    List.of(
                                            new GrossVehicleWeightRating(
                                                    null,
                                                    null
                                            )
                                    )
                            ),
                            "December",
                            null,
                            "Purdue Motor Company",
                            null,
                            "CAR",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyMotorVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveManufacturerName() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyMotorVehicle(
                            mockedContext,
                            "test",
                            "This conforms.",
                            null,
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "2025",
                            null,
                            null,
                            "CAR",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyMotorVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveType() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyMotorVehicle(
                            mockedContext,
                            "test",
                            "This conforms.",
                            null,
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            null,
                            null,
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyMotorVehicleShouldSave() throws
            IOException {
        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);
        final String expected = objectMapper.writeValueAsString(new FmvssCertification(
                null,
                null,
                new MotorVehicle(
                        "This conforms.",
                        List.of("See attachment A."),
                        grossAxleWeightRatings,
                        grossVehicleWeightRatings,
                        new Manufactured(
                                "December",
                                "2025"
                        ),
                        "Purdue Motor Company",
                        "Indiana Importer",
                        "CAR",
                        "test"
                ),
                null,
                null,
                "1"
        ));

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        final String result = subject.certifyMotorVehicle(
                mockedContext,
                "test",
                "This conforms.",
                objectMapper.writeValueAsString(List.of("See attachment A.")),
                serializedGawr,
                serializedGvwr,
                "December",
                "2025",
                "Purdue Motor Company",
                "Indiana Importer",
                "CAR",
                "1"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).putState(
                "TEST",
                expected.getBytes()
        );

        assertEquals(
                expected,
                result
        );
    }

    @Test
    void certifyIncompleteVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveVin() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyIncompleteVehicle(
                            mockedContext,
                            null,
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyIncompleteVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveSchemaVersion() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyIncompleteVehicle(
                            mockedContext,
                            "test",
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyIncompleteVehicleShouldThrowExceptionBecauseTheSchemaVersionIsNotANumber() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyIncompleteVehicle(
                            mockedContext,
                            "test",
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            "a"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyIncompleteVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveGawr() throws
            JsonProcessingException {

        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyIncompleteVehicle(
                            mockedContext,
                            "test",
                            null,
                            serializedGvwr,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyIncompleteVehicleShouldThrowExceptionBecauseTheGawrIsInvalid() throws
            JsonProcessingException {

        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyIncompleteVehicle(
                            mockedContext,
                            "test",
                            objectMapper.writeValueAsString(
                                    List.of(
                                            new GrossAxleWeightRating(
                                                    null,
                                                    null,
                                                    null,
                                                    null
                                            )
                                    )
                            ),
                            serializedGvwr,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyIncompleteVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveGvwr() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyIncompleteVehicle(
                            mockedContext,
                            "test",
                            serializedGawr,
                            null,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyIncompleteVehicleShouldThrowExceptionBecauseTheGvwrIsInvalid() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyIncompleteVehicle(
                            mockedContext,
                            "test",
                            serializedGawr,
                            objectMapper.writeValueAsString(
                                    List.of(
                                            new GrossVehicleWeightRating(
                                                    null,
                                                    null
                                            )
                                    )
                            ),
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyIncompleteVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveManufacturedMonth() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyIncompleteVehicle(
                            mockedContext,
                            "test",
                            serializedGawr,
                            serializedGvwr,
                            null,
                            "2025",
                            "Purdue Motor Company",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyIncompleteVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveManufacturedYear() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyIncompleteVehicle(
                            mockedContext,
                            "test",
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            null,
                            "Purdue Motor Company",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyIncompleteVehicleShouldThrowExceptionBecauseTheManufacturedYearIsNotANumber() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyIncompleteVehicle(
                            mockedContext,
                            "test",
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "a",
                            "Purdue Motor Company",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyIncompleteVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveManufacturerName() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyIncompleteVehicle(
                            mockedContext,
                            "test",
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "2025",
                            null,
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyIncompleteVehicleShouldSave() throws
            IOException {
        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);
        final String expected = objectMapper.writeValueAsString(new FmvssCertification(
                null,
                null,
                null,
                new MultistageVehicle(
                        null,
                        new IncompleteVehicle(
                                grossAxleWeightRatings,
                                grossVehicleWeightRatings,
                                new Manufactured(
                                        "December",
                                        "2025"
                                ),
                                "Purdue Motor Company",
                                "test"
                        ),
                        null
                ),
                null,
                "1"
        ));

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState("TEST")).thenReturn(null);

        final String result = subject.certifyIncompleteVehicle(
                mockedContext,
                "test",
                serializedGawr,
                serializedGvwr,
                "December",
                "2025",
                "Purdue Motor Company",
                "1"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getState("TEST");
        verify(
                mockedChaincodeStub,
                times(1)
        ).putState(
                "TEST",
                expected.getBytes()
        );

        assertEquals(
                expected,
                result
        );
    }

    @Test
    void certifyIntermediateVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveVin() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyIntermediateVehicle(
                            mockedContext,
                            null,
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyIntermediateVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveSchemaVersion() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyIntermediateVehicle(
                            mockedContext,
                            "test",
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyIntermediateVehicleShouldThrowExceptionBecauseTheSchemaVersionIsNotANumber() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyIntermediateVehicle(
                            mockedContext,
                            "test",
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            "a"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyIntermediateVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveManufacturedMonth() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyIntermediateVehicle(
                            mockedContext,
                            "test",
                            serializedGawr,
                            serializedGvwr,
                            null,
                            "2025",
                            "Purdue Motor Company",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyIntermediateVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveManufacturedYear() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyIntermediateVehicle(
                            mockedContext,
                            "test",
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            null,
                            "Purdue Motor Company",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyIntermediateVehicleShouldThrowExceptionBecauseTheManufacturedYearIsNotANumber() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyIntermediateVehicle(
                            mockedContext,
                            "test",
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "a",
                            "Purdue Motor Company",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyIntermediaVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveManufacturerName() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyIntermediateVehicle(
                            mockedContext,
                            "test",
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "2025",
                            null,
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyIntermediaVehicleShouldThrowExceptionBecauseNoIncompleteVehicleCertificationExists() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState("TEST")).thenReturn(null);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyIntermediateVehicle(
                            mockedContext,
                            "test",
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("An incomplete vehicle certification does not exist for test."));
    }

    @Test
    void certifyIntermediateVehicleShouldSave() throws
            IOException {
        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);
        final IncompleteVehicle incompleteVehicle = new IncompleteVehicle(
                grossAxleWeightRatings,
                grossVehicleWeightRatings,
                new Manufactured(
                        "May",
                        "2025"
                ),
                "Purdue Motor Company",
                "test"
        );
        final String expected = objectMapper.writeValueAsString(new FmvssCertification(
                null,
                null,
                null,
                new MultistageVehicle(
                        null,
                        incompleteVehicle,
                        List.of(
                                new IntermediateVehicle(
                                        grossAxleWeightRatings,
                                        grossVehicleWeightRatings,
                                        new Manufactured(
                                                "October",
                                                "2025"
                                        ),
                                        "Boilermaker Cars",
                                        "test"
                                )
                        )
                ),
                null,
                "1"
        ));

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState("TEST")).thenReturn(
                objectMapper.writeValueAsBytes(new FmvssCertification(
                                null,
                                null,
                                null,
                                new MultistageVehicle(
                                        null,
                                        incompleteVehicle,
                                        null
                                ),
                                null,
                                "1"
                        )
                )
        );

        final String result = subject.certifyIntermediateVehicle(
                mockedContext,
                "test",
                serializedGawr,
                serializedGvwr,
                "October",
                "2025",
                "Boilermaker Cars",
                "1"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getState("TEST");
        verify(
                mockedChaincodeStub,
                times(1)
        ).putState(
                "TEST",
                expected.getBytes()
        );

        assertEquals(
                expected,
                result
        );
    }

    @Test
    void certifyIntermediateVehicleShouldSaveAndPreserveIntermediateCertificationsOrder() throws
            IOException {
        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);
        final IncompleteVehicle incompleteVehicle = new IncompleteVehicle(
                grossAxleWeightRatings,
                grossVehicleWeightRatings,
                new Manufactured(
                        "May",
                        "2025"
                ),
                "Purdue Motor Company",
                "test"
        );
        final IntermediateVehicle existingCertification = new IntermediateVehicle(
                grossAxleWeightRatings,
                grossVehicleWeightRatings,
                new Manufactured(
                        "October",
                        "2025"
                ),
                "Boilermaker Cars",
                "test"
        );
        final IntermediateVehicle newCertification = new IntermediateVehicle(
                grossAxleWeightRatings,
                grossVehicleWeightRatings,
                new Manufactured(
                        "November",
                        "2025"
                ),
                "Andrew's Vehicles",
                "test"
        );
        final FmvssCertification expected = new FmvssCertification(
                null,
                null,
                null,
                new MultistageVehicle(
                        null,
                        incompleteVehicle,
                        List.of(
                                existingCertification,
                                newCertification
                        )
                ),
                null,
                "1"
        );

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState("TEST")).thenReturn(
                objectMapper.writeValueAsBytes(new FmvssCertification(
                                null,
                                null,
                                null,
                                new MultistageVehicle(
                                        null,
                                        incompleteVehicle,
                                        List.of(existingCertification)
                                ),
                                null,
                                "1"
                        )
                )
        );

        final String result = subject.certifyIntermediateVehicle(
                mockedContext,
                "test",
                serializedGawr,
                serializedGvwr,
                "November",
                "2025",
                "Andrew's Vehicles",
                "1"
        );
        final FmvssCertification actual = objectMapper.readValue(result, FmvssCertification.class);

        verify(
                mockedChaincodeStub,
                times(1)
        ).getState("TEST");
        verify(
                mockedChaincodeStub,
                times(1)
        ).putState(
                "TEST",
                objectMapper.writeValueAsBytes(expected)
        );

        assertEquals(
                expected,
                actual
        );
        assertEquals(
                existingCertification,
                actual.getIntermediateVehicles().get(0)
        );
        assertEquals(
                newCertification,
                actual.getIntermediateVehicles().get(1)
        );
    }

    @Test
    void certifyFinalVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveVin() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyFinalVehicle(
                            mockedContext,
                            null,
                            "This conforms.",
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            "CAR",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyFinalVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveConformityStatement() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyFinalVehicle(
                            mockedContext,
                            "tst",
                            null,
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            "CAR",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyFinalVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveSchemaVersion() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyFinalVehicle(
                            mockedContext,
                            "test",
                            "This conforms.",
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            "CAR",
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyFinalVehicleShouldThrowExceptionBecauseTheSchemaVersionIsNotANumber() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyFinalVehicle(
                            mockedContext,
                            "test",
                            "This conforms.",
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            "CAR",
                            "a"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyFinalVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveGawr() throws
            JsonProcessingException {

        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyFinalVehicle(
                            mockedContext,
                            "test",
                            "This conforms.",
                            null,
                            serializedGvwr,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            "CAR",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyFinalVehicleShouldThrowExceptionBecauseTheGawrIsInvalid() throws
            JsonProcessingException {

        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyFinalVehicle(
                            mockedContext,
                            "test",
                            "This conforms.",
                            objectMapper.writeValueAsString(
                                    List.of(
                                            new GrossAxleWeightRating(
                                                    null,
                                                    null,
                                                    null,
                                                    null
                                            )
                                    )
                            ),
                            serializedGvwr,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            "CAR",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyFinalVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveGvwr() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyFinalVehicle(
                            mockedContext,
                            "test",
                            "This conforms.",
                            serializedGawr,
                            null,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            "CAR",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyFinalVehicleShouldThrowExceptionBecauseTheGvwrIsInvalid() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyFinalVehicle(
                            mockedContext,
                            "test",
                            "This conforms.",
                            serializedGawr,
                            objectMapper.writeValueAsString(
                                    List.of(
                                            new GrossVehicleWeightRating(
                                                    null,
                                                    null
                                            )
                                    )
                            ),
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            "CAR",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyFinalVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveManufacturerMonth() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyFinalVehicle(
                            mockedContext,
                            "test",
                            "This conforms.",
                            serializedGawr,
                            serializedGvwr,
                            null,
                            "2025",
                            "Purdue Motor Company",
                            "CAR",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyFinalVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveManufacturerYear() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyFinalVehicle(
                            mockedContext,
                            "test",
                            "This conforms.",
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            null,
                            "Purdue Motor Company",
                            "CAR",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyFinalVehicleShouldThrowExceptionBecauseTheManufacturerYearIsNotANumber() throws
            JsonProcessingException {
        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyFinalVehicle(
                            mockedContext,
                            "test",
                            "This conforms.",
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "a",
                            "Purdue Motor Company",
                            "CAR",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyFinalVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveManufacturerName() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyFinalVehicle(
                            mockedContext,
                            "test",
                            "This conforms.",
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "2025",
                            null,
                            "CAR",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyFinalVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveType() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyFinalVehicle(
                            mockedContext,
                            "test",
                            "This conforms.",
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            null,
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyFinalVehicleShouldThrowExceptionBecauseNoIncompleteVehicleCertificationExists() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState("TEST")).thenReturn(null);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyFinalVehicle(
                            mockedContext,
                            "test",
                            "This conforms.",
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            "CAR",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("An incomplete vehicle certification does not exist for test."));
    }

    @Test
    void certifyFinalVehicleShouldSave() throws
            IOException {
        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);
        final IncompleteVehicle incompleteVehicle = new IncompleteVehicle(
                grossAxleWeightRatings,
                grossVehicleWeightRatings,
                new Manufactured(
                        "May",
                        "2025"
                ),
                "Purdue Motor Company",
                "test"
        );
        final IntermediateVehicle intermediateVehicle = new IntermediateVehicle(
                grossAxleWeightRatings,
                grossVehicleWeightRatings,
                new Manufactured(
                        "October",
                        "2025"
                ),
                "Boilermaker Cars",
                "test"
        );
        final FinalVehicle finalVehicle = new FinalVehicle(
                "This conforms.",
                grossAxleWeightRatings,
                grossVehicleWeightRatings,
                new Manufactured(
                        "December",
                        "2025"
                ),
                "Maya's Mods",
                "CAR",
                "test"
        );
        final String expected = objectMapper.writeValueAsString(new FmvssCertification(
                null,
                null,
                null,
                new MultistageVehicle(
                        finalVehicle,
                        incompleteVehicle,
                        List.of(intermediateVehicle)
                ),
                null,
                "1"
        ));

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState("TEST")).thenReturn(
                objectMapper.writeValueAsBytes(new FmvssCertification(
                                null,
                                null,
                                null,
                                new MultistageVehicle(
                                        null,
                                        incompleteVehicle,
                                        List.of(intermediateVehicle)
                                ),
                                null,
                                "1"
                        )
                )
        );

        final String result = subject.certifyFinalVehicle(
                mockedContext,
                "test",
                "This conforms.",
                serializedGawr,
                serializedGvwr,
                "December",
                "2025",
                "Maya's Mods",
                "CAR",
                "1"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getState("TEST");
        verify(
                mockedChaincodeStub,
                times(1)
        ).putState(
                "TEST",
                expected.getBytes()
        );

        assertEquals(
                expected,
                result
        );
    }

    @Test
    void certifyReplicaVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveExemptionStatement() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyReplicaVehicle(
                            mockedContext,
                            "test",
                            null,
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            "This is a replica.",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyReplicaVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveSchemaVersion() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyReplicaVehicle(
                            mockedContext,
                            "test",
                            "This is exempt.",
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            "This is a replica.",
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyReplicaVehicleShouldThrowExceptionBecauseTheSchemaVersionIsNotANumber() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyReplicaVehicle(
                            mockedContext,
                            "test",
                            "This is exempt.",
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            "This is a replica.",
                            "a"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyReplicaVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveVin() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyReplicaVehicle(
                            mockedContext,
                            null,
                            "This is exempt.",
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            "This is a replica.",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyReplicaVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveManufacturedMonth() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyReplicaVehicle(
                            mockedContext,
                            "test",
                            "This is exempt.",
                            serializedGawr,
                            serializedGvwr,
                            null,
                            "2025",
                            "Purdue Motor Company",
                            "This is a replica.",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyReplicaVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveManufacturedYear() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyReplicaVehicle(
                            mockedContext,
                            "test",
                            "This is exempt.",
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            null,
                            "Purdue Motor Company",
                            "This is a replica.",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyReplicaVehicleShouldThrowExceptionBecauseTheManufacturedYearIsNotANumber() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyReplicaVehicle(
                            mockedContext,
                            "test",
                            "This is exempt.",
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "a",
                            "Purdue Motor Company",
                            "This is a replica.",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyReplicaVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveGawr() throws
            JsonProcessingException {

        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyReplicaVehicle(
                            mockedContext,
                            "test",
                            "This is exempt.",
                            null,
                            serializedGvwr,
                            null,
                            "2025",
                            "Purdue Motor Company",
                            "This is a replica.",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyReplicaVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveValidGawr() throws
            JsonProcessingException {

        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyReplicaVehicle(
                            mockedContext,
                            "test",
                            "This is exempt.",
                            objectMapper.writeValueAsString(List.of(
                                    new GrossAxleWeightRating(
                                            null,
                                            null,
                                            null,
                                            null
                                    )
                            )),
                            serializedGvwr,
                            null,
                            "2025",
                            "Purdue Motor Company",
                            "This is a replica.",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyReplicaVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveGvwr() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyReplicaVehicle(
                            mockedContext,
                            "test",
                            "This is exempt.",
                            serializedGawr,
                            null,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            "This is a replica.",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyReplicaVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveValidGvwr() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyReplicaVehicle(
                            mockedContext,
                            "test",
                            "This is exempt.",
                            serializedGawr,
                            objectMapper.writeValueAsString(
                                    List.of(
                                            new GrossVehicleWeightRating(
                                                    null,
                                                    null
                                            )
                                    )
                            ),
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            "This is a replica.",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyReplicaVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveManufacturerName() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyReplicaVehicle(
                            mockedContext,
                            "test",
                            "This is exempt.",
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "2025",
                            null,
                            "This is a replica.",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyReplicaVehicleShouldThrowExceptionBecauseTheRequestDoesNotHaveReplicaStatement() throws
            JsonProcessingException {

        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.certifyReplicaVehicle(
                            mockedContext,
                            "test",
                            "This is exempt.",
                            serializedGawr,
                            serializedGvwr,
                            "December",
                            "2025",
                            "Purdue Motor Company",
                            null,
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void certifyReplicaVehicleShouldSave() throws
            IOException {
        final String serializedGawr = objectMapper.writeValueAsString(grossAxleWeightRatings);
        final String serializedGvwr = objectMapper.writeValueAsString(grossVehicleWeightRatings);
        final String expected = objectMapper.writeValueAsString(new FmvssCertification(
                null,
                null,
                null,
                null,
                new ReplicaVehicle(
                        "This is exempt.",
                        grossAxleWeightRatings,
                        grossVehicleWeightRatings,
                        new Manufactured(
                                "December",
                                "2025"
                        ),
                        "Purdue Motor Company",
                        "This is a replica.",
                        "test"
                ),
                "1"
        ));

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        final String result = subject.certifyReplicaVehicle(
                mockedContext,
                "test",
                "This is exempt.",
                serializedGawr,
                serializedGvwr,
                "December",
                "2025",
                "Purdue Motor Company",
                "This is a replica.",
                "1"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).putState(
                "TEST",
                expected.getBytes()
        );

        assertEquals(
                expected,
                result
        );
    }

    @Test
    void generate() throws JsonProcessingException {
        List<GrossAxleWeightRating> axle = List.of(
                new GrossAxleWeightRating(
                        "front weight",
                        List.of(),
                        "1",
                        "rear weight"
                )
        );

        String serializedGAWR = objectMapper.writeValueAsString(axle);

        System.out.println(serializedGAWR);
    }
}
