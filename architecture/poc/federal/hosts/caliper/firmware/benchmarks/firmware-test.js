'use strict';

const fs = require('fs');
const os = require('os');
const { WorkloadModuleBase } = require('@hyperledger/caliper-core');

class CreateFirmwareUpdatesWorkloadModule extends WorkloadModuleBase {
    constructor() {
        super();

        this.txIndex = -1;
    }

    async initializeWorkloadModule(workerIndex, totalWorkers, roundIndex, roundArguments, sutAdapter, sutContext) {
        await super.initializeWorkloadModule(workerIndex, totalWorkers, roundIndex, roundArguments, sutAdapter, sutContext);
    }

    async submitTransaction() {
        this.txIndex++;

        const lastDigit = this.txIndex % 10
        const models = [
            "Boilermaker",
            "Purdue Pete",
            "Gold",
            "Black",
            "Lafayette",
            "Westward",
            "Hammer",
            "Train",
            "Indy",
            "Husky"
        ]
        const year = [
            2020,
            2021,
            2022,
            2023,
            2024,
            2025,
            2019,
            2018,
            2017,
            2016
        ]
        const metadata = {
            publisher: "Purdue Motor Company",
            releaseNotes: "Here is a new release."
        }
        const majorVersion = this.txIndex % 100
        const version = `${majorVersion}.${lastDigit}.${this.txIndex}`
        const request = {
            contractId: this.roundArguments.contractId,
            contractFunction: 'createFirmwareUpdate',
            invokerIdentity: 'User1@purdue-motor-company.com',
            contractArguments: [
                `hash-${lastDigit}`,
                "Purdue Motor Company",
                JSON.stringify(metadata),
                models[lastDigit],
                `https://firmware.purdue-motor-company.com/firmware-${version}.bin`,
                String(version),
                String(year[lastDigit]),
                String(1)
            ],
            readOnly: false
        };

        await this.sutAdapter.sendRequests(request);

        const request2 = {
            contractId: this.roundArguments.contractId,
            contractFunction: 'checkForUpdate',
            invokerIdentity: 'User1@purdue-motor-company.com',
            contractArguments: [
                "Purdue Motor Company",
                models[lastDigit],
                String(year[lastDigit]),
            ],
            readOnly: true
        }

        await this.sutAdapter.sendRequests(request2);
    }
}

function createWorkloadModule() {
    return new CreateFirmwareUpdatesWorkloadModule();
}

module.exports.createWorkloadModule = createWorkloadModule;

// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C firmware -n firmware-updates-chaincode -c '{"function": "createFirmwareUpdate", "Args":["hash-0", "Purdue MoCo", null, "Boilermaker", "url-here", "12.1", "2025", "1"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C firmware -n firmware-updates-chaincode -c '{"function": "checkForUpdate", "Args":["Purdue MoCo", "Boilermaker", "2025"]}'

