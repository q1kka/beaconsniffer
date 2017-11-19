var mqtt = require('mqtt');
var mqttclient  = mqtt.connect("mqtt://localhost");

var redis = require("redis");
var redissub = redis.createClient();

var fs = require('fs');
var path = require("path");
var config = JSON.parse(fs.readFileSync(path.join(__dirname, '..', 'configurations.conf'), 'utf8'));

var homegroups = followHomegroups();

function followHomegroups() {
    var arr = [];
    var beacons = config.beacons;
    for(var x in beacons){
        var homegroup = beacons[x].id2;
        arr.push(homegroup);
    }
    var unique = arr.filter(function(elem, index, self) {
        return index == self.indexOf(elem);
    });

    for(i = 0; i < unique.length; i++){
        redissub.subscribe(unique[i] + "-parsed");
    }
    return unique;
}

redissub.on("message", function (channel, message) {
    var mqtttopic = config.mqtttopic;
    var homeGroup = channel.replace("-parsed", "");
    var minorid = message.toString().substring(0,1);
    var identifier = homeGroup + "-" + minorid;
    var activeString = message.toString().substring(2);
    var value = true;
    if(activeString == "inactive") { value = false; }
    var today = new Date();
    console.info(today.toISOString() + " - " + identifier + " " + activeString + " payload sent");
    var payloadString =
        '{ "payload": { "payload": "' + value + '", "type": "presence" }, ' +
        '"connection": { "devaddr": "' + identifier + '", "lasthop": "BLE", "relay": "' + config.relay + '" }, "timestamp": "' + today.toISOString() +'" }';
    mqttclient.publish(mqtttopic, payloadString);
});

redissub.on("subscribe", function (channel, count) {
    console.info("Subscribed to homegroup: " + channel)
});
