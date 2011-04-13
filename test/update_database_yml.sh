#!/bin/sh
cat $1 | sed "s/user(name)*: root/username: $2/" | sed "s/pass(word)*: /password: $3/" > build/database.yml.tmp
cp build/database.yml.tmp $1