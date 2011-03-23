class DatabaseDefn
  attr_reader :namespace, :name, :schema_rb, :models_dir

  def initialize(map)
    @namespace = map["root_namespace"]
    @name = map["db_name"]
    @schema_rb = map["schema_rb"]
    @models_dir = map["models"]
  end
  
  def connection_name
    @name.underscore + "_connection"
  end
  
  def getter
    "get#{@name}()"
  end
end