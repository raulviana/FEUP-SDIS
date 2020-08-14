# sdis1920-t1g12

## Distributed Systems Project 1 2019/2020

- java version used: "11.0.2" 2019-01-15 LTS


### Compiling and running the four pre-configured Peers

$ sudo bash setup.sh


### Backup Service:

App <peer_access_point> <BACKUP> <file> <replication degree>

Example: java App Peer1 BACKUP ./feup.jpg 1


### Restore Service:

App <peer_access_point> <RESTORE> <file>

Example: java App Peer1 RESTORE ./feup.jpg

### Delete Service:

App <peer_access_point> <DELETE> <file>

Example: java App Peer1 DELETE ./feup.jpg

### Reclaim Service


### State Servive:

App <peer_access_point> <SATE>

Example: java App Peer1 STATE

