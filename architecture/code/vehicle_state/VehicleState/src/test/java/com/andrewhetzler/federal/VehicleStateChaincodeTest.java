package com.andrewhetzler.federal;

import com.andrewhetzler.federal.vehicle_state.Vehicle;
import com.andrewhetzler.federal.vehicle_state.VehicleState;
import com.andrewhetzler.federal.vehicle_state.VehicleStateChaincode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.contract.ClientIdentity;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.andrewhetzler.federal.vehicle_state.VehicleStateChaincode.VEHICLE_STATE_PROPERTIES;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/13/25
 **/
class VehicleStateChaincodeTest {
    @Mock
    private Context mockedContext;
    @Mock
    private ChaincodeStub mockedChaincodeStub;
    @Mock
    private ClientIdentity mockedClientIdentity;
    private VehicleStateChaincode subject;
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final VehicleState expectedVehicleState = new VehicleState(
            "1",
            new Vehicle(
                    "correct-hash",
                    "test"
            )
    );
    private static final String TEST_COLLECTION = "purdue-motor-company-vehicles";
    private static final String RECORD_INITIAL_STATE_MSPS = "PurdueFinalAssemblerMSP";
    private static final String UPDATE_STATE_MSP = "PurdueDealerTechnicianMSP";
    private static final String OVERRIDE_STATE_MSP = "PurdueDealerTechnicianMSP";

    @BeforeEach
    public void setup() {
        openMocks(this);

        subject = new VehicleStateChaincode();
    }

    @Test
    void isValidShouldThrowExceptionBecauseVinIsMissing() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.isValid(
                            mockedContext,
                            null,
                            "hash-here"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void isValidShouldThrowExceptionBecauseVinIsBlank() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.isValid(
                            mockedContext,
                            "",
                            "hash-here"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void isValidShouldThrowExceptionBecauseHashIsMissing() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.isValid(
                            mockedContext,
                            "test",
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void isValidShouldThrowExceptionBecauseHashIsBlank() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.isValid(
                            mockedContext,
                            "test",
                            ""
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void isValidShouldThrowExceptionBecauseStateDoesNotExist() {
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState("TEST")).thenReturn(null);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.isValid(
                            mockedContext,
                            "test",
                            "hash-here"
                    );
                }
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getPrivateData(
                TEST_COLLECTION,
                "TEST"
        );
        assertTrue(exception.getMessage().contains("No state found for vehicle test."));
    }

    @Test
    void isValidShouldReturnFalseBecauseSubmittedHashDoesNotMatchActualHash() throws
                                                                              IOException {
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                TEST_COLLECTION,
                "TEST"
        )).thenReturn(
                objectMapper.writeValueAsBytes(new VehicleState(
                                                       "1",
                                                       new Vehicle(
                                                               "the-actual-hash",
                                                               "test"
                                                       )
                                               )
                )
        );

        final boolean result = subject.isValid(
                mockedContext,
                "test",
                "not-the-correct-hash"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getPrivateData(
                TEST_COLLECTION,
                "TEST"
        );
        assertFalse(result);
    }

    @Test
    void isValidShouldReturnTrueBecauseSubmittedHashDoesMatchActualHash() throws
                                                                          IOException {
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                TEST_COLLECTION,
                "TEST"
        )).thenReturn(
                objectMapper.writeValueAsBytes(new VehicleState(
                                                       "1",
                                                       new Vehicle(
                                                               "the-actual-hash",
                                                               "test"
                                                       )
                                               )
                )
        );

        final boolean result = subject.isValid(
                mockedContext,
                "test",
                "the-actual-hash"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getPrivateData(
                TEST_COLLECTION,
                "TEST"
        );
        assertTrue(result);
    }

