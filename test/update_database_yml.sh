#!/bin/sh
cat $1 | sed -E "s/user(name)?: root/username: $2/" | sed -E "s/pass(word)?: /password: $3/" > build/database.yml.tmp
cp build/database.yml.tmp $1