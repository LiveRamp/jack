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
      model_defns_by_namespace_table_names[database_defn.namespace] = by_table_name = {}

      model_defns, migration_number = SchemaRbParser.parse(base_dir + "/" + database_defn.schema_rb)
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
