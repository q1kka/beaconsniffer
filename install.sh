#!/usr/bin/env bash
echo "This script will install beaconsniffer software stack to fresh raspberry pi zero"
echo "ATTENTION: external configuration file must be double checked"
read -p "Press enter to continue"
echo "Updating Raspberry Pi"
sudo apt-get update
sudo apt-get upgrade -y
sudo apt-get dist-upgrade -y
echo "Update done"
echo "Downloading and installing screen"
sudo apt-get install screen
echo "Downloading and installing python & pip"
sudo apt-get install python python-pip -y
echo "Installing dependencies for python"
sudo pip install redis
echo "Downloading and installing Bluetooth stack"
sleep 2
sudo apt-get install bluez bluez-tools bluez-hcidump -y
echo "Downloading and installing Java"
sleep 2
sudo apt-get install oracle-java8-jdk -y
echo "Downloading and installing nodejs & npm"
sudo apt-get install nodejs npm -y
echo "Downloading and installing node dependencies"
sleep 2
sudo npm install mqtt --save
sudo npm install redis
sudo npm instal forever -G
echo "Dependencies satisfied, proceed to configuration"
sleep 3
clear
read -p "Automatically start scripts on boot? (y/n)?" choice
case "$choice" in
  y|Y ) ./startonboot.sh;;
  n|N ) echo "skipped crontab editing";;
  * ) echo "invalid";;
esac

java -jar /home/pi/beaconhue.jar
