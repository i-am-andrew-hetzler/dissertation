package com.andrewhetzler.state.pofr;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.contract.ClientIdentity;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

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

    @BeforeEach
    void setUp() {
        openMocks(this);

        subject = new ProofOfFinancialResponsibilityChaincode();
    }
}
