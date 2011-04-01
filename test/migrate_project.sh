#!/bin/sh
pushd `pwd`
cd $1
rake db:migrate
popd