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
    pofr);;
    licensing);;
    registration);;
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
  rm -rf federal/hosts/nhtsa-peer0/hyperledger
  rm -rf federal/hosts/purdue-motor-company-peer0/hyperledger
  rm -rf federal/hosts/bess-leasing-company-peer0/hyperledger

  echo "Deleted files for the federal network"
}

function deleteStateCryptography() {
  echo "Deleting state cryptography..."

  rm -rf state/cryptography/certs

  echo "Deleted state cryptography."
}

function deleteLicensingNetwork() {
  deleteStateCryptography
  rm -rf ../code/licensing/Licensing/build
  rm -f state/hosts/fl-orderer-1/etc/licensing.pb
  rm -f state/hosts/sd-orderer-1/etc/licensing.pb
  rm -f state/hosts/wv-orderer-1/etc/licensing.pb
  rm -rf state/hosts/fl-orderer-1/var/orderer
  rm -rf state/hosts/fl-orderer-1/var/production
  rm -rf state/hosts/sd-orderer-1/var/orderer
  rm -rf state/hosts/sd-orderer-1/var/production
  rm -rf state/hosts/wv-orderer-1/var/orderer
  rm -rf state/hosts/wv-orderer-1/var/production

  rm -rf state/hosts/fl-dmv-peer0/hyperledger
  rm -rf state/hosts/sd-dmv-peer0/hyperledger
  rm -rf state/hosts/wv-dmv-peer0/hyperledger
}

function deleteProofOfFinancialResponsibilityNetwork() {
  deleteStateCryptography
  rm -rf ../code/proof_of_financial_responsibility/ProofOfFinancialResponsibility/build
  rm -f state/hosts/al-orderer-1/etc/proof.pb
  rm -f state/hosts/ga-orderer-1/etc/proof.pb
  rm -f state/hosts/nd-orderer-1/etc/proof.pb
  rm -rf state/hosts/al-orderer-1/var/orderer
  rm -rf state/hosts/al-orderer-1/var/production
  rm -rf state/hosts/ga-orderer-1/var/orderer
  rm -rf state/hosts/ga-orderer-1/var/production
  rm -rf state/hosts/nd-orderer-1/var/orderer
  rm -rf state/hosts/nd-orderer-1/var/production

  rm -rf state/hosts/al-dmv-peer0/hyperledger
  rm -rf state/hosts/ga-dmv-peer0/hyperledger
  rm -rf state/hosts/nd-dmv-peer0/hyperledger
}

function deleteRegistrationNetwork() {
  deleteStateCryptography
  rm -fr ../code/registration/Registration/build
  rm -f state/hosts/az-orderer-1/etc/registration.pb
  rm -f state/hosts/pa-orderer-1/etc/registration.pb
  rm -f state/hosts/sd-orderer-1/etc/registration.pb
  rm -rf state/hosts/az-orderer-1/var/orderer
  rm -rf state/hosts/az-orderer-1/var/production
  rm -rf state/hosts/pa-orderer-1/var/orderer
  rm -rf state/hosts/pa-orderer-1/var/production
  rm -rf state/hosts/sd-orderer-1/var/orderer
  rm -rf state/hosts/sd-orderer-1/var/production

  rm -rf state/hosts/az-dmv-peer0/hyperledger
  rm -rf state/hosts/pa-dmv-peer0/hyperledger
  rm -rf state/hosts/sd-dmv-peer0/hyperledger
}

function delete() {
  checkNetwork $network

  case "${network}" in
    federal) deleteFederalNetwork;;
    licensing) deleteLicensingNetwork;;
    pofr) deleteProofOfFinancialResponsibilityNetwork;;
    registration) deleteRegistrationNetwork;;
  esac
}

function quit() {
  checkNetwork $network

  case "${network}" in
    federal)
      docker compose -f docker-compose-federal.yaml down
      ;;
    licensing)
      docker compose -f docker-compose-licensing.yaml down
      ;;
    pofr)
      docker compose -f docker-compose-proof-of-financial-responsibility.yaml down
      ;;
    registration)
      docker compose -f docker-compose-registration.yaml down
      ;;
  esac
}

