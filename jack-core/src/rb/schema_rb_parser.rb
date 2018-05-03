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

require 'fattr'

FORBIDDEN_FIELD_NAMES = ["tbl", "id"]

module FromHash
  def from_hash(ops)
    ops.each do |k,v|
      send("#{k}=",v)
    end
    self
  end
  def initialize(ops={})
    from_hash(ops)
  end
end

module ActiveRecord
  class Schema
    def self.define(ops = {}, &b)
      $schema = s = new(ops)
      s.instance_eval(&b)
      s
    end

    include FromHash
    fattr(:indexes) { [] }
    fattr(:tables) { [] }
    attr_accessor :version

    def create_table(name, ops = {}, &b)
      table = Table.new(ops.merge(name: name, schema: self))
      table.instance_eval(&b)
      self.tables << table
    end

    def add_index(table, fields, ops = {})
      self.indexes << Index.new(ops.merge(table: table, fields: fields))
    end
  end

  class Index
    include FromHash
    attr_accessor :name, :fields, :unique, :length, :table
  end

  class Table
    include FromHash
    attr_accessor :name, :force, :id, :schema
    fattr(:columns) { [] }

    def __column(type, name, ops = {})
      self.columns << Column.new(ops.merge(type: type, name: name))
    end

    %w(integer text datetime boolean string float binary date decimal varbinary).each do |f|
      define_method(f) do |*args|
        self.__column(f, *args)
      end
    end
    def to_model_defn
      return nil if name == 'schema_info'
      res = ModelDefn.new(42)
      res.table_name = name
      res.model_name = name.singularize.camelize
      res.fields = columns.each_with_index.map { |x,i| x.to_model_defn(i) }.compact
      res.migration_number = schema.version.to_s
      res
    end
  end

  class Column
    include FromHash
    attr_accessor :type, :name, :limit, :null, :default, :precision, :scale

    def to_model_defn(col_index)
      f = to_h

      name = f.delete('name')
      type = f.delete('type').to_sym
      raise "bad" unless name && type
      return nil if FORBIDDEN_FIELD_NAMES.include?(f['name'])

      if default.kind_of?(Numeric) && [:float,:decimal].include?(type)
        f['default'] = default.to_f
      elsif default.kind_of?(String)
        f['default'] = '"' + default + '"'
      end

      FieldDefn.new(
        name,
        type,
        col_index,
        f.symbolize_keys
      )
    end

    def to_h
      res = {}
      [:precision, :scale, :limit, :default, :type, :name, :null].each do |f|
        v = send(f)
        res[f.to_s] = v if !v.nil?
      end
      res
    end
  end
end

class SchemaRbParser
  def self.parse(schema_rb, ignored_tables)
    load schema_rb
    defns = $schema.tables.map(&:to_model_defn).compact.reject { |x| ignored_tables.include?(x.table_name) }
    [defns, $schema.version.to_s]
  end
end

if $0 == __FILE__
  puts SchemaRbParser.parse(ARGV[0]).inspect
end
