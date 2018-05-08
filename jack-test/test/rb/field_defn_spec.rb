require File.expand_path(File.dirname(__FILE__) + "/spec_helper.rb")

describe FieldDefn do 
  it "should parse :not_null as expected" do
    f = FieldDefn.new("string", :my_string, 0, {null: false})
    expect(f).not_to be_nullable

    f = FieldDefn.new("string", :my_string, 0, {})
    expect(f).to be_nullable
  end
end
