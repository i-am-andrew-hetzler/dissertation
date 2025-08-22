package com.andrewhetzler.federal.firmware.model;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/11/25
 **/
public interface Validation {
    boolean validate(final FirmwareUpdate firmwareUpdate);
}
