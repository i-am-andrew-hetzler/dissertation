package com.andrewhetzler.state.licensing;

import com.andrewhetzler.state.licensing.model.Address;
import com.andrewhetzler.state.licensing.model.License;
import com.andrewhetzler.state.licensing.model.LicenseSchema;
import com.andrewhetzler.state.licensing.model.Licensee;
import com.andrewhetzler.state.licensing.model.persisted.PersistedAddress;
import com.andrewhetzler.state.licensing.model.persisted.PersistedBirthdate;
import com.andrewhetzler.state.licensing.model.persisted.PersistedLicense;
import com.andrewhetzler.state.licensing.model.persisted.PersistedLicenseSchema;
import com.andrewhetzler.state.licensing.model.persisted.PersistedLicensee;
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
 * Date Created: 8/17/25
 **/
class LicensingChaincodeTest {
    @Mock
    private Context mockedContext;
    @Mock
    private ChaincodeStub mockedChaincodeStub;
    @Mock
    private ClientIdentity mockedClientIdentity;
    private LicensingChaincode subject = new LicensingChaincode();
    private static final String AUTHORIZED_STATE_MSP_ID = "TestStateMSP";
    private static final String AUTHORIZED_ISSUEE_MSP_ID = "TestStateDmvMSP";
    private static final String AUTHORIZED_3RD_PARTY_MSP_ID = "TestInsuranceCoMSP";
    private static final String STATE_COLLECTION = "TestStateLicenseCollection";
    private static final String TEST_INSURANCE_CO_COLLECTION = "TestInsuranceCoCollection";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        openMocks(this);

