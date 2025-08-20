package com.andrewhetzler.state.pofr;

import com.andrewhetzler.state.pofr.model.CertificateOfDeposit;
import com.andrewhetzler.state.pofr.model.Insurance;
import com.andrewhetzler.state.pofr.model.Insured;
import com.andrewhetzler.state.pofr.model.Policy;
import com.andrewhetzler.state.pofr.model.Proof;
import com.andrewhetzler.state.pofr.model.SelfInsurer;
import com.andrewhetzler.state.pofr.model.persisted.PersistedCertificateOfDeposit;
import com.andrewhetzler.state.pofr.model.persisted.PersistedInsurance;
import com.andrewhetzler.state.pofr.model.persisted.PersistedProof;
import com.andrewhetzler.state.pofr.model.persisted.PersistedSelfInsurer;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.Collections;
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

    /*
    State agencies and 3rd parties would call this method. Individuals would call the specific method (e.g., insurance)
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Proof viewProof(
            final Context context,
            final String registrationNumber
    ) throws
      IOException {
        if (!isMspIdInStateAgencies(context.getClientIdentity())
                && !isMspIdTheStateDmv(context.getClientIdentity())
                && !isMspIdInThirdPartyMspIds(context.getClientIdentity())
        ) {
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

        if (isMspIdInStateAgencies(context.getClientIdentity()) || isMspIdTheStateDmv(context.getClientIdentity())) {
            return createProofFromPersistedProof(existingProof);
        }
        else if (isMspIdInThirdPartyMspIds(context.getClientIdentity())) {
            saveProofDataTo3rdPartyCollection(
                    context,
                    createProofFromPersistedProof(existingProof),
                    registrationNumber
            );

            return createProofFromPersistedProof(existingProof);
        }
        else {
            /*
            This should not happen
             */
            throw new ChaincodeException(
                    "Unauthorized request.",
                    UNAUTHORIZED_REQUEST.toString()
            );
        }
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Proof viewProofIn3rdPartyCollection(
            final Context context,
            final String registrationNumber
    ) throws
      IOException {
        if (!isMspIdInThirdPartyMspIds(context.getClientIdentity())) {
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

        final byte[] existingProof = context.getStub().getPrivateData(
                String.format(
                        "%s_PROOF_COLLECTION",
                        context.getClientIdentity().getMSPID().toUpperCase()
                ),
                registrationNumber.toUpperCase()
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

        return objectMapper.readValue(
                existingProof,
                Proof.class
        );
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void revokeCertificateOfDeposits(
            final Context context,
            final String registrationNumber
    ) throws
      IOException {
        if (!isMspIdInStateAgencies(context.getClientIdentity()) && !isMspIdTheStateDmv(context.getClientIdentity())) {
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

        final byte[] existingProof = context.getStub().getPrivateData(
                STATE_PROOF_COLLECTION,
                registrationNumber.toUpperCase()
        );
        final PersistedProof proof = existingProof != null ? objectMapper.readValue(
                existingProof,
                PersistedProof.class
        ) : null;

        if (proof == null || proof.getCertificateOfDeposits().isEmpty()) {
            throw new ChaincodeException(
                    String.format(
                            "No proof of financial responsibility exists for registration number %s.",
                            registrationNumber
                    ),
                    PROOF_DOES_NOT_EXIST.toString()
            );
        }

        save(
                context,
                new PersistedProof(
                        List.of(),
                        proof.getInsurance(),
                        proof.getSchemaVersion(),
                        proof.getSelfInsurer()
                ),
                registrationNumber
        );
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void revokeSelfInsurance(
            final Context context,
            final String registrationNumber
    ) throws
      IOException {
        if (!isMspIdInStateAgencies(context.getClientIdentity()) && !isMspIdTheStateDmv(context.getClientIdentity())) {
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

        final byte[] existingProof = context.getStub().getPrivateData(
                STATE_PROOF_COLLECTION,
                registrationNumber.toUpperCase()
        );
        final PersistedProof proof = existingProof != null ? objectMapper.readValue(
                existingProof,
                PersistedProof.class
        ) : null;

        if (proof == null || proof.getSelfInsurer() == null) {
            throw new ChaincodeException(
                    String.format(
                            "No proof of financial responsibility exists for registration number %s.",
                            registrationNumber
                    ),
                    PROOF_DOES_NOT_EXIST.toString()
            );
        }

        save(
                context,
                new PersistedProof(
                        proof.getCertificateOfDeposits(),
                        proof.getInsurance(),
                        proof.getSchemaVersion(),
                        null
                ),
                registrationNumber
        );
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void cancelInsurance(
            final Context context,
            final String registrationNumber
    ) throws
      IOException {
        if (!isMspIdInStateAgencies(context.getClientIdentity()) && !isMspIdTheStateDmv(context.getClientIdentity())) {
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

        final byte[] existingProof = context.getStub().getPrivateData(
                STATE_PROOF_COLLECTION,
                registrationNumber.toUpperCase()
        );
        final PersistedProof proof = existingProof != null ? objectMapper.readValue(
                existingProof,
                PersistedProof.class
        ) : null;

        if (proof == null || proof.getInsurance() == null) {
            throw new ChaincodeException(
                    String.format(
                            "No proof of financial responsibility exists for registration number %s.",
                            registrationNumber
                    ),
                    PROOF_DOES_NOT_EXIST.toString()
            );
        }

        save(
                context,
                new PersistedProof(
                        proof.getCertificateOfDeposits(),
                        null,
                        proof.getSchemaVersion(),
                        proof.getSelfInsurer()
                ),
                registrationNumber
        );
    }

    private boolean isMspIdInStateAgencies(final ClientIdentity clientIdentity) {
        return STATE_AGENCIES_MSP_IDS.contains(clientIdentity.getMSPID());
    }

    private boolean isMspIdTheStateDmv(final ClientIdentity clientIdentity) {
        return STATE_DMV_MSP_ID.equalsIgnoreCase(clientIdentity.getMSPID());
    }

    private boolean isMspIdInThirdPartyMspIds(final ClientIdentity clientIdentity) {
        return THIRD_PARTY_MSP_IDS.contains(clientIdentity.getMSPID());
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

    private Proof createProofFromPersistedProof(final PersistedProof proof) {
        return new Proof(
                createCertificateOfDepositsFromPersistedCertificateOfDeposits(proof.getCertificateOfDeposits()),
                createInsuranceFromPersistedInsurance(proof.getInsurance()),
                proof.getSchemaVersion(),
                createSelfInsurerFromPersistedSelfInsurer(proof.getSelfInsurer())
        );
    }

    private List<CertificateOfDeposit> createCertificateOfDepositsFromPersistedCertificateOfDeposits(List<PersistedCertificateOfDeposit> deposits) {
        return deposits != null ? deposits.stream().map(cd -> new CertificateOfDeposit(
                cd.getAmount(),
                cd.getName()
        )).toList() : Collections.emptyList();
    }

    private Insurance createInsuranceFromPersistedInsurance(PersistedInsurance insurance) {
        return insurance != null ? new Insurance(
                insurance.getDescriptionOfVehicle(),
                insurance.getInsured().stream().map(insured -> new Insured(insured.getName())).toList(),
                new Policy(
                        insurance.getPolicy().getEffectiveDate(),
                        insurance.getPolicy().getExpirationDate(),
                        insurance.getPolicy().getInsurer(),
                        insurance.getPolicy().getPolicyNumber()
                )
        ) : null;
    }

    private SelfInsurer createSelfInsurerFromPersistedSelfInsurer(PersistedSelfInsurer selfInsurer) {
        return selfInsurer != null ? new SelfInsurer(
                selfInsurer.getAmount(),
                selfInsurer.getBusinessName(),
                selfInsurer.getName(),
                selfInsurer.getTitle()
        ) : null;
    }

    private void save(
            final Context context,
            final PersistedProof proof,
            final String registrationNumber
    ) throws
      JsonProcessingException {
        if (proof.hasNoProofs()) {
            context.getStub().delPrivateData(
                    STATE_PROOF_COLLECTION,
                    registrationNumber.toUpperCase()
            );
        } else {
            context.getStub().putPrivateData(
                    STATE_PROOF_COLLECTION,
                    registrationNumber.toUpperCase(),
                    objectMapper.writeValueAsBytes(proof)
            );
        }
    }

    private void saveProofDataTo3rdPartyCollection(
            final Context context,
            final Proof proof,
            final String registrationNumber
    ) throws
      JsonProcessingException {
        context.getStub().putPrivateData(
                String.format(
                        ("%s_PROOF_COLLECTION"),
                        context.getClientIdentity().getMSPID().toUpperCase()
                ),
                registrationNumber.toUpperCase(),
                objectMapper.writeValueAsBytes(proof)
        );
    }
}
