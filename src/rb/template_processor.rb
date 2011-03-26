
class TemplateProcessor
  private

  def self.adjust(s) 
    s.gsub(/^\s+<%(.*).%>$/, "<%\\1%>")
  end

  def self.load_template(file_name)
    ERB.new(adjust(File.read(File.dirname(__FILE__) + "/" + file_name)), nil, "<>")
  end

  DB_INTERFACE_TEMPLATE = load_template("templates/db_interface.erb")
  DB_IMPL_TEMPLATE = load_template("templates/db_impl.erb")
  DB_FIXTURES_TEMPLATE = load_template("templates/db_fixtures.erb")
  PERSISTENCE_INTERFACE_TEMPLATE = load_template("templates/persistence_interface.erb")
  MODEL_TEMPLATE = load_template("templates/model.erb")
  PERSISTENCE_IMPL_TEMPLATE = load_template("templates/persistence_impl.erb")

  DATABASES_IFACE_TEMPLATE = load_template("templates/databases_iface.erb")
  DATABASES_IMPL_TEMPLATE = load_template("templates/databases_impl.erb")

  public

  def self.process(project_defn, output_dir, model_defns_by_namespace_table_names)
    project_defn.databases.each do |database_defn|
      by_table_name = model_defns_by_namespace_table_names[database_defn.namespace]

      process_database_defn(project_defn, database_defn, output_dir, by_table_name.values.sort_by{|x| x.table_name}, by_table_name)
    end

    output_dir = output_dir + "/" + project_defn.databases_namespace.gsub(".", "/")
    file = File.new("#{output_dir}/IDatabases.java", "w")
    file.puts(DATABASES_IFACE_TEMPLATE.result(binding))
    file.close

    file = File.new("#{output_dir}/DatabasesImpl.java", "w")
    file.puts(DATABASES_IMPL_TEMPLATE.result(binding))
    file.close
  end
  
  def self.process_database_defn(project_defn, database_defn, output_dir, model_defns, by_table_name)
    output_dir = output_dir.dup + "/" + database_defn.namespace.gsub(".", "/")

    FileUtils.mkdir_p("#{output_dir}")
    FileUtils.mkdir_p("#{output_dir}/models/")
    FileUtils.mkdir_p("#{output_dir}/iface/")
    FileUtils.mkdir_p("#{output_dir}/impl/")

    db_name = database_defn.name
    root_package = database_defn.namespace

    model_defns.each do |model_defn|
      create_signature_full = model_defn.fields.map{|field_defn| ["final", field_defn.java_type, field_defn.name].join(" ")}.join(", ")
      create_signature_small = model_defn.fields.select{|field_defn| field_defn.args[":null"] == "false"}.map{|field_defn| ["final", field_defn.java_type, field_defn.name].join(" ")}.join(", ")
      create_signature_small = create_signature_full == create_signature_small || create_signature_small.empty? ? nil : create_signature_small

      file = File.new("#{output_dir}/models/#{model_defn.model_name}.java", "w")
      file.puts(MODEL_TEMPLATE.result(binding))
      file.close

      file = File.new("#{output_dir}/iface/#{model_defn.iface_name}.java", "w")
      file.puts(PERSISTENCE_INTERFACE_TEMPLATE.result(binding))
      file.close

      file = File.new("#{output_dir}/impl/#{model_defn.impl_name}.java", "w")
      file.puts(PERSISTENCE_IMPL_TEMPLATE.result(binding));
      file.close
    end

    file = File.new("#{output_dir}/I#{db_name}.java", "w")
    file.puts(DB_INTERFACE_TEMPLATE.result(binding))
    file.close

    file = File.new("#{output_dir}/impl/#{db_name}Impl.java", "w")
    file.puts(DB_IMPL_TEMPLATE.result(binding))
    file.close
    
    file = File.new("#{output_dir}/Base#{db_name}Fixtures.java", "w")
    file.puts(DB_FIXTURES_TEMPLATE.result(binding))
    file.close
    
  end

  def self.render_create_method(model_defn, signature, only_not_null = false)
    field_names = "Arrays.asList(#{model_defn.fields.select{|x| x.args[":null"] == "false" || !only_not_null}.map{|x| "\"#{x.name}\""}.join(", ")})"
    s =  "\n  public #{model_defn.model_name} create(#{signature}) throws IOException {\n"
    s << "    int __id = realCreate(new AttrSetter() {\n"
    s << "      public void set(PreparedStatement stmt) throws SQLException {\n"
    x = 1
    model_defn.fields.each do |field_defn|
      if field_defn.args[":null"] == "false" || !only_not_null
        s << "        if (#{field_defn.name} == null) {\n"
        s << "          stmt.setNull(#{x}, java.sql.Types.#{field_defn.sql_type});\n"
        s << "        } else {\n"
        s << "          stmt.set#{field_defn.prep_stmt_type}(#{x}, #{field_defn.prep_stmt_modifier(field_defn.name)});\n"
        s << "        }\n"
        x+= 1
      end
    end
    s << "      }\n"
    s << "    }, getInsertStatement(#{field_names}));\n"

    names_only = model_defn.fields.map{|field_defn| field_defn.args[":null"] == "false" || !only_not_null ? field_defn.name : "null"}.join(", ")
    s << "    return new #{model_defn.model_name}(__id#{names_only.empty? ? "" : ", "}#{names_only}, databases);\n"
    s << "  }\n"
  end
end