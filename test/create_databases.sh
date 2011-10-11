#!/bin/sh

if [ $# -gt 1 ]
then
  PASSWORD_CLAUSE=-p$2
fi
cat test/databases.sql | mysql -u $1 $PASSWORD_CLAUSE