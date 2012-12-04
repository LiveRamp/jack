#!/bin/bash

pushd test/test_project/database_1 && bundle install && bundle exec rake db:drop db:create db:migrate; popd
pushd test/test_project/database_2 && bundle install && bundle exec rake db:drop db:create db:migrate; popd
