# BLEsniffer-python
This script listens for Bluetooth Low Energy beacons received from
RadBeacon. These messages are published to Redis with epoc timestamp.

Altbeacon message schema must be used with this script.

## Deployment
Execute command in the working directory
```bash
sudo python beaconsniffer.py
```

Make sure that configurations can be found in the parent path.

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
