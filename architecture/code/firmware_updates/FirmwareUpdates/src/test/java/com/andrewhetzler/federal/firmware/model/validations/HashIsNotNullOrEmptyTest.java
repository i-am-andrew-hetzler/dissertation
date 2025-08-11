package com.andrewhetzler.federal.firmware.model.validations;

import com.andrewhetzler.federal.firmware.model.Firmware;
import com.andrewhetzler.federal.firmware.model.FirmwareUpdate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/11/25
 **/
class HashIsNotNullOrEmptyTest {
    public final HashIsNotNullOrEmpty subject = new HashIsNotNullOrEmpty();

    @Test
    void shouldReturnTrueBecauseHashIsNotNullorEmpty() {
        final boolean result = subject.validate(new FirmwareUpdate(
                new Firmware(
                        "Hash",
                        null,
                        null,
                        null,
                        null,
                        null,
                        -1
                ),
                1
        ));

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseBecauseHashIsNull() {
        final boolean result = subject.validate(new FirmwareUpdate(
                new Firmware(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        -1
                ),
                1
        ));

        assertFalse(result);
    }

    @Test
    void shouldReturnFalseBecauseHashIsEmpty() {
        final boolean result = subject.validate(new FirmwareUpdate(
                new Firmware(
                        "",
                        null,
                        null,
                        null,
                        null,
                        null,
                        -1
                ),
                1
        ));

        assertFalse(result);
    }
}