function generateCryptography() {
  echo "Generating cryptography for $1..."

  $binDirectory/cryptogen generate --config $2 --output=$3/cryptography/certs/

  echo "Generated cryptography for $1."
}

function generateFederalCryptography() {
  generateCryptography "Department of Transportation" federal/cryptography/config/crypto-config-dot-orderer.yaml federal

  generateCryptography NHTSA federal/cryptography/config/crypto-config-nhtsa-peer.yaml federal

  generateCryptography "Purdue Motor Company" federal/cryptography/config/crypto-config-purdue-motor-company-peer.yaml federal

  generateCryptography "Bess's Leasing Company" federal/cryptography/config/crypto-config-bess-leasing-company-peer.yaml federal

  generateCryptography "Purdue Motor Company Assembler" federal/cryptography/config/crypto-config-purdue-motor-company-assembler.yaml federal
  generateCryptography "Purdue Motor Company Technician" federal/cryptography/config/crypto-config-purdue-motor-company-technician.yaml federal
}

function generateLicensingCryptography() {
  generateCryptography "Florida DMV Orderer" state/cryptography/config/crypto-config-florida-orderer.yaml state
  generateCryptography "Florida DMV" state/cryptography/config/crypto-config-florida-peer.yaml state

  generateCryptography "South Dakota DMV Orderer" state/cryptography/config/crypto-config-south-dakota-orderer.yaml state
  generateCryptography "South Dakota DMV" state/cryptography/config/crypto-config-south-dakota-peer.yaml state

  generateCryptography "West Virginia DMV Orderer" state/cryptography/config/crypto-config-west-virginia-orderer.yaml state
  generateCryptography "West Virginia DMV" state/cryptography/config/crypto-config-west-virginia-peer.yaml state
#
  generateCryptography "Insurer Co" state/cryptography/config/crypto-config-insurer-co-peer.yaml state
}

function generateProofOfFinancialResponsibilityCryptography() {
  generateCryptography "Alabama Gov Orderer" state/cryptography/config/crypto-config-alabama-orderer.yaml state
  generateCryptography "Alabama Gov" state/cryptography/config/crypto-config-alabama-peer.yaml state
  generateCryptography "Alabama DMV" state/cryptography/config/crypto-config-alabama-dmv.yaml state

  generateCryptography "Georgia Gov Orderer" state/cryptography/config/crypto-config-georgia-orderer.yaml state
  generateCryptography "Georgia Gov" state/cryptography/config/crypto-config-georgia-peer.yaml state
  generateCryptography "Georgia DMV" state/cryptography/config/crypto-config-georgia-dmv.yaml state

  generateCryptography "North Dakota Gov Orderer" state/cryptography/config/crypto-config-north-dakota-orderer.yaml state
  generateCryptography "North Dakota Gov" state/cryptography/config/crypto-config-north-dakota-peer.yaml state
  generateCryptography "North Dakota DMV" state/cryptography/config/crypto-config-north-dakota-dmv.yaml state

  generateCryptography "Insurer Co" state/cryptography/config/crypto-config-insurer-co-peer.yaml state
}

