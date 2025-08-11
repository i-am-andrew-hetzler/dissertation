package com.andrewhetzler.federal.fmvss;

import com.andrewhetzler.federal.fmvss.model.FmvssCertification;
import com.andrewhetzler.federal.fmvss.model.Validation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/11/25
 **/
@Contract(
        name = "FmvssCertification",
        info = @Info(
                title = "FMVSS Certification",
                description = "The chaincode that powers the FMVSS certification use case.",
                version = "1.0.0",
                contact = @Contact(
                        email = "ahetzler@purdue.edu",
                        name = "Andrew Hetzler",
                        url = "www.andrewhetzler.com")))
@Default
public class FmvssCertificationChaincode {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<Validation> validations = new ArrayList<>();

    public FmvssCertificationChaincode() {
        if (validations.isEmpty()) {

        }
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public FmvssCertification viewCertification(final Context context, final String vin) throws IOException {
        final byte[] certification = context.getStub().getState(vin.toUpperCase());

        if (certification == null ||  certification.length == 0) {
            throw new ChaincodeException(
                    String.format("No certification found for vehicle %s.", vin),
                    FmvssCertificationError.CERTIFICATION_DOES_NOT_EXIST.toString()
            );
        }

        return objectMapper.readValue(
                certification,
                FmvssCertification.class
        );
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public FmvssCertification certifyAlteredVehicle(final Context context, final String vin, final String conformityStatement, ) {
        return null;
    }
}
