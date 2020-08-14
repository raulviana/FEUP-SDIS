# FEUP-SDIS
Repository for the **Distributed Systems** course

## TP1

### Authors

* Raul Viana - up201208089@fe.up.pt
* João Lemos - ee10201@fe.up.pt

### Objective

The first assignment objective was to develop a serverless back-up protocol for a local area network. 
Each client can perform the following actions:
backup a file
restore a file 
Delete a file
Manage local service storage
Retrieve local service state information

### Instructions

For testing, just run *original_setup.sh*. It will start the *rmi* server and four peers. 

### Commands

| Function | Command/Example |
| :------- | :-----: |
| Back-Up  | java App <peer_ap> BACKUP <file_name> |
|          | java App Peer1 BACKUP sec.pdf 1 |
| Restore  | java App <peer_ap> RESTORE <file_name> |
|          | java App Peer1 BACKUP sec.pdf 1 |
| Delete   | java App <peer_ap> DELETE <file_name> |
|          | java App Peer1 BACKUP sec.pdf 1 |
| Manage Storage | java App <peer_ap> RECLAIM <space> |
|          | java App Peer1 RECLAIM 0 |
| Service Information | java App <peer_ap> STATE |
|          | java App <peer_ap> STATE |


## TP2

### Authors

* [Luis Couto](https://github.com/limonete)
* [Amr Abdalrahman](https://github.com/Amr311)
* Raul Viana


### Objective

The second assignment objective was to develop a peer-to-peer distributed backup service for the Internet. We choose a centralized server approach without the use of the *chord* protocol. 


### Instructions

To test it you may run the **script.cmd** if you're on Windows or the **script.sh** if you're on Linux. These scripts will start the Server and three other Peers.


### Commands

The commands are the same as they were in the first assignment except for the manage storage and system information that doesn't exist in this second assignment. 

| Function | Command/Example |
| :------- | :-----: |
| Back-Up  | java App <peer_ap> BACKUP <file_name> |
|          | java App Peer1 BACKUP sec.pdf 1 |
| Restore  | java App <peer_ap> RESTORE <file_name> |
|          | java App Peer1 BACKUP sec.pdf 1 |
| Delete   | java App <peer_ap> DELETE <file_name> |
|          | java App Peer1 BACKUP sec.pdf 1 |
