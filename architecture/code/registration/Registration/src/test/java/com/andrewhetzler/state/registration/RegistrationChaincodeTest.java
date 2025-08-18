package com.andrewhetzler.state.registration;


import com.andrewhetzler.state.registration.model.Address;
import com.andrewhetzler.state.registration.model.Registrant;
import com.andrewhetzler.state.registration.model.Registration;
import com.andrewhetzler.state.registration.model.RegistrationSchema;
import com.andrewhetzler.state.registration.model.persisted.PersistedAddress;
import com.andrewhetzler.state.registration.model.persisted.PersistedRegistrant;
import com.andrewhetzler.state.registration.model.persisted.PersistedRegistration;
import com.andrewhetzler.state.registration.model.persisted.PersistedRegistrationSchema;
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
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/18/25
 **/
class RegistrationChaincodeTest {
    @Mock
    private Context mockedContext;
    @Mock
    private ChaincodeStub mockedChaincodeStub;
    @Mock
    private ClientIdentity mockedClientIdentity;
    private RegistrationChaincode subject;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String AUTHORIZED_STATE_MSP_ID = "TestStateMSP";
    private static final String AUTHORIZED_REGISTRANT_MSP_ID = "TestStateDmvRegistrantMSP";
    private static final String AUTHORIZED_STATE_DMV_MSP_IPD = "TestStateDmvMSP";
    private static final String AUTHORIZED_3RD_PARTY_MSP_ID = "TestInsuranceCoMSP";
    private static final String STATE_COLLECTION = "TestStateRegistrationCollection";

    @BeforeEach
    void setUp() {
        openMocks(this);

        subject = new RegistrationChaincode();
    }

    @Test
    void viewRegistrationShouldThrowExceptionBecauseRequestorIsUnauthorized() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn("BessCoMSP");

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewRegistration(
                            mockedContext,
                            "OH-HETZLER"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Unauthorized request."));
    }

