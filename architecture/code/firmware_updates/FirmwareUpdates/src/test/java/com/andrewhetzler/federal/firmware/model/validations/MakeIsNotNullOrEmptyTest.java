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
class MakeIsNotNullOrEmptyTest {
    public final MakeIsNotNullOrEmpty subject = new MakeIsNotNullOrEmpty();

    @Test
    void shouldReturnTrueBecauseMakeIsNotNullorEmpty() {
        final boolean result = subject.validate(new FirmwareUpdate(
                new Firmware(
                        null,
                        "Maserati",
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
    void shouldReturnFalseBecauseMakeIsNull() {
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
    void shouldReturnFalseBecauseMakeIsEmpty() {
        final boolean result = subject.validate(new FirmwareUpdate(
                new Firmware(
                        null,
                        "",
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
