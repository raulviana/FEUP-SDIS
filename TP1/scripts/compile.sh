#! /usr/bin/bash

# Basic compilation script
# To be executed in the root of the package (source code) hierarchy
# Assumes a package structure with only two directory levels
# Compiled code is placed under ./build/
# Modify it if needed to suite your purpose

javac -d build *.java

cp ./scripts/peer.sh ./build/peer.sh
cp ./scripts/test.sh ./build/test.sh
