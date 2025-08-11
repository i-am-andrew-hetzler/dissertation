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
class UrlIsNotNullOrEmptyTest {
    public final UrlIsNotNullOrEmpty subject = new UrlIsNotNullOrEmpty();

    @Test
    void shouldReturnTrueBecauseUrlIsNotNullOrEmpty() {
        final boolean result = subject.validate(new FirmwareUpdate(
                new Firmware(
                        null,
                        null,
                        null,
                        null,
                        "url here",
                        null,
                        null
                ),
                "1"
        ));

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseBecauseUrlIsNull() {
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
    void shouldReturnFalseBecauseUrlIsEmpty() {
        final boolean result = subject.validate(new FirmwareUpdate(
                new Firmware(
                        null,
                        "",
                        null,
                        null,
                        "",
                        null,
                        null
                ),
                "1"
        ));

        assertFalse(result);
    }
}
