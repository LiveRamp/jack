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
class AssociationDefn
  attr_accessor :type, :name, :args, :assoc_model_name, :foreign_key, :foreign_key_java_type, :assoc_model, :defunct

  def initialize(line, model_defn)
    @type = line.split(" ").first
    @name = line.split(" ")[1]

    @type = type
    @name = name.gsub(":", "").gsub(",", "")

    changed_class_name = line.match(/^.*:class_name => ['"]([^'"]*)['"].*$/)

    if changed_class_name 
      @assoc_model_name = changed_class_name[1]
    else
      case @type
        when "belongs_to"
          @assoc_model_name = @name.camelcase
        when "has_one"
          @assoc_model_name = @name.camelcase
        when "has_many"
          @assoc_model_name = @name.singularize.camelcase
      end
    end

    changed_fk = line.match(/^.*:foreign_key => ['"]([^'"]*)['"].*$/)
    if changed_fk
      @foreign_key = changed_fk[1]
    else
      changed_fk = line.match(/^.*:foreign_key => :([^,]*),?.*$/)
      if changed_fk
        @foreign_key = changed_fk[1]
      else
        case @type
          when "belongs_to"
            @foreign_key = "#{@name}_id"
          when "has_one"
            @foreign_key = "#{model_defn.model_name.foreign_key}"
          when "has_many"
            @foreign_key = "#{model_defn.model_name.foreign_key}"
        end
      end
    end

    if @type == "belongs_to"
      x = model_defn.fields.select{|f| f.name == @foreign_key}[0]
      if x
        @foreign_key_java_type = x.java_type
      else
        raise "In #{@name}, Unable to find a field named #{@foreign_key} on model #{model_defn.model_name}"
      end
    end

    @assoc_model = nil
    @defunct = false
    @args = args
  end

  def assoc_impl
    case @type
      when "belongs_to"
        "BelongsToAssociation"
      when "has_many"
        "HasManyAssociation"
      when "has_one"
        "HasOneAssociation"
    end
  end
  
  def constructor_call
    case @type
      when "belongs_to"
        "new #{assoc_impl}<#{assoc_model.model_name}>(databases.#{@assoc_model.database_defn.getter}.#{@assoc_model.persistence_getter}, #{@foreign_key})"
      else
        "new #{assoc_impl}<#{assoc_model.model_name}>(databases.#{@assoc_model.database_defn.getter}.#{@assoc_model.persistence_getter}, \"#{@foreign_key}\", id)"
    end
  end

  def java_type
    "#{assoc_impl}<#{assoc_model.model_name}>"
  end

  def find_model(model_defns_by_namespace_table_names)
    other_table_name = assoc_model_name.underscore.pluralize
    model_defns_by_namespace_table_names.each do |namespace, by_tablename|
      candidate = by_tablename[other_table_name]
      if candidate
        @assoc_model = candidate
        return
      end
    end
    puts "couldn't find a table named #{other_table_name} #{self.inspect}"
    @defunct = true
  end
  
  def field_name
    "__assoc_#{@name}"
  end
  
  def assoc_getter
    "get#{@name.camelcase}"
  end

  def assoc_getter_type
    case @type
      when "has_many"
        "Set<#{@assoc_model.model_name}>"
      else
        @assoc_model.model_name
    end
  end
end