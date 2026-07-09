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
    # Rails >= 6 dumps `ActiveRecord::Schema[7.1].define(...)`.  The [] selector
    # returns the class itself so the subsequent .define call is unchanged. [S1]
    def self.[](_version)
      self
    end

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

    def method_missing(m, *args, &block)
      puts "Warning: schema method #{m} is not currently supported"
    end
  end

  class Index
    include FromHash
    attr_accessor :name, :fields, :unique, :length, :table, :using
  end

  class Table
    include FromHash
    # :charset/:collation/:comment are table-level kwargs emitted by Rails >= 5
    # dumps (e.g. `create_table "x", charset: "utf8mb4", ...`).  They are
    # accepted here and ignored downstream (no effect on generated Java). [T1]
    attr_accessor :name, :force, :id, :limit, :options, :schema, :charset, :collation, :comment
    fattr(:columns) { [] }

    # Rails >= 5 dumpers elide column options that equal the MySQL adapter
    # default.  These tables re-materialize those defaults so a modern dump
    # produces byte-identical parse state (and serialVersionUIDs) to the legacy
    # 4.2 dump, where every sized column carried an explicit limit.
    DEFAULT_LIMITS = {
      'integer' => 4, 'string' => 255, 'text' => 65535, 'binary' => 65535, 'float' => 24
    }.freeze
    # `size:` is the Rails shorthand for text/binary blob sizing. [C5]
    SIZE_TO_LIMIT = { tiny: 255, medium: 16_777_215, long: 4_294_967_295 }.freeze
    # Column options that modern dumps may emit but that the 4.2-era dump never
    # did; they carry no information used by the Java layer, so they are dropped
    # (with a warning) before Column construction rather than crashing it. [C9]
    IGNORED_COLUMN_OPTIONS = [:collation, :charset, :comment, :unsigned,
                              :auto_increment, :as, :stored].freeze

    def __column(type, name, ops = {})
      return if FORBIDDEN_FIELD_NAMES.include?(name) || type == 'index'
      ops = ops.dup
      # C1: Rails >= 5 renders 8-byte integers as `t.bigint`; the legacy dump
      # rendered them as `t.integer ..., limit: 8`.  Normalize to the legacy
      # form so data_type and the UID component are unchanged.
      if type == 'bigint'
        type = 'integer'
        ops[:limit] ||= 8
      end
      # C5: map `size: :tiny/:medium/:long` on text/binary to the legacy limit.
      if (size = ops.delete(:size))
        ops[:limit] ||= SIZE_TO_LIMIT.fetch(size.to_sym) { raise "unknown size #{size.inspect} on column #{name}" }
      end
      # C2/C3/C4/C6: refill an elided limit with the adapter default.
      ops[:limit] ||= DEFAULT_LIMITS[type] if DEFAULT_LIMITS.key?(type)
      # C9: drop modern-only column options that Column cannot accept.
      IGNORED_COLUMN_OPTIONS.each do |opt|
        next unless ops.key?(opt)
        puts "Warning: ignoring option #{opt.inspect} on column #{name}"
        ops.delete(opt)
      end
      self.columns << Column.new(ops.merge(type: type, name: name))
    end

    %w(bigint integer index text datetime boolean string float binary date decimal varbinary).each do |f|
      define_method(f) do |*args|
        self.__column(f, *args)
      end
    end
    def to_model_defn
      return nil if name == 'schema_info'
      res = ModelDefn.new(42)
      res.table_name = name
      res.model_name = name.singularize.camelize
      res.fields = columns.each_with_index.map { |x,i| x.to_model_defn(i) }
      res.migration_number = schema.version.to_s
      res
    end
  end

  class Column
    include FromHash
    attr_accessor :type, :name, :length, :limit, :null, :default, :precision, :scale, :unique

    def to_model_defn(col_index)
      f = to_h

      name = f.delete('name')
      type = f.delete('type').to_sym
      raise "bad" unless name && type

      # C7: float/decimal defaults must land as a Ruby Float in args.  Legacy
      # dumps rendered them as a numeric literal (`default: 0.0`); Rails >= 5
      # renders them as a string (`default: "0.0"`).  Coerce both to Float so
      # the UID component (":default0.0") is identical either way.  Other string
      # defaults keep their existing quoted-literal treatment.
      if [:float, :decimal].include?(type)
        f['default'] = Float(default) unless default.nil?
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
  def self.parse(schema_rb, ignored_tables = [])
    load schema_rb
    defns = $schema.tables.map(&:to_model_defn).compact.reject { |x| ignored_tables.include?(x.table_name) }
    [defns, $schema.version.to_s]
  end
end

if $0 == __FILE__
  puts SchemaRbParser.parse(ARGV[0]).inspect
end
