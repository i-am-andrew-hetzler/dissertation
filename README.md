# Building a Compliant and Interoperable Blockchain Architecture for U.S. Connected and Automated Vehicles


## Repository Layout

### Architecture folder
The architecture folder contains four child folders: diagrams, code, poc, and schemas. The diagrams folder contains all the UML use 
case, C4 container, and C4 component diagrams. The code folder contains all the chaincode, gateway code, and performance 
test code for all the use cases. The poc folder contains the docker compose files, scripts, and other artifacts to run
the poc. The schemas folder contains all the formal JSON definitions for each use
case's schema.

### Compliance folder
The compliance folder contains the statutes, compliance analysis, and compliance requirements for each use case.
The compliance folder has two child folders: federal and state. Inside each child folder are folders for each use case
that level of government regulates. The use case folders contain the statutes (in PDF format under the legislation folder).

There are two files for the compliance analysis: _Federal Compliance Analysis_ and _State Compliance Analysis_. The federal analysis
document contains the analysis for FMVSS certification and recalls. The state analysis contains analysis for licensing, 
insurance, and registration. Note you will need Microsoft Excel to open these files.

There is a file named _Comprehensive Requirements_ that contains the technical and compliance requirements for each use case.
Note you will need Microsoft Word to open this file.

#### Federal Compliance
The eCFR is an authoritative but unofficial statute. The researcher reviewed the eCFRs but downloaded the official
sources from the Government Publishing Office for analysis (per the recommendation of eCFR).

#### State
The researcher collected and analyzed official sources for the state statutes. The sources varied between states.

### Root Level
Receipts were written for each phase of the study. There are three receipts: (a) architecture analysis, (b) legislation analysis,
and (c) legislation collection. Each receipt contains the start and end dates for that activity. In the case of the _legislation analysis_, 
notes were kept for each expert exchange.

## Running the PoC
**NOTE** Mac users must run the _cryptogen_ and _configtxgen_ binary first before running poc.sh. You will need to allow the binary to run.
You can do that by double-clicking the binary, open _System Preferences > Privacy & Security_ and scroll down to where
the binary is blocked. Click allow, run the binary again, click allow and enter your password. You should no longer get
prompted to allow the _cryptogen_ and _configtxgen_ binary.

## Running the Performance Tests
1. Make sure the instance(s) are running for the test.
2. Update the connection.yaml file for the test you want to run so that the pem certificate matches the user you are masquerading as. Note, it will be the /tls/ca.crt of the user.
3. Invoke the Docker Compose up command for the test.
### Firmware
```shell
npx caliper launch manager \
    --caliper-bind-sut fabric:fabric-gateway \
    --caliper-workspace . \
    --caliper-benchconfig federal/hosts/caliper/firmware/benchmarks/config.yaml \
    --caliper-networkconfig federal/hosts/caliper/firmware/networks/network.yaml
```
### FMVSS
```shell
npx caliper launch manager \
    --caliper-bind-sut fabric:fabric-gateway \
    --caliper-workspace . \
    --caliper-benchconfig federal/hosts/caliper/fmvss/benchmarks/config.yaml \
    --caliper-networkconfig federal/hosts/caliper/fmvss/networks/network.yaml
```

### Recall Notifications
```shell
npx caliper launch manager \
    --caliper-bind-sut fabric:fabric-gateway \
    --caliper-workspace . \
    --caliper-benchconfig federal/hosts/caliper/recall/public/benchmarks/config.yaml \
    --caliper-networkconfig federal/hosts/caliper/recall/public/networks/network.yaml
    
npx caliper launch manager \
    --caliper-bind-sut fabric:fabric-gateway \
    --caliper-workspace . \
    --caliper-benchconfig federal/hosts/caliper/recall/impactedOwner/benchmarks/config.yaml \
    --caliper-networkconfig federal/hosts/caliper/recall/impactedOwner/networks/network.yaml
    
npx caliper launch manager \
    --caliper-bind-sut fabric:fabric-gateway \
    --caliper-workspace . \
    --caliper-benchconfig federal/hosts/caliper/recall/lessorList/benchmarks/config.yaml \
    --caliper-networkconfig federal/hosts/caliper/recall/lessorList/networks/network.yaml
```

### Vehicle State
```shell
npx caliper launch manager \
    --caliper-bind-sut fabric:fabric-gateway \
    --caliper-workspace . \
    --caliper-benchconfig federal/hosts/caliper/vehicle/benchmarks/config.yaml \
    --caliper-networkconfig federal/hosts/caliper/vehicle/networks/network.yaml
```

### Proof of Financial Responsibility
```shell
npx caliper launch manager \
    --caliper-bind-sut fabric:fabric-gateway \
    --caliper-workspace . \
    --caliper-benchconfig state/hosts/caliper/proof/state/benchmarks/config.yaml \
    --caliper-networkconfig state/hosts/caliper/proof/state/networks/network.yaml
```

### Licensing
```shell
npx caliper launch manager \
    --caliper-bind-sut fabric:fabric-gateway \
    --caliper-workspace . \
    --caliper-benchconfig state/hosts/caliper/licensing/state/benchmarks/config.yaml \
    --caliper-networkconfig state/hosts/caliper/licensing/state/networks/network.yaml
```

### Registration
```shell
npx caliper launch manager \
    --caliper-bind-sut fabric:fabric-gateway \
    --caliper-workspace . \
    --caliper-benchconfig state/hosts/caliper/registration/state/benchmarks/config.yaml \
    --caliper-networkconfig state/hosts/caliper/registration/state/networks/network.yaml
```