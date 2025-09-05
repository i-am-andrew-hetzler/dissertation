'use strict';

const fs = require('fs');
const os = require('os');
const { WorkloadModuleBase } = require('@hyperledger/caliper-core');

class RevokeRegistrationWorkloadModule extends WorkloadModuleBase {
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

        for (let i = 0; i < 10; i++) {
            registrationNumber += characters.charAt(Math.floor(Math.random() * characters.length));
        }
        const addresses = [
            {
                "street1": "123 Main St",
                "street2": "Apt 1",
                "city": "West Lafayette",
                "county": "Wayne",
                "zipCode": "12345"
            }
        ]
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
            year: years[lastDigit]
        }
        const other = {
            isAutomatedVehicle: "true"
        }
        const request = {
            contractId: this.roundArguments.contractId,
            contractFunction: 'issueRegistration',
            invokerIdentity: 'User1@dmv.sd.gov',
            contractArguments: [
                JSON.stringify(other),
                JSON.stringify(addresses),
                "Jane Doe",
                "HLF certificate id here",
                `${registrationNumber}`,
                JSON.stringify(vehicleDescription),
                "1"
            ],
            readOnly: false
        };

        await this.sutAdapter.sendRequests(request);

        const request2 = {
            contractId: this.roundArguments.contractId,
            contractFunction: 'revokeRegistration',
            invokerIdentity: 'User1@dmv.sd.gov',
            contractArguments: [`${registrationNumber}`],
            readOnly: true
        }

        await this.sutAdapter.sendRequests(request2);
    }
}

function createWorkloadModule() {
    return new RevokeRegistrationWorkloadModule();
}

const characters = 'ABCDEFGHJKLMNPRSTUVWXYZ0123456789';

module.exports.createWorkloadModule = createWorkloadModule;

// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C registration -n registration-chaincode -c '{"function": "issueRegistration", "Args":["{\"isAutomatedVehicle\":\"true\"}", "[{\"street1\":\"123 Test Rd.\", \"street2\":\"Apartment 1\", \"city\":\"Example\", \"county\":\"Wayne\", \"zipCode\":\"12345\"}]", "Jane Doe", "HLF certificate id here", "OH-123", "{\"make\":\"Purdue Motor Company\"}", "1"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C registration -n registration-chaincode -c '{"function": "viewRegistration", "Args":["OH-123"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C registration -n registration-chaincode -c '{"function": "revokeRegistration", "Args":["OH-123"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C registration -n registration-chaincode -c '{"function": "viewRegistration", "Args":["OH-123"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C registration -n registration-chaincode -c '{"function": "viewRegistrationIn3rdPartyCollection", "Args":["OH-123"]}'
