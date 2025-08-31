#!/usr/bin/env bash

peer lifecycle chaincode commit --tls -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt --channelID licensing --name licensing-chaincode --version 1.0.0 --sequence 1 --peerAddresses $PEER_ADDRESS:$PEER_PORT --tlsRootCertFiles /etc/hyperledger/fabric/tls/ca.crt --peerAddresses ${INSURER_CO_PEER}:${INSURER_CO_PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/insurer-co/tls/ca.crt --peerAddresses ${LICENSEE_PEER}:${LICENSEE_PEER_PORT} --tlsRootCertFiles /etc/hyperledger/fabric/licensee/tls/ca.crt --collections-config /chaincode/licensing/config/collections_config.json

exit 0
