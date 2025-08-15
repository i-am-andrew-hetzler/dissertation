package com.andrewhetzler.federal.recall_notifications;

import com.andrewhetzler.federal.recall_notifications.model.public_recall.VehicleRecalls;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/14/25
 **/
class RecallNotificationsChaincodeTest {
    @Mock
    private Context mockedContext;
    @Mock
    private ChaincodeStub mockedChaincodeStub;
    private RecallNotificationsChaincode subject;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        openMocks(this);

        subject = new RecallNotificationsChaincode();
    }

    @Test
    void getRecallsForVehicleShouldThrowExceptionBecauseRequestIsMissingMake() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.getRecallsForVehicle(
                            mockedContext,
                            null,
                            "Boilermaker",
                            "123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void getRecallsForVehicleShouldThrowExceptionBecauseMakeIsBlank() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.getRecallsForVehicle(
                            mockedContext,
                            "",
                            "Boilermaker",
                            "123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void getRecallsForVehicleShouldThrowExceptionBecauseRequestIsMissingModel() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.getRecallsForVehicle(
                            mockedContext,
                            "Purdue Motor Company",
                            null,
                            "123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void getRecallsForVehicleShouldThrowExceptionBecauseModelIsBlank() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.getRecallsForVehicle(
                            mockedContext,
                            "Purdue Motor Company",
                            "",
                            "123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void getRecallsForVehicleShouldThrowExceptionBecauseRequestIsMissingVin() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.getRecallsForVehicle(
                            mockedContext,
                            "Purdue Motor Company",
                            "Boilermaker",
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void getRecallsForVehicleShouldThrowExceptionBecauseVinIsBlank() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.getRecallsForVehicle(
                            mockedContext,
                            "Purdue Motor Company",
                            "Boilermaker",
                            ""
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void getRecallsForVehicleShouldThrowExceptionBecauseNoRecallsExistForVehicle() {
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState("PURDUE MOTOR COMPANY-BOILERMAKER-123")).thenReturn(null);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.getRecallsForVehicle(
                            mockedContext,
                            "Purdue Motor Company",
                            "Boilermaker",
                            "123"
                    );
                }
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getState("PURDUE MOTOR COMPANY-BOILERMAKER-123");
        assertTrue(exception.getMessage().contains("No recalls were found for vehicle 123."));
    }

    @Test
    void getRecallsForVehicleShouldReturnRecalls() throws
                                                   IOException {
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState("PURDUE MOTOR COMPANY-BOILERMAKER-123")).thenReturn(
                objectMapper.writeValueAsBytes(
                        new VehicleRecalls(
                                List.of(
                                        "recall1",
                                        "recall2"
                                )
                        )
                )
        );

        final VehicleRecalls result = subject.getRecallsForVehicle(
                mockedContext,
                "Purdue Motor Company",
                "Boilermaker",
                "123"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getState("PURDUE MOTOR COMPANY-BOILERMAKER-123");
        assertEquals(
                new VehicleRecalls(
                        List.of(
                                "recall1",
                                "recall2"
                        )
                ),
                result
        );
    }

    @Test
    void saveRecallListForVehicleShouldThrowExceptionBecauseRequestIsMissingCampaignNumber() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveRecallListForVehicle(
                            mockedContext,
                            null,
                            "Purdue Motor Company",
                            "Boilermaker",
                            "123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveRecallListForVehicleShouldThrowExceptionBecauseCampaignNumberIsBlank() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveRecallListForVehicle(
                            mockedContext,
                            "Purdue Motor Company",
                            "Boilermaker",
                            "",
                            "123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveRecallListForVehicleShouldThrowExceptionBecauseRequestIsMissingMake() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveRecallListForVehicle(
                            mockedContext,
                            "recall ABC",
                            null,
                            "Boilermaker",
                            "123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveRecallListForVehicleShouldThrowExceptionBecauseMakeIsBlank() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveRecallListForVehicle(
                            mockedContext,
                            "recall ABC",
                            "",
                            "Boilermaker",
                            "123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveRecallListForVehicleShouldThrowExceptionBecauseRequestIsMissingModel() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveRecallListForVehicle(
                            mockedContext,
                            "recall ABC",
                            "Purdue Motor Company",
                            null,
                            "123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveRecallListForVehicleShouldThrowExceptionBecauseModelIsBlank() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveRecallListForVehicle(
                            mockedContext,
                            "recall ABC",
                            "Purdue Motor Company",
                            "",
                            "123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveRecallListForVehicleShouldThrowExceptionBecauseRequestIsMissingVin() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveRecallListForVehicle(
                            mockedContext,
                            "recall ABC",
                            "Purdue Motor Company",
                            "Boilermaker",
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveRecallListForVehicleShouldThrowExceptionBecauseVinIsBlank() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveRecallListForVehicle(
                            mockedContext,
                            "recall ABC",
                            "Purdue Motor Company",
                            "Boilermaker",
                            ""
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveRecallListForVehicleShouldSaveWhenFirstRecall() throws
                                                             IOException {
        final VehicleRecalls expected = new VehicleRecalls(
                List.of("recall ABC")
        );

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState("PURDUE MOTOR COMPANY-BOILERMAKER-123")).thenReturn(null);

        final VehicleRecalls result = subject.saveRecallListForVehicle(
                mockedContext,
                "recall ABC",
                "Purdue Motor Company",
                "Boilermaker",
                "123"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getState("PURDUE MOTOR COMPANY-BOILERMAKER-123");
        verify(
                mockedChaincodeStub,
                times(1)
        ).putState(
                "PURDUE MOTOR COMPANY-BOILERMAKER-123",
                objectMapper.writeValueAsBytes(expected)
        );
        assertEquals(
                expected,
                result
        );
    }

    @Test
    void saveRecallListForVehicleShouldSaveWhenThereAreMultipleRecalls() throws
                                                             IOException {
        final VehicleRecalls expected = new VehicleRecalls(
                List.of(
                        "recall ABC",
                        "recall DEF"
                )
        );

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState("PURDUE MOTOR COMPANY-BOILERMAKER-123")).thenReturn(
                objectMapper.writeValueAsBytes(
                        new VehicleRecalls(List.of("recall ABC"))
                )
        );

        final VehicleRecalls result = subject.saveRecallListForVehicle(
                mockedContext,
                "recall DEF",
                "Purdue Motor Company",
                "Boilermaker",
                "123"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getState("PURDUE MOTOR COMPANY-BOILERMAKER-123");
        verify(
                mockedChaincodeStub,
                times(1)
        ).putState(
                "PURDUE MOTOR COMPANY-BOILERMAKER-123",
                objectMapper.writeValueAsBytes(expected)
        );
        assertEquals(
                expected,
                result
        );
        assertEquals(
                expected.getRecalls().get(0),
                "recall ABC"
        );
        assertEquals(
                expected.getRecalls().get(1),
                "recall DEF"
        );
    }
}
