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

        const years = [
            "2020",
            "2021",
            "2022",
            "2023",
            "2024",
            "2025",
            "2019",
            "2018",
            "2017",
            "2016"
        ];
        const vehicleDescription = {
            make: "Purdue Motor Company",
            model: "Boilermaker",
            year: `${years[lastDigit]}`
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
        const insured = [
            {
                name: `${names[lastDigit]}`
            }
        ]
        const other = {
            logo: "http://www.example.com/logo.png"
        }
        const request = {
            contractId: this.roundArguments.contractId,
            contractFunction: 'saveInsurance',
            invokerIdentity: 'User1@dmv.alabama.gov',
            contractArguments: [
                JSON.stringify(vehicleDescription),
                JSON.stringify(insured),
                "01/01/2025",
                "12/31/2025",
                "InsurerCo Company",
                JSON.stringify(other),
                "Policy 123",
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
            contractFunction: 'cancelInsurance',
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

// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C proof -n proof-chaincode -c '{"function": "saveInsurance", "Args":["{\"make\":\"Purdue Motor Company\"}", "[{\"name\":\"John Doe\"}]", "01/01/25", "06/01/25", "Insurer Co", "{\"logo\":\"example.png\"}", "Policy # 123", "OH-123", "1"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C proof -n proof-chaincode -c '{"function": "viewProof", "Args":["OH-123"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C proof -n proof-chaincode -c '{"function": "cancelInsurance", "Args":["OH-123"]}'
