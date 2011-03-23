class ProjectDefn
  attr_reader :databases_namespace, :databases

  def initialize(map)
    @databases_namespace = map["databases_namespace"]
    @databases = []
    map["databases"].each do |database|
      @databases << DatabaseDefn.new(database)
    end
  end
end