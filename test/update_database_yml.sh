#!/bin/bash -l
cat $1 | sed "s/username:.*/username: $2/" | sed "s/password:.*/password: $3/" > build/database.yml.tmp
cp build/database.yml.tmp $1
