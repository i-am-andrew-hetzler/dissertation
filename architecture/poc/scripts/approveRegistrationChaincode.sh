#!/usr/bin/env bash

export REGISTRATION_PACKAGE_ID=`peer lifecycle chaincode calculatepackageid registration-1.0.0.tar.gz`

peer lifecycle chaincode approveformyorg -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --channelID registration --package-id $REGISTRATION_PACKAGE_ID --name registration-chaincode --version 1.0.0 --sequence 1 --collections-config /chaincode/registration/config/collections_config.json

exit 0
