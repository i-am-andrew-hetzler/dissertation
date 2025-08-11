package com.andrewhetzler.federal.firmware.model.validations;

import com.andrewhetzler.federal.firmware.model.FirmwareUpdate;
import com.andrewhetzler.federal.firmware.model.Validation;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/11/25
 **/
public class MakeIsNotNullOrEmpty implements Validation {
    @Override
    public boolean validate(FirmwareUpdate firmwareUpdate) {
        return firmwareUpdate.getMake() != null && !firmwareUpdate.getMake().isBlank();
    }
}
