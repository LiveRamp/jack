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
class FieldDefn
  attr_accessor :name, :data_type, :args, :ordinal, :default_value, :association
  def initialize(name, data_type, ordinal, args = {})
    @name = name
    @data_type = data_type
    @args = args
    @ordinal = ordinal
    @association = nil

    @nullable = !(args[:null] == false)
    @default_value = args[:default]

    if !@default_value.nil?
      if data_type == :datetime || data_type == :date
        @default_value = (Time.parse(@default_value).to_i * 1000).to_s.concat("L")
      end
    end

  end

  def nullable?
    @nullable
  end

  def is_long?
    !args[:limit].nil? && args[:limit].to_i > 4
  end

  JAVA_DEFAULT_VALUES = {
      :integer=>'0',
      :string=>'""',
      :datetime=>'0L',
      :varbinary=>'new byte[0]',
      :date=>'0L',
      :text=>'""',
      :binary=>'new byte[0]',
      :float=>'0.0',
      :decimal=>'0.0',
      :boolean=>'true',
      :bigint=>'0L',
      :bytes=>'new byte[0]',
      :long => "0L"
  }

  def java_default_value
    x = nil
    if data_type == :integer && is_long?
      x = :long
    else
      x = data_type
    end
    JAVA_DEFAULT_VALUES[x]
  end

  JAVA_TYPE_MAPPINGS = {
    true => {
      :integer=>'Integer',
      :string=>'String',
      :datetime=>'Long',
      :varbinary=>'byte[]',
      :date=>'Long',
      :text=>'String',
      :binary=>'byte[]',
      :float=>'Double',
      :decimal=>'Double',
      :boolean=>'Boolean',
      :bigint=>'Long',
      :bytes=>'byte[]',
      :long => "Long"
    },
    false => {
      :integer=>'int',
      :string=>'String',
      :datetime=>'long',
      :varbinary=>'byte[]',
      :date=>'long',
      :text=>'String',
      :binary=>'byte[]',
      :float=>'double',
      :decimal=>'double',
      :boolean=>'boolean',
      :bigint=>'long',
      :bytes=>'byte[]',
      :long => "long"
    }
  }

  def java_type(is_nullable = nullable?)
    x = nil
    if data_type == :integer && is_long?
      x = :long
    else
      x = data_type
    end
    JAVA_TYPE_MAPPINGS[is_nullable][x]
  end

  def sql_type
    mappings = {
      :integer=>'INTEGER',
      :string=>'CHAR',
      :datetime=>'DATE',
      :varbinary=>'VARBINARY',
      :date=>'DATE',
      :text=>'CHAR',
      :binary=>'BINARY',
      :float=>'DOUBLE',
      :decimal=>'DECIMAL',
      :boolean=>'BOOLEAN',
      :bigint=>'BIGINT',
      :bytes=>'BINARY'
    }
    if ret = mappings[data_type]
    else
      raise "unknown db_type #{data_type}"
    end
    ret
  end


  def prep_stmt_type()
    mappings = {
      :integer=>'Int',
      :string=>'String',
      :datetime=>'Timestamp',
      :varbinary=>'Bytes',
      :date=>'Date',
      :text=>'String',
      :binary=>'Bytes',
      :float=>'Double',
      :decimal=>'Double',
      :boolean=>'Boolean',
      :bigint=>'Long',
      :bytes=>'Bytes'
    }
    if data_type == :integer
      return "Long" if is_long?
    end
    if ret = mappings[data_type]
    else
      raise "unknown db_type #{data_type}"
    end
    ret
  end

  def prep_stmt_modifier(x)
    case data_type
      when :datetime
        "new Timestamp(#{x})"
      when :date
        "new Date(#{x})"
      else
        x
    end
  end


  def getter
    if data_type == :boolean
      "is#{name.camelcase}()"
    else
      "get#{name.camelcase}()"
    end
  end

  def post_modifier
    if data_type == :datetime
      ".getTime()"
    end
  end

  def get_from_rs
    case data_type
      when :integer
        is_long? ? "getLongOrNull(rs, \"#{name}\")" : "getIntOrNull(rs, \"#{name}\")"
      when :bigint
        "getLongOrNull(rs, \"#{name}\")"
      when :datetime
        "getDateAsLong(rs, \"#{name}\")"
      when :date
        "getDateAsLong(rs, \"#{name}\")"
      when :float
        "getDoubleOrNull(rs, \"#{name}\")"
      when :decimal
        "getDoubleOrNull(rs, \"#{name}\")"
      when :boolean
        "getBooleanOrNull(rs, \"#{name}\")"
      else
        "rs.get#{prep_stmt_type}(\"#{name}\")"
    end
  end

  def column_defn
    case data_type
      when :date
        "Column.fromDate(alias, _Fields.#{name});"
      when :datetime
        "Column.fromTimestamp(alias, _Fields.#{name});"
      else
        "Column.fromField(alias, _Fields.#{name}, #{java_type(true)}.class);"
    end
  end

  def serial_version_uid_component
    s = sprintf("%s%s%s", name, data_type, ordinal)

    # Maintain backwards compatability with older version of Jack that had
    # attribute keys like ':key' instead of :key
    old_args = Hash[args.map { |k, v| [":#{k}", v] }]
    old_args = old_args.sort_by { |x| x.join }

    s << old_args.join("")

    s
  end

end
