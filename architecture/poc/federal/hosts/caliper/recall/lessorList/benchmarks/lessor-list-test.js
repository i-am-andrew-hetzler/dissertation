'use strict';

const fs = require('fs');
const os = require('os');
const { WorkloadModuleBase } = require('@hyperledger/caliper-core');

class LessorListWorkloadModule extends WorkloadModuleBase {
    constructor() {
        super();

        this.txIndex = -1;
    }

    async initializeWorkloadModule(workerIndex, totalWorkers, roundIndex, roundArguments, sutAdapter, sutContext) {
        await super.initializeWorkloadModule(workerIndex, totalWorkers, roundIndex, roundArguments, sutAdapter, sutContext);
    }

    async submitTransaction() {
        this.txIndex++;
        let vin = '';
        const campaignNumber = `RECALL-${this.txIndex}-${Math.random()}`;
        const cities = [
            "West Lafayette",
            "Bloomington",
            "Indianapolis",
            "Fort Wayne",
            "South Bend",
            "Memphis",
            "Franklin",
            "Carmel",
            "Frankfurt",
            "Carlisle"
        ];
        const zipCodes = [
            "11111",
            "22222",
            "33333",
            "44444",
            "55555",
            "66666",
            "77777",
            "88888",
            "99999",
            "00000"
        ]
        const lastDigit = this.txIndex % 10
        let street2 = '';

        if (this.txIndex % 2 === 0) {
            street2 = "Apartment A";
        } else {
            street2 = null;
        }

        for (let i = 0; i < 17; i++) {
            vin += characters.charAt(Math.floor(Math.random() * characters.length));
        }

        const request = {
            contractId: this.roundArguments.contractId,
            contractFunction: 'saveLessorsListForRecall',
            invokerIdentity: 'User1@bess-leasing-company.com',
            contractArguments: [
                `${vin}`,
                "Jane Doe",
                "123 Test Road",
                `${street2}`,
                `${cities[lastDigit]}`,
                "Indiana",
                `${zipCodes[lastDigit]}`,
                "Purdue Motor Company",
                "Boilermaker",
                `${campaignNumber}`,
                "September 1, 2025",
                "2024",
                "1",
                "BessLeasingCompanyMSP"
            ],
            readOnly: false
        };

        await this.sutAdapter.sendRequests(request);

        const request2 = {
            contractId: this.roundArguments.contractId,
            contractFunction: 'viewLessorsListForRecall',
            invokerIdentity: 'User1@bess-leasing-company.com',
            contractArguments: [
                campaignNumber,
                "BessLeasingCompanyMSP"
            ],
            readOnly: true
        }

        await this.sutAdapter.sendRequests(request2);
    }
}

function createWorkloadModule() {
    return new LessorListWorkloadModule();
}

const characters = 'ABCDEFGHJKLMNPRSTUVWXYZ0123456789';

module.exports.createWorkloadModule = createWorkloadModule;

// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C recall -n recall-notifications-chaincode -c '{"function": "saveLessorsListForRecall", "Args":["VIN123", "John Doe", "123 Test Road", null, "Test", "OH", "12345", "Purdue Motor Company", "Boilermaker", "recall # def", "September 1, 2025", "2024", "1", "BessLeasingCompanyMSP"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C recall -n recall-notifications-chaincode -c '{"function": "viewLessorsListForRecall", "Args":["recall # def", "BessLeasingCompanyMSP"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C recall -n recall-notifications-chaincode -c '{"function": "saveLessorsListForRecall", "Args":["VIN987", "John Doe", "123 Test Road", null, "Test", "OH", "12345", "Purdue Motor Company", "Boilermaker", "recall # def", "September 1, 2025", "2024", "1", "BessLeasingCompanyMSP"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C recall -n recall-notifications-chaincode -c '{"function": "viewLessorsListForRecall", "Args":["recall # def", "BessLeasingCompanyMSP"]}'
