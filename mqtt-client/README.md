# MQTT-client
This module listens for beacon state change redis publishes. When state change message is received,
nicely formatted JSON is parsed and sent to MQTT broker defined in the configurations.

## Output JSON format
```
{
    "payload": {
        "payload": "boolean",
        "type": "string"
    },
    "connection": {
        "devaddr": "string",
        "lasthop": "string",
        "relay": "string",
        "timestamp": "string"
    }
}
```

## Dependecies
Following dependencies must be satisfied before deployment:
```
sudo apt-get install nodejs
sudo apt-get install npm
sudo npm install forever
sudo npm install mqtt --save
sudo npm install redis
```

## Standalone deployment
Execute following command on the script directory to start:
```
forever start mqtt.js
```

Forever makes sure that script is relaunched in event of crash. To view running
forever processes type `forever list`. To kill process use `forever stop <PID>`