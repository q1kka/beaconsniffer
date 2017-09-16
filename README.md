# Beaconsniffer v2
This repository contains software for Beaconsniffer v2. It consists of three modular software components
which uses Redis as an interface for data pipelining.

## Beacon configurations
All of the software components use the same beacon configurations. These configurations are stored
on the external JSON file. Configurations must be saved on the root directory of the codes.

Template for beacon configurations is provided in this repository.
ID2 and ID3 should be specified according to beacon firmware configuration.

```
{
    "beacons": {
        "beacon1": {
            "id2": "32201",  // ID2 is "homegroup"
            "id3": "1",  // ID3 is "minor id"
            "mac": "0C:F3:EE:0D:82:F5" // MAC of the device
        },
        ...
    },
    "relay": "beaconsniffer-x", // Define relay ID for platform provisioning
    "mqttbroker": "192.168.33.1", // Define mqtt broker if client is used
    "mqtttopic": "resourcedata"// MQTT topic in which data will be published to
}
```
## Installation and deployment
To easily install this software to raspberry pi zero proceed as following:
```
1.  Install fresh raspbian distro
2.  Change default passwd
3.  Configure networking
4.  Install git
5.  git clone https://github.com/q1kka/beaconsniffer-v2.git
6.  sudo mv template.conf configurations.conf
7.  Modify configurations in configurations.conf
6.  cd beaconsniffer-v2/
7.  ./install.sh

```
Installation script takes care of dependencies and crontab automatically. When installation
is done, device must be rebooted.

## Homegroup & Minor ID
Homegroup (ID2) provisions beacons under identifier specified in beacons firmware.
Minor ID (ID3) identifies unique beacon.

For example: If one wants to track two beacons belonging to one project,
firmware configurations on the beacon should be as following:

```
Beacon 1:
ID2: 32201
ID3: 1

Beacon 2:
ID2: 32201
ID3: 2
```

If further provision is needed, one can add more beacons and use different homegroup:

```
Beacon 3:
ID2: 32202
ID3:1

Beacon 4:
ID2: 32202
ID3:1
```
These configurations must be correct and double checked to prevent any problems on compatibility between software.
All of the software components support multiple homegroups at the same time.