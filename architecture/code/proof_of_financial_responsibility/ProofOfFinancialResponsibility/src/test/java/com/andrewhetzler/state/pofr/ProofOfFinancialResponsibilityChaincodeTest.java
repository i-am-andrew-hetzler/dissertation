package com.andrewhetzler.state.pofr;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.contract.ClientIdentity;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private static final String STATE_CERTIFICATE_OF_DEPOSITS_COLLECTION = "TestStateCertificateOfDepositCollection";
    private static final String STATE_INSURANCE_COLLECTION = "TestStateInsuranceCollection";
    private static final String STATE_SELF_INSURANCE_COLLECTION = "TestStateSelfInsuranceCollection";
    private static final String LICENSE_NUMBER = "OH-123";

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
                            LICENSE_NUMBER
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Unauthorized request."));
    }

    @Test
    void viewProofShouldThrowExceptionBecauseLicenseNumberIsMissing() {
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
    void viewProofShouldThrowExceptionBecauseLicenseNumberIsBlank() {
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
                STATE_CERTIFICATE_OF_DEPOSITS_COLLECTION,
                LICENSE_NUMBER
        )).thenReturn(null);
        when(mockedChaincodeStub.getPrivateData(
                STATE_INSURANCE_COLLECTION,
                LICENSE_NUMBER
        )).thenReturn(null);
        when(mockedChaincodeStub.getPrivateData(
                STATE_SELF_INSURANCE_COLLECTION,
                LICENSE_NUMBER
        )).thenReturn(null);

        final Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.viewProof(
                            mockedContext,
                            LICENSE_NUMBER
                    );
                }
        );

        assertTrue(exception.getMessage().contains("No proof of financial responsibility exists for license number"));
    }
}
