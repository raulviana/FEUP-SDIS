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
| Back-Up  | java App <peer_ap> BACKUP <file name> |
|          | java App Peer1 BACKUP sec.pdf 1 |
| Restore  | java App <peer_ap> RESTORE <file name> |
|          | java App Peer1 BACKUP sec.pdf 1 |
| Delete   | java App <peer_ap> DELETE <file name> |
|          | java App Peer1 BACKUP sec.pdf 1 |
| Manage Storage | java App <peer_ap> RECLAIM <space> |
|          | java App Peer1 RECLAIM 0 |
| Service Information | java App <peer_ap> STATE |
|          | java App <peer_ap> STATE |