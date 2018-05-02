require File.expand_path(File.dirname(__FILE__) + "/spec_helper.rb")

describe DatabaseDefn do
  it "should include all tables by default" do
    database = DatabaseDefn.new({})
    expect(database.ignored_tables).to be_empty

    database = DatabaseDefn.new({'ignored_tables' => ''})
    expect(database.ignored_tables).to be_empty
  end

  it "should ignore specified tables" do
    database = DatabaseDefn.new({'ignored_tables' => 'table_1 table_2'})
    expect(database.ignored_tables).to eq ['table_1', 'table_2']
  end
end
