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
        const lastDigit = this.txIndex % 10
        const date = [
            "January 1, 2025",
            "February 2, 2025",
            "March 3, 2025",
            "April 4, 2025",
            "May 5, 2025",
            "June 6, 2025",
            "July 7, 2025",
            "August 8, 2025",
            "September 9, 2024",
            "October 10, 2024",
        ]
        const descriptions = [
            "Windshield cracks if the vehicle goes above 10 mph.",
            "Brakes don't work in 10 degrees F or below.",
            "LIDAR sensor fails if the temperature is greater than 60 degrees F.",
            "The engine explodes if you go over a bump.",
            "Seatbelts don't lock.",
            "Airbags don't deploy if the crash is head-on.",
            "Infotainment catches fire if the volume is turned up.",
            "Tires deflate when it rains.",
            "Transmission snaps in half when switching lanes.",
            "Don't buy this car!"
        ]
        const remedy = "See remedy description."
        let status = '';

        if (lastDigit % 2 === 0) {
            status = "CLOSED"
        } else {
            status = "OPEN"
        }

        for (let i = 0; i < 17; i++) {
            vin += characters.charAt(Math.floor(Math.random() * characters.length));
        }

        const request = {
            contractId: this.roundArguments.contractId,
            contractFunction: 'saveVehicleRecall',
            invokerIdentity: 'User1@purdue-motor-company.com',
            contractArguments: [
                `${campaignNumber}`,
                `${date[lastDigit]}`,
                `${descriptions[lastDigit]}`,
                `${remedy}`,
                `S{status}`,
                "1",
                make,
                model,
                `${vin}`,
            ],
            readOnly: false
        };

        await this.sutAdapter.sendRequests(request);

        const request2 = {
            contractId: this.roundArguments.contractId,
            contractFunction: 'getVehicleRecall',
            invokerIdentity: 'User1@purdue-motor-company.com',
            contractArguments: [
                `${campaignNumber}`,
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

// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C recall -n recall-notifications-chaincode -c '{"function": "saveVehicleRecall", "Args":["recall # abc", "September 4, 2025", "This car is dangerous!", "No remedy program available.", "OPEN", "1", "Purdue Motor Company", "Boilermaker", "VIN123"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C recall -n recall-notifications-chaincode -c '{"function": "getVehicleRecall", "Args":["recall # abc", "Purdue Motor Company", "Boilermaker", "VIN123"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C recall -n recall-notifications-chaincode -c '{"function": "saveVehicleRecall", "Args":["recall # abc", "September 4, 2025", "This car is dangerous!", "No remedy program available.", "CLOSED", "1", "Purdue Motor Company", "Boilermaker", "VIN123"]}'
// peer chaincode invoke  -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --peerAddresses ${PEER_ADDRESS}:${PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt -C recall -n recall-notifications-chaincode -c '{"function": "getVehicleRecall", "Args":["recall # abc", "Purdue Motor Company", "Boilermaker", "VIN123"]}'
