package com.andrewhetzler.federal.recall_notifications;

import com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list.Address;
import com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list.ImpactedOwnerList;
import com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list.Owner;
import com.andrewhetzler.federal.recall_notifications.model.public_recall.PublicRecall;
import com.andrewhetzler.federal.recall_notifications.model.public_recall.Recall;
import com.andrewhetzler.federal.recall_notifications.model.public_recall.Vehicle;
import com.andrewhetzler.federal.recall_notifications.model.public_recall.VehicleRecalls;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.contract.ClientIdentity;
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
    @Mock
    private ClientIdentity mockedClientIdentity;
    private RecallNotificationsChaincode subject;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String PURDUE_MOCO_MSP_ID = "PurdueMotorCompanyMSP";

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
    void saveRecallListForVehicleShouldNotSaveWhenRecallAlreadyExists() throws
                                                                         IOException {
        final VehicleRecalls expected = new VehicleRecalls(
                List.of("recall ABC")
        );

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState("PURDUE MOTOR COMPANY-BOILERMAKER-123")).thenReturn(
                objectMapper.writeValueAsBytes(
                        new VehicleRecalls(List.of("recall ABC"))
                )
        );

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
                times(0)
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
    void saveVehicleRecallShouldSaveRecall() throws
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

    @Test
    void viewImpactedOwnersForRecallShouldThrowExceptionBecauseRequestIsUnauthorizedBecauseTheyAreNotNHTSAOrCorrectMSP() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn("MaseratiMSP");

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewImpactedOwnersForRecall(
                            mockedContext,
                            "recall ABC",
                            PURDUE_MOCO_MSP_ID
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Unauthorized request."));
    }

    @Test
    void viewImpactedOwnersForRecallShouldThrowExceptionBecauseCampaignNumberIsMissing() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(PURDUE_MOCO_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewImpactedOwnersForRecall(
                            mockedContext,
                            null,
                            PURDUE_MOCO_MSP_ID
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void viewImpactedOwnersForRecallShouldThrowExceptionBecauseCampaignNumberIsBlank() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(PURDUE_MOCO_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewImpactedOwnersForRecall(
                            mockedContext,
                            "",
                            PURDUE_MOCO_MSP_ID
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void viewImpactedOwnersForRecallShouldThrowExceptionBecauseCollectionIsMissing() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(PURDUE_MOCO_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewImpactedOwnersForRecall(
                            mockedContext,
                            "recall ABC",
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Unauthorized request."));
    }

    @Test
    void viewImpactedOwnersForRecallShouldThrowExceptionBecauseCollectionIsBlank() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(PURDUE_MOCO_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewImpactedOwnersForRecall(
                            mockedContext,
                            "recall ABC",
                            ""
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Unauthorized request."));
    }

    @Test
    void viewImpactedOwnersForRecallShouldThrowExceptionBecauseListDoesNotExistForRecall() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(PURDUE_MOCO_MSP_ID);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                PURDUE_MOCO_MSP_ID,
                "RECALL ABC"
        )).thenReturn(null);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewImpactedOwnersForRecall(
                            mockedContext,
                            "recall ABC",
                            PURDUE_MOCO_MSP_ID
                    );
                }
        );

        assertTrue(exception.getMessage().contains("No list exists for campaign number recall ABC."));
    }

    @Test
    void viewImpactedOwnersForRecallShouldReturnListForNHTSA() throws
                                                               IOException {
        final ImpactedOwnerList expected = new ImpactedOwnerList(
                List.of(
                        new com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list.Vehicle(
                                "1FV",
                                new Owner(
                                        new Address(
                                                "123 Test Road",
                                                null,
                                                "Example",
                                                "OH",
                                                "98765"
                                        ),
                                        "Unfortunate Dude"
                                ),
                                new com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list.Recall(
                                        "recall ABC",
                                        "NOT_COMPLETED"
                                )
                        )
                ),
                "1"
        );

        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn("NHTSAMSP");
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                PURDUE_MOCO_MSP_ID.toUpperCase(),
                "RECALL ABC"
        )).thenReturn(
                objectMapper.writeValueAsBytes(expected)
        );

        final ImpactedOwnerList result = subject.viewImpactedOwnersForRecall(
                mockedContext,
                "recall ABC",
                PURDUE_MOCO_MSP_ID
        );

        assertEquals(
                expected,
                result
        );
    }

    @Test
    void viewImpactedOwnersForRecallShouldReturnListFBecauseCollectionMatchesMSP() throws
                                                                                   IOException {
        final ImpactedOwnerList expected = new ImpactedOwnerList(
                List.of(
                        new com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list.Vehicle(
                                "1FV",
                                new Owner(
                                        new Address(
                                                "123 Test Road",
                                                null,
                                                "Example",
                                                "OH",
                                                "98765"
                                        ),
                                        "Unfortunate Dude"
                                ),
                                new com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list.Recall(
                                        "recall ABC",
                                        "NOT_COMPLETED"
                                )
                        )
                ),
                "1"
        );

        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(PURDUE_MOCO_MSP_ID);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                PURDUE_MOCO_MSP_ID.toUpperCase(),
                "RECALL ABC"
        )).thenReturn(
                objectMapper.writeValueAsBytes(expected)
        );

        final ImpactedOwnerList result = subject.viewImpactedOwnersForRecall(
                mockedContext,
                "recall ABC",
                PURDUE_MOCO_MSP_ID
        );

        assertEquals(
                expected,
                result
        );
    }

    @Test
    void saveImpactedOwnersForRecallShouldThrowExceptionBecauseRequestIsUnauthorizedBecauseTheyAreNotNHTSAOrCorrectMSP() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn("MaseratiMSP");

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveImpactedOwnersForRecall(
                            mockedContext,
                            "1FV",
                            "Unfortunate Dude",
                            "123 Test Road",
                            null,
                            "Example Junction",
                            "Ohio",
                            "98765",
                            "recall ABC",
                            "NOT_COMPLETED",
                            "1",
                            PURDUE_MOCO_MSP_ID
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Unauthorized request."));
    }

