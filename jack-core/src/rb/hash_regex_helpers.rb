module HashRegexHelpers
  def extract_string_hash_value(data, key)
    extract_hash_value(data, key, /['"](?<value>[^'"]*)['"]/)
  end

  def extract_symbol_hash_value(data, key)
    extract_hash_value(data, key, /:(?<value>[a-zA-Z@$_][a-zA-Z0-9_]*[a-zA-Z_=?!]?)/)
  end

  def extract_numeric_hash_value(data, key)
    extract_hash_value(data, key, /(?<value>\d+)/)
  end

  def extract_hash_value(data, key, value)
    value_regex = nil

    if value.is_a?(String)
      value_regex = "['\"](?<value>#{value})['\"]"
    elsif value.is_a?(Regexp)
      # Trim leading and trailing /. /a/ --> a
      value_regex = value.inspect[1,value.inspect.size-2]
    else
      value_regex = "(?<value>#{value.inspect})"
    end

    # regex = /(key: (?<value>#{value_regex}))|(:key => (?<value>))/
    matches = /#{key.to_s}(:|\s*=>)\s*#{value_regex}/.match(data)

    return nil if matches.nil?
    matches['value']
  end
end
