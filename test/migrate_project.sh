#!/bin/sh
pushd `pwd`
cd $1
bundle install
rake db:migrate
popd