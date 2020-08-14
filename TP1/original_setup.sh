#!/usr/bin/env bash

javac *.java

sudo iptables -A INPUT   -m pkttype --pkt-type multicast -j ACCEPT
sudo iptables -A FORWARD -m pkttype --pkt-type multicast -j ACCEPT
sudo iptables -A OUTPUT  -m pkttype --pkt-type multicast -j ACCEPT

rmiregistry

sleep .5

gnome-terminal -- java Peer 1.0 1 Peer1 224.0.0.15 8001 224.0.0.16 8002 224.0.0.17 8003

sleep .5

gnome-terminal -- java Peer 1.0 2 Peer2 224.0.0.15 8001 224.0.0.16 8002 224.0.0.17 8003

sleep .5

gnome-terminal -- java Peer 1.0 3 Peer3 224.0.0.15 8001 224.0.0.16 8002 224.0.0.17 8003

sleep .5

gnome-terminal -- java Peer 1.0 4 Peer4 224.0.0.15 8001 224.0.0.16 8002 224.0.0.17 8003

sleep 1

#gnome-terminal -- /bin/sh -c 'java App Peer1 BACKUP ./sec.pdf 1; echo "java App Peer1 BACKUP ./sec.pdf 1"; exec bash'
#gnome-terminal -- /bin/sh -c 'java App Peer1 RESTORE ./sec.pdf; echo "java App Peer1 RESTORE ./sec.pdf"; exec bash'
#gnome-terminal -- /bin/sh -c 'java App Peer1 DELETE ./sec.pdf; echo "java App Peer1 DELETE ./sec.pdf"; exec bash'

exit 0