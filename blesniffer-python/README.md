# BLEsniffer-python
This python script listens for Bluetooth Low Energy beacons received from
RadBeacon. These messages are published to Redis in the following format:

```
Redis publish format is following:
[0 127.0.0.1:42634] "PUBLISH" "32204" "3-1505507025"
```

Every time advertisement packange is received, message is published to redis topic.
Redis topic name is based on defined group id (major id). Published message contains
minor id and epoc time separated with strike. All further software components should be designed to work
with this standard to achieve full modularity.

## Standalone deployment
Execute command in the working directory
```bash
sudo python beaconsniffer.py
```

Make sure that configurations can be found in the parent path.
