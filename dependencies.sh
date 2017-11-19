#!/usr/bin/env bash
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
sudo ln -fs /usr/local/bin/node
sudo rm node_latest-armhf.deb
echo "Downloading and installing node dependencies"
sleep 2
sudo npm install mqtt --save
sudo npm install redis
sudo npm install forever -g