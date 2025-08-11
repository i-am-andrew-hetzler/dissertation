package com.andrewhetzler.federal.fmvss;

import com.andrewhetzler.federal.fmvss.model.AlteredVehicle;
import com.andrewhetzler.federal.fmvss.model.FinalVehicle;
import com.andrewhetzler.federal.fmvss.model.FmvssCertification;
import com.andrewhetzler.federal.fmvss.model.GrossAxleWeightRating;
import com.andrewhetzler.federal.fmvss.model.GrossVehicleWeightRating;
import com.andrewhetzler.federal.fmvss.model.ImportedVehicle;
import com.andrewhetzler.federal.fmvss.model.IncompleteVehicle;
import com.andrewhetzler.federal.fmvss.model.IntermediateAxleWeightRating;
import com.andrewhetzler.federal.fmvss.model.IntermediateVehicle;
import com.andrewhetzler.federal.fmvss.model.Manufactured;
import com.andrewhetzler.federal.fmvss.model.MotorVehicle;
import com.andrewhetzler.federal.fmvss.model.MultistageVehicle;
import com.andrewhetzler.federal.fmvss.model.ReplicaVehicle;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
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
    ;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private AlteredVehicle alteredVehicle;
    private ImportedVehicle importedVehicle;
    private MotorVehicle motorVehicle;
    private MultistageVehicle multistageVehicle;
    private ReplicaVehicle replicaVehicle;

    @BeforeEach
    void setUp() {
        openMocks(this);

        subject = new FmvssCertificationChaincode();
        alteredVehicle = new AlteredVehicle(
                "This conforms.",
                List.of(new GrossAxleWeightRating(
                        "FRONT",
                        List.of(
                                new IntermediateAxleWeightRating(
                                        1,
                                        "WEIGHT HERE"
                                )
                        ),
                        1,
                        "REAR"
                )),
                List.of(new GrossVehicleWeightRating(
                        1,
                        "ABC"
                )),
                "CAR"
        );
        importedVehicle = new ImportedVehicle(
                "This conforms.",
                "West Lafayette Co.",
                2024,
                "The VIN is located on the dashboard."
        );
        motorVehicle = new MotorVehicle(
                "This conforms.",
                List.of("See documentation."),
                List.of(new GrossAxleWeightRating(
                        "FRONT",
                        List.of(
                                new IntermediateAxleWeightRating(
                                        1,
                                        "WEIGHT HERE"
                                )
                        ),
                        1,
                        "REAR"
                )),
                List.of(new GrossVehicleWeightRating(
                        1,
                        "ABC"
                )),
                new Manufactured(
                        "December",
                        2020
                ),
                "Purdue Motor Co.",
                "Importers United",
                "CAR",
                "1DH"
        );
        multistageVehicle = new MultistageVehicle(
                new FinalVehicle(
                        "This conforms.",
                        List.of(new GrossAxleWeightRating(
                                "FRONT",
                                List.of(
                                        new IntermediateAxleWeightRating(
                                                1,
                                                "WEIGHT HERE"
                                        )
                                ),
                                1,
                                "REAR"
                        )),
                        List.of(new GrossVehicleWeightRating(
                                1,
                                "ABC"
                        )),
                        new Manufactured(
                                "May",
                                2020
                        ),
                        "Purdue Motor Co.",
                        "CAR",
                        "1DF"
                ),
                new IncompleteVehicle(
                        List.of(new GrossAxleWeightRating(
                                "FRONT",
                                List.of(
                                        new IntermediateAxleWeightRating(
                                                1,
                                                "WEIGHT HERE"
                                        )
                                ),
                                1,
                                "REAR"
                        )),
                        List.of(new GrossVehicleWeightRating(
                                1,
                                "ABC"
                        )),
                        new Manufactured(
                                "May",
                                2020
                        ),
                        "Purdue Motor Co.",
                        "1DF"
                ),
                List.of(
                        new IntermediateVehicle(
                                List.of(new GrossAxleWeightRating(
                                        "FRONT",
                                        List.of(
                                                new IntermediateAxleWeightRating(
                                                        1,
                                                        "WEIGHT HERE"
                                                )
                                        ),
                                        1,
                                        "REAR"
                                )),
                                List.of(new GrossVehicleWeightRating(
                                        1,
                                        "ABC"
                                )),
                                new Manufactured(
                                        "May",
                                        2020
                                ),
                                "Purdue Motor Co.",
                                "1DF"
                        )
                )
        );
        replicaVehicle = new ReplicaVehicle(
                "This is exemplt.",
                List.of(new GrossAxleWeightRating(
                        "FRONT",
                        List.of(
                                new IntermediateAxleWeightRating(
                                        1,
                                        "WEIGHT HERE"
                                )
                        ),
                        1,
                        "REAR"
                )),
                List.of(new GrossVehicleWeightRating(
                        1,
                        "ABC"
                )),
                new Manufactured(
                        "May",
                        2020
                ),
                "Purdue Motor Co.",
                "Replica statement.",
                "1DF"
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
        final FmvssCertification certification = new FmvssCertification(
                alteredVehicle,
                null,
                null,
                null,
                null,
                1
        );
        final byte[] expected = objectMapper.writeValueAsBytes(certification);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState(anyString())).thenReturn(expected);

        final FmvssCertification result = subject.viewCertification(
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
        final FmvssCertification certification = new FmvssCertification(
                null,
                importedVehicle,
                null,
                null,
                null,
                1
        );
        final byte[] expected = objectMapper.writeValueAsBytes(certification);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState(anyString())).thenReturn(expected);

        final FmvssCertification result = subject.viewCertification(
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
        final FmvssCertification certification = new FmvssCertification(
                null,
                null,
                motorVehicle,
                null,
                null,
                1
        );
        final byte[] expected = objectMapper.writeValueAsBytes(certification);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState(anyString())).thenReturn(expected);

        final FmvssCertification result = subject.viewCertification(
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
                1
        );
        final byte[] expected = objectMapper.writeValueAsBytes(certification);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState(anyString())).thenReturn(expected);

        final FmvssCertification result = subject.viewCertification(
                mockedContext,
                "motor-vehicle-car-1"
        );

        assertEquals(
                certification,
                result
        );
    }

    @Test
    void viewCertificationShouldReturnReplicaVehicleCertificationBecauseCertificationExists() throws
                                                                                              IOException {
        final FmvssCertification certification = new FmvssCertification(
                null,
                null,
                null,
                null,
                replicaVehicle,
                1
        );
        final byte[] expected = objectMapper.writeValueAsBytes(certification);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState(anyString())).thenReturn(expected);

        final FmvssCertification result = subject.viewCertification(
                mockedContext,
                "motor-vehicle-car-1"
        );

        assertEquals(
                certification,
                result
        );
    }
}
