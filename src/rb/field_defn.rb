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
      :date=>'BIGINT', 
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
      :date=>'Long', 
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
    if data_type == :datetime
      "new Timestamp(#{x})"
    else
      x
    end
  end


  def getter
    "get#{camelize(name)}()"
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
      else
        "rs.get#{prep_stmt_type}(\"#{name}\")"
    end
  end
  
end