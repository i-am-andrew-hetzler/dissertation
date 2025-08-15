package com.andrewhetzler.federal.recall_notifications;

import com.andrewhetzler.federal.recall_notifications.model.public_recall.PublicRecall;
import com.andrewhetzler.federal.recall_notifications.model.public_recall.Recall;
import com.andrewhetzler.federal.recall_notifications.model.public_recall.Vehicle;
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
import static org.mockito.Mockito.doThrow;
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
    void getRecallListForVehicleShouldThrowExceptionBecauseRequestIsMissingMake() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.getRecallListForVehicle(
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
    void getRecallListForVehicleShouldThrowExceptionBecauseMakeIsBlank() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.getRecallListForVehicle(
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
    void getRecallListForVehicleShouldThrowExceptionBecauseRequestIsMissingModel() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.getRecallListForVehicle(
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
    void getRecallListForVehicleShouldThrowExceptionBecauseModelIsBlank() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.getRecallListForVehicle(
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
    void getRecallListForVehicleShouldThrowExceptionBecauseRequestIsMissingVin() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.getRecallListForVehicle(
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
    void getRecallListForVehicleShouldThrowExceptionBecauseVinIsBlank() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.getRecallListForVehicle(
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
    void getRecallsForVehicleShouldThrowExceptionBecauseNoRecallListExistForVehicle() {
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState("PURDUE MOTOR COMPANY-BOILERMAKER-123")).thenReturn(null);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.getRecallListForVehicle(
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
    void getRecallsForVehicleShouldReturnRecallList() throws
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

        final VehicleRecalls result = subject.getRecallListForVehicle(
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

    @Test
    void getVehicleRecallShouldThrowExceptionBecauseRequestIsMissingCampaignNumber() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.getVehicleRecall(
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
    void getVehicleRecallShouldThrowExceptionBecauseCampaignNumberIsBlank() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.getVehicleRecall(
                            mockedContext,
                            "",
                            "Purdue Motor Company",
                            "Boilermaker",
                            "123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void getVehicleRecallShouldThrowExceptionBecauseRequestIsMissingMake() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.getVehicleRecall(
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
    void getVehicleRecallShouldThrowExceptionBecauseMakeIsBlank() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.getVehicleRecall(
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
    void getVehicleRecallShouldThrowExceptionBecauseRequestIsMissingModel() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.getVehicleRecall(
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
    void getVehicleRecallShouldThrowExceptionBecauseModelIsBlank() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.getVehicleRecall(
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
    void getVehicleRecallShouldThrowExceptionBecauseRequestIsMissingVin() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.getVehicleRecall(
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
    void getVehicleRecallShouldThrowExceptionBecauseVinIsBlank() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.getVehicleRecall(
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
    void getVehicleRecallShouldThrowExceptionBecauseCampaignNumberDoesNotExistForVehicle() {
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState("PURDUE MOTOR COMPANY-BOILERMAKER-123-RECALL ABC")).thenReturn(null);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.getVehicleRecall(
                            mockedContext,
                            "recall ABC",
                            "Purdue Motor Company",
                            "Boilermaker",
                            "123"
                    );
                }
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getState("PURDUE MOTOR COMPANY-BOILERMAKER-123-RECALL ABC");
        assertTrue(exception.getMessage().contains("The recall # recall ABC could not be found for vehicle 123."));
    }

    @Test
    void getVehicleRecallShouldReturnRecall() throws
                                              IOException {
        final PublicRecall expected = new PublicRecall(
                new Recall(
                        "recall ABC",
                        "May 27, 2025",
                        "The windshield explodes when the car runs for 10 minutes.",
                        "See a local Purdue Motor Company dealer.",
                        "NOT_COMPLETED"
                ),
                "1",
                new Vehicle(
                        "123",
                        "Purdue Motor Company",
                        "Boilermaker"
                )
        );

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState("PURDUE MOTOR COMPANY-BOILERMAKER-123-RECALL ABC")).thenReturn(
                objectMapper.writeValueAsBytes(expected)
        );

        final PublicRecall result = subject.getVehicleRecall(
                mockedContext,
                "recall ABC",
                "Purdue Motor Company",
                "Boilermaker",
                "123"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getState("PURDUE MOTOR COMPANY-BOILERMAKER-123-RECALL ABC");
        assertEquals(
                expected,
                result
        );
    }

    @Test
    void saveVehicleRecallShouldThrowExceptionBecauseCampaignNumberIsMissing() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveVehicleRecall(
                            mockedContext,
                            null,
                            "October 17, 2025",
                            "Windshield is bad",
                            "See a dealer",
                            "NOT_COMPLETED",
                            "1",
                            "Purdue Motor Company",
                            "Boilermaker",
                            "123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveVehicleRecallShouldThrowExceptionBecauseCampaignNumberIsBlank() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveVehicleRecall(
                            mockedContext,
                            "",
                            "October 17, 2025",
                            "Windshield is bad",
                            "See a dealer",
                            "NOT_COMPLETED",
                            "1",
                            "Purdue Motor Company",
                            "Boilermaker",
                            "123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveVehicleRecallShouldThrowExceptionBecauseDateIsMissing() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveVehicleRecall(
                            mockedContext,
                            "recall ABC",
                            null,
                            "Windshield is bad",
                            "See a dealer",
                            "NOT_COMPLETED",
                            "1",
                            "Purdue Motor Company",
                            "Boilermaker",
                            "123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveVehicleRecallShouldThrowExceptionBecauseDateIsBlank() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveVehicleRecall(
                            mockedContext,
                            "recall ABC",
                            "",
                            "Windshield is bad",
                            "See a dealer",
                            "NOT_COMPLETED",
                            "1",
                            "Purdue Motor Company",
                            "Boilermaker",
                            "123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveVehicleRecallShouldThrowExceptionBecauseDescriptionIsMissing() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveVehicleRecall(
                            mockedContext,
                            "recall ABC",
                            "October 17, 2025",
                            null,
                            "See a dealer",
                            "NOT_COMPLETED",
                            "1",
                            "Purdue Motor Company",
                            "Boilermaker",
                            "123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveVehicleRecallShouldThrowExceptionBecauseDescriptionIsBlank() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveVehicleRecall(
                            mockedContext,
                            "recall ABC",
                            "October 17, 2025",
                            "",
                            "See a dealer",
                            "NOT_COMPLETED",
                            "1",
                            "Purdue Motor Company",
                            "Boilermaker",
                            "123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveVehicleRecallShouldThrowExceptionBecauseRemedyProgramDescriptionIsMissing() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveVehicleRecall(
                            mockedContext,
                            "recall ABC",
                            "October 17, 2025",
                            "Windshield is bad",
                            null,
                            "NOT_COMPLETED",
                            "1",
                            "Purdue Motor Company",
                            "Boilermaker",
                            "123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveVehicleRecallShouldThrowExceptionBecauseRemedyProgramDescriptionIsBlank() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveVehicleRecall(
                            mockedContext,
                            "recall ABC",
                            "October 17, 2025",
                            "Windshield is bad",
                            "",
                            "NOT_COMPLETED",
                            "1",
                            "Purdue Motor Company",
                            "Boilermaker",
                            "123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveVehicleRecallShouldThrowExceptionBecauseRemedyStatusIsMissing() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveVehicleRecall(
                            mockedContext,
                            "recall ABC",
                            "October 17, 2025",
                            "Windshield is bad",
                            "See a dealer",
                            null,
                            "1",
                            "Purdue Motor Company",
                            "Boilermaker",
                            "123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveVehicleRecallShouldThrowExceptionBecauseRemedyStatusIsBlank() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveVehicleRecall(
                            mockedContext,
                            "recall ABC",
                            "October 17, 2025",
                            "Windshield is bad",
                            "See a dealer",
                            "",
                            "1",
                            "Purdue Motor Company",
                            "Boilermaker",
                            "123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveVehicleRecallShouldThrowExceptionBecauseSchemaVersionIsMissing() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveVehicleRecall(
                            mockedContext,
                            "recall ABC",
                            "October 17, 2025",
                            "Windshield is bad",
                            "See a dealer",
                            "NOT_COMPLETED",
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
    void saveVehicleRecallShouldThrowExceptionBecauseSchemaVersionIsBlank() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveVehicleRecall(
                            mockedContext,
                            "recall ABC",
                            "October 17, 2025",
                            "Windshield is bad",
                            "See a dealer",
                            "NOT_COMPLETED",
                            "",
                            "Purdue Motor Company",
                            "Boilermaker",
                            "123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveVehicleRecallShouldThrowExceptionBecauseMakeIsMissing() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveVehicleRecall(
                            mockedContext,
                            "recall ABC",
                            "October 17, 2025",
                            "Windshield is bad",
                            "See a dealer",
                            "NOT_COMPLETED",
                            "1",
                            null,
                            "Boilermaker",
                            "123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveVehicleRecallShouldThrowExceptionBecauseMakeIsBlank() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveVehicleRecall(
                            mockedContext,
                            "recall ABC",
                            "October 17, 2025",
                            "Windshield is bad",
                            "See a dealer",
                            "NOT_COMPLETED",
                            "1",
                            "",
                            "Boilermaker",
                            "123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveVehicleRecallShouldThrowExceptionModelIsMissing() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveVehicleRecall(
                            mockedContext,
                            "recall ABC",
                            "October 17, 2025",
                            "Windshield is bad",
                            "See a dealer",
                            "NOT_COMPLETED",
                            "1",
                            "Purdue Motor Company",
                            null,
                            "123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveVehicleRecallShouldThrowExceptionBecauseModelIsBlank() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveVehicleRecall(
                            mockedContext,
                            "recall ABC",
                            "October 17, 2025",
                            "Windshield is bad",
                            "See a dealer",
                            "NOT_COMPLETED",
                            "1",
                            "Purdue Motor Company",
                            "",
                            "123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveVehicleRecallShouldThrowExceptionBecauseVinIsMissing() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveVehicleRecall(
                            mockedContext,
                            "recall ABC",
                            "October 17, 2025",
                            "Windshield is bad",
                            "See a dealer",
                            "NOT_COMPLETED",
                            "1",
                            "Purdue Motor Company",
                            "Boilermaker",
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveVehicleRecallShouldThrowExceptionBecauseVinIsBlank() {
        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveVehicleRecall(
                            mockedContext,
                            "recall ABC",
                            "October 17, 2025",
                            "Windshield is bad",
                            "See a dealer",
                            "NOT_COMPLETED",
                            "1",
                            "Purdue Motor Company",
                            "Boilermaker",
                            ""
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveVehicleRecallShouldThrowExceptionBecauseErrorCallingSaveRecall() throws
                                                                              IOException {
        final PublicRecall expected = new PublicRecall(
                new Recall(
                        "recall ABC",
                        "October 17, 2025",
                        "Windshield is bad",
                        "See a dealer",
                        "NOT_COMPLETED"
                ),
                "1",
                new Vehicle(
                        "123",
                        "Purdue Motor Company",
                        "Boilermaker"
                )
        );

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState("PURDUE MOTOR COMPANY-BOILERMAKER-123")).thenReturn(null);

        final PublicRecall result = subject.saveVehicleRecall(
                mockedContext,
                "recall ABC",
                "October 17, 2025",
                "Windshield is bad",
                "See a dealer",
                "NOT_COMPLETED",
                "1",
                "Purdue Motor Company",
                "Boilermaker",
                "123"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).putState(
                "PURDUE MOTOR COMPANY-BOILERMAKER-123-RECALL ABC",
                objectMapper.writeValueAsBytes(expected)
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
                objectMapper.writeValueAsBytes(
                        new VehicleRecalls(List.of("recall ABC"))
                )
        );
        assertEquals(
                expected,
                result
        );
    }
}
