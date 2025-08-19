package com.andrewhetzler.state.pofr;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/19/25
 **/
@Contract(
        name = "pofr",
        info = @Info(
                title = "Proof of Financial Responsibility",
                description = "The chaincode that powers the proof of financial responsibility use case.",
                version = "1.0.0",
                contact = @Contact(
                        email = "ahetzler@purdue.edu",
                        name = "Andrew Hetzler",
                        url = "www.andrewhetzler.com")))
@Default
public class ProofOfFinancialResponsibilityChaincode {
    private final ObjectMapper objectMapper = new ObjectMapper();
}
