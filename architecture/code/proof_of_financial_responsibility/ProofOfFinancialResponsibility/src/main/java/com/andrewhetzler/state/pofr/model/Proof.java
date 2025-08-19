package com.andrewhetzler.state.pofr.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.hyperledger.fabric.contract.annotation.DataType;

import java.util.List;
import java.util.Map;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/19/25
 **/
@JsonPropertyOrder(alphabetic = true)
@DataType
public class Proof {
    private final List<Map<String, String>> bonds;
    private final List<CertificateOfDeposit> certificateOfDeposits;
    private final Insurance insurance;
    private final String schemaVersion;

}
