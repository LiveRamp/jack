#!/bin/bash -l

pushd test/test_project/database_1 && bundle install --path vendor/bundle && bundle exec rake db:drop db:create db:migrate; popd
pushd test/test_project/database_2 && bundle install --path vendor/bundle && bundle exec rake db:drop db:create db:migrate; popd
