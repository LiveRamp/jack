#!/bin/sh
pushd `pwd`
cd $1
bundle install
EXIT_CODE=$?
if [ EXIT_CODE -ne 0 ]
then
  exit EXIT_CODE
fi
rake db:migrate
EXIT_CODE=$?
if [ EXIT_CODE -ne 0 ]
then
  exit EXIT_CODE
fi
popd