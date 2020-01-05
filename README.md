<img src="doc/banner.png"></img>

Beaconsniffer is a presence detector prototype built on Raspberry Pi Zero. This prototype tracks signals broadcasted by simple generic BLE sensors. These sensors can be used to track many things, such as presence, temperature and humidity.

As a proof of concept, prototypes and beacons were used to determine presence of residents living inside smart home environment. This prototype was used to track and publish events when one or more resident leaves the apartment. 

Sensors were attached to the keychains of residents. In ideal use case the keychain always leaves the apartment with the resident and it can be used to efficiently determine presence status of resident with minimal latencies.

Some parts of this prototype were implemented using funding provided by Tekes (Business Finland. Beaconsniffer prototypes were used as a part of 5K smart city research by University of Jyväskylä.

This repository contains all software and hardware related to the prototype.

## Architecture & Modules

This prototype uses modular microservice architecture to achieve compatibility between different software components written in the language of chosing (that can be ran in Raspberry).

This software uses [Redis](https://github.com/antirez/redis) as the way of communication between microservice modules. The only required module is ``blesniffer``, which listens to raw beacon data and publishes it to redis topic.

Other modules can be implemented and used to transform or use the data provided by sniffer.

Currently implemented modules:

| Module            | Functionality                             |
|:------------------|:------------------------------------------|
| blesniffer        | listens and publishes raw advertisement packages        |
| presenceparser    | parses presence events from raw sniffer data         |
| mqtt-client       | publish presence events to MQTT broker   |

Monitoring modules:

```
// List running modules
screen -r

// To see module output 
screen -r <ID>

// Monitor redis database connecting modules
redis-cli monitor
```

## Installation and deployment
To easily install and deploy this prototype proceed as following:
```
1.  Install fresh raspbian distro
2.  Change default passwd
3.  Configure networking
4.  Install git
5.  git clone https://github.com/q1kka/beaconsniffer-ble
6.  cd beaconsniffer-ble/
7.  cp template.conf sniffer.conf
8.  Modify configurations in configurations.conf (see next section)
9.  ./install.sh
```
Installation scripts takes care of dependencies and auto-starting of enabled modules on boot. When installation is done, device must be rebooted.

### Configurations
All of the microservice modules share the same beacon configurations. These configurations are stored on the external JSON file in project root. For module specific configurations, see module documentation.

Template for basic configurations is provided in this repository. ID2 and ID3 should be specified according to beacon firmware configuration.

### Template
During the installation, template is copied from ```template.conf``` to ```sniffer.conf```. Only beacon configurations and name are required if no additional modules are being used.
``` 
{   
    "name": "beaconsniffer-x", 
    "beacons": {
        "user1": {
            "id2": "32201",  // ID2 is "homegroup"
            "id3": "1",  // ID3 is "minor id"
            "mac": "0C:F3:EE:0D:82:F5" // MAC of the device
        },
    }
}
```

## Beacons
This prototype is currently compatible with sensors that can be configured to send BLE advertisement packages in AltBeacon format. Suggested beacon is [RadBeacon Dot](/). Cheaper unbranded beacons can be unreliable and have poor battery life.

### Beacon configurations
RadBeacons can be configured with mobile app available on Android from Google Play. Following configurations can be used for reference:

```
iBeacon: OFF
AltBeacon: ON
Eddystone UID: OFF
Eddystone URL: OFF
ADVERTISING RATE: 1 (latency vs battery life)
TRANSMIT POWER: -10 (coverage vs battery life)
```

When determining optimal transmit power and advertising rate, couple things should be taken in to consideration. These things include physical location of the prototype and typical location of keychains when the resident is home. Best results are achieved when doing throughout testing and configurations at the same time during prototype deployment.

AltBeacon major and minor identifiers are used as project namespaces. Major ID (ID2) identifies group in which sensor belongs to. Minor ID (ID3) identifies single unique sensor.

For example: If one wants to track two beacons belonging to one project, firmware configurations on the beacon should be as following:
```
Beacon 1: ID2 - 32201, ID3 - 1
Beacon 2: ID2 - 32201, ID3 - 2
```

For further provisioning, one can add more beacons and use different namespaces to separate sensors from different deployments using same sensors.
 
## Sniffer hardware & casing 
3D models for the casing can be found in this repository. Models are given in .stl format for easy slicing and printing.

The casing is designed to fit Raspberry Pi Zero and basic EU power adaptor. Power adaptor used in the prototype is "LA-520W" model from PiHut. To fit everything in the case nice and smooth, cord of the adaptor needs to be cut and power given to Raspberry Pi via GPIO.

![Hardware in the case](/doc/bsniffer.png)
