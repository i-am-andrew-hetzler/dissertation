'use strict';

const fs = require('fs');
const os = require('os');
const { WorkloadModuleBase } = require('@hyperledger/caliper-core');

class IssueLicenseWorkloadModule extends WorkloadModuleBase {
    constructor() {
        super();

        this.txIndex = -1;
    }

    async initializeWorkloadModule(workerIndex, totalWorkers, roundIndex, roundArguments, sutAdapter, sutContext) {
        await super.initializeWorkloadModule(workerIndex, totalWorkers, roundIndex, roundArguments, sutAdapter, sutContext);
    }

    async submitTransaction() {
        this.txIndex++;
        let licenseNumber = '';

        for (let i = 0; i < 10; i++) {
            licenseNumber += characters.charAt(Math.floor(Math.random() * characters.length));
        }
        const classes = [
            "A",
            "D"
        ]
        const addresses = [
            {
                "street1": "123 Main St",
                "street2": "Apt 1",
                "city": "West Lafayette",
                "state": "SD",
                "zipCode": "12345"
            }
        ]
        const licenseDescription = {
            height: "6'",
            eyeColor: "red"
        }
        const other = {
            lifetimeHuntingLicense: "SD-HU-123456789"
        }
        const request = {
            contractId: this.roundArguments.contractId,
            contractFunction: 'issueLicense',
            invokerIdentity: 'User1@dmv.sd.gov',
            contractArguments: [
                JSON.stringify(classes),
                `${licenseNumber}`,
                JSON.stringify(addresses),
                "01",
                "01",
                "1900",
                JSON.stringify(licenseDescription),
                "false",
                "Jane Doe",
                "serialized-photo-byte-array-here",
                "serialized-signature-byte-array-here",
                JSON.stringify(other),
                "1",
                "x509::CN=Admin@dmv.sd.gov, OU=admin, L=San Francisco, ST=California, C=US::CN=ca.dmv.sd.gov, O=dmv.sd.gov, L=San Francisco, ST=California, C=US"
            ],
            readOnly: false
        };

        await this.sutAdapter.sendRequests(request);

        const request2 = {
            contractId: this.roundArguments.contractId,
            contractFunction: 'cancelLicense',
            invokerIdentity: 'User1@dmv.sd.gov',
            contractArguments: [`${licenseNumber}`],
            readOnly: true
        }

        await this.sutAdapter.sendRequests(request2);
    }
}

function createWorkloadModule() {
    return new IssueLicenseWorkloadModule();
}

const characters = 'ABCDEFGHJKLMNPRSTUVWXYZ0123456789';

module.exports.createWorkloadModule = createWorkloadModule;

// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C licensing -n licensing-chaincode -c '{"function": "issueLicense", "Args":["[\"A\",\"B\"]", "OH-123", "[{\"street1\":\"123 Test Rd.\", \"street2\":\"Apartment 1\", \"city\":\"Example\", \"state\":\"SD\", \"zipCode\":\"12345\"}]", "28", "01", "1900", "{\"eyeColor\":\"green\"}", "false", "Jane Doe", "serialized-photo-byte-array-here", "serialized-signature-byte-array-here", "{\"huntingLicense\":\"ABC-123\"}", "1", "x509::CN=Admin@dmv.sd.gov, OU=admin, L=San Francisco, ST=California, C=US::CN=ca.dmv.sd.gov, O=dmv.sd.gov, L=San Francisco, ST=California, C=US"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C licensing -n licensing-chaincode -c '{"function": "viewLicense", "Args":["OH-123"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C licensing -n licensing-chaincode -c '{"function": "viewLicenseIn3rdPartyCollection", "Args":["OH-123"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C licensing -n licensing-chaincode -c '{"function": "cancelLicense", "Args":["OH-123"]}'
