# Copyright 2011 Rapleaf
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
class DatabaseDefn
  attr_reader :namespace, :name, :schema_rb, :models_dir, :support_missing_id, :adapter

  def initialize(map)
    @namespace = map["root_namespace"]
    @name = map["db_name"]
    @schema_rb = map["schema_rb"]
    @models_dir = map["models"]
    @support_missing_id = map["support_missing_id"]
    @adapter = map["adapter"] ? map["adapter"] : "mysql"
  end

  def connection_name
    @name.underscore + "_connection"
  end

  def getter
    "get#{@name}()"
  end
end