//    HREE

    @Test
    void saveImpactedOwnersForRecallShouldThrowExceptionBecauseCampaignNumberIsMissing() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(PURDUE_MOCO_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveImpactedOwnersForRecall(
                            mockedContext,
                            "1FV",
                            "Unfortunate Dude",
                            "123 Test Road",
                            null,
                            "Example Junction",
                            "Ohio",
                            "98765",
                            null,
                            "NOT_COMPLETED",
                            "1",
                            PURDUE_MOCO_MSP_ID
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveImpactedOwnersForRecallShouldThrowExceptionBecauseCampaignNumberIsBlank() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(PURDUE_MOCO_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveImpactedOwnersForRecall(
                            mockedContext,
                            "1FV",
                            "Unfortunate Dude",
                            "123 Test Road",
                            null,
                            "Example Junction",
                            "Ohio",
                            "98765",
                            "",
                            "NOT_COMPLETED",
                            "1",
                            PURDUE_MOCO_MSP_ID
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveImpactedOwnersForRecallShouldThrowExceptionBecauseRemedyStatusIsMissing() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(PURDUE_MOCO_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveImpactedOwnersForRecall(
                            mockedContext,
                            "1FV",
                            "Unfortunate Dude",
                            "123 Test Road",
                            null,
                            "Example Junction",
                            "Ohio",
                            "98765",
                            "recall ABC",
                            null,
                            "1",
                            PURDUE_MOCO_MSP_ID
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveImpactedOwnersForRecallShouldThrowExceptionBecauseRemedyStatusIsBlank() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(PURDUE_MOCO_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveImpactedOwnersForRecall(
                            mockedContext,
                            "1FV",
                            "Unfortunate Dude",
                            "123 Test Road",
                            null,
                            "Example Junction",
                            "Ohio",
                            "98765",
                            "recall ABC",
                            "",
                            "1",
                            PURDUE_MOCO_MSP_ID
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveImpactedOwnersForRecallShouldThrowExceptionBecauseSchemaVersionIsMissing() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(PURDUE_MOCO_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveImpactedOwnersForRecall(
                            mockedContext,
                            "1FV",
                            "Unfortunate Dude",
                            "123 Test Road",
                            null,
                            "Example Junction",
                            "Ohio",
                            "98765",
                            "recall ABC",
                            "NOT_COMPLETED",
                            null,
                            PURDUE_MOCO_MSP_ID
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveImpactedOwnersForRecallShouldThrowExceptionBecauseSchemaVersionIsBlank() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(PURDUE_MOCO_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveImpactedOwnersForRecall(
                            mockedContext,
                            "1FV",
                            "Unfortunate Dude",
                            "123 Test Road",
                            null,
                            "Example Junction",
                            "Ohio",
                            "98765",
                            "recall ABC",
                            "NOT_COMPLETED",
                            "",
                            PURDUE_MOCO_MSP_ID
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void saveImpactedOwnersForRecallShouldThrowExceptionBecauseCollectionIsMissing() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(PURDUE_MOCO_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveImpactedOwnersForRecall(
                            mockedContext,
                            "1FV",
                            "Unfortunate Dude",
                            "123 Test Road",
                            null,
                            "Example Junction",
                            "Ohio",
                            "98765",
                            "recall ABC",
                            "NOT_COMPLETED",
                            "1",
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Unauthorized request."));
    }

    @Test
    void saveImpactedOwnersForRecallShouldThrowExceptionBecauseCollectionIsBlank() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(PURDUE_MOCO_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.saveImpactedOwnersForRecall(
                            mockedContext,
                            "1FV",
                            "Unfortunate Dude",
                            "123 Test Road",
                            null,
                            "Example Junction",
                            "Ohio",
                            "98765",
                            "recall ABC",
                            "NOT_COMPLETED",
                            "1",
                            ""
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Unauthorized request."));
    }

    @Test
    void saveImpactedOwnersForRecallShouldSaveNewVehicleEntry() throws
                                                                                   IOException {
        final ImpactedOwnerList expected = new ImpactedOwnerList(
                List.of(
                        new com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list.Vehicle(
                                "1FV",
                                new Owner(
                                        new Address(
                                                "123 Test Road",
                                                null,
                                                "Example",
                                                "OH",
                                                "98765"
                                        ),
                                        "Unfortunate Dude"
                                ),
                                new com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list.Recall(
                                        "recall ABC",
                                        "NOT_COMPLETED"
                                )
                        )
                ),
                "1"
        );

        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(PURDUE_MOCO_MSP_ID);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                PURDUE_MOCO_MSP_ID.toUpperCase(),
                "RECALL ABC"
        )).thenReturn(null);

        final ImpactedOwnerList result = subject.saveImpactedOwnersForRecall(
                mockedContext,
                "1FV",
                "Unfortunate Dude",
                "123 Test Road",
                null,
                "Example",
                "OH",
                "98765",
                "recall ABC",
                "NOT_COMPLETED",
                "1",
                PURDUE_MOCO_MSP_ID
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).putPrivateData(
                PURDUE_MOCO_MSP_ID.toUpperCase(),
                "RECALL ABC",
                objectMapper.writeValueAsBytes(expected)
        );
        assertEquals(
                expected,
                result
        );
    }

    @Test
    void saveImpactedOwnersForRecallShouldSaveForSecondVehicleEntry() throws
                                                                IOException {
        final ImpactedOwnerList expected = new ImpactedOwnerList(
                List.of(
                        new com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list.Vehicle(
                                "1FV",
                                new Owner(
                                        new Address(
                                                "123 Test Road",
                                                null,
                                                "Example",
                                                "OH",
                                                "98765"
                                        ),
                                        "Unfortunate Dude"
                                ),
                                new com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list.Recall(
                                        "recall ABC",
                                        "NOT_COMPLETED"
                                )
                        ),
                        new com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list.Vehicle(
                                "2FV",
                                new Owner(
                                        new Address(
                                                "987 University Lane",
                                                "Suite 1",
                                                "West Lafayette",
                                                "IN",
                                                "34567"
                                        ),
                                        "Jane Doe"
                                ),
                                new com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list.Recall(
                                        "recall ABC",
                                        "COMPLETED"
                                )
                        )
                ),
                "1"
        );

        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(PURDUE_MOCO_MSP_ID);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                PURDUE_MOCO_MSP_ID.toUpperCase(),
                "RECALL ABC"
        )).thenReturn(
                objectMapper.writeValueAsBytes(
                        new ImpactedOwnerList(
                                List.of(
                                        new com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list.Vehicle(
                                                "1FV",
                                                new Owner(
                                                        new Address(
                                                                "123 Test Road",
                                                                null,
                                                                "Example",
                                                                "OH",
                                                                "98765"
                                                        ),
                                                        "Unfortunate Dude"
                                                ),
                                                new com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list.Recall(
                                                        "recall ABC",
                                                        "NOT_COMPLETED"
                                                )
                                        )
                                ),
                                "1"
                        )
                )
        );

        final ImpactedOwnerList result = subject.saveImpactedOwnersForRecall(
                mockedContext,
                "2FV",
                "Jane Doe",
                "987 University Lane",
                "Suite 1",
                "West Lafayette",
                "IN",
                "34567",
                "recall ABC",
                "COMPLETED",
                "1",
                PURDUE_MOCO_MSP_ID
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).putPrivateData(
                PURDUE_MOCO_MSP_ID.toUpperCase(),
                "RECALL ABC",
                objectMapper.writeValueAsBytes(expected)
        );
        assertEquals(
                expected,
                result
        );
    }

    @Test
    void saveImpactedOwnersForRecallShouldUpdateFirstVehicleEntry() throws
                                                                      IOException {
        final ImpactedOwnerList expected = new ImpactedOwnerList(
                List.of(
                        new com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list.Vehicle(
                                "1FV",
                                new Owner(
                                        new Address(
                                                "123 Test Road",
                                                null,
                                                "Example",
                                                "OH",
                                                "98765"
                                        ),
                                        "Unfortunate Dude"
                                ),
                                new com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list.Recall(
                                        "recall ABC",
                                        "COMPLETED"
                                )
                        )
                ),
                "1"
        );

        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(PURDUE_MOCO_MSP_ID);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                PURDUE_MOCO_MSP_ID.toUpperCase(),
                "RECALL ABC"
        )).thenReturn(
                objectMapper.writeValueAsBytes(
                        new ImpactedOwnerList(
                                List.of(
                                        new com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list.Vehicle(
                                                "1FV",
                                                new Owner(
                                                        new Address(
                                                                "123 Test Road",
                                                                null,
                                                                "Example",
                                                                "OH",
                                                                "98765"
                                                        ),
                                                        "Unfortunate Dude"
                                                ),
                                                new com.andrewhetzler.federal.recall_notifications.model.impacted_owner_list.Recall(
                                                        "recall ABC",
                                                        "NOT_COMPLETED"
                                                )
                                        )
                                ),
                                "1"
                        )
                )
        );

        final ImpactedOwnerList result = subject.saveImpactedOwnersForRecall(
                mockedContext,
                "1FV",
                "Unfortunate Dude",
                "123 Test Road",
                null,
                "Example",
                "OH",
                "98765",
                "recall ABC",
                "COMPLETED",
                "1",
                PURDUE_MOCO_MSP_ID
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).putPrivateData(
                PURDUE_MOCO_MSP_ID.toUpperCase(),
                "RECALL ABC",
                objectMapper.writeValueAsBytes(expected)
        );
        assertEquals(
                expected,
                result
        );
    }
}
