#!/bin/bash

SRCDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

if [[ $# -eq 0 ]]; then
  valid_dbs=$(find "${SRCDIR}/../config" -name database.*\.yml | sed 's/.*database\.//; s/\.yml//; s/config\///')
  printf "No DB specified. Valid choices are: \n${valid_dbs}\n"
  printf "Usage: select-dbms.sh <db_type>\n"
  exit -1
fi;

for root in "config" "test/test_project/database_1/config" "test/test_project/database_2/config"; do
  echo "Copying $root/database.$1.yml -> $root/database.yml"
  pushd $root > /dev/null
  rm -f database.yml
  cp database.$1.yml database.yml
  if [[ $? -ne 0 ]]; then exit $?; fi
  popd > /dev/null
done
for root in "test/test_project/database_1/" "test/test_project/database_2/"; do
  echo "Copying $root/Gemfile.$1 -> $root/Gemfile"
  pushd $root > /dev/null
  rm -f database.yml
  cp Gemfile.$1 Gemfile
  if [[ $? -ne 0 ]]; then exit $?; fi
  popd > /dev/null
done
