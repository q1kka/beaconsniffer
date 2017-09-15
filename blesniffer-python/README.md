# BLEsniffer-python
This script listens for Bluetooth Low Energy beacons received from
RadBeacon. These messages are published to Redis with epoc timestamp.

Altbeacon message schema must be used with this script.

## Deployment
Execute command in the working directory
```bash
sudo python beaconsniffer.py
```

Make sure that configurations can be found in the same path.

## Beacon configurations
This script configurations are stored on the external JSON file. Configurations must be saved on the
same directory where the script is deployed.

Template for beacon configurations is provided in this repository.
ID2 and ID3 should be specified according to beacon firmware configuration.

```JSON
{
    "beacons": {
        "beacon1": {
            "id2": "32201",  // ID2 is "homegroup"
            "id3": "1",  // ID3 is "minor id"
            "mac": "0C:F3:EE:0D:82:F5" // MAC of the device
        }
        ...
    }
}
```

### Homegroup & Minor ID
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

These configurations must be correct and double checked to prevent any problems on other software components.

## Redis
This code uses Redis publish / subscribe as an interface between other software components
using collected data. When message is received information is also printed on the console.

*To monitor redis traffic use: `redis-cli monitor`*

```
Redis publish format is following:

[0 127.0.0.1:42634] "PUBLISH" "32204" "3-1505507025"
```

Message is published to channel which name is based on homegroup ID. Published message contains
minor id and epoc time separated with strike. All further software components should be designed to work
with this standard to achieve modularity.
