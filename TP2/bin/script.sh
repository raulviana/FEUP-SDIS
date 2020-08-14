
#!/bin/bash


javac *.java

gnome-terminal -- /bin/sh -c 'java Server 8080; echo "java Server 8080"; exec bash'

sleep 0.8

gnome-terminal -- /bin/sh -c 'java Peer localhost 8080 8081; echo "java Peer localhost 8080 8081"; exec bash'

sleep 0.8

gnome-terminal -- /bin/sh -c 'java Peer localhost 8080 8082; echo "java Peer localhost 8080 8082"; exec bash'

#sleep 0.8

#gnome-terminal -- /bin/sh -c 'java Peer localhost 8080 8083; echo "java Peer localhost 8080 8083"; exec bash'

#sleep 0.8

#gnome-terminal -- /bin/sh -c 'java Peer localhost 8080 8084; echo "java Peer localhost 8080 8084"; exec bash'
