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
                Map.of(
                        "isAutonomousVehicle",
                        "true"
                ),
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
                                "make",
                                "Purdue Motor Company",
                                "model",
                                "Boilermaker",
                                "year",
                                "2025",
                                "color",
                                "Black"
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
                                                "make",
                                                "Purdue Motor Company",
                                                "model",
                                                "Boilermaker",
                                                "year",
                                                "2025",
                                                "color",
                                                "Black"
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
                                Map.of(
                                        "isAutonomousVehicle",
                                        "true"
                                ),
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
                                                "make",
                                                "Purdue Motor Company",
                                                "model",
                                                "Boilermaker",
                                                "year",
                                                "2025",
                                                "color",
                                                "Black"
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
                Map.of(
                        "isAutonomousVehicle",
                        "true"
                ),
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
                                "make",
                                "Purdue Motor Company",
                                "model",
                                "Boilermaker",
                                "year",
                                "2025",
                                "color",
                                "Black"
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
                                                "make",
                                                "Purdue Motor Company",
                                                "model",
                                                "Boilermaker",
                                                "year",
                                                "2025",
                                                "color",
                                                "Black"
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
                Map.of(
                        "isAutonomousVehicle",
                        "true"
                ),
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
                                "make",
                                "Purdue Motor Company",
                                "model",
                                "Boilermaker",
                                "year",
                                "2025",
                                "color",
                                "Black"
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
                                                "make",
                                                "Purdue Motor Company",
                                                "model",
                                                "Boilermaker",
                                                "year",
                                                "2025",
                                                "color",
                                                "Black"
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
                String.format(
                        "%s_REGISTRATION_COLLECTION",
                        AUTHORIZED_3RD_PARTY_MSP_ID.toUpperCase()
                ),
                "OH-HETZLER",
                objectMapper.writeValueAsBytes(expected)
        );
        assertEquals(
                expected,
                result
        );
    }

    @Test
    void viewRegistrationIn3rdPartyCollectionShouldThrowExceptionBecauseRequestorIsUnauthorized() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn("BessCoMSP");

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewRegistrationIn3rdPartyCollection(
                            mockedContext,
                            "OH-HETZLER"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Unauthorized request."));
    }

    @Test
    void viewRegistrationIn3rdPartyCollectionShouldThrowExceptionBecauseRegistrationNumberIsMissing() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_3RD_PARTY_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewRegistrationIn3rdPartyCollection(
                            mockedContext,
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void viewRegistrationIn3rdPartyCollectionShouldThrowExceptionBecauseRegistrationNumberIsBlank() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_3RD_PARTY_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewRegistrationIn3rdPartyCollection(
                            mockedContext,
                            ""
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void viewRegistrationIn3rdPartyCollectionShouldThrowExceptionBecauseNoRegistrationExists() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_3RD_PARTY_MSP_ID);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                "OH-HETZLER"
        )).thenReturn(null);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewRegistrationIn3rdPartyCollection(
                            mockedContext,
                            "OH-HETZLER"
                    );
                }
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getPrivateData(
                String.format(
                        "%s_REGISTRATION_COLLECTION",
                        AUTHORIZED_3RD_PARTY_MSP_ID.toUpperCase()
                ),
                "OH-HETZLER"
        );
        assertTrue(exception.getMessage().contains("No registration exists for registration number OH-HETZLER."));
    }

    @Test
    void viewRegistrationIn3rdPartyCollectionShouldReturnBecauseRequestorIsAuthorized3rdParty() throws
                                                                                                IOException {
        final RegistrationSchema expected = new RegistrationSchema(
                Map.of(
                        "isAutonomousVehicle",
                        "true"
                ),
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
                                "make",
                                "Purdue Motor Company",
                                "model",
                                "Boilermaker",
                                "year",
                                "2025",
                                "color",
                                "Black"
                        )
                ),
                "1"
        );
        final String pdcName = String.format(
                "%s_REGISTRATION_COLLECTION",
                AUTHORIZED_3RD_PARTY_MSP_ID.toUpperCase()
        );

        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_3RD_PARTY_MSP_ID);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                pdcName,
                "OH-HETZLER"
        )).thenReturn(objectMapper.writeValueAsBytes(expected));

        final RegistrationSchema result = subject.viewRegistrationIn3rdPartyCollection(
                mockedContext,
                "OH-HETZLER"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getPrivateData(
                pdcName,
                "OH-HETZLER"
        );
        assertEquals(
                expected,
                result
        );
    }

    @Test
    void issueRegistrationShouldThrowExceptionBecauseRequestorIsUnauthorized() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn("BessCoMSP");

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueRegistration(
                            mockedContext,
                            null,
                            null,
                            "Jane Doe",
                            "unique-1",
                            "OH-HETZLER",
                            null,
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Unauthorized request."));
    }

    @Test
    void issueRegistrationShouldThrowExceptionBecauseRegistrationNumberIsMissing() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueRegistration(
                            mockedContext,
                            null,
                            null,
                            null,
                            "unique-1",
                            null,
                            null,
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void issueRegistrationShouldThrowExceptionBecauseRegistrationNumberIsBlank() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueRegistration(
                            mockedContext,
                            null,
                            null,
                            "",
                            "unique-1",
                            "",
                            null,
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void issueRegistrationShouldThrowExceptionBecauseUniqueIdIsMissing() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueRegistration(
                            mockedContext,
                            null,
                            null,
                            null,
                            null,
                            "OH-HETZLER",
                            null,
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void issueRegistrationShouldThrowExceptionBecauseUniqueIdIsBlank() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueRegistration(
                            mockedContext,
                            null,
                            null,
                            "",
                            "",
                            "OH-HETZLER",
                            null,
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void issueRegistrationShouldThrowExceptionBecauseSchemaVersionIsMissing() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueRegistration(
                            mockedContext,
                            null,
                            null,
                            "Jane Doe",
                            "unique-1",
                            "OH-HETZLER",
                            null,
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void issueRegistrationShouldThrowExceptionBecauseSchemaVersionIsBlank() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueRegistration(
                            mockedContext,
                            null,
                            null,
                            "Jane Doe",
                            "unique-1",
                            "OH-HETZLER",
                            null,
                            ""
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void issueRegistrationShouldThrowExceptionBecauseSchemaVersionIsNotANumber() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueRegistration(
                            mockedContext,
                            null,
                            null,
                            "Jane Doe",
                            "unique-1",
                            "A",
                            null,
                            "A"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void issueRegistrationShouldIssueBecauseNoRegistrationExists() throws
                                                                   IOException {
        final Map<String, String> other = Map.of(
                "isAutonomousVehicle",
                "true"
        );
        final List<Address> addresses = List.of(
                new Address(
                        "123 University Lane",
                        null,
                        "West Lafayette",
                        "Purdue County",
                        "98765"
                )
        );
        final Map<String, String> vehicleDescriptions = Map.of(
                "make",
                "Purdue Motor Company",
                "model",
                "Boilermaker",
                "year",
                "2025",
                "color",
                "Black"
        );
        final RegistrationSchema expected = new RegistrationSchema(
                other,
                List.of(
                        new Registrant(
                                addresses,
                                "Jane Doe"
                        )
                ),
                new Registration(
                        "OH-HETZLER",
                        vehicleDescriptions
                ),
                "1"
        );

        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                "OH-HETZLER"
        )).thenReturn(null);

        final RegistrationSchema result = subject.issueRegistration(
                mockedContext,
                objectMapper.writeValueAsString(other),
                objectMapper.writeValueAsString(addresses),
                "Jane Doe",
                "unique-1",
                "OH-HETZLER",
                objectMapper.writeValueAsString(vehicleDescriptions),
                "1"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).putPrivateData(
                STATE_COLLECTION,
                "OH-HETZLER",
                objectMapper.writeValueAsBytes(
                        new PersistedRegistrationSchema(
                                other,
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
                                        vehicleDescriptions
                                ),
                                "1"
                        )
                )
        );
        assertEquals(
                expected,
                result
        );
    }

    @Test
    void issueRegistrationShouldIssueBecauseRegistrationExistsButNewRegistrantAdded() throws
                                                                   IOException {
        final Map<String, String> other = Map.of(
                "isAutonomousVehicle",
                "true"
        );
        final List<Address> addresses = List.of(
                new Address(
                        "123 University Lane",
                        null,
                        "West Lafayette",
                        "Purdue County",
                        "98765"
                )
        );
        final Map<String, String> vehicleDescriptions = Map.of(
                "make",
                "Purdue Motor Company",
                "model",
                "Boilermaker",
                "year",
                "2025",
                "color",
                "Black"
        );
        final RegistrationSchema expected = new RegistrationSchema(
                other,
                List.of(
                        new Registrant(
                                addresses,
                                "Jane Doe"
                        ),
                        new Registrant(
                                addresses,
                                "Maya Doe"
                        )
                ),
                new Registration(
                        "OH-HETZLER",
                        vehicleDescriptions
                ),
                "1"
        );

        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                "OH-HETZLER"
        )).thenReturn(
                objectMapper.writeValueAsBytes(
                        new PersistedRegistrationSchema(
                                other,
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
                                        vehicleDescriptions
                                ),
                                "1"
                        )
                )
        );

        final RegistrationSchema result = subject.issueRegistration(
                mockedContext,
                objectMapper.writeValueAsString(other),
                objectMapper.writeValueAsString(addresses),
                "Maya Doe",
                "unique-2",
                "OH-HETZLER",
                objectMapper.writeValueAsString(vehicleDescriptions),
                "1"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).putPrivateData(
                STATE_COLLECTION,
                "OH-HETZLER",
                objectMapper.writeValueAsString(
                        new PersistedRegistrationSchema(
                                other,
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
                                        ),
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
                                                "Maya Doe",
                                                "unique-2"
                                        )
                                ),
                                new PersistedRegistration(
                                        "OH-HETZLER",
                                        vehicleDescriptions
                                ),
                                "1"
                        )
                )
        );
        assertEquals(
                expected,
                result
        );
    }

    @Test
    void issueRegistrationShouldIssueBecauseRegistrationExistsRegistrantDataUpdated() throws
                                                                                      IOException {
        final Map<String, String> other = Map.of(
                "isAutonomousVehicle",
                "true"
        );
        final List<Address> addresses = List.of(
                new Address(
                        "123 University Lane",
                        null,
                        "West Lafayette",
                        "Purdue County",
                        "98765"
                )
        );
        final Map<String, String> vehicleDescriptions = Map.of(
                "make",
                "Purdue Motor Company",
                "model",
                "Boilermaker",
                "year",
                "2025",
                "color",
                "Black"
        );
        final RegistrationSchema expected = new RegistrationSchema(
                other,
                List.of(
                        new Registrant(
                                addresses,
                                "Jane Doe"
                        )
                ),
                new Registration(
                        "OH-HETZLER",
                        vehicleDescriptions
                ),
                "1"
        );

        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                "OH-HETZLER"
        )).thenReturn(
                objectMapper.writeValueAsBytes(
                        new PersistedRegistrationSchema(
                                other,
                                List.of(
                                        new PersistedRegistrant(
                                                List.of(
                                                        new PersistedAddress(
                                                                "856 Teacher Drive",
                                                                "Suite B",
                                                                "College Park",
                                                                "Penn State County",
                                                                "98732"
                                                        )
                                                ),
                                                "Jane Doe",
                                                "unique-1"
                                        )
                                ),
                                new PersistedRegistration(
                                        "OH-HETZLER",
                                        vehicleDescriptions
                                ),
                                "1"
                        )
                )
        );

        final RegistrationSchema result = subject.issueRegistration(
                mockedContext,
                objectMapper.writeValueAsString(other),
                objectMapper.writeValueAsString(addresses),
                "Jane Doe",
                "unique-1",
                "OH-HETZLER",
                objectMapper.writeValueAsString(vehicleDescriptions),
                "1"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).putPrivateData(
                STATE_COLLECTION,
                "OH-HETZLER",
                objectMapper.writeValueAsString(
                        new PersistedRegistrationSchema(
                                other,
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
                                        vehicleDescriptions
                                ),
                                "1"
                        )
                )
        );
        assertEquals(
                expected,
                result
        );
    }

    @Test
    void revokeRegistrationShouldThrowExceptionBecauseRequestorIsUnauthorized() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn("BessCoMSP");

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.revokeRegistration(
                            mockedContext,
                            "OH-HETZLER"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Unauthorized request."));
    }

    @Test
    void revokeRegistrationShouldThrowExceptionBecauseRegistrationNumberIsMissing() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.revokeRegistration(
                            mockedContext,
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void revokeRegistrationShouldThrowExceptionBecauseRegistrationNumberIsBlank() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.revokeRegistration(
                            mockedContext,
                            ""
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void revokeRegistrationShouldThrowExceptionBecauseNoRegistrationExists() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                "OH-HETZLER"
        )).thenReturn(null);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.revokeRegistration(
                            mockedContext,
                            "OH-HETZLER"
                    );
                }
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getPrivateData(
                String.format(STATE_COLLECTION),
                "OH-HETZLER"
        );
        assertTrue(exception.getMessage().contains("No registration exists for registration number OH-HETZLER."));
    }

    @Test
    void revokeRegistrationShouldDeleteRegistration() throws
                                                      IOException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                "OH-HETZLER"
        )).thenReturn(
                objectMapper.writeValueAsBytes(
                        new PersistedRegistrationSchema(
                                Map.of(
                                        "isAutonomousVehicle",
                                        "true"
                                ),
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
                                                "make",
                                                "Purdue Motor Company",
                                                "model",
                                                "Boilermaker",
                                                "year",
                                                "2025",
                                                "color",
                                                "Black"
                                        )
                                ),
                                "1"
                        )
                )
        );

        subject.revokeRegistration(
                mockedContext,
                "OH-HETZLER"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getPrivateData(
                String.format(STATE_COLLECTION),
                "OH-HETZLER"
        );
        verify(
                mockedChaincodeStub,
                times(1)
        ).delPrivateData(
                STATE_COLLECTION,
                "OH-HETZLER"
        );
    }

    @Test
    void cancelRegistrationShouldThrowExceptionBecauseRequestorIsUnauthorized() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn("BessCoMSP");

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.cancelRegistration(
                            mockedContext,
                            "OH-HETZLER"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Unauthorized request."));
    }

    @Test
    void cancelRegistrationShouldThrowExceptionBecauseRegistrationNumberIsMissing() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_REGISTRANT_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.cancelRegistration(
                            mockedContext,
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void cancelRegistrationShouldThrowExceptionBecauseRegistrationNumberIsBlank() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_REGISTRANT_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.cancelRegistration(
                            mockedContext,
                            ""
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void cancelRegistrationShouldThrowExceptionBecauseNoRegistrationExists() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_REGISTRANT_MSP_ID);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                "OH-HETZLER"
        )).thenReturn(null);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.cancelRegistration(
                            mockedContext,
                            "OH-HETZLER"
                    );
                }
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getPrivateData(
                String.format(STATE_COLLECTION),
                "OH-HETZLER"
        );
        assertTrue(exception.getMessage().contains("No registration exists for registration number OH-HETZLER."));
    }

    @Test
    void cancelRegistrationShouldThrowExceptionBecauseTheRequestorIsARegistrantButNotAssociatedWithRgistration() throws
                                                                                                                 JsonProcessingException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_REGISTRANT_MSP_ID);
        when(mockedClientIdentity.getId()).thenReturn("unique-2");
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                "OH-HETZLER"
        )).thenReturn(
                objectMapper.writeValueAsBytes(
                        new PersistedRegistrationSchema(
                                Map.of(
                                        "isAutonomousVehicle",
                                        "true"
                                ),
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
                                                "make",
                                                "Purdue Motor Company",
                                                "model",
                                                "Boilermaker",
                                                "year",
                                                "2025",
                                                "color",
                                                "Black"
                                        )
                                ),
                                "1"
                        )
                )
        );

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.cancelRegistration(
                            mockedContext,
                            "OH-HETZLER"
                    );
                }
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getPrivateData(
                String.format(STATE_COLLECTION),
                "OH-HETZLER"
        );
        assertTrue(exception.getMessage().contains("Unauthorized request."));
    }

    @Test
    void cancelRegistrationShouldDeleteOnlyElement2FromTheRecordBecauseThereAreMultipleRegistrants() throws
                                                                                                     IOException {
        final RegistrationSchema expected = new RegistrationSchema(
                Map.of(
                        "isAutonomousVehicle",
                        "true"
                ),
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
                                "make",
                                "Purdue Motor Company",
                                "model",
                                "Boilermaker",
                                "year",
                                "2025",
                                "color",
                                "Black"
                        )
                ),
                "1"
        );

        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_REGISTRANT_MSP_ID);
        when(mockedClientIdentity.getId()).thenReturn("unique-2");
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                "OH-HETZLER"
        )).thenReturn(
                objectMapper.writeValueAsBytes(
                        new PersistedRegistrationSchema(
                                Map.of(
                                        "isAutonomousVehicle",
                                        "true"
                                ),
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
                                        ),
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
                                                "John Doe",
                                                "unique-2"
                                        )
                                ),
                                new PersistedRegistration(
                                        "OH-HETZLER",
                                        Map.of(
                                                "make",
                                                "Purdue Motor Company",
                                                "model",
                                                "Boilermaker",
                                                "year",
                                                "2025",
                                                "color",
                                                "Black"
                                        )
                                ),
                                "1"
                        )
                )
        );

        RegistrationSchema result = subject.cancelRegistration(
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
                STATE_COLLECTION,
                "OH-HETZLER",
                objectMapper.writeValueAsBytes(
                        new PersistedRegistrationSchema(
                                Map.of(
                                        "isAutonomousVehicle",
                                        "true"
                                ),
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
                                                "make",
                                                "Purdue Motor Company",
                                                "model",
                                                "Boilermaker",
                                                "year",
                                                "2025",
                                                "color",
                                                "Black"
                                        )
                                ),
                                "1"
                        )
                )
        );
        assertEquals(
                expected,
                result
        );
    }

    @Test
    void cancelRegistrationShouldDeleteRecordBecauseThereWasOnly1Registrant() throws
                                                                              IOException {
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
                                Map.of(
                                        "isAutonomousVehicle",
                                        "true"
                                ),
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
                                                "make",
                                                "Purdue Motor Company",
                                                "model",
                                                "Boilermaker",
                                                "year",
                                                "2025",
                                                "color",
                                                "Black"
                                        )
                                ),
                                "1"
                        )
                )
        );

        RegistrationSchema result = subject.cancelRegistration(
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
        ).delPrivateData(
                STATE_COLLECTION,
                "OH-HETZLER"
        );
        assertEquals(
                null,
                result
        );
    }
}
