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
class VersionIsNotNullOrEmptyTest {
    public final VersionIsNotNullOrEmpty subject = new VersionIsNotNullOrEmpty();

    @Test
    void shouldReturnTrueBecauseVersionIsNotNullOrEmpty() {
        final boolean result = subject.validate(new FirmwareUpdate(
                new Firmware(
                        null,
                        null,
                        null,
                        null,
                        null,
                        "1.0",
                        -1
                ),
                1
        ));

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseBecauseVersionIsNull() {
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
    void shouldReturnFalseBecauseVersionIsEmpty() {
        final boolean result = subject.validate(new FirmwareUpdate(
                new Firmware(
                        null,
                        null,
                        null,
                        null,
                        null,
                        "",
                        -1
                ),
                1
        ));

        assertFalse(result);
    }
}
