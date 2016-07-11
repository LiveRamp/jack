#!/bin/bash
cat $1 | sed "s/username: root/username: $2/" | sed "s/password:/password: $3/" > build/database.yml.tmp
cp build/database.yml.tmp $1
