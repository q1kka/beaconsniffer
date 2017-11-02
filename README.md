# Beaconsniffer v2

Beaconsniffer is a device prototype that listens for Bluetooth Low Energy advertisement beacons sent by
keyfob-beacon and determines presence from raw data. Presence information can be published to MQTT topic
in JSON format for further usage and integrations. Beaconsniffer is built on Raspberry Pi Zero.

![Beaconsniffer](/doc/bsniffer.png)

## Beacons
This prototype is currently compatible with keyfob-beacons that can be configured to send advertisement
packages in AltBeacon format. Suggested beacon is [RadBeacon Dot](/). Cheaper unbranded beacons can be
unreliable and have poor battery life.

Beacon is attached to keychain of monitored person. In ideal use case the keychain always leaves the
apartment with the resident and it can be used to efficiently determine presence status with minimal latencies.
Quality keyfob sensor has battery life of years.
 
## Hardware & Casing
3D models for the casing can be found in this repository. Models are given in .stl format for
easy slicing and printing.

The casing is designed to fit Raspberry Pi Zero and basic EU power adaptor.
Power adaptor used in the prototype is "LA-520W" model from PiHut. To fit everything in the
case nice and smooth, cord of the adaptor needs to be cut and power given to Raspberry Pi via GPIO.

![Hardware in the case](/doc/hardware1.png)

## Configurations
### Beacon configurations
RadBeacons can be configured with mobile app available on Android from Google Play. Following configurations
are for reference:

```
iBeacon: OFF
AltBeacon: ON
Eddystone UID: OFF
Eddystone URL: OFF
MAJOR ID: 32001 (see "beacon provision" below)
MINOR ID: 1
ADVERTISING RATE: 1 (latency vs battery life)
TRANSMIT POWER: -10 (coverage vs battery life)
```

When determining optimal transmit power and advertising rate, couple things should be taken in to 
consideration. These things include physical location of the prototype and typical location of keychains
when the resident is home. Best results are achieved when doing throughout testing and configurations at 
the same time.

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
5.  git clone https://github.com/q1kka/beaconsniffer-v2.
6.  cd beaconsniffer-v2/
7.  cp template.conf configurations.conf
8.  Modify configurations in configurations.conf
9.  ./install.sh

```
Installation script takes care of dependencies and crontab automatically. When installation
is done, device must be rebooted.

## Homegroup & Minor ID
Homegroup (ID2) provisions beacons under identifier specified in beacons firmware.
Minor ID (ID3) identifies unique beacon.

For example: If one wants to track two beacons belonging to one project,
firmware configurations on the beacon should be as following:

Beacon 1:  ID2 - 32201, ID3 - 1
Beacon 2: ID2 - 32201, ID3 - 2

If further provision is needed, one can add more beacons and use different homegroup.
These configurations must be correct and double checked to prevent any problems on compatibility between software.
All of the software components must support multiple homegroups at the same time.