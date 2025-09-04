'use strict';

const fs = require('fs');
const os = require('os');
const { WorkloadModuleBase } = require('@hyperledger/caliper-core');

class FmvssWorkloadModule extends WorkloadModuleBase {
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

        for (let i = 0; i < 17; i++) {
            vin += characters.charAt(Math.floor(Math.random() * characters.length));
        }

        const lastDigit = this.txIndex % 10
        const month = [
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
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
        const docTables = []
        const request = {
            contractId: this.roundArguments.contractId,
            contractFunction: 'certifyMotorVehicle',
            invokerIdentity: 'User1@purdue-motor-company.com',
            contractArguments: [
                `${vin}`,
                "This vehicle conforms to the safety standards of the FMVSS.",
                JSON.stringify(docTables),
                "[{\"front\":\"front weight\",\"intermediate\":[],\"order\":\"1\",\"rear\":\"rear weight\"}]",
                "[{\"order\":\"1\",\"value\":\"10000 lbs\"}]",
                String(month[lastDigit]),
                String(year[lastDigit]),
                "Purdue Motor Company",
                "Maya Importer",
                "CAR",
                String(1)
            ],
            readOnly: false
        };

        await this.sutAdapter.sendRequests(request);

        const request2 = {
            contractId: this.roundArguments.contractId,
            contractFunction: 'viewCertification',
            invokerIdentity: 'User1@purdue-motor-company.com',
            contractArguments: [
                `${vin}`
            ],
            readOnly: true
        }

        await this.sutAdapter.sendRequests(request2);
    }
}

function createWorkloadModule() {
    return new FmvssWorkloadModule();
}

const characters = 'ABCDEFGHJKLMNPRSTUVWXYZ0123456789';

module.exports.createWorkloadModule = createWorkloadModule;

// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C fmvss -n fmvss-certification-chaincode -c '{"function": "certifyImportedVehicle", "Args":["imported-VIN", "This conforms to imported.", "Andrew Imports", "2025", "This complies with VIN.", "1"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C fmvss -n fmvss-certification-chaincode -c '{"function": "viewCertification", "Args":["imported-VIN"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C fmvss -n fmvss-certification-chaincode -c '{"function": "certifyMotorVehicle", "Args":["motor-VIN", "This conforms to motor.", null, "[{\"front\":\"front weight\",\"intermediate\":[],\"order\":\"1\",\"rear\":\"rear weight\"}]", "[{\"order\":\"1\",\"value\":\"10000 lbs\"}]", "May", "2025", "Purdue MoCo", "Bess Imports", "CAR", "1"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C fmvss -n fmvss-certification-chaincode -c '{"function": "viewCertification", "Args":["motor-VIN"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C fmvss -n fmvss-certification-chaincode -c '{"function": "certifyReplicaVehicle", "Args":["replica-VIN", "This is exempt.", "[{\"front\":\"front weight\",\"intermediate\":[],\"order\":\"1\",\"rear\":\"rear weight\"}]", "[{\"order\":\"1\",\"value\":\"10000 lbs\"}]", "May", "2025", "Purdue MoCo", "Replica statement", "1"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C fmvss -n fmvss-certification-chaincode -c '{"function": "viewCertification", "Args":["replica-VIN"]}'

// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C fmvss -n fmvss-certification-chaincode -c '{"function": "certifyIncompleteVehicle", "Args":["incomplete-VIN", "[{\"front\":\"front weight\",\"intermediate\":[],\"order\":\"1\",\"rear\":\"rear weight\"}]", "[{\"order\":\"1\",\"value\":\"10000 lbs\"}]", "May", "2025", "Purdue MoCo", "1"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C fmvss -n fmvss-certification-chaincode -c '{"function": "viewCertification", "Args":["incomplete-VIN"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C fmvss -n fmvss-certification-chaincode -c '{"function": "certifyIntermediateVehicle", "Args":["incomplete-VIN", null, null, "May", "2025", "Purdue MoCo", "1"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C fmvss -n fmvss-certification-chaincode -c '{"function": "viewCertification", "Args":["incomplete-VIN"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C fmvss -n fmvss-certification-chaincode -c '{"function": "certifyFinalVehicle", "Args":["incomplete-VIN", "Conforms to final.", "[{\"front\":\"front weight\",\"intermediate\":[],\"order\":\"1\",\"rear\":\"rear weight\"}]", "[{\"order\":\"1\",\"value\":\"10000 lbs\"}]", "August", "2025", "Maya MoCo", "TRUCK", "1"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C fmvss -n fmvss-certification-chaincode -c '{"function": "viewCertification", "Args":["incomplete-VIN"]}'

// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C fmvss -n fmvss-certification-chaincode -c '{"function": "certifyMotorVehicle", "Args":["altered-VIN", "This conforms to motor.", null, "[{\"front\":\"front weight\",\"intermediate\":[],\"order\":\"1\",\"rear\":\"rear weight\"}]", "[{\"order\":\"1\",\"value\":\"10000 lbs\"}]", "May", "2025", "Purdue MoCo", "Bess Imports", "CAR", "1"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C fmvss -n fmvss-certification-chaincode -c '{"function": "certifyAlteredVehicle", "Args":["altered-VIN", "This conforms to altered.", null, null, "CAR", "1"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C fmvss -n fmvss-certification-chaincode -c '{"function": "viewCertification", "Args":["altered-VIN"]}'

