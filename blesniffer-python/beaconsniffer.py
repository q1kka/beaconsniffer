#!/usr/bin/env python
# -*- coding: utf-8 -*-

import commands
import json
import os
import time
from subprocess import Popen, PIPE

import redis

# network stack settings for ble interface
hci = "hci0"
hcidump = ['sudo', 'hcidump', '-i', hci, '--raw']
hcitool = "sudo hcitool -i " + hci + " lescan --whitelist --duplicates > /dev/null &"

# local redis database connection
red = redis.StrictRedis(host='localhost', port=6379, db=0)
# minimun interval between consecutive redis set
beaconInterval = 5000L

# load configurations from parent directory
fileDir = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
filename = os.path.join(fileDir, 'beacons.conf')
config = [filename]

# identifiers is "ID2-ID3"
macs, reversemacs, identifiers = [[], [], []]
lastseentime = []


def main():
    try:
        if hcistart() != 0:
            configjson = readjson(config[0])
            if configjson != 0:
                setconfig(configjson["beacons"])
                # Format last seen arrays for every followed beacon
                for i in range(len(macs)):
                    lastseentime.append(0L)
                # Configure hci stack
                configurehci()
                # Start ble scan & dump
                os.system(hcitool)
                print timeprint() + " " + "lescan process started."
                rawdump = start(hcidump)
                if rawdump != 0:
                    print timeprint() + " " + "rawdump started."
                sniffer(rawdump)
    except Exception, e:
        print timeprint() + " " + "Error in main(): " + str(e)
    finally:
        os.system("sudo pkill --signal SIGINT hcitool")


# Parse beacon configurations from external config file.
def setconfig(configjson):
    keys = configjson.keys()
    keys.sort()
    for i in range(len(keys)):
        mac = configjson[keys[i]]["mac"]
        macbytes = mac.split(":")
        id2 = configjson[keys[i]]["id2"]
        id3 = configjson[keys[i]]["id3"]
        # Check MAC address syntax
        if len(mac) == 17 and len(macbytes) == 6:
            macs.append(mac)
            reversemacs.append(" ".join(list(reversed(macbytes))))
            identifiers.append(id2 + "-" + id3)
        else:
            print timeprint() + " " + mac + " wrong mac syntax."
    return 1


# Add followed MAC addresses to hcitool whitelist
def configurehci():
    try:
        os.system("sudo hcitool lewlclr")
        print timeprint() + " " + "whitelist cleared."
        for mac in macs:
            if len(mac) == 17:
                os.system("sudo hcitool lewladd " + mac)
                print timeprint() + " " + mac + " added to whitelist."
            else:
                print timeprint() + " " + mac + " false mac."
        print timeprint() + " " + "whitelist ok."
    except Exception, e:
        print timeprint() + " " + "error configuring whitelist: " + str(e)
        return 0


def sniffer(rawdump):
    global beaconInterval, lastseentime
    print "sniffer started"
    while True:
        try:
            line = unicode(rawdump.stdout.readline(), "utf-8")
            if line.startswith(">"):
                while True:
                    tnow = timenow()
                    for i in range(len(macs)):
                        if reversemacs[i] in line:
                            if lastseentime[i] + beaconInterval < tnow:
                                lastreceived = (tnow - lastseentime[i]) / 1000
                                print timeprint() + "- New advertisement beacon from: " + macs[
                                    i] + " - seconds from last message: " + str(lastreceived)
                                ids = identifiers[i].split("-")
                                red.publish(ids[0], ids[1] + "-" + str((int(time.time()))))
                                lastseentime[i] = tnow
                                break
                    line2 = unicode(rawdump.stdout.readline(), "utf-8")
                    if line2.startswith(">"):
                        line = line2
                    else:
                        line = line + line2
                        break
        except Exception, e:
            print timeprint() + " " + "Error in sniffer-loop: " + str(e)
            line = ""
            time.sleep(0.1)


def hcistart():
    try:
        if "UP RUNNING" not in commands.getstatusoutput('hciconfig ' + hci)[1]:
            i = 0
            while "DOWN" not in commands.getstatusoutput('hciconfig ' + hci)[1]:
                print hci + " RESTARTING"
                i += 1
                if i == 30:
                    os.system("sudo /etc/init.d/bluetooth restart && sudo hciconfig " + hci + " up")
                    i = 0
                time.sleep(10)
            if "UP RUNNING" not in commands.getstatusoutput('hciconfig ' + hci)[1]:
                print hci + " UP RUNNING"
        return 1
    except Exception, e:
        print u"Error in hcistart(): " + str(e)
        return 0


# Read JSON from file
def readjson(configfile):
    try:
        with open(configfile) as json_data:
            ret = json.load(json_data)
    except Exception, e:
        print "Error in readjson(): " + str(e)
        return 0
    return ret


# Starts given command on host OS
def start(cmd):
    try:
        scan = Popen(cmd, stdout=PIPE, bufsize=1)
        return scan
    except Exception, e:
        print u"Error in startScan(): " + str(e)
    return 0


def timenow():  # in milliseconds
    # noinspection PyArgumentList
    return long(time.time() * 1000)


def timeprint():
    return time.strftime('%Y-%m-%d_%H:%M:%S')


if __name__ == "__main__":
    main()
