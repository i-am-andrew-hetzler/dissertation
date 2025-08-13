package com.andrewhetzler.federal;

import com.andrewhetzler.federal.vehicle_state.Vehicle;
import com.andrewhetzler.federal.vehicle_state.VehicleState;
import com.andrewhetzler.federal.vehicle_state.VehicleStateChaincode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.mockito.Mockito.doNothing;
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
    private VehicleStateChaincode subject;
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final VehicleState expectedVehicleState = new VehicleState(
            "1",
            new Vehicle(
                    "correct-hash",
                    "test"
            )
    );
    private static final String TEST_COLLECTION = "test_vehicle_state_collection";

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
    void recordInitialStateShouldThrowExceptionBecauseRequestDoesNotHaveTransientMap() {
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
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
        when(mockedChaincodeStub.getTransient()).thenReturn(transientMap);
        when(mockedChaincodeStub.getPrivateData(
                TEST_COLLECTION,
                "TEST"
        )).thenReturn(null);
        doThrow().when()
//        doNothing().when(mockedChaincodeStub.putPrivateData(
//                VEHICLE_STATE_PROPERTIES,
//                "TEST",
//                objectMapper.writeValueAsBytes(expectedVehicleState)
//        )).thenThrow(new Exception("Error"));

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.recordInitialState(mockedContext);
                }
        );

        assertTrue(exception.getMessage().contains("State already exists for vehicle test."));
    }
}
