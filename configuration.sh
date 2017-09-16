#!/usr/bin/env bash
read -p "Start blesniffer on startup (y/n)?" choice

function blesniffercrontab {
    crontab -l > cron
    #echo new cron into cron file
    echo "@reboot screen -S blesniffer -dm sh -c 'sleep 15; python /home/pi/beaconsniffer-v2/blesniffer-python/beaconsniffer.py; exec bash'" >> cron
    #install new cron file
    crontab cron
    rm mycron
}

case "$choice" in
  y|Y ) blesniffercrontab;;
  n|N ) echo "skipped crontab editing";;
  * ) echo "invalid";;
esac