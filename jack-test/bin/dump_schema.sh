#!/bin/bash

set -e
set -o pipefail

export GEM_HOME=gems/

dump_schema() {
  BASEDIR=$1
  DATABASE=$2
  DB_NAME=$3
  CLASS_DEST=$4
  DB_USER=$5
  DB_PASS=$6

  echo "Database: "$DATABASE
  DB_DUMP_PATH=$DATABASE.dump

  echo "Removing dump path $DB_DUMP_PATH"
  rm -f $DB_DUMP_PATH

  # generate schema dump
  echo "Generating schema dump"

  cd $BASEDIR

  echo "Dumping with user "$DB_USER", pass "$DB_PASS", db "$DB_NAME" to path "$DB_DUMP_PATH
  mysqldump --user=$DB_USER --password=$DB_PASS --no-data $DB_NAME --routines | sed -e 's/DEFINER[ ]*=[ ]*`[^`]*`@`[^`]*`//g' > $DB_DUMP_PATH

  echo "Copying db dump from ${DB_DUMP_PATH} to ${CLASS_DEST}"
  mv $DB_DUMP_PATH $CLASS_DEST
}

BASEDIR=$1
DB_USER=root
DB_PASS=""
RAILS_ENV="test"

cd $BASEDIR

echo "Creating directories"
CLASS_DEST=$BASEDIR/build/classes
mkdir -p $CLASS_DEST;


for DATABASE in $(ls databases)
do
  DB_NAME=$DATABASE"_"$RAILS_ENV

  echo "Database: "$DATABASE
  echo "Db name: "$DB_NAME
  dump_schema $BASEDIR $DATABASE $DB_NAME $CLASS_DEST $DB_USER "$DB_PASS"
done

echo "Done!"