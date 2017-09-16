#!/usr/bin/env bash
echo "This script will install beaconsniffer software stack to fresh raspberry pi zero"
echo "ATTENTION: external configuration file must be double checked"
read -p "Press enter to continue"
clear
sudo apt-get update
clear
read -p "Update Raspberry Pi? (y/n)" update
case "$update" in
  y|Y ) sudo apt-get upgrade && sudo apt-get dist-upgrade;;
  n|N ) echo "skipped update";;
  *  ) echo "invalid";;
esac
echo "Downloading and installing screen"
sudo apt-get install screen -y
echo "Downloading and installing redis"
sudo apt-get install redis-server -y
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
wget http://node-arm.herokuapp.com/node_latest_armhf.deb
sudo dpkg -i node_latest_armhf.deb
sudo ln -fs /usr/local/bin/node /usr/bin/
echo "Downloading and installing node dependencies"
sleep 2
sudo npm install mqtt --save
sudo npm install redis
sudo npm install forever -g
echo "Dependencies satisfied, proceed to configuration"
sleep 3
clear
read -p "Automatically start scripts on boot? (y/n)?" choice
case "$choice" in
  y|Y ) crontab.sh;;
  n|N ) echo "skipped crontab editing";;
  * ) echo "invalid";;
esac
clear
read -p "Installation done, press enter to reboot"
sudo reboot -h now
