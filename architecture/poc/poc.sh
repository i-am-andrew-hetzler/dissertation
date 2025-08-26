#!/usr/bin/env bash

function header() {
  echo "******************************************************"
  echo $1
  echo "******************************************************"
}

function usage() {
    echo "There are three flags to run this script.\n"
    echo "-d <network> Deletes the specified network.\n"
    echo "EXAMPLE: sh run.sh -d federal\n\n"
    echo "-q <network> Quits (stops) the specified network.\n"
    echo "EXAMPLE: sh run.sh -q federal\n\n"
    echo "-s <network> Starts the specified network.\n"
    echo "EXAMPLE: sh run.sh -s federal\n\n\n"
}

function checkNetwork() {
  case $network in
    federal);;
    *)
      echo "Invalid network name. See usage"
      exit 1
      ;;
  esac
}

function deleteFederalNetwork() {
  echo "Deleting files for the federal network..."

  rm -rf federal/cryptography/certs
  rm -rf ../code/firmware_updates/FirmwareUpdates/build
  rm -rf ../code/fmvss_certification/FmvssCertification/build
  rm -rf ../code/recall_notifications/RecallNotifications/build
  rm -rf ../code/vehicle_state/VehicleState/build
  rm -f federal/hosts/dot-orderer-1/etc/firmware.pb
  rm -f federal/hosts/dot-orderer-1/etc/fmvss.pb
  rm -f federal/hosts/dot-orderer-1/etc/recall.pb
  rm -f federal/hosts/dot-orderer-1/etc/vehicle.pb
  rm -rf federal/hosts/dot-orderer-1/var/orderer
  rm -rf federal/hosts/dot-orderer-1/var/production

  echo "Deleted files for the federal network"
}

function delete() {
  checkNetwork $network

  case "${network}" in
    federal) deleteFederalNetwork;;
  esac
}

function quit() {
  checkNetwork $network

  case "${network}" in
    federal)
      docker compose -f docker-compose-federal.yaml down
      ;;
  esac

  echo "quit"
}

function generateCryptography() {
  echo "Generating cryptography for $1..."

  $binDirectory/cryptogen generate --config $2 --output=federal/cryptography/certs/

  echo "Generated cryptography for $1."
}

function generateFederalCryptography() {
  generateCryptography "Department of Transportation" federal/cryptography/config/crypto-config-dot-orderer.yaml
  generateCryptography NHTSA federal/cryptography/config/crypto-config-nhtsa-peer.yaml
}

function buildChaincode() {
  echo "Building $1 chaincode..."
  ./$2gradlew installDist -p $2
  echo "Built $1 chaincode."
}

function buildFederalChaincode() {
  buildChaincode "Firmware Updates" ../code/firmware_updates/FirmwareUpdates/
  buildChaincode "FMVSS Certification" ../code/fmvss_certification/FmvssCertification/
  buildChaincode "Recall Notifications" ../code/recall_notifications/RecallNotifications/
  buildChaincode "Vehicle State" ../code/vehicle_state/VehicleState/
}

function generateGenesisBlock() {
  echo "Generating genesis block for $1 channel..."
  $binDirectory/configtxgen -configPath $2 -profile $3 -outputBlock federal/hosts/dot-orderer-1/etc/$4 -channelID $5
  echo "Generated genesis block for $1 channel."
}

function generateFederalGenesisBlocks() {
  generateGenesisBlock firmware federal/config/ firmware firmware.pb firmware
  generateGenesisBlock fmvss federal/config/ fmvss fmvss.pb fmvss
  generateGenesisBlock recall federal/config/ recall recall.pb recall
  generateGenesisBlock vehicle federal/config/ vehicle vehicle.pb vehicle
}

function start() {
  checkNetwork $network

  header "RUNNING PREFLIGHT TASKS..."
  echo "Downloading javaenv..."
  docker pull hyperledger/fabric-javaenv:2.5.6
  echo "Downloaded javaenv."

  case "${network}" in
    federal)
      generateFederalCryptography
      buildFederalChaincode
      generateFederalGenesisBlocks
      ;;
  esac

  header "FINISHED PREFLIGHT TASKS.\n\n"
  header "STARTING THE NETWORK..."

  case "${network}" in
    federal)
      docker compose -f docker-compose-federal.yaml up --build -d
      ;;
  esac

  header "STARTED THE NETWORK."
}

function setBinDirectory() {
  case "${os}" in
    Darwin)
      if [[ "$cpu" == "x86_64" ]]; then
        binDirectory=./bin/Darwin/x64
      else
        binDirectory=./bin/Darwin/arm
      fi
      ;;
    Linux)
      if [[ "$cpu" == "x86_64" ]]; then
        binDirectory=./bin/linux/x64
      else
        binDirectory=./bin/linux/arm
      fi
      ;;
    *)
      echo "Unsupported operating system!"
      exit 1
  esac
}

os=$(uname -s)
cpu=$(uname -m)

setBinDirectory

echo "Operating System:  $os"
echo "CPU Architecture:  $cpu\n\n"

while getopts d:q:s: flag; do
    case "${flag}" in
        d) network=${OPTARG} delete;;
        q) network=${OPTARG} quit;;
        s) network=${OPTARG} start;;
        *) usage;;
    esac
done

exit 0
