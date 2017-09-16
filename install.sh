#!/usr/bin/env bash
echo "This script will install beaconsniffer software stack to fresh raspberry pi zero"
read -p "Press enter to continue"
echo "Updating Raspberry Pi"
sudo apt-get update
sudo apt-get upgrade -y
sudo apt-get dist-upgrade -y
echo "Update done"
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
sudo apt-get install
java -jar /home/pi/beaconhue.jar
