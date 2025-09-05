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

        for (let i = 0; i < 8; i++) {
            registrationNumber += characters.charAt(Math.floor(Math.random() * characters.length));
        }

        const other = {
            logo: "http://www.example.com/logo.png"
        }
        const request = {
            contractId: this.roundArguments.contractId,
            contractFunction: 'saveSelfInsurance',
            invokerIdentity: 'User1@dmv.alabama.gov',
            contractArguments: [
                "50000",
                "Acme Corporation",
                "John Doe",
                JSON.stringify(other),
                "CEO",
                "1",
                `${registrationNumber}`,
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
            contractFunction: 'revokeSelfInsurance',
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

// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C proof -n proof-chaincode -c '{"function": "saveSelfInsurance", "Args":["35000", "Acme Corporation", "John Doe", "{\"logo\":\"example.png\"}", "CEO", "1", "OH-123"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C proof -n proof-chaincode -c '{"function": "viewProof", "Args":["OH-123"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C proof -n proof-chaincode -c '{"function": "revokeSelfInsurance", "Args":["OH-123"]}'
