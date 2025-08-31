#!/usr/bin/env bash

export PROOF_PACKAGE_ID=`peer lifecycle chaincode calculatepackageid proof-1.0.0.tar.gz`

peer lifecycle chaincode approveformyorg -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --channelID proof --package-id $PROOF_PACKAGE_ID --name proof-chaincode --version 1.0.0 --sequence 1 --collections-config /chaincode/proof/config/collections_config.json

exit 0