function generateRegistrationCryptography() {
  generateCryptography "Arizona Gov Orderer" state/cryptography/config/crypto-config-arizona-orderer.yaml state
  generateCryptography "Arizona Gov" state/cryptography/config/crypto-config-arizona-peer.yaml state
  generateCryptography "Arizona DMV" state/cryptography/config/crypto-config-arizona-dmv.yaml state

  generateCryptography "South Dakota Gov Orderer" state/cryptography/config/crypto-config-south-dakota-orderer.yaml state
  generateCryptography "South Dakota Gov" state/cryptography/config/crypto-config-south-dakota-peer.yaml state
  generateCryptography "South Dakota DMV" state/cryptography/config/crypto-config-south-dakota-dmv.yaml state

  generateCryptography "Pennsylvania Gov Orderer" state/cryptography/config/crypto-config-pennsylvania-orderer.yaml state
  generateCryptography "Pennsylvania Gov" state/cryptography/config/crypto-config-pennsylvania-peer.yaml state
  generateCryptography "Pennsylvania DMV" state/cryptography/config/crypto-config-pennsylvania-dmv.yaml state

  generateCryptography "Insurer Co" state/cryptography/config/crypto-config-insurer-co-peer.yaml state
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

function buildLicensingChaincode() {
  buildChaincode "Licensing" ../code/licensing/Licensing/
}

function buildProofOfFinancialResponsibilityChaincode() {
  buildChaincode "Proof of Financial Responsibility" ../code/proof_of_financial_responsibility/ProofOfFinancialResponsibility/
}

function buildRegistrationChaincode() {
  buildChaincode "Registration" ../code/registration/Registration/
}

function generateGenesisBlock() {
  echo "Generating genesis block for $1 channel..."
  $binDirectory/configtxgen -configPath $2 -profile $3 -outputBlock "$4" -channelID $5
  echo "Generated genesis block for $1 channel."
}

function generateFederalGenesisBlocks() {
  generateGenesisBlock firmware federal/config/ firmware federal/hosts/dot-orderer-1/etc/firmware.pb firmware
  generateGenesisBlock fmvss federal/config/ fmvss federal/hosts/dot-orderer-1/etc/fmvss.pb fmvss
  generateGenesisBlock recall federal/config/ recall federal/hosts/dot-orderer-1/etc/recall.pb recall
  generateGenesisBlock vehicle federal/config/ vehicle federal/hosts/dot-orderer-1/etc/vehicle.pb vehicle
}

function generateLicensingGenesisBlocks() {
  generateGenesisBlock licensing state/config/florida/ licensing state/hosts/fl-orderer-1/etc/licensing.pb licensing
  generateGenesisBlock licensing state/config/south-dakota/ licensing state/hosts/sd-orderer-1/etc/licensing.pb licensing
  generateGenesisBlock licensing state/config/west-virginia/ licensing state/hosts/wv-orderer-1/etc/licensing.pb licensing
}

function generateProofOfFinancialResponsibilityGenesisBlocks() {
  generateGenesisBlock proof state/config/alabama/ proof state/hosts/al-orderer-1/etc/proof.pb proof
  generateGenesisBlock proof state/config/georgia/ proof state/hosts/ga-orderer-1/etc/proof.pb proof
  generateGenesisBlock proof state/config/north-dakota/ proof state/hosts/nd-orderer-1/etc/proof.pb proof
}

function generateRegistrationGenesisBlocks() {
  generateGenesisBlock registration state/config/arizona/ registration state/hosts/az-orderer-1/etc/registration.pb registration
  generateGenesisBlock registration state/config/pennsylvania/ registration state/hosts/pa-orderer-1/etc/registration.pb registration
  generateGenesisBlock registration state/config/south-dakota/ registration state/hosts/sd-orderer-1/etc/registration.pb registration
}

function joinOrdererToChannel() {
  echo "Joining ${1} to the ${2} channel..."
  docker exec $1 osnadmin channel join --channelID $2 --config-block /etc/hyperledger/fabric/$2.pb -o localhost:$3 --ca-file /var/hyperledger/orderer/tls/ca.crt --client-cert /var/hyperledger/orderer/admin/client.crt --client-key /var/hyperledger/orderer/admin/client.key
  echo "Joined ${1} to the ${2} channel."
}

function joinFederalOrderersToChannels() {
  joinOrdererToChannel dot-orderer-1 firmware 7053
  joinOrdererToChannel dot-orderer-1 fmvss 7053
  joinOrdererToChannel dot-orderer-1 recall 7053
  joinOrdererToChannel dot-orderer-1 vehicle 7053
}

function joinLicensingOrderersToChannel() {
  joinOrdererToChannel fl-orderer-1 licensing 7353
  joinOrdererToChannel sd-orderer-1 licensing 7453
  joinOrdererToChannel wv-orderer-1 licensing 7553
}

function joinProofOfFinancialResponsibilityOrderersToChannel() {
  joinOrdererToChannel al-orderer-1 proof 7653
  joinOrdererToChannel ga-orderer-1 proof 7753
  joinOrdererToChannel nd-orderer-1 proof 7853
}

function joinRegistrationOrderersToChannel() {
  joinOrdererToChannel az-orderer-1 registration 7953
  joinOrdererToChannel pa-orderer-1 registration 8053
  joinOrdererToChannel sd-orderer-1 registration 7453
}

function joinPeerToChannel() {
  echo "Joining ${1} to the ${2} channel..."

  docker exec $1 peer channel fetch 0 $2.pb -c $2 -o $3 --tls --cafile /etc/hyperledger/fabric/orderer/tls/ca.crt
  docker exec $1 peer channel join -b ./$2.pb

  echo "Joined ${1} to the ${2} channel."
}

function joinFederalPeersToChannels() {
  joinPeerToChannel nhtsa-peer0 firmware dot-orderer-1:7050
  joinPeerToChannel nhtsa-peer0 fmvss dot-orderer-1:7050
  joinPeerToChannel nhtsa-peer0 recall dot-orderer-1:7050
  joinPeerToChannel nhtsa-peer0 vehicle dot-orderer-1:7050

  joinPeerToChannel purdue-motor-company-peer0 firmware dot-orderer-1:7050
  joinPeerToChannel purdue-motor-company-peer0 fmvss dot-orderer-1:7050
  joinPeerToChannel purdue-motor-company-peer0 recall dot-orderer-1:7050
  joinPeerToChannel purdue-motor-company-peer0 vehicle dot-orderer-1:7050

  joinPeerToChannel bess-leasing-company-peer0 recall dot-orderer-1:7050
}

function joinLicensingPeersToChannel() {
  joinPeerToChannel fl-dmv-peer0 licensing fl-orderer-1:7350
  joinPeerToChannel sd-dmv-peer0 licensing sd-orderer-1:7450
  joinPeerToChannel wv-dmv-peer0 licensing wv-orderer-1:7550
}

function updateAnchorPeers() {
  echo "Updating ${1} anchor peers..."
  docker exec $2 ./scripts/updateAnchorPeers.sh $3
  echo "Updated ${1} anchor peers."
}

function updateFederalAnchorPeers() {
  updateAnchorPeers NHTSA nhtsa-peer0 firmware
  updateAnchorPeers NHTSA nhtsa-peer0 fmvss
  updateAnchorPeers NHTSA nhtsa-peer0 recall
  updateAnchorPeers NHTSA nhtsa-peer0 vehicle

  updateAnchorPeers "Purdue Motor Company" purdue-motor-company-peer0 firmware
  updateAnchorPeers "Purdue Motor Company" purdue-motor-company-peer0 fmvss
  updateAnchorPeers "Purdue Motor Company" purdue-motor-company-peer0 recall
  updateAnchorPeers "Purdue Motor Company" purdue-motor-company-peer0 vehicle

  updateAnchorPeers "Bess's Leasing Company" bess-leasing-company-peer0 recall
}

function updateLicensingAnchorPeers() {
  updateAnchorPeers "Florida DMV" fl-dmv-peer0 licensing
  updateAnchorPeers "South Dakota DMV" sd-dmv-peer0 licensing
  updateAnchorPeers "West Virginia DMV" wv-dmv-peer0 licensing
}

function deployChaincode() {
  echo "Deploying ${2} chaincode to ${1}..."
  docker exec $1 peer lifecycle chaincode package $2.tar.gz --path $3 --lang java --label $2
  echo "Deployed ${2} chaincode to ${1}."
}

function installChaincode() {
  echo "Installing ${2} chaincode on ${1}..."
  docker exec $1 peer lifecycle chaincode install $2.tar.gz
  echo "Installed ${2} chaincode on ${1}."
}

function approveChaincode() {
  echo "Approving all chaincode on ${1}..."

  docker exec $1 ./scripts/$2.sh $1

  echo "Approved all chaincode on ${1}."
}

function commitChaincode() {
  echo "Committing chaincode on ${1}..."

  docker exec $1 ./scripts/$2.sh

  echo "Committed chaincode on ${1}."
}

function installFederalChaincode() {
  deployChaincode nhtsa-peer0 "firmware-1.0.0" /chaincode/firmware/FirmwareUpdates
  installChaincode nhtsa-peer0 "firmware-1.0.0"
  deployChaincode nhtsa-peer0 "fmvss-1.0.0" /chaincode/fmvss/FmvssCertification
  installChaincode nhtsa-peer0 "fmvss-1.0.0"
  deployChaincode nhtsa-peer0 "recall-1.0.0" /chaincode/recall/RecallNotifications
  installChaincode nhtsa-peer0 "recall-1.0.0"
  deployChaincode nhtsa-peer0 "vehicle-1.0.0" /chaincode/vehicle/VehicleState
  installChaincode nhtsa-peer0 "vehicle-1.0.0"

  deployChaincode purdue-motor-company-peer0 "firmware-1.0.0" /chaincode/firmware/FirmwareUpdates
  installChaincode purdue-motor-company-peer0 "firmware-1.0.0"
  deployChaincode purdue-motor-company-peer0 "fmvss-1.0.0" /chaincode/fmvss/FmvssCertification
  installChaincode purdue-motor-company-peer0 "fmvss-1.0.0"
  deployChaincode purdue-motor-company-peer0 "recall-1.0.0" /chaincode/recall/RecallNotifications
  installChaincode purdue-motor-company-peer0 "recall-1.0.0"
  deployChaincode purdue-motor-company-peer0 "vehicle-1.0.0" /chaincode/vehicle/VehicleState
  installChaincode purdue-motor-company-peer0 "vehicle-1.0.0"

  deployChaincode bess-leasing-company-peer0 "recall-1.0.0" /chaincode/recall/RecallNotifications
  installChaincode bess-leasing-company-peer0 "recall-1.0.0"

  approveChaincode nhtsa-peer0 approveFederalChaincode
  approveChaincode purdue-motor-company-peer0 approveFederalChaincode
  approveChaincode bess-leasing-company-peer0 approveFederalChaincode

  commitChaincode nhtsa-peer0 commitFederalChaincode
}

function installLicensingChaincode() {
  deployChaincode fl-dmv-peer0 "licensing-1.0.0" /chaincode/licensing/Licensing
  installChaincode fl-dmv-peer0 "licensing-1.0.0"
  approveChaincode fl-dmv-peer0 approveLicensingChaincode

  deployChaincode sd-dmv-peer0 "licensing-1.0.0" /chaincode/licensing/Licensing
  installChaincode sd-dmv-peer0 "licensing-1.0.0"
  approveChaincode sd-dmv-peer0 approveLicensingChaincode

  deployChaincode wv-dmv-peer0 "licensing-1.0.0" /chaincode/licensing/Licensing
  installChaincode wv-dmv-peer0 "licensing-1.0.0"
  approveChaincode wv-dmv-peer0 approveLicensingChaincode
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
    licensing)
      generateLicensingCryptography
      buildLicensingChaincode
      generateLicensingGenesisBlocks
      ;;
    pofr)
      generateProofOfFinancialResponsibilityCryptography
      buildProofOfFinancialResponsibilityChaincode
      generateProofOfFinancialResponsibilityGenesisBlocks
      ;;
    registration)
      generateRegistrationCryptography
      buildRegistrationChaincode
      generateRegistrationGenesisBlocks
      ;;
  esac

  header "FINISHED PREFLIGHT TASKS.\n\n"
  header "STARTING THE NETWORK..."

  case "${network}" in
    federal)
      docker compose -f docker-compose-federal.yaml up --build -d
      ;;
    licensing)
      docker compose -f docker-compose-licensing.yaml up --build -d
      ;;
    pofr)
      docker compose -f docker-compose-proof-of-financial-responsibility.yaml up --build -d
      ;;
    registration)
      docker compose -f docker-compose-registration.yaml up --build -d
      ;;
  esac

  header "STARTED THE NETWORK.\n\n"
  header "STARTING NETWORK SETUP..."

  case "$network" in
    federal)
      joinFederalOrderersToChannels
      joinFederalPeersToChannels
      updateFederalAnchorPeers
      installFederalChaincode
      ;;
    licensing)
      joinLicensingOrderersToChannel
      joinLicensingPeersToChannel
      updateLicensingAnchorPeers
      installLicensingChaincode
      ;;
    pofr)
      joinProofOfFinancialResponsibilityOrderersToChannel
      ;;
    registration)
      joinRegistrationOrderersToChannel
      ;;
  esac

  header "FINISHED NETWORK SETUP.\n\n"
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
