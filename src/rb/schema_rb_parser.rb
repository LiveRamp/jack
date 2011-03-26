
class SchemaRbParser
  def self.parse(schema_rb)
    file_lines = File.read(schema_rb).split("\n")
    file_lines.reject{|l| l =~ /^\s*$/}

    models = []
    migration_number = nil

    until file_lines.empty?
      line = file_lines.shift
      if line =~ /ActiveRecord::Schema.define\(:version => \d+\) do/
        migration_number = line.match(/ActiveRecord::Schema.define\(:version => (\d+)\) do/)[1]
      elsif line =~ /create_table/
        model_defn = ModelDefn.new(line, migration_number)

        ordinal = 0
        line = file_lines.shift
        while line =~ /^\s*t\.[a-z]+ / && !file_lines.empty?
          matches = line.match(/^\s*t\.([a-z]+)\s*"([^"]+)",?(.*)$/)
          raise "problem with #{model_defn.table_name}" if !matches
          field_defn = FieldDefn.new(matches[2], matches[1].to_sym, ordinal, Hash[matches[3].split(',').map{|a| a.split("=>").map{|s| s.strip}}])
          model_defn.fields << field_defn

          line = file_lines.shift
          ordinal += 1
        end
        models << model_defn
      end
    end
    models
  end
end

if $0 == __FILE__
  puts SchemaRbParser.parse(ARGV[0]).inspect
end