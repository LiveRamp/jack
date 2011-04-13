#!/bin/sh
cat config/database.yml.prototype | sed "s/username: root/username: $1/" | sed "s/password: /password: $2/" > config/database.yml