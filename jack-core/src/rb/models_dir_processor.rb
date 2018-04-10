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

class ModelsDirProcessor
  extend HashRegexHelpers

  def self.process(base_dir, database_defn, model_defns_by_table_name)
    models_dir = base_dir + "/" + database_defn.models_dir
    Dir.glob("#{models_dir}/**/*.rb").each do |model_file_name|
      model_content = File.read(model_file_name)
      if model_content =~ /^\s*(belongs_to)|(has_one)|(has_many)/ || model_content =~ / < ActiveRecord::Base/ && model_content !~ /self.abstract_class = true/
        lines = model_content.split("\n").reject {|l| l =~ /^\s*#/}
        process_model(lines, database_defn, model_defns_by_table_name)
      end
    end
  end

  def self.process_model(model_content_lines, database_defn, model_defns_by_table_name)
    class_name = get_class_name(model_content_lines)
    table_name = class_name.split('::').last.underscore.pluralize

    if md = model_defns_by_table_name[table_name]
      md.associations = parse_associations(model_content_lines, "belongs_to", md) +
        parse_associations(model_content_lines, "has_many", md, ":through") +
        parse_associations(model_content_lines, "has_one", md)
    else
      puts "Warning: Couldn't find any table '#{table_name}' that corresponded to model '#{class_name}'. No code will be generated for this model."
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
    matching_lines.
      reject do |l|
        # ":jack_generate => false" flag makes it easy to tell Jack to ignore
        # associations in cases where you are using rails features that jack
        # doesn't support (e.g. polymorphic associations)
        extract_hash_value(l, :jack_generate, false)
      end.
      map do |l|
        AssociationDefn.new(l, model_defn)
      end
  end
end
