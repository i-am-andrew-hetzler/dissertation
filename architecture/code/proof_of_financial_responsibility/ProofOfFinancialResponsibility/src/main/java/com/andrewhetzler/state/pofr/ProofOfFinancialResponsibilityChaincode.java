package com.andrewhetzler.state.pofr;

import com.andrewhetzler.state.pofr.model.Proof;
import com.andrewhetzler.state.pofr.model.persisted.PersistedProof;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.contract.ClientIdentity;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.andrewhetzler.state.pofr.ProofOfFinancialResponsibilityChaincodeError.INVALID_REQUEST;
import static com.andrewhetzler.state.pofr.ProofOfFinancialResponsibilityChaincodeError.PROOF_DOES_NOT_EXIST;
import static com.andrewhetzler.state.pofr.ProofOfFinancialResponsibilityChaincodeError.UNAUTHORIZED_REQUEST;

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
    private static final String STATE_PROOF_COLLECTION = System.getenv().getOrDefault(
            "STATE_PROOF_COLLECTION",
            "TestStateProofCollection"
    );
    private static final List<String> STATE_AGENCIES_MSP_IDS = Arrays.asList(System.getenv().getOrDefault(
            "STATE_AGENCIES_MSP_IDS",
            "TestStateMSP"
    ).split(";"));
    private static final String STATE_DMV_MSP_ID = System.getenv().getOrDefault(
            "STATE_DMV_MSP_ID",
            "TestStateDmvMSP"
    );
    private static final String STATE_DMV_INSURED_MSP_ID = System.getenv().getOrDefault(
            "STATE_DMV_INSURED_MSP_ID",
            "TestStateDmvInsuredMSP"
    );
    private static final List<String> THIRD_PARTY_MSP_IDS = Arrays.asList(System.getenv().getOrDefault(
            "THIRD_PARTY_MSP_IDS",
            "TestInsuranceCoMSP"
    ).split(";"));

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Proof viewProof(
            final Context context,
            final String registrationNumber
    ) throws
      IOException {
        if (!isAuthorized(context.getClientIdentity())) {
            throw new ChaincodeException(
                    "Unauthorized request.",
                    UNAUTHORIZED_REQUEST.toString()
            );
        }

        if (isNullOrBlank(registrationNumber)) {
            throw new ChaincodeException(
                    "Invalid request.",
                    INVALID_REQUEST.toString()
            );
        }

        final PersistedProof existingProof = getProof(
                context,
                registrationNumber
        );

        if (existingProof == null) {
            throw new ChaincodeException(
                    String.format(
                            "No proof of financial responsibility exists for registration number %s.",
                            registrationNumber
                    ),
                    PROOF_DOES_NOT_EXIST.toString()
            );
        }



        return null;
    }

    private boolean isMspIdInStateAgencies(final ClientIdentity clientIdentity) {
        return STATE_AGENCIES_MSP_IDS.contains(clientIdentity.getMSPID());
    }

    private boolean isMspIdTheStateDmv(final ClientIdentity clientIdentity) {
        return STATE_DMV_MSP_ID.equalsIgnoreCase(clientIdentity.getMSPID());
    }

    private boolean isMspIdAnInsured(final ClientIdentity clientIdentity) {
        return STATE_DMV_INSURED_MSP_ID.equalsIgnoreCase(clientIdentity.getMSPID());
    }

    private boolean isMspIdInThirdPartyMspIds(final ClientIdentity clientIdentity) {
        return THIRD_PARTY_MSP_IDS.contains(clientIdentity.getMSPID());
    }

    private boolean isAuthorized(final ClientIdentity clientIdentity) {
        return isMspIdInStateAgencies(clientIdentity) ||
                isMspIdTheStateDmv(clientIdentity) ||
                isMspIdAnInsured(clientIdentity) ||
                isMspIdInThirdPartyMspIds(clientIdentity);
    }

    private boolean isNullOrBlank(final String value) {
        return value == null || value.isBlank();
    }

    private boolean isNumber(final String value) {
        try {
            Integer.parseInt(value);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    private PersistedProof getProof(
            final Context context,
            final String registrationNumber
    ) throws
      IOException {
        final byte[] proof = context.getStub().getPrivateData(
                STATE_PROOF_COLLECTION,
                registrationNumber.toUpperCase()
        );

        return proof != null ? objectMapper.readValue(
                proof,
                PersistedProof.class
        ) : null;
    }
}
