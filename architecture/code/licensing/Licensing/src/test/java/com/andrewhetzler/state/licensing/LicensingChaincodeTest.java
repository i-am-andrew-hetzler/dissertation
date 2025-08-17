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
    private static final String AUTHORIZED_ISSUEE_MSP_ID = "TestStateDmvLicenseeMSP";
    private static final String AUTHORIZED_STATE_DMV_MSP_IPD = "TestStateDmvMSP";
    private static final String AUTHORIZED_3RD_PARTY_MSP_ID = "TestInsuranceCoMSP";
    private static final String STATE_COLLECTION = "TestStateLicenseCollection";
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
                                "2014"
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
                                                "2014"
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
                                "2014"
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
                                                "2014"
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
                                "2014"
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
                                                "2014"
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
                                "2014"
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
                                                "2014"
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
                                "2014"
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
                                        "2014"
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
                String.format(
                        "%s_LICENSE_COLLECTION",
                        AUTHORIZED_3RD_PARTY_MSP_ID.toUpperCase()
                ),
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
                                "2014"
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
                String.format(
                        "%s_LICENSE_COLLECTION",
                        AUTHORIZED_3RD_PARTY_MSP_ID.toUpperCase()
                ),
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
                String.format(
                        "%s_LICENSE_COLLECTION",
                        AUTHORIZED_3RD_PARTY_MSP_ID.toUpperCase()
                ),
                "IN-123"
        );
        assertEquals(
                expected,
                result
        );
    }

    @Test
    void issueLicenseShouldThrowExceptionBecauseRequestorIsUnauthorized() throws
                                                                          JsonProcessingException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_ISSUEE_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueLicense(
                            mockedContext,
                            objectMapper.writeValueAsString(List.of("A")),
                            "IN-123",
                            objectMapper.writeValueAsString(
                                    List.of(
                                            new Address(
                                                    "123 University Lane",
                                                    null,
                                                    "West Lafayette",
                                                    "IN",
                                                    "98765"
                                            )
                                    )
                            ),
                            "17",
                            "October",
                            "2014",
                            objectMapper.writeValueAsString(Map.of(
                                    "eyeColor",
                                    "Blue"
                            )),
                            "false",
                            "Maya the Husky",
                            "string-encoded byte array here",
                            "string-encoded byte array here",
                            objectMapper.writeValueAsString(Map.of(
                                    "lifetimeHuntingLicense",
                                    "IN-HUNTER-123"
                            )),
                            "1",
                            "unique-1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Unauthorized request."));
    }

    @Test
    void issueLicenseShouldThrowExceptionBecauseLicenseNumberIsMissing() throws
                                                                         JsonProcessingException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueLicense(
                            mockedContext,
                            objectMapper.writeValueAsString(List.of("A")),
                            null,
                            objectMapper.writeValueAsString(
                                    List.of(
                                            new Address(
                                                    "123 University Lane",
                                                    null,
                                                    "West Lafayette",
                                                    "IN",
                                                    "98765"
                                            )
                                    )
                            ),
                            "17",
                            "October",
                            "2014",
                            objectMapper.writeValueAsString(Map.of(
                                    "eyeColor",
                                    "Blue"
                            )),
                            "false",
                            "Maya the Husky",
                            "string-encoded byte array here",
                            "string-encoded byte array here",
                            objectMapper.writeValueAsString(Map.of(
                                    "lifetimeHuntingLicense",
                                    "IN-HUNTER-123"
                            )),
                            "1",
                            "unique-1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void issueLicenseShouldThrowExceptionBecauseLicenseNumberIsBlank() throws
                                                                       JsonProcessingException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueLicense(
                            mockedContext,
                            objectMapper.writeValueAsString(List.of("A")),
                            "",
                            objectMapper.writeValueAsString(
                                    List.of(
                                            new Address(
                                                    "123 University Lane",
                                                    null,
                                                    "West Lafayette",
                                                    "IN",
                                                    "98765"
                                            )
                                    )
                            ),
                            "17",
                            "October",
                            "2014",
                            objectMapper.writeValueAsString(Map.of(
                                    "eyeColor",
                                    "Blue"
                            )),
                            "false",
                            "Maya the Husky",
                            "string-encoded byte array here",
                            "string-encoded byte array here",
                            objectMapper.writeValueAsString(Map.of(
                                    "lifetimeHuntingLicense",
                                    "IN-HUNTER-123"
                            )),
                            "1",
                            "unique-1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void issueLicenseShouldThrowExceptionBecauseAddressesAreMissing() throws
                                                                      JsonProcessingException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueLicense(
                            mockedContext,
                            objectMapper.writeValueAsString(List.of("A")),
                            "IN-123",
                            null,
                            "17",
                            "October",
                            "2014",
                            objectMapper.writeValueAsString(Map.of(
                                    "eyeColor",
                                    "Blue"
                            )),
                            "false",
                            "Maya the Husky",
                            "string-encoded byte array here",
                            "string-encoded byte array here",
                            objectMapper.writeValueAsString(Map.of(
                                    "lifetimeHuntingLicense",
                                    "IN-HUNTER-123"
                            )),
                            "1",
                            "unique-1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void issueLicenseShouldThrowExceptionBecauseAddressesAreBlank() throws
                                                                    JsonProcessingException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueLicense(
                            mockedContext,
                            objectMapper.writeValueAsString(List.of("A")),
                            "",
                            objectMapper.writeValueAsString(
                                    List.of(
                                            new Address(
                                                    "123 University Lane",
                                                    null,
                                                    "West Lafayette",
                                                    "IN",
                                                    "98765"
                                            )
                                    )
                            ),
                            "17",
                            "October",
                            "2014",
                            objectMapper.writeValueAsString(Map.of(
                                    "eyeColor",
                                    "Blue"
                            )),
                            "false",
                            "Maya the Husky",
                            "string-encoded byte array here",
                            "string-encoded byte array here",
                            objectMapper.writeValueAsString(Map.of(
                                    "lifetimeHuntingLicense",
                                    "IN-HUNTER-123"
                            )),
                            "1",
                            "unique-1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void issueLicenseShouldThrowExceptionBecauseBirthDayIsMissing() throws
                                                                    JsonProcessingException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueLicense(
                            mockedContext,
                            objectMapper.writeValueAsString(List.of("A")),
                            "IN-123",
                            objectMapper.writeValueAsString(
                                    List.of(
                                            new Address(
                                                    "123 University Lane",
                                                    null,
                                                    "West Lafayette",
                                                    "IN",
                                                    "98765"
                                            )
                                    )
                            ),
                            null,
                            "October",
                            "2014",
                            objectMapper.writeValueAsString(Map.of(
                                    "eyeColor",
                                    "Blue"
                            )),
                            "false",
                            "Maya the Husky",
                            "string-encoded byte array here",
                            "string-encoded byte array here",
                            objectMapper.writeValueAsString(Map.of(
                                    "lifetimeHuntingLicense",
                                    "IN-HUNTER-123"
                            )),
                            "1",
                            "unique-1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void issueLicenseShouldThrowExceptionBecauseBirthDayIsBlank() throws
                                                                  JsonProcessingException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueLicense(
                            mockedContext,
                            objectMapper.writeValueAsString(List.of("A")),
                            "IN-123",
                            objectMapper.writeValueAsString(
                                    List.of(
                                            new Address(
                                                    "123 University Lane",
                                                    null,
                                                    "West Lafayette",
                                                    "IN",
                                                    "98765"
                                            )
                                    )
                            ),
                            "",
                            "October",
                            "2014",
                            objectMapper.writeValueAsString(Map.of(
                                    "eyeColor",
                                    "Blue"
                            )),
                            "false",
                            "Maya the Husky",
                            "string-encoded byte array here",
                            "string-encoded byte array here",
                            objectMapper.writeValueAsString(Map.of(
                                    "lifetimeHuntingLicense",
                                    "IN-HUNTER-123"
                            )),
                            "1",
                            "unique-1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void issueLicenseShouldThrowExceptionBecauseBirthMonthIsMissing() throws
                                                                      JsonProcessingException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueLicense(
                            mockedContext,
                            objectMapper.writeValueAsString(List.of("A")),
                            "IN-123",
                            objectMapper.writeValueAsString(
                                    List.of(
                                            new Address(
                                                    "123 University Lane",
                                                    null,
                                                    "West Lafayette",
                                                    "IN",
                                                    "98765"
                                            )
                                    )
                            ),
                            "17",
                            null,
                            "2014",
                            objectMapper.writeValueAsString(Map.of(
                                    "eyeColor",
                                    "Blue"
                            )),
                            "false",
                            "Maya the Husky",
                            "string-encoded byte array here",
                            "string-encoded byte array here",
                            objectMapper.writeValueAsString(Map.of(
                                    "lifetimeHuntingLicense",
                                    "IN-HUNTER-123"
                            )),
                            "1",
                            "unique-1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void issueLicenseShouldThrowExceptionBecauseBirthMonthIsBlank() throws
                                                                    JsonProcessingException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueLicense(
                            mockedContext,
                            objectMapper.writeValueAsString(List.of("A")),
                            "IN-123",
                            objectMapper.writeValueAsString(
                                    List.of(
                                            new Address(
                                                    "123 University Lane",
                                                    null,
                                                    "West Lafayette",
                                                    "IN",
                                                    "98765"
                                            )
                                    )
                            ),
                            "17",
                            "",
                            "2014",
                            objectMapper.writeValueAsString(Map.of(
                                    "eyeColor",
                                    "Blue"
                            )),
                            "false",
                            "Maya the Husky",
                            "string-encoded byte array here",
                            "string-encoded byte array here",
                            objectMapper.writeValueAsString(Map.of(
                                    "lifetimeHuntingLicense",
                                    "IN-HUNTER-123"
                            )),
                            "1",
                            "unique-1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void issueLicenseShouldThrowExceptionBecauseBirthYearIsMissing() throws
                                                                     JsonProcessingException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueLicense(
                            mockedContext,
                            objectMapper.writeValueAsString(List.of("A")),
                            "IN-123",
                            objectMapper.writeValueAsString(
                                    List.of(
                                            new Address(
                                                    "123 University Lane",
                                                    null,
                                                    "West Lafayette",
                                                    "IN",
                                                    "98765"
                                            )
                                    )
                            ),
                            "17",
                            "October",
                            null,
                            objectMapper.writeValueAsString(Map.of(
                                    "eyeColor",
                                    "Blue"
                            )),
                            "false",
                            "Maya the Husky",
                            "string-encoded byte array here",
                            "string-encoded byte array here",
                            objectMapper.writeValueAsString(Map.of(
                                    "lifetimeHuntingLicense",
                                    "IN-HUNTER-123"
                            )),
                            "1",
                            "unique-1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void issueLicenseShouldThrowExceptionBecauseNameIsMissing() throws
                                                                JsonProcessingException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueLicense(
                            mockedContext,
                            objectMapper.writeValueAsString(List.of("A")),
                            "IN-123",
                            objectMapper.writeValueAsString(
                                    List.of(
                                            new Address(
                                                    "123 University Lane",
                                                    null,
                                                    "West Lafayette",
                                                    "IN",
                                                    "98765"
                                            )
                                    )
                            ),
                            "17",
                            "October",
                            "2014",
                            objectMapper.writeValueAsString(Map.of(
                                    "eyeColor",
                                    "Blue"
                            )),
                            "false",
                            null,
                            "string-encoded byte array here",
                            "string-encoded byte array here",
                            objectMapper.writeValueAsString(Map.of(
                                    "lifetimeHuntingLicense",
                                    "IN-HUNTER-123"
                            )),
                            "1",
                            "unique-1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void issueLicenseShouldThrowExceptionBecauseNameIsBlank() throws
                                                              JsonProcessingException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueLicense(
                            mockedContext,
                            objectMapper.writeValueAsString(List.of("A")),
                            "IN-123",
                            objectMapper.writeValueAsString(
                                    List.of(
                                            new Address(
                                                    "123 University Lane",
                                                    null,
                                                    "West Lafayette",
                                                    "IN",
                                                    "98765"
                                            )
                                    )
                            ),
                            "17",
                            "October",
                            "2014",
                            objectMapper.writeValueAsString(Map.of(
                                    "eyeColor",
                                    "Blue"
                            )),
                            "false",
                            "",
                            "string-encoded byte array here",
                            "string-encoded byte array here",
                            objectMapper.writeValueAsString(Map.of(
                                    "lifetimeHuntingLicense",
                                    "IN-HUNTER-123"
                            )),
                            "1",
                            "unique-1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void issueLicenseShouldThrowExceptionBecauseBirthYearIsBlank() throws
                                                                   JsonProcessingException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueLicense(
                            mockedContext,
                            objectMapper.writeValueAsString(List.of("A")),
                            "IN-123",
                            objectMapper.writeValueAsString(
                                    List.of(
                                            new Address(
                                                    "123 University Lane",
                                                    null,
                                                    "West Lafayette",
                                                    "IN",
                                                    "98765"
                                            )
                                    )
                            ),
                            "17",
                            "October",
                            "",
                            objectMapper.writeValueAsString(Map.of(
                                    "eyeColor",
                                    "Blue"
                            )),
                            "false",
                            "Maya the Husky",
                            "string-encoded byte array here",
                            "string-encoded byte array here",
                            objectMapper.writeValueAsString(Map.of(
                                    "lifetimeHuntingLicense",
                                    "IN-HUNTER-123"
                            )),
                            "1",
                            "unique-1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void issueLicenseShouldThrowExceptionBecauseSerializedPhotographIsMissing() throws
                                                                                JsonProcessingException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueLicense(
                            mockedContext,
                            objectMapper.writeValueAsString(List.of("A")),
                            "IN-123",
                            objectMapper.writeValueAsString(
                                    List.of(
                                            new Address(
                                                    "123 University Lane",
                                                    null,
                                                    "West Lafayette",
                                                    "IN",
                                                    "98765"
                                            )
                                    )
                            ),
                            "17",
                            "October",
                            "2014",
                            objectMapper.writeValueAsString(Map.of(
                                    "eyeColor",
                                    "Blue"
                            )),
                            "false",
                            "Maya the Husky",
                            "",
                            "string-encoded byte array here",
                            objectMapper.writeValueAsString(Map.of(
                                    "lifetimeHuntingLicense",
                                    "IN-HUNTER-123"
                            )),
                            "1",
                            "unique-1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void issueLicenseShouldThrowExceptionBecauseSerializedPhotographIsBlank() throws
                                                                              JsonProcessingException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueLicense(
                            mockedContext,
                            objectMapper.writeValueAsString(List.of("A")),
                            "IN-123",
                            objectMapper.writeValueAsString(
                                    List.of(
                                            new Address(
                                                    "123 University Lane",
                                                    null,
                                                    "West Lafayette",
                                                    "IN",
                                                    "98765"
                                            )
                                    )
                            ),
                            "17",
                            "October",
                            "2014",
                            objectMapper.writeValueAsString(Map.of(
                                    "eyeColor",
                                    "Blue"
                            )),
                            "false",
                            "Maya the Husky",
                            "",
                            "string-encoded byte array here",
                            objectMapper.writeValueAsString(Map.of(
                                    "lifetimeHuntingLicense",
                                    "IN-HUNTER-123"
                            )),
                            "1",
                            "unique-1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void issueLicenseShouldThrowExceptionBecauseSerializedSignatureIsMissing() throws
                                                                               JsonProcessingException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueLicense(
                            mockedContext,
                            objectMapper.writeValueAsString(List.of("A")),
                            "IN-123",
                            objectMapper.writeValueAsString(
                                    List.of(
                                            new Address(
                                                    "123 University Lane",
                                                    null,
                                                    "West Lafayette",
                                                    "IN",
                                                    "98765"
                                            )
                                    )
                            ),
                            "17",
                            "October",
                            "2014",
                            objectMapper.writeValueAsString(Map.of(
                                    "eyeColor",
                                    "Blue"
                            )),
                            "false",
                            "Maya the Husky",
                            "string-encoded byte array here",
                            null,
                            objectMapper.writeValueAsString(Map.of(
                                    "lifetimeHuntingLicense",
                                    "IN-HUNTER-123"
                            )),
                            "1",
                            "unique-1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void issueLicenseShouldThrowExceptionBecauseSerializedSignatureIsBlank() throws
                                                                             JsonProcessingException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueLicense(
                            mockedContext,
                            objectMapper.writeValueAsString(List.of("A")),
                            "IN-123",
                            objectMapper.writeValueAsString(
                                    List.of(
                                            new Address(
                                                    "123 University Lane",
                                                    null,
                                                    "West Lafayette",
                                                    "IN",
                                                    "98765"
                                            )
                                    )
                            ),
                            "17",
                            "October",
                            "2014",
                            objectMapper.writeValueAsString(Map.of(
                                    "eyeColor",
                                    "Blue"
                            )),
                            "false",
                            "Maya the Husky",
                            "string-encoded byte array here",
                            "",
                            objectMapper.writeValueAsString(Map.of(
                                    "lifetimeHuntingLicense",
                                    "IN-HUNTER-123"
                            )),
                            "1",
                            "unique-1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void issueLicenseShouldThrowExceptionBecauseSchemaVersionIsMissing() throws
                                                                         JsonProcessingException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueLicense(
                            mockedContext,
                            objectMapper.writeValueAsString(List.of("A")),
                            "IN-123",
                            objectMapper.writeValueAsString(
                                    List.of(
                                            new Address(
                                                    "123 University Lane",
                                                    null,
                                                    "West Lafayette",
                                                    "IN",
                                                    "98765"
                                            )
                                    )
                            ),
                            "17",
                            "October",
                            "2014",
                            objectMapper.writeValueAsString(Map.of(
                                    "eyeColor",
                                    "Blue"
                            )),
                            "false",
                            "Maya the Husky",
                            "string-encoded byte array here",
                            "string-encoded byte array here",
                            objectMapper.writeValueAsString(Map.of(
                                    "lifetimeHuntingLicense",
                                    "IN-HUNTER-123"
                            )),
                            null,
                            "unique-1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void issueLicenseShouldThrowExceptionBecauseSchemaVersionsBlank() throws
                                                                      JsonProcessingException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueLicense(
                            mockedContext,
                            objectMapper.writeValueAsString(List.of("A")),
                            "IN-123",
                            objectMapper.writeValueAsString(
                                    List.of(
                                            new Address(
                                                    "123 University Lane",
                                                    null,
                                                    "West Lafayette",
                                                    "IN",
                                                    "98765"
                                            )
                                    )
                            ),
                            "17",
                            "October",
                            "2014",
                            objectMapper.writeValueAsString(Map.of(
                                    "eyeColor",
                                    "Blue"
                            )),
                            "false",
                            "Maya the Husky",
                            "string-encoded byte array here",
                            "string-encoded byte array here",
                            objectMapper.writeValueAsString(Map.of(
                                    "lifetimeHuntingLicense",
                                    "IN-HUNTER-123"
                            )),
                            "",
                            "unique-1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void issueLicenseShouldThrowExceptionBecauseSchemaVersionsIsNotANumber() throws
                                                                             JsonProcessingException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueLicense(
                            mockedContext,
                            objectMapper.writeValueAsString(List.of("A")),
                            "IN-123",
                            objectMapper.writeValueAsString(
                                    List.of(
                                            new Address(
                                                    "123 University Lane",
                                                    null,
                                                    "West Lafayette",
                                                    "IN",
                                                    "98765"
                                            )
                                    )
                            ),
                            "17",
                            "October",
                            "2014",
                            objectMapper.writeValueAsString(Map.of(
                                    "eyeColor",
                                    "Blue"
                            )),
                            "false",
                            "Maya the Husky",
                            "string-encoded byte array here",
                            "string-encoded byte array here",
                            objectMapper.writeValueAsString(Map.of(
                                    "lifetimeHuntingLicense",
                                    "IN-HUNTER-123"
                            )),
                            "A",
                            "unique-1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void issueLicenseShouldThrowExceptionBecauseLicenseeIdIsMissing() throws
                                                                      JsonProcessingException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueLicense(
                            mockedContext,
                            objectMapper.writeValueAsString(List.of("A")),
                            "IN-123",
                            objectMapper.writeValueAsString(
                                    List.of(
                                            new Address(
                                                    "123 University Lane",
                                                    null,
                                                    "West Lafayette",
                                                    "IN",
                                                    "98765"
                                            )
                                    )
                            ),
                            "17",
                            "October",
                            "2014",
                            objectMapper.writeValueAsString(Map.of(
                                    "eyeColor",
                                    "Blue"
                            )),
                            "false",
                            "Maya the Husky",
                            "string-encoded byte array here",
                            "string-encoded byte array here",
                            objectMapper.writeValueAsString(Map.of(
                                    "lifetimeHuntingLicense",
                                    "IN-HUNTER-123"
                            )),
                            "1",
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void issueLicenseShouldThrowExceptionBecauseLicenseeIdIsBlank() throws
                                                                    JsonProcessingException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.issueLicense(
                            mockedContext,
                            objectMapper.writeValueAsString(List.of("A")),
                            "IN-123",
                            objectMapper.writeValueAsString(
                                    List.of(
                                            new Address(
                                                    "123 University Lane",
                                                    null,
                                                    "West Lafayette",
                                                    "IN",
                                                    "98765"
                                            )
                                    )
                            ),
                            "17",
                            "October",
                            "2014",
                            objectMapper.writeValueAsString(Map.of(
                                    "eyeColor",
                                    "Blue"
                            )),
                            "false",
                            "Maya the Husky",
                            "string-encoded byte array here",
                            "string-encoded byte array here",
                            objectMapper.writeValueAsString(Map.of(
                                    "lifetimeHuntingLicense",
                                    "IN-HUNTER-123"
                            )),
                            "1",
                            ""
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void issueLicenseShouldReturnLicense() throws
                                           JsonProcessingException {
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
                                "2014"
                        ),
                        Map.of(
                                "eyeColor",
                                "Blue"
                        ),
                        "false",
                        "Maya the Husky",
                        "string-encoded byte array here",
                        "string-encoded byte array here"
                ),
                Map.of(
                        "lifetimeHuntingLicense",
                        "IN-HUNTER-123"
                ),
                "1"
        );

        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final LicenseSchema result = subject.issueLicense(
                mockedContext,
                objectMapper.writeValueAsString(List.of("A")),
                "IN-123",
                objectMapper.writeValueAsString(
                        List.of(
                                new Address(
                                        "123 University Lane",
                                        null,
                                        "West Lafayette",
                                        "IN",
                                        "98765"
                                )
                        )
                ),
                "17",
                "October",
                "2014",
                objectMapper.writeValueAsString(Map.of(
                        "eyeColor",
                        "Blue"
                )),
                "false",
                "Maya the Husky",
                "string-encoded byte array here",
                "string-encoded byte array here",
                objectMapper.writeValueAsString(Map.of(
                        "lifetimeHuntingLicense",
                        "IN-HUNTER-123"
                )),
                "1",
                "unique-1"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).putPrivateData(
                STATE_COLLECTION,
                "IN-123",
                objectMapper.writeValueAsBytes(
                        new PersistedLicenseSchema(
                                new PersistedLicense(
                                        expected.getLicense().getClasses(),
                                        expected.getLicenseNumber()
                                ),
                                new PersistedLicensee(
                                        expected.getLicensee().getAddresses().stream().map(address -> new PersistedAddress(
                                                address.getStreet1(),
                                                address.getStreet2(),
                                                address.getCity(),
                                                address.getState(),
                                                address.getZipCode()
                                        )).toList(),
                                        new PersistedBirthdate(
                                                expected.getLicensee().getBirthdate().getDay(),
                                                expected.getLicensee().getBirthdate().getMonth(),
                                                expected.getLicensee().getBirthdate().getYear()
                                        ),
                                        expected.getLicensee().getDescription(),
                                        expected.getLicensee().getIsVeteran(),
                                        expected.getLicensee().getName(),
                                        expected.getLicensee().getPhotograph(),
                                        expected.getLicensee().getSignature(),
                                        "unique-1"
                                ),
                                expected.getOther(),
                                expected.getSchemaVersion()
                        )
                )
        );
        assertEquals(
                expected,
                result
        );
    }

    @Test
    void revokeLicenseShouldThrowExceptionBecauseRequestorIsUnauthorized() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_ISSUEE_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.revokeLicense(
                            mockedContext,
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Unauthorized request."));
    }

    @Test
    void revokeLicenseShouldThrowExceptionBecauseLicenseNumberIsMissing() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.revokeLicense(
                            mockedContext,
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void revokeLicenseShouldThrowExceptionBecauseLicenseNumberIsBlank() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.revokeLicense(
                            mockedContext,
                            ""
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void revokeLicenseShouldThrowExceptionBecauseNoLicenseExistsForSpecifiedLicenseNumber() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                "OH-ABC123"
        )).thenReturn(null);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.revokeLicense(
                            mockedContext,
                            "OH-abc123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("No license exists for license number OH-abc123."));
    }

    @Test
    void revokeLicenseShouldDeleteLicense() throws
                                            IOException {
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
                                        "2014"
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
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                "IN-123"
        )).thenReturn(persistedLicense);

        subject.revokeLicense(
                mockedContext,
                "IN-123"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).delPrivateData(
                STATE_COLLECTION,
                "IN-123"
        );
    }

    @Test
    void cancelLicenseShouldThrowExceptionBecauseRequestorIsUnauthorized() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.revokeLicense(
                            mockedContext,
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Unauthorized request."));
    }

    @Test
    void cancelLicenseShouldThrowExceptionBecauseLicenseNumberIsMissing() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_ISSUEE_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.cancelLicense(
                            mockedContext,
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void cancelLicenseShouldThrowExceptionBecauseLicenseNumberIsBlank() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_ISSUEE_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.cancelLicense(
                            mockedContext,
                            ""
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void cancelLicenseShouldThrowExceptionBecauseNoLicenseExistsForSpecifiedLicenseNumber() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_ISSUEE_MSP_ID);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                "OH-ABC123"
        )).thenReturn(null);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.cancelLicense(
                            mockedContext,
                            "OH-abc123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("No license exists for license number OH-abc123."));
    }

    @Test
    void cancelLicenseShouldThrowExceptionBecauseRequestorIsALicenseeButNotTheActualLicensee() throws
                                                                                               JsonProcessingException {
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
                                        "2014"
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
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_ISSUEE_MSP_ID);
        when(mockedClientIdentity.getId()).thenReturn("unique-2");
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                "IN-123"
        )).thenReturn(persistedLicense);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.cancelLicense(
                            mockedContext,
                            "IN-123"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Unauthorized request."));
    }


    @Test
    void cancelLicenseShouldDeleteLicense() throws
                                            IOException {
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
                                        "2014"
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
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_ISSUEE_MSP_ID);
        when(mockedClientIdentity.getId()).thenReturn("unique-1");
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                "IN-123"
        )).thenReturn(persistedLicense);

        subject.cancelLicense(
                mockedContext,
                "IN-123"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).delPrivateData(
                STATE_COLLECTION,
                "IN-123"
        );
    }
}
