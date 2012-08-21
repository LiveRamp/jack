for root in "config" "test/test_project/database_1/config" "test/test_project/database_2/config"; do
  echo "Copying $root/database.$1.yml -> $root/database.yml"
  pushd $root > /dev/null
  rm -f database.yml
  cp database.$1.yml database.yml
  if [[ $? -ne 0 ]]; then exit $?; fi
  popd > /dev/null
done