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
  attr_accessor :name, :data_type, :args, :ordinal
  def initialize(name, data_type, ordinal, args = {})
    @name = name
    @data_type = data_type
    @args = args
    @ordinal = ordinal
  end

  def is_long?
    !args[":limit"].nil? && args[":limit"].to_i > 4
  end
  
  def java_type
    mappings = {
      :integer=>'Integer', 
      :string=>'String', 
      :datetime=>'Long', 
      :varbinary=>'byte[]', 
      :date=>'Long', 
      :text=>'String', 
      :binary=>'byte[]', 
      :float=>'Double', 
      :boolean=>'Boolean',
      :bigint=>'Long',
      :bytes=>'byte[]'
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
      "is#{camelize(name)}()"
    else
      "get#{camelize(name)}()"
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
      else
        "rs.get#{prep_stmt_type}(\"#{name}\")"
    end
  end
  
end