class ModelDefn
  attr_accessor :model_name, :namespace, :database_defn
  attr_reader :fields, :table_name
  attr_accessor :associations

  def initialize(decl_line)
    @fields = []
    @associations = []

    @table_name = decl_line.match(/^\s*create_table "([^")]*)".*$/)[1]
    if decl_line =~ /:id => false/
      raise "Table #{@table_name} appears not to have a primary key, which is currently unsupported."
    end

    # check if the primary key has been renamed
    if decl_line =~ /:primary_key => "|'/
      raise "Table #{@table_name} appears to have a renamed primary key, which is currently unsupported."
    end
  end
  
  def iface_name
    "I#{model_name}Persistence"
  end
  
  def impl_name
    "Base#{model_name}PersistenceImpl"
  end
  
  def persistence_getter
    x = table_name.camelcase
    x[0,1] = x[0,1].downcase
    "#{x}()"
  end
  
  def import
    "import #{namespace}.models.#{model_name};"
  end
  
  def validate
    raise unless table_name && table_name != ""
    raise unless model_name && model_name != ""
    raise unless database_defn
  end
end