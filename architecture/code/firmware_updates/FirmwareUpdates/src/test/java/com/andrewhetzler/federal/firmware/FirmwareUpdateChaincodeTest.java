package com.andrewhetzler.federal.firmware;

import com.andrewhetzler.federal.firmware.model.Firmware;
import com.andrewhetzler.federal.firmware.model.FirmwareUpdate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/10/25
 **/
class FirmwareUpdateChaincodeTest {
    @Mock
    private Context mockedContext;
    @Mock
    private ChaincodeStub mockedChaincodeStub;
    private FirmwareUpdateChaincode subject;
    private final FirmwareUpdate firmwareUpdate = new FirmwareUpdate(
            new Firmware(
                    "hash1",
                    "Maserati",
                    null,
                    "MC20",
                    "www.update.com",
                    "1.1.3",
                    "2025"
            ),
            "1"
    );
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        openMocks(this);

        subject = new FirmwareUpdateChaincode();
    }

    @Test
    void checkForUpdatesShouldThrowExceptionBecauseUpdateDoesNotExist() {
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState(anyString())).thenReturn(null);

        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.checkForUpdate(
                            mockedContext,
                            "Maserati",
                            "MC20",
                            "2025"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Update does not exist for Maserati MC20 2025"));
    }

    @Test
    void checkForUpdatesShouldReturnAnUpdateBecauseFirmwareExists() throws
            IOException {
        final byte[] expected = objectMapper.writeValueAsBytes(firmwareUpdate);

        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);
        when(mockedChaincodeStub.getState("MASERATI-MC20-2025")).thenReturn(expected);

        final String result = subject.checkForUpdate(
                mockedContext,
                "Maserati",
                "MC20",
                "2025"
        );

        assertThat(result).isEqualTo(objectMapper.writeValueAsString(firmwareUpdate));
    }

    @Test
    void createFirmwareShouldThrowExceptionBecauseRequestIsInvalid() {
        Exception exception = assertThrows(
                ChaincodeException.class,
                () -> {
                    subject.createFirmwareUpdate(
                            mockedContext,
                            null,
                            "Maserati",
                            null,
                            "MC20",
                            "www.example.com",
                            "1.1",
                            "2025",
                            "1"
                    );
                }
        );

        assertTrue(exception.getMessage().contains("Invalid request."));
    }

    @Test
    void createFirmwareShouldSave() throws
            JsonProcessingException {
        when(mockedContext.getStub()).thenReturn(mockedChaincodeStub);

        final String result = subject.createFirmwareUpdate(
                mockedContext,
                firmwareUpdate.getHash(),
                firmwareUpdate.getMake(),
                null,
                firmwareUpdate.getModel(),
                firmwareUpdate.getUrl(),
                firmwareUpdate.getVersion(),
                firmwareUpdate.getYear(),
                "1"
        );

        verify(
                mockedChaincodeStub,
                times(1)
        ).putState(
                "MASERATI-MC20-2025",
                objectMapper.writeValueAsBytes(firmwareUpdate)
        );
        assertEquals(
                objectMapper.writeValueAsString(firmwareUpdate),
                result
        );
    }
}
