#!/usr/bin/env bash

function getChannelConfig() {
  peer channel fetch config config_update/original/current_config_"$1".pb -c "$1" -o ${ORDERER_ADDRESS}:${ORDERER_PORT} --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt
}

function decodeChannelConfig() {
  configtxlator proto_decode --input config_update/original/current_config_"$1".pb --type common.Block --output config_update/original/decoded_current_config_"$1"_block.json
  jq .data.data[0].payload.data.config config_update/original/decoded_current_config_"$1"_block.json > config_update/original/decoded_payload_current_config_"$1"_block.json
}

function appendAnchorPeers() {
  jq '.channel_group.groups.Application.groups.'${PEER_MSP}'.values += {"AnchorPeers":{"mod_policy": "Admins","value":{"anchor_peers": [{"host": "'${PEER_ADDRESS}'","port": '${PEER_PORT}'}]},"version": "0"}}' config_update/original/decoded_payload_current_config_"$1"_block.json > config_update/update/modified_config_"$1"_block.json
}

function encodeChannelUpdate() {
  configtxlator proto_encode --input config_update/original/decoded_payload_current_config_"$1"_block.json --type common.Config --output config_update/original/original_config_"$1".pb
  configtxlator proto_encode --input config_update/update/modified_config_"$1"_block.json --type common.Config --output config_update/update/modified_config_"$1".pb
  configtxlator compute_update --channel_id "$1" --original config_update/original/original_config_"$1".pb --updated config_update/update/modified_config_"$1".pb --output config_update/update/updated_config_"$1".pb
  configtxlator proto_decode --input config_update/update/updated_config_"$1".pb --type common.ConfigUpdate --output config_update/update/updated_config_"$1".json
}

function createChannelUpdateEnvelope() {
  echo '{"payload":{"header":{"channel_header":{"channel_id":"'${1}'", "type":2}},"data":{"config_update":'$(cat config_update/update/updated_config_"$1".json)'}}}' | jq . > config_update/update/config_"$1"_update_in_envelope.json

  configtxlator proto_encode --input config_update/update/config_"$1"_update_in_envelope.json --type common.Envelope --output config_update/update/${PEER_ADDRESS}_"$1"_anchors.tx
}

function joinAnchorPeers() {
  getChannelConfig $1
  decodeChannelConfig $1
  appendAnchorPeers $1
  encodeChannelUpdate $1
  createChannelUpdateEnvelope $1
  peer channel update -o ${ORDERER_ADDRESS}:${ORDERER_PORT} -c "$1" -f config_update/update/${PEER_ADDRESS}_"$1"_anchors.tx --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt
}

mkdir -p config_update/original
mkdir config_update/update

joinAnchorPeers firmware
joinAnchorPeers fmvss
joinAnchorPeers recall
joinAnchorPeers vehicle

exit 0
