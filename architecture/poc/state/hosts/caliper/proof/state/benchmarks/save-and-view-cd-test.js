'use strict';

const fs = require('fs');
const os = require('os');
const { WorkloadModuleBase } = require('@hyperledger/caliper-core');

class IssueRegistrationWorkloadModule extends WorkloadModuleBase {
    constructor() {
        super();

        this.txIndex = -1;
    }

    async initializeWorkloadModule(workerIndex, totalWorkers, roundIndex, roundArguments, sutAdapter, sutContext) {
        await super.initializeWorkloadModule(workerIndex, totalWorkers, roundIndex, roundArguments, sutAdapter, sutContext);
    }

    async submitTransaction() {
        this.txIndex++;
        let registrationNumber = '';
        const lastDigit = this.txIndex % 10

        for (let i = 0; i < 8; i++) {
            registrationNumber += characters.charAt(Math.floor(Math.random() * characters.length));
        }

        const names = [
            "Andrew Doe",
            "Bess Doe",
            "Maya Doe",
            "Samson Doe",
            "Rylee Doe",
            "Rhett Doe",
            "Cooper Doe",
            "Reese Doe",
            "Sutton Doe",
            "John Doe"
        ]
        const request = {
            contractId: this.roundArguments.contractId,
            contractFunction: 'saveCertificateOfDeposit',
            invokerIdentity: 'User1@dmv.alabama.gov',
            contractArguments: [
                "35000",
                `${names[lastDigit]}`,
                `${registrationNumber}`,
                "1"
            ],
            readOnly: false
        };

        await this.sutAdapter.sendRequests(request);

        const request2 = {
            contractId: this.roundArguments.contractId,
            contractFunction: 'viewProof',
            invokerIdentity: 'User1@dmv.alabama.gov',
            contractArguments: [`${registrationNumber}`],
            readOnly: true
        }

        await this.sutAdapter.sendRequests(request2);

        const request3 = {
            contractId: this.roundArguments.contractId,
            contractFunction: 'revokeCertificateOfDeposits',
            invokerIdentity: 'User1@dmv.alabama.gov',
            contractArguments: [`${registrationNumber}`],
            readOnly: true
        }

        await this.sutAdapter.sendRequests(request3);
    }
}

function createWorkloadModule() {
    return new IssueRegistrationWorkloadModule();
}

const characters = 'ABCDEFGHJKLMNPRSTUVWXYZ0123456789';

module.exports.createWorkloadModule = createWorkloadModule;

// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C proof -n proof-chaincode -c '{"function": "saveCertificateOfDeposit", "Args":["35000", "John Doe", "OH-123", "1"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C proof -n proof-chaincode -c '{"function": "viewProof", "Args":["OH-123"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C proof -n proof-chaincode -c '{"function": "revokeCertificateOfDeposits", "Args":["OH-123"]}'