    @Test
    void recordInitialStateShouldThrowExceptionBecauseRequestorIsNotAuthorized() {
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn("NotAuthorizedMSP");

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.recordInitialState(mockedContext);
                }
        );

        assertTrue(exception.getMessage().contains("Unauthorized request."));
    }

    @Test
    void recordInitialStateShouldThrowExceptionBecauseRequestDoesNotHaveTransientMap() {
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(RECORD_INITIAL_STATE_MSPS);
        when(mockedChaincodeStub.getTransient()).thenReturn(null);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.recordInitialState(mockedContext);
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void recordInitialStateShouldThrowExceptionBecauseRequestDoesNotHaveVehicleStateProperties() {
        final Map<String, byte[]> transientMap = new HashMap<>();

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(RECORD_INITIAL_STATE_MSPS);
        when(mockedChaincodeStub.getTransient()).thenReturn(transientMap);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.recordInitialState(mockedContext);
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void recordInitialStateShouldThrowExceptionBecauseRequestStateAlreadyExists() throws
                                                                                  JsonProcessingException {
        final Map<String, byte[]> transientMap = new HashMap<>();

        transientMap.put(
                VEHICLE_STATE_PROPERTIES,
                objectMapper.writeValueAsBytes(expectedVehicleState)
        );

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(RECORD_INITIAL_STATE_MSPS);
        when(mockedChaincodeStub.getTransient()).thenReturn(transientMap);
        when(mockedChaincodeStub.getPrivateData(
                TEST_COLLECTION,
                "TEST"
        )).thenReturn(objectMapper.writeValueAsBytes(expectedVehicleState));

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.recordInitialState(mockedContext);
                }
        );

        assertTrue(exception.getMessage().contains("State already exists for vehicle test."));
    }

    @Test
    void recordInitialStateShouldThrowExceptionBecauseThereWasAnErrorSaving() throws
                                                                              JsonProcessingException {
        final Map<String, byte[]> transientMap = new HashMap<>();

        transientMap.put(
                VEHICLE_STATE_PROPERTIES,
                objectMapper.writeValueAsBytes(expectedVehicleState)
        );

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(RECORD_INITIAL_STATE_MSPS);
        when(mockedChaincodeStub.getTransient()).thenReturn(transientMap);
        when(mockedChaincodeStub.getPrivateData(
                TEST_COLLECTION,
                "TEST"
        )).thenReturn(null);
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    doThrow(ChaincodeException.class).when(mockedChaincodeStub).putPrivateData(
                            TEST_COLLECTION,
                            "TEST",
                            objectMapper.writeValueAsBytes(expectedVehicleState)
                    );

                    subject.recordInitialState(mockedContext);
                }
        );

        assertTrue(exception.getMessage().contains("There was an error saving the state for vehicle test."));
    }

    @Test
    void recordInitialStateShouldSave() throws
                                        IOException {
        final Map<String, byte[]> transientMap = new HashMap<>();

        transientMap.put(
                VEHICLE_STATE_PROPERTIES,
                objectMapper.writeValueAsBytes(expectedVehicleState)
        );

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn("PurdueFinalAssemblerMSP");
        when(mockedChaincodeStub.getTransient()).thenReturn(transientMap);
        when(mockedChaincodeStub.getPrivateData(
                TEST_COLLECTION,
                "TEST"
        )).thenReturn(null);

        subject.recordInitialState(mockedContext);

        verify(
                mockedChaincodeStub,
                times(1)
        ).putPrivateData(
                TEST_COLLECTION,
                "TEST",
                objectMapper.writeValueAsBytes(expectedVehicleState)
        );
    }

    @Test
    void updateStateShouldThrowExceptionBecauseRequestorIsNotAuthorized() {
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn("NotAuthorizedMSP");

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.updateState(mockedContext);
                }
        );

        assertTrue(exception.getMessage().contains("Unauthorized request."));
    }

    @Test
    void updateStateShouldThrowExceptionBecauseRequestDoesNotHaveTransientMap() {
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(UPDATE_STATE_MSP);
        when(mockedChaincodeStub.getTransient()).thenReturn(null);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.updateState(mockedContext);
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void updateStateShouldThrowExceptionBecauseRequestDoesNotHaveVehicleStateProperties() {
        final Map<String, byte[]> transientMap = new HashMap<>();

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(UPDATE_STATE_MSP);
        when(mockedChaincodeStub.getTransient()).thenReturn(transientMap);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.updateState(mockedContext);
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void updateStateShouldThrowExceptionBecauseRequestDoesNotHaveCalculatedHashProperty() throws
                                                                                          JsonProcessingException {
        final Map<String, byte[]> transientMap = new HashMap<>();

        transientMap.put(
                VEHICLE_STATE_PROPERTIES,
                objectMapper.writeValueAsBytes(expectedVehicleState)
        );

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(UPDATE_STATE_MSP);
        when(mockedChaincodeStub.getTransient()).thenReturn(transientMap);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.updateState(mockedContext);
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void updateStateShouldThrowExceptionBecauseStateDoesNotExist() throws
                                                                   JsonProcessingException {
        final Map<String, byte[]> transientMap = new HashMap<>();

        transientMap.put(
                VEHICLE_STATE_PROPERTIES,
                objectMapper.writeValueAsBytes(expectedVehicleState)
        );
        transientMap.put(
                "calculated_hash",
                "hash-here".getBytes()
        );

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(UPDATE_STATE_MSP);
        when(mockedChaincodeStub.getTransient()).thenReturn(transientMap);
        when(mockedChaincodeStub.getPrivateData(
                TEST_COLLECTION,
                "TEST"
        )).thenReturn(null);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.updateState(mockedContext);
                }
        );

        assertTrue(exception.getMessage().contains("No state found for vehicle test."));
    }

    @Test
    void updateStateShouldThrowExceptionBecauseStateDoesNotMatch() throws
                                                                   JsonProcessingException {
        final Map<String, byte[]> transientMap = new HashMap<>();

        transientMap.put(
                VEHICLE_STATE_PROPERTIES,
                objectMapper.writeValueAsBytes(expectedVehicleState)
        );
        transientMap.put(
                "calculated_hash",
                "hash-here".getBytes()
        );

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(UPDATE_STATE_MSP);
        when(mockedChaincodeStub.getTransient()).thenReturn(transientMap);
        when(mockedChaincodeStub.getPrivateData(
                TEST_COLLECTION,
                "TEST"
        )).thenReturn(objectMapper.writeValueAsBytes(new VehicleState(
                "1",
                new Vehicle(
                        "not-the-correct-hash",
                        "test"
                )
        )));

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.updateState(mockedContext);
                }
        );

        assertTrue(exception.getMessage().contains("The calculated state does not match the expected state for vehicle test."));
    }

    @Test
    void updateStatePurdueDealerTechnicianShouldSave() throws
                                                       IOException {
        final Map<String, byte[]> transientMap = new HashMap<>();

        transientMap.put(
                VEHICLE_STATE_PROPERTIES,
                objectMapper.writeValueAsBytes(
                        new VehicleState(
                                "1",
                                new Vehicle(
                                        "updated-hash",
                                        "test"
                                )
                        )
                )
        );
        transientMap.put(
                "calculated_hash",
                expectedVehicleState.getVehicleHash().getBytes()
        );

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(UPDATE_STATE_MSP);
        when(mockedChaincodeStub.getTransient()).thenReturn(transientMap);
        when(mockedChaincodeStub.getPrivateData(
                TEST_COLLECTION,
                "TEST"
        )).thenReturn(objectMapper.writeValueAsBytes(expectedVehicleState));

        subject.updateState(mockedContext);
    }

    @Test
    void updateStatePurdueVehicleOwnerShouldSave() throws
                                                   IOException {
        final Map<String, byte[]> transientMap = new HashMap<>();

        transientMap.put(
                VEHICLE_STATE_PROPERTIES,
                objectMapper.writeValueAsBytes(
                        new VehicleState(
                                "1",
                                new Vehicle(
                                        "updated-hash",
                                        "test"
                                )
                        )
                )
        );
        transientMap.put(
                "calculated_hash",
                expectedVehicleState.getVehicleHash().getBytes()
        );

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn("PurdueVehicleOwnerMSP");
        when(mockedChaincodeStub.getTransient()).thenReturn(transientMap);
        when(mockedChaincodeStub.getPrivateData(
                TEST_COLLECTION,
                "TEST"
        )).thenReturn(objectMapper.writeValueAsBytes(expectedVehicleState));

        subject.updateState(mockedContext);
    }

    @Test
    void overrideStateShouldThrowExceptionBecauseRequestorIsNotAuthorized() {
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn("NotAuthorizedMSP");

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.overrideState(mockedContext);
                }
        );

        assertTrue(exception.getMessage().contains("Unauthorized request."));
    }

    @Test
    void overrideStateShouldThrowExceptionBecauseRequestDoesNotHaveTransientMap() {
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(OVERRIDE_STATE_MSP);
        when(mockedChaincodeStub.getTransient()).thenReturn(null);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.overrideState(mockedContext);
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void overrideStateShouldThrowExceptionBecauseRequestDoesNotHaveVehicleStateProperties() {
        final Map<String, byte[]> transientMap = new HashMap<>();

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(OVERRIDE_STATE_MSP);
        when(mockedChaincodeStub.getTransient()).thenReturn(transientMap);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.overrideState(mockedContext);
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void overrideStateShouldThrowExceptionBecauseStateDoesNotExist() throws
                                                                     JsonProcessingException {
        final Map<String, byte[]> transientMap = new HashMap<>();

        transientMap.put(
                VEHICLE_STATE_PROPERTIES,
                objectMapper.writeValueAsBytes(expectedVehicleState)
        );

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(OVERRIDE_STATE_MSP);
        when(mockedChaincodeStub.getTransient()).thenReturn(transientMap);
        when(mockedChaincodeStub.getPrivateData(
                TEST_COLLECTION,
                "TEST"
        )).thenReturn(null);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.overrideState(mockedContext);
                }
        );

        assertTrue(exception.getMessage().contains("No state found for vehicle test."));
    }

    @Test
    void overrideStatePurdueDealerTechnicianShouldSave() throws
                                                         IOException {
        final Map<String, byte[]> transientMap = new HashMap<>();

        transientMap.put(
                VEHICLE_STATE_PROPERTIES,
                objectMapper.writeValueAsBytes(
                        new VehicleState(
                                "1",
                                new Vehicle(
                                        "updated-hash",
                                        "test"
                                )
                        )
                )
        );

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(OVERRIDE_STATE_MSP);
        when(mockedChaincodeStub.getTransient()).thenReturn(transientMap);
        when(mockedChaincodeStub.getPrivateData(
                TEST_COLLECTION,
                "TEST"
        )).thenReturn(objectMapper.writeValueAsBytes(expectedVehicleState));

        subject.overrideState(mockedContext);
    }
}
