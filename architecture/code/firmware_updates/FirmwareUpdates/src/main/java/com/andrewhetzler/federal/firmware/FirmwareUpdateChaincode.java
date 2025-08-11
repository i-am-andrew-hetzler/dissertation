package com.andrewhetzler.federal.firmware;

import com.andrewhetzler.federal.firmware.model.Firmware;
import com.andrewhetzler.federal.firmware.model.FirmwareUpdate;
import com.andrewhetzler.federal.firmware.model.Validation;
import com.andrewhetzler.federal.firmware.model.validations.HashIsNotNullOrEmpty;
import com.andrewhetzler.federal.firmware.model.validations.MakeIsNotNullOrEmpty;
import com.andrewhetzler.federal.firmware.model.validations.ModelIsNotNullOrEmpty;
import com.andrewhetzler.federal.firmware.model.validations.SchemaVersionIsNotNullOrEmpty;
import com.andrewhetzler.federal.firmware.model.validations.UrlIsNotNullOrEmpty;
import com.andrewhetzler.federal.firmware.model.validations.VersionIsNotNullOrEmpty;
import com.andrewhetzler.federal.firmware.model.validations.YearIsNotNullOrEmpty;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.Map;

import static com.andrewhetzler.federal.firmware.FirmwareUpdateError.UPDATE_DOES_NOT_EXIT;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/10/25
 **/
@Contract(
        name = "firmwareUpdates",
        info = @Info(
                title = "Firmware Updates",
                description = "The chaincode that powers the firmware updates use case.",
                version = "1.0.0",
                contact = @Contact(
                        email = "ahetzler@purdue.edu",
                        name = "Andrew Hetzler",
                        url = "www.andrewhetzler.com")))
@Default
public class FirmwareUpdateChaincode {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<Validation> validations = new ArrayList<>();

    public FirmwareUpdateChaincode() {
        if (validations.isEmpty()) {
            validations.add(new HashIsNotNullOrEmpty());
            validations.add(new MakeIsNotNullOrEmpty());
            validations.add(new ModelIsNotNullOrEmpty());
            validations.add(new UrlIsNotNullOrEmpty());
            validations.add(new VersionIsNotNullOrEmpty());
            validations.add(new SchemaVersionIsNotNullOrEmpty());
            validations.add(new YearIsNotNullOrEmpty());
        }
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public FirmwareUpdate checkForUpdate(
            final Context context,
            final String make,
            final String model,
            final String year
    ) throws
      IOException {
        final byte[] firmwareUpdateTransaction = context.getStub().getState(String.format(
                "%s-%s-%s",
                make.toLowerCase(),
                model.toLowerCase(),
                year
        ));

        if (firmwareUpdateTransaction == null || firmwareUpdateTransaction.length == 0) {
            throw new ChaincodeException(
                    String.format(
                            "Update does not exist for %s %s %s",
                            make,
                            model,
                            year
                    ),
                    UPDATE_DOES_NOT_EXIT.toString()
            );
        }

        return objectMapper.readValue(
                firmwareUpdateTransaction,
                FirmwareUpdate.class
        );
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public FirmwareUpdate createFirmwareUpdate(
            final Context context,
            final String hash,
            final String make,
            final Map<String, String> metadata,
            final String model,
            final String url,
            final String version,
            final String year,
            final String schemaVersion
    ) throws
      JsonProcessingException {
        final FirmwareUpdate firmwareUpdate = new FirmwareUpdate(
                new Firmware(
                        hash,
                        make,
                        metadata,
                        model,
                        url,
                        version,
                        year
                ),
                schemaVersion
        );

        firmwareUpdate.validate(validations);

        context.getStub().putState(
                String.format(
                        "Update does not exist for %s %s %s",
                        make,
                        model,
                        year
                ),
                objectMapper.writeValueAsBytes(firmwareUpdate)
        );

        return firmwareUpdate;
    }
}
