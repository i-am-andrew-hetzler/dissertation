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
class SchemaVersionIsNotNullOrEmptyTest {
    public final SchemaVersionIsNotNullOrEmpty subject = new SchemaVersionIsNotNullOrEmpty();

    @Test
    void shouldReturnTrueBecauseSchemaVersionIsNotNullorEmpty() {
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

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseBecauseSchemaVersionIsNull() {
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
    void shouldReturnFalseBecauseSchemaVersionIsEmpty() {
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
                ""
        ));

        assertFalse(result);
    }
}
