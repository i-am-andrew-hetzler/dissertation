package com.andrewhetzler.state.pofr;

import com.andrewhetzler.state.pofr.model.Insurance;
import com.andrewhetzler.state.pofr.model.Insured;
import com.andrewhetzler.state.pofr.model.Policy;
import com.andrewhetzler.state.pofr.model.Proof;
import com.andrewhetzler.state.pofr.model.persisted.PersistedCertificateOfDeposit;
import com.andrewhetzler.state.pofr.model.persisted.PersistedInsurance;
import com.andrewhetzler.state.pofr.model.persisted.PersistedInsured;
import com.andrewhetzler.state.pofr.model.persisted.PersistedPolicy;
import com.andrewhetzler.state.pofr.model.persisted.PersistedProof;
import com.andrewhetzler.state.pofr.model.persisted.PersistedSelfInsurer;
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
 * Date Created: 8/19/25
 **/
class ProofOfFinancialResponsibilityChaincodeTest {
    @Mock
    private Context mockedContext;
    @Mock
    private ChaincodeStub mockedChaincodeStub;
    @Mock
    private ClientIdentity mockedClientIdentity;
    private ProofOfFinancialResponsibilityChaincode subject;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String AUTHORIZED_STATE_MSP_ID = "TestStateMSP";
    private static final String AUTHORIZED_INSURED_MSP_ID = "TestStateDmvInsuredMSP";
    private static final String AUTHORIZED_STATE_DMV_MSP_IPD = "TestStateDmvMSP";
    private static final String AUTHORIZED_3RD_PARTY_MSP_ID = "TestInsuranceCoMSP";
    private static final String STATE_COLLECTION = "TestStateProofCollection";
    private static final String REGISTRATION_NUMBER = "OH-123";

    @BeforeEach
    void setUp() {
        openMocks(this);

        subject = new ProofOfFinancialResponsibilityChaincode();
    }

    @Test
    void viewProofShouldThrowExceptionBecauseRequestorIsUnauthorized() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn("BessCoMSP");

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewProof(
                            mockedContext,
                            REGISTRATION_NUMBER
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Unauthorized request."));
    }