    @Test
    void viewRegistrationShouldThrowExceptionBecauseRegistrationNumberIsMissing() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewRegistration(
                            mockedContext,
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void viewRegistrationShouldThrowExceptionBecauseRegistrationNumberIsBlank() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewRegistration(
                            mockedContext,
                            ""
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void viewRegistrationShouldThrowExceptionBecauseNoRegistrationExists() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_MSP_ID);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                "OH-HETZLER"
        )).thenReturn(null);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewRegistration(
                            mockedContext,
                            "OH-HETZLER"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("No registration exists for registration number OH-HETZLER."));
    }

    @Test
    void viewRegistrationShouldReturnBecauseRequestorIsFromStateMSP() throws
                                                                      IOException {
        final RegistrationSchema expected = new RegistrationSchema(
                Map.of("isAutonomousVehicle", "true"),
                List.of(
                        new Registrant(
                                List.of(
                                        new Address(
                                                "123 University Lane",
                                                null,
                                                "West Lafayette",
                                                "Purdue County",
                                                "98765"
                                        )
                                ),
                                "Jane Doe"
                        )
                ),
                new Registration(
                        "OH-HETZLER",
                        Map.of(
                                "make", "Purdue Motor Company",
                                "model", "Boilermaker",
                                "year", "2025",
                                "color", "Black"
                        )
                ),
                "1"
        );

        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_MSP_ID);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                "OH-HETZLER"
        )).thenReturn(
                objectMapper.writeValueAsBytes(
                        new PersistedRegistrationSchema(
                                expected.getOther(),
                                List.of(
                                        new PersistedRegistrant(
                                               List.of(
                                                       new PersistedAddress(
                                                               "123 University Lane",
                                                               null,
                                                               "West Lafayette",
                                                               "Purdue County",
                                                               "98765"
                                                       )
                                               ),
                                               "Jane Doe",
                                               "unique-1"
                                        )
                                ),
                                new PersistedRegistration(
                                        "OH-HETZLER",
                                        Map.of(
                                                "make", "Purdue Motor Company",
                                                "model", "Boilermaker",
                                                "year", "2025",
                                                "color", "Black"
                                        )
                                ),
                                "1"
                        )
                )
        );

        final RegistrationSchema result = subject.viewRegistration(
                mockedContext,
                "OH-HETZLER"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getPrivateData(
                STATE_COLLECTION,
                "OH-HETZLER"
        );
        assertEquals(
                expected,
                result
        );
    }

    @Test
    void viewRegistrationShouldThrowExceptionBecauseTheRequestorIsARegistrantButNotAssociatedWithRegistration() throws
                                                                                                                JsonProcessingException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_REGISTRANT_MSP_ID);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedClientIdentity.getId()).thenReturn("unique-2");
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                "OH-HETZLER"
        )).thenReturn(
                objectMapper.writeValueAsBytes(
                        new PersistedRegistrationSchema(
                                Map.of("isAutonomousVehicle", "true"),
                                List.of(
                                        new PersistedRegistrant(
                                                List.of(
                                                        new PersistedAddress(
                                                                "123 University Lane",
                                                                null,
                                                                "West Lafayette",
                                                                "Purdue County",
                                                                "98765"
                                                        )
                                                ),
                                                "Jane Doe",
                                                "unique-1"
                                        )
                                ),
                                new PersistedRegistration(
                                        "OH-HETZLER",
                                        Map.of(
                                                "make", "Purdue Motor Company",
                                                "model", "Boilermaker",
                                                "year", "2025",
                                                "color", "Black"
                                        )
                                ),
                                "1"
                        )
                )
        );

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewRegistration(
                            mockedContext,
                            "OH-HETZLER"
                    );
                }
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getPrivateData(
                STATE_COLLECTION,
                "OH-HETZLER"
        );
        assertTrue(exception.getMessage().contains("Unauthorized request."));
    }

    @Test
    void viewRegistrationShouldReturnBecauseRequestorIsTheActualRegistrant() throws
                                                                      IOException {
        final RegistrationSchema expected = new RegistrationSchema(
                Map.of("isAutonomousVehicle", "true"),
                List.of(
                        new Registrant(
                                List.of(
                                        new Address(
                                                "123 University Lane",
                                                null,
                                                "West Lafayette",
                                                "Purdue County",
                                                "98765"
                                        )
                                ),
                                "Jane Doe"
                        )
                ),
                new Registration(
                        "OH-HETZLER",
                        Map.of(
                                "make", "Purdue Motor Company",
                                "model", "Boilermaker",
                                "year", "2025",
                                "color", "Black"
                        )
                ),
                "1"
        );

        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_REGISTRANT_MSP_ID);
        when(mockedClientIdentity.getId()).thenReturn("unique-1");
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                "OH-HETZLER"
        )).thenReturn(
                objectMapper.writeValueAsBytes(
                        new PersistedRegistrationSchema(
                                expected.getOther(),
                                List.of(
                                        new PersistedRegistrant(
                                                List.of(
                                                        new PersistedAddress(
                                                                "123 University Lane",
                                                                null,
                                                                "West Lafayette",
                                                                "Purdue County",
                                                                "98765"
                                                        )
                                                ),
                                                "Jane Doe",
                                                "unique-1"
                                        )
                                ),
                                new PersistedRegistration(
                                        "OH-HETZLER",
                                        Map.of(
                                                "make", "Purdue Motor Company",
                                                "model", "Boilermaker",
                                                "year", "2025",
                                                "color", "Black"
                                        )
                                ),
                                "1"
                        )
                )
        );

        final RegistrationSchema result = subject.viewRegistration(
                mockedContext,
                "OH-HETZLER"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getPrivateData(
                STATE_COLLECTION,
                "OH-HETZLER"
        );
        assertEquals(
                expected,
                result
        );
    }

    @Test
    void viewRegistrationShouldReturnBecauseRequestorIsAuthorized3rdParty() throws
                                                                             IOException {
        final RegistrationSchema expected = new RegistrationSchema(
                Map.of("isAutonomousVehicle", "true"),
                List.of(
                        new Registrant(
                                List.of(
                                        new Address(
                                                "123 University Lane",
                                                null,
                                                "West Lafayette",
                                                "Purdue County",
                                                "98765"
                                        )
                                ),
                                "Jane Doe"
                        )
                ),
                new Registration(
                        "OH-HETZLER",
                        Map.of(
                                "make", "Purdue Motor Company",
                                "model", "Boilermaker",
                                "year", "2025",
                                "color", "Black"
                        )
                ),
                "1"
        );

        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_3RD_PARTY_MSP_ID);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                "OH-HETZLER"
        )).thenReturn(
                objectMapper.writeValueAsBytes(
                        new PersistedRegistrationSchema(
                                expected.getOther(),
                                List.of(
                                        new PersistedRegistrant(
                                                List.of(
                                                        new PersistedAddress(
                                                                "123 University Lane",
                                                                null,
                                                                "West Lafayette",
                                                                "Purdue County",
                                                                "98765"
                                                        )
                                                ),
                                                "Jane Doe",
                                                "unique-1"
                                        )
                                ),
                                new PersistedRegistration(
                                        "OH-HETZLER",
                                        Map.of(
                                                "make", "Purdue Motor Company",
                                                "model", "Boilermaker",
                                                "year", "2025",
                                                "color", "Black"
                                        )
                                ),
                                "1"
                        )
                )
        );

        final RegistrationSchema result = subject.viewRegistration(
                mockedContext,
                "OH-HETZLER"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getPrivateData(
                STATE_COLLECTION,
                "OH-HETZLER"
        );
        verify(
                mockedChaincodeStub,
                times(1)
        ).putPrivateData(
                String.format("%s_REGISTRATION_COLLECTION", AUTHORIZED_3RD_PARTY_MSP_ID.toUpperCase()),
                "OH-HETZLER",
                objectMapper.writeValueAsBytes(expected)
        );
        assertEquals(
                expected,
                result
        );
    }
}
