class ModelsDirProcessor
  def self.process(database_defn, model_defns_by_table_name)
    models_dir = database_defn.models_dir
    Dir.open(models_dir) do |dir|
      dir.each do |model_file_name|
        if model_file_name =~ /\.rb$/
          model_content = File.read(File.expand_path(models_dir) + "/" + model_file_name)
          if model_content =~ /^\s*(belongs_to)|(has_one)|(has_many)/ || model_content =~ / < ActiveRecord::Base/ && model_content !~ /self.abstract_class = true/
            lines = model_content.split("\n").reject {|l| l =~ /^\s*#/}
            process_model(lines, database_defn, model_defns_by_table_name)
          end
        end
      end
    end
  end
  
  def self.process_model(model_content_lines, database_defn, model_defns_by_table_name)
    class_name = get_class_name(model_content_lines)
    table_name = class_name.underscore.pluralize

    if md = model_defns_by_table_name[table_name]
      md.associations = parse_associations(model_content_lines, "belongs_to", md) +
        parse_associations(model_content_lines, "has_many", md, ":through") +
        parse_associations(model_content_lines, "has_one", md)
    else
      raise "didn't find a match for #{table_name}"
    end
  end

  def self.get_class_name(model_content_lines)
    class_lines = model_content_lines.select{|l| l =~ /class\s*[^< ]*.*</}
    arb_lines = class_lines.select{|l| l =~ /ActiveRecord::Base/}
    class_lines = class_lines.map{|l| l.match(/class\s*([^< ]*).*</)[1]}
    raise "coulnd't find an appropriate class name for this model! #{model_content_lines.first}" if class_lines.empty? && arb_lines.empty?
    arb_lines.empty? ? class_lines.first : arb_lines.first.split(" ")[1]
  end
  
  def self.parse_associations(lines, matches, model_defn, not_matches = nil)
    matching_lines = lines.select{|l| l =~ /^\s*#{matches}/}
    if not_matches
      matching_lines = matching_lines.reject { |l| l =~ /^\s*#{not_matches}/ }
    end
    matching_lines.map{|l| AssociationDefn.new(l, model_defn)}
  end
  
end