# Presenceparser
This program subscribes to Redis topic in which blesniffer-python script publishes data to.
Logic is used to determine status of presence and publish presence messages back to redis
in the different topic.

## Deployment
Precompiled jar can be found in this repository. Run it by executing

`java -jar presenceparser-0.5-beta.jar`

## Output
Software outputs presence data to Redis topic. Topic name is based on input topic
in which data is received in. Format of outputted data is following:

```
[0 127.0.0.1:45126] "PUBLISH" "32201-parsed" "1-inactive"
```

Presence data is published to channel which name is the same as the input channel with added "-parsed"
Message payload is minorid-inactive/active.