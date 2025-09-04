'use strict';

const fs = require('fs');
const os = require('os');
const { WorkloadModuleBase } = require('@hyperledger/caliper-core');

class VehicleListWorkloadModule extends WorkloadModuleBase {
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
        const campaignNumber = `RECALL-${this.txIndex}`;
        const make = "Purdue Motor Company";
        const model = "Boilermaker";

        for (let i = 0; i < 17; i++) {
            vin += characters.charAt(Math.floor(Math.random() * characters.length));
        }

        const request = {
            contractId: this.roundArguments.contractId,
            contractFunction: 'saveRecallListForVehicle',
            invokerIdentity: 'User1@purdue-motor-company.com',
            contractArguments: [
                campaignNumber,
                make,
                model,
                `${vin}`,
            ],
            readOnly: false
        };

        await this.sutAdapter.sendRequests(request);

        const request2 = {
            contractId: this.roundArguments.contractId,
            contractFunction: 'getRecallListForVehicle',
            invokerIdentity: 'User1@purdue-motor-company.com',
            contractArguments: [
                make,
                model,
                `${vin}`,
            ],
            readOnly: true
        }

        await this.sutAdapter.sendRequests(request2);
    }
}

function createWorkloadModule() {
    return new VehicleListWorkloadModule();
}

const characters = 'ABCDEFGHJKLMNPRSTUVWXYZ0123456789';

module.exports.createWorkloadModule = createWorkloadModule;

// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C recall -n recall-notifications-chaincode -c '{"function": "saveRecallListForVehicle", "Args":["recall # def", "Purdue Motor Company", "Boilermaker", "VIN123"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C recall -n recall-notifications-chaincode -c '{"function": "getRecallListForVehicle", "Args":["Purdue Motor Company", "Boilermaker", "VIN123"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C recall -n recall-notifications-chaincode -c '{"function": "saveRecallListForVehicle", "Args":["recall # def", "Purdue Motor Company", "Boilermaker", "VIN123"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C recall -n recall-notifications-chaincode -c '{"function": "getRecallListForVehicle", "Args":["Purdue Motor Company", "Boilermaker", "VIN123"]}'
