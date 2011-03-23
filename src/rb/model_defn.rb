class ModelDefn
  attr_accessor :table_name, :model_name, :namespace, :database_defn
  attr_reader :fields
  attr_accessor :associations

  def initialize()
    @fields = []
    @associations = []
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