    @Test
    void viewProofShouldThrowExceptionBecauseRegistrationNumberIsMissing() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewProof(
                            mockedContext,
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void viewProofShouldThrowExceptionBecauseRegistrationNumberIsBlank() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewProof(
                            mockedContext,
                            ""
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void viewProofShouldThrowExceptionBecauseNoPOFRFound() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_MSP_ID);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER
        )).thenReturn(null);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewProof(
                            mockedContext,
                            REGISTRATION_NUMBER
                    );
                }
        );

        assertTrue(exception.getMessage().contains("No proof of financial responsibility exists for registration number OH-123"));
    }

    @Test
    void viewProofShouldReturnProofBecauseRequestorIsAStateAgent() throws
                                                                   IOException {
        final Proof expected = new Proof(
                List.of(),
                new Insurance(
                        Map.of(
                                "make",
                                "Purdue Motor Company",
                                "model",
                                "Boilermaker",
                                "year",
                                "2024"
                        ),
                        List.of(
                                new Insured("Maya Husky")
                        ),
                        new Policy(
                                "01/01/25",
                                "12/31/25",
                                "Boilermaker Insurance",
                                "ABC123"
                        )
                ),
                "1",
                null
        );
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_MSP_ID);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER
        )).thenReturn(objectMapper.writeValueAsBytes(expected));

        final Proof result = subject.viewProof(
                mockedContext,
                REGISTRATION_NUMBER
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER
        );
        assertEquals(
                expected,
                result
        );
    }

    @Test
    void viewProofShouldReturnProofBecauseRequestorIsAnAuthorized3rdParty() throws
                                                                            IOException {
        final Proof expected = new Proof(
                List.of(),
                new Insurance(
                        Map.of(
                                "make",
                                "Purdue Motor Company",
                                "model",
                                "Boilermaker",
                                "year",
                                "2024"
                        ),
                        List.of(
                                new Insured("Maya Husky")
                        ),
                        new Policy(
                                "01/01/25",
                                "12/31/25",
                                "Boilermaker Insurance",
                                "ABC123"
                        )
                ),
                "1",
                null
        );
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_3RD_PARTY_MSP_ID);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER
        )).thenReturn(objectMapper.writeValueAsBytes(expected));

        final Proof result = subject.viewProof(
                mockedContext,
                REGISTRATION_NUMBER
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER
        );
        verify(
                mockedChaincodeStub,
                times(1)
        ).putPrivateData(
                String.format(
                        "%s_PROOF_COLLECTION",
                        AUTHORIZED_3RD_PARTY_MSP_ID.toUpperCase()
                ),
                REGISTRATION_NUMBER.toUpperCase(),
                objectMapper.writeValueAsBytes(expected)
        );
        assertEquals(
                expected,
                result
        );
    }

    @Test
    void viewProofIn3rdPartyCollectionShouldThrowExceptionBecauseRequestorIsUnauthorized() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn("BessCoMSP");

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewProofIn3rdPartyCollection(
                            mockedContext,
                            REGISTRATION_NUMBER
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Unauthorized request."));
    }

    @Test
    void viewProofIn3rdPartyCollectionShouldThrowExceptionBecauseRegistrationNumberIsMissing() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_3RD_PARTY_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewProofIn3rdPartyCollection(
                            mockedContext,
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void viewProofIn3rdPartyCollectionShouldThrowExceptionBecauseRegistrationNumberIsBlank() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_3RD_PARTY_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewProofIn3rdPartyCollection(
                            mockedContext,
                            ""
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void viewProofIn3rdPartyCollectionShouldThrowExceptionBecauseNoPOFRFound() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_3RD_PARTY_MSP_ID);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER
        )).thenReturn(null);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewProofIn3rdPartyCollection(
                            mockedContext,
                            REGISTRATION_NUMBER
                    );
                }
        );

        assertTrue(exception.getMessage().contains("No proof of financial responsibility exists for registration number OH-123"));
    }

    @Test
    void viewProofIn3rdPartyCollectionShouldReturnProofBecauseRequestorIsAnAuthorized3rdParty() throws
                                                                                                IOException {
        final Proof expected = new Proof(
                List.of(),
                new Insurance(
                        Map.of(
                                "make",
                                "Purdue Motor Company",
                                "model",
                                "Boilermaker",
                                "year",
                                "2024"
                        ),
                        List.of(
                                new Insured("Maya Husky")
                        ),
                        new Policy(
                                "01/01/25",
                                "12/31/25",
                                "Boilermaker Insurance",
                                "ABC123"
                        )
                ),
                "1",
                null
        );
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_3RD_PARTY_MSP_ID);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                String.format(
                        "%s_PROOF_COLLECTION",
                        AUTHORIZED_3RD_PARTY_MSP_ID.toUpperCase()
                ),
                REGISTRATION_NUMBER.toUpperCase()
        )).thenReturn(objectMapper.writeValueAsBytes(expected));

        final Proof result = subject.viewProofIn3rdPartyCollection(
                mockedContext,
                REGISTRATION_NUMBER
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getPrivateData(
                String.format(
                        "%s_PROOF_COLLECTION",
                        AUTHORIZED_3RD_PARTY_MSP_ID.toUpperCase()
                ),
                REGISTRATION_NUMBER
        );
        assertEquals(
                expected,
                result
        );
    }

    @Test
    void revokeCertificateOfDepositShouldThrowExceptionBecauseRequestorIsUnauthorized() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn("BessCoMSP");

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.revokeCertificateOfDeposits(
                            mockedContext,
                            REGISTRATION_NUMBER
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Unauthorized request."));
    }

    @Test
    void revokeCertificateOfDepositShouldThrowExceptionBecauseRegistrationNumberIsMissing() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.revokeCertificateOfDeposits(
                            mockedContext,
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void revokeCertificateOfDepositShouldThrowExceptionBecauseRegistrationNumberIsBlank() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.revokeCertificateOfDeposits(
                            mockedContext,
                            ""
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void revokeCertificateOfDepositShouldThrowExceptionBecausePofrDoesNotExist() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER.toUpperCase()
        )).thenReturn(null);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.revokeCertificateOfDeposits(
                            mockedContext,
                            REGISTRATION_NUMBER
                    );
                }
        );

        assertTrue(exception.getMessage().contains("No proof of financial responsibility exists for registration number OH-123."));
    }

    @Test
    void revokeCertificateOfDepositShouldThrowExceptionBecausePofrExistsButNoCoDExist() throws
                                                                                        JsonProcessingException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER.toUpperCase()
        )).thenReturn(
                objectMapper.writeValueAsBytes(
                        new PersistedProof(
                                List.of(),
                                null,
                                "1",
                                null
                        )
                )
        );

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.revokeCertificateOfDeposits(
                            mockedContext,
                            REGISTRATION_NUMBER
                    );
                }
        );

        assertTrue(exception.getMessage().contains("No proof of financial responsibility exists for registration number OH-123."));
    }

    @Test
    void revokeCertificateOfDepositShouldDeleteEntryBecauseProofHasNoProofs() throws
                                                                              IOException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER.toUpperCase()
        )).thenReturn(
                objectMapper.writeValueAsBytes(
                        new PersistedProof(
                                List.of(
                                        new PersistedCertificateOfDeposit(
                                                "1000",
                                                "Samson the cat"
                                        )
                                ),
                                null,
                                "1",
                                null
                        )
                )
        );

        subject.revokeCertificateOfDeposits(
                mockedContext,
                REGISTRATION_NUMBER
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER.toUpperCase()
        );
        verify(
                mockedChaincodeStub,
                times(1)
        ).delPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER.toUpperCase()
        );
    }

    @Test
    void revokeCertificateOfDepositShouldSaveProofBecauseOtherProofsExist() throws
                                                                              IOException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER.toUpperCase()
        )).thenReturn(
                objectMapper.writeValueAsBytes(
                        new PersistedProof(
                                List.of(
                                        new PersistedCertificateOfDeposit(
                                                "1000",
                                                "Samson the cat"
                                        )
                                ),
                                null,
                                "1",
                                new PersistedSelfInsurer(
                                        "1000",
                                        "Lazy Cat Inc",
                                        "Samson",
                                        "CEO"
                                )
                        )
                )
        );

        subject.revokeCertificateOfDeposits(
                mockedContext,
                REGISTRATION_NUMBER
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER.toUpperCase()
        );
        verify(
                mockedChaincodeStub,
                times(1)
        ).putPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER.toUpperCase(),
                objectMapper.writeValueAsBytes(
                        new PersistedProof(
                                List.of(),
                                null,
                                "1",
                                new PersistedSelfInsurer(
                                        "1000",
                                        "Lazy Cat Inc",
                                        "Samson",
                                        "CEO"
                                )
                        )
                )
        );
    }

    @Test
    void revokeSelfInsuranceShouldThrowExceptionBecauseRequestorIsUnauthorized() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn("BessCoMSP");

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.revokeSelfInsurance(
                            mockedContext,
                            REGISTRATION_NUMBER
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Unauthorized request."));
    }

    @Test
    void revokeSelfInsuranceShouldThrowExceptionBecauseRegistrationNumberIsMissing() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.revokeSelfInsurance(
                            mockedContext,
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void revokeSelfInsuranceShouldThrowExceptionBecauseRegistrationNumberIsBlank() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.revokeSelfInsurance(
                            mockedContext,
                            ""
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void revokeSelfInsuranceShouldThrowExceptionBecausePofrDoesNotExist() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER.toUpperCase()
        )).thenReturn(null);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.revokeSelfInsurance(
                            mockedContext,
                            REGISTRATION_NUMBER
                    );
                }
        );

        assertTrue(exception.getMessage().contains("No proof of financial responsibility exists for registration number OH-123."));
    }

    @Test
    void revokeSelfInsuranceShouldThrowExceptionBecausePofrExistsButNoSelfInsuranceExist() throws
                                                                                        JsonProcessingException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER.toUpperCase()
        )).thenReturn(
                objectMapper.writeValueAsBytes(
                        new PersistedProof(
                                List.of(),
                                null,
                                "1",
                                null
                        )
                )
        );

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.revokeSelfInsurance(
                            mockedContext,
                            REGISTRATION_NUMBER
                    );
                }
        );

        assertTrue(exception.getMessage().contains("No proof of financial responsibility exists for registration number OH-123."));
    }

    @Test
    void revokeSelfInsuranceShouldDeleteEntryBecauseProofHasNoProofs() throws
                                                                              IOException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER.toUpperCase()
        )).thenReturn(
                objectMapper.writeValueAsBytes(
                        new PersistedProof(
                                List.of(),
                                null,
                                "1",
                                new PersistedSelfInsurer(
                                        "1000",
                                        "The Lazy Cat",
                                        "Samson the cat",
                                        "CEO"
                                )
                        )
                )
        );

        subject.revokeSelfInsurance(
                mockedContext,
                REGISTRATION_NUMBER
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER.toUpperCase()
        );
        verify(
                mockedChaincodeStub,
                times(1)
        ).delPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER.toUpperCase()
        );
    }

    @Test
    void revokeSelfInsuranceShouldSaveProofBecauseOtherProofsExist() throws
                                                                            IOException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER.toUpperCase()
        )).thenReturn(
                objectMapper.writeValueAsBytes(
                        new PersistedProof(
                                List.of(
                                        new PersistedCertificateOfDeposit(
                                                "1000",
                                                "Samson the cat"
                                        )
                                ),
                                null,
                                "1",
                                new PersistedSelfInsurer(
                                        "1000",
                                        "Lazy Cat Inc",
                                        "Samson",
                                        "CEO"
                                )
                        )
                )
        );

        subject.revokeSelfInsurance(
                mockedContext,
                REGISTRATION_NUMBER
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER.toUpperCase()
        );
        verify(
                mockedChaincodeStub,
                times(1)
        ).putPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER.toUpperCase(),
                objectMapper.writeValueAsBytes(
                        new PersistedProof(
                                List.of(
                                        new PersistedCertificateOfDeposit(
                                                "1000",
                                                "Samson the cat"
                                        )
                                ),
                                null,
                                "1",
                                null
                        )
                )
        );
    }

    @Test
    void cancelInsuranceShouldThrowExceptionBecauseRequestorIsUnauthorized() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn("BessCoMSP");

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.cancelInsurance(
                            mockedContext,
                            REGISTRATION_NUMBER
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Unauthorized request."));
    }

    @Test
    void cancelInsuranceShouldThrowExceptionBecauseRegistrationNumberIsMissing() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_MSP_ID);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.cancelInsurance(
                            mockedContext,
                            null
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void cancelInsuranceShouldThrowExceptionBecauseRegistrationNumberIsBlank() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.cancelInsurance(
                            mockedContext,
                            ""
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void cancelInsuranceShouldThrowExceptionBecausePofrDoesNotExist() {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER.toUpperCase()
        )).thenReturn(null);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.cancelInsurance(
                            mockedContext,
                            REGISTRATION_NUMBER
                    );
                }
        );

        assertTrue(exception.getMessage().contains("No proof of financial responsibility exists for registration number OH-123."));
    }

    @Test
    void cancelInsuranceShouldThrowExceptionBecausePofrExistsButNoInsuranceExist() throws
                                                                                           JsonProcessingException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER.toUpperCase()
        )).thenReturn(
                objectMapper.writeValueAsBytes(
                        new PersistedProof(
                                List.of(),
                                null,
                                "1",
                                null
                        )
                )
        );

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.cancelInsurance(
                            mockedContext,
                            REGISTRATION_NUMBER
                    );
                }
        );

        assertTrue(exception.getMessage().contains("No proof of financial responsibility exists for registration number OH-123."));
    }

    @Test
    void cancelInsuranceShouldDeleteEntryBecauseProofHasNoProofs() throws
                                                                       IOException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER.toUpperCase()
        )).thenReturn(
                objectMapper.writeValueAsBytes(
                        new PersistedProof(
                                List.of(),
                                new PersistedInsurance(
                                        null,
                                        null,
                                        null
                                ),
                                "1",
                                null
                        )
                )
        );

        subject.cancelInsurance(
                mockedContext,
                REGISTRATION_NUMBER
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER.toUpperCase()
        );
        verify(
                mockedChaincodeStub,
                times(1)
        ).delPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER.toUpperCase()
        );
    }

    @Test
    void cancelInsuranceShouldSaveProofBecauseOtherProofsExist() throws
                                                                     IOException {
        when(mockedContext.getClientIdentity()).thenReturn(mockedClientIdentity);
        when(mockedClientIdentity.getMSPID()).thenReturn(AUTHORIZED_STATE_DMV_MSP_IPD);
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER.toUpperCase()
        )).thenReturn(
                objectMapper.writeValueAsBytes(
                        new PersistedProof(
                                List.of(
                                        new PersistedCertificateOfDeposit(
                                                "1000",
                                                "Samson the cat"
                                        )
                                ),
                                new PersistedInsurance(
                                        Map.of(),
                                        List.of(
                                                new PersistedInsured("Maya the Husky")
                                        ),
                                        new PersistedPolicy(
                                                "01/01/25",
                                                "12/31/25",
                                                "Progressive",
                                                "ABC123"
                                        )
                                ),
                                "1",
                                null
                        )
                )
        );

        subject.cancelInsurance(
                mockedContext,
                REGISTRATION_NUMBER
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).getPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER.toUpperCase()
        );
        verify(
                mockedChaincodeStub,
                times(1)
        ).putPrivateData(
                STATE_COLLECTION,
                REGISTRATION_NUMBER.toUpperCase(),
                objectMapper.writeValueAsBytes(
                        new PersistedProof(
                                List.of(
                                        new PersistedCertificateOfDeposit(
                                                "1000",
                                                "Samson the cat"
                                        )
                                ),
                                null,
                                "1",
                                null
                        )
                )
        );
    }
}
