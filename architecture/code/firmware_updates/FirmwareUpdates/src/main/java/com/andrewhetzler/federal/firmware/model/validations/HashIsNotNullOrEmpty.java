package com.andrewhetzler.federal.firmware.model.validations;

import com.andrewhetzler.federal.firmware.model.FirmwareUpdate;
import com.andrewhetzler.federal.firmware.model.Validation;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/11/25
 **/
public class HashIsNotNullOrEmpty implements Validation {
    @Override
    public boolean validate(FirmwareUpdate firmwareUpdate) {
        return firmwareUpdate.getHash() != null && !firmwareUpdate.getHash().isBlank();
    }
}
