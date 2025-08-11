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
class YearIsNotNullOrEmptyTest {
    public final YearIsNotNullOrEmpty subject = new YearIsNotNullOrEmpty();

    @Test
    void shouldReturnTrueBecauseYearIsNotNullorEmpty() {
        final boolean result = subject.validate(new FirmwareUpdate(
                new Firmware(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        "2025"
                ),
                "1"
        ));

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseBecauseYearIsNull() {
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
                null
        ));

        assertFalse(result);
    }

    @Test
    void shouldReturnFalseBecauseYearIsEmpty() {
        final boolean result = subject.validate(new FirmwareUpdate(
                new Firmware(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        ""
                ),
                ""
        ));

        assertFalse(result);
    }
}
