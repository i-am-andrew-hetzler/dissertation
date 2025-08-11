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
class ModelIsNotNullOrEmptyTest {
    public final ModelIsNotNullOrEmpty subject = new ModelIsNotNullOrEmpty();

    @Test
    void shouldReturnTrueBecauseModelIsNotNullorEmpty() {
        final boolean result = subject.validate(new FirmwareUpdate(
                new Firmware(
                        null,
                        null,
                        null,
                        "MC20",
                        null,
                        null,
                        null
                ),
                "1"
        ));

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseBecauseModelIsNull() {
        final boolean result = subject.validate(new FirmwareUpdate(
                new Firmware(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                ),
                "1"
        ));

        assertFalse(result);
    }

    @Test
    void shouldReturnFalseBecauseModelIsEmpty() {
        final boolean result = subject.validate(new FirmwareUpdate(
                new Firmware(
                        null,
                        null,
                        null,
                        "",
                        null,
                        null,
                        null
                ),
                "1"
        ));

        assertFalse(result);
    }
}
