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

class ModelDefn
  attr_accessor :namespace, :database_defn
  attr_reader :fields, :table_name, :model_name, :migration_number
  attr_accessor :associations

  def initialize(decl_line, migration_number)
    @fields = []
    @associations = []
    @migration_number = migration_number

    @table_name = decl_line.match(/^\s*create_table "([^")]*)".*$/)[1]
    @model_name = @table_name.singularize.camelcase

    if decl_line =~ /:id => false/
      raise "Table #{@table_name} appears not to have a primary key, which is currently unsupported."
    end

    # check if the primary key has been renamed
    if decl_line =~ /:primary_key => "|'/
      raise "Table #{@table_name} appears to have a renamed primary key, which is currently unsupported."
    end
  end
  
  def iface_name
    "I#{model_name}Persistence"
  end
  
  def impl_name
    "Base#{model_name}PersistenceImpl"
  end
  
  def persistence_getter
    x = table_name.camelcase
    x[0,1] = x[0,1].downcase
    "#{x}()"
  end
  
  def import
    "import #{namespace}.models.#{model_name};"
  end
  
  def validate
    raise unless table_name && table_name != ""
    raise unless model_name && model_name != ""
    raise unless database_defn
  end
end