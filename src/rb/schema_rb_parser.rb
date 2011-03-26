
class SchemaRbParser
  def self.parse(schema_rb)
    file_lines = File.read(schema_rb).split("\n")
    file_lines.reject{|l| l =~ /^\s*$/}

    models = []

    line_no = 0
    # debugger
    while line_no < file_lines.size
      line = file_lines[line_no]
      if file_lines[line_no] =~ /create_table/
        model_defn = ModelDefn.new(file_lines[line_no])

        # model_defn.table_name = file_lines[line_no].match(/^\s*create_table "([^")]*)".*$/)[1]
        model_defn.model_name = model_defn.table_name.singularize.camelcase

        # start sucking up the column statements
        line_no += 1
        ordinal = 0
        while file_lines[line_no] =~ /^\s*t\.[a-z]+ /
          matches = file_lines[line_no].match(/^\s*t\.([a-z]+)\s*"([^"]+)",?(.*)$/)
          raise "problem with #{model_defn.table_name}" if !matches
          field_defn = FieldDefn.new(matches[2], matches[1].to_sym, ordinal, Hash[matches[3].split(',').map{|a| a.split("=>").map{|s| s.strip}}])
          model_defn.fields << field_defn
          line_no += 1
          ordinal +=1
        end
        models << model_defn
      end
      
      line_no += 1
    end
    models
  end
end

if $0 == __FILE__
  puts SchemaRbParser.parse(ARGV[0]).inspect
end