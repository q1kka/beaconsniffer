# MQTT-client
This node.js code listens for redis publishes from presenceparser. When message is received
nicely formatted JSON is parsed and sent to MQTT broker on the gateway device.

## Dependecies
Following dependencies must be installed before deployment:
```
sudo apt-get install nodejs
sudo apt-get install npm
sudo npm install forever
sudo npm install mqtt --save
sudo npm install redis
```

## Deployment
**Before deployment define MQTT broker address on the script file:** 
```javascript
var mqttclient  = mqtt.connect('mqtt://localhost');
var mqtttopic = "resourcedata";
```
Execute following command on the script directory to start:
```
forever start mqtt.js
```

Forever makes sure that script is relaunched in event of crash. To view running
forever processes type `forever list`. To kill process use `forever stop <PID>`