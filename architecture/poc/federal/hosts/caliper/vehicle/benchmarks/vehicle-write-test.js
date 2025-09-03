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
        let vin = '';

        for (let i = 0; i < 17; i++) {
            vin += characters.charAt(Math.floor(Math.random() * characters.length));
        }

        const hash = vin.split('').reverse().join('');
        const vehicle = {
            schemaVersion: "1",
            vehicle: {
                hash: `${hash}`,
                vin: `${vin}`
            }
        }
        const updatedVehicle = {
            schemaVersion: "1",
                vehicle: {
                hash: `${hash}-update`,
                    vin: `${vin}`
            }
        }
        const overrideVehicle = {
            schemaVersion: "1",
            vehicle: {
                hash: `${hash}-override`,
                vin: `${vin}`
            }
        }
        const request = {
            contractId: this.roundArguments.contractId,
            contractFunction: 'recordInitialState',
            invokerIdentity: 'User1@purdue-motor-company.com',
            contractArguments: [],
            transientMap: {
                "vehicle_state_properties": JSON.stringify(vehicle)
            },
            readOnly: false
        };

        await this.sutAdapter.sendRequests(request);

        const request2 = {
            contractId: this.roundArguments.contractId,
            contractFunction: 'updateState',
            invokerIdentity: 'User1@purdue-motor-company.com',
            contractArguments: [],
            transientMap: {
                "calculated_hash": `${hash}`,
                "vehicle_state_properties": JSON.stringify(updatedVehicle)
            },
            readOnly: false
        }

        await this.sutAdapter.sendRequests(request2);

        const request3 = {
            contractId: this.roundArguments.contractId,
            contractFunction: 'overrideState',
            invokerIdentity: 'User1@purdue-motor-company.com',
            contractArguments: [],
            transientMap: {
                "vehicle_state_properties": JSON.stringify(updatedVehicle)
            },
            readOnly: false
        }

        await this.sutAdapter.sendRequests(request3);
    }
}

function createWorkloadModule() {
    return new CreateFirmwareUpdatesWorkloadModule();
}

const characters = 'ABCDEFGHJKLMNPRSTUVWXYZ0123456789';

module.exports.createWorkloadModule = createWorkloadModule;

// export VEHICLE=$(echo -n "{\"schemaVersion\":\"1\",\"vehicle\":{\"hash\":\"hash-1\",\"vin\":\"ABC-DEF\"}}" | base64 | tr -d \\n)
// export VEHICLE_UPDATE=$(echo -n "{\"schemaVersion\":\"1\",\"vehicle\":{\"hash\":\"hash-2\",\"vin\":\"ABC-DEF\"}}" | base64 | tr -d \\n)
// export VEHICLE_OVERRIDE=$(echo -n "{\"schemaVersion\":\"1\",\"vehicle\":{\"hash\":\"hash-3\",\"vin\":\"ABC-DEF\"}}" | base64 | tr -d \\n)
// export ORIGINAL_HASH=$(echo -n "hash-1" | base64 | tr -d \\n)
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C vehicle -n vehicle-state-chaincode -c '{"function": "recordInitialState", "Args":[]}' --transient "{\"vehicle_state_properties\":\"$VEHICLE\"}"
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C vehicle -n vehicle-state-chaincode -c '{"function": "isValid", "Args":["ABC-DEF", "hash-1"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C vehicle -n vehicle-state-chaincode -c '{"function": "updateState", "Args":[]}' --transient "{\"vehicle_state_properties\":\"$VEHICLE_UPDATE\",\"calculated_hash\":\"$ORIGINAL_HASH\"}"
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C vehicle -n vehicle-state-chaincode -c '{"function": "isValid", "Args":["ABC-DEF", "hash-2"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C vehicle -n vehicle-state-chaincode -c '{"function": "overrideState", "Args":[]}' --transient "{\"vehicle_state_properties\":\"$VEHICLE_OVERRIDE\"}"
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C vehicle -n vehicle-state-chaincode -c '{"function": "isValid", "Args":["ABC-DEF", "hash-3"]}'


