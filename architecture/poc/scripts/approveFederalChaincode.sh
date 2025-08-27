#!/usr/bin/env bash

export FIRMWARE_PACKAGE_ID=`peer lifecycle chaincode calculatepackageid firmware-1.0.0.tar.gz`
export FMVSS_PACKAGE_ID=`peer lifecycle chaincode calculatepackageid fmvss-1.0.0.tar.gz`
export RECALL_PACKAGE_ID=`peer lifecycle chaincode calculatepackageid recall-1.0.0.tar.gz`
export VEHICLE_PACKAGE_ID=`peer lifecycle chaincode calculatepackageid vehicle-1.0.0.tar.gz`

peer lifecycle chaincode approveformyorg -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --channelID firmware --package-id $FIRMWARE_PACKAGE_ID --name firmware-updates-chaincode --version 1.0.0 --sequence 1
peer lifecycle chaincode approveformyorg -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --channelID fmvss --package-id $FMVSS_PACKAGE_ID --name fmvss-certification-chaincode --version 1.0.0 --sequence 1
peer lifecycle chaincode approveformyorg -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --channelID recall --package-id $RECALL_PACKAGE_ID --name recall-notifications-chaincode --version 1.0.0 --sequence 1 /chaincode/recall/config/collections_config.json
peer lifecycle chaincode approveformyorg -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --channelID vehicle --package-id $VEHICLE_PACKAGE_ID --name vehicle-state-chaincode --version 1.0.0 --sequence 1 --collections-config /chaincode/vehicle/config/collections_config.json

exit 0