        subject = new LicensingChaincode();
    }

    @Test
    void viewLicenseShouldThrowExceptionBecauseRequestorIsUnauthorized() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn("BessCoMSP");

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewLicense(
                            mockedContext,
                            "OH-ABC123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Unauthorized request."));
    }

    @Test
    void viewLicenseShouldThrowExceptionBecauseLicenseNumberIsMissing() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewLicense(
                            mockedContext,
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void viewLicenseShouldThrowExceptionBecauseLicenseNumberIsBlank() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewLicense(
                            mockedContext,
                            ""
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void viewLicenseShouldThrowExceptionBecauseNoLicenseExistsForSpecifiedLicenseNumber() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_MSP_ID);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                "OH-ABC123"
        )).thenReturn(null);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewLicense(
                            mockedContext,
                            "OH-abc123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("No license exists for license number OH-abc123."));
    }

    @Test
    void viewLicenseShouldReturnLicenseBecauseRequestorIsFromStateMSPWithNoAddress() throws
                                                                                     IOException {
        final LicenseSchema expected = new LicenseSchema(
                new License(
                        List.of("A"),
                        "IN-123"
                ),
                new Licensee(
                        List.of(),
                        new Birthdate(
                                "17",
                                "October",
                                "2017"
                        ),
                        Map.of(
                                "eyeColor",
                                "Blue",
                                "weight",
                                "40 ls"
                        ),
                        "false",
                        "Maya",
                        "string-encoded byte array here",
                        "string-encoded byte array here"
                ),
                Map.of(
                        "isPurdueStudent",
                        "true"
                ),
                "1"
        );

        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_MSP_ID);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                "IN-123"
        )).thenReturn(
                objectMapper.writeValueAsBytes(
                        new PersistedLicenseSchema(
                                new PersistedLicense(
                                        List.of("A"),
                                        "IN-123"
                                ),
                                new PersistedLicensee(
                                        List.of(),
                                        new PersistedBirthdate(
                                                "17",
                                                "October",
                                                "2017"
                                        ),
                                        Map.of(
                                                "eyeColor",
                                                "Blue",
                                                "weight",
                                                "40 ls"
                                        ),
                                        "false",
                                        "Maya",
                                        "string-encoded byte array here",
                                        "string-encoded byte array here",
                                        "unique-1"
                                ),
                                Map.of(
                                        "isPurdueStudent",
                                        "true"
                                ),
                                "1"
                        )
                )
        );

        final LicenseSchema result = subject.viewLicense(
                mockedContext,
                "IN-123"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getPrivateData(
                STATE_COLLECTION,
                "IN-123"
        );
        assertEquals(
                expected,
                result
        );
    }

    @Test
    void viewLicenseShouldReturnLicenseBecauseRequestorIsFromStateMSPWithAddress() throws
                                                                                   IOException {
        final LicenseSchema expected = new LicenseSchema(
                new License(
                        List.of("A"),
                        "IN-123"
                ),
                new Licensee(
                        List.of(
                                new Address(
                                        "123 University Lane",
                                        null,
                                        "West Lafayette",
                                        "IN",
                                        "98765"
                                )
                        ),
                        new Birthdate(
                                "17",
                                "October",
                                "2017"
                        ),
                        Map.of(
                                "eyeColor",
                                "Blue",
                                "weight",
                                "40 ls"
                        ),
                        "false",
                        "Maya",
                        "string-encoded byte array here",
                        "string-encoded byte array here"
                ),
                Map.of(
                        "isPurdueStudent",
                        "true"
                ),
                "1"
        );

        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_MSP_ID);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                "IN-123"
        )).thenReturn(
                objectMapper.writeValueAsBytes(
                        new PersistedLicenseSchema(
                                new PersistedLicense(
                                        List.of("A"),
                                        "IN-123"
                                ),
                                new PersistedLicensee(
                                        List.of(
                                                new PersistedAddress(
                                                        "123 University Lane",
                                                        null,
                                                        "West Lafayette",
                                                        "IN",
                                                        "98765"
                                                )
                                        ),
                                        new PersistedBirthdate(
                                                "17",
                                                "October",
                                                "2017"
                                        ),
                                        Map.of(
                                                "eyeColor",
                                                "Blue",
                                                "weight",
                                                "40 ls"
                                        ),
                                        "false",
                                        "Maya",
                                        "string-encoded byte array here",
                                        "string-encoded byte array here",
                                        "unique-1"
                                ),
                                Map.of(
                                        "isPurdueStudent",
                                        "true"
                                ),
                                "1"
                        )
                )
        );

        final LicenseSchema result = subject.viewLicense(
                mockedContext,
                "IN-123"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getPrivateData(
                STATE_COLLECTION,
                "IN-123"
        );
        assertEquals(
                expected,
                result
        );
    }

    @Test
    void viewLicenseShouldThrowExceptionBecauseRequestorHasCertIssuedByDmvButIsNotTheActualLicensee() throws
                                                                                                      JsonProcessingException {
        final LicenseSchema expected = new LicenseSchema(
                new License(
                        List.of("A"),
                        "IN-123"
                ),
                new Licensee(
                        List.of(),
                        new Birthdate(
                                "17",
                                "October",
                                "2017"
                        ),
                        Map.of(
                                "eyeColor",
                                "Blue",
                                "weight",
                                "40 ls"
                        ),
                        "false",
                        "Maya",
                        "string-encoded byte array here",
                        "string-encoded byte array here"
                ),
                Map.of(
                        "isPurdueStudent",
                        "true"
                ),
                "1"
        );

        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_ISSUEE_MSP_ID);
        when(mockedClientIdentity.getId()).thenReturn("unique-2");
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                "IN-123"
        )).thenReturn(
                objectMapper.writeValueAsBytes(
                        new PersistedLicenseSchema(
                                new PersistedLicense(
                                        List.of("A"),
                                        "IN-123"
                                ),
                                new PersistedLicensee(
                                        List.of(),
                                        new PersistedBirthdate(
                                                "17",
                                                "October",
                                                "2017"
                                        ),
                                        Map.of(
                                                "eyeColor",
                                                "Blue",
                                                "weight",
                                                "40 ls"
                                        ),
                                        "false",
                                        "Maya",
                                        "string-encoded byte array here",
                                        "string-encoded byte array here",
                                        "unique-1"
                                ),
                                Map.of(
                                        "isPurdueStudent",
                                        "true"
                                ),
                                "1"
                        )
                )
        );

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewLicense(
                            mockedContext,
                            "IN-123"
                    );
                }
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getPrivateData(
                STATE_COLLECTION,
                "IN-123"
        );
        assertTrue(exception.getMessage().contains("Unauthorized request."));
    }

    @Test
    void viewLicenseShouldReturnLicenseBecauseRequestorIsTheActualLicensee() throws
                                                                             IOException {
        final LicenseSchema expected = new LicenseSchema(
                new License(
                        List.of("A"),
                        "IN-123"
                ),
                new Licensee(
                        List.of(),
                        new Birthdate(
                                "17",
                                "October",
                                "2017"
                        ),
                        Map.of(
                                "eyeColor",
                                "Blue",
                                "weight",
                                "40 ls"
                        ),
                        "false",
                        "Maya",
                        "string-encoded byte array here",
                        "string-encoded byte array here"
                ),
                Map.of(
                        "isPurdueStudent",
                        "true"
                ),
                "1"
        );

        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_ISSUEE_MSP_ID);
        when(mockedClientIdentity.getId()).thenReturn("unique-1");
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                "IN-123"
        )).thenReturn(
                objectMapper.writeValueAsBytes(
                        new PersistedLicenseSchema(
                                new PersistedLicense(
                                        List.of("A"),
                                        "IN-123"
                                ),
                                new PersistedLicensee(
                                        List.of(),
                                        new PersistedBirthdate(
                                                "17",
                                                "October",
                                                "2017"
                                        ),
                                        Map.of(
                                                "eyeColor",
                                                "Blue",
                                                "weight",
                                                "40 ls"
                                        ),
                                        "false",
                                        "Maya",
                                        "string-encoded byte array here",
                                        "string-encoded byte array here",
                                        "unique-1"
                                ),
                                Map.of(
                                        "isPurdueStudent",
                                        "true"
                                ),
                                "1"
                        )
                )
        );

        final LicenseSchema result = subject.viewLicense(
                mockedContext,
                "IN-123"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getPrivateData(
                STATE_COLLECTION,
                "IN-123"
        );
        assertEquals(
                expected,
                result
        );
    }

    @Test
    void viewLicenseShouldReturnLicenseAndSaveToPdcBecauseRequestorIsAnAuthorized3rdParty() throws
                                                                                            IOException {
        final LicenseSchema expected = new LicenseSchema(
                new License(
                        List.of("A"),
                        "IN-123"
                ),
                new Licensee(
                        List.of(),
                        new Birthdate(
                                "17",
                                "October",
                                "2017"
                        ),
                        Map.of(
                                "eyeColor",
                                "Blue",
                                "weight",
                                "40 ls"
                        ),
                        "false",
                        "Maya",
                        "string-encoded byte array here",
                        "string-encoded byte array here"
                ),
                Map.of(
                        "isPurdueStudent",
                        "true"
                ),
                "1"
        );
        final byte[] persistedLicense = objectMapper.writeValueAsBytes(
                new PersistedLicenseSchema(
                        new PersistedLicense(
                                List.of("A"),
                                "IN-123"
                        ),
                        new PersistedLicensee(
                                List.of(),
                                new PersistedBirthdate(
                                        "17",
                                        "October",
                                        "2017"
                                ),
                                Map.of(
                                        "eyeColor",
                                        "Blue",
                                        "weight",
                                        "40 ls"
                                ),
                                "false",
                                "Maya",
                                "string-encoded byte array here",
                                "string-encoded byte array here",
                                "unique-1"
                        ),
                        Map.of(
                                "isPurdueStudent",
                                "true"
                        ),
                        "1"
                )
        );

        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_3RD_PARTY_MSP_ID);
        when(mockedClientIdentity.getId()).thenReturn("unique-3");
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                "IN-123"
        )).thenReturn(persistedLicense);

        final LicenseSchema result = subject.viewLicense(
                mockedContext,
                "IN-123"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getPrivateData(
                STATE_COLLECTION,
                "IN-123"
        );
        verify(
                mockedChaincodeStub,
                times(1)
        ).putPrivateData(
                String.format("%s_LICENSE_COLLECTION", AUTHORIZED_3RD_PARTY_MSP_ID.toUpperCase()),
                "IN-123",
                objectMapper.writeValueAsBytes(expected)
        );
        assertEquals(
                expected,
                result
        );
    }

    @Test
    void viewLicenseIn3rdPartyCollectionShouldThrowExceptionBecauseRequestorIsUnauthorized() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn("BessCoMSP");

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewLicenseIn3rdPartyCollection(
                            mockedContext,
                            "OH-ABC123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Unauthorized request."));
    }

    @Test
    void viewLicenseIn3rdPartyCollectionShouldThrowExceptionBecauseLicenseNumberIsMissing() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_3RD_PARTY_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewLicenseIn3rdPartyCollection(
                            mockedContext,
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void viewLicenseIn3rdPartyCollectionShouldThrowExceptionBecauseLicenseNumberIsBlank() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_3RD_PARTY_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewLicenseIn3rdPartyCollection(
                            mockedContext,
                            ""
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void viewLicenseIn3rdPartyCollectionShouldThrowExceptionBecauseNoLicenseExistsForSpecifiedLicenseNumber() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_3RD_PARTY_MSP_ID);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                "OH-ABC123"
        )).thenReturn(null);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewLicenseIn3rdPartyCollection(
                            mockedContext,
                            "OH-abc123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("No license exists for license number OH-abc123."));
    }

    @Test
    void viewLicenseIn3rdPartyCollectionShouldReturnLicenseBecauseRequestorIsInThe3rdPartyMSPAndLicenseExists() throws
                                                                             IOException {
        final LicenseSchema expected = new LicenseSchema(
                new License(
                        List.of("A"),
                        "IN-123"
                ),
                new Licensee(
                        List.of(),
                        new Birthdate(
                                "17",
                                "October",
                                "2017"
                        ),
                        Map.of(
                                "eyeColor",
                                "Blue",
                                "weight",
                                "40 ls"
                        ),
                        "false",
                        "Maya",
                        "string-encoded byte array here",
                        "string-encoded byte array here"
                ),
                Map.of(
                        "isPurdueStudent",
                        "true"
                ),
                "1"
        );

        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_3RD_PARTY_MSP_ID);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                String.format("%s_LICENSE_COLLECTION", AUTHORIZED_3RD_PARTY_MSP_ID.toUpperCase()),
                "IN-123"
        )).thenReturn(objectMapper.writeValueAsBytes(expected));

        final LicenseSchema result = subject.viewLicenseIn3rdPartyCollection(
                mockedContext,
                "IN-123"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getPrivateData(
                String.format("%s_LICENSE_COLLECTION", AUTHORIZED_3RD_PARTY_MSP_ID.toUpperCase()),
                "IN-123"
        );
        assertEquals(
                expected,
                result
        );
    }
}
