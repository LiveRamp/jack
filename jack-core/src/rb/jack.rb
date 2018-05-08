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

require File.expand_path(File.dirname(__FILE__)) + "/requires.rb"

class Jack
  def self.run(argv)
    project_yml = argv[0]
    output_dir = argv[1]
    base_dir = File.dirname(project_yml)

    project_defn = ProjectDefn.new(YAML.load(File.open(project_yml)))

    model_defns_by_namespace_table_names = {}
    migration_number = 0

    # initial pass to establish all the tables
    project_defn.databases.each do |database_defn|

      # NOTE: if you are loading multiple databases, the inflections loaded for databases will NOT be unloaded
      #       for subsequently processed databases.  hopefully this does not cause problems in any production environment
      #       if it does, we'll need to fork the process or load these some other way.
      if database_defn.inflections_file
        require "#{base_dir}/#{database_defn.inflections_file}"
      end

      model_defns_by_namespace_table_names[database_defn.namespace] = by_table_name = {}

      model_defns, migration_number = SchemaRbParser.parse(base_dir + "/" + database_defn.schema_rb, database_defn.ignored_tables)
      model_defns.each do |model_defn|
        model_defn.database_defn = database_defn
        model_defn.namespace = database_defn.namespace
        by_table_name[model_defn.table_name] = model_defn
      end
    end

    # second pass to accumulate all the associations
    project_defn.databases.each do |database|
      by_table_name = model_defns_by_namespace_table_names[database.namespace]

      ModelsDirProcessor.process(base_dir, database, by_table_name)

      by_table_name.values.each do |model_defn|
        model_defn.associations.each do |assoc_defn|
          assoc_defn.find_model(model_defns_by_namespace_table_names)
          model_defn.fields.each do |field_defn|
            if assoc_defn.foreign_key == field_defn.name
              field_defn.association = assoc_defn
            end
          end
        end
        model_defn.associations.reject!{|assoc_defn| assoc_defn.defunct}
        model_defn.validate
      end
    end

    # third pass to generate the files
    TemplateProcessor.process(project_defn, output_dir, model_defns_by_namespace_table_names, migration_number)
  end
end

if $0 == __FILE__
  if ARGV.size == 2
    Jack.run(ARGV)
  else
    puts <<-END
Wrong number of arguments.
Usage:
  ruby src/rb/jack.rb <path to project.yml> <path where output should be generated>
    END
  end
end
