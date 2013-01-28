#!/bin/bash

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
