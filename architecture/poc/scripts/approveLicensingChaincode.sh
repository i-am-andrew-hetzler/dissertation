#!/usr/bin/env bash

export LICENSING_PACKAGE_ID=`peer lifecycle chaincode calculatepackageid licensing-1.0.0.tar.gz`

peer lifecycle chaincode approveformyorg -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --channelID licensing --package-id $LICENSING_PACKAGE_ID --name licensing-chaincode --version 1.0.0 --sequence 1 --collections-config /chaincode/licensing/config/collections_config.json

exit 0
